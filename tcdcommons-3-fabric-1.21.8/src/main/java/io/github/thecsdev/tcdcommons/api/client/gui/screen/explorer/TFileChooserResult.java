package io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer;

import java.io.File;

import javax.swing.JFileChooser;

import org.jetbrains.annotations.Nullable;

public interface TFileChooserResult
{
	public ReturnValue getReturnValue();
	public @Nullable File getSelectedFile();
	
	public static enum ReturnValue
	{
		APPROVE_OPTION(JFileChooser.APPROVE_OPTION),
		CANCEL_OPTION(JFileChooser.CANCEL_OPTION),
		ERROR_OPTION(JFileChooser.ERROR_OPTION);
		
		private final int value;
		ReturnValue(int value) { this.value = value; }
		public final int getValue() { return this.value; }
	}
}