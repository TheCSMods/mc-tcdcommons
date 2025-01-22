package io.github.thecsdev.tcdcommons.api.util.enumerations;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.text.Text;

public enum FileChooserDialogType
{
	OPEN_FILE(TCDCT.gui_explorer_title_open()),
	SAVE_FILE(TCDCT.gui_explorer_title_save()),
	SELECT_DIRECTORY(TCDCT.gui_explorer_title_selDir());
	
	private final Text dialogTitle;
	FileChooserDialogType(Text dialogTitle) { this.dialogTitle = Objects.requireNonNull(dialogTitle); }
	
	/**
	 * Returns the default dialog title {@link Text} that corresponds
	 * to a given {@link FileChooserDialogType} by default.
	 */
	public Text getDialogTitle() { return this.dialogTitle; }
}