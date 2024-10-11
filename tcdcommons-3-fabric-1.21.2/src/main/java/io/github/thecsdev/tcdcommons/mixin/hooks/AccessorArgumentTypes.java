package io.github.thecsdev.tcdcommons.mixin.hooks;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@Mixin(ArgumentTypes.class)
public interface AccessorArgumentTypes
{
	static @Accessor("CLASS_MAP") Map<Class<?>, ArgumentSerializer<?, ?>> getClassMap() { throw new AssertionError(); }
}