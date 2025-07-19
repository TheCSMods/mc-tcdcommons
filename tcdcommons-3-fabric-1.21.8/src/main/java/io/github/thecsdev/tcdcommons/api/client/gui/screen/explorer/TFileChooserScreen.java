package io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MAGIC_ITEM_Z_OFFSET;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreenPlus;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserResult.ReturnValue;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext.InputType;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.util.enumerations.FileChooserDialogType;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import io.github.thecsdev.tcdcommons.api.util.io.TExtensionFileFilter;
import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

/**
 * A file chooser screen that allows the user to choose a
 * {@link File} for opening and saving.
 * @see TFileChooserBuilder
 * @see TFileChooserScreen#builder()
 */
public final class TFileChooserScreen extends TScreenPlus implements IParentScreenProvider
{
	// ==================================================
	protected final FileChooserDialogType type;
	protected final ArrayList<TFileFilter> filters = new ArrayList<>();
	protected final @Nullable String targetExtension; //highly important for "save", so the dialog can know what to save as
	
	protected final Consumer<TFileChooserResult> onComplete;
	private boolean isDone = false;
	//
	protected Path currentPath;
	protected @Nullable TFileFilter currentFileFilter;
	//
	protected @Nullable File selectedFile;
	//
	protected boolean fullScreen = false;
	protected boolean explorerPanelDraggable;
	private boolean closedUsingClose = false; //closed using `close()`?
	// --------------------------------------------------
	protected @Nullable Screen parent;
	protected final TElement contentPane;
	protected final TFileExplorerPanel explorerPanel;
	// ==================================================
	protected TFileChooserScreen(
			FileChooserDialogType type,
			Path startingPath,
			@Nullable String targetExtension,
			Consumer<TFileChooserResult> onComplete) throws NullPointerException
	{
		super(TCDCT.gui_explorer_title());
		this.type = Objects.requireNonNull(type);
		this.onComplete = Objects.requireNonNull(onComplete);
		
		if(targetExtension != null)
		{
			TExtensionFileFilter ff = null;
			addFileFilter(ff = new TExtensionFileFilter(targetExtension));
			targetExtension = ff.getFileExtension(); //sanitize the extension String
		}
		this.targetExtension = targetExtension;
		
		this.currentPath = Objects.requireNonNull(startingPath);
		
		this.parent = MC_CLIENT.currentScreen;
		this.contentPane = new TFillColorElement(0, 0, 100, 100, /*-1771805596*/436207615)
		{
			public boolean isHoverable() { return true; }
			public boolean input(TInputContext inputContext)
			{
				//only handle hovered and mouse LMB clicks
				if(!isHovered() || inputContext.getInputType() != InputType.MOUSE_PRESS || inputContext.getMouseButton() != 0)
					return false;
				//play a "pling" sound to let the user know they can't click "out of bounds"
				MC_CLIENT.getSoundManager().play(
						PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING,
						2));
				return true;
			}
		};
		this.contentPane.setZOffset(MAGIC_ITEM_Z_OFFSET);
		this.explorerPanel = new TFileExplorerPanel(0, 0, 100, 100, new FEPProxy(this));
		this.contentPane.addChild(this.explorerPanel, false); //sub-children need to be added here
	}
	// --------------------------------------------------
	public static TFileChooserBuilder builder() { return new TFileChooserBuilder(); }
	//
	public static void showOpenFileDialog(Consumer<TFileChooserResult> onComplete) { showOpenFileDialog(null, onComplete); }
	public static void showOpenFileDialog(@Nullable String targetExtension, Consumer<TFileChooserResult> onComplete) { showDialog(FileChooserDialogType.OPEN_FILE, targetExtension, onComplete); }
	
	public static void showSaveFileDialog(Consumer<TFileChooserResult> onComplete) { showSaveFileDialog(null, onComplete); }
	public static void showSaveFileDialog(@Nullable String targetExtension, Consumer<TFileChooserResult> onComplete) { showDialog(FileChooserDialogType.SAVE_FILE, targetExtension, onComplete); }
	
	public static void showSelectDirectoryDialog(Consumer<TFileChooserResult> onComplete) { showDialog(FileChooserDialogType.SELECT_DIRECTORY, null, onComplete); }
	
	protected static void showDialog(FileChooserDialogType type, @Nullable String targetExtension, Consumer<TFileChooserResult> onComplete)
	{
		builder().showDialog(type, targetExtension, onComplete);
	}
	// ==================================================
	public final @Override void close() { closedUsingClose = true; MC_CLIENT.setScreen(this.parent); }
	public final @Override boolean shouldPause() { return true; }
	public final @Override boolean shouldCloseOnEsc() { return true; }
	public final @Override boolean shouldRenderInGameHud() { return false; }
	// --------------------------------------------------
	public final @Override void renderBackground(TDrawContext pencil)
	{
		if(this.parent != null) this.parent.render(pencil, pencil.mouseX, pencil.mouseY, pencil.deltaTime);
		else super.renderBackground(pencil);
	}
	// --------------------------------------------------
	protected final @Override void onClosed()
	{
		//make sure the promise wasn't already completed before
		if(this.isDone) return;
		this.isDone = true;
		
		//complete the promise
		//this has to be done on the main thread, as this place is not allowed to raise potential exceptions
		MC_CLIENT.executeSync(() ->
		{
			if(this.onComplete == null) return; //fail-safe
			final var sel = this.getSelectedFile();
			final var ret = this.closedUsingClose ?
					(sel == null ? ReturnValue.CANCEL_OPTION : ReturnValue.APPROVE_OPTION) :
					ReturnValue.ERROR_OPTION;
			this.onComplete.accept(new TFileChooserResult()
			{
				public final @Nullable @Override File getSelectedFile() { return sel; }
				public final @Override ReturnValue getReturnValue() { return ret; }
			});
		});
	}
	// ==================================================
	public final @Nullable @Override Screen getParentScreen() { return this.parent; }
	public final void setParentScreen(@Nullable Screen parent) { this.parent = parent; }
	// --------------------------------------------------
	/**
	 * Clears all {@link TFileFilter}s associated with this {@link TFileChooserScreen},
	 * and adds the given {@link TFileFilter}.
	 * @param fileFilter The {@link TFileFilter} to set as the sole filter for this {@link TFileChooserScreen}.
	 * @see #addFileFilter(TFileFilter)
	 */
	@Experimental
	protected final void setFileFilter(TFileFilter fileFilter) throws NullPointerException
	{
		Objects.requireNonNull(fileFilter);
		this.filters.clear();
		this.filters.trimToSize();
		this.filters.add(fileFilter);
		this.currentFileFilter = fileFilter;
	}
	
	/**
	 * Adds a {@link TFileFilter} to the list of {@link TFileFilter}s associated with
	 * this {@link TFileChooserScreen}.
	 * @param fileFilter The {@link TFileFilter} to add.
	 * @see #setFileFilter(TFileFilter)
	 */
	@Experimental
	protected final void addFileFilter(TFileFilter fileFilter) throws NullPointerException
	{
		if(this.filters.size() == 0)
			this.currentFileFilter = fileFilter;
		this.filters.add(Objects.requireNonNull(fileFilter));
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link File} that was eventually selected by the user, if any.
	 * @apiNote Can represent both a file and a directory.
	 * @apiNote Will be null if the user didn't select a file, or the operation was cancelled.
	 */
	@Experimental
	protected final @Nullable File getSelectedFile() { return this.selectedFile; }
	// ==================================================
	public final void refresh() { clearChildren(); init(); }
	// --------------------------------------------------
	protected final @Override void init()
	{
		//if the promise was completed, this screen is unusable
		if(this.isDone) return;
		
		//handle no file filters having been added by this point
		if(this.filters.size() == 0 && this.currentFileFilter == null)
			addFileFilter(TFileFilter.ALL_FILES);
		
		//content-pane
		addChild(this.contentPane, false);
		this.contentPane.setPosition(0, 0, false);
		this.contentPane.setSize(getWidth(), getHeight());
		
		//FCPanel
		if(this.fullScreen)
		{
			this.explorerPanelDraggable = false;
			this.explorerPanel.setPosition(0, 0, true);
			this.explorerPanel.setSize(getWidth(), getHeight());
		}
		else
		{
			final int w8 = getWidth() / 8, h8 = getHeight() / 8;
			final int w = getWidth() - w8, h = getHeight() - h8;
			this.explorerPanelDraggable = true;
			this.explorerPanel.setPosition(w8 / 2, h8 / 2, true);
			this.explorerPanel.setSize(w, h);
		}
		this.explorerPanel.refresh();
	}
	// ==================================================
}