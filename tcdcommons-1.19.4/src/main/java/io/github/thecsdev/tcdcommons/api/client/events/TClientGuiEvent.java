package io.github.thecsdev.tcdcommons.api.client.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public interface TClientGuiEvent
{
	// ==================================================
	/**
	 * See {@link SetScreenPost#setScreenPost(Screen)}
	 */
	Event<SetScreenPost> SET_SCREEN_POST = EventFactory.createLoop();
	
	/**
	 * See {@link GameHudRenderPre#gameHudRenderPre(MatrixStack, float)}
	 */
	Event<GameHudRenderPre> RENDER_GAME_HUD_PRE = EventFactory.createEventResult();
	
	/**
	 * See {@link GameHudRenderPost#gameHudRenderPost(MatrixStack, float)}
	 */
	Event<GameHudRenderPost> RENDER_GAME_HUD_POST = EventFactory.createLoop();
	// ==================================================
	interface SetScreenPost
	{
		/**
		 * An event that is invoked AFTER a {@link Screen} has
		 * been set for the {@link MinecraftClient}.
		 * @param screen The new {@link Screen}.
		 */
		void setScreenPost(Screen screen);
	}
	
	interface GameHudRenderPre
	{
		/**
		 * An event that is invoked BEFORE an {@link InGameHud} renders.
		 * @param matrices The rendering {@link MatrixStack}.
		 * @param tickDelta The delta time in ticks.
		 */
		EventResult gameHudRenderPre(MatrixStack matrices, float tickDelta);
	}
	
	interface GameHudRenderPost
	{
		/**
		 * An event that is invoked AFTER an {@link InGameHud} renders.
		 * @param matrices The rendering {@link MatrixStack}.
		 * @param tickDelta The delta time in ticks.
		 */
		void gameHudRenderPost(MatrixStack matrices, float tickDelta);
	}
	// ==================================================
}