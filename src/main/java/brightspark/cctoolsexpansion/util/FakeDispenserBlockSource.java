package brightspark.cctoolsexpansion.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;

public class FakeDispenserBlockSource implements IBlockSource {
	private ITurtleAccess turtle;
	private final InteractDirection direction;
	private final Lazy<BlockState> state = Lazy.of(() -> Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, getStateDirection()));
	private final Lazy<FakeDispenserTileEntity> te = Lazy.of(() -> new FakeDispenserTileEntity(turtle));

	public FakeDispenserBlockSource(ITurtleAccess turtle, InteractDirection direction) {
		this.turtle = turtle;
		this.direction = direction;
	}

	private Direction getStateDirection() {
		switch (direction) {
			case UP:
				return Direction.UP;
			case DOWN:
				return Direction.DOWN;
			default:
				return turtle.getDirection();
		}
	}

	@Override
	public double getX() {
		return (double) turtle.getPosition().getX() + 0.5D;
	}

	@Override
	public double getY() {
		return (double) turtle.getPosition().getY() + 0.5D;
	}

	@Override
	public double getZ() {
		return (double) turtle.getPosition().getZ() + 0.5D;
	}

	@Override
	public BlockPos getBlockPos() {
		return turtle.getPosition();
	}

	@Override
	public BlockState getBlockState() {
		return state.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends TileEntity> T getBlockTileEntity() {
		return (T) te.get();
	}

	@Override
	public ServerWorld getWorld() {
		return (ServerWorld) turtle.getWorld();
	}
}
