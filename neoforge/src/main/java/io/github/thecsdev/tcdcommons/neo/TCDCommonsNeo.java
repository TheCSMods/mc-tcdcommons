package io.github.thecsdev.tcdcommons.neo;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfoProvider;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import io.github.thecsdev.tcdcommons.neo.api.util.io.mod.NeoForgeModInfoProvider;
import io.github.thecsdev.tcdcommons.server.TCDCommonsServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(TCDCommons.ModID)
public class TCDCommonsNeo
{
	// ==================================================
	public TCDCommonsNeo()
	{
		//init mod info provider
		ModInfoProvider.setInstance(new NeoForgeModInfoProvider());

		//create an instance of the mod's main class, depending on the dist
		switch(FMLEnvironment.dist)
		{
			case CLIENT           -> new TCDCommonsClient();
			case DEDICATED_SERVER -> new TCDCommonsServer();
		}
	}
	// ==================================================
}
