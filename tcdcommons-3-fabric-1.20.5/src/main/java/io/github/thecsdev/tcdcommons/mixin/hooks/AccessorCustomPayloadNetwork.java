package io.github.thecsdev.tcdcommons.mixin.hooks;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetwork;
import io.github.thecsdev.tcdcommons.api.network.CustomPayloadNetworkReceiver;
import net.minecraft.util.Identifier;

@Mixin(value = CustomPayloadNetwork.class, remap = false)
public interface AccessorCustomPayloadNetwork
{
	static @Accessor("CPN_PACKET_ID") Identifier getCpnPacketId() { throw new AssertionError(); }
	static @Accessor("C2S") Map<Identifier, CustomPayloadNetworkReceiver> getC2S() { throw new AssertionError(); }
	static @Accessor("S2C") Map<Identifier, CustomPayloadNetworkReceiver> getS2C() { throw new AssertionError(); }
}