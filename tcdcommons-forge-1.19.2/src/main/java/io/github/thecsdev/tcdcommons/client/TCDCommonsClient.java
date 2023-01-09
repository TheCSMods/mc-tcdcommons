package io.github.thecsdev.tcdcommons.client;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.registry.TCDCommonsClientRegistry;

public class TCDCommonsClient extends TCDCommons
{
	public TCDCommonsClient()
	{
		TCDCommonsClientRegistry.init();
	}
}