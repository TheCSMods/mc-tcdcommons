package io.github.thecsdev.tcdcommons.api.client.badge;

import static io.github.thecsdev.tcdcommons.api.badge.PlayerBadgeHandler.PBH_CUSTOM_DATA_ID;
import static io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks.getCustomDataEntryG;
import static io.github.thecsdev.tcdcommons.api.hooks.entity.EntityHooks.setCustomDataEntryG;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.badge.PlayerBadgeHandler;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * A client-side-only {@link PlayerBadge} that is only
 * registered in the client-side environment.
 */
public abstract class ClientPlayerBadge extends PlayerBadge
{
	// ==================================================
	public final @Override boolean shouldSave() { return false; }
	// ==================================================
	/**
	 * Returns a "client-side" {@link PlayerBadgeHandler} for a given {@link ClientPlayerEntity},
	 * that tracks {@link PlayerBadge} statistics on the "client-side".
	 * @param localPlayer The {@link ClientPlayerEntity}.
	 */
	@SuppressWarnings("deprecation")
	public static final PlayerBadgeHandler getClientPlayerBadgeHandler(ClientPlayerEntity localPlayer)
	{
		@Nullable PlayerBadgeHandler badgeHandler = getCustomDataEntryG(localPlayer, PBH_CUSTOM_DATA_ID);
		if(badgeHandler == null)
			badgeHandler = setCustomDataEntryG(localPlayer, PBH_CUSTOM_DATA_ID, new PlayerBadgeHandler());
		return badgeHandler;
	}
	// ==================================================
}