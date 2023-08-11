package io.github.thecsdev.tcdcommons.api.util.math;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class Tuple2<T1, T2>
{
	// ==================================================
	public T1 Item1;
	public T2 Item2;
	// ==================================================
	public Tuple2() { this(null, null); }
	public Tuple2(T1 item1, T2 item2)
	{
		this.Item1 = item1;
		this.Item2 = item2;
	}
	// ==================================================
	/**
	 * Sets all item's values to null.
	 */
	public @Virtual void clear() { this.Item1 = null; this.Item2 = null; }
	
	/**
	 * Returns true if all item's values are set to null.
	 */
	public @Virtual boolean isCleared() { return this.Item1 == null && this.Item2 == null; }
	
	/**
	 * Returns true if all item's values are <b>not</b> set to null.
	 */
	public @Virtual boolean isFull() { return this.Item1 != null && this.Item2 != null; }
	// ==================================================
}