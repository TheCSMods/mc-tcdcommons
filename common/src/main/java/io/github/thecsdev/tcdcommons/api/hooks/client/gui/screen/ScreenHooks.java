package io.github.thecsdev.tcdcommons.api.hooks.client.gui.screen;

import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorScreen;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

/**
 * Utility class providing methods to interact with {@link Screen} objects.
 */
public final class ScreenHooks
{
	private ScreenHooks() {}

	/**
	 * Returns the list of child elements of the given {@link Screen}.
	 *
	 * @param screen The {@link Screen}
	 * @return the list of child elements
	 */
	public static List<? extends GuiEventListener> children(Screen screen)
	{
		return screen.children();
	}

	/**
	 * Adds a drawable child to the given {@link Screen}.
	 *
	 * @param screen The {@link Screen}
	 * @param drawableElement the drawable child to add
	 * @param <T> the type of the drawable child
	 * @return the added child
	 */
	public static <T extends GuiEventListener & Renderable & NarratableEntry> T addDrawableChild(Screen screen, T drawableElement)
	{
		return ((AccessorScreen)screen).tcdcommons_addDrawableChild(drawableElement);
	}

	/**
	 * Adds a drawable to the given screen.
	 *
	 * @param screen The {@link Screen}
	 * @param drawable the drawable to add
	 * @param <T> the type of the drawable
	 * @return the added drawable
	 */
	public static <T extends Renderable> T addDrawable(Screen screen, T drawable)
	{
		return ((AccessorScreen)screen).tcdcommons_addDrawable(drawable);
	}

	/**
	 * Adds a selectable child to the given {@link Screen}.
	 *
	 * @param screen The {@link Screen}
	 * @param child the selectable child to add
	 * @param <T> the type of the selectable child
	 * @return the added child
	 */
	public static <T extends GuiEventListener & NarratableEntry> T addSelectableChild(Screen screen, T child)
	{
		return ((AccessorScreen)screen).tcdcommons_addSelectableChild(child);
	}

	/**
	 * Removes a child from the given {@link Screen}.
	 *
	 * @param screen The {@link Screen}
	 * @param child the child to remove
	 */
	public static void remove(Screen screen, GuiEventListener child)
	{
		((AccessorScreen)screen).tcdcommons_remove(child);
	}

	/**
	 * Clears all children from the given {@link Screen}.
	 *
	 * @param screen The {@link Screen}
	 */
	public static void clearChildren(Screen screen)
	{
		((AccessorScreen)screen).tcdcommons_clearChildren();
	}
}