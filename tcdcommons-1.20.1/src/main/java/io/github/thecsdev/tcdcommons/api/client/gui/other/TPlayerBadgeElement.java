package io.github.thecsdev.tcdcommons.api.client.gui.other;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import io.github.thecsdev.tcdcommons.api.features.player.badges.PlayerBadge;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.client.gui.DrawContext;

/**
 * A {@link TElement} that renders {@link PlayerBadge}s on the {@link TScreen}.
 */
public class TPlayerBadgeElement extends TElement
{
	// ==================================================
	protected PlayerBadge badge;
	// ==================================================
	public TPlayerBadgeElement(int x, int y, int width, int height, PlayerBadge badge)
	{
		super(x, y, width, height);
		setBadge(badge);
	}
	public @Override boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return !gainingFocus; }
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
		{
			String txt_id = Objects.toString(badge.getBadgeId(), "minecraft:null");
			String txt_desc = "-";
			var text_desc = badge.getDescription();
			if(text_desc != null) txt_desc = text_desc.getString();
			
			setTooltip(
					TextUtils.fLiteral("§e" + badge.getName().getString())
					.append(TextUtils.fLiteral("\n§7" + txt_id))
					.append(TextUtils.fLiteral("\n\n§r" + txt_desc))
				);
		}
		else setTooltip(null);
	}
	// ==================================================
	public @Override void render(DrawContext pencil, int mouseX, int mouseY, float deltaTime)
	{
		if(this.badge != null)
			this.badge.renderOnClientScreen(pencil, x, y, width, height, deltaTime);
	}
	// ==================================================
}
