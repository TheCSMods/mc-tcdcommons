package io.github.thecsdev.tcdcommons.api.util.io.mod;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;

final class FabricModInfoProvider extends ModInfoProvider
{
	// ==================================================
	public final @Override @Nullable ModInfo getModInfo(String modId) throws NullPointerException
	{
		if(!FabricLoader.getInstance().isModLoaded(Objects.requireNonNull(modId)))
			return null;
		return new FabricModInfo(modId);
	}
	// --------------------------------------------------
	public final @Override String[] getLoadedModIDs()
	{
		return FabricLoader.getInstance().getAllMods().stream()
				.map(mod -> mod.getMetadata().getId())
				.toArray(String[]::new);
	}
	// --------------------------------------------------
	public final @Override boolean isModLoaded(String modId) throws NullPointerException
	{
		return FabricLoader.getInstance().isModLoaded(Objects.requireNonNull(modId));
	}
	// ==================================================
}