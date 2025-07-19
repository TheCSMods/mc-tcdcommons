package io.github.thecsdev.tcdcommons.client.world.registry;

import com.mojang.serialization.Lifecycle;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;

class DamageTypeDynamicReg extends SimpleRegistry<DamageType>
{
	public DamageTypeDynamicReg() { super(RegistryKeys.DAMAGE_TYPE, Lifecycle.stable()); }
}