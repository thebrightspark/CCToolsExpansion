package brightspark.cctoolsexpansion.upgrade;

import brightspark.cctoolsexpansion.CCToolsExpansion;
import brightspark.cctoolsexpansion.peripheral.DispenserPeripheral;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurtleDispenser extends AbstractTurtleUpgrade {
	@OnlyIn(Dist.CLIENT)
	private ModelResourceLocation modelLeft;
	@OnlyIn(Dist.CLIENT)
	private ModelResourceLocation modelRight;

	public TurtleDispenser(ResourceLocation id) {
		super(id, TurtleUpgradeType.PERIPHERAL, "Dispensing", Blocks.DISPENSER);
	}

	@Nullable
	@Override
	public IPeripheral createPeripheral(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
		return new DispenserPeripheral(turtle);
	}

	@Nonnull
	@Override
	@OnlyIn(Dist.CLIENT)
	public TransformedModel getModel(@Nullable ITurtleAccess turtle, @Nonnull TurtleSide side) {
		if (modelLeft == null || modelRight == null) {
			modelLeft = new ModelResourceLocation(new ResourceLocation(CCToolsExpansion.MOD_ID, "turtle_dispenser_left"), "inventory");
			modelRight = new ModelResourceLocation(new ResourceLocation(CCToolsExpansion.MOD_ID, "turtle_dispenser_right"), "inventory");
		}
		return TransformedModel.of(side == TurtleSide.LEFT ? modelLeft : modelRight);
	}
}
