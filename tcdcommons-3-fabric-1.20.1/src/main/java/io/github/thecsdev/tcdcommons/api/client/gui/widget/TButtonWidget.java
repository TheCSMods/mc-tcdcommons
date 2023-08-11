package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProviderSetter;
import net.minecraft.text.Text;

public @Virtual class TButtonWidget extends TClickableWidget implements ITextProviderSetter
{
	// ==================================================
	protected @Nullable Text text;
	protected @Nullable Consumer<TButtonWidget> onClick;
	// ==================================================
	public TButtonWidget(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TButtonWidget(int x, int y, int width, int height, Text text) { this(x, y, width, height, text, null); }
	public TButtonWidget(int x, int y, int width, int height, Text text, Consumer<TButtonWidget> onClick)
	{
		super(x, y, width, height);
		this.text = text;
		this.onClick = onClick;
	}
	// --------------------------------------------------
	public final @Nullable @Override Text getText() { return this.text; }
	public @Virtual @Override void setText(@Nullable Text text) { this.text = text; }
	// --------------------------------------------------
	public final @Nullable Consumer<TButtonWidget> getOnClick() { return this.onClick; }
	public @Virtual void setOnClick(@Nullable Consumer<TButtonWidget> onClick) { this.onClick = onClick; }
	// ==================================================
	protected @Virtual @Override void onClick()
	{
		if(this.onClick == null) return;
		this.onClick.accept(this);
	}
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil)
	{
		pencil.drawTButton(getButtonTextureY());
		pencil.drawTElementTextTH(this.text, HorizontalAlignment.CENTER);
	}
	// ==================================================
}