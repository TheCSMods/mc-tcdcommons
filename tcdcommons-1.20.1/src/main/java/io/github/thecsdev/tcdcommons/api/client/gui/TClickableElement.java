package io.github.thecsdev.tcdcommons.api.client.gui;

import org.jetbrains.annotations.Nullable;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class TClickableElement extends TElement
{
	// ==================================================
	/**
	 * Whether this {@link TClickableElement} is enabled or not.
	 * When disabled, the user will not be able to interact with
	 * this {@link TClickableElement}.
	 */
	protected boolean enabled;

	/**
	 * When set to true, {@link #drawButton(TDrawContext, int, int, float, int)}
	 * will draw buttons using the Vanilla style and textures.<br/><br/>
	 * <b>Note:</b> Vanilla buttons do not support button heights that are not 20px.
	 */
	protected boolean drawsVanillaButton;
	
	/**
	 * The text associated with this {@link TClickableElement}.
	 * For example, in case of buttons, this text is rendered
	 * on the button, and so on...
	 */
	protected @Nullable Text message;
	// --------------------------------------------------
	public Event<TClickableElementEvent_Clicking> eClicking = EventFactory.createEventResult();
	public Event<TClickableElementEvent_Clicked> eClicked = EventFactory.createLoop();
	// ==================================================
	public TClickableElement(int x, int y, int width, int height, @Nullable Text message)
	{
		super(x, y, width, height);
		this.enabled = true;
		this.drawsVanillaButton = false;
		this.message = message;
	}
	// --------------------------------------------------
	@Override
	public boolean getEnabled() { return this.enabled; }
	
	/**
	 * See {@link #isEnabled()}.
	 * @param enabled Whether or not this {@link TClickableElement} should be enabled.
	 */
	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	/**
	 * Returns {@link #drawsVanillaButton}.
	 * @see #setDrawsVanillaButton(boolean)
	 */
	public boolean getDrawsVanillaButton() { return this.drawsVanillaButton; }
	
	/**
	 * Sets {@link #drawsVanillaButton}.
	 * @param drawsVanillaBtn Whether or not to make this widget use the vanilla way of drawing buttons.
	 */
	public void setDrawsVanillaButton(boolean drawsVanillaBtn) { this.drawsVanillaButton = drawsVanillaBtn; }
	// --------------------------------------------------
	/**
	 * Called by {@link #mousePressed(int, int, int)} and
	 * {@link #keyPressed(int, int, int)} whenever this
	 * element is clicked or pressed.
	 */
	protected abstract void onClick();
	// --------------------------------------------------
	/**
	 * Returns the text associated with this {@link TClickableElement}.<br/>
	 * See {@link #message}.
	 */
	public @Nullable Text getMessage() { return this.message; }
	
	/**
	 * Sets the text associated with this {@link TClickableElement}.<br/>
	 * See {@link #message}.
	 * @param label The text message.
	 */
	public void setMessage(@Nullable Text label) { this.message = label; }
	// ==================================================
	@Override
	public boolean mousePressed(int mouseX, int mouseY, int button)
	{
		if(button == 0 && isHovered())
		{
			if(this.eClicking.invoker().invoke(this).isFalse())
				return false;
			GuiUtils.playClickSound();
			onClick();
			this.eClicked.invoker().invoke(this);
			//getEvents().CLICKED.p_invoke(handler -> handler.run());
			return true;
		}
		return false;
	}
	// --------------------------------------------------
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//257 - ENTER; 335 - NUMPAD ENTER; 32 - SPACE;
		if (keyCode == 257 || keyCode == 335 || keyCode == 32)
		{
			if(this.eClicking.invoker().invoke(this).isFalse())
				return false;
			GuiUtils.playClickSound();
			onClick();
			this.eClicked.invoker().invoke(this);
			//getEvents().CLICKED.p_invoke(handler -> handler.run());
			return true;
		}
		return false;
	}
	// ==================================================
	/**
	 * Renders the background for this {@link TClickableElement}.
	 * By default, it does nothing, but you may override it and make it do something.<br/>
	 * <br/>
	 * Primarily called by {@link #drawButton(MatrixStack, int, int, float, int)}
	 * so as to let you define a custom button background.
	 * @param pencil The {@link TDrawContext}.
	 * @param mouseX The X mouse cursor position on the {@link Screen}.
	 * @param mouseY The Y mouse cursor position on the {@link Screen}.
	 * @param deltaTime The time elapsed since the last render.
	 */
	protected void renderBackground(TDrawContext pencil, int mouseX, int mouseY, float deltaTime) {}
	// --------------------------------------------------
	/**
	 * See {@link #getButtonYImage(boolean, boolean)}.<br/>
	 * This method uses {@link #isEnabled()} and {@link #isFocusedOrHovered()} as arguments.
	 */
	public final int getButtonYImage() { return getButtonYImage(isEnabled(), isFocusedOrHovered()); }
	
	/**
	 * Used by {@link #drawButton(MatrixStack, int, int, float)} to
	 * obtain the button texture image depending on whether the button
	 * is enabled and hovered.
	 * @param enabled Is the button enabled?
	 * @param hovered Is the button hovered?
	 */
	public static int getButtonYImage(boolean enabled, boolean hovered)
	{
		if(!enabled) return 0;
		else if(hovered) return 2;
		else return 1;
	  }
	// ==================================================
	protected void drawButton(TDrawContext pencil, int mouseX, int mouseY, float deltaTime)
	{
		drawButton(pencil, mouseX, mouseY, deltaTime, getButtonYImage());
	}
	
	protected void drawButton(TDrawContext pencil, int mouseX, int mouseY, float deltaTime, int yImage)
	{
		//apply shader stuff
		pencil.setShaderColor(1, 1, 1, getAlpha());
		
		//draw the background image
		if(this.drawsVanillaButton)
		{
			//old Vanilla's way of drawing buttons
			pencil.drawTexture(WIDGETS_TEXTURE, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
			pencil.drawTexture(WIDGETS_TEXTURE, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);
		}
		//my new way of drawing buttons. it supports
		//scalability and texture tiling as well
		else pencil.drawTNineSlicedTexture(T_WIDGETS_TEXTURE, yImage * 20, 0, 20, 20, 3);
		
		//render button background
		renderBackground(pencil, mouseX, mouseY, deltaTime);
	}
	// ==================================================
	public interface TClickableElementEvent_Clicking { public EventResult invoke(TClickableElement element); }
	public interface TClickableElementEvent_Clicked { public void invoke(TClickableElement element); }
	// ==================================================
}