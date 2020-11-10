package brightspark.cctoolsexpansion;

import brightspark.cctoolsexpansion.upgrade.TurtleBow;
import brightspark.cctoolsexpansion.upgrade.TurtleDispenser;
import brightspark.cctoolsexpansion.upgrade.TurtleHammer;
import brightspark.cctoolsexpansion.util.TurtleToolDetails;
import brightspark.cctoolsexpansion.util.TurtleToolType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.shared.turtle.upgrades.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mod(CCToolsExpansion.MOD_ID)
public class CCToolsExpansion {
	public static final String MOD_ID = "cctoolsexpansion";
	private static final String FILE_NAME = MOD_ID + "-tools.json";
	private static final String FILE_DEFAULT_PATH = "/assets/" + MOD_ID + "/tools.json";
	private static final Gson GSON = new Gson();
	private static final Type JSON_TYPE = new TypeToken<List<TurtleToolDetails>>() {}.getType();

	private final Logger logger = LogManager.getLogger();
	private final Path turtleToolsFile;

	public CCToolsExpansion() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		Path configDir = FMLPaths.CONFIGDIR.get();
		turtleToolsFile = configDir.resolve(FILE_NAME);
		if (Files.notExists(turtleToolsFile)) {
			try (InputStream input = this.getClass().getResourceAsStream(FILE_DEFAULT_PATH)) {
				Files.copy(input, turtleToolsFile);
			} catch (IOException e) {
				logger.error("Failed to create blank " + FILE_NAME, e);
			}
		}
	}

	private void setup(FMLCommonSetupEvent event) {
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleBow(createId(Items.BOW), "Archer", Items.BOW));
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleDispenser(createId(Blocks.DISPENSER)));

		readTurtleToolDetails().stream()
			.map(this::createTurtleUpgrade)
			.filter(Objects::nonNull)
			.forEach(ComputerCraftAPI::registerTurtleUpgrade);
	}

	private List<TurtleToolDetails> readTurtleToolDetails() {
		try {
			return GSON.fromJson(new JsonReader(new FileReader(turtleToolsFile.toFile())), JSON_TYPE);
		} catch (FileNotFoundException e) {
			logger.error("Failed to read file " + turtleToolsFile, e);
			return Collections.emptyList();
		}
	}

	private ResourceLocation createId(ForgeRegistryEntry<?> obj) {
		return new ResourceLocation(MOD_ID, Objects.requireNonNull(obj.getRegistryName()).getPath().replace(':', '_'));
	}

	private ITurtleUpgrade createTurtleUpgrade(TurtleToolDetails turtleToolDetails) {
		if (StringUtils.isNullOrEmpty(turtleToolDetails.itemId)) {
			logger.error("Turtle upgrade item ID is missing or empty! -> " + turtleToolDetails);
			return null;
		}
		if (StringUtils.isNullOrEmpty(turtleToolDetails.adjective)) {
			logger.error("Turtle upgrade adjective is missing or empty! -> " + turtleToolDetails);
			return null;
		}
		if (turtleToolDetails.type == null) {
			logger.error("Turtle upgrade type is missing or invalid! -> " + turtleToolDetails);
			return null;
		}

		ResourceLocation rl = new ResourceLocation(turtleToolDetails.itemId);
		Item item = ForgeRegistries.ITEMS.getValue(rl);
		if (item == null) {
			logger.error("Couldn't register turtle upgrade due to non-existent item with ID '{}'", rl);
			return null;
		} else {
			String adjective = turtleToolDetails.adjective;
			TurtleToolType type = turtleToolDetails.type;
			ResourceLocation upgradeId = createId(item);
			logger.info("Registering turtle upgrade for item '{}' with ID '{}', adjective '{}' and type '{}'", upgradeId, item, adjective, type);
			switch (type) {
				case TOOL:
					return new TurtleTool(upgradeId, adjective, item);
				case SHOVEL:
					return new TurtleShovel(upgradeId, adjective, item);
				case AXE:
					return new TurtleAxe(upgradeId, adjective, item);
				case HOE:
					return new TurtleHoe(upgradeId, adjective, item);
				case SWORD:
					return new TurtleSword(upgradeId, adjective, item);
				case HAMMER:
					return new TurtleHammer(upgradeId, adjective, item);
				case BOW:
					return new TurtleBow(upgradeId, adjective, item);
				default:
					logger.error("Unhandled upgrade type '{}'", type);
					return null;
			}
		}
	}
}
