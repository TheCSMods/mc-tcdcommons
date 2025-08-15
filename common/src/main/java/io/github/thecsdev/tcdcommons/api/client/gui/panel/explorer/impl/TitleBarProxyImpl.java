package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.TitleBar.TitleBarProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.FileExplorerPanelProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final @Internal class TitleBarProxyImpl implements TitleBarProxy
{
	// ==================================================
	protected final TFileExplorerPanel explorer;
	protected final FileExplorerPanelProxy proxy;
	// ==================================================
	protected TitleBarProxyImpl(TFileExplorerPanel explorer)
	{
		this.explorer = Objects.requireNonNull(explorer);
		this.proxy = Objects.requireNonNull(explorer.proxy);
	}
	// ==================================================
	public final @Nullable @Override Component getTitle() { return proxy.getTitle(); }
	public final @Nullable @Override UITexture getIcon() { return proxy.getIcon(); }
	// --------------------------------------------------
	public final @Override boolean canClose() { return proxy.canClose(); }
	public final @Override boolean canRestore() { return proxy.canRestore(); }
	public final @Override boolean canMinimize() { return proxy.canMinimize(); }
	// --------------------------------------------------
	public final @Override void onClose()
	{
		explorer.actionBar.getCancelButton().click(false);
		proxy.onClose();
	}
	public final @Override void onRestore() { proxy.onRestore(); }
	public final @Override void onMinimize() { proxy.onMinimize(); }
	// ==================================================
}