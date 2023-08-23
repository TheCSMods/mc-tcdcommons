package io.github.thecsdev.tcdcommons.client;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.client.network.TCDCommonsClientNetworkHandler;
import net.minecraft.client.MinecraftClient;

public final class TCDCommonsClient extends TCDCommons
{
	//a helper field so `MinecraftClient.getInstance()` doesn't have to be called a bunch of times
	public static final @Internal MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
	//a z-offset value that makes GUI elements render on top of any 3D items on the screen
	public static final @Internal int MAGIC_ITEM_Z_OFFSET = 125;
	
	public TCDCommonsClient()
	{
		//init stuff
		TCDCommonsClientNetworkHandler.init();
	}
}