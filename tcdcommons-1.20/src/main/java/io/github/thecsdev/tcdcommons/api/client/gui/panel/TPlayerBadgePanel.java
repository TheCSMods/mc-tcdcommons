package io.github.thecsdev.tcdcommons.api.client.gui.panel;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TPlayerBadgeElement;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;

/**
 * Need a place to showcase an array of {@link PlayerBadge}s?<br/>
 * This {@link TPanelElement} may be the right one for you.<br/>
 * Don't forget to call {@link #init(Iterable)}.
 * @see #init(Iterable)
 * @see #init(Iterable, int)
 */
public class TPlayerBadgePanel extends TPanelElement
{
	// ==================================================
	public TPlayerBadgePanel(int x, int y, int width, int height, boolean drawBackground)
	{
		//initialize super
		super(x, y, width, height);
		//handle background drawing
		if(!drawBackground)
		{
			setScrollPadding(0);
			setBackgroundColor(0);
			setOutlineColor(0);
		}
		//make scrollable vertically
		setScrollFlags(SCROLL_VERTICAL);
	}
	// --------------------------------------------------
	/**
	 * Clears all elements and re-adds new {@link TPlayerBadgeElement}s
	 * based on the given {@link PlayerBadge} {@link Iterable}.
	 * @param badges The {@link PlayerBadge} {@link Iterable}.
	 */
	public void init(Iterable<PlayerBadge> badges) { init(badges, 20); }
	
	/**
	 * Clears all elements and re-adds new {@link TPlayerBadgeElement}s
	 * based on the given {@link PlayerBadge} {@link Iterable}.
	 * @param badges The {@link PlayerBadge} {@link Iterable}.
	 * @param size The GUI width/height of the {@link TPlayerBadgeElement}s.
	 */
	public void init(Iterable<PlayerBadge> badges, int size)
	{
		//clear old children
		this.clearTChildren();
		//prepare for element initialization
		if(size < 15) size = 15;
		final int sp = getScrollPadding();
		int lastX = sp, lastY = sp;
		//iterate badges
		for(PlayerBadge badge : badges)
		{
			//skip null entries in the event they exist
			if(badge == null) continue;
			//create and add element
			var el = new TPlayerBadgeElement(lastX, lastY, size, size, badge);
			this.addTChild(el, true);
			//calculate next offsets
			lastX += size + 3;
			if(lastX > getTpeWidth() - sp - size) { lastX = 0; lastY += size + 3; }
		}
	}
	// ==================================================
}