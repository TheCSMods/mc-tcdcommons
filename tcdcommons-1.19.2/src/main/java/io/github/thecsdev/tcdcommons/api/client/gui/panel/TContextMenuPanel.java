package io.github.thecsdev.tcdcommons.api.client.gui.panel;

import java.awt.Rectangle;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.FocusOrigin;
import io.github.thecsdev.tcdcommons.api.client.gui.util.GuiUtils;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * A context menu.<br/>
 * <br/>
 * <b>Note:</b> There can only be one context menu open per screen,
 * and the context menu parent <b>has to be</b> the screen itself.
 */
public class TContextMenuPanel extends TPanelElement
{
	// ==================================================
	protected static final int BORDER_COLOR = 1358954495;
	// ==================================================
	public TContextMenuPanel(int x, int y, int width)
	{
		super(x, y, width, 0);
		setZOffset(50);
		setScrollPadding(0);
		setScrollFlags(SCROLL_VERTICAL);
	}
	// --------------------------------------------------
	public @Override @Nullable Rectangle getRenderingBoundingBox()
	{
		//for menus, the parent has to be the screen itself
		if(getTParent() != this.screen) return null;
		return super.getRenderingBoundingBox();
	}
	// --------------------------------------------------
	public @Override boolean canBeAddedTo(TParentElement parent) { return (parent instanceof TScreen); }
	// --------------------------------------------------
	public @Override void onParentChanged()
	{
		if(this.screen == null) return;
		this.screen.getTChildren().removeIf(child ->
				child != this &&
				TContextMenuPanel.class.isAssignableFrom(child.getClass()));
	}
	// --------------------------------------------------
	protected final @Nullable @Override TContextMenuPanel createContextMenu(int x, int y) { return null; }
	// ==================================================
	@Override
	protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, GuiUtils.applyAlpha(-16777216, getAlpha()));
		drawOutline(matrices, BORDER_COLOR);
	}
	// ==================================================
	/**
	 * Updates the height of this element based on the children.<br/>
	 * Also moves the element into view if it is out of view.
	 */
	public void updatePositionAndSize()
	{
		//update the height
		this.height = Math.min(getLocalBottomY(), 150);
		//update the Y
		if(getTParent() != null)
		{
			int moveX = getTParent().getTpeWidth() - (getTpeX() + getTpeWidth());
			int moveY = getTParent().getTpeHeight() - (getTpeY() + getTpeHeight());
			moveX = Math.min(moveX, 0);
			moveY = Math.min(moveY, 0);
			move(moveX, moveY);
		}
		//update the children's boxes
		for(TElement child : getTChildren()) child.updateRenderingBoundingBox();
	}
	// --------------------------------------------------
	@Override
	public <T extends TElement> boolean addTChild(T child, boolean reposition)
	{
		boolean b0 = super.addTChild(child, reposition);
		if(b0) updatePositionAndSize();
		return b0;
	}
	// ==================================================
	public int getLocalBottomY()
	{
		return this.topmosts.isFull() ?
				(this.topmosts.Item2.getTpeY() + this.topmosts.Item2.getTpeHeight()) - getTpeY() + 1 : 0;
	}
	// --------------------------------------------------
	/**
	 * Creates and adds a button to this {@link TContextMenuPanel}.
	 * @param label The button text.
	 * @param action The action performed when the button is pressed.
	 * @return The added button.
	 */
	public TButtonWidget addButton(Text label, Consumer<TButtonWidget> action)
	{
		CMWButton btn = new CMWButton(getLocalBottomY(), 15, label);
		btn.setOnClick(action);
		addTChild(btn, true);
		
		if(label != null)
		{
			int lblW = getTextRenderer().getWidth(label.getString());
			if(lblW > this.width) this.width = lblW + 15;
			updatePositionAndSize();
		}
		return btn;
	}
	// --------------------------------------------------
	/**
	 * Creates and adds a separator {@link TElement} to this {@link TContextMenuPanel}.
	 * @return The added separator {@link TElement}.
	 */
	public TElement addSeparator()
	{
		CMWSeparator sep = new CMWSeparator(getLocalBottomY());
		addTChild(sep, true);
		return sep;
	}
	// ==================================================
	protected class CMWButton extends TButtonWidget
	{
		public CMWButton(int y, int height, Text message) { super(0, y, 0, height, message, null); }
		protected final @Nullable @Override TContextMenuPanel createContextMenu(int x, int y) { return null; }
		@Override public int getTpeX() { return TContextMenuPanel.this.getTpeX(); }
		@Override public int getTpeWidth() { return TContextMenuPanel.this.getTpeWidth(); }
		@Override public float getAlpha() { return TContextMenuPanel.this.getAlpha(); }
		protected @Override void onClick()
		{
			var parent = TContextMenuPanel.this.getTParent();
			if(parent != null) parent.removeTChild(TContextMenuPanel.this);
			super.onClick();
		}
		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
		{
			drawMessage(matrices, HorizontalAlignment.LEFT, deltaTime);
			if(isFocusedOrHovered()) drawOutline(matrices, -1);
		}
	}
	// --------------------------------------------------
	protected class CMWSeparator extends TElement
	{
		public CMWSeparator(int y) { super(0, y, 0, 3); }
		protected final @Nullable @Override TContextMenuPanel createContextMenu(int x, int y) { return null; }
		@Override public int getTpeX() { return TContextMenuPanel.this.getTpeX(); }
		@Override public int getTpeWidth() { return TContextMenuPanel.this.getTpeWidth(); }
		@Override public float getAlpha() { return TContextMenuPanel.this.getAlpha(); }
		@Override public boolean canChangeFocus(FocusOrigin focusOrigin, boolean gainingFocus) { return !gainingFocus; }
		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
		{
			int w8 = getTpeWidth() / 8;
			/*fill(matrices,
					getTpeX() + w8,
					getTpeY() + 1,
					(getTpeX() + getTpeWidth()) - w8,
					getTpeY() + getTpeHeight(),
					GuiUtils.applyAlpha(TContextMenuWidget.this.separatorColor, getAlpha()));*/
			drawHorizontalLine(matrices, getTpeX() + w8, (getTpeX() + getTpeWidth()) - w8, getTpeY() + 1,
					GuiUtils.applyAlpha(BORDER_COLOR, getAlpha()));
		}
	}
	// ==================================================
}