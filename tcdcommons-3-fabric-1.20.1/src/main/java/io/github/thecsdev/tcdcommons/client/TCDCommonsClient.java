package io.github.thecsdev.tcdcommons.client;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TEntityRendererElement;
import io.github.thecsdev.tcdcommons.client.network.TCDCommonsClientNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EntityType;

public final class TCDCommonsClient extends TCDCommons
{
	//a helper field so `MinecraftClient.getInstance()` doesn't have to be called a bunch of times
	public static final @Internal MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
	//a z-offset value that makes GUI elements render on top of any 3D items on the screen
	public static final @Internal int MAGIC_ITEM_Z_OFFSET = 50 + ItemRenderer.field_32934;
	
	public TCDCommonsClient()
	{
		//init stuff
		TCDCommonsClientNetworkHandler.init();
		
		//pre-initialize components now to avoid lag spikes later
		try
		{
			//trigger the loading of entity renderer classes
			new TEntityRendererElement(0, 0, 1, 1, EntityType.MARKER);
		}
		catch(Throwable e) {}
	}
}