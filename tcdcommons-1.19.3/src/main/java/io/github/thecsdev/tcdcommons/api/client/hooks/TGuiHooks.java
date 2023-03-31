package io.github.thecsdev.tcdcommons.api.client.hooks;

import java.util.List;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.MixinButtonWidget;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.MixinGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;

public final class TGuiHooks
{
	// ==================================================
	private TGuiHooks() {}
	// ==================================================
	/**
	 * Returns the {@link ButtonWidget.PressAction} that
	 * should be invoked when the given {@link ButtonWidget} is pressed.
	 * @param button The target {@link ButtonWidget}.
	 */
	public static ButtonWidget.PressAction getButtonPressAction(ButtonWidget button)
	{
		return ((MixinButtonWidget)button).getOnPress();
	}
	
	/**
	 * Sets the {@link ButtonWidget.PressAction} that
	 * should be invoked when the given {@link ButtonWidget} is pressed.
	 * @param button The target {@link ButtonWidget}.
	 * @param pressAction The new on-press action.
	 */
	public static void setButtonPressAction(ButtonWidget button, ButtonWidget.PressAction pressAction)
	{
		((MixinButtonWidget)button).setOnPress(pressAction);
	}
	// ==================================================
	/**
	 * Returns the child {@link Widget}s of a given {@link GridWidget}.
	 * @param grid The target {@link GridWidget}.
	 * @throws NullPointerException When an argument is null.
	 */
	public static List<ClickableWidget> getGridWidgetChildren(GridWidget grid)
	{
		return ((MixinGridWidget)grid).getChildren();
	}
	
	/**
	 * Sets the child {@link Widget}s of a given {@link GridWidget}.
	 * @param grid The target {@link GridWidget}.
	 * @param widgets The new widget {@link List} to assign.
	 * @throws NullPointerException When an argument is null.
	 */
	public static void setGridWidgetChildren(GridWidget grid, List<ClickableWidget> widgets)
	{
		Objects.requireNonNull(widgets);
		((MixinGridWidget)grid).setChildren(widgets);
	}
	// ==================================================
}
