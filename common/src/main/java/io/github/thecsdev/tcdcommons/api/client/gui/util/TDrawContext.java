package io.github.thecsdev.tcdcommons.api.client.gui.util;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.ColorStack.BlendMethod;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TCheckboxWidget;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorDrawContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.client.gui.widget.TClickableWidget.BUTTON_TEXTURES;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

/**
 * {@link TCDCommons}'s implementation of {@link GuiGraphics}.
 */
public final class TDrawContext extends GuiGraphics
{
	// ==================================================
	// -- More broken stuff;
	//private static final EntityRenderDispatcher ERD = TCDCommonsClient.MC_CLIENT.getEntityRenderDispatcher();
	//private static final EntityModelLoader ERD_EML = getModelLoader(ERD);
	//private static final Camera ERD_CAMERA = new Camera();
	// ==================================================
	public static final @Beta int DEFAULT_TEXT_SIDE_OFFSET = 5;
	public static final @Beta int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
	public static final @Beta int DEFAULT_ERROR_COLOR = 0xFFFF00FF;
	public static final @Beta ResourceLocation TEXTURE_FILL = ResourceLocation.fromNamespaceAndPath(getModID(), "textures/gui/fill.png");
	public static final @Beta ResourceLocation TEXTURE_ICONS = ResourceLocation.fromNamespaceAndPath(getModID(), "textures/gui/icons.png");
	// ==================================================
	protected final ColorStack colorStack = new ColorStack();
	protected final TextScaleStack textScaleStack = new TextScaleStack();
	protected final Minecraft client;
	// --------------------------------------------------
	/**
	 * Represents the {@link TParentElement} that is currently
	 * being rendered by a {@link TScreen}.<br/>
	 * <b>Can be null. Read only, do not modify.</b>
	 */
	protected @Nullable TParentElement currentTarget;
	// --------------------------------------------------
	public final int mouseX, mouseY;
	public final float deltaTime;
	protected float textScale = 1;
	// ==================================================
	protected TDrawContext(GuiGraphics drawContext, int mouseX, int mouseY, float deltaTime) {
		this(((AccessorDrawContext)drawContext), mouseX, mouseY, deltaTime);
	}
	
	private TDrawContext(AccessorDrawContext drawContext, int mouseX, int mouseY, float deltaTime)
	{
		//copy variables from given draw context into this draw context
		super(drawContext.getClient(), drawContext.getState());
		
		final var mixin_this = ((AccessorDrawContext)(Object)this);
		mixin_this.setClient(drawContext.getClient());
		mixin_this.setMatrices(drawContext.getMatrices());
		mixin_this.setState(drawContext.getState());
		
		//initialize variables
		this.client        = drawContext.getClient();
		this.currentTarget = null;
		this.mouseX        = mouseX;
		this.mouseY        = mouseY;
		this.deltaTime     = deltaTime;
	}
	
	/**
	 * Creates a new {@link TDrawContext} instance and returns it.
	 * @param drawContext The "parent" draw context.
	 * @param mouseX The current mouse cursor X position.
	 * @param mouseY The current mouse cursor Y position.
	 * @param deltaTime Time elapsed between the current frame and the last frame.
	 */
	public static TDrawContext of(GuiGraphics drawContext, int mouseX, int mouseY, float deltaTime)
	{
		//if, in the future, the DrawContext constructor behavior changes,
		//this factory method will be able to handle that change,
		//hence why the 'new' keyword isn't allowed to be used directly,
		//as that'd require calling the DrawContext constructor as well.
		return new TDrawContext(drawContext, mouseX, mouseY, deltaTime);
	}
	
