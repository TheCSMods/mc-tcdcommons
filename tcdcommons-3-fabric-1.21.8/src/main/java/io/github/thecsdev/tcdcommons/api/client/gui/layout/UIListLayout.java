package io.github.thecsdev.tcdcommons.api.client.gui.layout;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Axis2D;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.enumerations.VerticalAlignment;

public @Virtual class UIListLayout extends UILayout
{
	// ==================================================
	protected Axis2D direction;
	protected HorizontalAlignment horizontalAlignment;
	protected VerticalAlignment verticalAlignment;
	protected int childPadding;
	// ==================================================
	public UIListLayout(Axis2D direction) throws NullPointerException { this(direction, 3); }
	public UIListLayout(Axis2D direction, int childPadding) throws NullPointerException
	{
		this(direction, VerticalAlignment.TOP, HorizontalAlignment.LEFT, childPadding);
	}
	public UIListLayout(
			Axis2D direction,
			VerticalAlignment verticalAlignment,
			HorizontalAlignment horizontalAlignment) throws NullPointerException
	{
		this(direction, verticalAlignment, horizontalAlignment, 3);
	}
	public UIListLayout(
			Axis2D direction,
			VerticalAlignment verticalAlignment,
			HorizontalAlignment horizontalAlignment,
			int childPadding) throws NullPointerException
	{
		this.direction = Objects.requireNonNull(direction);
		this.horizontalAlignment = Objects.requireNonNull(horizontalAlignment);
		this.verticalAlignment = Objects.requireNonNull(verticalAlignment);
		this.childPadding = childPadding;
	}
	// ==================================================
	@SuppressWarnings("removal")
	public @Virtual @Override void apply(TParentElement parent)
	{
		//prepare
		final var direction = this.direction;
		if(parent == null || direction == null) return;
		
		final int sPadding = getElementScrollPadding(parent), cPadding = this.childPadding;
		int nextX = parent.getX() + sPadding;
		int nextY = parent.getY() + sPadding;
		
		//iterate children, and position them; staring off with TOP-LEFT
		for(final var child : parent.getChildren())
		{
			//position child
			child.setPosition(nextX, nextY, false);
			
			//increment next XY
			if(direction == Axis2D.X) nextX = child.getEndX() + cPadding;
			else if(direction == Axis2D.Y) nextY = child.getEndY() + cPadding;
		}
		
		// ---------- align children based on alignments
		if(parent.getChildren().size() < 1) return;
		parent.getChildren().updateTopmostChildren();
		
		// ----- "global" alignment
		int globalMoveX = 0, globalMoveY = 0;
		final var pTopmosts = parent.getChildren().getTopmostElements();
		if(direction == Axis2D.X) switch(this.horizontalAlignment)
		{
			case CENTER:
			{
				final int left = pTopmosts.Item3.getX() - (parent.getX() + sPadding);
				final int right = pTopmosts.Item4.getEndX() - (parent.getEndX() - sPadding);
				globalMoveX = (left - right) / 2;
				break;
			}
			case RIGHT:
			{
				final int right = pTopmosts.Item4.getEndX();
				final int pEndX = parent.getEndX() - sPadding;
				globalMoveX = (pEndX - right);
				break;
			}
			default: break;
		}
		else if(direction == Axis2D.Y) switch(this.verticalAlignment)
		{
			case CENTER:
			{
				final int top = pTopmosts.Item1.getY() - (parent.getY() + sPadding);
				final int bottom = pTopmosts.Item2.getEndY() - (parent.getEndY() - sPadding);
				globalMoveY = (top - bottom) / 2;
				break;
			}
			case BOTTOM:
			{
				final int bottom = pTopmosts.Item2.getEndY();
				final int pEndY = parent.getEndY() - sPadding;
				globalMoveY = (pEndY - bottom);
				break;
			}
			default: break;
		}
		//"global" move for all elements
		if(globalMoveX != 0 || globalMoveY != 0)
			for(final var child : parent.getChildren())
				child.move(globalMoveX, globalMoveY);
		
		// ----- "local" alignment of individual elements
		for(final var child : parent.getChildren())
		{
			if(direction == Axis2D.X) switch(this.verticalAlignment)
			{
				case CENTER:
					child.setPosition(
							child.getX(),
							parent.getY() + (parent.getHeight() / 2) - (child.getHeight() / 2),
							false);
					break;
				case BOTTOM:
					child.setPosition(
							child.getX(),
							parent.getEndY() - sPadding - child.getHeight(),
							false);
					break;
				default: break;
			}
			else if(direction == Axis2D.Y) switch(this.horizontalAlignment)
			{
				case CENTER:
					child.setPosition(
							parent.getX() + (parent.getWidth() / 2) - (child.getWidth()/ 2),
							child.getY(),
							false);
					break;
				case RIGHT:
					child.setPosition(
							parent.getEndX() - sPadding - child.getWidth(),
							child.getY(),
							false);
					break;
				default: break;
			}
		}
	}
	// ==================================================
}