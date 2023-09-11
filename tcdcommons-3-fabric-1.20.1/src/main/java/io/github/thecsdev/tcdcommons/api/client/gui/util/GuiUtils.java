package io.github.thecsdev.tcdcommons.api.client.gui.util;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenWrapper;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.hooks.client.gui.widget.GridWidgetHooks;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public final class GuiUtils
{
	// ==================================================
	private GuiUtils() {}
	// ==================================================
	/**
	 * Forked from {@link ClickableWidget#playDownSound(SoundManager)}.<br/>
	 * Plays a GUI click sound.
	 */
	public static void playClickSound()
	{
		TCDCommonsClient.MC_CLIENT.getSoundManager()
			.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
	}
	// ==================================================
	/**
	 * Shows the user a prompt screen to open a given web URL.<br/>
	 * <br/>
	 * {@link MinecraftClient#currentScreen} is passed at the parent screen.<br/>
	 * See {@link #showUrlPrompt(Screen, String, boolean)}.
	 * @param url The web URL to open.
	 * @param trusted Whether or not the given URL can be trusted.
	 */
	public static Screen showUrlPrompt(String url, boolean trusted)
	{
		return showUrlPrompt(TCDCommonsClient.MC_CLIENT.currentScreen, url, trusted);
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
			if(accepted) Util.getOperatingSystem().open(url);
			TCDCommonsClient.MC_CLIENT.setScreen(parent);
		},
		url, trusted);
		TCDCommonsClient.MC_CLIENT.setScreen(screen);
		return screen;
	}
	// ==================================================
	/**
	 * Re-initializes a {@link Screen} using {@link Screen#init(MinecraftClient, int, int)}.
	 * @param screen The screen to re-initialize.
	 */
	public static Screen initScreen(Screen screen)
	{
		Objects.requireNonNull(screen);
		final var client = TCDCommonsClient.MC_CLIENT;
		final var window = client.getWindow();
		final int w = window.getScaledWidth();
		final int h = window.getScaledHeight();
		screen.init(client, w, h);
		return screen;
	}
	// --------------------------------------------------
	/**
	 * Attempts to find the parent {@link Screen} of the {@link MinecraftClient#currentScreen}.
	 * @apiNote The current {@link Screen} must be an {@link IParentScreenProvider} for this to work!
	 */
	public static Screen getCurrentScreenParent() { return getParentScreen(TCDCommonsClient.MC_CLIENT.currentScreen); }
	
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
	 * Creates a {@link TooltipPositioner} with default behavior, for a given {@link TElement}.
	 * @param target The {@link TElement} to create the {@link TooltipPositioner} for.
	 */
	public static TooltipPositioner createDefaultTooltipPositioner(TElement target)
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
	 * Attempts to find and return a {@link ButtonWidget} on a {@link Screen}
	 * using the {@link ButtonWidget}'s {@link Text}.
	 * @param screen The target {@link Screen}.
	 * @param buttonText The {@link Text} to look for on found {@link ButtonWidget}s.
	 * @throws NullPointerException If an argument is null.
	 */
	public static final @Nullable ButtonWidget findButtonWidgetOnScreen
	(Screen screen, Text buttonText) throws NullPointerException
	{
		Objects.requireNonNull(screen);
		Objects.requireNonNull(buttonText);
		return __findButtonWidgetOnScreen(buttonText,
				screen.children()
				.stream()
				.map(e -> (Element)e)
				.toList());
	}
	
	private static final @Internal @Nullable ButtonWidget __findButtonWidgetOnScreen
	(Text buttonText, List<Element> elements)
	{
		String btnTxtStr = buttonText.getString();
		ButtonWidget foundBtn = null;
		
		//iterate all drawables
		for(Element selectable : elements)
		{
			//check grids
			if(selectable instanceof GridWidget)
			{
				GridWidget grid = (GridWidget)selectable;
				var gridCh = GridWidgetHooks.getChildren(grid).stream().map(i -> (Element)i).toList();
				return __findButtonWidgetOnScreen(buttonText, gridCh);
			}
			
			//ignore non-buttons
			if(!(selectable instanceof ButtonWidget))
				continue;
			ButtonWidget btn = (ButtonWidget)selectable;
			
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