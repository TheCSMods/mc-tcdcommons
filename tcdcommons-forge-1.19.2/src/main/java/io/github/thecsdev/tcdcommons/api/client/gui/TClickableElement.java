package io.github.thecsdev.tcdcommons.api.client.gui;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.events.TClickableElementEvents;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

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
	 * The text associated with this {@link TClickableElement}.
	 * For example, in case of buttons, this text is rendered
	 * on the button, and so on...
	 */
	protected @Nullable Component message;
	// --------------------------------------------------
	private final TClickableElementEvents __events = new TClickableElementEvents(this);
	// ==================================================
	public TClickableElement(int x, int y, int width, int height, @Nullable Component message)
	{
		super(x, y, width, height);
		this.enabled = true;
		this.message = message;
	}
	public @Override TClickableElementEvents getEvents() { return this.__events; }
	// --------------------------------------------------
	@Override
	public boolean getEnabled() { return this.enabled; }
	
	/**
	 * See {@link #isEnabled()}.
	 * @param enabled Whether or not this {@link TClickableElement} should be enabled.
	 */
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
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
	public @Nullable Component getMessage() { return this.message; }
	
	/**
	 * Sets the text associated with this {@link TClickableElement}.<br/>
	 * See {@link #message}.
	 * @param label The text message.
	 */
	public void setMessage(@Nullable Component label) { this.message = label; }
	// ==================================================
	@Override
	public boolean mousePressed(int mouseX, int mouseY, int button)
	{
		if(button == 0 && isHovered())
		{
			GuiUtils.playClickSound();
			onClick();
			getEvents().CLICKED.p_invoke(handler -> handler.run());
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
			GuiUtils.playClickSound();
			onClick();
			getEvents().CLICKED.p_invoke(handler -> handler.run());
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
	 * @param matrices The {@link MatrixStack}.
	 * @param mouseX The X mouse cursor position on the {@link Screen}.
	 * @param mouseY The Y mouse cursor position on the {@link Screen}.
	 * @param deltaTime The time elapsed since the last render.
	 */
	protected void renderBackground(PoseStack matrices, int mouseX, int mouseY, float deltaTime) {}
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
	protected void drawButton(PoseStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		drawButton(matrices, mouseX, mouseY, deltaTime, getButtonYImage());
	}
	
	protected void drawButton(PoseStack matrices, int mouseX, int mouseY, float deltaTime, int yImage)
	{
		drawButton(matrices, mouseX, mouseY, deltaTime, 1, 1, 1, yImage);
	}
	
	protected void drawButton(PoseStack matrices, int mouseX, int mouseY, float deltaTime, float r, float g, float b, int yImage)
	{
		//apply shader stuff
		float alpha = getAlpha();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(r, g, b, alpha);
		
		//*whatever this is*
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		//draw the background image
		{
			//old Vanilla's way of drawing buttons
			/*RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
			drawTexture(matrices, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
			drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);*/
		}
		{
			//my new way of drawing buttons. it supports
			//scalability and texture tiling as well
			RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
			draw9SliceTexture(matrices, yImage * 20, 0, 20, 20, 3);
		}
		renderBackground(matrices, mouseX, mouseY, deltaTime);
	}
	
	/**
	 * Draws {@link #getMessage()} with the default {@link HorizontalAlignment} (center).<br/>
	 * See {@link #drawMessage(MatrixStack, HorizontalAlignment, float)}.
	 * @param matrices The {@link MatrixStack}.
	 * @param deltaTime The time elapsed since the last frame.
	 */
	protected final void drawMessage(PoseStack matrices, float deltaTime)
	{
		drawMessage(matrices, HorizontalAlignment.CENTER, deltaTime);
	}
	
	/**
	 * Draws {@link #getMessage()}.
	 * @param matrices The {@link MatrixStack}.
	 * @param alignment The message text alignment.
	 * @param deltaTime The time elapsed since the last render.
	 */
	protected void drawMessage(PoseStack matrices, HorizontalAlignment alignment, float deltaTime)
	{
		drawTElementText(matrices, getMessage(), alignment, deltaTime);
	}
	// ==================================================
}