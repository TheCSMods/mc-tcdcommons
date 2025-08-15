package io.github.thecsdev.tcdcommons.api.hooks.client.gui.widget;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorGridWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.List;

public final class GridWidgetHooks
{
	private GridWidgetHooks() {}
	
	/**
	 * Retrieves the {@link List} of {@link LayoutElement}s stored in a {@link GridLayout}.
	 * @param gridWidget The target {@link GridLayout}.
	 * @apiNote Returns the {@link GridLayout}'s internal {@link List} directly. Careful with how you handle it.
	 */
	public static List<LayoutElement> getChildren(GridLayout gridWidget)
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