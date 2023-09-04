package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public @Virtual class TSelectFileFilterWidget extends TSelectWidget<TSelectFileFilterWidget.FileFilterEntry>
{
	// ==================================================
	public TSelectFileFilterWidget(int x, int y, int width, int height, Iterable<TFileFilter> fileFilters)
	{
		//construct super
		super(x, y, width, height);
		
		//add file filter entries
		for(TFileFilter fileFilter : fileFilters)
			addEntry(new FileFilterEntry(fileFilter));
	}
	// ==================================================
	/**
	 * Returns a {@link FileFilterEntry} that is associated with a given {@link TFileFilter} value.
	 * Will return {@code null} if no such {@link FileFilterEntry} exists or if it was removed.
	 * @param fileFilter The {@link TFileFilter} value to look for in the {@link FileFilterEntry}s.
	 * @see FileFilterEntry#getFileFilter()
	 */
	public final FileFilterEntry entryOf(TFileFilter fileFilter) { return this.entries.find(e -> e.getFileFilter() == fileFilter); }
	// --------------------------------------------------
	/**
	 * Sets the selected {@link FileFilterEntry} using its {@link TFileFilter} value.
	 * @throws NoSuchElementException If this {@link TSelectFileFilterWidget} does not have
	 * a {@link FileFilterEntry} that corresponds with the given {@link TFileFilter} value.
	 * @see #entryOf(TFileFilter)
	 * @see FileFilterEntry#getFileFilter()
	 */
	public final void setSelected(TFileFilter fileFilter) throws NoSuchElementException
	{
		final var e = entryOf(fileFilter);
		if(e == null && fileFilter != null)
			throw new NoSuchElementException();
		setSelected(e);
	}
	// ==================================================
	public static final class FileFilterEntry implements TSelectWidget.Entry
	{
		protected final TFileFilter fileFilter;
		
		public FileFilterEntry(TFileFilter fileFilter) { this.fileFilter = Objects.requireNonNull(fileFilter); }
		public final TFileFilter getFileFilter() { return this.fileFilter; }
		
		public final @Override Text getText() { return this.fileFilter.getDescription(); }
		public final @Nullable @Override Tooltip getTooltip() { return null; }
		public final @Nullable @Override Runnable getOnSelect() { return null; }
		
		public final @Override int hashCode() { return this.fileFilter.hashCode(); }
		public final @Override boolean equals(Object obj)
		{
			//basic checks
			if(obj == this) return true;
			else if(obj == null || !Objects.equals(obj.getClass(), getClass())) return false;
			//compare entry file filters
			return Objects.equals(this.fileFilter, ((FileFilterEntry)obj).fileFilter);
		}
	}
	// ==================================================
}