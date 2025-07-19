package io.github.thecsdev.tcdcommons.api.client.util.interfaces;

import net.minecraft.client.gui.tooltip.Tooltip;

/**
 * Represents {@link Object}s that provide {@link Tooltip}s via {@link #getTooltip()}.
 */
public interface ITooltipProvider
{
	public Tooltip getTooltip();
}