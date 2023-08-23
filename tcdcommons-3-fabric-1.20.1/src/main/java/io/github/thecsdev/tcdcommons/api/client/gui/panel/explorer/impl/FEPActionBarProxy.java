package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl;

import static io.github.thecsdev.tcdcommons.api.util.io.TExtensionFileFilter.sanitizeExtension;
import static io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.TXT_ACTION_OPEN;
import static io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.TXT_ACTION_SAVE;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.ActionBar.ActionBarProxy;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.impl.TFileExplorerPanel.FileExplorerPanelProxy;
import io.github.thecsdev.tcdcommons.api.util.enumerations.FileChooserDialogType;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import net.minecraft.text.Text;

final @Internal class FEPActionBarProxy implements ActionBarProxy
{
	// ==================================================
	protected final TFileExplorerPanel explorer;
	protected final FileExplorerPanelProxy proxy;
	// ==================================================
	protected FEPActionBarProxy(TFileExplorerPanel explorer)
	{
		this.explorer = Objects.requireNonNull(explorer);
		this.proxy = Objects.requireNonNull(explorer.proxy);
	}
	// ==================================================
	public final @Override Iterable<TFileFilter> getFileFilters() { return proxy.getFileFilters(); }
	public final @Override TFileFilter getSelectedFileFilter() { return proxy.getSelectedFileFilter(); }
	public final @Override void onSelectFileFilter(TFileFilter selectedFileFilter)
	{
		this.proxy.setSelectedFileFilter(selectedFileFilter);
		this.explorer.fileListPanel.refresh(); //refreshing the explorer directly results in a StackOverflow
	}
	// --------------------------------------------------
	public final @Override Text getSubmitButtonText()
	{
		return (this.explorer.type == FileChooserDialogType.SAVE_FILE) ?
				TXT_ACTION_SAVE :
				TXT_ACTION_OPEN;
	}
	// --------------------------------------------------
	public final @Override void onCancel() { this.explorer.completeAsCancelled(); }
	public final @Override void onSubmit(SubmitContext submitContext)
	{
		switch(this.explorer.type)
		{
			case SELECT_DIRECTORY: onSubmit_selDir(submitContext); break;
			case OPEN_FILE: onSubmit_openFile(submitContext); break;
			case SAVE_FILE: onSubmit_saveFile(submitContext); break;
			default: this.explorer.completeAsError(); break;
		}
	}
	// ==================================================
	private final void onSubmit_selDir(SubmitContext submitContext)
	{
		//find the current directory
		final Path currentDirectory = this.explorer.getProxy().getCurrentDirectory();
		final String selFileName = submitContext.getSelectedFileName();
		
		//if the user did not type anything into the file-name input field,
		//approve the current directory as the selected directory
		if(selFileName.length() == 0)
			this.explorer.completeAsApprove(currentDirectory.toFile());
		
		//but if the user did type something in, account for that.
		//do so by navigating to the typed-in directory. if that fails,
		//let the user know that the input is "invalid" by focusing on it
		else try
		{
			final Path nextDir = currentDirectory.resolve(selFileName);
			if(Files.exists(nextDir))
			{
				this.explorer.proxy.setCurrentDirectory(nextDir);
				this.explorer.refresh();
			}
			else this.explorer.actionBar.focusOnFileNameInput();
		}
		catch(SecurityException se) { onSubmit_securityError(se); }
	}
	// --------------------------------------------------
	private final void onSubmit_openFile(SubmitContext submitContext)
	{
		//find the current directory
		final Path currentDirectory = this.explorer.getProxy().getCurrentDirectory();
		
		//if the user did not type anything into the file-name input field,
		//then the user has yet to make a choice. focus on the input field to let the user know that
		if(submitContext.getSelectedFileName().length() == 0)
			this.explorer.actionBar.focusOnFileNameInput();
		
		//but if the user did type a selection into the input field...
		else try
		{
			//obtain the selected file name, alongside the target extension if applicable
			String selFileName = submitContext.getSelectedFileName();
			String targetExtension = this.explorer.proxy.getTargetFileExtension();
			if(targetExtension != null)
			{
				//if there is a target extension, we must account for it,
				//and ensure the selected file name ends with that extension
				targetExtension = sanitizeExtension(targetExtension);
				if(!selFileName.endsWith(targetExtension))
					selFileName += targetExtension;
			}
			
			//next, we obtain the selected file instance using the
			//string that also accounts for the target extension
			final File nextFile = currentDirectory.resolve(selFileName).toFile();
			
			//if the file passes all filters, and exists, and is a file, complete as approve...
			final var ff = submitContext.getSelectedFileFilter();
			if((ff != null && ff.accept(nextFile)) && nextFile.exists() && nextFile.isFile())
				this.explorer.completeAsApprove(nextFile);
			//...but if not, focus on the input field, to let the user know the input is "invalid"
			else this.explorer.actionBar.focusOnFileNameInput();
		}
		catch(SecurityException se) { onSubmit_securityError(se); }
	}
	// --------------------------------------------------
	private final void onSubmit_saveFile(SubmitContext submitContext)
	{
		//find the current directory
		final Path currentDirectory = this.explorer.getProxy().getCurrentDirectory();
		
		//if the user did not type anything into the file-name input field,
		//then the user has yet to make a choice. focus on the input field to let the user know that
		if(submitContext.getSelectedFileName().length() == 0)
			this.explorer.actionBar.focusOnFileNameInput();
		
		//but if the user did type a selection into the input field...
		else
		{
			//obtain the selected file name, alongside the target extension if applicable
			String selFileName = submitContext.getSelectedFileName();
			String targetExtension = this.explorer.proxy.getTargetFileExtension();
			if(targetExtension != null)
			{
				//if there is a target extension, we must account for it,
				//and ensure the selected file name ends with that extension
				targetExtension = sanitizeExtension(targetExtension);
				if(!selFileName.endsWith(targetExtension))
					selFileName += targetExtension;
			}

			//next, we obtain the selected file instance using the
			//string that also accounts for the target extension
			final File nextFile = currentDirectory.resolve(selFileName).toFile();
			
			//complete as approve. ignore filters and possibility that the file exists (for now?)
			//TODO - implement a system to ask the user to confirm for files that already exist
			this.explorer.completeAsApprove(nextFile);
		}
	}
	// --------------------------------------------------
	private final void onSubmit_securityError(SecurityException se)
	{
		TCDCommons.LOGGER.error(FEPActionBarProxy.class.getSimpleName() + " encountered a " +
				SecurityException.class.getSimpleName());
		se.printStackTrace();
		this.explorer.completeAsError();
	}
	// ==================================================
}