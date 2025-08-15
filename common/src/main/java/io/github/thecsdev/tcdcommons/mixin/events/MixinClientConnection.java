package io.github.thecsdev.tcdcommons.mixin.events;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.thecsdev.tcdcommons.TCDCommonsConfig.DEV_ENV;

@Mixin(Connection.class)
public abstract class MixinClientConnection
{
	// ==================================================
	@Inject(method = "exceptionCaught", at = @At("HEAD"))
	public void onExceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci)
	{
		//for debugging purposes when an exception is raised on the network thread
		//because Minecraft doesn't log or display such exceptions
		if(DEV_ENV) ex.printStackTrace();
	}
	// ==================================================
}