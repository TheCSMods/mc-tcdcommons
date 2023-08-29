	package io.github.thecsdev.tcdcommons.api.client.gui.screen.explorer;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.enumerations.FileChooserDialogType;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

/**
 * Sometimes the {@link TFileChooserScreen#showDialog(FileChooserDialogType, String)}
 * options are too limited, and you need more control over how the
 * {@link TFileChooserScreen} will behave. This is where {@link TFileChooserBuilder} comes in.
 * @see TFileChooserScreen#builder()
 */
public final class TFileChooserBuilder extends Object
{
	// ==================================================
	private final ArrayList<TFileFilter> filters = new ArrayList<>();
	// --------------------------------------------------
	private @Nullable Path startingPath;
	private @Nullable Screen parent;
	// ==================================================
	protected TFileChooserBuilder() { this.parent = MC_CLIENT.currentScreen; }
	// ==================================================
	/**
	 * Sets the {@link Screen} that will be opened after the
	 * {@link TFileChooserScreen} closes.
	 */
	public final TFileChooserBuilder setParentScreen(@Nullable Screen parent) { this.parent = parent; return this; }
	// --------------------------------------------------
	/**
	 * Sets the {@link Path} that will be initially shown on the {@link TFileChooserScreen}.
	 */
	public final TFileChooserBuilder setStartingPath(@Nullable Path startingPath)
	{
		this.startingPath = startingPath;
		return this;
	}
	// --------------------------------------------------
	/**
	 * Adds a {@link TFileFilter} to the list of {@link TFileFilter}s
	 * that will be used by the {@link TFileChooserScreen}.
	 * @param fileFilter The {@link TFileFilter} to add.
	 */
	public final TFileChooserBuilder addFileFilter(TFileFilter fileFilter) throws NullPointerException
	{
		this.filters.add(Objects.requireNonNull(fileFilter));
		return this;
	}
	// --------------------------------------------------
	public final CompletableFuture<TFileChooserResult> showOpenFileDialog() { return showOpenFileDialog(null); }
	public final CompletableFuture<TFileChooserResult> showOpenFileDialog(String targetExtension) { return showDialog(FileChooserDialogType.OPEN_FILE, targetExtension); }
	
	public final CompletableFuture<TFileChooserResult> showSaveFileDialog() { return showSaveFileDialog(null); }
	public final CompletableFuture<TFileChooserResult> showSaveFileDialog(String targetExtension) { return showDialog(FileChooserDialogType.SAVE_FILE, targetExtension); }
	
	public final CompletableFuture<TFileChooserResult> showSelectDirectoryDialog() { return showDialog(FileChooserDialogType.SELECT_DIRECTORY, null); }
	
	/**
	 * Builds the {@link TFileChooserScreen} and shows it using {@link MinecraftClient#setScreen(Screen)}.
	 * @param dialogType The {@link TFileChooserScreen}'s {@link FileChooserDialogType}.
	 * @param targetExtension The {@link File} extension that will be used for open/save dialogs. Mandatory for {@link FileChooserDialogType#SAVE_FILE}!
	 * @return A {@link CompletableFuture} that will be completed once the user makes a choice.
	 * @apiNote Do not use {@link CompletableFuture#get()}. Will result in a dead-lock!
	 */
	public final CompletableFuture<TFileChooserResult> showDialog
	(FileChooserDialogType dialogType, @Nullable String targetExtension)
	{
		//ensure there is always a starting path
		if(this.startingPath == null)
			this.startingPath = Path.of(System.getProperty("user.home"));
		
		//create the screen
		final var screen = new TFileChooserScreen(Objects.requireNonNull(dialogType), this.startingPath, targetExtension);
		screen.parent = this.parent;
		
		//put file filters into the screen
		if(this.filters.size() > 0)
			screen.filters.addAll(this.filters);
		
		//show the screen and return
		MC_CLIENT.setScreen(screen.getAsScreen());
		return screen.promise;
	}
	// ==================================================
}