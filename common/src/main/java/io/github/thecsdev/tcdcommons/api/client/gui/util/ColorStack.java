package io.github.thecsdev.tcdcommons.api.client.gui.util;

import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * A {@link Stack} of RGBA colors.
 * This class extends the {@link Stack} class and is used to
 * store {@link Entry} objects, each representing an RGBA color.
 * @see Stack
 */
public final class ColorStack extends Stack<ColorStack.Entry>
{
	// ==================================================
	private static final long serialVersionUID = 2434143149100104895L;
	private @Nullable Entry lastCalculation; //caching to avoid re-calculations
	// ==================================================
	public final Entry push(float r, float g, float b, float a) { return push(r, g, b, a, BlendMethod.MULTIPLY); }
	public final Entry push(float r, float g, float b, float a, BlendMethod blendMethod) { return push(new Entry(r, g, b, a, blendMethod)); }
	public final Entry pushAlpha(float alpha) { return push(1, 1, 1, alpha); }
	//
	//clear last calculated entry when pushing and popping, as it no longer applies
	public final @Override Entry push(Entry item) { this.lastCalculation = null; return super.push(item); }
	public final synchronized @Override Entry pop() { this.lastCalculation = null; return super.pop(); }
	// --------------------------------------------------
	public final void apply(TDrawContext pencil) { calculate().apply(pencil); }
	public final Entry calculate()
	{
		//check if a calculation was already done before
		if(this.lastCalculation != null)
			return this.lastCalculation;
		
		//get ready to blend colors
		float red = 1, green = 1, blue = 1, alpha = 1;
		
		//iterate all entries, and blend colors
		for (final Entry entry : this)
		{
			//check the entry blend method, and blend based on that
			switch(entry.blendMethod)
			{
				case MULTIPLY:
					red *= entry.r;
					green *= entry.g;
					blue *= entry.b;
					if(entry.blendAlpha) alpha *= entry.a;
					break;
				case ADD:
					red += entry.r;
					green += entry.g;
					blue += entry.b;
					if(entry.blendAlpha) alpha += entry.a;
					break;
				case SET:
					red = entry.r;
					green = entry.g;
					blue = entry.b;
					if(entry.blendAlpha) alpha = entry.a;
					break;
				
				case SUBTRACT:
					red -= entry.r;
					green -= entry.g;
					blue -= entry.b;
					if(entry.blendAlpha) alpha -= entry.a;
					break;
				/* - i fear no man
				 * - but that thing
				 * - *looks at divisions by zero*
				 * - it scares me...
				 * case DIVIDE:
					red /= entry.r;
					green /= entry.g;
					blue /= entry.b;
					if(entry.blendAlpha) alpha /= entry.a;
					break;*/
				case SET_ALPHA:
					alpha = entry.a;
					break;
				default: break;
			}
			//this is the usual scenario. by default, Alpha is blended
			//separately using the MULTIPLY method. when "blend alpha"
			//is enabled however, this will not execute, and Alpha will
			//blend using the same method other color channels blend with
			if(!entry.blendAlpha && entry.blendMethod != BlendMethod.SET_ALPHA)
				alpha *= entry.a;
		}
		
		// Ensure the color components are in the range [0, 1]
		red = Math.max(0, Math.min(1, red));
		green = Math.max(0, Math.min(1, green));
		blue = Math.max(0, Math.min(1, blue));
		alpha = Math.max(0, Math.min(1, alpha));
		
		//assign cache and return
		return (this.lastCalculation = new Entry(red, green, blue, alpha, null));
	}
	// ==================================================
	/**
	 * Defines the methods for blending colors.
	 * <p>
	 * This enumeration is used to specify the method used to blend colors in a {@link ColorStack}.
	 * </p>
	 */
	public static enum BlendMethod { SET, ADD, SUBTRACT, MULTIPLY, /*DIVIDE,*/ SET_ALPHA }
	
	/**
	 * Represents an entry in the ColorStack.
	 * <p>
	 * Each entry consists of four float values representing the red, green,
	 * blue, and alpha channels of a color.
	 */
	public static final class Entry
	{
		/** An RGBA color channel value of this {@link Entry}. */
		public final float r,g,b,a;
		/** The blending method that will be used to blend the color values of this {@link Entry}. */
		public final @Nullable BlendMethod blendMethod;
		/** When set to true, the Alpha channel will blend using the {@link #blendMethod}. */
		public final boolean blendAlpha;
		
		public Entry() { this(1,1,1,1); }
		public Entry(float r, float g, float b, float a) { this(r, g, b, a, BlendMethod.MULTIPLY); }
		public Entry(float r, float g, float b, float a, BlendMethod blendMethod) { this(r, g, b, a, blendMethod, false); }
		public Entry(float r, float g, float b, float a, BlendMethod blendMethod, boolean blendAlpha)
		{
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
			this.blendMethod = blendMethod;
			this.blendAlpha = blendAlpha;
		}
		
		public final void apply(TDrawContext pencil) { pencil.setShaderColor(r, g, b, a); }
		/*public final @Override Entry clone()
		{
			final var newEntry = new Entry(r, g, b, a, blendMethod);
			newEntry.blendAlpha = this.blendAlpha;
			return newEntry;
		}*/
	}
	// ==================================================
}