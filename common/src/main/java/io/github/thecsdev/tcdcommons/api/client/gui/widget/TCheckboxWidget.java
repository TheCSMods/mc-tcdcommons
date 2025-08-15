package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorCheckboxWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

public @Virtual class TCheckboxWidget extends TButtonWidget
{
	// ==================================================
	public static final ResourceLocation SELECTED_HIGHLIGHTED_TEXTURE = AccessorCheckboxWidget.getSelectedHighlightedTexture();
	public static final ResourceLocation SELECTED_TEXTURE = AccessorCheckboxWidget.getSelectedTexture();
	public static final ResourceLocation HIGHLIGHTED_TEXTURE = AccessorCheckboxWidget.getHighlightedTexture();
	public static final ResourceLocation TEXTURE = AccessorCheckboxWidget.getTexture();
	// --------------------------------------------------
	protected boolean checked = false;
	protected boolean showText;
	protected HorizontalAlignment textAlignment;
	protected HorizontalAlignment checkboxAlignment;
	// --------------------------------------------------
	//XY positions for the checkbox box and the checkbox text
	protected int cX, cY, tX, tY;
	// ==================================================
	public TCheckboxWidget(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TCheckboxWidget(int x, int y, int width, int height, Component text) { this(x, y, width, height, text, false); }
	public TCheckboxWidget(int x, int y, int width, int height, Component text, boolean checked)
	{
		super(x, y, width, height, null);
		this.checked = checked;
		this.showText = true;
		this.textAlignment = HorizontalAlignment.LEFT;
		this.checkboxAlignment = HorizontalAlignment.LEFT;
		setText(text); //update the alignment stuff
	}
	// ==================================================
	public final boolean getChecked() { return this.checked; }
	public @Virtual void setChecked(boolean checked) { this.checked = checked; }
	//
	public final boolean getShowText() { return this.showText; }
	public @Virtual void setShowText(boolean showText) { this.showText = showText; }
	// --------------------------------------------------
	protected @Virtual @Override void onClick() { this.checked = !this.checked; super.onClick(); }
	public @Override void setText(@Nullable Component text)
	{
		super.setText(text);
		setHorizontalAlignment(this.textAlignment, this.checkboxAlignment); //update it
	}
	// --------------------------------------------------
	public final void setHorizontalAlignment(HorizontalAlignment forText, HorizontalAlignment forCheckbox)
	{
		//check
		if(forCheckbox == HorizontalAlignment.CENTER)
			forCheckbox = HorizontalAlignment.LEFT;
		
		//obtain the text renderer
		final Font tr = MC_CLIENT.font;
		
		//set
		this.textAlignment = forText;
		this.checkboxAlignment = forCheckbox;
		
		//define x1 and x2 as temp. values for the X bounds
		final Component txt = getText();
		int x1 = this.x, x2 = this.x + this.width, msgW = (txt != null) ? tr.width(txt.getVisualOrderText()) : 0;
		
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
	public @Override void render(TDrawContext pencil)
	{
		//obtain the text renderer
		final var textRenderer = getTextRenderer();
		
	    //draw the texture and the background
		pencil.drawTCheckbox(this.x + cX, this.y + cY, 20, this.height, isFocusedOrHovered(), this.checked);
	    
	    //draw the text
	    if(this.showText)
	    {
	    	if(textAlignment != HorizontalAlignment.CENTER)
	    		pencil.drawString(textRenderer, getText(), this.x + tX, this.y + tY, 0xffE0E0E0);
	    	else
	    		pencil.drawCenteredString(textRenderer, getText(), this.x + tX, this.y + tY, 0xffE0E0E0);
	    }
	}
	// ==================================================
}