package io.github.thecsdev.tcdcommons.client;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;
import net.fabricmc.api.ClientModInitializer;

public final class TCDCommonsClient extends TCDCommons implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		TCDCommonsClientRegistry.init();
	}
}