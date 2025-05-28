package io.github.thecsdev.tcdcommons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

/**
 * Fabric Mod Loader entry-points for this mod.
 */
public final class TCDCommonsFabric implements ClientModInitializer, DedicatedServerModInitializer
{
	// ==================================================
	public @Override void onInitializeClient() { new io.github.thecsdev.tcdcommons.client.TCDCommonsClient(); }
	public @Override void onInitializeServer() { new io.github.thecsdev.tcdcommons.server.TCDCommonsServer(); }
	// ==================================================
}