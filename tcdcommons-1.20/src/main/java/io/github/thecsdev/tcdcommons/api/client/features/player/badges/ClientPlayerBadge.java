package io.github.thecsdev.tcdcommons.api.client.features.player.badges;

import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;

/**
 * A client-side-only {@link PlayerBadge} that is only
 * registered in the client-side environment.
 */
public abstract class ClientPlayerBadge extends PlayerBadge
{
	// ==================================================
	protected ClientPlayerBadge() { super(); }
	// --------------------------------------------------
	public final @Override boolean shouldSave() { return false; }
	// ==================================================
}