package io.github.thecsdev.tcdcommons.api.util.enumerations;

import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public enum FileChooserDialogType
{
	OPEN_FILE(TCDCT.gui_explorer_title_open()),
	SAVE_FILE(TCDCT.gui_explorer_title_save()),
	SELECT_DIRECTORY(TCDCT.gui_explorer_title_selDir());
	
	private final Component dialogTitle;
	FileChooserDialogType(Component dialogTitle) { this.dialogTitle = Objects.requireNonNull(dialogTitle); }
	
	/**
	 * Returns the default dialog title {@link Component} that corresponds
	 * to a given {@link FileChooserDialogType} by default.
	 */
	public Component getDialogTitle() { return this.dialogTitle; }
}