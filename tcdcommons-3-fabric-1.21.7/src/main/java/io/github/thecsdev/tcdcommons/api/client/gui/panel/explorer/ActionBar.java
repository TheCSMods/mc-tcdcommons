package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TClickableWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSelectFileFilterWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TTextFieldWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.interfaces.TFileFilter;
import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.text.Text;

@ApiStatus.Experimental
public @Virtual class ActionBar extends TRefreshablePanelElement
{
	// ==================================================
	public static final int HEIGHT = 45+10+10;
	protected final ActionBarProxy proxy;
	// --------------------------------------------------
	private @Nullable FileNameInputField in_filename;
	private @Nullable TSelectFileFilterWidget in_filefilter;
	private @Nullable TClickableWidget btn_done;
	private @Nullable TClickableWidget btn_cancel;
	// ==================================================
	public ActionBar(int x, int y, int width, ActionBarProxy proxy) { this(x, y, width, HEIGHT, proxy); }
	public ActionBar(int x, int y, int width, int height, ActionBarProxy proxy)
	{
		super(x, y, Math.max(width, 50), Math.max(height, HEIGHT));
		super.scrollFlags = 0;
		super.scrollPadding = 0;
		super.outlineColor = 0;
		super.backgroundColor = -16777216; //black
		
		this.proxy = Objects.requireNonNull(proxy);
	}
	// ==================================================
	public final ActionBarProxy getProxy() { return this.proxy; }
	// --------------------------------------------------
	protected final boolean TRefreshablePanelElement_super_input(TInputContext inputContext) { return super.input(inputContext); }
	public @Virtual @Override boolean input(TInputContext inputContext) { return false; /*clear all input handling*/ }
	// ==================================================
	public final @Nullable TTextFieldWidget getFileNameInput() { return this.in_filename; }
	public final @Nullable TSelectFileFilterWidget getFileFilterInput() { return this.in_filefilter; }
	public final @Nullable TClickableWidget getDoneButton() { return this.btn_done; }
	public final @Nullable TClickableWidget getCancelButton() { return this.btn_cancel; }
	// --------------------------------------------------
	/**
	 * Sets the input text in the "file name" {@link TTextFieldWidget}.
	 * @throws IllegalStateException If this {@link ActionBar} isn't yet initialized.
	 */
	public final void setSelectedFileName(String selectedFileName) throws IllegalStateException
	{
		if(this.in_filename == null)
			throw new IllegalStateException("Not initialized yet.");
		this.in_filename.setInput(selectedFileName);
	}
	
	/**
	 * Sets the selected {@link TFileFilter} in the "file type" input {@link TSelectFileFilterWidget}.
	 * @throws IllegalStateException If this {@link ActionBar} isn't yet initialized.
	 * @throws NoSuchElementException If the chosen {@link TFileFilter} isn't an option in the {@link TSelectFileFilterWidget}.
	 */
	public final void setSelectedFileFilter(TFileFilter fileFilter) throws IllegalStateException, NoSuchElementException
	{
		if(this.in_filefilter == null)
			throw new IllegalStateException("Not initialized yet.");
		this.in_filefilter.setSelected(fileFilter);
	}
	
	public final void focusOnFileNameInput() throws IllegalStateException
	{
		if(this.in_filename == null || getParentTScreen() == null)
			throw new IllegalStateException("Not initialized yet.");
		getParentTScreen().setFocusedElement(this.in_filename);
	}
	// ==================================================
	protected @Virtual @Override void init()
	{
		//labels
		final var lbl_filename = new TLabelElement(5, 5, getWidth() - 10, 15);
		lbl_filename.setText(TCDCT.gui_explorer_actionBar_fileName());
		addChild(lbl_filename, true);
		
		final var lbl_filetype = new TLabelElement(getX() + 5, lbl_filename.getEndY() + 5, lbl_filename.getWidth(), 15);
		lbl_filetype.setText(TCDCT.gui_explorer_actionBar_fileType());
		addChild(lbl_filetype, false);
		
		//filters
		this.in_filename = new FileNameInputField(0, lbl_filename.getY(), (lbl_filename.getWidth()/10)*7, 15);
		this.in_filename.setPosition(lbl_filename.getEndX() - this.in_filename.getWidth(), this.in_filename.getY(), false);
		addChild(this.in_filename, false);
		
		this.in_filefilter = new TSelectFileFilterWidget(
				this.in_filename.getX(), this.in_filename.getY() + 20,
				this.in_filename.getWidth(), 15,
				this.proxy.getFileFilters());
		//MUST BE BEFORE EVENT HANDLER, OTHERWISE STACKOVERFLOW
		this.in_filefilter.setSelected(this.in_filefilter.entryOf(this.proxy.getSelectedFileFilter()), false);
		//ONLY AFTER, DECLARE THE HANDLER
		in_filefilter.eSelectionChanged.register((self, sel) -> this.proxy.onSelectFileFilter(sel == null ? null : sel.getFileFilter()));
		addChild(in_filefilter, false);
		
		//buttons
		final var btn_canc = new TButtonWidget(getEndX() - 65, getEndY() - 20, 60, 15);
		btn_canc.setText(translatable("gui.cancel"));
		btn_canc.setEnabled(this.proxy.canCancel());
		btn_canc.setOnClick(__ -> this.proxy.onCancel());
		addChild(btn_canc, false);
		
		final var btn_done = new TButtonWidget(btn_canc.getX() - 65, btn_canc.getY(), 60, 15);
		btn_done.setText(this.proxy.getSubmitButtonText());
		btn_done.setEnabled(this.proxy.canSubmit());
		btn_done.setOnClick(__ -> this.proxy.onSubmit(new ActionBar.ActionBarProxy.SubmitContext()
		{
			public final @Override String getSelectedFileName() { return in_filename.getInput(); }
			public final @Override TFileFilter getSelectedFileFilter()
			{
				final var ff = in_filefilter.getSelected();
				return (ff != null) ? ff.getFileFilter() : TFileFilter.ALL_FILES;
			}
		}));
		addChild(btn_done, false);
		
		//post-init stuff
		this.btn_done = btn_done;
		this.btn_cancel = btn_canc;
	}
	// ==================================================
	protected final class FileNameInputField extends TTextFieldWidget
	{
		final static int[] blacklist = new int[] { 34, 60, 62, 124, 58, 42, 63, 92, 47 };
		public FileNameInputField(int x, int y, int width, int height) { super(x, y, width, height); }
		public final @Override boolean isCharacterAllowed(char c) { return Arrays.binarySearch(blacklist, c) < 0; }
	}
	// --------------------------------------------------
	public static interface ActionBarProxy
	{
		public Iterable<TFileFilter> getFileFilters();
		public TFileFilter getSelectedFileFilter();
		public void onSelectFileFilter(TFileFilter selectedFileFilter);
		
		public Text getSubmitButtonText();
		
		default boolean canCancel() { return true; }
		default boolean canSubmit() { return true; }
		
		public void onCancel();
		public void onSubmit(SubmitContext submitContext);
		
		public static interface SubmitContext
		{
			public String getSelectedFileName();
			public TFileFilter getSelectedFileFilter();
		}
	}
	// ==================================================
}