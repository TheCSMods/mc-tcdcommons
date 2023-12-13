package io.github.thecsdev.tcdcommons.api.util.math;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class Tuple3<T1, T2, T3> extends Tuple2<T1, T2>
{
	// ==================================================
	public T3 Item3;
	// ==================================================
	public Tuple3() { this(null, null, null); }
	public Tuple3(T1 item1, T2 item2, T3 item3)
	{
		super(item1, item2);
		this.Item3 = item3;
	}
	// ==================================================
	public @Virtual @Override void clear() { super.clear(); this.Item3 = null; }
	public @Virtual @Override boolean isCleared() { return super.isCleared() && this.Item3 == null; }
	public @Virtual @Override boolean isFull() { return super.isFull() && this.Item3 != null; }
	// ==================================================
}