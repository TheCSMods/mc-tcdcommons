package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl;

import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.AbstractFileListPanel.FileListPanelProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.FileExplorerPanelProxy;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;

final @Internal class SidebarPanelProxyImpl implements FileListPanelProxy
{
	// ==================================================
	protected final TFileExplorerPanel explorer;
	protected final FileExplorerPanelProxy proxy;
	// ==================================================
	protected SidebarPanelProxyImpl(TFileExplorerPanel explorer)
	{
		this.explorer = Objects.requireNonNull(explorer);
		this.proxy = Objects.requireNonNull(explorer.proxy);
	}
	// ==================================================
	public Path getCurrentPath() { return this.proxy.getCurrentDirectory(); }
	public TFileFilter getCurrentFileFilter() { return this.proxy.getSelectedFileFilter(); }
	// --------------------------------------------------
	public void onPathSelected(Path selectedPath)
	{
		this.proxy.setCurrentDirectory(selectedPath);
		this.explorer.refresh();
	}
	// ==================================================
}