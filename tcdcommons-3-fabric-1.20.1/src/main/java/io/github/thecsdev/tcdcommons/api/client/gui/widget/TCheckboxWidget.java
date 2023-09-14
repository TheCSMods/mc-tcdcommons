package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public @Virtual class TCheckboxWidget extends TButtonWidget
{
	// ==================================================
	/**
	 * The {@link Identifier} for the GUI checkbox texture used by Minecraft.
	 */
	public static final Identifier TEXTURE_CHECKBOX = new Identifier("textures/gui/checkbox.png");
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
	public TCheckboxWidget(int x, int y, int width, int height, Text text) { this(x, y, width, height, text, false); }
	public TCheckboxWidget(int x, int y, int width, int height, Text text, boolean checked)
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
	public @Override void setText(@Nullable Text text)
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
		final TextRenderer tr = MC_CLIENT.textRenderer;
		
		//set
		this.textAlignment = forText;
		this.checkboxAlignment = forCheckbox;
		
		//define x1 and x2 as temp. values for the X bounds
		final Text txt = getText();
		int x1 = this.x, x2 = this.x + this.width, msgW = (txt != null) ? tr.getWidth(txt.asOrderedText()) : 0;
		
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
		this.tY = (this.y + (this.height / 2)) - (tr.fontHeight / 2);
		
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
		final boolean isFocused = (getParentTScreen().getFocusedElement() == this);
	    pencil.drawTexture(
	    		TEXTURE_CHECKBOX,
	    		this.x + cX, this.y + cY, //XY
	    		isFocused ? 20 : 0, this.checked ? 20 : 0, //UV
	    		20, this.height, //WH
	    		64,64); //texture WH
	    
	    //draw the text
	    if(this.showText)
	    {
	    	if(textAlignment != HorizontalAlignment.CENTER)
	    		pencil.drawTextWithShadow(textRenderer, getText(), this.x + tX, this.y + tY, 0xE0E0E0);
	    	else
	    		pencil.drawCenteredTextWithShadow(textRenderer, getText(), this.x + tX, this.y + tY, 0xE0E0E0);
	    }
	}
	// ==================================================
}