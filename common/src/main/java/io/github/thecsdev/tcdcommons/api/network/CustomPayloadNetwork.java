package io.github.thecsdev.tcdcommons.api.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * <b>TCDCommons API</b>'s networking API, for registering and handling custom payloads.
 */
@SuppressWarnings("removal")
public final class CustomPayloadNetwork
{
	// ==================================================
	private CustomPayloadNetwork() {}
	// ==================================================
	/**
	 * An internal method for converting vanilla game {@link PacketFlow} to
	 * Architectury API {@link NetworkManager.Side}.
	 */
	@ApiStatus.Internal
	private static final NetworkManager.Side flow2side(PacketFlow packetFlow)
	{
		return switch(packetFlow) {
			case SERVERBOUND -> NetworkManager.Side.C2S;
			case CLIENTBOUND -> NetworkManager.Side.S2C;
			default -> throw new IllegalArgumentException("Invalid packet flow - " + packetFlow);
		};
	}
	// --------------------------------------------------
	/**
	 * Registers a custom payload packet receiver for a given side.
	 * @param side The {@link PacketFlow} direction aka side.
	 * @param id The custom payload ID.
	 * @param receiver The thing that will handle packets on the receiving end.
	 */
	public static final CustomPayloadNetworkReceiver registerReciever(
			PacketFlow side, ResourceLocation id, CustomPayloadNetworkReceiver receiver)
	{
		//preparation
		final var archSide = flow2side(Objects.requireNonNull(side));
		Objects.requireNonNull(id);
		Objects.requireNonNull(receiver);

		//register
		NetworkManager.registerReceiver(archSide, id, (buffer, ctx) ->
		{
			final PacketListener connection = switch(archSide) {
				case S2C -> ((LocalPlayer)ctx.getPlayer()).connection;
				case C2S -> ((ServerPlayer)ctx.getPlayer()).connection;
			};
			receiver.receiveCustomPayload(new CustomPayloadNetworkReceiver.PacketContext()
			{
				public @Override PacketListener getPacketListener() { return connection; }
				public @Override PacketFlow getNetworkSide() { return side; }
				public @Override ResourceLocation getPacketId() { return id; }
				public @Override FriendlyByteBuf getPacketBuffer() { return buffer; }
				public @Override Player getPlayer() { return ctx.getPlayer(); }
			});
		});

		//return once registered
		return receiver;
	}
	// --------------------------------------------------
	public static final void sendC2S(ResourceLocation id, ByteBuf buffer) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(buffer);
		NetworkManager.sendToServer(id, new RegistryFriendlyByteBuf(buffer, Minecraft.getInstance().getConnection().registryAccess()));
	}

	public static final void sendS2C(ServerPlayer player, ResourceLocation id, ByteBuf buffer) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(id);
		Objects.requireNonNull(buffer);
		NetworkManager.sendToPlayer(player, id, new RegistryFriendlyByteBuf(buffer, player.registryAccess()));
	}
	// ==================================================
}
