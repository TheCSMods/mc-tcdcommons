package thecsdev.tcdcommons.client;

import net.fabricmc.api.ClientModInitializer;
import thecsdev.tcdcommons.TCDCommons;
import thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;

public final class TCDCommonsClient extends TCDCommons implements ClientModInitializer
{
	/** This does nothing. */
	@Override
	public void onInitializeClient()
	{
		TCDCommonsClientRegistry.init();
	}
}