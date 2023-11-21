package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * A {@link UILayout} that organizes elements in a table-like layout,
 * where elements are ordered in a similar-to grid-like layout.
 */
public @Virtual class UITableLayout extends UILayout
{
	// ==================================================
	protected int columnCount;
	protected int cellPadding;
	// ==================================================
	public UITableLayout(int columns) { this(columns, 5); }
	public UITableLayout(int columns, int padding)
	{
		this.columnCount = Math.max(Math.abs(columns), 1);
		this.cellPadding = Math.abs(padding);
	}
	// ==================================================
	/**
	 * Calculates and returns the "current" size a table cell's column should be.
	 */
	public final int getCellColumnSize()
	{
		//if there's no parent, return 0
		final var parent = getParent();
		if(parent == null) return 0;
		//calculate and return
		//TODO - Handle vertical UITableLayout columns if they ever get added
		int res = parent.getWidth();                      //1. Obtain max cell column size
		res -= (this.columnCount - 1) * this.cellPadding; //2. Subtract all cell paddings
		res -= getParentScrollPadding() * 2;              //3. Subtract scroll paddings for both sides
		res /= Math.max(this.columnCount, 1);             //4. Divide remainder by cell count
		return  res;
	}
	// ==================================================
	//TODO - Handle vertical UITableLayout columns if they ever get added
	public @Virtual void apply()
	{
		//do nothing if there's no parent
		final var parent = getParent();
		if(parent == null) return;

		//obtain cell column size, and iterate rows and columns
		final int pX = parent.getX();
		final int size = getCellColumnSize();
		final int scrollPadding = getParentScrollPadding();
		
		int column = 1, rowY = parent.getY() + scrollPadding, nextRowY = rowY;
		for(final var child : getParent().getChildren())
		{
			//skip layout children
			if(child instanceof UILayout) continue;
			
			//calculate column position, and then child center position
			final int cp = pX + scrollPadding + (this.cellPadding * (column - 1)) + (size * (column - 1));
			final int ccp = (cp + (size / 2)) - (child.getWidth() / 2);
			
			//position child
			child.setPosition(ccp, rowY, false);
			final int cey = child.getEndY();
			if(cey > nextRowY) nextRowY = cey;
			
			//increment columns
			if((++column) > this.columnCount)
			{
				column = 1;
				rowY = nextRowY + this.cellPadding;
			}
		}
	}
	// --------------------------------------------------
	protected @Virtual @Override void onSiblingAdded(TElement sibling, boolean repositioned) { apply(); }
	protected @Virtual @Override void onSiblingRemoved(TElement sibling, boolean repositioned) { apply(); }
	protected @Virtual @Override void onParentResized(int oldWidth, int oldHeight) { apply(); }
	// ==================================================
}