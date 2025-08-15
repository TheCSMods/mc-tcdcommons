package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.Direction2D;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public @Virtual class TSliderWidget extends TButtonWidget
{
	// ==================================================
	protected Direction2D sliderDirection;
	protected double value;
	protected int knobSize;
	// ==================================================
	public TSliderWidget(int x, int y, int width, int height, double value) { this(x, y, width, height, null, value); }
	public TSliderWidget(int x, int y, int width, int height, Component text, double value)
	{
		super(x, y, width, height, text);
		this.sliderDirection = Direction2D.RIGHT;
		this.value = value;
		this.knobSize = 4;
	}
	// ==================================================
	public final Direction2D getSliderDirection() { return this.sliderDirection; }
	public @Virtual void setSliderDirection(Direction2D direction)
	{
		if(direction == null) direction = Direction2D.RIGHT;
		this.sliderDirection = direction;
	}
	//
	public final double getValue() { return this.value; }
	public final void setValue(double value) { setValue(value, true); }
	public @Virtual void setValue(double value, boolean applyValue)
	{
		this.value = Mth.clamp(value, 0, 1);
		if(applyValue) click(false);
	}
	public final void setValueFromMouse(double mouseX, double mouseY)
	{
		int j = getKnobSize();
		//handle horizontal direction
		if(getSliderDirection().isHorizontal())
		{
			//assume right (positive X)
			double val = (mouseX - (this.x + j)) / (this.width - (j*2));
			//check if left
			if(getSliderDirection() == Direction2D.LEFT)
				val = 1 - Mth.clamp(val, 0, 1);
			//set value
			setValue(val);
		}
		//handle vertical direction
		else
		{
			//assume down (positive Y)
			double val = (mouseY - (this.y + j)) / (this.height - (j*2));
			//check if up
			if(getSliderDirection() == Direction2D.UP)
				val = 1 - Mth.clamp(val, 0, 1);
			//set value
			setValue(val);
		}
	}
	//
	public final int getKnobSize() { return this.knobSize; }
	public @Virtual void setKnobSize(int size)
	{
		int i0 = getSliderDirection().isHorizontal() ? getWidth() : getHeight();
		this.knobSize = Mth.clamp(size, 4, Math.max(i0, 4));
	}
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		pencil.drawTButton(false, false);
		renderSliderProgressBar(pencil);
		renderSliderKnob(pencil);
		pencil.drawTElementTextTH(this.text, HorizontalAlignment.CENTER);
	}
	// --------------------------------------------------
	/**
	 * Draws a progress bar that is used as the visual slider value indicator.<br/>
	 * Another primary use for this is this being the {@link #getSliderDirection()} indicator.
	 * @param pencil The {@link TDrawContext}.
	 */
	public @Virtual void renderSliderProgressBar(TDrawContext pencil)
	{
		//only render it if the value is > 0
		if(!(this.value > 0)) return;
		
		//define scissor parameters
		int sX = getX(), sY = getY(), sW = getWidth(), sH = getHeight();
		
		//calculate scissor parameters based on the slider direction
		switch(getSliderDirection())
		{
			case RIGHT: sW = (int)(this.value * (sW /*- (getKnobSize()*2)*/)); break;
			case DOWN: sH = (int)(this.value * (sH /*- (getKnobSize()*2)*/)); break;
			case LEFT:
				sW = (int)(this.value * (sW /*- (getKnobSize()*2)*/));
				sX = getEndX() - sW;
				break;
			case UP:
				sH = (int)(this.value * (sH /*- (getKnobSize()*2)*/));
				sY = getEndY() - sH;
				break;
			default: break;
		}
		
		//render the progress button
	    pencil.pushTShaderColor(0.8f, 0.8f, 0.8f, 1);
	    pencil.enableScissor(sX, sY, sX + sW, sY + sH);
	    pencil.drawTButton(this.enabled, isFocusedOrHovered());
	    pencil.disableScissor();
	    pencil.popTShaderColor();
	}
	// --------------------------------------------------
	/**
	 * Draws the dragabble little knob that is used as the visual slider value indicator.
	 * @param pencil The {@link TDrawContext}.
	 */
	public final void renderSliderKnob(TDrawContext pencil)
	{
		//define J-XYWH
		final int j = getKnobSize();
	    int x = 0, y = 0, w = 0, h = 0;
	    
	    //calculate
	    switch(getSliderDirection())
	    {
		    case RIGHT:
		    	x = getX() + (int)(getValue() * (getWidth() - (j*2)));
		    	y = getY();
		    	w = j*2;
		    	h = getHeight();
		    	break;
		    case LEFT:
		    	x = getEndX() - (int)(getValue() * (getWidth() - (j*2))) - j*2;
		    	y = getY();
		    	w = j*2;
		    	h = getHeight();
		    	break;
		    case DOWN:
		    	x = getX();
		    	y = getY() + (int)(getValue() * (getHeight() - (j*2)));
		    	w = getWidth();
		    	h = j*2;
		    	break;
		    case UP:
		    	x = getX();
		    	y = getEndY() - (int)(getValue() * (getHeight() - (j*2))) - j*2;
		    	w = getWidth();
		    	h = j*2;
		    	break;
	    	default: return;
	    }
	    
	    //finally draw
	    renderSliderKnob(pencil, x, y, w, h);
	}
	
	/**
	 * Draws the dragabble little knob that is used as the visual slider value indicator.
	 * @param pencil The {@link TDrawContext}.
	 * @param knobX The knob X position.
	 * @param knobY The knob Y position.
	 * @param knobWidth The knob size width.
	 * @param knobHeight The knob size height.
	 */
	public @Virtual void renderSliderKnob(TDrawContext pencil, int knobX, int knobY, int knobWidth, int knobHeight)
	{
		pencil.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_TEXTURES.get(this.enabled, isFocusedOrHovered()), knobX, knobY, knobWidth, knobHeight);
	}
	// ==================================================
	public @Virtual @Override boolean input(TInputContext inputContext)
	{
		//requires a parent screen
		if(getParentTScreen() == null) return false;
		//don't handle input if disabled
		else if(!isEnabled()) return false;
		
		//handle input based on type
		switch(inputContext.getInputType())
		{
			case MOUSE_PRESS:
				//break if the user pressed any button other than LMB
				if(inputContext.getMouseButton() != 0) break;
				//do not click yet, just return
				return true;
			case MOUSE_DRAG:
				if(inputContext.getMouseButton() != 0) break;
				final var mousePos = inputContext.getMousePosition();
				setValueFromMouse(mousePos.x, mousePos.y);
				return true;
			case MOUSE_DRAG_END:
				click(true);
				return true;
			case KEY_PRESS:
			{
				final var keyCode = inputContext.getKeyboardKey().keyCode;
				final var dir = getSliderDirection();
				boolean bl = (dir.isHorizontal()) ? (keyCode == 263 || keyCode == 262) : (keyCode == 265 || keyCode == 264);
			    if (bl)
			    {
			    	//obtain direction
			    	float f = (dir.isHorizontal()) ? (keyCode == 263 ? -1 : 1) : (keyCode == 265 ? -1 : 1);
			    	if(dir == Direction2D.LEFT || dir == Direction2D.UP) f *= -1;
			    	//slide in the obtained direction
			    	float w = dir.isHorizontal() ? getWidth() : getHeight();
			    	setValue(getValue() + (f / (w - (getKnobSize()*2))));
			    	//return
			    	return true;
			    }
			    break;
			}
			case KEY_RELEASE:
				final var keyCode = inputContext.getKeyboardKey().keyCode;
				final var dir = getSliderDirection();
				boolean bl = (dir.isHorizontal()) ? (keyCode == 263 || keyCode == 262) : (keyCode == 265 || keyCode == 264);
				if(bl)
				{
					click(true);
					return true;
				}
				break;
			default: break;
		}
		
		//if the input isn't handled, return false
		return false;
	}
	// ==================================================
}