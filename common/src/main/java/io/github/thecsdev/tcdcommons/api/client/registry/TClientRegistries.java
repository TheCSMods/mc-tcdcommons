package io.github.thecsdev.tcdcommons.api.client.registry;

import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.render.badge.PlayerBadgeRenderer;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.events.client.gui.hud.InGameHudEvent;
import io.github.thecsdev.tcdcommons.api.registry.TMutableRegistry;
import io.github.thecsdev.tcdcommons.api.registry.TSimpleRegistry;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class TClientRegistries
{
	// ==================================================
	private TClientRegistries() {}
	// ==================================================
	/**
	 * Contains {@link PlayerBadgeRenderer}s that were registered for this session.
	 */
	@SuppressWarnings("removal")
	public static final TSimpleRegistry<PlayerBadgeRenderer<?>> PLAYER_BADGE_RENDERER = new TSimpleRegistry<>();
	
	/**
	 * Contains {@link Screen}s that are to be rendered on top of the {@link Gui}
	 * when the {@link InGameHudEvent#RENDER_POST} {@link TEvent} takes place.
	 * @apiNote {@link StatsListener} will work even when the {@link Screen} is rendered on the {@link Gui}.
	 */
	public static final TMutableRegistry<Screen> HUD_SCREEN = new TMutableRegistry<>();
	// ==================================================
	//initialize registry behavior logic
	static
	{
		//initialize screen-s being registered while the screen is available
		HUD_SCREEN.eRegistered.register((id, screen) ->
		{
			//obtain the client
			final var client = TCDCommonsClient.MC_CLIENT;
			
			//do not make initialization attempts if the client is not ready yet
			if(client.getWindow() == null || client.getNarrator() == null)
				return;
			
			//initialize the registered screen
			GuiUtils.initScreen(screen);
		});
	}
	// ==================================================
	/**
	 * Manually re-initializes all {@link Screen} registered in {@link #HUD_SCREEN}.
	 * @apiNote {@link Internal}. Not intended for outside use.
	 * @apiNote Automatically called when the game window resizes.
	 */
	public static final @Internal void reInitHudScreens()
	{
		//do not make initialization attempts if there are no hud screens,
		//or if the client is not ready yet
		final var client = TCDCommonsClient.MC_CLIENT;
		if(HUD_SCREEN.size() == 0 || client.getWindow() == null || client.getNarrator() == null)
			return;
		
		//iterate all registered screens, and re-initialize them
		for(final var hudScreen : HUD_SCREEN)
			GuiUtils.initScreen(hudScreen.getValue());
	}
	// ==================================================
}