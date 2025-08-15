package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.Util;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Path;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

@ApiStatus.Experimental
public @Virtual class SidebarPanel extends AbstractFileListPanel<AbstractFileListPanel.FileListPanelProxy>
{
	// ==================================================
	public SidebarPanel(int x, int y, int width, int height, FileListPanelProxy proxy) { super(x, y, width, height, proxy); }
	// ==================================================
	protected @Virtual @Override void init()
	{
		try
		{
			//user section
			addSectionLabel(literal("*"));
			addFileListItem(new File(System.getProperty("user.home")));
			switch(Util.getPlatform())
			{
				case WINDOWS:
					addFileListItem(new File(System.getProperty("user.home") + "\\Desktop"));
					break;
				case LINUX:
				case OSX:
					// Assuming Desktop directory is usually at "user.home/Desktop" on Linux and MacOS
					addFileListItem(new File(System.getProperty("user.home") + "/Desktop"));
					break;
	
				case SOLARIS: break;
				case UNKNOWN:
				default: break;
			}
			
			//java environment section
			addSectionLabel(literal("*"));
			final Path userDir = Path.of(System.getProperty("user.dir"));
			addFileListItem(userDir.toFile());
			addFileListItem(userDir.resolve("config").toFile());
			//addFileListItem(userDir.resolve("mods").toFile());
			//addFileListItem(userDir.resolve("resourcepacks").toFile());
			addFileListItem(userDir.resolve("saves").toFile());
			
			//roots section
			addSectionLabel(literal("*"));
			for(final var rootFile : File.listRoots())
				addFileListItem(rootFile);
		}
		catch(SecurityException exc) {}
	}
	// ==================================================
}