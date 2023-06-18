package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TClickableElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import net.minecraft.text.Text;

public class TButtonWidget extends TClickableElement
{
	// ==================================================
	protected Consumer<TButtonWidget> onClick;
	// ==================================================
	public TButtonWidget(int x, int y, int width, int height, Text message, Consumer<TButtonWidget> onClick)
	{
		super(x, y, width, height, message);
		setOnClick(onClick);
	}
	// --------------------------------------------------
	/**
	 * Returns the action that will be invoked once this
	 * {@link TButtonWidget} is pressed.
	 */
	@Nullable
	public Consumer<TButtonWidget> getOnClick() { return onClick; }
	
	/**
	 * The action that will be invoked once this {@link TButtonWidget} is pressed.
	 * @param onClick The on-click action.
	 */
	public void setOnClick(@Nullable Consumer<TButtonWidget> onClick) { this.onClick = onClick; }
	// --------------------------------------------------
	@Override
	protected void onClick()
	{
		if(this.onClick != null)
			this.onClick.accept(this);
	}
	// ==================================================
	@Override
	public void render(TDrawContext pencil, int mouseX, int mouseY, float deltaTime)
	{
		drawButton(pencil, mouseX, mouseY, deltaTime);
		pencil.drawTText(getMessage(), HorizontalAlignment.CENTER);
	}
	// ==================================================
}