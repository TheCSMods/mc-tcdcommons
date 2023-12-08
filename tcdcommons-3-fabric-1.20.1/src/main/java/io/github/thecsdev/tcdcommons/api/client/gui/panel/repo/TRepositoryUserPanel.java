package io.github.thecsdev.tcdcommons.api.client.gui.panel.repo;

import static io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext.TEXTURE_ICONS;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.thecsdev.tcdcommons.api.client.gui.other.TFillColorElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TTextureElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TRefreshablePanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UIExternalTexture;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import io.github.thecsdev.tcdcommons.api.util.thread.TaskScheduler;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * A {@link TRefreshablePanelElement} that displays information about
 * a given repository user using {@link RepositoryUserInfo}.
 */
@Deprecated(forRemoval = true)
@Virtual class TRepositoryUserPanel extends TRefreshablePanelElement
{
	// ==================================================
	/**
	 * Caches {@link RepositoryUserInfo} avatar images aka profile pictures.
	 */
	private static final Cache<String, UITexture> AVATAR_CACHE;
	
	/**
	 * A {@link UITexture} that is typically used by {@link #loadAvatarImageAsync(URL, TTextureElement, UITexture)}
	 * as a "fallback" texture for when the intended texture fails to load due to an {@link Exception} being raised.
	 */
	public static final UITexture FALLBACK_AVATAR_TEXTURE;
	// --------------------------------------------------
	protected @Nullable RepositoryUserInfo userInfo;
	protected @Nullable Text displayName;
	// ==================================================
	static
	{
		AVATAR_CACHE = CacheBuilder.newBuilder()
				.expireAfterWrite(30, TimeUnit.MINUTES)
				.build();
		TaskScheduler.schedulePeriodicCacheCleanup(AVATAR_CACHE);
		FALLBACK_AVATAR_TEXTURE = new UITexture(TEXTURE_ICONS, new Rectangle(0, 0, 64, 64));
	}
	// --------------------------------------------------
	public TRepositoryUserPanel(int x, int y, int width, RepositoryUserInfo userInfo)
	{
		super(x, y, width, 25);
		setScrollPadding(5);
		setUserInfo(userInfo);
	}
	// ==================================================
	/**
	 * Returns the {@link RepositoryUserInfo} this panel is associated with.
	 */
	public final RepositoryUserInfo getUserInfo() { return this.userInfo; }
	
	/**
	 * Sets the {@link RepositoryUserInfo} that will be displayed by this {@link TRepositoryUserPanel}.
	 * @param userInfo The {@link RepositoryUserInfo}.
	 * @see #getUserInfo()
	 */
	public final void setUserInfo(@Nullable RepositoryUserInfo userInfo)
	{
		//initialize fields
		this.userInfo = userInfo;
		
		if(userInfo != null)
		{
			//initialize display name
			final @Nullable var name = userInfo.getName();
			final @Nullable var dName = userInfo.getDisplayName();
			final var finalDName = literal("");
			if(name == null && dName == null) finalDName.append("-");
			else if(name != null && dName != null)
			{
				if(Objects.equals(name, dName.getString())) finalDName.append("@" + name);
				else finalDName.append(dName).append(" (@").append(name).append(")");
			}
			else if(name != null && dName == null) finalDName.append(name);
			else if(name == null && dName != null) finalDName.append("@").append(dName);
			this.displayName = finalDName;
			
			//initialize tooltip
			final var ttt = literal("")
					.append(literal(this.displayName.getString()).formatted(Formatting.YELLOW))
					.append("\n")
					.append(literal(userInfo.getDescription().getString()).formatted(Formatting.GRAY));
			setTooltip(Tooltip.of(ttt));
		}
		else
		{
			this.displayName = null;
			setTooltip(null);
		}
		
		//refresh if possible
		if(getParent() != null) refresh();
	}
	// --------------------------------------------------
	protected @Virtual @Override void init()
	{
		//prepare
		final int sp = getScrollPadding();
		
		//avatar icon
		final int icoSize = Math.min(Math.max(getHeight() - (sp*2), 10), 50);
		final var icoFrame = new TFillColorElement(sp, sp, icoSize, icoSize, 0xff000000);
		final var textureElement = new TTextureElement(1, 1, icoSize - 2, icoSize - 2);
		icoFrame.addChild(textureElement, true);
		addChild(textureElement, true);
		try { loadAvatarImageAsync(new URL(this.userInfo.getAvatarURL()), textureElement); }
		catch(NullPointerException | MalformedURLException e)
		{
			textureElement.setTexture(FALLBACK_AVATAR_TEXTURE);
			textureElement.setTextureColor(0.4f, 0.4f, 0.4f, 0.4f);
		}
		
		//profile name text
		final var lbl_name = new TLabelElement(sp + icoSize + 5, sp, getWidth() - (icoSize + (sp * 2) + 5), 15);
		lbl_name.setText(this.displayName != null ? this.displayName : literal("-"));
		lbl_name.setTextHorizontalAlignment(HorizontalAlignment.LEFT);
		addChild(lbl_name, true);
	}
	// ==================================================
	/**
	 * Asynchronously loads an avatar hosted on the WWW, onto a {@link TTextureElement}.
	 * @param avatarUrl The {@link URL} pointing to the web resource where the avatar is hosted.
	 * @param textureElement The {@link TTextureElement} onto which the avatar will be applied.
	 * @param fallbackTexture The {@link UITexture} that will be applied in the event something goes wrong.
	 * @throws NullPointerException If a non-{@link Nullable} argument is {@code null}.
	 */
	protected static final void loadAvatarImageAsync(
			final URL avatarUrl,
			final TTextureElement textureElement)
			throws NullPointerException
	{
		//prepare requirements
		Objects.requireNonNull(avatarUrl);
		Objects.requireNonNull(textureElement);
		
		//check cache
		final String cacheKey = avatarUrl.toString().toLowerCase();
		final @Nullable var cached = AVATAR_CACHE.getIfPresent(cacheKey);
		if(cached != null)
		{
			textureElement.setTexture(cached);
			textureElement.setTextureColor(1, 1, 1, 1);
			return;
		}
		
		//load texture asynchronously if not cached
		UIExternalTexture.loadTextureAsync(
				avatarUrl, MC_CLIENT,
				tex ->
				{
					AVATAR_CACHE.put(cacheKey, tex);
					textureElement.setTexture(tex);
					textureElement.setTextureColor(1, 1, 1, 1);
				},
				err ->
				{
					AVATAR_CACHE.put(cacheKey, FALLBACK_AVATAR_TEXTURE);
					textureElement.setTexture(FALLBACK_AVATAR_TEXTURE);
					textureElement.setTextureColor(0.4f, 0.4f, 0.4f, 0.4f);
				});
	}
	// ==================================================
}