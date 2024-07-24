package io.github.thecsdev.tcdcommons.client.world.registry;

import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.Lifecycle;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry.Reference;

class DamageTypeDynamicReg extends SimpleRegistry<DamageType>
{
	public DamageTypeDynamicReg() { super(RegistryKeys.DAMAGE_TYPE, Lifecycle.stable()); }

	@Override
	public Optional<Reference<DamageType>> getEntry(RegistryKey<DamageType> key)
	{
		if(!this.contains(key))
			this.add(key, new DamageType(Objects.toString(key.getValue()), 0), getLifecycle());
		return super.getEntry(key);
	}
}