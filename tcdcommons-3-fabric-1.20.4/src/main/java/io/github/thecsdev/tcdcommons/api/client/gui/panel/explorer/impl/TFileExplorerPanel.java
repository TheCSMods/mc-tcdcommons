package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.ActionBar;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.FileListPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.NavigationBar;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.SidebarPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.TitleBar;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.TitleBar.TitleBarProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.item.FileListItem;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserResult;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.client.gui.util.input.MouseDragHelper;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TScrollBarWidget;
import io.github.thecsdev.tcdcommons.api.util.enumerations.FileChooserDialogType;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import io.github.thecsdev.tcdcommons.api.util.io.TExtensionFileFilter;
import net.minecraft.text.Text;

/**
 * A {@link File} explorer panel that allows the user to browse their
 * local {@link File}s through the Minecraft GUI.
 * @see TFileChooserScreen
 * @apiNote Not a substitute or a replacement for {@link TFileChooserScreen}.
 */
public final class TFileExplorerPanel extends TRefreshablePanelElement
{
	// ==================================================
	/**
	 * The default scale at which the {@link TitleBarProxy#getTitle()} is rendered.
	 */
	public static final float TEXT_SCALE = 0.8f;
	
	public static final Text TXT_ACTION_SAVE = translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.save_file");
	public static final Text TXT_ACTION_OPEN = translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.open_file");
	// --------------------------------------------------
	protected final FileExplorerPanelProxy proxy;
	protected final FileChooserDialogType type;
	protected final String extension;
	//
	protected boolean userMadeAChoice = false;
	//
	protected @Nullable TitleBar titleBar;
	protected @Nullable NavigationBar navigationBar;
	protected @Nullable ActionBar actionBar;
	protected @Nullable SidebarPanel sidebarPanel;
	protected @Nullable FileListPanel fileListPanel;
	// --------------------------------------------------
	protected final MouseDragHelper dragHelper;
	// ==================================================
	public TFileExplorerPanel(int x, int y, int width, int height, FileExplorerPanelProxy proxy)
	{
		super(x, y, Math.max(width, 150), Math.max(height, 150));
		this.scrollPadding = 0;
		this.scrollFlags = 0;
		this.outlineColor = /*Color.GRAY.getRGB()*/-8355712;
		
		this.proxy = Objects.requireNonNull(proxy);
		this.type = proxy.getDialogType();
		this.extension = TExtensionFileFilter.sanitizeExtension(proxy.getTargetFileExtension());

		this.dragHelper = new MouseDragHelper()
		{
			protected final @Override void apply(int deltaX, int deltaY) { move(deltaX, deltaY); }
		};
	}
	// ==================================================
	/**
	 * Returns the {@link FileExplorerPanelProxy}
	 * associated with this {@link TFileExplorerPanel}.
	 */
	public final FileExplorerPanelProxy getProxy() { return this.proxy; }
	// ==================================================
	//reject any inputs send over to this panel, as this panel won't need them
	protected final boolean TRefreshablePanelElement_super_input(TInputContext inputContext) { return super.input(inputContext); }
	public final @Override boolean input(TInputContext inputContext)
	{
		switch(inputContext.getInputType())
		{
			//return true on mouse presses so as to allow dragging
			case MOUSE_PRESS: return this.proxy.canDrag();
			//for mouse drag, forward the drag to the drag helper if draggable
			case MOUSE_DRAG:
				if(!this.proxy.canDrag()) break;
				return this.dragHelper.onMouseDrag(inputContext.getMouseDelta());
			//once the dragging ends, snap to parent bounds, so the panel cannot be dragged out of bounds
			case MOUSE_DRAG_END:
				MouseDragHelper.snapToParentBounds(TFileExplorerPanel.this);
				return true;
			default: break;
		}
		
		//return false by default
		return false;
	}
	// --------------------------------------------------
	//ensure the visual text scale is appropriate from within this panel
	//(push before rendering, and pop after post-rendering)
	public final @Override void render(TDrawContext pencil) { pencil.pushTTextScale(TEXT_SCALE); super.render(pencil); }
	public final @Override void postRender(TDrawContext pencil) { super.postRender(pencil); pencil.popTTextScale(); }
	// --------------------------------------------------
	protected final void completeAsError() { complete(TFileChooserResult.ReturnValue.ERROR_OPTION, null); }
	protected final void completeAsCancelled() { complete(TFileChooserResult.ReturnValue.CANCEL_OPTION, null); }
	protected final void completeAsApprove(final File selectedFile) { complete(TFileChooserResult.ReturnValue.APPROVE_OPTION, selectedFile); }
	protected final void complete(final TFileChooserResult.ReturnValue returnValue, final File selectedFile)
	{
		complete(new TFileChooserResult()
		{
			public final @Override ReturnValue getReturnValue() { return returnValue; }
			public final @Override @Nullable File getSelectedFile() { return selectedFile; }
		});
	}
	protected final void complete(final TFileChooserResult result)
	{
		//remove from parent
		if(getParent() != null)
			getParent().removeChild(this);
		
		//track the "complete" flag
		if(this.userMadeAChoice) return;
		this.userMadeAChoice = true;
		
		//call proxy method
		this.proxy.onComplete(result);
	}
	// --------------------------------------------------
	public final @Override void init()
	{
		//do not initialize if completed
		if(this.userMadeAChoice) return;
		
		//initialize components
		this.titleBar = new TitleBar(getX(), getY(), getWidth(), new TitleBarProxyImpl(this));
		this.navigationBar = new NavigationBar(getX(), this.titleBar.getEndY(), getWidth(), new NavigationBarProxyImpl(this));
		this.actionBar = new ActionBar(getX(), getEndY() - ActionBar.HEIGHT, getWidth(), new ActionBarProxyImpl(this));
		
		final int panelY = this.navigationBar.getEndY();
		final int panelH = this.actionBar.getY() - panelY;
		final int sidePanelW = getWidth() / 3, listPanelW = getWidth() - sidePanelW;
		
		this.sidebarPanel = new SidebarPanel(getX(), panelY, sidePanelW - 10, panelH, new SidebarPanelProxyImpl(this));
		this.fileListPanel = new FileListPanel(getX() + sidePanelW, panelY, listPanelW - 10, panelH, new FileListPanelProxyImpl(this));
		
		//add components
		addChild(this.titleBar, false);
		addChild(this.navigationBar, false);
		addChild(this.actionBar, false);
		addChild(this.sidebarPanel, false);
		addChild(this.fileListPanel, false);
		
		addChild(new TScrollBarWidget(this.sidebarPanel.getEndX(), this.sidebarPanel.getY(), 10, this.sidebarPanel.getHeight(), this.sidebarPanel), false);
		addChild(new TScrollBarWidget(this.fileListPanel.getEndX(), this.fileListPanel.getY(), 10, this.fileListPanel.getHeight(), this.fileListPanel), false);
	}
	// ==================================================
	/**
	 * This {@code interface} defines the "current state" a {@link TFileExplorerPanel}
	 * is in at a given moment. Because changes to the game's window and GUI components
	 * triggers a GUI "reset" for technical reasons, this {@code interface} is responsible
	 * for properly preserving and communicating the state of the {@link TFileExplorerPanel}
	 * to the said panel.
	 */
	public static interface FileExplorerPanelProxy extends TitleBar.TitleBarProxy
	{
		// ----------------------------------------------
		/**
		 * Returns the {@link FileChooserDialogType} associated
		 * with the corresponding {@link TFileExplorerPanel}.
		 * @apiNote Must not return {@code null}. Must be a constant value. Must not change.
		 */
		public FileChooserDialogType getDialogType();
		
