package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.item.FileListItem;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserScreen;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a {@link TRefreshablePanelElement} that visually
 * lists {@link File}s in a given directory.
 */
@ApiStatus.Experimental
public abstract class AbstractFileListPanel<P extends AbstractFileListPanel.FileListPanelProxy> extends TRefreshablePanelElement
{
	// ==================================================
	protected final P proxy;
	// ==================================================
	public AbstractFileListPanel(int x, int y, int width, int height, P proxy)
	{
		super(x, y, width, height);
		this.scrollFlags = SCROLL_VERTICAL;
		this.scrollPadding = 5;
		this.backgroundColor = -13816531;
		this.outlineColor = 0;
		
		this.proxy = Objects.requireNonNull(proxy);
	}
	// ==================================================
	/**
	 * Returns the {@link FileListPanelProxy} associated
	 * with this {@link AbstractFileListPanel}.
	 */
	public final P getProxy() { return this.proxy; }
	// ==================================================
	/**
	 * Calculates and returns the "width" that should be applied to the next
	 * "vertical list item" element that will be added to this {@link AbstractFileListPanel}.
	 */
	protected final int nextVerticalItemW() { return getWidth() - (getScrollPadding() * 2); }
	
	/**
	 * Calculates and returns the "X" coordinate that should be applied to the next
	 * "vertical list item" element that will be added to this {@link AbstractFileListPanel}.
	 */
	protected final int nextVerticalItemX() { return getX() + getScrollPadding(); }
	
	/**
	 * Calculates and returns the "Y" coordinate that should be applied to the next
	 * "vertical list item" element that will be added to this {@link AbstractFileListPanel}.
	 */
	protected final int nextVerticalItemY()
	{
		@SuppressWarnings("removal")
		final var b = getChildren().getTopmostElements().Item2;
		if(b != null) return b.getEndY() + 3;
		else return getY() + getScrollPadding();
	}
	// --------------------------------------------------
	/**
	 * Creates a new {@link FileListItem} that with a given {@link Component},
	 * adds it to this {@link AbstractFileListPanel}, and then returns it.
	 */
	protected @Virtual TLabelElement addSectionLabel(final Component text)
	{
		final var lbl = new TLabelElement(nextVerticalItemX(), nextVerticalItemY(), nextVerticalItemW(), 15, text);
		addChild(lbl, false);
		return lbl;
	}
	
	/**
	 * Creates a new {@link FileListItem} that is associated with a given
	 * {@link File}, adds it to this {@link AbstractFileListPanel}, and then returns it.
	 */
	protected @Virtual FileListItem addFileListItem(final @Nullable File file)
	{
		//create and add the item
		final var item = new FileListItem(nextVerticalItemX(), nextVerticalItemY(), nextVerticalItemW(), file);
		if(file != null)
			item.setOnClick(__ -> this.proxy.onPathSelected(file.toPath()));
		addChild(item, false);
		
		//return the item
		return item;
	}
	// ==================================================
	public static interface FileListPanelProxy
	{
		/**
		 * Returns the {@link Path} the file explorer this
		 * {@link AbstractFileListPanel} is associated with is currently displaying.
		 * <p>
		 * For example, if {@link TFileChooserScreen} is currently displaying
		 * a directory, and this {@link AbstractFileListPanel} is associated with it,
		 * then this method will return the {@link Path} of that directory being displayed.
		 */
		public Path getCurrentPath();
		
		/**
		 * Returns the {@link TFileFilter} that should currently apply to
		 * this {@link AbstractFileListPanel}.
		 */
		public TFileFilter getCurrentFileFilter();
		
		/**
		 * Invoked when the user clicks on a {@link File}
		 * featured on this {@link AbstractFileListPanel}.
		 */
		public void onPathSelected(Path selectedPath);
	}
	// ==================================================
}