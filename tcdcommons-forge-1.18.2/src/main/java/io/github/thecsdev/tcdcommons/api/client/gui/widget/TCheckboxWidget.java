package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.thecsdev.tcdcommons.api.client.gui.TClickableElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class TCheckboxWidget extends TClickableElement
{
	// ==================================================
	/**
	 * The {@link Identifier} for the GUI checkbox texture used by Minecraft.
	 */
	public static final ResourceLocation TEXTURE_CHECKBOX = new ResourceLocation("textures/gui/checkbox.png");
	// --------------------------------------------------
	protected boolean checked;
	protected boolean showMessage;
	protected HorizontalAlignment textAlignment;
	protected HorizontalAlignment checkboxAlignment;
	// --------------------------------------------------
	//XY positions for the checkbox box and the checkbox text
	protected int cX, cY, tX, tY;
	// ==================================================
	public TCheckboxWidget(int x, int y, int width, int height, MutableComponent message, boolean checked)
	{
		this(x, y, width, height, message, checked, true);
	}
	
	public TCheckboxWidget(int x, int y, int width, int height, MutableComponent message, boolean checked, boolean showMessage)
	{
		super(x, y, width, height, message);
		this.checked = checked;
		this.showMessage = showMessage;
		this.setHorizontalAlignment(HorizontalAlignment.LEFT, HorizontalAlignment.LEFT);
	}
	
	public @Override void setMessage(Component message)
	{
		super.setMessage(message);
		setHorizontalAlignment(this.textAlignment, this.checkboxAlignment); //update it
	}
	// --------------------------------------------------
	public boolean isChecked() { return checked; }
	public boolean getChecked() { return isChecked(); }
	public void setChecked(boolean checked) { this.checked = checked; }
	// --------------------------------------------------
	/**
	 * Sets the {@link HorizontalAlignment} of the
	 * text and the checkbox.
	 * @param forText The {@link HorizontalAlignment} for the checkbox text.
	 * @param forCheckbox The {@link HorizontalAlignment} for the checkbox box.<br/>
	 * Does not accept {@link HorizontalAlignment#CENTER}.
	 */
	public void setHorizontalAlignment(HorizontalAlignment forText, HorizontalAlignment forCheckbox)
	{
		//check
		if(forCheckbox == HorizontalAlignment.CENTER)
			forCheckbox = HorizontalAlignment.LEFT;
		
		//obtain the text renderer
		Minecraft minecraftClient = Minecraft.getInstance();
		Font tr = minecraftClient.font;
		
		//set
		this.textAlignment = forText;
		this.checkboxAlignment = forCheckbox;
		
		//define x1 and x2 as temp. values for the X bounds
		int x1 = this.x, x2 = this.x + this.width, msgW = tr.width(getMessage());
		
		//checkbox XY
		this.cX = (forCheckbox == HorizontalAlignment.RIGHT) ? x2 - 20 : x1;
		this.cY = this.y;
		
		//redefine x1 and x2 so as to help define the text XY
		if(forCheckbox == HorizontalAlignment.LEFT)
			x1 += 25;
		else x2 -= 25;
		
		//text XY
		switch (forText)
		{
			case LEFT: this.tX = x1; break;
			case RIGHT: this.tX = x2 - msgW; break;
			case CENTER: this.tX = ((x1 + x2) / 2) - (msgW / 2); break;
			default: this.tX = x1; break;
		}
		this.tY = (this.y + (this.height / 2)) - (tr.lineHeight / 2);
		
		//subtract
		this.tX -= this.x;
		this.tY -= this.y;
		this.cX -= this.x;
		this.cY -= this.y;
	}
	// ==================================================
	@Override
	protected void onClick() { this.checked = !this.checked; }
	// --------------------------------------------------
	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		//calc. alpha
		float alpha = getAlpha();
		
		//obtain the text renderer
		Minecraft minecraftClient = Minecraft.getInstance();
		Font textRenderer = minecraftClient.font;
		
		//apply shader stuff
	    RenderSystem.setShaderTexture(0, TEXTURE_CHECKBOX);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
	    
	    //*i still don't know what this stuff is*
	    RenderSystem.enableDepthTest();
	    RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    //RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
	    
	    //draw the texture and the background
	    drawTexture(
	    		matrices, //matrices
	    		this.x + cX, this.y + cY, //XY
	    		isFocused() ? 20 : 0, this.checked ? 20 : 0, //UV
	    		20, this.height, //WH
	    		64,64); //texture WH
	    renderBackground(matrices, mouseX, mouseY, deltaTime);
	    
	    //draw the text
	    if(this.showMessage)
	    {
	    	if(textAlignment != HorizontalAlignment.CENTER)
	    		drawTextWithShadow(matrices, textRenderer, getMessage(), this.x + tX, this.y + tY, GuiUtils.applyAlpha(0xE0E0E0, alpha));
	    	else
	    		drawCenteredText(matrices, textRenderer, getMessage(), this.x + tX, this.y + tY, GuiUtils.applyAlpha(0xE0E0E0, alpha));
	    }
	}
	// ==================================================
}