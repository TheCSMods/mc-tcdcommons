package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.AbstractFileListPanel.FileListPanelProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.FileExplorerPanelProxy;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;

final @Internal class FileListPanelProxyImpl implements FileListPanelProxy
{
	// ==================================================
	protected final TFileExplorerPanel explorer;
	protected final FileExplorerPanelProxy proxy;
	// ==================================================
	protected FileListPanelProxyImpl(TFileExplorerPanel explorer)
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
		try
		{
			final File file = selectedPath.toFile();
			switch(this.explorer.type)
			{
				//for "select directory", only handle directory clicks, and ignore file clicks
				case SELECT_DIRECTORY: if(file.isDirectory()) { navigateToPath(selectedPath); } break;
				
				//for all other types, handle things differently:
				//- directory clicks trigger navigation
				//- file clicks trigger potential selection
				default:
					//if the file is a directory, navigate and break...
					if(file.isDirectory()) { navigateToPath(selectedPath); break; }
					
					//...else handle file clicks
					final var fileName = file.getName();
					final var nameInput = this.explorer.actionBar.getFileNameInput();
					
					if(!Objects.equals(nameInput.getInput(), fileName))
						nameInput.setInput(fileName); //this makes the user have to "double click" to select a file
					else this.explorer.completeAsApprove(file);
			}
		}
		catch(SecurityException se) {/*do nothing ig...*/}
	}
	// ==================================================
	private final void navigateToPath(Path selectedPath)
	{
		this.proxy.setCurrentDirectory(selectedPath);
		this.explorer.refresh();
	}
	// ==================================================
}