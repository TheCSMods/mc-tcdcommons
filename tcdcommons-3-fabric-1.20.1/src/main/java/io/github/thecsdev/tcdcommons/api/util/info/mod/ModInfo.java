package io.github.thecsdev.tcdcommons.api.util.info.mod;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
	public abstract @Nullable Text getName();
	public abstract @Nullable Text getDescription();
	public abstract @Nullable Text[] getAuthors();
	public abstract @Nullable Identifier getIconId();
	public abstract @Nullable String getHomePageURL();
	public abstract @Nullable String getSourcesURL();
	// ==================================================
}