package brightspark.cctoolsexpansion;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Map;

import static brightspark.cctoolsexpansion.CCToolsExpansion.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCTEClientHandler {
	@SubscribeEvent
	public static void textureStitch(TextureStitchEvent.Pre event) {
		if (!event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
			return;
		event.addSprite(new ResourceLocation(MOD_ID, "block/turtle_dispenser_face"));
	}

	@SubscribeEvent
	public static void modelBake(ModelBakeEvent event) {
		ModelLoader loader = event.getModelLoader();
		Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

		regExtraModel(loader, registry, "turtle_dispenser_left");
		regExtraModel(loader, registry, "turtle_dispenser_right");
	}

	// Just doing the same as in dan200.computercraft.client.ClientRegistry#onModelBakeEvent
	private static void regExtraModel(ModelLoader loader, Map<ResourceLocation, IBakedModel> registry, String modelName) {
		ResourceLocation rl = new ResourceLocation(MOD_ID, "item/" + modelName);
		IUnbakedModel model = loader.getUnbakedModel(rl);
		model.getTextures(loader::getUnbakedModel, new HashSet<>());
		IBakedModel baked = model.bakeModel(loader, ModelLoader.defaultTextureGetter(), SimpleModelTransform.IDENTITY, rl);
		if (baked != null) {
			ModelResourceLocation mrl = new ModelResourceLocation(new ResourceLocation(MOD_ID, modelName), "inventory");
			registry.put(mrl, baked);
		}
 	}
}
