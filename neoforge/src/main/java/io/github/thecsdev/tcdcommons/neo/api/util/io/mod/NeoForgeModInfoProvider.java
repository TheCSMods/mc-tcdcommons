package io.github.thecsdev.tcdcommons.neo.api.util.io.mod;

import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfo;
import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfoProvider;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public final class NeoForgeModInfoProvider extends ModInfoProvider
{
	public final @Nullable @Override ModInfo getModInfo(String modId)
		throws NullPointerException { return new NeoForgeModInfo(modId); }

	public final @Override String[] getLoadedModIDs()
	{
		return FMLLoader.getLoadingModList().getMods().stream()
			.map(it -> it.getModId())
			.toArray(String[]::new);
	}
}
