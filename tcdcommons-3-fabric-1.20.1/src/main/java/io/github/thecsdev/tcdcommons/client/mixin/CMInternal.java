package io.github.thecsdev.tcdcommons.client.mixin;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.spongepowered.asm.mixin.Mixin;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;

/**
 * An {@link Internal} class for {@link TCDCommonsClient} {@link Mixin}s.
 * @apiNote Do not interact with this class at all, in any way! Doing so will break stuff.
 */
public final @Internal class CMInternal
{
	//don't allow instance constructions
	private CMInternal() {}
	
	//tracks the currently opened TScreen, so `instanceof` and casting don't have to be done
	public static @Internal @Nullable TScreen CURRENT_T_SCREEN = null;
}