package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.text.MutableText;

/**
 * Same as {@link TSelectWidget}, but the dropdown
 * options are enum values. 
 */
public class TSelectEnumWidget<T extends Enum<T>> extends TSelectWidget
{
	// ==================================================
	protected final Class<? extends Enum<T>> enumType;
	// --------------------------------------------------
	@Nullable protected Enum<T> selected;
	@Nullable protected Function<Enum<T>, MutableText> enumValueToLabel;
	@Nullable protected Consumer<Enum<T>> onSelectionChange;
	// ==================================================
	public TSelectEnumWidget(int x, int y, int width, int height,
			Class<? extends Enum<T>> enumType)
	{
		this(x, y, width, height, enumType, null);
	}
	
	public TSelectEnumWidget(int x, int y, int width, int height,
			Class<? extends Enum<T>> enumType,
			@Nullable Enum<T> selectedValue)
	{
		this(x, y, width, height, enumType, null, null, selectedValue);
	}
	
	public TSelectEnumWidget(int x, int y, int width, int height,
			Class<? extends Enum<T>> enumType,
			@Nullable Function<Enum<T>, MutableText> enumValueToLabel,
			@Nullable Consumer<Enum<T>> onSelectionChange,
			@Nullable Enum<T> selectedValue)
	{
		//define fields
		super(x, y, width, height);
		
		Objects.requireNonNull(enumType, "enumType must not be null.");
		this.enumType = enumType;
		
		setEnumValueToLabel(enumValueToLabel);
		setOnSelectionChange(onSelectionChange);
		
		if(selectedValue != null) setSelected(selectedValue, false);
	}
	
	protected @Override void onOptionSelected(SWEntry option) { /*message already assigned in setSelected*/ }
	// --------------------------------------------------
	public @Override void openDropdownMenu()
	{
		//check if already open
		if(isDropdownOpen()) return;
		//clear all entries
		this.entries.clear();
		//update all entries by adding in new ones
		for(Enum<T> enumValue : enumType.getEnumConstants())
			addDropdownOption(enumValueToLabel(enumValue), () -> setSelected(enumValue));
		//call super
		super.openDropdownMenu();
	}
	// ==================================================
	/**
	 * Returns the {@link #enumType} for this {@link TSelectEnumWidget}.<br/>
	 * Useful for dealing with generic types.
	 */
	public Class<? extends Enum<T>> getEnumType() { return enumType; }
	
	/**
	 * Uses {@link #getEnumValueToLabel()} to convert
	 * an a given enum value into a user-friendly label.
	 */
	public MutableText enumValueToLabel(Enum<T> enumValue)
	{
		if(enumValue == null) return TextUtils.literal("null");
		return (enumValueToLabel != null) ?
				enumValueToLabel.apply(enumValue) :
				TextUtils.literal(enumValue.toString());
	}
	// --------------------------------------------------
	/**
	 * Returns the selected enum value from the
	 * dropdown selection menu.
	 */
	@Nullable
	public Enum<T> getSelected() { return this.selected; }
	
	/**
	 * Sets the selected enum value for the dropdown selection menu.
	 * This will invoke the selection change event.
	 * @param value The new selection value.
	 */
	public void setSelected(@Nullable Enum<T> value) { setSelected(value, true); }
	
	/**
	 * Sets the selected enum value for the dropdown selection menu.
	 * You may choose if this will invoke the selection change event.
	 * @param value The new selection value.
	 * @param invokeEvent If set to true, then this will invoke the selection change event.
	 */
	public void setSelected(@Nullable Enum<T> value, boolean invokeEvent)
	{
		if(value == this.selected) return;
		this.selected = value;
		setMessage(enumValueToLabel(value));
		if(invokeEvent && this.onSelectionChange != null)
			this.onSelectionChange.accept(value);
	}
	// --------------------------------------------------
	/**
	 * Returns the function that will be used to convert
	 * the selected enum value to a user-friendly label
	 * that will be displayed on the button.
	 */
	@Nullable
	public Function<Enum<T>, MutableText> getEnumValueToLabel() { return this.enumValueToLabel; }
	
	/**
	 * Sets the {@link #enumValueToLabel}.<br/>
	 * See {@link #getEnumValueToLabel()} for more info.
	 * @param enumValueToLabel The {@link Function} that will be used.
	 */
	public void setEnumValueToLabel(@Nullable Function<Enum<T>, MutableText> enumValueToLabel)
	{
		//update the function
		this.enumValueToLabel = enumValueToLabel;
		//update the label (if an option is selected)
		Enum<T> sel = getSelected();
		if(sel != null) setMessage(enumValueToLabel(sel));
	}
	// --------------------------------------------------
	/**
	 * Returns the consumer that will be invoked when the
	 * selected value of this {@link TSelectEnumWidget} is changed.
	 */
	@Nullable
	public Consumer<Enum<T>> getOnSelectionChange() { return this.onSelectionChange; }
	
	/**
	 * Sets the {@link #onSelectionChange}.<br/>
	 * See {@link #getOnSelectionChange()} for more info.
	 * @param onSelectionChange The {@link Consumer} that will be used.
	 */
	public void setOnSelectionChange(@Nullable Consumer<Enum<T>> onSelectionChange) { this.onSelectionChange = onSelectionChange; }
	// ==================================================
}