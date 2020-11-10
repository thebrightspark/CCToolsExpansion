package brightspark.cctoolsexpansion.upgrade;

import brightspark.cctoolsexpansion.peripheral.BowPeripheral;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurtleBow extends AbstractTurtleUpgrade {
	public TurtleBow(ResourceLocation id, String adjective, Item item) {
		super(id, TurtleUpgradeType.PERIPHERAL, adjective, item);
	}

	@Nullable
	@Override
	public IPeripheral createPeripheral(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
		return new BowPeripheral(turtle, getCraftingItem());
	}

	@Nonnull
	@Override
	@OnlyIn(Dist.CLIENT)
	public TransformedModel getModel(@Nullable ITurtleAccess turtle, @Nonnull TurtleSide side) {
		return TransformedModel.of(getCraftingItem(), new TransformationMatrix(new Matrix4f(new float[]{
			0.0f, 0.0f, -1.0f, 1.0f + (side == TurtleSide.LEFT ? -0.40625f : 0.40625f),
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 0.0f, 1.0f,
		})));
	}

	@Override
	public void update(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof BowPeripheral) {
			((BowPeripheral) peripheral).update();
		}
	}
}
