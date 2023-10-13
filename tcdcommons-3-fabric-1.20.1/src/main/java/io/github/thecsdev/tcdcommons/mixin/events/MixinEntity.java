package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.thecsdev.tcdcommons.api.util.collections.GenericProperties;
import io.github.thecsdev.tcdcommons.mixin.addons.MixinEntityAddon;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Mixin(Entity.class)
public abstract class MixinEntity implements MixinEntityAddon
{
	// ==================================================
	private @Unique GenericProperties<Identifier> tcdcommons_customData = new GenericProperties<>();
	public @Override GenericProperties<Identifier> tcdcommons_getCustomData() { return tcdcommons_customData; }
	// ==================================================
	//protected abstract @Shadow void populateCrashReport(CrashReportSection section);
	// ==================================================
}