		/**
		 * A method that is called once the user makes a choice in
		 * the {@link TFileExplorerPanel}.
		 * @apiNote This panel being closed programmatically or removed
		 * from its parent element does not count as a "user choice / completion".
		 */
		public void onComplete(TFileChooserResult result);
		
		public Iterable<TFileFilter> getFileFilters();
		public TFileFilter getSelectedFileFilter();
		public void setSelectedFileFilter(TFileFilter fileFilter);
		
		/**
		 * Returns the {@link Path} of a directory that should currently
		 * be displayed by the {@link TFileExplorerPanel}.
		 */
		public Path getCurrentDirectory();
		
		/**
		 * Called by {@link TFileExplorerPanel} to indicate user directory navigation.
		 * @apiNote Do not call {@link TFileExplorerPanel#refresh()} here. It'll be done automatically.
		 */
		public void setCurrentDirectory(Path directory);
		
		/**
		 * Returns the {@link File} extension that should be used when "saving"
		 * files. Primarily used for {@link FileChooserDialogType#SAVE_FILE},
		 * as the dialog <b>has to</b> somehow know what extension you wish to "save as".
		 * @apiNote Returning {@code null} will allow the user to pick any extension themselves.
		 */
		public @Nullable String getTargetFileExtension();
		
		/**
		 * Returns true if the {@link TFileExplorerPanel} may be dragged by the
		 * user's mouse by clicking and dragging the {@link TitleBar}.
		 * @see MouseDragHelper#snapToParentBounds(TElement)
		 */
		default boolean canDrag() { return false; }
		// ----------------------------------------------
		/**
		 * Returns the title {@link Text} that should be used for this
		 * {@link TFileExplorerPanel} dialog.
		 * @see FileChooserDialogType#getDialogTitle()
		 */
		default @Override Text getTitle() { return getDialogType().getDialogTitle(); }
		
		/**
		 * Returns the {@link TitleBar} icon for this {@link TFileExplorerPanel}.
		 */
		default @Override UITexture getIcon() { return FileListItem.TEX_DIR; }
		
		default @Override boolean canClose() { return true; }
		default @Override boolean canRestore() { return true; }
		default @Override boolean canMinimize() { return false; }
		// ----------------------------------------------
	}
	// ==================================================
}