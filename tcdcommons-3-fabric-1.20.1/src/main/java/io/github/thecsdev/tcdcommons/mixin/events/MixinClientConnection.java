package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection
{
	// ==================================================
	protected abstract @Shadow boolean isOpen();
	// ==================================================
	/*@Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks)V", at = @At("HEAD"))
	public void onPreSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci)
	{
		if(!isOpen()) return;
	}*/
	// ==================================================
}