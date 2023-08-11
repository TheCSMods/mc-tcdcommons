package io.github.thecsdev.tcdcommons.api.client.badge;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;

/**
 * A client-side-only {@link PlayerBadge} that is only
 * registered in the client-side environment.
 */
public abstract class ClientPlayerBadge extends PlayerBadge
{
	public final @Override boolean shouldSave() { return false; }
}