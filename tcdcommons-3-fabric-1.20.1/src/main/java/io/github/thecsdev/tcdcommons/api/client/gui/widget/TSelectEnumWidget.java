package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.widget.TSelectEnumWidget.EnumEntry;
import io.github.thecsdev.tcdcommons.api.client.util.interfaces.ITooltipProvider;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProvider;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

/**
 * A {@link TSelectWidget} where the user selects {@link Enum} values.
 */
public @Virtual class TSelectEnumWidget<E extends Enum<E>> extends TSelectWidget<EnumEntry<E>>
{
	// ==================================================
	protected final Class<E> enumType;
	// ==================================================
	public TSelectEnumWidget(int x, int y, int width, int height, E enumValue)
	{
		this(x, y, width, height, Objects.requireNonNull(enumValue).getDeclaringClass(), DEFAULT_LABEL);
		setSelected(enumValue);
	}
	
	public TSelectEnumWidget(int x, int y, int width, int height, Class<E> enumType)
	{
		this(x, y, width, height, enumType, DEFAULT_LABEL);
	}
	
	public TSelectEnumWidget(int x, int y, int width, int height, Class<E> enumType, Text text)
	{
		//super
		super(x, y, width, height, text);
		
		//define and null-check the enum type
		this.enumType = Objects.requireNonNull(enumType);
		if(!this.enumType.isEnum())
			throw new IllegalArgumentException("Illegal " + Enum.class.getSimpleName() + " type. " +
					enumType.getName() + " is not an " + Enum.class.getSimpleName() + ".");
		
		//add enum entries using the enum type
		for(final var enumValue : this.enumType.getEnumConstants())
			addEntry(new EnumEntry<>(enumValue));
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link Class} object representing the generic type {@code E} 
	 * that extends {@link Enum}.
	 */
	public final Class<E> getEnumType() { return this.enumType; }
	// ==================================================
	/**
	 * Returns an {@link EnumEntry} that is associated with a given {@link Enum} value.
	 * Will return {@code null} if no such {@link EnumEntry} exists or if it was removed.
	 * @param enumValue The {@link Enum} value to look for in the {@link EnumEntry}s.
	 * @see EnumEntry#getEnumValue()
	 */
	public final @Nullable EnumEntry<E> entryOf(E enumValue) { return this.entries.find(e -> e.enumValue == enumValue); }
	// --------------------------------------------------
	/**
	 * Sets the selected {@link EnumEntry} using its {@link Enum} value.
	 * @throws NoSuchElementException If this {@link TSelectEnumWidget} does not have
	 * an {@link EnumEntry} that corresponds with the given {@link Enum} value.
	 * @see #entryOf(Enum)
	 * @see EnumEntry#getEnumValue()
	 */
	public final void setSelected(E enumValue) throws NoSuchElementException
	{
		final var e = entryOf(enumValue);
		if(e == null && enumValue != null)
			throw new NoSuchElementException();
		setSelected(e);
	}
	// ==================================================
	/**
	 * An {@link Enum}-based implementation of {@link TSelectWidget.Entry}.
	 * @apiNote To have your {@link Enum} entries display custom {@link Text} labels,
	 * make sure your {@link Enum} {@code implements} {@link ITextProvider}.
	 */
	public static final class EnumEntry<E extends Enum<E>> implements TSelectWidget.Entry
	{
		protected final E enumValue;
		protected final Text enumText;
		protected final @Nullable Tooltip tooltip;
		
		public EnumEntry(E enumValue)
		{
			this.enumValue = Objects.requireNonNull(enumValue);
			this.enumText = (enumValue instanceof ITextProvider) ?
					((ITextProvider)enumValue).getText() :
					literal(enumValue.name());
			this.tooltip = (enumValue instanceof ITooltipProvider) ?
					((ITooltipProvider)enumValue).getTooltip() :
					null;
		}
		public E getEnumValue() { return this.enumValue; }
		
		public final @Override Text getText() { return this.enumText; }
		public final @Override @Nullable Tooltip getTooltip() { return this.tooltip; }
		public final @Override @Nullable Runnable getOnSelect() { return null; }
	}
	// ==================================================
}