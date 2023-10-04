package io.github.thecsdev.tcdcommons.mixin.hooks;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadC2SPacket.class)
public interface AccessorCustomPayloadC2SPacket
{
	@Accessor("ID_TO_READER")
	static Map<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>> getIdToReader()
	{
		throw new AssertionError();
	}
	
	@Mutable
	@Accessor("ID_TO_READER")
	static void setIdToReader(Map<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>> idToReader)
	{
		throw new AssertionError();
	}
}