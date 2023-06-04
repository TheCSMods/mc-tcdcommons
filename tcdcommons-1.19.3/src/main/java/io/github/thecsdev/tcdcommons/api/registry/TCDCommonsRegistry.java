package io.github.thecsdev.tcdcommons.api.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import io.github.thecsdev.tcdcommons.api.features.player.PlayerBadge;
import net.minecraft.util.Identifier;

public class TCDCommonsRegistry
{
	// ==================================================
	/**
	 * Maintains a record of all {@link PlayerBadge}s that have been registered during the current session.<br/>
	 * The term "session" has different meanings in different contexts:<br/>
	 * <ul>
	 * <li>In a client context, a "session" refers to the period when the client is actively
	 * connected to a server or an internal server.</li>
	 * <li>In a server context, a "session" refers to a single runtime of the server, beginning
	 * from when the server starts and ending when it stops.</li>
	 * </ul>
	 */
	public static final BiMap<Identifier, PlayerBadge> PlayerSessionBadges;
	// --------------------------------------------------
	/**
	 * Calls the static constructor for this class if it hasn't been called yet.
	 */
	public static void init() {}
	static
	{
		//define the registries
		PlayerSessionBadges = HashBiMap.create();
	}
	// ==================================================
	protected TCDCommonsRegistry() {}
	// ==================================================
}