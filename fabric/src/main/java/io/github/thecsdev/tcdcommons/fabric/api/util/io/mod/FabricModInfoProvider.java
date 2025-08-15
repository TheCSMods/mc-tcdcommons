package io.github.thecsdev.tcdcommons.fabric.api.util.io.mod;

import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfo;
import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfoProvider;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class FabricModInfoProvider extends ModInfoProvider
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