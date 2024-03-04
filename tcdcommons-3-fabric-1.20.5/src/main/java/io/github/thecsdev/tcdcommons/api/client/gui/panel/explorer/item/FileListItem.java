package io.github.thecsdev.tcdcommons.api.client.gui.panel.explorer.item;

import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE;
import static io.github.thecsdev.tcdcommons.api.client.gui.panel.TPanelElement.COLOR_OUTLINE_FOCUSED;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext.DEFAULT_TEXT_COLOR;
import static io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext.DEFAULT_TEXT_SIDE_OFFSET;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;

import java.awt.Rectangle;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.client.gui.util.UITexture;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import net.minecraft.text.Text;

@ApiStatus.Experimental
public @Virtual class FileListItem extends TButtonWidget
{
	// ==================================================
	public static final int HEIGHT = 15;
	// --------------------------------------------------
	public static final UITexture TEX_DIR_ALT  = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(0,20,20,20));
	public static final UITexture TEX_DIR      = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(20,20,20,20));
	public static final UITexture TEX_FILE     = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(40,20,20,20));
	public static final UITexture TEX_FILE_TXT = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(60,20,20,20));
	public static final UITexture TEX_FILE_IMG = new UITexture(T_WIDGETS_TEXTURE, new Rectangle(80,20,20,20));
	// ==================================================
	protected @Nullable File file;
	//protected @Deprecated @Nullable Text fileName; -- super.text exists
	protected @Nullable Text fileSizeName;
	protected boolean fileHidden;
	//
	protected final int fileNameSideOffset;
	// ==================================================
	public FileListItem(int x, int y, int width, File file)
	{
		super(x, y, width, HEIGHT);
		this.fileNameSideOffset = getHeight() + DEFAULT_TEXT_SIDE_OFFSET;
		setFile(file);
	}
	// ==================================================
	public final @Nullable File getFile() { return this.file; }
	// --------------------------------------------------
	public final void setFile(@Nullable File file)
	{
		setFile(file,
				file == null ?
						null :
						(file.getParent() == null) ?
								file.getAbsolutePath() :
								file.getName()
		);
	}
	public final void setFile(@Nullable File file, String fileNameLabel)
	{
		//null check the label
		if(fileNameLabel == null) fileNameLabel = "";
		
		//assign file and its name
		this.file = file;
		super.text = literal(fileNameLabel);
		
		//assign file size name text and file icon
		Text fsn = null;
		UITexture icon = null;
		try
		{
			if(file != null)
			{
				//handle file size name
				if(!file.isDirectory())
					fsn = literal(FileUtils.byteCountToDisplaySize(file.length()));
				
				//handle file icon
				if(file.isDirectory())
					icon = TEX_DIR;
				else if(fileNameLabel.endsWith(".txt"))
					icon = TEX_FILE_TXT;
				else if(fileNameLabel.endsWith(".png") || fileNameLabel.endsWith(".jpg"))
					icon = TEX_FILE_IMG;
				else /*if file extension is anything else*/
					icon = TEX_FILE;
				
				//handle file-hidden
				this.fileHidden = file.isHidden();
			}
		}
		catch(SecurityException se) { this.fileHidden = true; /*exceptions should not be harmful here*/}
		this.icon = icon;
		this.fileSizeName = fsn;
	}
	// --------------------------------------------------
	public final void setIcon(@Nullable UITexture icon) { this.icon = icon; }
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		//draw icon
		if(this.icon != null)
		{
			if(!this.fileHidden) renderBackground(pencil);
			else
			{
				//note: the game doesn't render with transparency, so use 60% white and 40% black instead
				pencil.pushTShaderColor(0.6f, 0.6f, 0.6f, 1);
				renderBackground(pencil);
				pencil.popTShaderColor();
			}
		}
		//draw texts
		pencil.drawTElementTextTHSC(super.text, HorizontalAlignment.LEFT, this.fileNameSideOffset, DEFAULT_TEXT_COLOR);
		pencil.drawTElementTextTH(this.fileSizeName, HorizontalAlignment.RIGHT);
	}
	
	protected @Virtual @Override void renderBackground(TDrawContext pencil)
	{
		if(this.icon != null)
			this.icon.drawTexture(pencil, getX(), getY(), HEIGHT, HEIGHT);
	}
	
	public @Virtual @Override void postRender(TDrawContext pencil)
	{
		if(isFocused()) pencil.drawTBorder(COLOR_OUTLINE_FOCUSED);
		else if(isHovered()) pencil.drawTBorder(COLOR_OUTLINE);
	}
	// ==================================================
}