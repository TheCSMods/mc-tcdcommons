package io.github.thecsdev.tcdcommons.api.client.gui.other;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Direction2D;

/**
 * A {@link TElement} that renders a progress-bar on the screen.
 */
public @Virtual class TProgressBarElement extends TElement
{
	// ==================================================
	protected float       progress;
	protected Direction2D direction;
	// ==================================================
	public TProgressBarElement(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.progress = 0;
		this.direction = Direction2D.LEFT;
	}
	// ==================================================
	/**
	 * Returns the 0-1 progress of this progress-bar.
	 */
	public final float getProgress() { return this.progress; }
	
	/**
	 * Sets the 0-1 progress of this progress-bar.
	 * @param progress The [0 to 1] value of the progress.
	 */
	public final void setProgress(float progress) { this.progress = (progress = Math.max(0, Math.min(1, progress))); }
	// --------------------------------------------------
	/**
	 * Returns the {@link Direction2D} in which the
	 * progress-bar will visually progress.
	 */
	public final Direction2D getDirection() { return this.direction; }
	
	/**
	 * Sets the {@link Direction2D} in which the progress-bar's visually progresses.
	 * @param direction The {@link Direction2D}.
	 */
	public final void setDirection(Direction2D direction) { this.direction = (direction != null) ? direction : Direction2D.RIGHT; }
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		pencil.drawTFill(0xff444444);
		renderProgress(pencil);
	}
	public @Virtual @Override void postRender(TDrawContext pencil) { pencil.drawTBorder(TPanelElement.COLOR_OUTLINE); }
	// --------------------------------------------------
	/**
	 * Calculates the region in which the progress part of the progress-bar is to be
	 * rendered, and then calls {@link #renderProgress(TDrawContext, int, int, int, int)}.
	 * @param pencil The {@link TDrawContext}.
	 */
	protected final void renderProgress(TDrawContext pencil)
	{
		//handle rendering based on direction
		switch(this.direction)
		{
			//handle horizontal
			case LEFT:
			case RIGHT:
			{
				int w = (int) (getWidth() * this.progress);
				int x = (this.direction == Direction2D.LEFT) ? getX() : getEndX() - w;
				renderProgress(pencil, x, getY(), w, getHeight());
				break;
			}
			//handle vertical
			case UP:
			case DOWN:
			{
				int h = (int) (getHeight() * this.progress);
				int y = (this.direction == Direction2D.UP) ? getY() : getEndY() - h;
				renderProgress(pencil, getX(), y, getWidth(), h);
				break;
			}
			//handle other/null
			default: return;
		}
	}
	
	/**
	 * Renders this progress-bar's progress in a given region.
	 * @param pencil The {@link TDrawContext}.
	 */
	protected @Virtual void renderProgress(TDrawContext pencil, int x, int y, int width, int height)
	{
		pencil.fill(x, y, x + width, y + height, 0xff656565);
	}
	// ==================================================
}