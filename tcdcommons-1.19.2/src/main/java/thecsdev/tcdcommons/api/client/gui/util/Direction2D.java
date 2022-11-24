package thecsdev.tcdcommons.api.client.gui.util;

/**
 * Represents a two-dimensional direction for GUIs.
 */
public enum Direction2D
{
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	public boolean isVertical() { return this == UP || this == DOWN; }
	public boolean isHorizontal() { return this == LEFT || this == RIGHT; }
}