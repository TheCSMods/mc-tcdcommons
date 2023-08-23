package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl;

import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.NavigationBar.NavigationBarProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.FileExplorerPanelProxy;

final @Internal class FEPNavigationBarProxy implements NavigationBarProxy
{
	// ==================================================
	protected final TFileExplorerPanel explorer;
	protected final FileExplorerPanelProxy proxy;
	// ==================================================
	protected FEPNavigationBarProxy(TFileExplorerPanel explorer)
	{
		this.explorer = Objects.requireNonNull(explorer);
		this.proxy = Objects.requireNonNull(explorer.proxy);
	}
	// ==================================================
	public final @Override Path getCurrentPath() { return this.proxy.getCurrentDirectory(); }
	// --------------------------------------------------
	public final @Override boolean canRefresh() { return true; }
	public final @Override void onRefresh() { this.explorer.refresh(); }
	// ==================================================
}