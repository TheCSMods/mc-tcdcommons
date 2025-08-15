package io.github.thecsdev.tcdcommons.fabric;

import io.github.thecsdev.tcdcommons.TCDCommonsConfig;
import io.github.thecsdev.tcdcommons.fabric.api.util.io.mod.FabricModInfoProvider;
import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfoProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric Mod Loader entry-points for this mod.
 */
public final class TCDCommonsFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer
{
	// ==================================================
	public @Override void onInitializeClient() { new io.github.thecsdev.tcdcommons.client.TCDCommonsClient(); }
	public @Override void onInitializeServer() { new io.github.thecsdev.tcdcommons.server.TCDCommonsServer(); }
	// ----------------------------------------------------
	public @Override void onInitialize()
	{
		TCDCommonsConfig.DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
		ModInfoProvider.setInstance(new FabricModInfoProvider());
	}
	// ==================================================
}