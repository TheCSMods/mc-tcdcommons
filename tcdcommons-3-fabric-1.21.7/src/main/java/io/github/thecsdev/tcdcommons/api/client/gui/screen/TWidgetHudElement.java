package io.github.thecsdev.tcdcommons.api.client.gui.screen;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.TInputContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.input.MouseDragHelper;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;

/**
 * A {@link TElement} that "wraps" another {@link TElement} and makes it draggable.
 */
@Internal
@Experimental
final class TWidgetHudElement extends TElement
{
	// ==================================================
	private static final int GRID_CELL_SIZE = 4;
	// --------------------------------------------------
	protected final TElement target;
	// ==================================================
	public TWidgetHudElement(TElement target)
	{
		//init super
		super(target.getX(), target.getY(), target.getWidth(), target.getHeight());
		
		//validate and assign target
		if(target.getParent() != null)
			throw new IllegalArgumentException("Target must not have a parent.");
		this.target = target;
		
		//add target
		addChild(target, false);
		addChild(new MouseCaptureElement(), true);
	}
	// ==================================================
	public final @Override boolean isFocusable() { return true; }
	// --------------------------------------------------
	public @Virtual @Override void render(TDrawContext pencil) {}
	// ==================================================
	/**
	 * This {@link TElement}'s sole purpose is to capture mouse input.
	 */
	private final class MouseCaptureElement extends TElement
	{
		// ----------------------------------------------
		private final MouseDragHelper mdh;
		// ----------------------------------------------
		public MouseCaptureElement()
		{
			super(0, 0, TWidgetHudElement.this.width, TWidgetHudElement.this.height);
			this.setZOffset(TWidgetHudElement.this.target.getZOffset() + 0.1f);
			
			//set up the mouse drag helper
			this.mdh = new MouseDragHelper()
			{
				protected @Override void apply(int deltaX, int deltaY) {
					TWidgetHudElement.this.move(deltaX, deltaY);
				}
			};
			
			//forward the context menu event to the target
			this.eContextMenu.register((__, cMenu) -> TWidgetHudElement.this.target
					.eContextMenu.invoker().invoke(TWidgetHudElement.this.target, cMenu));
		}
		// ----------------------------------------------
		public @Override boolean isFocusable() { return false; }
		//public @Virtual @Override void tick() { setAlpha(Screen.hasControlDown() ? 1 : 0); }
		public @Virtual @Override void render(TDrawContext pencil) {}
		public @Virtual @Override void postRender(TDrawContext pencil)
		{
			//draw a red outline when the drag wrapper is focused
			if(TWidgetHudElement.this.isFocused() || this.isHovered())
				pencil.drawTBorder(0xFFFFFFFF);
		}
		// ----------------------------------------------
		public @Virtual @Override boolean input(TInputContext inputContext)
		{
			//handle input by type
			switch(inputContext.getInputType())
			{
				//when mouse press happens;
				//- for button 1, display context menu
				//- for all other buttons, accept the input for dragging purposes
				case MOUSE_PRESS:
					if(inputContext.getMouseButton() == 1)
					{
						final var cm = this.createContextMenu();
						if(cm != null) cm.open();
					}
					return true;
				//when mouse drag happens, move the element around
				case MOUSE_DRAG:
					if(inputContext.getMouseButton() == 0)
						return this.mdh.onMouseDrag(inputContext.getMouseDelta());
					else break;
				//when mouse drag ends, clear the helper, end snap the element to bounds
				case MOUSE_DRAG_END:
					//clear the mouse drag helper
					this.mdh.clear();
					
					//snap to grid
					final int gridSize = GRID_CELL_SIZE;
					int x = TWidgetHudElement.this.getX(), y = TWidgetHudElement.this.getY();
					x = Math.round((float)x / gridSize) * gridSize;
					y = Math.round((float)y / gridSize) * gridSize;
					TWidgetHudElement.this.setPosition(x, y, false);
					
					//finally, snap to parent bounds
					MouseDragHelper.snapToParentBounds(TWidgetHudElement.this);
					
					//return
					return true;
				default: break;
			}
			//return false by default
			return false;
		}
		// ----------------------------------------------
	}
	// ==================================================
}