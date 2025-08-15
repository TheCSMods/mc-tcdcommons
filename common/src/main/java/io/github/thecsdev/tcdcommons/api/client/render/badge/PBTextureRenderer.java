package io.github.thecsdev.tcdcommons.api.client.render.badge;

import io.github.thecsdev.tcdcommons.api.badge.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link PlayerBadgeRenderer} that uses {@link UITexture}s for rendering.
 */
public final class PBTextureRenderer extends PlayerBadgeRenderer<PlayerBadge>
{
	// ==================================================
	protected final UITexture texture;
	// ==================================================
	public PBTextureRenderer(UITexture texture)
	{
		super(PlayerBadge.class);
		this.texture = texture;
	}
	// ==================================================
	/**
	 * Returns the {@link UITexture} this {@link PBTextureRenderer} renders.
	 */
	public final @Nullable UITexture getTexture() { return this.texture; }
	// ==================================================
	public @Virtual @Override void render(
			GuiGraphics pencil,
			int x, int y, int width, int height,
			int mouseX, int mouseY, float deltaTime)
	{
		if(this.texture != null) this.texture.drawTexture(pencil, x, y, width, height);
		else pencil.fill(x, y, x + width, y + height, TDrawContext.DEFAULT_ERROR_COLOR);
	}
	// ==================================================
}