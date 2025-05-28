package io.github.thecsdev.tcdcommons.api.util.exceptions;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Thrown when reading a {@link File}'s data, and the data
 * cannot be parsed because it's "version" is unsupported.
 */
public final class UnsupportedFileVersionException extends IOException
{
	// ==================================================
	private static final long serialVersionUID = 1704594052564951970L;
	private final String fileVersion;
	// ==================================================
	public UnsupportedFileVersionException(String fileVersion)
	{
		super(String.format("Failed to read file data. Unsupported file version: %s.", fileVersion));
		this.fileVersion = fileVersion;
	}
	// ==================================================
	/**
	 * Returns a {@link String} representation of the {@link File} data version.
	 */
	public String getFileVersion() { return this.fileVersion; }
	// ==================================================
	public final @Override int hashCode() { return Objects.hash(this.fileVersion); }
	public final @Override boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		UnsupportedFileVersionException other = (UnsupportedFileVersionException) obj;
		return Objects.equals(this.fileVersion, other.fileVersion);
	}
	// ==================================================
}