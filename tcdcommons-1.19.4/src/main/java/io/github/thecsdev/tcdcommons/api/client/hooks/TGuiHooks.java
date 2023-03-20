package io.github.thecsdev.tcdcommons.api.client.hooks;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.MixinButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;

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
}
