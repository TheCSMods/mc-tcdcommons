package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.util.collections.GenericProperties;
import io.github.thecsdev.tcdcommons.mixin.addons.MixinEntityAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntity implements MixinEntityAddon
{
	// ==================================================
	private @Unique GenericProperties<ResourceLocation> tcdcommons_customData = new GenericProperties<>();
	public @Override GenericProperties<ResourceLocation> tcdcommons_getCustomData() { return tcdcommons_customData; }
	// ==================================================
	//protected abstract @Shadow void populateCrashReport(CrashReportSection section);
	// ==================================================
}