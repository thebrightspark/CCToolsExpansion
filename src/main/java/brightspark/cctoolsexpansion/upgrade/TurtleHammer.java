package brightspark.cctoolsexpansion.upgrade;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleVerb;
import dan200.computercraft.api.turtle.event.TurtleBlockEvent;
import dan200.computercraft.shared.TurtlePermissions;
import dan200.computercraft.shared.turtle.core.TurtlePlaceCommand;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import dan200.computercraft.shared.turtle.upgrades.TurtleTool;
import dan200.computercraft.shared.util.DropConsumer;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TurtleHammer extends TurtleTool {

	public TurtleHammer(ResourceLocation id, String adjective, Item item) {
		super(id, adjective, item);
	}

	@Nonnull
	@Override
	public TurtleCommandResult useTool(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side, @Nonnull TurtleVerb verb, @Nonnull Direction direction) {
		if (verb == TurtleVerb.DIG)
			return dig(turtle, direction, side);
		return super.useTool(turtle, side, verb, direction);
	}

	private TurtleCommandResult dig(ITurtleAccess turtle, Direction direction, TurtleSide side) {
		BlockPos centerPos = turtle.getPosition().offset(direction);
		BlockPos.Mutable start = centerPos.toMutable();
		BlockPos.Mutable end = centerPos.toMutable();
		switch (direction) {
			case NORTH:
			case SOUTH:
				start.move(Direction.DOWN).move(Direction.WEST);
				end.move(Direction.UP).move(Direction.EAST);
				break;
			case WEST:
			case EAST:
				start.move(Direction.DOWN).move(Direction.NORTH);
				end.move(Direction.UP).move(Direction.SOUTH);
				break;
			case UP:
			case DOWN:
				start.move(Direction.WEST).move(Direction.NORTH);
				end.move(Direction.EAST).move(Direction.SOUTH);
		}
		return BlockPos.getAllInBox(start, end)
			.map(pos -> digBlock(turtle, direction, side, pos))
			.collect(Collectors.toSet())
			.contains(true)
			? TurtleCommandResult.success()
			: TurtleCommandResult.failure();
	}

	// Mostly copied from TurtleTool and adapted for 3x3 mining
	private boolean digBlock(ITurtleAccess turtle, Direction direction, TurtleSide side, BlockPos blockPos) {
		// Get ready to dig
		World world = turtle.getWorld();

		if (world.isAirBlock(blockPos) || WorldUtil.isLiquidBlock(world, blockPos)) {
			return false;
		}

		BlockState state = world.getBlockState(blockPos);
		FluidState fluidState = world.getFluidState(blockPos);

		TurtlePlayer turtlePlayer = TurtlePlaceCommand.createPlayer(turtle, turtle.getPosition(), direction);
		turtlePlayer.loadInventory(item.copy());

		if (ComputerCraft.turtlesObeyBlockProtection) {
			// Check spawn protection
			if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, blockPos, state, turtlePlayer))) {
				return false;
			}

			if (!TurtlePermissions.isBlockEditable(world, blockPos, turtlePlayer)) {
				return false;
			}
		}

		// Check if we can break the block
		if (!canBreakBlock(state, world, blockPos, turtlePlayer)) {
			return false;
		}

		// Fire the dig event, checking whether it was cancelled.
		TurtleBlockEvent.Dig digEvent = new TurtleBlockEvent.Dig(turtle, turtlePlayer, world, blockPos, state, this, side);
		if (MinecraftForge.EVENT_BUS.post(digEvent)) {
			return false;
		}

		// Consume the items the block drops
		DropConsumer.set(world, blockPos, turtleDropConsumer(turtle));

		TileEntity tile = world.getTileEntity(blockPos);

		// Much of this logic comes from PlayerInteractionManager#tryHarvestBlock, so it's a good idea
		// to consult there before making any changes.

		// Play the destruction sound and particles
		world.playEvent(2001, blockPos, Block.getStateId(state));

		// Destroy the block
		boolean canHarvest = state.canHarvestBlock(world, blockPos, turtlePlayer);
		boolean canBreak = state.removedByPlayer(world, blockPos, turtlePlayer, canHarvest, fluidState);
		if (canBreak) state.getBlock().onPlayerDestroy(world, blockPos, state);
		if (canHarvest && canBreak) {
			state.getBlock().harvestBlock(world, turtlePlayer, blockPos, state, tile, turtlePlayer.getHeldItemMainhand());
		}

		stopConsuming(turtle);

		return true;
	}

	// Copied from TurtleTool
	private static Function<ItemStack, ItemStack> turtleDropConsumer(ITurtleAccess turtle) {
		return drop -> InventoryUtil.storeItems(drop, turtle.getItemHandler(), turtle.getSelectedSlot());
	}

	// Copied from TurtleTool
	private static void stopConsuming(ITurtleAccess turtle) {
		List<ItemStack> extra = DropConsumer.clear();
		for (ItemStack remainder : extra) {
			WorldUtil.dropItemStack(remainder, turtle.getWorld(), turtle.getPosition(), turtle.getDirection().getOpposite());
		}
	}
}
