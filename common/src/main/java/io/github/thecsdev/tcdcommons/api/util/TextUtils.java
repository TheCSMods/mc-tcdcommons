package io.github.thecsdev.tcdcommons.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

import static net.minecraft.ChatFormatting.PREFIX_CODE;

/**
 * Contains various {@link Component} related utilities.<br/>
 * <i>It is not recommended to call {@link Component} related functions too
 * frequently. Instead, store {@link Component}s in a field, and access them from there.</i>
 */
public final class TextUtils
{
	// ==================================================
	private TextUtils() {}
	// ==================================================
	/**
	 * Returns a literal {@link MutableComponent} using the given {@link String} argument.
	 * @param text The literal text.
	 */
	public static MutableComponent literal(String text) { return Component.literal(text); }
	
	/**
	 * Returns a translatable {@link MutableComponent} using the
	 * given translation key. The returned text will depend
	 * on the user's language settings.
	 * @param translationKey The translation key.
	 * @param params The translatable {@link Component} formatting parameters.
	 */
	public static MutableComponent translatable(String translationKey, Object... params)
	{
		return Component.translatable(translationKey, params);
	}
	
	/**
	 * Same as {@link #literal(String)}, except it is
	 * also formatted using {@link #formatted(String)}.
	 * @param text The literal text.
	 */
	public static MutableComponent fLiteral(String text) { return formatted(text); }
	
	/**
	 * Same as {@link #translatable(String, Object...)}, except it
	 * is also formatted using {@link #formatted(Component)}.
	 * @param translationKey The translation key.
	 * @param params The translatable text formatting parameters.
	 */
	public static MutableComponent fTranslatable(String translationKey, Object... params)
	{
		return formatted(translatable(translationKey, params));
	}
	// --------------------------------------------------
	/**
	 * Formats (styles) a given {@link MutableComponent} and returns
	 * the formatted {@link MutableComponent}.<br/>
	 * Please see {@link #formatted(String)}.
	 * @param text The {@link Component} to style/format.
	 */
	public static MutableComponent formatted(Component text) { return formatted(text.getString()); }
	
	/**
	 * Formats (styles) a given text and returns the formatted {@link MutableComponent}.
	 * The {@link ChatFormatting#PREFIX_CODE} is used to format the given text.
	 * @param text The text to format.
	 */
	public static MutableComponent formatted(String text) //§
	{
		//null check
		if(StringUtils.isBlank(text))
			return literal("");
		
		//define the text that will be build upon
		MutableComponent result = literal("");
		
		//the text must start with the special formatting symbol.
		//if it doesn't, prepend the reset character
		if(!(text.charAt(0) == '\u00A7')) text = "r" + text;
		
		//slice and iterate slices
		for(String slice : text.split(Pattern.quote("§"))/*splitStyleChars(text)*/)
		{
			//if for whatever reason, there are multiple (§)-s in a row
			if(slice.length() == 0)
			{
				result.append(Character.toString(PREFIX_CODE));
				continue;
			}
			
			//look for a corresponding formatting code
			ChatFormatting sliceFormat = ChatFormatting.getByCode(slice.charAt(0));
			if(sliceFormat == null)
			{
				result.append(literal(PREFIX_CODE + slice));
				continue;
			}
			
			//remove the leftover formatting code from the slice
			slice = slice.substring(1);
			
			//append a new text with the given formatting
			result.append(literal(slice).withStyle(sliceFormat));
			continue;
		}
		return result;
	}
	// ==================================================
}