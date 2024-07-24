package io.github.thecsdev.tcdcommons.api.util.enumerations;

public enum AutomaticSize
{
	/** Automatic size is applied in the X axis. */
	X,
	
	/** Automatic size is applied in the Y axis. */
	Y,
	
	/** Automatic size is applied in both axes. */
	XY;
	
	public final boolean hasX() { return this == X || this == XY; }
	public final boolean hasY() { return this == Y || this == XY; }
}