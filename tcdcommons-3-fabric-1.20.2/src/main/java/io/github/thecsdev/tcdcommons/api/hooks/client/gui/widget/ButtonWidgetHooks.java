package io.github.thecsdev.tcdcommons.api.hooks.client.gui.widget;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;

public final class ButtonWidgetHooks
{
	private ButtonWidgetHooks() {}
	
	/**
	 * Returns the {@link ButtonWidget.PressAction} for a given {@link ButtonWidget}.
	 * @param buttonWidget The target {@link ButtonWidget}.
	 */
	public static ButtonWidget.PressAction getOnPress(ButtonWidget buttonWidget)
	{
		return ((AccessorButtonWidget)buttonWidget).getOnPress();
	}
	
	/**
	 * Sets the {@link ButtonWidget.PressAction} for a given {@link ButtonWidget}.
	 * @param buttonWidget The target {@link ButtonWidget}.
	 * @param pressAction The {@link ButtonWidget.PressAction} to assign to the {@link ButtonWidget}.
	 */
	public static void setOnPress(ButtonWidget buttonWidget, ButtonWidget.PressAction pressAction)
	{
		((AccessorButtonWidget)buttonWidget).setOnPress(pressAction);
	}
}