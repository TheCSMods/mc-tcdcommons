package io.github.thecsdev.tcdcommons.api.client.gui.util;

import java.util.Stack;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;

/**
 * A {@link Stack} of {@link Text} scale values for {@link Text} rendering.
 */
public final class TextScaleStack extends Stack<TextScaleStack.Entry>
{
	// ==================================================
	private static final long serialVersionUID = -8954458726234722058L;
	private @Nullable Entry lastCalculation; //caching to avoid re-calculations
	// ==================================================
	public final Entry push(float scale) { return push(scale, BlendMethod.MULTIPLY); }
	public final Entry push(float scale, BlendMethod blendMethod) { return push(new Entry(scale, blendMethod)); }
	//
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
		float scale = 1;
		
		//iterate all entries, and blend colors
		for (final Entry entry : this)
		{
			switch (entry.blendMethod)
			{
				case MULTIPLY: scale *= entry.scale; break;
				case ADD: scale += entry.scale; break;
				case SET: scale = entry.scale; break;
				case SUBTRACT: scale -= entry.scale; break;
				default: break;
			}
		}
		
		//assign cache and return
		return (this.lastCalculation = new Entry(scale, null));
	}
	// ==================================================
	public static enum BlendMethod { SET, ADD, SUBTRACT, MULTIPLY }
	
	/**
	 * Represents an entry in the {@link TextScaleStack}.
	 */
	public static final class Entry
	{
		public final float scale;
		public final @Nullable BlendMethod blendMethod;
		
		public Entry() { this(1); }
		public Entry(float scale) { this(scale, BlendMethod.MULTIPLY); }
		public Entry(float scale, BlendMethod blendMethod)
		{
			this.scale = Math.abs(scale);
			this.blendMethod = blendMethod;
		}
		
		public final void apply(TDrawContext pencil) { pencil.textScale = Math.abs(this.scale); }
	}
	// ==================================================
}