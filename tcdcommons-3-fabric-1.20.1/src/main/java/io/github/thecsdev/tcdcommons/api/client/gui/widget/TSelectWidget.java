package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.menu.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.collections.IdealList;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProvider;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProviderSetter;
import io.github.thecsdev.tcdcommons.util.TCDCT;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

/**
 * A {@link TButtonWidget} that features a dropdown item selection menu.
 */
public @Virtual class TSelectWidget<T extends TSelectWidget.Entry> extends TButtonWidget implements Iterable<T>
{
	// ==================================================
	public static final Text DEFAULT_LABEL   = TCDCT.gui_wSelect_defLabel();
	public static final UITexture TEX_OPENED = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(20,0,20,20));
	public static final UITexture TEX_CLOSED = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(0,0,20,20));
	// --------------------------------------------------
	protected final Class<T> entryType;
	/**
	 * This is where the {@link TSelectWidget} keeps track
	 * of all selectable {@link Entry}s added to this element.
	 */
	protected final IdealList<T> entries = new IdealList<>();
	protected @Nullable T selected;
	//
	protected @Nullable TContextMenuPanel contextMenu;
	protected final TElementEvent_ContextMenu ehContextMenu = (self, contextMenu) -> this.contextMenu = contextMenu;
	// --------------------------------------------------
	public final TEvent<TSelectWidgetEvent_SelectionChanged<T>> eSelectionChanged = TEventFactory.createLoop();
	// ==================================================
	/**
	 * @apiNote Do not pass any arguments into the {@code T...} array.
	 * The array servers as a "getter" utility for the generic {@link Entry} type.
	 */
	public @SafeVarargs TSelectWidget(int x, int y, int width, int height, T... entryTypeClassGetter)
	{ this(x, y, width, height, DEFAULT_LABEL, entryTypeClassGetter); }
	
	/**
	 * @apiNote Do not pass any arguments into the {@code T...} array.
	 * The array servers as a "getter" utility for the generic {@link Entry} type.
	 */
	public @SuppressWarnings("unchecked") @SafeVarargs TSelectWidget
	(int x, int y, int width, int height, Text text, T... entryTypeClassGetter)
	{
		super(x, y, Math.max(width, 30), Math.max(height, 15), text); //minimum size: [30,20]
		this.entryType = (Class<T>) entryTypeClassGetter.getClass().getComponentType();
		
		this.eContextMenu.register(this.ehContextMenu);
	}
	// --------------------------------------------------
	public @Virtual @Override void tick()
	{
		//make sure this event handler is always registered
		if(!this.eContextMenu.isRegistered(this.ehContextMenu))
			this.eContextMenu.register(this.ehContextMenu);
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link Class} object representing the generic type {@code T} 
	 * that extends {@link Entry}.
	 */
	public final Class<T> getEntryType() { return this.entryType; }
	public final @Override Iterator<T> iterator() { return this.entries.iterator(); }
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		//draw the button
		pencil.drawTButton(getButtonTextureY());
		pencil.enableScissor(getX(), getY(), getEndX(), getEndY());
		pencil.drawTElementTextTH(this.text, HorizontalAlignment.LEFT);
		pencil.disableScissor();
		//draw the dropdown arrow texture
		final boolean isOpen = (contextMenu != null && contextMenu.isOpen() && contextMenu.getTarget() == this);
		(isOpen ? TEX_OPENED : TEX_CLOSED).drawTexture(pencil, getEndX() - 22, getY() + 2, 16, 16);
	}
	// ==================================================
	/**
	 * {@inheritDoc}<p>
	 * For {@link TSelectWidget}, it also creates the context menu using
	 * {@link #createContextMenu()}, and then the context menu is opened.
	 */
	protected @Virtual @Override void onClick() { super.onClick(); createContextMenu().open(); }
	
	/**
	 * {@inheritDoc}<p>
	 * For {@link TSelectWidget}, a {@link TContextMenuPanel} must always
	 * be created, and this method must never return {@code null}.
	 */
	public @Virtual @Override TContextMenuPanel createContextMenu()
	{
		//create the context menu
		final var menu = new TContextMenuPanel(this);
		
		//add entries to the context menu
		for(final T entry : this.entries)
			contextMenu.addButton(entry.getText(), __ -> setSelected(entry))
				.setTooltip(contextMenu.getTooltip());
		
		//return the context menu
		return menu;
	}
	// --------------------------------------------------
	/**
	 * Adds an {@link Entry} to this {@link TSelectWidget}.<br/>
	 * Can not be {@code null}.
	 * @param entry The {@link Entry} to add.
	 * @see Entry
	 * @see SimpleEntry
	 */
	public final boolean addEntry(T entry)
	{
		if(entry == null || this.entries.contains(entry))
			return false;
		else return this.entries.add(entry);
	}
	
	/**
	 * Removes an {@link Entry} from this {@link TSelectWidget}.
	 * @param entry The {@link Entry} to remove.
	 */
	public final boolean removeEntry(T entry) { return this.entries.remove(entry); }
	
	/**
	 * Returns the {@link Entry} that is currently selected, or
	 * {@code null} if no {@link Entry} is selected at the moment.
	 */
	public final @Nullable T getSelected() { return this.selected; }
	
	/**
	 * Sets an {@link Entry} as the "selected" {@link Entry}.
	 * @param selected The {@link Entry} to set as "selected".
	 * @throws IllegalArgumentException If This {@link TSelectWidget} does not contain the {@link Entry}.
	 * @see #getSelected()
	 */
	public final void setSelected(final @Nullable T selected) throws IllegalArgumentException { setSelected(selected, true); }
	
	/**
	 * Sets an {@link Entry} as the "selected" {@link Entry}.
	 * @param selected The {@link Entry} to set as "selected".
	 * @param invokeEvent Whether or not {@link #eSelectionChanged} should be invoked.
	 * @throws IllegalArgumentException If This {@link TSelectWidget} does not contain the {@link Entry}.
	 * @see #getSelected()
	 */
	public final void setSelected(final @Nullable T selected, boolean invokeEvent) throws IllegalArgumentException
	{
		//null check and argument check
		if(selected != null && !this.entries.contains(selected))
			throw new IllegalArgumentException("Cannot set a non-added entry as 'selected'.");
		//set selected
		this.selected = selected;
		//update text
		if(selected != null)
		{
			setText(selected.getText());
			//and invoke on-select here
			final var onSel = selected.getOnSelect();
			if(onSel != null) onSel.run();
		}
		else setText(DEFAULT_LABEL);
		//invoke events
		onSelectionChanged();
		if(invokeEvent)
			this.eSelectionChanged.invoker().invoke(this, selected);
	}
	protected @Virtual void onSelectionChanged() {}
	// ==================================================
	/**
	 * Represents a {@link TSelectWidget} dropdown menu entry.
	 */
	public static interface Entry extends ITextProvider
	{
		/**
		 * Optionally returns a {@link Tooltip} for this {@link Entry}.
		 */
		public @Nullable Tooltip getTooltip();
		
		/**
		 * Optionally returns a {@link Runnable} that will be
		 * executed when this {@link Entry} is selected.
		 */
		public @Nullable Runnable getOnSelect();
		
		/**
		 * Returns {@link #getText()} as a {@link String}, or
		 * {@code null} if {@link #getText()} also returns {@code null}.
		 */
		default String getTextAsString()
		{
			final var txt = getText();
			if(txt == null) return null;
			else return txt.getString();
		}
	}
	// --------------------------------------------------
	/**
	 * A simple {@link Entry} implementation featuring getters and setters.
	 */
	public static final class SimpleEntry implements Entry, ITextProviderSetter
	{
		protected @Nullable Text text;
		protected @Nullable Tooltip tooltip;
		protected @Nullable Runnable onSelect;
		
		public SimpleEntry(Text text) { this(text, null); }
		public SimpleEntry(Text text, Runnable onSelect)
		{
			this.text = text;
			this.onSelect = onSelect;
		}
		
		public final @Override Text getText() { return this.text; }
		public @Virtual @Override void setText(Text text) { this.text = text; }
		public final @Override Tooltip getTooltip() { return this.tooltip; }
		public final void setTooltip(Text tooltipText) { setTooltip(Tooltip.of(tooltipText)); }
		public @Virtual void setTooltip(Tooltip tooltip) { this.tooltip = tooltip; }
		public final @Override Runnable getOnSelect() { return this.onSelect; }
		public @Virtual void setOnSelect(Runnable onSelect) { this.onSelect = onSelect; }
		
		public final @Override int hashCode() { return Objects.hashCode(getTextAsString()); }
		public final @Override boolean equals(Object obj)
		{
			//basic checks
			if(obj == this) return true;
			else if(obj == null || !Objects.equals(obj.getClass(), getClass())) return false;
			//compare entry texts
			final String otherObjText = ((Entry)obj).getTextAsString();
			return Objects.equals(getTextAsString(), otherObjText);
		}
	}
	// ==================================================
	public static interface TSelectWidgetEvent_SelectionChanged<T extends TSelectWidget.Entry>
	{
		/**
		 * A {@link TEvent} that is invoked when the selection
		 * changes for a {@link TSelectWidget}.
		 * @param element The {@link TSelectWidget} whose selection got changed.
		 * @param selected The current selection after it was changed.
		 */
		public void invoke(TSelectWidget<T> element, T selected);
	}
	// ==================================================
}