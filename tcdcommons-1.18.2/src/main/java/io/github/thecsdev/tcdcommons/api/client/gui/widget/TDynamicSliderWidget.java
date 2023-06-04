package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

/**
 * A slider widget that allows you to define it's
 * value change listener without having to extend
 * {@link AbstractTSliderWidget}.
 */
public class TDynamicSliderWidget extends AbstractTSliderWidget
{
	// ==================================================
	protected Text label;
	protected Consumer<TDynamicSliderWidget> onValueChange;
	protected Formatting valueFormatting;
	// ==================================================
	/**
	 * Creates a {@link TDynamicSliderWidget}.
	 * @param value Ranges from 0 to 1.
	 * @param onValueChange The action that will be executed when the value of this slider is changed.
	 */
	public TDynamicSliderWidget(int x, int y, int width, int height, double value,
			@Nullable Consumer<TDynamicSliderWidget> onValueChange)
	{
		this(x, y, width, height, value, null, onValueChange);
	}
	
	/**
	 * Creates a {@link TDynamicSliderWidget}.
	 * @param value Ranges from 0 to 1.
	 * @param label See {@link #getLabel()}.
	 * @param onValueChange The action that will be executed when the value of this slider is changed.
	 */
	public TDynamicSliderWidget(
			int x, int y, int width, int height, double value,
			@Nullable Text label,
			@Nullable Consumer<TDynamicSliderWidget> onValueChange)
	{
		super(x, y, width, height, 0);
		setLabel(label);
		setOnValueChange(onValueChange);
		setValueFormatting(Formatting.WHITE);
		
		//set value to apply all of the above
		//assignments, and to apply the value
		setValue(value);
	}
	// --------------------------------------------------
	/**
	 * Returns the label text for this slider. The label
	 * is the text shown on the slider alongside it's value.<br/>
	 * For example, if the label were set to "Volume",
	 * the slider would look like this:<br/>
	 * <pre>
	 * {@code
	 * ┌─────╥──────────────────────────┐
	 * │     ║    Volume: 15%           │
	 * └─────╨──────────────────────────┘
	 * }</pre>
	 */
	public Text getLabel() { return this.label; }
	
	/**
	 * Sets the label {@link Text} of this slider.<br/>
	 * See {@link #getLabel()}.
	 * @param label The label {@link Text}.
	 */
	public TDynamicSliderWidget setLabel(@Nullable Text label)
	{
		this.label = label;
		return this;
	}
	
	/**
	 * Gets the action that will be executed when the value
	 * of this slider changes.
	 */
	@Nullable
	public Consumer<TDynamicSliderWidget> getOnValueChange() { return onValueChange; }
	
	/**
	 * Sets the action that will be executed when the value
	 * of this slider changes.
	 * @param onValueChange See above.
	 * @return this
	 */
	public TDynamicSliderWidget setOnValueChange(@Nullable Consumer<TDynamicSliderWidget> onValueChange)
	{
		this.onValueChange = onValueChange;
		return this;
	}
	
	public Formatting getValueFormatting() { return this.valueFormatting; }
	public TDynamicSliderWidget setValueFormatting(Formatting formatting)
	{
		this.valueFormatting = formatting;
		return this;
	}
	// ==================================================
	@Override
	protected void applyValue()
	{
		if(this.onValueChange != null)
			this.onValueChange.accept(this);
	}
	// --------------------------------------------------
	@Override
	protected void updateMessage()
	{
		if(valueFormatting == null) valueFormatting = Formatting.WHITE;
		int val = MathHelper.clamp((int)(this.value * 100), 0, 100);
		
		if(this.label != null)
			setMessage(TextUtils.fLiteral(label.getString() + ": " + valueFormatting.toString() + val + "%"));
		else setMessage(TextUtils.fLiteral(valueFormatting.toString() + val + "%"));
	}
	// ==================================================
}