package io.github.thecsdev.tcdcommons.api.util.enumerations;

/**
 * {@link Enum} representing 2D directions: UP, DOWN, LEFT, RIGHT.
 */
public enum Direction2D
{
	/**
	 * Represents upward direction.
	 */
	UP,

	/**
	 * Represents downward direction.
	 */
	DOWN,

	/**
	 * Represents leftward direction.
	 */
	LEFT,

	/**
	 * Represents rightward direction.
	 */
	RIGHT;
	
	public boolean isVertical() { return this == UP || this == DOWN; }
	public boolean isHorizontal() { return this == LEFT || this == RIGHT; }
}