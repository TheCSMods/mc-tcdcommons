package io.github.thecsdev.tcdcommons.client.world.tick;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;

@Beta
public @Virtual class EmptyClientTickScheduler<T> implements QueryableTickScheduler<T>
{
	public @Virtual @Override int getTickCount() { return 0; }
	public @Virtual @Override boolean isQueued(BlockPos pos, T type) { return false; }
	public @Virtual @Override void scheduleTick(OrderedTick<T> orderedTick) {}
	public @Virtual @Override boolean isTicking(BlockPos pos, T type) { return false; }
}