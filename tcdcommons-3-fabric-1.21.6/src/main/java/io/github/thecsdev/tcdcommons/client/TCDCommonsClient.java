package io.github.thecsdev.tcdcommons.client;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.test.client.gui.screen.TestTScreen;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.client.MinecraftClient;

public final class TCDCommonsClient extends TCDCommons
{
	// ==================================================
	//a helper field so `MinecraftClient.getInstance()` doesn't have to be called a bunch of times
	public static final @Internal MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
	//a z-offset value that makes GUI elements render on top of any 3D items on the screen
	public static final @Internal int MAGIC_ITEM_Z_OFFSET = 50 + 200;
	// ==================================================
	public TCDCommonsClient()
	{
		//FIXME - Remove;
		ClientPlayerBlockBreakEvents.AFTER.register((world, player, position, state) ->
		{
			final var screen = new TestTScreen(null);
			MC_CLIENT.setScreen(screen.getAsScreen());
		});
	}
	// ==================================================
}