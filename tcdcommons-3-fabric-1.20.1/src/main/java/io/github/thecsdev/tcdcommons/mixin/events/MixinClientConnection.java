package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection
{
	// ==================================================
	protected abstract @Shadow boolean isOpen();
	// ==================================================
	@Inject(method = "exceptionCaught", at = @At("HEAD"))
	public void onExceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci)
	{
		//for debugging purposes when an exception is raised on the network thread
		//because Minecraft doesn't log or display such exceptions
		if(FabricLoader.getInstance().isDevelopmentEnvironment())
			ex.printStackTrace();
	}
	// ==================================================
}