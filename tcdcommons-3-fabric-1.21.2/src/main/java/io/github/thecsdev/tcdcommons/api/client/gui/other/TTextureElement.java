package io.github.thecsdev.tcdcommons.api.client.gui.other;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

public @Virtual class TTextureElement extends TBlankElement
{
	// ==================================================
	protected @Nullable UITexture texture;
	protected final float[] textureColor = new float[/*4*/] {1, 1, 1, 1};
	// ==================================================
	public TTextureElement(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TTextureElement(int x, int y, int width, int height, UITexture texture)
	{
		super(x, y, width, height);
		this.texture = texture;
	}
	// ==================================================
	public final @Nullable UITexture getTexture() { return this.texture; }
	public @Virtual void setTexture(UITexture texture) { this.texture = texture; }
	// --------------------------------------------------
	public final float[] getTextureColor() { return this.textureColor; }
	public final void setTextureColor(float r, float g, float b) { setTextureColor(r, g, b, this.textureColor[3]); }
	public @Virtual void setTextureColor(float r, float g, float b, float a)
	{
		this.textureColor[0] = r;
		this.textureColor[1] = g;
		this.textureColor[2] = b;
		this.textureColor[3] = a;
	}
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil)
	{
		if(texture == null) return;
		pencil.pushTShaderColor(this.textureColor[0], this.textureColor[1], this.textureColor[2], this.textureColor[3]);
		texture.drawTexture(pencil);
		pencil.popTShaderColor();
	}
	// ==================================================
}