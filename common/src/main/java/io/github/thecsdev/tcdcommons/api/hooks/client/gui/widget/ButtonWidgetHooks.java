package io.github.thecsdev.tcdcommons.api.hooks.client.gui.widget;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorButtonWidget;
import net.minecraft.client.gui.components.Button;

public final class ButtonWidgetHooks
{
	private ButtonWidgetHooks() {}
	
	/**
	 * Returns the {@link Button.OnPress} for a given {@link Button}.
	 * @param buttonWidget The target {@link Button}.
	 */
	public static Button.OnPress getOnPress(Button buttonWidget)
	{
		return ((AccessorButtonWidget)buttonWidget).getOnPress();
	}
	
	/**
	 * Sets the {@link Button.OnPress} for a given {@link Button}.
	 * @param buttonWidget The target {@link Button}.
	 * @param pressAction The {@link Button.OnPress} to assign to the {@link Button}.
	 */
	public static void setOnPress(Button buttonWidget, Button.OnPress pressAction)
	{
		((AccessorButtonWidget)buttonWidget).setOnPress(pressAction);
	}
}