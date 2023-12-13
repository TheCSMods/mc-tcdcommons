package io.github.thecsdev.tcdcommons.api.network;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.network.packet.TCustomPayload;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * This is a utility class that allows you to register custom payload receivers
 * using {@link TCDCommons}'s network implementation.
 * @see #registerReceiver(NetworkSide, Identifier, CustomPayloadNetworkReceiver)
 * @see #unregisterReceiver(NetworkSide, Identifier)
 * @see TCustomPayload
 * @apiNote To send custom payload packets over this network, use {@link TCustomPayload}.
 */
public final class CustomPayloadNetwork extends Object
{
	// ==================================================
	private CustomPayloadNetwork() {}
	// --------------------------------------------------
	//note: Mixin Reflection used these two variables. DO NOT RENAME THEM!
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
	// ==================================================
}