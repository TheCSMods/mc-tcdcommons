package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TTextFieldWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

@ApiStatus.Experimental
public @Virtual class NavigationBar extends TRefreshablePanelElement
{
	// ==================================================
	/**
	 * The default {@link NavigationBar} height.
	 */
	public static final int HEIGHT = 15;
	// --------------------------------------------------
	protected final NavigationBarProxy proxy;
	// ==================================================
	public NavigationBar(int x, int y, int width, NavigationBarProxy proxy) { this(x, y, width, HEIGHT, proxy); }
	public NavigationBar(int x, int y, int width, int height, NavigationBarProxy proxy)
	{
		super(x, y, width, Math.max(height, HEIGHT));
		super.scrollFlags = 0;
		super.scrollPadding = 0;
		super.outlineColor = 0;
		super.backgroundColor = -16777216; //black
		
		this.proxy = Objects.requireNonNull(proxy);
	}
	// ==================================================
	/**
	 * Returns the {@link NavigationBarProxy} associated with this {@link NavigationBar}.
	 */
	public final NavigationBarProxy getProxy() { return this.proxy; }
	// --------------------------------------------------
	protected final boolean TRefreshablePanelElement_super_input(TInputContext inputContext) { return super.input(inputContext); }
	public @Virtual @Override boolean input(TInputContext inputContext) { return false; /*clear all input handling*/ }
	// ==================================================
	protected @Virtual @Override void init()
	{
		final var btn_back = new TButtonWidget(0, 0, HEIGHT, HEIGHT, literal("<"));
		btn_back.setEnabled(this.proxy.canNavigateBackward());
		btn_back.setOnClick(__ -> this.proxy.onNavigateBackward());
		addChild(btn_back, true);

		final var btn_forw = new TButtonWidget(HEIGHT, 0, HEIGHT, HEIGHT, literal(">"));
		btn_forw.setEnabled(this.proxy.canNavigateForward());
		btn_forw.setOnClick(__ -> this.proxy.onNavigateForward());
		addChild(btn_forw, true);

		final var btn_refr = new TButtonWidget(HEIGHT*2, 0, HEIGHT, HEIGHT, literal("O"));
		btn_refr.setEnabled(this.proxy.canRefresh());
		btn_refr.setOnClick(__ -> this.proxy.onRefresh());
		addChild(btn_refr, true);
		
		final var txt_path = new TTextFieldWidget(HEIGHT*3, 0, getWidth()-(HEIGHT*3)-1, HEIGHT);
		txt_path.setEnabled(false);
		txt_path.setInput(this.proxy.getCurrentPath().toAbsolutePath().toString());
		addChild(txt_path, true);
	}
	// ==================================================
	/**
	 * An {@code interface} that provides valuable methods that define
	 * what happens when given events take place within the {@link NavigationBar}.
	 */
	public static interface NavigationBarProxy
	{
		/**
		 * Returns the {@link Path} that should currently
		 * be displayed on the {@link NavigationBar}.
		 */
		public Path getCurrentPath();
		
		default boolean canNavigateForward() { return false; }
		default boolean canNavigateBackward() { return false; }
		default boolean canRefresh() { return false; }
		
		default void onNavigateForward() {}
		default void onNavigateBackward() {}
		
		/**
		 * Invoked when the "refresh" navigation button is pressed.
		 * @apiNote Not to be confused with {@link TRefreshablePanelElement#refresh()}!
		 */
		default void onRefresh() {}
	}
	// ==================================================
}