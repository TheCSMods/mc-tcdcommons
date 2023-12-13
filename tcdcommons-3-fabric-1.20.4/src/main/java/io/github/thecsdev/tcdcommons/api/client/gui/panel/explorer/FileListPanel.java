package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.item.FileListItem;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import net.minecraft.text.Text;

@ApiStatus.Experimental
public @Virtual class FileListPanel extends AbstractFileListPanel<AbstractFileListPanel.FileListPanelProxy>
{
	// ==================================================
	/**
	 * A {@link FileFilter} that filters out "hidden" root files.
	 * @apiNote It is recommended to use this to filter out critical files and void potential harm.
	 */
	protected @Internal static final FileFilter FF_HIDDEN_ROOT_FILES = new FileFilter()
	{
		public final @Override boolean accept(File pathname)
		{
			//null check, just in case
			if(pathname == null) return false;
			
			//perform the filtering
			try
			{
				final var parent = pathname.getParentFile();
				if(parent != null && parent.getParent() == null && pathname.isHidden())
					return false;
			}
			catch(SecurityException se) { return false; /*forbidden files don't pass*/ }
			
			//by default, the file passes
			return true;
		}
	};
	// --------------------------------------------------
	protected @Nullable TFileFilter fileFilter;
	// ==================================================
	public FileListPanel(int x, int y, int width, int height, FileListPanelProxy proxy) { super(x, y, width, height, proxy); }
	// ==================================================
	protected @Virtual @Override void init() throws NullPointerException
	{
		//obtain the current path
		final Path currPath = this.proxy.getCurrentPath();
		if(currPath == null) return; //can't deal with null, but also don't crash
		final var currentPathParent = currPath.getParent();
		
		//obtain the file filter from the proxy
		this.fileFilter = this.proxy.getCurrentFileFilter();
		
		//put everything in a try-catch statement, to avoid crashes
		try
		{
			//handle path not existing
			if(!Files.exists(currPath))
			{
				//add an option to navigate back
				if(currentPathParent != null)
					addGoBackFileListItem(currentPathParent.toFile());
				//add a text indicating "no such directory"
				addCenteredLabel(translatable("tcdcommons.api.client.gui.panel.explorer.filelistpanel.err_nosuchdir"));
				return;
			}
			
			//obtain path as file
			final File currFile = Files.isDirectory(currPath) ?
					currPath.toFile() :
					currPath.getParent().toFile();
			
			//add a "go back" list entry, allowing the user to navigate to the previous folder
			final var currFileParent = currFile.getParentFile();
			if(currFileParent != null) addGoBackFileListItem(currFileParent);
			
			//iterate all child directories, and list them
			for(final File childDir : currFile.listFiles(File::isDirectory))
				addFileListItem(childDir);
			
			//iterate all child non-directory files, and list them
			for(final File childDir : currFile.listFiles(File::isFile))
				addFileListItem(childDir);
		}
		catch(SecurityException se)
		{
			//add an option to navigate back
			if(currentPathParent != null)
				addGoBackFileListItem(currentPathParent.toFile());
			//add a text indicating "no such directory"
			addCenteredLabel(translatable("tcdcommons.api.client.gui.panel.explorer.filelistpanel.err_noaccess"));
			return;
		}
	}
	// --------------------------------------------------
	public final boolean testFileFilter(@Nullable File file)
	{
		return !(!FF_HIDDEN_ROOT_FILES.accept(file) || (this.fileFilter != null && !this.fileFilter.accept(file)));
	}
	// --------------------------------------------------
	protected @Nullable @Override FileListItem addFileListItem(@Nullable File file)
	{
		//test the file against the file filters
		if(!testFileFilter(file)) return null;
		//if the file passes, add it
		return super.addFileListItem(file);
	}
	
	protected @Virtual FileListItem addGoBackFileListItem(final File parentDir) throws NullPointerException
	{ 
		final var goBack = addFileListItem(Objects.requireNonNull(parentDir));
		goBack.setIcon(FileListItem.TEX_DIR_ALT);
		goBack.setText(literal("..."));
		return goBack;
	}
	
	/**
	 * Creates and adds a {@link TLabelElement} with a given {@link Text}, and then returns it.
	 * @apiNote The return type of this method may change in the future, hence {@link TElement} being returned.
	 */
	@ApiStatus.Experimental
	protected @Virtual TElement addCenteredLabel(@Nullable Text text)
	{
		final var lbl = new TLabelElement(
				this.scrollPadding, (getHeight() / 2) - 10,
				getWidth() - (this.scrollPadding * 2), 20,
				text);
		lbl.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
		addChild(lbl, true);
		return lbl;
	}
	// ==================================================
}