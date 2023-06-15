package io.github.thecsdev.tcdcommons.api.client.gui.util;

import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

/**
 * Provides a few useful GUI-related utility functions.
 */
public final class GuiUtils
{
	// ==================================================
	private GuiUtils() {}
	// ==================================================
	/**
	 * Applies scissors to the {@link RenderSystem} using
	 * {@link RenderSystem#enableScissor(int, int, int, int)} while rendering.<br/>
	 * <br/>
	 * <b>Note:</b> Keep in mind that this method will disable scissors
	 * after the rendering is done, as well as clear/override any previously
	 * applied scissors. See {@link TScreen#resetScissors()}.
	 * @param client The current {@link MinecraftClient} instance.
	 * @param x The X coordinate of the scissor box.
	 * @param y The Y coordinate of the scissor box.
	 * @param width The width of the scissor box.
	 * @param height The height of the scissor box.
	 * @param renderingAction Perform whatever rendering you need to perform here.
	 */
	public static void applyScissor(MinecraftClient client, int x, int y, int width, int height, Runnable renderingAction)
	{
		//null checks
		Objects.requireNonNull(client, "client must not be null.");
		Objects.requireNonNull(renderingAction, "renderingAction must not be null.");
		
		enableScissor(client, x, y, width, height);
		renderingAction.run();
		disableScissor();
	}
	// --------------------------------------------------
	/**
	 * Unlike {@link #applyScissor(MinecraftClient, int, int, int, int, Runnable)},
	 * this method enables scissors and does not disable them at all.
	 * Don't forget to disable the scissors later using {@link #disableScissor()}.
	 * @param client The current {@link MinecraftClient} instance.
	 * @param x The X coordinate of the scissor box.
	 * @param y The Y coordinate of the scissor box.
	 * @param width The width of the scissor box.
	 * @param height The height of the scissor box.
	 */
	public static void enableScissor(MinecraftClient client, int x, int y, int width, int height)
	{
		//calculate XYWH
		double scale = client.getWindow().getScaleFactor();
		x = (int) (x * scale);
		y = (int) (client.getWindow().getFramebufferHeight() - (y + height) * scale);
		width = (int) (width * scale);
		height = (int) (height * scale);
		
		//enable scissors
		RenderSystem.enableScissor(x, y, width, height);
	}
	
	/**
	 * Disables any scissors applied to the {@link RenderSystem}
	 * by calling {@link RenderSystem#disableScissor()}.
	 */
	public static void disableScissor() { RenderSystem.disableScissor(); }
	// --------------------------------------------------
	/**
	 * Applies a given alpha value to a given RBGA color.
	 * @param color The input color {@link Integer} value.
	 * @param alpha The alpha/opacity to apply to the input color.
	 * @return The {@link Integer} value of the color with the alpha applied to it.
	 */
	public static int applyAlpha(int color, float alpha)
	{
		return color | MathHelper.ceil(alpha * ((float)(color & 0xFF) / 255)) << 24;
	}
	// ==================================================
	/**
	 * Forked from {@link ClickableWidget#playDownSound(SoundManager)}.<br/>
	 * Plays a GUI click sound.
	 */
	public static void playClickSound()
	{
		MinecraftClient.getInstance().getSoundManager()
			.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
	}
	// --------------------------------------------------
	/**
	 * Shows the user a prompt screen to open a given web URL.<br/>
	 * <br/>
	 * {@link MinecraftClient#currentScreen} is passed at the parent screen.<br/>
	 * See {@link #showUrlPrompt(Screen, String, boolean)}.
	 * @param url The web URL to open.
	 * @param trusted Whether or not the given URL can be trusted.
	 */
	@SuppressWarnings("resource")
	public static Screen showUrlPrompt(String url, boolean trusted)
	{
		return showUrlPrompt(MinecraftClient.getInstance().currentScreen, url, trusted);
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
			MinecraftClient.getInstance().setScreen(parent);
		},
		url, trusted);
		MinecraftClient.getInstance().setScreen(screen);
		return screen;
	}
	// ==================================================
	/**
	 * Re-initializes a {@link Screen} using {@link Screen#init(MinecraftClient, int, int)}.
	 * @param screen The screen to re-initialize.
	 */
	public static Screen initScreen(Screen screen)
	{
		Objects.requireNonNull(screen, "screen must not be null.");
		MinecraftClient c = MinecraftClient.getInstance();
		int w = c.getWindow().getScaledWidth();
		int h = c.getWindow().getScaledHeight();
		screen.init(c, w, h);
		return screen;
	}
	// ==================================================
}