package io.github.thecsdev.tcdcommons.server;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.server.registry.TCDCommonsServerRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;

public final class TCDCommonsServer extends TCDCommons implements DedicatedServerModInitializer
{
	// ==================================================
	public @Override void onInitializeServer()
	{
		//init the server registry API
		TCDCommonsServerRegistry.init();
	}
	// ==================================================
}