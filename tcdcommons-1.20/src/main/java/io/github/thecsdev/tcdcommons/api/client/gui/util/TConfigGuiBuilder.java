package io.github.thecsdev.tcdcommons.api.client.gui.util;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;
import java.util.Objects;
import java.util.function.Consumer;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TCheckboxWidget;
import net.minecraft.text.Text;

/**
 * Builds config GUIs on {@link TPanelElement}s.
 */
public /*final*/ class TConfigGuiBuilder
{
	// ==================================================
	public static final Text TXT_SAVE = translatable("selectWorld.edit.save");
	// ==================================================
	/**
	 * The target {@link TPanelElement}, on top of which
	 * the config GUI will be built.
	 */
	public final TPanelElement panel;
	protected final TElementList panelChildren;
	
	/**
	 * The {@link Runnable} that gets executed after applying
	 * all config changes so as to save the applied changes.
	 */
	protected Runnable saveConfig;
	// --------------------------------------------------
	/**
	 * Keeps track of the last {@link TElement} that was
	 * added to the target {@link #panel}.
	 */
	protected TElement lastElement;
	// ==================================================
	/**
	 * @param targetPanel The target {@link TPanelElement} onto which the config GUI will be built.
	 * @param saveConfig The {@link Runnable} that will run after applying config changes.
	 * @throws NullPointerException If an argument is null. 
	 */
	public TConfigGuiBuilder(TPanelElement targetPanel, Runnable saveConfig)
	{
		this.panel = Objects.requireNonNull(targetPanel);
		this.panelChildren = this.panel.getTChildren();
		this.saveConfig = saveConfig;
	}
	// --------------------------------------------------
	protected int nextGlobalX() { return this.panel.getTpeX() + this.panel.getScrollPadding(); }
	protected int nextGlobalW() { return this.panel.getTpeWidth() - (this.panel.getScrollPadding() * 2); }
	
	/**
	 * Returns the next Y coordinate for the next child that
	 * may get added to the target {@link #panel}.
	 */
	protected int nextGlobalY()
	{
		return (this.panelChildren.size() == 0) ?
				this.panel.getTpeY() + this.panel.getScrollPadding() :
				this.panelChildren.getTopmostElements().Item2.getTpeEndY() + 3;
	}
	// --------------------------------------------------
	/**
	 * Applies all GUI configurations made by the user
	 * and then runs {@link #saveConfig}.
	 */
	public void applyAllConfigChanges()
	{
		//apply
		this.panel.forEachChild(child ->
		{
			if(child instanceof TCGB_Apply)
				((TCGB_Apply)child).applyConfig();
			return false;
		}, true);
		//save
		if(this.saveConfig != null)
			this.saveConfig.run();
	}
	// ==================================================
	/**
	 * Returns the {@link #lastElement} that was added
	 * to the target {@link #panel}.
	 */
	public final TElement getLastElement() { return this.lastElement; }
	// --------------------------------------------------
	/**
	 * Applies a tooltip {@link Text} to the last added {@link #lastElement}.
	 * @param tooltipText The tooltip {@link Text}.
	 */
	public TConfigGuiBuilder setTooltip(Text tooltipText)
	{
		if(this.lastElement != null)
			this.lastElement.setTooltip(tooltipText);
		return this;
	}
	// ==================================================
	/**
	 * Creates and adds a new {@link TLabelElement} to the target {@link #panel}.
	 * @param text The label text.
	 */
	public TConfigGuiBuilder addLabel(final Text text) { return addLabel(text, null); }
	
	/**
	 * Creates and adds a new {@link TLabelElement} to the target {@link #panel}.
	 * @param text The label text.
	 * @param alignment The {@link HorizontalAlignment} of the label text.
	 */
	public TConfigGuiBuilder addLabel(final Text text, final HorizontalAlignment alignment)
	{
		var element = new TLabelElement(nextGlobalX(), nextGlobalY(), nextGlobalW(), 20);
		element.setText(text);
		element.setHorizontalAlignment(alignment != null ? alignment : HorizontalAlignment.LEFT);
		this.panel.addTChild(element, false);
		//return
		this.lastElement = element;
		return this;
	}
	// --------------------------------------------------
	/**
	 * Creates and adds a new {@link Boolean} checkbox to the target {@link #panel}.
	 * @param text The checkbox label.
	 * @param value Will the checkbox start off as checked or unchecked?
	 * @param onToggle What will happen when the checkbox is clicked?
	 * @throws NullPointerException If an argument is null.
	 */
	public TConfigGuiBuilder addBoolean(final Text text, final boolean value, final Consumer<Boolean> onToggle)
	{
		Objects.requireNonNull(onToggle);
		var element = new TCGB_Checkbox(nextGlobalX(), nextGlobalY(), nextGlobalW(), 20, text, value)
		{
			public void applyConfig() { onToggle.accept(this.getChecked()); }
		};
		element.setDrawsVanillaButton(true);
		element.setHorizontalAlignment(HorizontalAlignment.LEFT, HorizontalAlignment.RIGHT);
		this.panel.addTChild(element, false);
		//return
		this.lastElement = element;
		return this;
	}
	// --------------------------------------------------
	/**
	 * Adds a {@link TButtonWidget} to the target {@link #panel}.
	 * @param text The {@link TButtonWidget} text.
	 * @param onClick The on-click action for the {@link TButtonWidget}.
	 */
	public TConfigGuiBuilder addButton(Text text, Consumer<TButtonWidget> onClick)
	{
		//calculate
		int tW = ((text == null) ? 80 : Math.min(this.panel.getTextRenderer().getWidth(text), 80)) + 20;
		int nX = nextGlobalX();
		int nW = nextGlobalW();
		nX += nW;
		nW = Math.min(nW, tW);
		nX -= nW;
		//create
		var element = new TButtonWidget(nX, nextGlobalY(), nW, 20, text, onClick);
		element.setDrawsVanillaButton(true);
		this.panel.addTChild(element, false);
		//return
		this.lastElement = element;
		return this;
	}
	// ==================================================
	/**
	 * An internal interface applied to special {@link TConfigGuiBuilder} GUI
	 * elements that is used to apply the given GUI element's state to a target config.
	 */
	protected static interface TCGB_Apply { void applyConfig(); }
	protected abstract static class TCGB_Checkbox extends TCheckboxWidget implements TCGB_Apply
	{
		public TCGB_Checkbox(int x, int y, int width, int height, Text message, boolean checked) {
			super(x, y, width, height, message, checked);
		}
	}
	// ==================================================
}