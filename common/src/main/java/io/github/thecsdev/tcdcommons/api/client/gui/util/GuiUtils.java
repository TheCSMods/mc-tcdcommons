package io.github.thecsdev.tcdcommons.api.client.gui.util;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenWrapper;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.hooks.client.gui.widget.GridWidgetHooks;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.Objects;

public final class GuiUtils
{
	// ==================================================
	private GuiUtils() {}
	// ==================================================
	/**
	 * Forked from {@link AbstractWidget#playDownSound(SoundManager)}.<br/>
	 * Plays a GUI click sound.
	 */
	public static void playClickSound()
	{
		TCDCommonsClient.MC_CLIENT.getSoundManager()
			.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
	}
	// ==================================================
	/**
	 * Shows the user a prompt screen to open a given web URL.<br/>
	 * <br/>
	 * {@link Minecraft#screen} is passed at the parent screen.<br/>
	 * See {@link #showUrlPrompt(Screen, String, boolean)}.
	 * @param url The web URL to open.
	 * @param trusted Whether or not the given URL can be trusted.
	 */
	public static Screen showUrlPrompt(String url, boolean trusted)
	{
		return showUrlPrompt(TCDCommonsClient.MC_CLIENT.screen, url, trusted);
	}
	
	/**
	 * Shows the user a prompt screen to open a given web URL.
	 * @param parent The currently opened screen, aka the screen that
	 * will be opened once the {@link ConfirmLinkScreen} is closed.
	 * @param url The web URL to open.
	 * @param trusted Whether or not the given URL can be trusted.
	 */
	public static Screen showUrlPrompt(Screen parent, String url, boolean trusted)
	{
		var screen = new ConfirmLinkScreen(accepted ->
		{
			if(accepted) Util.getPlatform().openUri(url);
			TCDCommonsClient.MC_CLIENT.setScreen(parent);
		},
		url, trusted);
		TCDCommonsClient.MC_CLIENT.setScreen(screen);
		return screen;
	}
	// ==================================================
	/**
	 * Re-initializes a {@link Screen} using {@link Screen#init(Minecraft, int, int)}.
	 * @param screen The screen to re-initialize.
	 * @throws NullPointerException If {@link Minecraft#getWindow()} returns {@code null}.
	 */
	public static Screen initScreen(Screen screen) throws NullPointerException
	{
		Objects.requireNonNull(screen);
		final var client = TCDCommonsClient.MC_CLIENT;
		final var window = client.getWindow();
		final int w = window.getGuiScaledWidth();
		final int h = window.getGuiScaledHeight();
		screen.init(client, w, h);
		return screen;
	}
	// --------------------------------------------------
	/**
	 * Attempts to find the parent {@link Screen} of the {@link Minecraft#screen}.
	 * @apiNote The current {@link Screen} must be an {@link IParentScreenProvider} for this to work!
	 */
	public static Screen getCurrentScreenParent() { return getParentScreen(TCDCommonsClient.MC_CLIENT.screen); }
	
	/**
	 * Attempts to find the parent {@link Screen} of the given {@link Screen}.
	 * @apiNote The given {@link Screen} must be an {@link IParentScreenProvider} for this to work!
	 */
	public static Screen getParentScreen(@Nullable Screen of)
	{
		//null check
		if(of == null) return null;
		
		//check if the target is a parent provider
		if(of instanceof IParentScreenProvider)
			return ((IParentScreenProvider)of).getParentScreen();
		
		//check if the target is a TScreen that itself is a parent provider
		if(of instanceof TScreenWrapper<?>)
		{
			final TScreen tOf = ((TScreenWrapper<?>)of).getTargetTScreen();
			if(tOf instanceof IParentScreenProvider)
				return ((IParentScreenProvider)tOf).getParentScreen();
		}
		
		//return null if nothing is found
		return null;
	}
	// ==================================================
	/**
	 * Creates a {@link ClientTooltipPositioner} with default behavior, for a given {@link TElement}.
	 * @param target The {@link TElement} to create the {@link ClientTooltipPositioner} for.
	 */
	public static ClientTooltipPositioner createDefaultTooltipPositioner(TElement target)
	{
		return (screenW, screenH, tX, tY, tW, tH) ->
		{
			//a parent screen is required to position the tooltip
			final var parentScreen = target.getParentTScreen();
			if(parentScreen == null) return new Vector2i(0,0);
			
			if(target.isFocused())
			{
				int newX = target.getX() + 2;
				int newY = target.getEndY() + 4;
				// Re-align X
				if(newX + tW > screenW) { newX -= (tW - target.getWidth()) + 4; }
				// Re-align Y
				if(newY + tH > screenH) { newY -= (tH + target.getHeight()) + 8; }
				return new Vector2i(newX, newY);
			}
			else
			{
				final var mousePos = parentScreen.getMousePosition();
				int newX = mousePos.x + 10;
				int newY = mousePos.y + 10;
				// Re-align X
				if(newX + tW > screenW) { newX -= tW + 15; }
				// Re-align Y
				if(newY + tH > screenH) { newY -= tH + 15; }
				return new Vector2i(newX, newY);
			}
		};
	}
	// ==================================================
	/**
	 * Attempts to find and return a {@link Button} on a {@link Screen}
	 * using the {@link Button}'s {@link Component}.
	 * @param screen The target {@link Screen}.
	 * @param buttonText The {@link Component} to look for on found {@link Button}s.
	 * @throws NullPointerException If an argument is null.
	 */
	public static @Nullable Button findButtonWidgetOnScreen
	(Screen screen, Component buttonText) throws NullPointerException
	{
		Objects.requireNonNull(screen);
		Objects.requireNonNull(buttonText);
		return __findButtonWidgetOnScreen(buttonText,
				screen.children()
				.stream()
				.map(e -> (GuiEventListener)e)
				.toList());
	}
	
	private static @Internal @Nullable Button __findButtonWidgetOnScreen
	(Component buttonText, List<GuiEventListener> elements)
	{
		String btnTxtStr = buttonText.getString();
		Button foundBtn = null;
		
		//iterate all drawables
		for(GuiEventListener selectable : elements)
		{
			//check grids
			if(selectable instanceof GridLayout)
			{
				GridLayout grid = (GridLayout)selectable;
				var gridCh = GridWidgetHooks.getChildren(grid).stream().map(i -> (GuiEventListener)i).toList();
				return __findButtonWidgetOnScreen(buttonText, gridCh);
			}
			
			//ignore non-buttons
			if(!(selectable instanceof Button))
				continue;
			Button btn = (Button)selectable;
			
			//compare texts
			if(!btnTxtStr.equals(btn.getMessage().getString()))
				continue;
			
			//return the button
			foundBtn = btn;
		}
		
		//return the button if found
		return foundBtn;
	}
	// ==================================================
}