package io.github.thecsdev.tcdcommons.client;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.client.network.TCDCommonsClientNetworkHandler;
import net.minecraft.client.MinecraftClient;

public final class TCDCommonsClient extends TCDCommons
{
	//a helper field so `MinecraftClient.getInstance()` doesn't have to be called a bunch of times
	public static final @Internal @Nullable MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
	
	public TCDCommonsClient()
	{
		//init stuff
		TCDCommonsClientNetworkHandler.init();
	}
}