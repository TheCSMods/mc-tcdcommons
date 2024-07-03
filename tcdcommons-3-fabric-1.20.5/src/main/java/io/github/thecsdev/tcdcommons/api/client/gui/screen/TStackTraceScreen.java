package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.layout.UIListLayout;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TStackTracePanel;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Axis2D;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.enumerations.VerticalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.screen.Screen;

/**
 * A {@link TScreen} that shows a stack trace to the user.
 */
public final class TStackTraceScreen extends TScreen implements IParentScreenProvider
{
	// ==================================================
	private final Screen parent;
	private final Throwable throwable;
	// ==================================================
	public TStackTraceScreen(@Nullable Screen parent, Throwable throwable) throws NullPointerException
	{
		super(literal(throwable.getClass().getName()));
		this.parent = parent;
		this.throwable = Objects.requireNonNull(throwable);
	}
	public final @Override Screen getParentScreen() { return this.parent; }
	public final Throwable getThrowable() { return this.throwable; }
	public final @Override void close() { MC_CLIENT.setScreen(this.parent); }
	// ==================================================
	protected final @Override void init()
	{
		//add a content pane onto which all the elements will be added
		final var contentPane = new TFillColorElement(0, 0, getWidth(), getHeight());
		contentPane.setColor(0x22FFFFFF);
		contentPane.setZOffset(TCDCommonsClient.MAGIC_ITEM_Z_OFFSET);
		addChild(contentPane, false);
		
		//create and a stack trace panel
		int w = (int)(getWidth() * 0.6f);
		if(w < 300) w = 300;
		if(w > getWidth()) w = getWidth();
		
		final var panel_st = new TStackTracePanel(0, 0, w, getHeight() - 50, this.throwable);
		panel_st.setCloseAction(() -> close());
		contentPane.addChild(panel_st, false);
		
		new UIListLayout(Axis2D.Y, VerticalAlignment.CENTER, HorizontalAlignment.CENTER).apply(contentPane);
	}
	// ==================================================
}