package io.github.thecsdev.tcdcommons.fabric.api.util.io.mod;

import io.github.thecsdev.tcdcommons.api.util.io.mod.ModInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.fLiteral;

public final class FabricModInfo extends ModInfo
{
	// ==================================================
	private final ModContainer modContainer;
	private final String modVersion;
	private final Component modName, modDescription;
	private final Component[] authors;
	private final @Nullable ResourceLocation modIconId;
	private final @Nullable String modHomePageUrl, modSourcesUrl;
	// ==================================================
	public FabricModInfo(String modId) throws NullPointerException, NoSuchElementException
	{
		super(modId);
		this.modContainer = FabricLoader.getInstance().getModContainer(modId).orElseThrow();
		
		final var meta = this.modContainer.getMetadata();
		this.modVersion = meta.getVersion().toString();
		this.modName = fLiteral(meta.getName());
		this.modDescription = fLiteral(meta.getDescription());
		this.authors = meta.getAuthors().stream().map(person -> fLiteral(person.getName())).toArray(Component[]::new);
		
		this.modIconId = ResourceLocation.fromNamespaceAndPath(modId, "icon.png"); //FIXME - Obtain mod icon path properly
		
		final var contact = meta.getContact();
		this.modHomePageUrl = contact.get("homepage").orElse(null);
		this.modSourcesUrl = contact.get("sources").orElse(null);
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