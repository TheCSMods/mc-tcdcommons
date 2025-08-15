package io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.FileExplorerPanelProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer.TFileChooserResult.ReturnValue;
import io.github.thecsdev.tcdcommons.api.util.enumerations.FileChooserDialogType;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;

final @Internal class FEPProxy implements FileExplorerPanelProxy
{
	// ==================================================
	protected final TFileChooserScreen screen;
	// ==================================================
	protected FEPProxy(TFileChooserScreen screen) { this.screen = Objects.requireNonNull(screen); }
	// ==================================================
	public final @Override FileChooserDialogType getDialogType() { return this.screen.type; }
	// --------------------------------------------------
	public final @Override Iterable<TFileFilter> getFileFilters() { return this.screen.filters; }
	public final @Override TFileFilter getSelectedFileFilter() { return this.screen.currentFileFilter; }
	public final @Override void setSelectedFileFilter(TFileFilter fileFilter) { this.screen.currentFileFilter = fileFilter; }
	// --------------------------------------------------
	public final @Override Path getCurrentDirectory() { return this.screen.currentPath; }
	public final @Override void setCurrentDirectory(Path directory) { this.screen.currentPath = directory; }
	// --------------------------------------------------
	public final @Override @Nullable String getTargetFileExtension() { return this.screen.targetExtension; }
	// --------------------------------------------------
	public final @Override boolean canRestore() { return true; }
	public void onRestore()
	{
		this.screen.fullScreen = !this.screen.fullScreen;
		this.screen.refresh();
	}
	// --------------------------------------------------
	public final @Override boolean canDrag() { return this.screen.explorerPanelDraggable; }
	// ==================================================
	public final @Override void onComplete(TFileChooserResult result)
	{
		//if the selected file is null, the screen will treat that as "cancel"
		//if the selected file isn't null, the screen will treat that as "approve"
		this.screen.selectedFile = (result.getReturnValue() == ReturnValue.APPROVE_OPTION) ?
				result.getSelectedFile(): null;
		//closing the screen will make it "complete" the "promise"
		//the screen has to be closed via `close()`, as any other way would make the screen treat that as "error"
		this.screen.close();
	}
	// ==================================================
}