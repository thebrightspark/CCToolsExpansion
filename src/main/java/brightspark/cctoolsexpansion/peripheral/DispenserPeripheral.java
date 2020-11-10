package brightspark.cctoolsexpansion.peripheral;

import brightspark.cctoolsexpansion.command.TurtleDispenseCommand;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.core.InteractDirection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DispenserPeripheral implements IPeripheral {
	private final ITurtleAccess turtle;

	public DispenserPeripheral(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	@Nonnull
	@Override
	public String getType() {
		return "dispenser";
	}

	@Nullable
	@Override
	public Object getTarget() {
		return turtle;
	}

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof DispenserPeripheral;
	}

	@LuaFunction
	public final MethodResult dispense() {
		return turtle.executeCommand(new TurtleDispenseCommand(InteractDirection.FORWARD));
	}

	@LuaFunction
	public final MethodResult dispenseUp() {
		return turtle.executeCommand(new TurtleDispenseCommand(InteractDirection.UP));
	}

	@LuaFunction
	public final MethodResult dispenseDown() {
		return turtle.executeCommand(new TurtleDispenseCommand(InteractDirection.DOWN));
	}
}
