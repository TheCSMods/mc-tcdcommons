package io.github.thecsdev.tcdcommons.api.network;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

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
	 * The unique {@link Identifier} of {@link TCDCommons}'s custom payload
	 * packets for the {@link CustomPayloadNetwork}.
	 */
	//note: Mixin Reflection used these variables. DO NOT RENAME THEM!
	static final @Internal Identifier CPN_PACKET_ID = new Identifier(getModID(), "cpn");
	// --------------------------------------------------
	//note: Mixin Reflection used these variables. DO NOT RENAME THEM!
	private static final Map<Identifier, CustomPayloadNetworkReceiver> C2S = new LinkedHashMap<>();
	private static final Map<Identifier, CustomPayloadNetworkReceiver> S2C = new LinkedHashMap<>();
	// ==================================================
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
	public static CustomPayloadNetworkReceiver registerReceiver
	(NetworkSide side, Identifier packetId, CustomPayloadNetworkReceiver receiver)
	{
		Objects.requireNonNull(packetId);
		Objects.requireNonNull(receiver);
		switch(Objects.requireNonNull(side))
		{
			case SERVERBOUND: C2S.put(packetId, receiver); break;
			case CLIENTBOUND: S2C.put(packetId, receiver); break;
			default: throw new IllegalArgumentException("Unexpected network side " + side);
		}
		return receiver;
	}
	
	/**
	 * Un-Registers a {@link CustomPayloadNetworkReceiver} for a given custom payload packet.
	 * @param side The {@link NetworkSide} the {@link CustomPayloadNetworkReceiver} is listening on.
	 * @param packetId The unique {@link Identifier} of the custom payload packets being listened.
	 * @return {@code true} if the {@link CustomPayloadNetworkReceiver} was present before getting removed.
	 */
	public static boolean unregisterReceiver(NetworkSide side, Identifier packetId)
	{
		Objects.requireNonNull(packetId);
		switch(Objects.requireNonNull(side))
		{
			case SERVERBOUND: return (C2S.remove(packetId) != null);
			case CLIENTBOUND: return (S2C.remove(packetId) != null);
			default: return false;
		}
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
		Objects.requireNonNull(packetId);
		Objects.requireNonNull(packetData);
		if(!TCDCommons.isClient()) throw new IllegalStateException("NOT_CLIENT");
		else if(packetData.refCnt() < 1) throw new IllegalStateException("REF_CNT");

		//obtain connection
		final var mc = net.minecraft.client.MinecraftClient.getInstance();
		final @Nullable var conn = mc.getNetworkHandler();
		if(conn == null) return;

		//send data
		conn.sendPacket(new CustomPayloadC2SPacket(new TCustomPayload(packetId, packetData)));
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
		//requirements
		Objects.requireNonNull(player);
		Objects.requireNonNull(packetId);
		Objects.requireNonNull(packetData);
		//if(!TCDCommons.isServer()) throw new IllegalStateException("NOT_SERVER"); --hats off to me being stupid here
		if(packetData.refCnt() < 1) throw new IllegalStateException("REF_CNT");

		//send data
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(
				new TCustomPayload(packetId, packetData)));
	}
	// ==================================================
}