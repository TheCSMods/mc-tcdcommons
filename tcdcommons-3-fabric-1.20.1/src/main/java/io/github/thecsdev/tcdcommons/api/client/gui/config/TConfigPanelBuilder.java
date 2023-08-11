package io.github.thecsdev.tcdcommons.api.client.gui.config;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TElementList;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TCheckboxWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

@Beta
public @Virtual class TConfigPanelBuilder
{
	// ==================================================
	public static final Text TEXT_SAVE = translatable("selectWorld.edit.save");
	// ==================================================
	/**
	 * The target {@link TPanelElement}, on top of which
	 * the config GUI will be built.
	 */
	public final TPanelElement targetPanel;
	protected final TElementList targetPanelChildren;
	
	/**
	 * The {@link Runnable} that gets executed after applying
	 * all config changes so as to save the applied changes.
	 */
	protected @Nullable Runnable saveConfig;
	// --------------------------------------------------
	/**
	 * Keeps track of the last {@link TElement} that was
	 * added to the target {@link #targetPanel}.
	 */
	protected @Nullable TElement lastElement;
	// ==================================================
	/**
	 * @param targetPanel The target {@link TPanelElement} onto which the config GUI will be built.
	 * @param saveConfig The {@link Runnable} that will run after applying config changes.
	 * @throws NullPointerException If an argument is null. 
	 */
	public TConfigPanelBuilder(TPanelElement targetPanel, Runnable saveConfig)
	{
		this.targetPanel = Objects.requireNonNull(targetPanel);
		this.targetPanelChildren = this.targetPanel.getChildren();
		this.saveConfig = saveConfig;
	}
	// --------------------------------------------------
	/**
	 * Applies all GUI configurations made by the user
	 * and then runs {@link #saveConfig}.
	 */
	public final void applyAllConfigChanges()
	{
		//apply
		this.targetPanel.findChild(child ->
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
	 * Returns the {@link #targetPanel} on top of which the config GUI will be built.
	 */
	public final @Nullable TPanelElement getTargetPanel() { return this.targetPanel; }
	
	/**
	 * Returns the {@link #lastElement} that was added
	 * to the target {@link #targetPanel}.
	 */
	public final @Nullable TElement getLastElement() { return this.lastElement; }
	// --------------------------------------------------
	protected @Virtual int nextGlobalX() { return this.targetPanel.getX() + this.targetPanel.getScrollPadding(); }
	protected @Virtual int nextGlobalW() { return this.targetPanel.getWidth() - (this.targetPanel.getScrollPadding() * 2); }
	
	/**
	 * Returns the next Y coordinate for the next child that
	 * may get added to the target {@link #targetPanel}.
	 */
	protected @Virtual int nextGlobalY()
	{
		return (this.targetPanelChildren.size() == 0) ?
				this.targetPanel.getY() + this.targetPanel.getScrollPadding() :
				this.targetPanelChildren.getTopmostElements().Item2.getEndY() + 3;
	}
	// --------------------------------------------------
	/**
	 * Applies a tooltip {@link Text} to the last added {@link #lastElement}.
	 * @param tooltipText The tooltip {@link Text}.
	 */
	public final TConfigPanelBuilder setTooltip(Text tooltipText) { return setTooltip(Tooltip.of(tooltipText)); }
	public @Virtual TConfigPanelBuilder setTooltip(Tooltip tooltipText)
	{
		if(this.lastElement != null)
			this.lastElement.setTooltip(tooltipText);
		return this;
	}
	// ==================================================
	/**
	 * Creates and adds a new {@link TLabelElement} to the target {@link #targetPanel}.
	 * @param text The label text.
	 */
	public final TConfigPanelBuilder addLabel(final Text text) { return addLabel(text, null); }
	
	/**
	 * Creates and adds a new {@link TLabelElement} to the target {@link #targetPanel}.
	 * @param text The label text.
	 * @param alignment The {@link HorizontalAlignment} of the label text.
	 */
	public @Virtual TConfigPanelBuilder addLabel(final Text text, final HorizontalAlignment alignment)
	{
		var element = new TLabelElement(nextGlobalX(), nextGlobalY(), nextGlobalW(), 20);
		element.setText(text);
		element.setTextHorizontalAlignment(alignment != null ? alignment : HorizontalAlignment.LEFT);
		this.targetPanel.addChild(element, false);
		//return
		this.lastElement = element;
		return this;
	}
	// --------------------------------------------------
	/**
	 * Creates and adds a new {@link Boolean} checkbox to the target {@link #targetPanel}.
	 * @param text The checkbox label.
	 * @param value Will the checkbox start off as checked or unchecked?
	 * @param onToggle What will happen when the checkbox is clicked?
	 * @throws NullPointerException If an argument is null.
	 */
	public @Virtual TConfigPanelBuilder addBoolean(final Text text, final boolean value, final Consumer<Boolean> onToggle)
	{
		Objects.requireNonNull(onToggle);
		var element = new TCGB_Checkbox(nextGlobalX(), nextGlobalY(), nextGlobalW(), 20, text, value)
		{
			public void applyConfig() { onToggle.accept(this.getChecked()); }
		};
		element.setHorizontalAlignment(HorizontalAlignment.LEFT, HorizontalAlignment.RIGHT);
		this.targetPanel.addChild(element, false);
		//return
		this.lastElement = element;
		return this;
	}
	// --------------------------------------------------
	/**
	 * Adds a {@link TButtonWidget} to the target {@link #targetPanel}.
	 * @param text The {@link TButtonWidget} text.
	 * @param onClick The on-click action for the {@link TButtonWidget}.
	 */
	public @Virtual TConfigPanelBuilder addButton(Text text, Consumer<TButtonWidget> onClick)
	{
		//calculate
		int tW = ((text == null) ? 80 : Math.min(TCDCommonsClient.MC_CLIENT.textRenderer.getWidth(text), 80)) + 20;
		int nX = nextGlobalX();
		int nW = nextGlobalW();
		nX += nW;
		nW = Math.min(nW, tW);
		nX -= nW;
		//create
		var element = new TButtonWidget(nX, nextGlobalY(), nW, 20, text, onClick);
		this.targetPanel.addChild(element, false);
		//return
		this.lastElement = element;
		return this;
	}
	// ==================================================
	/**
	 * An internal interface applied to special {@link TConfigPanelBuilder} GUI
	 * elements that is used to apply the given GUI element's state to a target config.
	 */
	protected static interface TCGB_Apply { void applyConfig(); }
	// --------------------------------------------------
	protected abstract static class TCGB_Checkbox extends TCheckboxWidget implements TCGB_Apply
	{
		public TCGB_Checkbox(int x, int y, int width, int height, Text message, boolean checked)
		{
			super(x, y, width, height, message, checked);
		}
	}
	// ==================================================
}