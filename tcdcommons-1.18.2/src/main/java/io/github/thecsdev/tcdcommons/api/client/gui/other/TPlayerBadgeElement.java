package io.github.thecsdev.tcdcommons.api.client.gui.other;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A {@link TElement} that renders {@link PlayerBadge}s on the {@link TScreen}.
 */
public class TPlayerBadgeElement extends TBlankElement
{
	// ==================================================
	protected PlayerBadge badge;
	// ==================================================
	public TPlayerBadgeElement(int x, int y, int width, int height, PlayerBadge badge)
	{
		super(x, y, width, height);
		setBadge(badge);
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link PlayerBadge} associated with
	 * this {@link TPlayerBadgeElement}.
	 */
	public @Nullable PlayerBadge getBadge() { return this.badge; }
	
	/**
	 * Sets the {@link PlayerBadge} that will be rendered on the {@link TScreen}.
	 * @param badge The target {@link PlayerBadge}.
	 */
	public void setBadge(PlayerBadge badge)
	{
		this.badge = badge;
		if(badge != null)
			setTooltip(
					TextUtils.fLiteral("§6" + badge.getName().getString())
					.append(TextUtils.fLiteral("\n§7" + badge.getBadgeId().toString()))
					.append(TextUtils.fLiteral("\n\n§r" + badge.getDescription().getString()))
					);
		else setTooltip(null);
	}
	// ==================================================
	public @Override void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		if(this.badge != null)
			this.badge.renderOnClientScreen(matrices, x, y, width, height, deltaTime);
	}
	// ==================================================
}
