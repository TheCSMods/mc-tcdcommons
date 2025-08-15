package io.github.thecsdev.tcdcommons.api.util.interfaces;

import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;

/**
 * An {@link FunctionalInterface} similar to the {@link FileFilter} one,
 * but it also offers {@link #getDescription()} that returns {@link Component}.
 */
@FunctionalInterface
public interface TFileFilter extends FileFilter
{
	/**
	 * Returns a user-friendly descriptive {@link Component} for
	 * this {@link TFileFilter}, describing the files it filters.
	 * <p>
	 * Example: "Image files (.png, .jpg, ...)"
	 */
	default @Nullable Component getDescription() { return null; }
	
	/**
	 * A {@link TFileFilter} that does not filter out any files.<p>
	 * Returns {@code true} if the {@link File} being tested is not {@code null}.
	 */
	public static final TFileFilter ALL_FILES = new TFileFilter()
	{
		public final @Override boolean accept(File pathname) { return pathname != null; }
		public final @Nullable @Override Component getDescription() { return TCDCT.gui_explorer_fileFilter_allFiles(); }
	};
}