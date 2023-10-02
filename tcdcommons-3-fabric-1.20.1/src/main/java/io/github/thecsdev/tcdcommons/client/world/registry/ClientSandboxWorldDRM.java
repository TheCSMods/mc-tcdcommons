package io.github.thecsdev.tcdcommons.client.world.registry;

import com.mojang.serialization.Lifecycle;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;

public class ClientSandboxWorldDRM extends MutableDynamicRegistryManager
{
	public ClientSandboxWorldDRM()
	{
		//---------- initialize DAMAGE_TYPE registry
		//obtain registry
		final var dtr = /*(SimpleRegistry<DamageType>)get(RegistryKeys.DAMAGE_TYPE)*/ new DamageTypeDynamicReg();
		try { this.registriesPutMethod.invoke(this.registries, RegistryKeys.DAMAGE_TYPE, dtr); }
		catch(Exception e) { throw new RuntimeException("Failed to create DamageType registry.", e); }
		
		//create and use a Registerable wrapper instance to
		//register all damage types to the registry
		DamageTypes.bootstrap(new Registerable<DamageType>()
		{
			@SuppressWarnings("unchecked")
			public @Override <S> RegistryEntryLookup<S> getRegistryLookup
			(RegistryKey<? extends Registry<? extends S>> registryRef)
			{
				return (RegistryEntryLookup<S>)dtr.createMutableEntryLookup();
			}
			
			public @Override Reference<DamageType> register
			(RegistryKey<DamageType> key, DamageType value, Lifecycle lifecycle)
			{
				Registry.register(dtr, key, value);
				return Reference.standAlone(dtr.getEntryOwner(), key);
			}
		});
		
		//---------- initialize ResourceKey[minecraft:root / minecraft:worldgen/biome] registry
		final var btr = (SimpleRegistry<Biome>)get(RegistryKeys.BIOME);
		BuiltinBiomes.bootstrap(new Registerable<Biome>()
		{
			@SuppressWarnings("unchecked")
			public @Override <S> RegistryEntryLookup<S> getRegistryLookup
			(RegistryKey<? extends Registry<? extends S>> registryRef)
			{
				return (RegistryEntryLookup<S>)btr.createMutableEntryLookup();
			}
			
			public @Override Reference<Biome> register
			(RegistryKey<Biome> key, Biome value, Lifecycle lifecycle)
			{
				Registry.register(btr, key, value);
				return Reference.standAlone(btr.getEntryOwner(), key);
			}
		});
	}
}