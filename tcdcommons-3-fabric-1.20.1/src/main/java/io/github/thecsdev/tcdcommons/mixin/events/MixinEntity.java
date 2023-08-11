package io.github.thecsdev.tcdcommons.mixin.events;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.google.common.collect.Maps;

import io.github.thecsdev.tcdcommons.mixin.addons.MixinEntityAddon;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Mixin(Entity.class)
public abstract class MixinEntity implements MixinEntityAddon
{
	// ==================================================
	private @Unique HashMap<Identifier, Object> tcdcommons_customData = Maps.newHashMap();
	public @Override HashMap<Identifier, Object> tcdcommons_getCustomData() { return tcdcommons_customData; }
	// ==================================================
	//protected abstract @Shadow void populateCrashReport(CrashReportSection section);
	// ==================================================
}