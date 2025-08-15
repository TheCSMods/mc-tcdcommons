package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.enumerations.VerticalAlignment;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A {@link UILayout} that arranges a {@link TParentElement}'s children
 * in a horizontal grid-like order, where the children are placed next
 * to one another, continuing on the next "line" once the current
 * "line" runs out of space.
 */
public @Virtual class UIHorizontalGridLayout extends UILayout
{
	// ==================================================
	protected final HorizontalAlignment hAlignment;
	protected final VerticalAlignment vAlignment;
	protected final Point margin;
	// ==================================================
	public UIHorizontalGridLayout() { this(HorizontalAlignment.LEFT, VerticalAlignment.TOP); }
	public UIHorizontalGridLayout(HorizontalAlignment hAlignment) { this(hAlignment, VerticalAlignment.TOP); }
	public UIHorizontalGridLayout(VerticalAlignment vAlignment) { this(HorizontalAlignment.LEFT, vAlignment); }
	public UIHorizontalGridLayout(HorizontalAlignment hAlignment, VerticalAlignment vAlignment)
			throws NullPointerException
	{
		this.hAlignment = Objects.requireNonNull(hAlignment);
		this.vAlignment = Objects.requireNonNull(vAlignment);
		this.margin = new Point(2, 2);
	}
	// ==================================================
	/**
	 * Sets the margin that will be applied to a {@link TParentElement}'s children
	 * as they get arranged into a grid. The margin is the gap left between children.
	 * @param x The horizontal margin.
	 * @param y The vertical margin.
	 * @return {@code this}
	 */
	public @Virtual UIHorizontalGridLayout setMargin(int x, int y) { this.margin.setLocation(x, y); return this; }
	// ==================================================
	public @Virtual @Override void apply(TParentElement parent)
	{
		//requirements
		Objects.requireNonNull(parent);
		
		//prevent layout conflicts by first moving all children out of the way
		//(note: a cheesy solution)
		parent.getChildren().forEach(c -> c.setPosition(Integer.MIN_VALUE, Integer.MIN_VALUE, TElement.SP_MOVE_CHILDREN));
		
		//iteration preparations
		@Nullable Rectangle rowN1 = null;
		
		//iterate all children, and move them in a grid-like formation
		for(final var child : parent.getChildren())
		{
			//check if there's space left in the current row
			//(also handles the initial aka 1st row)
			if(rowN1 == null || child.getWidth() > Math.max(rowN1.width, 0))
			{
				final boolean wasNull = (rowN1 == null);
				rowN1 = nextChildVerticalRect(parent, this.defaultPsp);
				if(!wasNull) rowN1.y += this.margin.y; //apply Y margin to subsequent rows
			}
			
			//place the next child, and offset the N1
			child.setPosition(rowN1.x, rowN1.y, false);
			
			//move N1 to the right
			final int num = this.margin.x + child.getWidth();
			rowN1.x += num;
			rowN1.width -= num;
		}
		
		//apply alignments
		//- note: left is the default and is already pre-aligned
		//- note: top is the default and is already pre-aligned
		if(this.hAlignment != HorizontalAlignment.LEFT) applyHAlignment(parent);
		if(this.vAlignment != VerticalAlignment.TOP) applyVAlignment(parent);
	}
	// ==================================================
	protected @Virtual void applyHAlignment(TParentElement parent)
	{
		//requirements
		Objects.requireNonNull(parent);
		if(parent.getChildren().size() == 0) return;
		
		//map the children based on their Y coordinate
		final var rows = StreamSupport.stream(parent.getChildren().spliterator(), false)
				.collect(Collectors.groupingBy(TParentElement::getY));
		rows.values().forEach(row -> row.sort(Comparator.comparingInt(TParentElement::getX)));
		
		//iterate each row, and align it
		for(final var row : rows.values())
		{
			//shouldn't happen, but check just in case
			if(row.size() == 0) continue;
			
			//calculate the amount of pixels to move based on alignment
			final AtomicInteger toMove = new AtomicInteger(0);
			switch(this.hAlignment)
			{
				case LEFT:
				{
					//calculate parent and child start X
					final int pStartX = parent.getX() + getElementScrollPadding(parent);
					final int cStartX = row.getFirst().getX();
					
					//the difference = the amount to move
					toMove.set(cStartX - pStartX);
					break;
				}
				case RIGHT:
				{
					//calculate parent and child end X
					final int pEndX = parent.getEndX() - getElementScrollPadding(parent);
					final int cEndX = row.getLast().getEndX();
					
					//the difference = the amount to move
					toMove.set(pEndX - cEndX);
					break;
				}
				case CENTER:
				{
					//calculate parent and child start and end X
					final int pStartX = parent.getX() + getElementScrollPadding(parent);
					final int pEndX = parent.getEndX() - getElementScrollPadding(parent);
					final int cStartX = row.getFirst().getX();
					final int cEndX = row.getLast().getEndX();
					
					//calculate centers
					final int pCenterX = (pStartX + pEndX) / 2;
					final int cCenterX = (cStartX + cEndX) / 2;
					
					//the difference between the centers = the amount to move
					toMove.set(pCenterX - cCenterX);
					break;
				}
				default: break;
			};
			
			//move all elements
			row.forEach(el -> el.move(toMove.get(), 0));
		}
	}
	// --------------------------------------------------
	@SuppressWarnings("removal")
	protected @Virtual void applyVAlignment(TParentElement parent)
	{
		//requirements
		Objects.requireNonNull(parent);
		if(parent.getChildren().size() == 0) return;
		
		//calculate values
		final var pt = parent.getChildren().getTopmostElements();
		
		//calculate the amount of pixels to move based on alignment
		final AtomicInteger toMove = new AtomicInteger(0);
		switch(this.vAlignment)
		{
			case TOP:
			{
				//calculate parent and child start Y
				final var pStartY = parent.getY() + getElementScrollPadding(parent);
				final var cStartY = pt.Item1.getY();
				
				//the difference = the amount to move
				toMove.set(cStartY - pStartY);
				break;
			}
			case BOTTOM:
			{
				//calculate parent and child end Y
				final var pEndY = parent.getEndY() - getElementScrollPadding(parent);
				final var cEndY = pt.Item2.getEndY();
				
				//the difference = the amount to move
				toMove.set(pEndY - cEndY);
				break;
			}
			case CENTER:
			{
				//calculate parent and child start and end Y
				final var pStartY = parent.getY() + getElementScrollPadding(parent);
				final var pEndY = parent.getEndY() - getElementScrollPadding(parent);
				final var cStartY = pt.Item1.getY();
				final var cEndY = pt.Item2.getEndY();
				
				//calculate centers
				final int pCenterY = (pStartY + pEndY) / 2;
				final int cCenterY = (cStartY + cEndY) / 2;
				
				//the difference between the centers = the amount to move
				toMove.set(pCenterY - cCenterY);
				break;
			}
			default: break;
		}
		
		//move all elements
		parent.getChildren().forEach(el -> el.move(0, toMove.get()));
	}
	// ==================================================
}