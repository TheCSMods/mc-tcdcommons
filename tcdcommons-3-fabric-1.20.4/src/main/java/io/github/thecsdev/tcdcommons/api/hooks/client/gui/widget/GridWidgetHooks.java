package io.github.thecsdev.tcdcommons.api.hooks.client.gui.widget;

import java.util.List;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorGridWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;

public final class GridWidgetHooks
{
	private GridWidgetHooks() {}
	
	/**
	 * Retrieves the {@link List} of {@link Widget}s stored in a {@link GridWidget}.
	 * @param gridWidget The target {@link GridWidget}.
	 * @apiNote Returns the {@link GridWidget}'s internal {@link List} directly. Careful with how you handle it.
	 */
	public static List<Widget> getChildren(GridWidget gridWidget)
	{
		return ((AccessorGridWidget)gridWidget).getChildren();
	}
	
	/*
	 * Sets the children {@link List} for a given {@link GridWidget}.
	 * @param gridWidget The target {@link GridWidget}.
	 * @param children The new children {@link List}.
	 * @throws NullPointerException If an argument is null.
	 *
	@Deprecated - causes complications
	public static void setChildren(GridWidget gridWidget, ArrayList<Widget> children)
	{
		Objects.requireNonNull(children);
		((AccessorGridWidget)gridWidget).setChildren(children);
	}*/
}