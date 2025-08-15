package io.github.thecsdev.tcdcommons.neo.api.util.io.mod;

import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class NeoForgeModInfo extends ModInfo
{
	// ==================================================
	private final net.neoforged.fml.loading.moddiscovery.ModInfo modContainer;
	private final String modVersion;
	private final Component modName, modDescription;
	private final Component[] authors;
	private final @Nullable ResourceLocation modIconId;
	private final @Nullable String modHomePageUrl, modSourcesUrl;
	// ==================================================
	public NeoForgeModInfo(String modId) throws NullPointerException, NoSuchElementException
	{
		super(modId);
		this.modContainer = FMLLoader.getLoadingModList().getMods()
				.stream()
				.filter(it -> it.getModId().equals(modId))
				.findFirst()
				.orElseThrow();

		this.modVersion     = this.modContainer.getVersion().toString();
		this.modName        = Component.literal(this.modContainer.getDisplayName());
		this.modDescription = Component.literal(this.modContainer.getDescription());
		this.authors        = new Component[0];
		this.modIconId      = ResourceLocation.fromNamespaceAndPath(modId, this.modContainer.getLogoFile().orElse("assets/icon.png"));
		this.modHomePageUrl = this.modContainer.getUpdateURL().map(url -> Objects.toString(url)).orElse("");
		this.modSourcesUrl  = this.modHomePageUrl;
	}
	// ==================================================
	public final @Override String getVersion() { return this.modVersion; }
	public final @Override Component getName() { return this.modName; }
	public final @Override Component getDescription() { return this.modDescription; }
	public final @Override Component[] getAuthors() { return this.authors; }
	public final @Override @Nullable ResourceLocation getIconId() { return this.modIconId; }
	public final @Override @Nullable String getHomePageURL() { return this.modHomePageUrl; }
	public final @Override @Nullable String getSourcesURL() { return this.modSourcesUrl; }
	// ==================================================
}
