package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.interfaces.ITextProviderSetter;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public @Virtual class TTextFieldWidget extends TClickableWidget implements ITextProviderSetter
{
	// ==================================================
	protected String __text = "";
	protected Text __displayText = literal("");
	protected int __displayTextWidth = 0;
	// --------------------------------------------------
	public final TEvent<TTextFieldWidgetEvent_TextChanged> eTextChanged = TEventFactory.createLoop();
	// ==================================================
	public TTextFieldWidget(int x, int y, int width, int height) { super(x, y, width, height); }
	// --------------------------------------------------
	public @Virtual @Override boolean isFocusable() { return true; }
	protected @Virtual @Override void onClick() {}
	// --------------------------------------------------
	/**
	 * <b>Important note:</b> This returns the display text for this {@link TTextFieldWidget}.
	 * <p>
	 * To see the user input text, use {@link #getInput()}.
	 * @see #getInput()
	 */
	public final @Internal @Override Text getText() { return this.__displayText; }
	public final @Internal @Override void setText(Text text) { setInput((text != null) ? text.getString() : ""); }
	//
	public final String getInput() { return this.__text; }
	public final void setInput(String text) { setInput(text, true); }
	public final void setInput(String text, boolean invokeEvent)
	{
		if(text == null) text = "";
		this.__text = text;
		this.__displayText = literal(text);
		this.__displayTextWidth = TCDCommonsClient.MC_CLIENT.textRenderer.getWidth(this.__displayText);
		if(invokeEvent) this.eTextChanged.invoker().invoke(this, this.__text);
	}
	// ==================================================
	public @Virtual @Override boolean input(TInputContext inputContext)
	{
		//requires a parent screen
		if(getParentTScreen() == null) return false;
		//don't handle input if disabled
		else if(!this.enabled) return false;
		
		//handle inputs
		switch(inputContext.getInputType())
		{
			case CHAR_TYPE: return inputChar(inputContext.getTypedChar());
			case KEY_PRESS:
				//obtain key-code
				final var keyCode = inputContext.getKeyboardKey().keyCode;
				//handle backspace/delete keys
				if(keyCode == 259 || keyCode == 261)
					inputBackspace(keyCode == 261);
				//handle Shift+Enter key presses
				else if(Screen.hasShiftDown() && (keyCode == 257 || keyCode == 335))
					click(false);
				//if nothing handles the key-press, return false
				else return false;
				return true; //if the click was handled, return true
			case MOUSE_CLICK:
				//break if the user pressed any button other than LMB
				if(inputContext.getMouseButton() != 0) break;
				//click and return
				click(false);
				return true;
			default: break;
		}
		
		//return false if the input wasn't handled
		return false;
	}
	// --------------------------------------------------
	/**
	 * Writes a single {@link Character} to the {@link #getInput()} text.
	 * @param character The {@link Character} to write.
	 */
	public final boolean inputChar(char character) { return inputText(Character.toString(character)); }
	
	/**
	 * Writes a {@link String} of text to the {@link #getInput()} text.
	 * @param text The {@link String} of text to write.
	 */
	public @Virtual boolean inputText(String text)
	{
		setInput(this.__text + text);
		return true;
	}
	// --------------------------------------------------
	/**
	 * Inputs a single backspace/delete key command,
	 * erasing a single {@link Character} from the {@link #getInput()} text.
	 * @param isDelete If true, the delete key is used; If false, the backspace key is used;
	 */
	public final boolean inputBackspace(boolean isDelete)
	{
		//if there's no text (not even white space), return false
		if(StringUtils.isEmpty(this.__text)) return false;
		//(delete currently unsupported; requires cursor navigation;)
		else if(isDelete) return false;
		
		//remove last character
		setInput(this.__text.substring(0, this.__text.length() - 1));
		
		//return
		return true;
	}
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		pencil.drawTFill(-16777216);
		renderText(pencil);
		pencil.disableScissor();
	}
	
	/**
	 * Renders the input text of this {@link TTextFieldWidget}.
	 * @param pencil The {@link TDrawContext}.
	 * @see #getText()
	 * @see #getInput()
	 */
	protected @Virtual void renderText(TDrawContext pencil)
	{
		pencil.enableScissor(getX(), getY(), getEndX(), getEndY());
		final var align = (this.__displayTextWidth < (getWidth() - 6)) ?
				HorizontalAlignment.LEFT : HorizontalAlignment.RIGHT;
		pencil.drawTElementTextTH(this.__displayText, align);
	}
	
	public @Virtual @Override void postRender(TDrawContext pencil)
	{
		if(getParentTScreen().getFocusedElement() == this) pencil.drawTBorder(-1);
		else pencil.drawTBorder(TPanelElement.COLOR_OUTLINE);
	}
	// ==================================================
	public static interface TTextFieldWidgetEvent_TextChanged { public void invoke(TTextFieldWidget element, String newText); }
	// ==================================================
}