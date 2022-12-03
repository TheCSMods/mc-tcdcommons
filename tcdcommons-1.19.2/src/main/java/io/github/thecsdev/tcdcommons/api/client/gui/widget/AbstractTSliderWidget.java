package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.thecsdev.tcdcommons.api.client.gui.TClickableElement;
import io.github.thecsdev.tcdcommons.api.client.gui.events.TSliderWidgetEvents;
import io.github.thecsdev.tcdcommons.api.client.gui.util.Direction2D;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractTSliderWidget extends TClickableElement
{
	// ==================================================
	protected static final int BUTTON_Y_ENABLED = getButtonYImage(true, false);
	protected static final int BUTTON_Y_DISABLED = getButtonYImage(false, false);
	// --------------------------------------------------
	/**
	 * The direction in which the slider will slide.
	 */
	protected Direction2D sliderDirection;
	
	/**
	 * The current value of the slider.
	 */
	protected double value;
	
	/**
	 * The size of the slider's knob drawn in
	 * {@link #drawSliderKnob(MatrixStack, int, int, float)}.
	 */
	protected int knobSize;
	
	/**
	 * When set to true, {@link #getMessage()} will be
	 * drawn on this slider.
	 */
	protected boolean drawMessage;
	// --------------------------------------------------
	private TSliderWidgetEvents __events = new TSliderWidgetEvents(this);
	// ==================================================
	public AbstractTSliderWidget(int x, int y, int width, int height, double value)
	{
		super(x, y, width, height, null);
		setSliderDirection(Direction2D.RIGHT);
		setValue(value, false);
		setKnobSize(4);
		setDrawMessage(true);
	}
	
	@Override
	public TSliderWidgetEvents getEvents() { return this.__events; }
	// --------------------------------------------------
	@Override
	public void setPosition(int x, int y, int flags)
	{
		super.setPosition(x, y, flags);
		if(screen != null && screen.getDraggingTChild() == this)
			this.setValueFromMouse(screen.getMouseX(), screen.getMouseY());
	}
	// ==================================================
	/**
	 * Returns the value of this {@link AbstractTSliderWidget}
	 * ranging from 0 to 1.
	 */
	public double getValue() { return this.value; }
	
	/**
	 * Sets the value of this {@link AbstractTSliderWidget},
	 * and then applies the value using {@link #applyValue()}.
	 * @param value The value ranging from 0 to 1.
	 */
	public boolean setValue(double value) { return setValue(value, true); }
	
	/**
	 * Sets the value of this {@link AbstractTSliderWidget}
	 * white letting you choose whether it will get applied or not.
	 * @param value The value ranging from 0 to 1.
	 * @param applyValue Will {@link #applyValue()} be called?
	 */
	public boolean setValue(double value, boolean applyValue)
	{
		double oldValue = this.value;
		this.value = MathHelper.clamp(value, 0, 1);
		boolean changed = oldValue != this.value;
		
		if(applyValue && changed)
		{
			applyValue();
			//handle value change events only in here:
			getEvents().VALUE_CHANGED.p_invoke(handler -> handler.accept(this.value));
		}
		updateMessage();
		return changed;
	}
	
	/**
	 * Sets the value of the slider using the mouse cursor.<br/>
	 * Mainly used by {@link #mouseDragged(double, double, double, double, int)}.
	 * @param mouseX The cursor X position.
	 * @param mouseY The cursor Y position.
	 */
	public void setValueFromMouse(double mouseX, double mouseY)
	{
		int j = getKnobSize();
		//handle horizontal direction
		if(getSliderDirection().isHorizontal())
		{
			//assume right (positive X)
			double val = (mouseX - (this.x + j)) / (this.width - (j*2));
			//check if left
			if(getSliderDirection() == Direction2D.LEFT)
				val = 1 - MathHelper.clamp(val, 0, 1);
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
				val = 1 - MathHelper.clamp(val, 0, 1);
			//set value
			setValue(val);
		}
	}
	// --------------------------------------------------
	/**
	 * Called by {@link #setValue(double, boolean)}.<br/>
	 * Use this to apply the value however you'd like.
	 */
	protected abstract void applyValue();
	
	/**
	 * Called by {@link #setValue(double, boolean)}.<br/>
	 * Use {@link #setMessage(net.minecraft.text.MutableText)}
	 * to update the text shown on this {@link AbstractTSliderWidget}.
	 */
	protected abstract void updateMessage();
	// --------------------------------------------------
	/**
	 * Returns the {@link #knobSize}.
	 */
	public int getKnobSize() { return this.knobSize; }
	
	/**
	 * Sets the {@link #knobSize}.
	 * @param size The knob size.
	 */
	public void setKnobSize(int size)
	{
		int i0 = getSliderDirection().isHorizontal() ? getTpeWidth() : getTpeHeight();
		this.knobSize = MathHelper.clamp(size, 4, Math.max(i0, 4));
	}
	// --------------------------------------------------
	/**
	 * Returns the direction in which this slider will slide.<br/>
	 * See {@link #sliderDirection}.
	 */
	public Direction2D getSliderDirection() { return this.sliderDirection; }
	
	/**
	 * Sets the slider direction.<br/>
	 * See {@link #getSliderDirection()} for more info.
	 * @param direction The new slider direction.
	 * @throws NullPointerException When the argument is null.
	 */
	public void setSliderDirection(Direction2D direction)
	{
		Objects.requireNonNull(direction, "direction must not be null.");
		this.sliderDirection = direction;
	}
	// --------------------------------------------------
	/**
	 * Returns {@link #drawMessage}. It is set to true when
	 * {@link #getMessage()} should be drawn.
	 */
	public boolean getDrawMessage() { return this.drawMessage; }
	
	/**
	 * Sets {@link #drawMessage}.
	 * @param draw Whether or not {@link #getMessage()} should be drawn.
	 */
	public void setDrawMessage(boolean draw) { this.drawMessage = draw; }
	// ==================================================
	@Override protected void onClick() {}
	// --------------------------------------------------
	@Override
	public boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return true; }
	// --------------------------------------------------
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		Direction2D dir = getSliderDirection();
		boolean bl = (dir.isHorizontal()) ? (keyCode == 263 || keyCode == 262) : (keyCode == 265 || keyCode == 264);
	    if (bl)
	    {
	    	//obtain direction
	    	float f = (dir.isHorizontal()) ? (keyCode == 263 ? -1 : 1) : (keyCode == 265 ? -1 : 1);
	    	if(dir == Direction2D.LEFT || dir == Direction2D.UP) f *= -1;
	    	//slide in the obtained direction
	    	float w = dir.isHorizontal() ? getTpeWidth() : getTpeHeight();
	    	setValue(getValue() + (f / (w - (getKnobSize()*2))));
	    	//return
	    	return true;
	    }
	    return false;
	}
	// --------------------------------------------------
	@Override
	public boolean mousePressed(int mouseX, int mouseY, int button)
	{
		if(button != 0) return false;
		setValueFromMouse(mouseX, mouseY);
		if(this.screen != null) this.screen.setFocusedTChild(this);
		return false;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, double deltaX, double deltaY, int button)
	{
		if(button != 0) return false;
		setValueFromMouse(mouseX, mouseY);
		return true;
	}
	
	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button)
	{
		if(button != 0) return false;
		GuiUtils.playClickSound();
		return true;
	}
	
	@Override
	public boolean mouseScrolled(int mouseX, int mouseY, int amount)
	{
		double dAmount = MathHelper.clamp(amount, -1, 1);
		double divH = (getTpeWidth() - (getKnobSize()*2));
		double divV = (getTpeHeight() - (getKnobSize()*2));
		switch(getSliderDirection())
		{
			case RIGHT: setValue(getValue() + (dAmount / divH)); break;
			case DOWN: setValue(getValue() + (dAmount / divV)); break;
			case UP: setValue(getValue() + (-dAmount / divV)); break;
			case LEFT: setValue(getValue() + (-dAmount / divH)); break;
			default: return false;
		}
		return true;
	}
	// ==================================================
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		drawButton(matrices, mouseX, mouseY, deltaTime, BUTTON_Y_DISABLED);
		drawSliderProgressBar(matrices, mouseX, mouseY, deltaTime);
		drawSliderKnob(matrices, mouseX, mouseY, deltaTime);
		if(getDrawMessage()) drawMessage(matrices, deltaTime);
	}
	// --------------------------------------------------
	/**
	 * Draws a progress bar that is used as the visual slider value indicator.
	 * Another primary use for this is this being the {@link #getSliderDirection()} indicator.
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the TScreen.
	 * @param mouseY The Y mouse cursor position on the TScreen.
	 * @param deltaTime The time elapsed since the last render.
	 */
	protected void drawSliderProgressBar(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		if(this.screen != null && this.value > 0)
		{
			//define scissor parameters
			int sX = getTpeX();
			int sY = getTpeY();
			int sW = getTpeWidth();
			int sH = getTpeHeight();
			//calculate scissor parameters based on the slider direction
			switch(getSliderDirection())
			{
				case RIGHT: sW = (int)(this.value * (sW /*- (getKnobSize()*2)*/)); break;
				case DOWN: sH = (int)(this.value * (sH /*- (getKnobSize()*2)*/)); break;
				case LEFT:
					sW = (int)(this.value * (sW /*- (getKnobSize()*2)*/));
					sX = getTpeEndX() - sW;
					break;
				case UP:
					sH = (int)(this.value * (sH /*- (getKnobSize()*2)*/));
					sY = getTpeEndY() - sH;
					break;
				default: break;
			}
			//render the progress button
			RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
		    RenderSystem.setShaderColor(0.6f, 0.6f, 0.6f, getAlpha());
		    draw9SliceTexture(matrices, sX, sY, sW, sH, 20, 0, 20, 20, 256, 256, 3);
		}
	}
	// --------------------------------------------------
	/**
	 * Draws the dragabble little knob that is used as
	 * the visual slider value indicator.
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the TScreen.
	 * @param mouseY The Y mouse cursor position on the TScreen.
	 * @param deltaTime The time elapsed since the last render.
	 */
	protected final void drawSliderKnob(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		/* OLD VANILLA SYSTEM:
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
	    RenderSystem.setShaderColor(1, 1, 1, getAlpha());
	    int i = (isFocusedOrHovered() ? 2 : 1) * 20;
	    int j = getKnobSize();
	    drawTexture(matrices, this.x + (int)(this.value * (this.width - (j*2))), this.y, j, this.height, 0, 46 + i, 4, 20, 256, 256);
	    drawTexture(matrices, this.x + (int)(this.value * (this.width - (j*2))) + j, this.y, j, this.height, 196, 46 + i, 4, 20, 256, 256);*/
		
		//=========== new system for drawing the knob
		//RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
	    //RenderSystem.setShaderColor(1, 1, 1, getAlpha());
	    //int uvU = (!isFocusedOrHovered() ? 1 : 2) * 20;
	    int j = getKnobSize();
	    
	    //define XYWH
	    int x = 0, y = 0, w = 0, h = 0;
	    
	    switch(getSliderDirection())
	    {
		    case RIGHT:
		    	x = getTpeX() + (int)(getValue() * (getTpeWidth() - (j*2))); y = getTpeY();
		    	w = j*2; h = getTpeHeight();
		    	//draw9SliceTexture(matrices, getTpeX() + (int)(this.value * (this.width - (j*2))), this.y, j*2, this.height, uvU, 0, 20, 20, 256, 256, 3);
		    	break;
		    case LEFT:
		    	x = getTpeEndX() - (int)(getValue() * (getTpeWidth() - (j*2))) - j*2; y = getTpeY();
		    	w = j*2; h = getTpeHeight();
		    	//draw9SliceTexture(matrices, getTpeEndX() - (int)(this.value * (this.width - (j*2))) - j*2, this.y, j*2, this.height, uvU, 0, 20, 20, 256, 256, 3);
		    	break;
		    case DOWN:
		    	x = getTpeX(); y = getTpeY() + (int)(getValue() * (getTpeHeight() - (j*2)));
		    	w = getTpeWidth(); h = j*2;
		    	//draw9SliceTexture(matrices, this.x, getTpeY() + (int)(this.value * (this.height - (j*2))), this.width, j*2, uvU, 0, 20, 20, 256, 256, 3);
		    	break;
		    case UP:
		    	x = getTpeX(); y = getTpeEndY() - (int)(getValue() * (getTpeHeight() - (j*2))) - j*2;
		    	w = getTpeWidth(); h = j*2;
		    	//draw9SliceTexture(matrices, this.x, getTpeEndY() - (int)(this.value * (this.height - (j*2))) - j*2, this.width, j*2, uvU, 0, 20, 20, 256, 256, 3);
		    	break;
	    	default: return;
	    }
	    
	    //finally draw
	    drawSliderKnob(matrices, mouseX, mouseY, deltaTime, x, y, w, h);
	}
	
	/**
	 * Draws the dragabble little knob that is used as
	 * the visual slider value indicator.
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the TScreen.
	 * @param mouseY The Y mouse cursor position on the TScreen.
	 * @param deltaTime The time elapsed since the last render.
	 * @param x The knob X position.
	 * @param y The knob Y position.
	 * @param width The knob size width.
	 * @param height The knob size height.
	 */
	protected void drawSliderKnob(MatrixStack matrices,
			int mouseX, int mouseY, float deltaTime,
			int x, int y, int width, int height)
	{
		//calculate stuff
	    int uvU = (!isFocusedOrHovered() ? 1 : 2) * 20;
	    
	    //draw the 9slice
	    RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
	    RenderSystem.setShaderColor(1, 1, 1, getAlpha());
		draw9SliceTexture(matrices, x, y, width, height, uvU, 0, 20, 20, 256, 256, 3);
	}
	// ==================================================
}