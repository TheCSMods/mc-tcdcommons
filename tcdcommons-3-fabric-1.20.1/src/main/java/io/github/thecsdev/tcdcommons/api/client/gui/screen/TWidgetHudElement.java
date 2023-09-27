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
				case MOUSE_PRESS:
					if(inputContext.getMouseButton() == 1)
					{
						final var cm = this.createContextMenu();
						if(cm != null) cm.open();
					}
					return true;
				case MOUSE_DRAG:
					if(inputContext.getMouseButton() == 0)
						return this.mdh.onMouseDrag(inputContext.getMouseDelta());
					else break;
				case MOUSE_DRAG_END: this.mdh.clear(); return true;
				default: break;
			}
			//return false by default
			return false;
		}
		// ----------------------------------------------
	}
	// ==================================================
}