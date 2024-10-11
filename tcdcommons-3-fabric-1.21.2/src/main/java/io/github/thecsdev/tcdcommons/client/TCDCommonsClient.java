package io.github.thecsdev.tcdcommons.client;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;

public final class TCDCommonsClient extends TCDCommons
{
	// ==================================================
	//a helper field so `MinecraftClient.getInstance()` doesn't have to be called a bunch of times
	public static final @Internal MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
	//a z-offset value that makes GUI elements render on top of any 3D items on the screen
	public static final @Internal int MAGIC_ITEM_Z_OFFSET = 50 + ItemRenderer.field_32934;
	// --------------------------------------------------
	/**
	 * A {@link KeyBinding}, that when pressed, refreshes
	 * the {@link MinecraftClient#currentScreen}.
	 */
	public static final KeyBinding KEY_RCS;
	// ==================================================
	static
	{
		//register key-bindings
		KEY_RCS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				TCDCT.KEY_RCS,
				InputUtil.UNKNOWN_KEY.getCode(),
				getModID()));
	}
	// --------------------------------------------------
	public TCDCommonsClient() {}
	// ==================================================
}