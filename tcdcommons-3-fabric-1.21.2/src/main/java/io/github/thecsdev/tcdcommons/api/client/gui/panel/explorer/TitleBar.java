package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TTextureElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.text.Text;

@ApiStatus.Experimental
public @Virtual class TitleBar extends TRefreshablePanelElement
{
	// ==================================================
	/**
	 * The default {@link TitleBar} height.
	 */
	public static final int HEIGHT = 15;
	// --------------------------------------------------
	protected final TitleBarProxy proxy;
	// ==================================================
	public TitleBar(int x, int y, int width, TitleBarProxy proxy) { this(x, y, width, HEIGHT, proxy); }
	public TitleBar(int x, int y, int width, int height, TitleBarProxy proxy)
	{
		super(x, y, Math.max(width, HEIGHT*3 + 20), Math.max(height, HEIGHT));
		super.scrollFlags = 0;
		super.scrollPadding = 0;
		super.outlineColor = 0;
		super.backgroundColor = -16777216; //black
		
		this.proxy = Objects.requireNonNull(proxy);
	}
	// ==================================================
	protected final boolean TRefreshablePanelElement_super_input(TInputContext inputContext) { return super.input(inputContext); }
	public @Virtual @Override boolean input(TInputContext inputContext) { return false; /*clear all input handling*/ }
	// --------------------------------------------------
	protected @Virtual @Override void init()
	{
		//icon
		final var ico = new TTextureElement(0, 0, HEIGHT, HEIGHT);
		ico.setTexture(this.proxy.getIcon());
		addChild(ico, true);
		
		//title label
		final var lbl_title = new TLabelElement(HEIGHT, 1, getWidth() - HEIGHT, HEIGHT);
		lbl_title.setText(this.proxy.getTitle());
		addChild(lbl_title, true);
		
		//buttons
		init_squareButtons();
	}
	// --------------------------------------------------
	/**
	 * By default, creates square buttons for "close", "restore", and "minimize" operations.
	 * @see #addSquareButton(Text, Consumer)
	 */
	protected @Virtual void init_squareButtons()
	{
		if(this.proxy.canMinimize())
			addSquareButton(literal("-"), __ -> this.proxy.onMinimize());
		
		if(this.proxy.canRestore())
			addSquareButton(literal("\u25A1"), __ -> this.proxy.onRestore());
		
		if(this.proxy.canClose())
			addSquareButton(literal("X"), __ -> this.proxy.onClose());
	}
	// ==================================================
	/**
	 * Adds a new square {@link TButtonWidget} to the right side of this {@link TitleBar},
	 * offsetting any existing buttons to the left.
	 * @return The newly created and added {@link TButtonWidget}.
	 */
	protected final TButtonWidget addSquareButton(Text text, Consumer<TButtonWidget> onClick)
	{
		//offset all existing buttons
		for(final var c : getChildren())
			if(c instanceof TitleBarSquareButton)
				c.setPosition(c.getX() - HEIGHT, c.getY(), false);
		
		//add a new button
		final var btn = new TitleBarSquareButton(getWidth() - HEIGHT, 0, HEIGHT, HEIGHT, text, onClick);
		addChild(btn, true);
		
		//return the new button
		return btn;
	}
	// --------------------------------------------------
	/**
	 * This class serves as more of an "identifier" to a special type of
	 * {@link TButtonWidget} used by {@link TitleBar}s.
	 */
	protected static final class TitleBarSquareButton extends TButtonWidget
	{
		public TitleBarSquareButton(int x, int y, int width, int height, Text text, Consumer<TButtonWidget> onClick) { super(x, y, width, height, text, onClick); }
		public TitleBarSquareButton(int x, int y, int width, int height, Text text) { super(x, y, width, height, text); }
		public TitleBarSquareButton(int x, int y, int width, int height) { super(x, y, width, height); }
	}
	// ==================================================
	/**
	 * An {@code interface} that provides valuable methods that define
	 * what happens when given events take place within the {@link TitleBar}.
	 */
	public static interface TitleBarProxy
	{
		/**
		 * Returns the {@link Text} that should be shown on the {@link TitleBar}.
		 */
		public @Nullable Text getTitle();
		
		/**
		 * Returns the {@link UITexture} that represents the "icon" that
		 * will be shown on the {@link TitleBar}.
		 */
		default @Nullable UITexture getIcon() { return null; }
		
		default boolean canMinimize() { return false; }
		default boolean canRestore() { return false; }
		default boolean canClose() { return false; }
		
		default void onMinimize() {}
		default void onRestore() {}
		default void onClose() {}
	}
	// ==================================================
}