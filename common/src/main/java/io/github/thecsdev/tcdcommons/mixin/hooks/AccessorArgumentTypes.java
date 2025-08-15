package io.github.thecsdev.tcdcommons.mixin.hooks;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ArgumentTypeInfos.class)
public interface AccessorArgumentTypes
{
	static @Accessor("BY_CLASS") Map<Class<?>, ArgumentTypeInfo<?, ?>> getClassMap() { throw new AssertionError(); }
}