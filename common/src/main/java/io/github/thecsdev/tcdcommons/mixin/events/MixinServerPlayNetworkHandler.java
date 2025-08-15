package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.server.PlayerManagerEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//using higher priority to allow other mods's mixins to cancel events before this mixin handles them
@Mixin(value = ServerGamePacketListenerImpl.class, priority = 9001)
public abstract class MixinServerPlayNetworkHandler extends ServerCommonPacketListenerImpl
{
	private MixinServerPlayNetworkHandler(MinecraftServer server, Connection connection, CommonListenerCookie clientData) { super(server, connection, clientData); }

	public @Shadow ServerPlayer player;
	
	@Inject(method = "handleChat", at = @At("TAIL")) //note: has to be tail
	public void onOnChatMessage(ServerboundChatPacket packet, CallbackInfo callback)
	{
		PlayerManagerEvent.PLAYER_CHATTED.invoker().invoke(player, packet.message());
	}
}