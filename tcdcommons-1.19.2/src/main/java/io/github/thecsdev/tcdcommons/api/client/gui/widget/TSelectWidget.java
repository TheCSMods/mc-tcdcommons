package io.github.thecsdev.tcdcommons.api.client.gui.widget;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.thecsdev.tcdcommons.api.client.gui.TElement;
import io.github.thecsdev.tcdcommons.api.client.gui.panel.TContextMenuPanel;
import io.github.thecsdev.tcdcommons.api.client.gui.util.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * A button that has a dropdown menu when clicked.
 */
public class TSelectWidget extends TButtonWidget
{
	// ==================================================
	/**
	 * Holds a reference to the {@link TContextMenuPanel}
	 * used as the dropdown selection menu.<br/>
	 * When set to null, the menu is closed.
	 */
	protected SWContextMenu contextMenu;
	
	/**
	 * The event handler that is used by {@link TSelectWidget}
	 * to handle movement of this element. Used to update
	 * the position and size of the dropdown if it is opened.
	 */
	protected BiConsumer<Integer, Integer> ehMoved_forDropdown;
	// --------------------------------------------------
	/**
	 * This is where the {@link TSelectWidget} keeps track
	 * of all selectable entries added to this element.
	 */
	protected final ArrayList<SWEntry> entries;
	// ==================================================
	public TSelectWidget(int x, int y, int width, int height)
	{
		super(x, y, width, height, null, null);
		this.entries = Lists.newArrayList();
		this.contextMenu = null;
		setMessage(TextUtils.fTranslatable("tcdcommons.gui.tselectwidget.default_label"));
		
		ehMoved_forDropdown = getEvents().MOVED.addWeakEventHandler((dX, dY) ->
		{
			if(isDropdownOpen() && this.contextMenu != null)
				this.contextMenu.updatePositionAndSize();
		});
	}
	// --------------------------------------------------
	protected @Override void onClick()
	{
		//if the context menu is already open, then close it
		if(this.screen == null) return;
		
		//handle super button click
		super.onClick();
		
		//if the dropdown context menu is open, then close it
		if(isDropdownOpen())
		{
			closeDropdownMenu();
			this.screen.setFocusedTChild(this);
			return;
		}
		//if not, then open a new dropdown context menu
		openDropdownMenu();
	}
	// ==================================================
	/**
	 * Opens the dropdown selection menu.
	 */
	public void openDropdownMenu()
	{
		if(isDropdownOpen()) return;
		contextMenu = new SWContextMenu();
		this.screen.addTChild(contextMenu); //1. fist
		for(SWEntry entry : this.entries) entry.createContextMenuItem(contextMenu); //2. second
	}
	
	/**
	 * Closes the dropdown selection menu.
	 */
	public void closeDropdownMenu()
	{
		if(!isDropdownOpen()) return;
		contextMenu.getTParent().removeTChild(contextMenu);
		contextMenu = null;
	}
	// --------------------------------------------------
	/**
	 * Returns true when the dropdown menu is currently opened.
	 */
	public boolean isDropdownOpen()
	{
		return this.screen != null &&
				this.contextMenu != null &&
				this.contextMenu.screen == this.screen;
	}
	// ==================================================
	/**
	 * Called when the user selects a given {@link SWEntry} option from the dropdown menu.<br/>
	 * <br/>
	 * Override this to define what happens when this method is called.<br/>
	 * Keep in mind that by default, this calls {@link #setMessage(MutableText)}.
	 * @param option The option that was selected.
	 */
	protected void onOptionSelected(SWEntry option) { setMessage(option.message); }
	// --------------------------------------------------
	public TSelectWidget addDropdownOption(Text label, Runnable onSelect) { return addDropdownOption(label, onSelect, null); }
	public TSelectWidget addDropdownOption(Text label, Runnable onSelect, Text tooltip)
	{
		this.entries.add(new SWEntry(label, onSelect, tooltip));
		return this;
	}
	
	public TSelectWidget addDropdownSeparator() { this.entries.add(new SWEntry(null, null)); return this; }
	// ==================================================
	/**
	 * Draws the dropdown arrow for this {@link TSelectWidget}.<br/>
	 * This arrow serves as an indicator that this is a dropdown menu.
	 */
	public void drawDropdownArrow(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		if(getTpeHeight() < 10) return;
		
		//apply shader stuff
		float alpha = getAlpha();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, T_WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1, 1, 1, alpha);
		
		//blend stuff
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		//calculate coords
		int y4 = getTpeHeight() / 4;
		int y2 = getTpeHeight() / 2;
		int x = (getTpeWidth() - y4) - y2;
		
		//draw
		int uvX = !isDropdownOpen() ? 0 : 20;
		drawTexture(matrices, getTpeX() + x, getTpeY() + y4, y2, y2, uvX, 40, 20, 20, 255, 255);
	}
	// --------------------------------------------------
	public @Override void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTime)
	{
		drawButton(matrices, mouseX, mouseY, deltaTime);
		drawMessage(matrices, HorizontalAlignment.LEFT, deltaTime);
		drawDropdownArrow(matrices, mouseX, mouseY, deltaTime);
	}
	// ==================================================
	public @Override TElement next()
	{
		if(isDropdownOpen() && this.contextMenu != null)
			return this.contextMenu;
		return super.next();
	}
	
	/**
	 * Same as {@link #next()}, but it is called from <b>super</b>.
	 */
	public final TElement super_next() { return super.next(); }
	// ==================================================
	protected class SWEntry
	{
		public final Text message;
		public final Runnable onClick;
		public final Text tooltip;
		public SWEntry(Text message, Runnable onClick) { this(message, onClick, null); }
		public SWEntry(Text message, Runnable onClick, Text tooltip)
		{
			this.message = message;
			this.onClick = onClick;
			this.tooltip = tooltip;
		}
		public void createContextMenuItem(TContextMenuPanel cMenu)
		{
			if(cMenu == null) return;
			if(this.message == null) { cMenu.addSeparator(); return; }
			cMenu.addButton(message, btn ->
			{
				if(!TSelectWidget.this.isDropdownOpen()) return;
				TSelectWidget.this.onClick();
				TSelectWidget.this.onOptionSelected(this);
				if(this.onClick != null) this.onClick.run();
			}).setTooltip(this.tooltip);
		}
	}
	// --------------------------------------------------
	protected class SWContextMenu extends TContextMenuPanel
	{
		public SWContextMenu()
		{
			super(0, 0, TSelectWidget.this.getTpeWidth());
			updatePositionAndSize();
		}
		public @Override void updatePositionAndSize()
		{
			setPosition(TSelectWidget.this.getTpeX(), TSelectWidget.this.getTpeY() + TSelectWidget.this.getTpeHeight(), false);
			super.updatePositionAndSize();
		}
		public @Override TElement previous() { return TSelectWidget.this; }
		public @Override TElement next()
		{
			TElement snxt = super.next();
			if(snxt != null) return snxt;
			return TSelectWidget.this.super_next();
		}
	}
	// ==================================================
}