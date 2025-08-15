package io.github.thecsdev.tcdcommons.client.mixin;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * An {@link Internal} class for {@link TCDCommonsClient} {@link Mixin}s.
 * @apiNote Do not interact with this class at all, in any way! Doing so will break stuff.
 */
public final @Internal class TCMInternal
{
	//don't allow instance constructions
	private TCMInternal() {}
	
	//tracks the currently opened TScreen, so `instanceof` and casting don't have to be done
	public static @Internal @Nullable TScreen CURRENT_T_SCREEN = null;
	
	//tracks the currently applied scissors during the current rendering frame -- Not needed anymore
	//public static final @Internal Rectangle CURRENT_SCISSORS = new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
}