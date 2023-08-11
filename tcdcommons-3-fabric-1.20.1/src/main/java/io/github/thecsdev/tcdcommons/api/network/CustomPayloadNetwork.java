package io.github.thecsdev.tcdcommons.api.network;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

public final class CustomPayloadNetwork extends Object
{
	// ==================================================
	private CustomPayloadNetwork() {}
	// --------------------------------------------------
	private static final Map<Identifier, CustomPayloadNetworkReceiver> C2S = new LinkedHashMap<>();
	private static final Map<Identifier, CustomPayloadNetworkReceiver> S2C = new LinkedHashMap<>();
	// ==================================================
	/**
	 * Registers a {@link CustomPayloadNetworkReceiver} for a given custom payload packet.<p>
	 * <b>Important:</b> Receivers are executed on the network thread.
	 * @param side The side which to listen on.
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
	// ==================================================
}