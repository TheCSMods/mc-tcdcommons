package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProviderSetter;
import net.minecraft.text.Text;

/**
 * A {@link TButtonWidget} is a {@link TClickableWidget} that
 * looks and behaves like a vanilla button.
 */
public @Virtual class TButtonWidget extends TClickableWidget implements ITextProviderSetter
{
	// ==================================================
	protected @Nullable Text text;
	protected @Nullable Consumer<TButtonWidget> onClick;
	protected @Nullable UITexture icon;
	protected final float[] iconColor = new float[] { 1, 1, 1, 1 };
	// ==================================================
	public TButtonWidget(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TButtonWidget(int x, int y, int width, int height, Text text) { this(x, y, width, height, text, null); }
	public TButtonWidget(int x, int y, int width, int height, Text text, Consumer<TButtonWidget> onClick)
	{
		super(x, y, Math.max(width, 5), Math.max(height, 5));
		this.text = text;
		this.onClick = onClick;
	}
	// --------------------------------------------------
	public final @Nullable @Override Text getText() { return this.text; }
	public @Virtual @Override void setText(@Nullable Text text) { this.text = text; }
	// --------------------------------------------------
	public final @Nullable Consumer<TButtonWidget> getOnClick() { return this.onClick; }
	public @Virtual void setOnClick(@Nullable Consumer<TButtonWidget> onClick) { this.onClick = onClick; }
	// --------------------------------------------------
	public final @Nullable UITexture getIcon() { return this.icon; }
	public @Virtual void setIcon(@Nullable UITexture icon) { this.icon = icon; }
	// --------------------------------------------------
	public final float[] getIconColor() { return this.iconColor; }
	public final void setIconColor(float r, float g, float b) { setIconColor(r, g, b, this.iconColor[3]); }
	public @Virtual void setIconColor(float r, float g, float b, float a)
	{
		this.iconColor[0] = r;
		this.iconColor[1] = g;
		this.iconColor[2] = b;
		this.iconColor[3] = a;
	}
	// ==================================================
	protected @Virtual @Override void onClick()
	{
		if(this.onClick == null) return;
		this.onClick.accept(this);
	}
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil)
	{
		renderBackground(pencil);
		pencil.enableScissor(getX(), getY(), getEndX(), getEndY());
		pencil.drawTElementTextTH(this.text, HorizontalAlignment.CENTER);
		pencil.disableScissor();
	}
	
	/**
	 * By default, renders the "background" texture and icon for this {@link TButtonWidget}.
	 * @param pencil The {@link TDrawContext}.
	 */
	protected @Virtual void renderBackground(TDrawContext pencil)
	{
		pencil.drawTButton(this.enabled, isFocusedOrHovered());
		renderIcon(pencil);
	}
	
	/**
	 * By default, renders the "background" icon for this {@link TButtonWidget}.
	 * @param pencil The {@link TDrawContext}.
	 */
	protected @Virtual void renderIcon(TDrawContext pencil)
	{
		if(this.icon != null)
		{
			pencil.pushTShaderColor(this.iconColor[0], this.iconColor[1], this.iconColor[2], this.iconColor[3]);
			this.icon.drawTexture(pencil, getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4);
			pencil.popTShaderColor();
		}
	}
	// ==================================================
}