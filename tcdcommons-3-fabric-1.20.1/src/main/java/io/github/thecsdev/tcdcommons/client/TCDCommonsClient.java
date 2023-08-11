package io.github.thecsdev.tcdcommons.client;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.client.network.TCDCommonsClientNetworkHandler;
import net.minecraft.client.MinecraftClient;

public final class TCDCommonsClient extends TCDCommons
{
	public static final @Nullable MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
	public TCDCommonsClient()
	{
		//init stuff
		TCDCommonsClientNetworkHandler.init();
	}
}