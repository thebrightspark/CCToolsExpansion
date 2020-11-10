package brightspark.cctoolsexpansion.peripheral;

import brightspark.cctoolsexpansion.command.TurtleChargeCommand;
import brightspark.cctoolsexpansion.command.TurtleShootCommand;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class BowPeripheral implements IPeripheral {
	public static final Lazy<Item> ARROW = Lazy.of(() -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "arrow")));
	public static final Lazy<Set<Item>> ARROW_SET = Lazy.of(() -> Collections.singleton(ARROW.get()));

	public static boolean containsArrow(ITurtleAccess turtle) {
		return turtle.getInventory().hasAny(ARROW_SET.get());
	}

	public static ItemStack findArrow(ITurtleAccess turtle) {
		IInventory inv = turtle.getInventory();
		Item arrowItem = ARROW.get();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == arrowItem) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private final ITurtleAccess turtle;
	private final ItemStack bowStack;
	private int charge = 0;
	private boolean charging = false;

	public BowPeripheral(ITurtleAccess turtle, ItemStack bowStack) {
		this.turtle = turtle;
		this.bowStack = bowStack;
	}

	@Nonnull
	@Override
	public String getType() {
		return "bow";
	}

	@Nullable
	@Override
	public Object getTarget() {
		return turtle;
	}

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof BowPeripheral;
	}

	public void update() {
		if (charging)
			charge++;
	}

	public ItemStack getBowStack() {
		return bowStack.copy();
	}

	public boolean isCharging() {
		return charging;
	}

	public int getCharge() {
		return charge;
	}

	public void startCharging() {
		charging = true;
		charge = 0;
	}

	public void resetCharge() {
		charging = false;
		charge = 0;
	}

	@LuaFunction
	public final MethodResult charge() {
		return turtle.executeCommand(new TurtleChargeCommand(this));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@LuaFunction
	public final MethodResult shoot(Optional<Integer> angleX, Optional<Integer> angleY) throws LuaException {
		int x = angleX.orElse(0);
		int y = angleY.orElse(0);
		if (x < -45 || x > 45)
			throw new LuaException("X angle " + x + " out of range");
		if (y < -90 || y > 90)
			throw new LuaException("Y angle " + y + " out of range");
		return turtle.executeCommand(new TurtleShootCommand(this, x, y));
	}
}
