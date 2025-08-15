package io.github.thecsdev.tcdcommons.api.util.io.mod;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A "wrapper" utility {@link Class} providing information about a given mod.
 */
public abstract class ModInfo
{
	// ==================================================
	private final String modId;
	// ==================================================
	public ModInfo(String modId) throws NullPointerException, NoSuchElementException
	{
		this.modId = Objects.requireNonNull(modId);
	}
	// --------------------------------------------------
	public @Override int hashCode() { return Objects.hash(this.modId); }
	public @Override boolean equals(Object obj)
	{
		if(obj == null) return false;
		else if(this == obj || ((obj instanceof ModInfo) && Objects.equals(((ModInfo)obj).modId, this.modId)))
			return true;
		return false;
	}
	// ==================================================
	/**
	 * Returns the unique ID of the mod this {@link ModInfo}
	 * {@link Objects} is associated with.
	 */
	public final String getId() { return this.modId; }
	// --------------------------------------------------
	public abstract String getVersion();
	public abstract @Nullable Component getName();
	public abstract @Nullable Component getDescription();
	public abstract @Nullable Component[] getAuthors();
	public abstract @Nullable ResourceLocation getIconId();
	public abstract @Nullable String getHomePageURL();
	public abstract @Nullable String getSourcesURL();
	// ==================================================
}