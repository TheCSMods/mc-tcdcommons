package io.github.thecsdev.tcdcommons.api.util;

import static net.minecraft.util.Formatting.FORMATTING_CODE_PREFIX;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Contains various {@link Text} related utilities.<br/>
 * <i>It is not recommended to call {@link Text} related functions too
 * frequently. Instead, store {@link Text}s in a field, and access them from there.</i>
 */
public final class TextUtils
{
	// ==================================================
	private TextUtils() {}
	// ==================================================
	/**
	 * Returns a literal {@link MutableText} using the
	 * given {@link String} argument.
	 * @param text The literal text.
	 */
	public static MutableText literal(String text) { return new LiteralText(text); }
	
	/**
	 * Returns a translatable {@link MutableText} using the
	 * given translation key. The returned text will depend
	 * on the user's language settings.
	 * @param translationKey The translation key.
	 * @param params The translatable text formatting parameters.
	 */
	public static MutableText translatable(String translationKey, Object... params)
	{
		return new TranslatableText(translationKey, params);
	}
	
	/**
	 * Same as {@link #literal(String)}, except it is
	 * also formatted using {@link #formatted(String)}.
	 * @param text The literal text.
	 */
	public static MutableText fLiteral(String text) { return formatted(text); }
	
	/**
	 * Same as {@link #translatable(String, Object...)}, except it
	 * is also formatted using {@link #formatted(Text)}.
	 * @param translationKey The translation key.
	 * @param params The translatable text formatting parameters.
	 */
	public static MutableText fTranslatable(String translationKey, Object... params)
	{
		return formatted(translatable(translationKey, params));
	}
	// --------------------------------------------------
	/**
	 * Formats (styles) a given {@link MutableText} and returns
	 * the formatted {@link MutableText}.<br/>
	 * Please see {@link #formatted(String)}.
	 * @param text The {@link Text} to style/format.
	 */
	public static MutableText formatted(Text text) { return formatted(text.getString()); }
	
	/**
	 * Formats (styles) a given text and returns the formatted {@link MutableText}.
	 * The {@link Formatting#FORMATTING_CODE_PREFIX} is used to format the given text.
	 * @param text The text to format.
	 */
	public static MutableText formatted(String text) //§
	{
		//null check
		if(StringUtils.isBlank(text))
			return literal("");
		
		//define the text that will be build upon
		MutableText result = literal("");
		
		//the text must start with the special formatting symbol.
		//if it doesn't, prepend the reset character
		if(!(text.charAt(0) == '§')) text = "r" + text;
		
		//slice and iterate slices
		for(String slice : text.split(Pattern.quote("§"))/*splitStyleChars(text)*/)
		{
			//if for whatever reason, there are multiple (§)-s in a row
			if(slice.length() == 0)
			{
				result.append(Character.toString(FORMATTING_CODE_PREFIX));
				continue;
			}
			
			//look for a corresponding formatting code
			Formatting sliceFormat = Formatting.byCode(slice.charAt(0));
			if(sliceFormat == null)
			{
				result.append(literal(FORMATTING_CODE_PREFIX + slice));
				continue;
			}
			
			//remove the leftover formatting code from the slice
			slice = slice.substring(1);
			
			//append a new text with the given formatting
			result.append(literal(slice).formatted(sliceFormat));
			continue;
		}
		return result;
	}
	// ==================================================
}