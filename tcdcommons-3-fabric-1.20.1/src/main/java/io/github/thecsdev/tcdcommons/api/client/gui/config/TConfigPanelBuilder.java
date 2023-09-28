package io.github.thecsdev.tcdcommons.api.client.gui.config;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

import java.awt.Rectangle;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.other.TLabelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TCheckboxWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.collections.HookedMap;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import net.minecraft.text.Text;

/**
 * A utility for creating config GUIs in {@link TPanelElement}s.
 */
public @Virtual class TConfigPanelBuilder<T extends TConfigPanelBuilder<T>> extends Object
{
	// ==================================================
	public static final Text TEXT_SAVE   = translatable("selectWorld.edit.save");
	public static final Text TEXT_CANCEL = translatable("gui.cancel");
	// --------------------------------------------------
	/**
	 * Holds {@link Consumer} actions that are to be invoked for each
	 * {@link TElement} that was added to the {@link #targetPanel}.
	 * @apiNote The {@link Consumer} has to accept the {@link Map} key.
	 * If it doesn't, a {@link ClassCastException} will be raised.
	 */
	protected final Map<TElement, Consumer<?>> applyActions = HookedMap.of(new WeakHashMap<>(), map ->
	{
		//remove all entries that have for whatever reason been removed from the target panel
		map.entrySet().removeIf(entry ->
		{
			final var key = entry.getKey();
			return (key == null) || (key.getParent() != getTargetPanel());
		});
	});
	protected @Nullable Runnable onSave;
	// --------------------------------------------------
	protected final TPanelElement targetPanel;
	protected @Nullable TElement lastAddedElement;
	// ==================================================
	protected TConfigPanelBuilder(TPanelElement target) throws NullPointerException
	{
		this.targetPanel = Objects.requireNonNull(target);
	}
	//
	protected final @SuppressWarnings("unchecked") T self() { return (T)this; }
	// --------------------------------------------------
	/**
	 * Creates a new {@link TConfigPanelBuilder} instance and returns it.
	 * @param target The {@link TPanelElement} onto which the config GUI will be built.
	 * @apiNote For any subclasses, {@link Override} this method by having it return the subclass type.
	 */
	public static @Virtual TConfigPanelBuilder<?> builder(TPanelElement target) { return new TConfigPanelBuilder<>(target); }
	
	/**
	 * This method's name may be a bit misleading, as all it does is call {@link #setOnSave(Runnable)}.
	 * @param onSave See {@link #setOnSave(Runnable)}.
	 * @apiNote The config GUI elements are added the moment you call "add[...]()` methods
	 * such as {@link #addLabel(Text)} for example, and not after you invoke {@link #build(Runnable)}.
	 */
	public final T build(@Nullable Runnable onSave) { setOnSave(onSave); return self(); }
	// ==================================================
	/**
	 * Returns the {@link #targetPanel} onto which the config GUI is being built.
	 */
	public final TPanelElement getTargetPanel() { return this.targetPanel; }
	
	/**
	 * Returns the {@link TElement} that was last added via this {@link TConfigPanelBuilder}.
	 */
	public final @Nullable TElement getLastAddedElement() { return this.lastAddedElement; }
	// --------------------------------------------------
	/**
	 * Sets the {@link #onSave} {@link Runnable} action that will be
	 * invoked once {@link #saveChanges()} is called, and all the config changes are applied.
	 */
	public final void setOnSave(@Nullable Runnable onSave) { this.onSave = onSave; }
	
	/**
	 * Iterates over entries in the {@link #applyActions} {@link Map},
	 * and uses them to apply and save any changes made by the user.
	 */
	@SuppressWarnings("unchecked")
	public final void saveChanges()
	{
		//iterate all elements that have apply actions tied to them,
		//and invoke those apply actions
		for(final var entry : this.applyActions.entrySet())
			((Consumer<Object>)entry.getValue()).accept(entry.getKey());
		
		//save overall config changes
		if(this.onSave != null) this.onSave.run();
	}
	// ==================================================
	/**
	 * Returns the "vertical margin" that should be applied next.
	 */
	protected @Virtual int vMargin() { return this.lastAddedElement != null ? 5 : 0; }
	// --------------------------------------------------
	public final T addLabel(Text text) { addLabelB(text); return self(); }
	public @Virtual TLabelElement addLabelB(Text text)
	{
		final var n1 = nextPanelVerticalRect(this.targetPanel);
		final var lbl = new TLabelElement(n1.x, n1.y + vMargin(), n1.width, n1.height, text);
		this.targetPanel.addChild(lbl, false);
		this.lastAddedElement = lbl; //mark the last added element
		return lbl;
	}
	// --------------------------------------------------
	public final T addCheckbox(Text text, boolean value, Consumer<TCheckboxWidget> applyValue) { addCheckboxB(text, value, applyValue); return self(); }
	public @Virtual TCheckboxWidget addCheckboxB(Text text, boolean value, Consumer<TCheckboxWidget> applyValue)
	{
		final var n1 = nextPanelVerticalRect(this.targetPanel);
		final var box = new TCheckboxWidget(n1.x, n1.y + vMargin(), n1.width, n1.height, text, value);
		box.setHorizontalAlignment(HorizontalAlignment.LEFT, HorizontalAlignment.RIGHT);
		this.targetPanel.addChild(box, false);
		this.lastAddedElement = box;            //mark the last added element
		this.applyActions.put(box, applyValue); //mark the apply action for this element
		return box;
	}
	// ==================================================
	/**
	 * Returns the next free (global) Y coordinate at which to
	 * place the next {@link TElement} that will be added to a given {@link TPanelElement}.
	 */
	public static final int nextPanelBottomY(TPanelElement panel)
	{
		@SuppressWarnings("removal")
		final TElement bottom = panel.getChildren().getTopmostElements().Item2;
		return (bottom != null) ? bottom.getEndY() : panel.getY() + panel.getScrollPadding();
	}
	
	/**
	 * Returns the next free (global-coordinate) space in the vertical
	 * direction for the next {@link TElement} that will be added to a given {@link TPanelElement}.
	 */
	public static final Rectangle nextPanelVerticalRect(TPanelElement panel)
	{
		final int sp = panel.getScrollPadding();
		return new Rectangle(panel.getX() + sp, nextPanelBottomY(panel), panel.getWidth() - (sp*2), 20);
	}
	// ==================================================
}