package thecsdev.tcdcommons.api.client.gui.widget;

import java.awt.Color;
import java.awt.Rectangle;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import thecsdev.tcdcommons.api.client.gui.TElement;
import thecsdev.tcdcommons.api.client.gui.events.TTextFieldWidgetEvents;
import thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import thecsdev.tcdcommons.api.util.SubjectToChange;
import thecsdev.tcdcommons.api.util.TextUtils;

/**
 * @apiNote Unfinished.
 */
@SubjectToChange(value = "Big time. This one needs a lot of attention.", when = "At any moment.")
public class TTextFieldWidget extends TElement
{
	// ==================================================
	public static final int TEXT_PADDING = 5;
	// --------------------------------------------------
	protected final StringBuilder text;
	protected int textColor;
	protected int lineSpacing;
	protected boolean multiline;
	// --------------------------------------------------
	protected MultilineText multilineText;
	// --------------------------------------------------
	private TTextFieldWidgetEvents __events = new TTextFieldWidgetEvents(this);
	// ==================================================
	public TTextFieldWidget(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		this.text = new StringBuilder();
		this.textColor = Color.WHITE.getRGB();
		this.lineSpacing = 4;
		this.multiline = false;
	}
	// --------------------------------------------------
	public @Override TTextFieldWidgetEvents getEvents() { return __events; }
	public @Override boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return true; }
	// ==================================================
	/**
	 * Returns true if this {@link TTextFieldWidget}
	 * supports multiline editing.
	 */
	public boolean getMultiline() { return this.multiline; }
	
	/**
	 * Sets {@link #getMultiline()}.
	 * @param multiline Whether or not this
	 * {@link TTextFieldWidget} will support multiline editing.
	 */
	public void setMultiline(boolean multiline) { this.multiline = multiline; }
	
	public int getLineSpacing() { return this.lineSpacing; }
	public void setLineSpacing(int spacing) { this.lineSpacing = MathHelper.clamp(spacing, 0, 20); }
	// ==================================================
	/**
	 * Refreshes the {@link #multilineText} used for
	 * rendering this {@link TTextFieldWidget}'s text.
	 * @param invokeEvent Invoke the {@link TTextFieldWidgetEvents#TEXT_CHANGED} event?
	 */
	protected final void refreshTextRender(boolean invokeEvent)
	{
		String txt = getText();
		Text message = TextUtils.literal(txt);
		int w = Math.max(getTpeWidth() - (TEXT_PADDING * 2), 10);
		this.multilineText = MultilineText.create(getTextRenderer(), message, w);
		//invoke event
		if(invokeEvent)
			getEvents().TEXT_CHANGED.p_invoke(handler -> handler.accept(txt));
	}
	// --------------------------------------------------
	/**
	 * Returns this {@link TTextFieldWidget}'s input text.
	 */
	public String getText() { return this.text.toString(); }
	
	/**
	 * Sets this {@link TTextFieldWidget}'s input text.
	 * @param text The new input text.
	 */
	public final void setText(String text) { setText(text, true); }
	
	/**
	 * Sets this {@link TTextFieldWidget}'s input text.
	 * @param text The new input text.
	 * @param invokeEvent Invoke the {@link TTextFieldWidgetEvents#TEXT_CHANGED} event?
	 */
	public void setText(String text, boolean invokeEvent)
	{
		//null check
		if(text == null) text = "";
		//handle multi-lining
		if(!getMultiline())
			text = text.replaceAll("[\\t\\n\\r]+", " ");
		//set and refresh
		this.text.setLength(0);
		this.text.append(text);
		//refresh
		refreshTextRender(invokeEvent);
	}
	
	/**
	 * Returns the height of the text being rendered on this
	 * {@link TTextFieldWidget}. The height depends on the
	 * {@link TextRenderer#fontHeight}, {@link #getLineSpacing()},
	 * as well as the number of lines of the text being rendered.
	 */
	public final int getTextHeight()
	{
		int fh = getTextRenderer().fontHeight + getLineSpacing();
		if(this.multilineText == null) return fh;
		return this.multilineText.count() * fh;
	}
	// --------------------------------------------------
	/**
	 * Writes a piece of text or a character to the input {@link #getText()}.
	 * @param text The text to write.
	 */
	public void write(String text)
	{
		//null check
		if(text == null) return;
		//handle multi-lining
		if(!getMultiline())
			text = text.replaceAll("[\\t\\n\\r]+", " ");
		//insert and move cursor
		this.text.append(text);
		//refresh
		refreshTextRender(true);
	}
	
	/**
	 * It's like {@link #write(String)}, but in reverse. Calling this method will
	 * remove characters from the {@link #getText()}.
	 * @param isDelete false if "Backspace" was pressed, and true if "Delete" was pressed.
	 */
	//very creative method name
	public void unWrite(boolean isDelete)
	{
		//don't do anything if chars is below 1 or
		//if there is no text to be removed
		if(getText().length() < 1)
			return;
		//remove last character
		this.text.deleteCharAt(this.text.length() - 1);
		//refresh
		refreshTextRender(true);
	}
	// ==================================================
	@Override
	public boolean mousePressed(int mouseX, int mouseY, int button) { return true; }
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//handle newline typing (enter and numpad enter)
		if (keyCode == 257 || keyCode == 335)
		{
			write("\n");
			return true;
		}
		//space-bar does nothing here
		if(keyCode == 32) return false;
		//arrow key pressing (cursor navigation) (left, right)
		/*if(keyCode == 263 || keyCode == 262)
			moveCursor(keyCode == 263 ? -1 : 1);*/
		//handle super
		if(TScreen.hasShiftDown() && super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		//handle character deletion (backspace and delete)
		if(keyCode == 259 || keyCode == 261)
		{
			unWrite(keyCode == 261);
			return true;
		}
		//default outcome is false
		return false;
	}
	
	public @Override boolean charTyped(char character, int modifiers)
	{
		write(Character.toString(character));
		return true;
	}
	// ==================================================
	public @Override void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		renderBackground(matrices, mouseX, mouseY, deltaTime);
		drawText(matrices, deltaTime);
	}
	
	protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
	    RenderSystem.setShaderColor(1, 1, 1, getAlpha());
		draw9SliceTexture(matrices, isFocused() ? 20 : 0, 20, 20, 20, 3);
	}
	// --------------------------------------------------
	protected void drawText(MatrixStack matrices, float deltaTime)
	{
		//null check
		if(this.multilineText == null || this.screen == null)
			return;
		//scissors
		Rectangle rr = getRenderingBoundingBox();
		if(rr == null) return; //weird, shouldn't happen, but it does.
		GuiUtils.enableScissor(this.screen.getClient(), rr.x, rr.y, rr.width, rr.height);
		//draw
		int lh = getTextRenderer().fontHeight + getLineSpacing();
		this.multilineText.draw(matrices, getTpeX() + TEXT_PADDING, getTpeY() + TEXT_PADDING, lh, textColor);
		//scissors, again
		this.screen.resetScissors();
	}
	// ==================================================
}