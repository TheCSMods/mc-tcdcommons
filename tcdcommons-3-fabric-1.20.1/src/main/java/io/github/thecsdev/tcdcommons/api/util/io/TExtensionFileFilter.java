package io.github.thecsdev.tcdcommons.api.util.io;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import net.minecraft.text.Text;

/**
 * A {@link TFileFilter} that filters {@link File} by their extension name.
 */
public final class TExtensionFileFilter implements TFileFilter
{
	// ==================================================
	private final @Nullable String fileExtension;
	private final Text description;
	// ==================================================
	public TExtensionFileFilter(@Nullable String fileExtension)
	{
		//sanitize the argument
		fileExtension = sanitizeExtension(fileExtension);
		
		//define description
		this.description = translatable("tcdcommons.api.util.interfaces.tfilefilter.x_files", fileExtension);
		
		//define file extension (extensionless must be represented by null)
		this.fileExtension = (fileExtension.endsWith(".") ? null : fileExtension);
	}
	// ==================================================
	/**
	 * Returns the {@link File} extension this {@link TFileFilter} is filtering,
	 * or {@code null} if this {@link TFileFilter} targets passes {@link File} extensions.
	 * @apiNote Contains the period (aka the "." character).
	 */
	public final @Nullable String getFileExtension() { return this.fileExtension; }
	// --------------------------------------------------
	public final @Override Text getDescription() { return this.description; }
	// --------------------------------------------------
	public final @Override boolean accept(File pathname)
	{
		try
		{
			if(pathname == null)
				return false;
			else if(pathname.isDirectory() || this.fileExtension == null)
				return true;
			return pathname.getPath().endsWith(this.fileExtension);
		}
		catch(SecurityException se) { return false; }
	}
	// ==================================================
	/**
	 * Sanitizes a {@link String} that represents a {@link File} extension.
	 * @apiNote Makes the {@link String} start with "." as well.
	 * @apiNote {@code null} and blank {@link String}s will turn into ".".
	 */
	public static String sanitizeExtension(String fileExtension)
	{
		fileExtension = StringUtils.defaultString(fileExtension).replaceAll("[^a-zA-Z0-9.]", "");
		fileExtension = (fileExtension.startsWith(".") ? "" : ".") + fileExtension;
		return fileExtension.toLowerCase();
	}
	// ==================================================
}