	/**
	 * Updates the current drawing context info and the {@link #currentTarget}.<br/>
	 * Calling this indicates that you plan on rendering another GUI element.
	 * @param target The {@link TParentElement} that is about to be rendered next.
	 */
	public final void updateContext(TParentElement target) { this.currentTarget = target; }
	//public final @Nullable TParentElement getCurrentTarget() { return this.currentTarget; } -- not even used
	// ==================================================
	/**
	 * Fills a given area on the screen with a given color.<p>
	 * Overridden to add support for the {@link #colorStack}.
	 */
	public final @Override void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color)
	{
		//calculate the color parameters, and blend with the color stack (use the 'multiply' blending method)
		final var calc = this.colorStack.calculate();
		float a = (ARGB.alpha(color) / 255.0f) * calc.a; //note: 255 has to be a decimal
		float r = (ARGB.red(color) / 255.0f)   * calc.r;
		float g = (ARGB.green(color) / 255.0f) * calc.g;
		float b = (ARGB.blue(color) / 255.0f)  * calc.b;
		
		color = ((int)(a * 255) << 24) |
				((int)(r * 255) << 16) |
				((int)(g * 255) << 8)  |
				(int)(b * 255);
		
		//call super
		super.fill(pipeline, x1, y1, x2, y2, color);
	}
	// ==================================================
	/**
	 * Pushes a color to the shader {@link #colorStack}.<p>
	 * After you're done, don't forget to {@link #popTShaderColor()}
	 * @param red The R color channel (0 to 1).
	 * @param green The G color channel (0 to 1).
	 * @param blue The B color channel (0 to 1).
	 * @param alpha The A color channel (0 to 1).
	 * @see #pushTShaderColor(float, float, float, float, BlendMethod)
	 * @see #popTShaderColor()
	 */
	public final void pushTShaderColor(float red, float green, float blue, float alpha)
	{
		this.colorStack.push(red, green, blue, alpha);
		applyTShaderColor();
	}
	
	/**
	 * Same as {@link #pushTShaderColor(float, float, float, float)}, but you
	 * also get to choose the {@link BlendMethod}.<p>
	 * After you're done, don't forget to {@link #popTShaderColor()}
	 * @param red The R color channel (0 to 1).
	 * @param green The G color channel (0 to 1).
	 * @param blue The B color channel (0 to 1).
	 * @param alpha The A color channel (0 to 1).
	 * @see #pushTShaderColor(float, float, float, float, BlendMethod)
	 * @see #popTShaderColor()
	 */
	public final void pushTShaderColor(float red, float green, float blue, float alpha, BlendMethod blendMethod)
	{
		this.colorStack.push(red, green, blue, alpha, blendMethod);
		applyTShaderColor();
	}
	public final void popTShaderColor() { this.colorStack.pop(); applyTShaderColor(); }
	public final void applyTShaderColor() { this.colorStack.apply(this); }
	
	/**
	 * Sets the shader color to a color of choice, bypassing the {@link #colorStack}.<p>
	 * It is recommended to use {@link #pushTShaderColor(float, float, float, float)} instead.<br/>
	 * When pushing to {@link #colorStack}, don't forget to {@link #popTShaderColor()}.<p>
	 * If you end up using this, and you want to re-apply the {@link #colorStack}, use {@link #applyTShaderColor()}.
	 * @param red The R color channel (0 to 1).
	 * @param green The G color channel (0 to 1).
	 * @param blue The B color channel (0 to 1).
	 * @param alpha The A color channel (0 to 1).
	 * @see #pushTShaderColor(float, float, float, float)
	 * @see #popTShaderColor()
	 * @see #applyTShaderColor()
	 */
	public final void setShaderColor(float red, float green, float blue, float alpha)
	{
		//this must be a final override, so the color stack can work properly,
		//and so there's always a way to set shader color without using the color stack
		//RenderSystem.setShaderColor(red, green, blue, alpha); -- FIXME - Feature gone!
	}
	// --------------------------------------------------
	public final void pushTTextScale(float scale) { this.textScaleStack.push(scale); applyTTextScale(); }
	public final void popTTextScale() { this.textScaleStack.pop(); applyTTextScale(); }
	public final void applyTTextScale() { this.textScaleStack.apply(this); }
	// ==================================================
	/**
	 * Draws a display {@link Component} for the {@link #currentTarget}.
	 * @param text The {@link Component} to draw.
	 * @param horizontalAlgnment The {@link HorizontalAlignment} in which to draw the {@link Component}.
	 */
	public final void drawTElementTextTH(Component text, HorizontalAlignment horizontalAlgnment)
	{
		drawTElementTextTHSC(text, horizontalAlgnment, DEFAULT_TEXT_SIDE_OFFSET, DEFAULT_TEXT_COLOR);
	}
	
	/**
	 * Draws a display {@link Component} for the {@link #currentTarget}.
	 * @param text The {@link Component} to draw.
	 * @param horizontalAlgnment The {@link HorizontalAlignment} in which to draw the {@link Component}.
	 * @param color The {@link Component} color.
	 */
	public final void drawTElementTextTHC(Component text, HorizontalAlignment horizontalAlgnment, int color)
	{
		drawTElementTextTHSC(text, horizontalAlgnment, DEFAULT_TEXT_SIDE_OFFSET, color);
	}
	
	/**
	 * Draws a display {@link Component} for the {@link #currentTarget}.
	 * @param text The {@link Component} to draw.
	 * @param horizontalAlgnment The {@link HorizontalAlignment} in which to draw the {@link Component}.
	 * @param sideOffset If drawing left or right, the offset in pixels towards the center that will be applied.
	 * @param color The {@link Component} color.
	 */
	public final void drawTElementTextTHSC(Component text, HorizontalAlignment horizontalAlgnment, int sideOffset, int color)
	{
		drawTElementTextTHSCS(text, horizontalAlgnment, sideOffset, color, 1);
	}
	
	public final void drawTElementTextTHSS(Component text, HorizontalAlignment horizontalAlgnment, int sideOffset, float textScale)
	{
		drawTElementTextTHSCS(text, horizontalAlgnment, sideOffset, DEFAULT_TEXT_COLOR, textScale);
	}
	
	public final void drawTElementTextTHSCS(Component text, HorizontalAlignment horizontalAlgnment, int sideOffset, int color, float textScale)
	{
		//null-check the text and the alignment
		if(text == null || horizontalAlgnment == null)
			return;

		//obtain some info and do some calculations
		textScale *= this.textScale; //important part
		
		final Font txtR = this.client.font;
		final int textWidth = txtR.width(text);
		final int targetCenter = this.currentTarget.getY() + (this.currentTarget.getHeight() / 2);

		//calculate text X and Y
		double textX, textY = targetCenter - (txtR.lineHeight * textScale / 2);
		switch(horizontalAlgnment)
		{
			case LEFT:
				textX = this.currentTarget.getX() + sideOffset;
				break;
			case RIGHT:
				textX = this.currentTarget.getEndX() - ((textWidth * textScale) + sideOffset);
				break;
			case CENTER:
				textX = this.currentTarget.getX() + (this.currentTarget.getWidth() / 2);
				textX -= (textWidth * textScale) / 2;
				break;
			default: throw new IllegalArgumentException("Unexpected " + HorizontalAlignment.class.getSimpleName() + ".");
		}

		//draw
		pose().pushMatrix();
		pose().translate((float)textX, (float)textY);
		pose().scale(textScale, textScale);
		drawString(txtR, text, 0, 0, color, true);
		pose().popMatrix();
	}
	// --------------------------------------------------
	/**
	 * Draws an inner outline around the {@link #currentTarget}.
	 * @param color The border color.
	 */
	public final void drawTBorder(int color)
	{
		renderOutline(
				this.currentTarget.getX(), this.currentTarget.getY(),
				this.currentTarget.getWidth(), this.currentTarget.getHeight(),
				color);
	}
	
	/**
	 * Draws a {@link #fill(int, int, int, int, int)} over the {@link #currentTarget}.
	 * @param color The fill color.
	 */
	public final void drawTFill(int color)
	{
		fill(
				this.currentTarget.getX(), this.currentTarget.getY(),
				this.currentTarget.getEndX(), this.currentTarget.getEndY(),
				color);
	}
	// --------------------------------------------------
	/**
	 * Draws a nine-sliced texture on top of the {@link #currentTarget}.<br/>
	 * Uses 256x256 as the texture image width and height. 
	 * @param textureId The texture {@link ResourceLocation}.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param regionWidth The width of the texture's UV region, measured in pixels.
	 * @param regionHeight The height of the texture's UV region, measured in pixels.
	 * @param slicedBorderSize The size of the sliced pieces around the center piece.
	 */
	public final void drawTNineSlicedTexture(
			ResourceLocation textureId,
			int u, int v, int regionWidth, int regionHeight,
			int slicedBorderSize)
	{
		drawTNineSlicedTexture(textureId, u, v, regionWidth, regionHeight, 256, 256, slicedBorderSize);
	}
	
	/**
	 * Draws a nine-sliced texture on top of the {@link #currentTarget}.
	 * @param textureId The texture {@link ResourceLocation}.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvW The width of the texture's UV region, measured in pixels.
	 * @param uvH The height of the texture's UV region, measured in pixels.
	 * @param tW The width of the texture image, measured in pixels.
	 * @param tH The height of the texture image, measured in pixels.
	 * @param s The size of the sliced pieces around the center piece.
	 */
	public final void drawTNineSlicedTexture(
			ResourceLocation textureId,
			int u, int v, int uvW, int uvH,
			int tW, int tH,
			int s)
	{
		drawTNineSlicedTexture(textureId,
				this.currentTarget.getX(), this.currentTarget.getY(),
				this.currentTarget.getWidth(), this.currentTarget.getHeight(),
				u, v, uvW, uvH, tW, tH, s);
	}
	
	/**
	 * Draws a nine-sliced texture.
	 * @param textureId The texture {@link ResourceLocation}.
	 * @param x The starting X position on the screen.
	 * @param y The starting Y position on the screen.
	 * @param w The width size of the sliced texture on the screen.
	 * @param h The height size of the sliced texture on the screen.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvW The width of the texture's UV region, measured in pixels.
	 * @param uvH The height of the texture's UV region, measured in pixels.
	 * @param tW The width of the texture image, measured in pixels.
	 * @param tH The height of the texture image, measured in pixels.
	 * @param s The size of the sliced pieces around the center piece.
	 */
	public final void drawTNineSlicedTexture(
			ResourceLocation textureId,
			int x, int y, int w, int h,
			int u, int v, int uvW, int uvH,
			int tW, int tH,
			int s)
	{
		//calculations
		final int s2 = s * 2;
		
		//draw 9-slice if possible...
		if(s2 < w || s2 < h)
		{
			//the four corners
			blit(RenderPipelines.GUI_TEXTURED, textureId, x, y, s, s, u, v, s, s, tW, tH);
			blit(RenderPipelines.GUI_TEXTURED, textureId, x + w - s, y, s, s, u + uvW - s, v, s, s, tW, tH);
			blit(RenderPipelines.GUI_TEXTURED, textureId, x, y + h - s, s, s, u, v + uvH - s, s, s, tW, tH);
			blit(RenderPipelines.GUI_TEXTURED, textureId, x + w - s, y + h - s, s, s, u + uvW - s, v + uvH - s, s, s, tW, tH);
			
			//the four sides
			blit(RenderPipelines.GUI_TEXTURED, textureId, x + s, y, w - s2, s, u + s, v, uvW - s2, s, tW, tH);
			blit(RenderPipelines.GUI_TEXTURED, textureId, x, y + s, s, h - s2, u, v + s, s, uvH - s2, tW, tH);	
			blit(RenderPipelines.GUI_TEXTURED, textureId, x + w - s, y + s, s, h - s2, u + uvW - s, v + s, s, uvH - s2, tW, tH);
			blit(RenderPipelines.GUI_TEXTURED, textureId, x + s, y + h - s, w - s2, s, u + s, v + uvH - s, uvW - s2, s, tW, tH);
			
			//the middle
			drawTRepeatingTexture(textureId, x + s, y + s, w - s2, h - s2, u + s, v + s, uvW - s2, uvH - s2, tW, tH);
		}
		//...else draw the full texture
		else
		{
			//if the slicing is larger than the element itself, then draw
			//the full texture in one single draw without slicing
			blit(RenderPipelines.GUI_TEXTURED, textureId, x, y, w, h, u, v, uvW, uvH, tW, tH);
		}
	}
	// --------------------------------------------------
	/**
	 * Draws a repeating texture.
	 * @param x The starting X position on the screen.
	 * @param y The starting Y position on the screen.
	 * @param width The width size of the repeating texture on the screen.
	 * @param height The height size of the repeating texture on the screen.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvRegionWidth The width of the texture's UV region, measured in pixels.
	 * @param uvRegionHeight The height of the texture's UV region, measured in pixels.
	 * @param textureWidth The width of the texture image, measured in pixels.
	 * @param textureHeight The height of the texture image, measured in pixels.
	 */
	public final void drawTRepeatingTexture(
			ResourceLocation textureId,
			int x, int y, int width, int height,
			int u, int v, int uvRegionWidth, int uvRegionHeight,
			int textureWidth, int textureHeight)
	{
		int endX = x + width, endY = y + height;
		for(int y1 = y; y1 < endY; y1 += uvRegionHeight)
		for(int x1 = x; x1 < endX; x1 += uvRegionWidth)
		{
			int nextW = uvRegionWidth, nextH = uvRegionHeight;
			if(x1 + nextW > endX) nextW -= (x1 + nextW) - endX;
			if(y1 + nextH > endY) nextH -= (y1 + nextH) - endY;
			if(nextW < 1 || nextH < 1) continue;
			blit(RenderPipelines.GUI_TEXTURED, textureId, x1, y1, nextW, nextH, u, v, nextW, nextH, textureWidth, textureHeight);
		}
	}
	// --------------------------------------------------
	/**
	 * Draws a GUI button on top of the {@link #currentTarget}.
	 */
	public final void drawTButton(boolean enabled, boolean focused)
	{
		final var curr = this.currentTarget;
		blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_TEXTURES.get(enabled, focused),
				curr.getX(), curr.getY(),
				curr.getWidth(), curr.getHeight());
	}
	
	public final void drawTCheckbox(int x, int y, boolean highlighted, boolean checked) { drawTCheckbox(x, y, 20, 20, highlighted, checked); }
	public final void drawTCheckbox(int x, int y, int width, int height, boolean highlighted, boolean checked)
	{
		final ResourceLocation tex = (checked) ?
				(highlighted ? TCheckboxWidget.SELECTED_HIGHLIGHTED_TEXTURE : TCheckboxWidget.SELECTED_TEXTURE) :
				(highlighted ? TCheckboxWidget.HIGHLIGHTED_TEXTURE : TCheckboxWidget.TEXTURE);
		blitSprite(RenderPipelines.GUI_TEXTURED, tex, x, y, width, height);
	}
	// ==================================================
	@Experimental
	@Deprecated(forRemoval = true) //broken
	public final void drawTEntity(Entity entity, int size, boolean followCursor) throws NullPointerException
	{
		if(entity == null) return;
		final var c = this.currentTarget;
		drawTEntity(entity, c.getX(), c.getY(), c.getWidth(), c.getHeight(), size, followCursor);
	}
	
	@Experimental
	@Deprecated(forRemoval = true) //broken
	public final void drawTEntity(Entity entity, int x, int y, int width, int height, int size, boolean followCursor)
	{
		//null check
		if(entity == null) return;
		
		//prepare to render
		final float f = 0.0625F;
		final int mouseX = followCursor ? this.mouseX : x + (width / 2) + 100;
		final int mouseY = followCursor ? this.mouseY : y + (height / 2) + 50;
		final @Nullable LivingEntity livingEntity = (entity instanceof LivingEntity) ? (LivingEntity)entity : null;
		
		//vanilla rendering
		if(livingEntity != null && MC_CLIENT.level != null) //vanilla rendering depends on client-world
		{
			InventoryScreen.renderEntityInInventoryFollowsMouse(
					this,
					x, y,
					x + width, y + height,
					size,
					f, mouseX, mouseY,
					livingEntity);
			return;
		}
		
		//FIXME - Feature decimated and crushed to bits. Rebuild it!
		/*//rendering in a way that supports all entity types
		float g = x + (width / 2);
		float h = y + (height / 2);
		enableScissor(x, y, x + width, y + height);
		float i = (float)Math.atan(((g - mouseX) / 40.0F));
		float j = (float)Math.atan(((h - mouseY) / 40.0F));
		Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
		Quaternionf quaternionf2 = (new Quaternionf()).rotateX(j * 20.0F * 0.017453292F);
		quaternionf.mul((Quaternionfc)quaternionf2);
		float k = 0;
		float l = entity.getYaw();
		float m = entity.getPitch();
		float n = 0;
		float o = 0;
		if(livingEntity != null)
		{
			k = livingEntity.bodyYaw;
			n = livingEntity.lastHeadYaw;
			o = livingEntity.headYaw;
			livingEntity.bodyYaw = 180.0F + i * 20.0F;
			livingEntity.headYaw = entity.getYaw();
			livingEntity.lastHeadYaw = entity.getYaw();
		}
		entity.setYaw(180.0F + i * 40.0F);
		entity.setPitch(-j * 20.0F);
		Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f, 0.0F);
		__drawEntity(g, h, size, vector3f, quaternionf, quaternionf2, entity);
		entity.setYaw(l);
		entity.setPitch(m);
		if(livingEntity != null)
		{
			livingEntity.bodyYaw = k;
			livingEntity.lastHeadYaw = n;
			livingEntity.headYaw = o;
		}
		disableScissor();*/
	}
	
	private final @Internal void __drawEntity(
			float x, float y, int size,
			Vector3f vector3f, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2,
			Entity entity)
	{
		//FIXME - Feature decimated and crushed to bits. Rebuild it!
		/*getMatrices().push();
		getMatrices().translate(x, y, 50.0D);
		getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling(size, size, -size));
		getMatrices().translate(vector3f.x, vector3f.y, vector3f.z);
		getMatrices().multiply(quaternionf);
		DiffuseLighting.enableGuiShaderLighting();
		if (quaternionf2 != null)
		{
			quaternionf2.conjugate();
			ERD.setRotation(quaternionf2);
		} 
		ERD.setRenderShadows(false);
		ERD.render(entity, 0.0D, 0.0D, 0.0D, 1.0F, getMatrices(), getVertexConsumers(), 15728880);
		draw();
		ERD.setRenderShadows(true);
		getMatrices().pop();
		DiffuseLighting.enableGuiDepthLighting();*/
	  }
	// ==================================================
}