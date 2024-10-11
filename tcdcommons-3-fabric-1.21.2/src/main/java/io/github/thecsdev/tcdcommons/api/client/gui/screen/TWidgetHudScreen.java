package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import static io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries.HUD_SCREEN;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.registry.TClientRegistries;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.IParentScreenProvider;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A {@link TScreen} whose primary purpose is appending GUI widgets
 * to the {@link InGameHud} via {@link TClientRegistries#HUD_SCREEN}.
 */
@Experimental
public abstract class TWidgetHudScreen extends TScreen implements IParentScreenProvider
{
	// ==================================================
	/**
	 * Stores the {@link WidgetEntry}s for this {@link TWidgetHudScreen}.
	 */
	private final @Internal List<WidgetEntry<?>> entries = new LinkedList<>();
	//
	protected final Identifier hudScreenId;
	protected @Nullable Screen parent;
	// ==================================================
	public TWidgetHudScreen(Text title, Identifier hudScreenId) throws NullPointerException
	{
		super(title);
		this.hudScreenId = Objects.requireNonNull(hudScreenId);
	}
	public final @Override Screen getParentScreen() { return this.parent; }
	// ==================================================
	/**
	 * Refreshes this {@link TWidgetHudScreen} by calling
	 * {@link #clearChildren()} and then calling {@link #init()}.
	 */
	public void refresh() { if(this.client == null) return; clearChildren(); init(); }
	//
	protected @Virtual @Override void init()
	{
		this.entries.removeIf(entry -> entry == null);
		for(final var entry : this.entries)
			initEntry(entry);
	}
	//
	/**
	 * Initializes a {@link WidgetEntry} that belongs to this {@link TWidgetHudScreen}.
	 * @param entry The {@link WidgetEntry} from {@link #entries} to initialize.
	 */
	protected final boolean initEntry(WidgetEntry<?> entry)
	{
		//create the element
		if(!containsEntry(entry)) return false;
		
		final var el = new TWidgetHudElement(Objects.requireNonNull(entry.createWidget()));
		entry.lastCreatedWidget = el;
		
		//calculate the placement coordinates
		final int w = getWidth(), h = getHeight();
		final double aX = entry.anchorX, aY = entry.anchorY; //anchor XY
		final int sX = (int)(w * aX), sY = (int)(h * aY); //screen XY
		final int eX = (int)(el.getWidth() * aX), eY = (int)(el.getHeight() * aY); //element XY
		final int pX = (sX - eX), pY = (sY - eY); //placement XY
		
		//set position and add register position change event
		el.setPosition(pX, pY, false);
		el.eMoved.register((__, deltaX, deltaY) -> //important: must come after setting the position
		{
			//when the element moves, update the anchor XY
			final int dW = (w - el.getWidth()), dH = (h - el.getHeight()); //difference WH
			entry.anchorX = (double)el.getX() / dW;
			entry.anchorY = (double)el.getY() / dH;
		});
		
		//add element
		return addChild(el, false);
	}
	
	/**
	 * Refreshes a {@link WidgetEntry}'s GUI by removing it and then
	 * re-creating it by using {@link WidgetEntry#createWidget()}.
	 */
	public final void refreshEntry(WidgetEntry<?> entry)
	{
		//null check
		if(entry.lastCreatedWidget == null)
			return;
		//remove and re-init
		removeChild(entry.lastCreatedWidget);
		initEntry(entry);
	}
	// --------------------------------------------------
	protected @Virtual @Override void onOpened()
	{
		//auto-registration;
		//registry raises exceptions for already occupied keys, so perform a check first
		if(!HUD_SCREEN.containsKey(this.hudScreenId))
			HUD_SCREEN.register(this.hudScreenId, getAsScreen());
	}
	protected @Virtual @Override void onClosed()
	{
		//super
		super.onClosed();
		this.parent = null; //clear parent
		
		//do not auto-unregister if has entries
		if(this.entries.size() != 0) return;
		
		//auto-unregistration;
		//unregister this screen if has no entries
		if(Objects.equals(HUD_SCREEN.getValue(this.hudScreenId).orElse(null), getAsScreen()))
			HUD_SCREEN.unregister(this.hudScreenId);
		
		//refresh the screen
		refresh();
	}
	public @Virtual @Override void close() { MC_CLIENT.setScreen(this.parent); }
	// --------------------------------------------------
	public @Virtual @Override void renderBackground(TDrawContext pencil) {}
	// ==================================================
	/**
	 * Adds a {@link WidgetEntry} to this {@link TWidgetHudScreen}.
	 * @return {@code true} if the {@link WidgetEntry} was added successfully.
	 */
	public boolean addEntry(WidgetEntry<?> entry)
	{
		//don't add the entry if it's null or if it's a duplicate
		if(entry == null || this.entries.contains(entry) || (entry.whs != null && entry.whs != this))
			return false;
		//add entry otherwise, and refresh
		entry.whs = this;
		try { return this.entries.add(entry); } finally { refresh(); }
	}
	
	/**
	 * Returns {@code true} if this {@link TWidgetHudScreen} contains a given {@link WidgetEntry}.
	 */
	public boolean containsEntry(WidgetEntry<?> entry) { return this.entries.contains(entry); }
	
	/**
	 * Removes a {@link WidgetEntry} from this {@link TWidgetHudScreen}.
	 */
	public boolean removeEntry(WidgetEntry<?> entry)
	{
		entry.whs = null;
		try { return this.entries.remove(entry); } finally { refresh(); }
	}
	
	/**
	 * Clears all {@link WidgetEntry}s from this {@link TWidgetHudScreen}.
	 */
	public final void clearEntries()
	{
		this.entries.forEach(e -> e.whs = null);
		this.entries.clear();
		refresh();
	}
	
	/**
	 * Returns the number of {@link WidgetEntry}s added to this {@link TWidgetHudScreen}.
	 */
	public final int entryCount() { return this.entries.size(); }
	// --------------------------------------------------
	/**
	 * Returns the {@link Identifier} for this {@link TWidgetHudScreen}
	 * that is used in the {@link TClientRegistries#HUD_SCREEN} registry.
	 */
	public final Identifier getHudScreenID() { return this.hudScreenId; }
	
	/**
	 * Sets the {@link #parent} {@link Screen}.
	 * @param parent The new parent {@link Screen}.
	 * @see #getParentScreen()
	 */
	public @Virtual void setParentScreen(Screen parent) { this.parent = parent; }
	// ==================================================
	/**
	 * A {@link TWidgetHudScreen} widget entry.
	 */
	public static abstract class WidgetEntry<T extends TElement>
	{
		// ----------------------------------------------
		private @Internal @Nullable TWidgetHudScreen whs; //FORBIDDEN DIRECT ACCESS. USE THE GETTER!
		private @Internal TElement lastCreatedWidget;
		// ----------------------------------------------
		/**
		 * Anchor points that dictate how the {@link TElement} of
		 * this {@link WidgetEntry} will be positioned on the {@link TWidgetHudScreen}.
		 */
		protected double anchorX = 0.5, anchorY = 0.5;
		// ----------------------------------------------
		public WidgetEntry() {}
		public WidgetEntry(double anchorX, double anchorY)
		{
			this.anchorX = anchorX;
			this.anchorY = anchorY;
		}
		// ----------------------------------------------
		/**
		 * Returns the {@link TWidgetHudScreen} this {@link WidgetEntry} belongs to.
		 */
		public final TWidgetHudScreen getHudScreen()
		{
			if(this.whs != null && !this.whs.containsEntry(this))
				this.whs = null;
			return this.whs;
		}
		
		/**
		 * Calls {@link TWidgetHudScreen#refreshEntry(WidgetEntry)}.
		 */
		public final void refreshEntry()
		{
			final var whs = getHudScreen();
			if(whs != null) whs.refreshEntry(this);
		}
		
		/**
		 * Calls {@link TWidgetHudScreen#removeEntry(WidgetEntry)}.
		 */
		public final void removeEntry()
		{
			final var whs = getHudScreen();
			if(whs != null) whs.removeEntry(this);
		}
		// ----------------------------------------------
		/**
		 * Creates the widget {@link TElement}.
		 * @apiNote The positioning will be done automatically.
		 * For that reason, make the initial X and Y equal to 0.
		 * @apiNote Must not return {@code null}.
		 */
		public abstract T createWidget();
		// ----------------------------------------------
	}
	// ==================================================
}