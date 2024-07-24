package io.github.thecsdev.tcdcommons.api.util.io.mod;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.fLiteral;

import java.util.NoSuchElementException;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

final class FabricModInfo extends ModInfo
{
	// ==================================================
	private final ModContainer modContainer;
	private final String modVersion;
	private final Text modName, modDescription;
	private final Text[] authors;
	private final @Nullable Identifier modIconId;
	private final @Nullable String modHomePageUrl, modSourcesUrl;
	// ==================================================
	public FabricModInfo(String modId) throws NullPointerException, NoSuchElementException
	{
		super(modId);
		this.modContainer = FabricLoader.getInstance().getModContainer(modId).get();
		
		final var meta = this.modContainer.getMetadata();
		this.modVersion = meta.getVersion().toString();
		this.modName = fLiteral(meta.getName());
		this.modDescription = fLiteral(meta.getDescription());
		this.authors = meta.getAuthors().stream().map(person -> fLiteral(person.getName())).toArray(Text[]::new);
		
		this.modIconId = new Identifier(modId, "icon.png"); //FIXME - Obtain mod icon path properly
		
		final var contact = meta.getContact();
		this.modHomePageUrl = contact.get("homepage").orElse(null);
		this.modSourcesUrl = contact.get("sources").orElse(null);
	}
	// ==================================================
	public final @Override String getVersion() { return this.modVersion; }
	public final @Override Text getName() { return this.modName; }
	public final @Override Text getDescription() { return this.modDescription; }
	public final @Override Text[] getAuthors() { return this.authors; }
	public final @Override @Nullable Identifier getIconId() { return this.modIconId; }
	public final @Override @Nullable String getHomePageURL() { return this.modHomePageUrl; }
	public final @Override @Nullable String getSourcesURL() { return this.modSourcesUrl; }
	// ==================================================
}