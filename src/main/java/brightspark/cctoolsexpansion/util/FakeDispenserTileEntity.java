package brightspark.cctoolsexpansion.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;

public class FakeDispenserTileEntity extends DispenserTileEntity {
	private final ITurtleAccess turtle;

	public FakeDispenserTileEntity(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	@Override
	public int addItemStack(ItemStack stack) {
		IInventory inv = turtle.getInventory();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i).isEmpty()) {
				inv.setInventorySlotContents(i, stack);
				return i;
			}
		}
		return -1;
	}
}
