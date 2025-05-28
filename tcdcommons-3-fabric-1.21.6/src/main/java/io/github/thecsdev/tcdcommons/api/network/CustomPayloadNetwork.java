package io.github.thecsdev.tcdcommons.api.network;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import io.github.thecsdev.tcdcommons.client.network.TcdcClientPlayNetworkHandler;
import io.github.thecsdev.tcdcommons.network.TcdcServerPlayNetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * This is a utility class that allows you to register custom payload receivers
 * using {@link TCDCommons}'s network implementation.
 * @see #registerReceiver(NetworkSide, Identifier, CustomPayloadNetworkReceiver)
 * @see #unregisterReceiver(NetworkSide, Identifier)
 */
public final class CustomPayloadNetwork extends Object
{
	// ==================================================
	private CustomPayloadNetwork() {}
	// --------------------------------------------------
	/**
	 * The unique {@link Identifier} of {@link TCDCommons}'s {@link TCustomPayload}
	 * packets for the {@link CustomPayloadNetwork}.
	 * @see TCustomPayload#ID
	 */
	public static final Identifier CPN_PACKET_ID = Identifier.of(getModID(), "cpn");
	// --------------------------------------------------
	//Note: Mixin accessor are used for these variables. DO NOT RENAME THEM!
	private static final Map<Identifier, CustomPayloadNetworkReceiver> C2S_PLAY = new LinkedHashMap<>();
	private static final Map<Identifier, CustomPayloadNetworkReceiver> S2C_PLAY = new LinkedHashMap<>();
	// ==================================================
	/**
	 * Please use {@link #registerPlayReceiver(NetworkSide, Identifier, CustomPayloadNetworkReceiver)}.<br/>
	 * {@link Deprecated} because the method's name changed for clarity reasons.
	 */
	@Deprecated(since = "v3.11", forRemoval = true)
	public static CustomPayloadNetworkReceiver registerReceiver
	(NetworkSide side, Identifier packetId, CustomPayloadNetworkReceiver receiver)
	{
		return registerPlayReceiver(side, packetId, receiver);
	}
	
	/**
	 * Registers a {@link CustomPayloadNetworkReceiver} for a given custom payload packet.<p>
	 * <b>Important:</b> Receivers are executed on the network thread.
	 * @param side The {@link NetworkSide} which to listen on.
	 * @param packetId The unique {@link Identifier} of the custom payload packets to listen for.
	 * @param receiver The listener interface.
	 * @return The {@link CustomPayloadNetworkReceiver} that was registered.
	 * @throws NullPointerException If an argument is null.
	 * @throws IllegalArgumentException If the {@link NetworkSide} is "unexpected".
	 * @see ThreadExecutor#executeSync(Runnable)
	 * @apiNote These packets take place during the "play" network phase.
	 * @apiNote {@link CustomPayloadNetworkReceiver}s are executed on the network thread.
	 * @apiNote {@link NetworkSide#CLIENTBOUND} only works on the client-side.
	 */
	public static CustomPayloadNetworkReceiver registerPlayReceiver
	(NetworkSide side, Identifier packetId, CustomPayloadNetworkReceiver receiver)
	{
		Objects.requireNonNull(packetId);
		Objects.requireNonNull(receiver);
		switch(Objects.requireNonNull(side))
		{
			case SERVERBOUND: C2S_PLAY.put(packetId, receiver); break;
			case CLIENTBOUND: S2C_PLAY.put(packetId, receiver); break;
			default: throw new IllegalArgumentException("Unexpected network side " + side);
		}
		return receiver;
	}
	
	/**
	 * Please see {@link #unregisterPlayReceiver(NetworkSide, Identifier)}.<br/>
	 * {@link Deprecated} because the method's name changed for clarity reasons.
	 */
	@Deprecated(since = "v3.11", forRemoval = true)
	public static boolean unregisterReceiver(NetworkSide side, Identifier packetId)
	{
		return unregisterPlayReceiver(side, packetId);
	}
	
	/**
	 * Un-Registers a {@link CustomPayloadNetworkReceiver} for a given custom payload packet.
	 * @param side The {@link NetworkSide} the {@link CustomPayloadNetworkReceiver} is listening on.
	 * @param packetId The unique {@link Identifier} of the custom payload packets being listened.
	 * @return {@code true} if the {@link CustomPayloadNetworkReceiver} was present before getting removed.
	 */
	public static boolean unregisterPlayReceiver(NetworkSide side, Identifier packetId)
	{
		Objects.requireNonNull(packetId);
		return switch (Objects.requireNonNull(side))
		{
			case SERVERBOUND -> (C2S_PLAY.remove(packetId) != null);
			case CLIENTBOUND -> (S2C_PLAY.remove(packetId) != null);
			default -> false;
		};
	}
	// --------------------------------------------------
	/**
	 * Sends a custom payload from the client to the server, using the {@link TCDCommons} network protocol.
	 * @param packetId The custom payload packet {@link Identifier}.
	 * @param packetData The custom payload.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If (NOT_CLIENT) or (REF_CNT &lt; 1).
	 * @apiNote The {@link ByteBuf} WILL be released!
	 */
	public static final void sendC2S(Identifier packetId, ByteBuf packetData)
		throws IllegalStateException, NullPointerException
	{
		//requirements
		if(!TCDCommons.isClient()) throw new IllegalStateException("NOT_CLIENT");

		//obtain player
		@SuppressWarnings("resource")
		final var player = net.minecraft.client.MinecraftClient.getInstance().player;
		if(player == null || player.networkHandler == null) return;

		//send data
		TcdcClientPlayNetworkHandler.of(player).sendCustomPayloadNetwork(packetId, packetData);
	}
	
	/**
	 * Sends a custom payload from the server to the client, using the {@link TCDCommons} network protocol.
	 * @param player The {@link ServerPlayerEntity} to send the packet to.
	 * @param packetId The custom payload packet {@link Identifier}.
	 * @param packetData The custom payload.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If (REF_CNT &lt; 1).
	 * @apiNote The {@link ByteBuf} WILL be released!
	 */
	public static final void sendS2C(ServerPlayerEntity player, Identifier packetId, ByteBuf packetData)
			throws IllegalStateException, NullPointerException
	{
		Objects.requireNonNull(player);
		TcdcServerPlayNetworkHandler.of(player).sendCustomPayloadNetwork(packetId, packetData);
	}
	// ==================================================
}