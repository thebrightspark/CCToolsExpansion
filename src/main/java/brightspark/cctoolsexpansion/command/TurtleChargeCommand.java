package brightspark.cctoolsexpansion.command;

import brightspark.cctoolsexpansion.peripheral.BowPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;

import javax.annotation.Nonnull;

public class TurtleChargeCommand implements ITurtleCommand {
	private final BowPeripheral bowPeripheral;

	public TurtleChargeCommand(BowPeripheral bowPeripheral) {
		this.bowPeripheral = bowPeripheral;
	}

	@Nonnull
	@Override
	public TurtleCommandResult execute(@Nonnull ITurtleAccess turtle) {
		if (bowPeripheral.isCharging())
			return TurtleCommandResult.failure("Already charging");
		if (!BowPeripheral.containsArrow(turtle))
			return TurtleCommandResult.failure("No arrows in inventory");
		bowPeripheral.startCharging();
		return TurtleCommandResult.success();
	}
}
