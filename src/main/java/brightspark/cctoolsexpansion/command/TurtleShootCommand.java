package brightspark.cctoolsexpansion.command;

import brightspark.cctoolsexpansion.peripheral.BowPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;

public class TurtleShootCommand implements ITurtleCommand {
	private final BowPeripheral bowPeripheral;
	private final int angleX, angleY;

	public TurtleShootCommand(BowPeripheral bowPeripheral, int angleX, int angleY) {
		this.bowPeripheral = bowPeripheral;
		this.angleX = angleX;
		this.angleY = angleY;
	}

	@Nonnull
	@Override
	public TurtleCommandResult execute(@Nonnull ITurtleAccess turtle) {
		if (!bowPeripheral.isCharging()) {
			return TurtleCommandResult.failure("Need to charge first!");
		}
		ItemStack arrowStack = BowPeripheral.findArrow(turtle);
		if (arrowStack.isEmpty()) {
			return TurtleCommandResult.failure("No arrows in inventory");
		}
		if (shoot(turtle, arrowStack)) {
			turtle.playAnimation(TurtleAnimation.WAIT);
			return TurtleCommandResult.success();
		} else {
			return TurtleCommandResult.failure("Failed to shoot arrow");
		}
	}

	// Partly copied from BowItem#onPlayerStoppedUsing
	private boolean shoot(ITurtleAccess turtle, ItemStack arrowStack) {
		TurtlePlayer player = TurtlePlayer.get(turtle);
		World world = player.world;
		int charge = bowPeripheral.getCharge() / 2;
		charge = ForgeEventFactory.onArrowLoose(bowPeripheral.getBowStack(), world, player, charge, !arrowStack.isEmpty());
		if (charge < 0)
			return false;

		float velocity = BowItem.getArrowVelocity(charge);
		if (velocity < 0.1F)
			return false;

		bowPeripheral.resetCharge();

		ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
		AbstractArrowEntity entity = arrowItem.createArrow(world, arrowStack, player);
		BlockPos posInfront = turtle.getPosition().offset(turtle.getDirection());
		entity.setPositionAndUpdate(posInfront.getX() + 0.5D, posInfront.getY() + 0.5D, posInfront.getZ() + 0.5D);
		entity.func_234612_a_(player, player.rotationPitch + angleY, player.rotationYaw + angleX, 0F, velocity * 3F, 1F);
		if (velocity >= 1F)
			entity.setIsCritical(true);
		arrowStack.shrink(1);
		return world.addEntity(entity);
	}
}
