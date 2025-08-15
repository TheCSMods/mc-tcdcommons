package io.github.thecsdev.tcdcommons.client;

import io.github.thecsdev.tcdcommons.TCDCommons;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class TCDCommonsClient extends TCDCommons
{
	// ==================================================
	//a helper field so `MinecraftClient.getInstance()` doesn't have to be called a bunch of times
	public static       @Internal Minecraft MC_CLIENT;
	//a z-offset value that makes GUI elements render on top of any 3D items on the screen
	public static final @Internal int MAGIC_ITEM_Z_OFFSET = 50 + 200;
	// ==================================================
	public TCDCommonsClient() {}
	// ==================================================
}