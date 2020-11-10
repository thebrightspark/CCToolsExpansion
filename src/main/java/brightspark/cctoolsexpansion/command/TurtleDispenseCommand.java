package brightspark.cctoolsexpansion.command;

import brightspark.cctoolsexpansion.util.FakeDispenserBlockSource;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class TurtleDispenseCommand implements ITurtleCommand {
	private final InteractDirection direction;

	public TurtleDispenseCommand(InteractDirection direction) {
		this.direction = direction;
	}

	@Nonnull
	@Override
	public TurtleCommandResult execute(@Nonnull ITurtleAccess turtle) {
		int slot = turtle.getSelectedSlot();
		IInventory inv = turtle.getInventory();
		ItemStack stack = inv.getStackInSlot(slot);
		if (stack.isEmpty())
			return TurtleCommandResult.failure("Selected slot is empty");
		IDispenseItemBehavior behavior = DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.get(stack.getItem());
		if (behavior == null)
			return TurtleCommandResult.failure("No dispenser behaviour for item " + stack.getItem().getRegistryName());

		inv.setInventorySlotContents(slot, behavior.dispense(new FakeDispenserBlockSource(turtle, direction), stack));
		turtle.playAnimation(TurtleAnimation.WAIT);
		return TurtleCommandResult.success();
	}
}
