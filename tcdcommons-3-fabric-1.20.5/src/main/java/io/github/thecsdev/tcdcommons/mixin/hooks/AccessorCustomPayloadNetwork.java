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
	static @Accessor("C2S_PLAY") Map<Identifier, CustomPayloadNetworkReceiver> getPlayC2S() { throw new AssertionError(); }
	static @Accessor("S2C_PLAY") Map<Identifier, CustomPayloadNetworkReceiver> getPlayS2C() { throw new AssertionError(); }
}