package io.github.thecsdev.tcdcommons.api.events;

import com.google.common.collect.BiMap;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry;
import net.minecraft.util.Identifier;

/**
 * {@link Event}s related to registries such as {@link TCDCommonsRegistry}.
 */
public interface TRegistryEvent
{
	// ==================================================
	/**
	 * See {@link BadgeRegistrationCallback#badgeRegistrationCallback(BiMap)}
	 */
	Event<BadgeRegistrationCallback> PLAYER_BADGE = EventFactory.createLoop();
	// ==================================================
	interface BadgeRegistrationCallback
	{
		/**
		 * An event that is invoked when {@link PlayerBadge}s need to be registered somewhere.<br/>
		 * Typically used for {@link TCDCommonsRegistry#PlayerBadges} registrations.
		 * @param badgeRegistry The registry where {@link PlayerBadge}s are kept track of.
		 * @see TCDCommonsRegistry#PlayerBadges
		 */
		void badgeRegistrationCallback(BiMap<Identifier, PlayerBadge> badgeRegistry);
	}
	// ==================================================
}