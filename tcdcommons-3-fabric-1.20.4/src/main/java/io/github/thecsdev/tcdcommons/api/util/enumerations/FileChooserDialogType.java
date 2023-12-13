package io.github.thecsdev.tcdcommons.api.util.enumerations;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.util.Objects;

import net.minecraft.text.Text;

public enum FileChooserDialogType
{
	OPEN_FILE(translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_open")),
	SAVE_FILE(translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_save")),
	SELECT_DIRECTORY(translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_seldir"));
	
	private final Text dialogTitle;
	FileChooserDialogType(Text dialogTitle) { this.dialogTitle = Objects.requireNonNull(dialogTitle); }
	
	/**
	 * Returns the default dialog title {@link Text} that corresponds
	 * to a given {@link FileChooserDialogType} by default.
	 */
	public Text getDialogTitle() { return this.dialogTitle; }
}