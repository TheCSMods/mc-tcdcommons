package io.github.thecsdev.tcdcommons.api.client.gui.util;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;
import static io.github.thecsdev.tcdcommons.api.client.gui.widget.TClickableWidget.BUTTON_TEXTURE_SLICE_SIZE;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.awt.Color;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.client.gui.TParentElement;
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.util.ColorStack.BlendMethod;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TClickableWidget;
import io.github.thecsdev.tcdcommons.api.util.enumerations.HorizontalAlignment;
import io.github.thecsdev.tcdcommons.client.TCDCommonsClient;
import io.github.thecsdev.tcdcommons.client.mixin.hooks.AccessorDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

/**
 * {@link TCDCommons}'s implementation of {@link DrawContext}.
 */
public final class TDrawContext extends DrawContext
{
	// ==================================================
	private static final EntityRenderDispatcher ERD = TCDCommonsClient.MC_CLIENT.getEntityRenderDispatcher();
	//private static final EntityModelLoader ERD_EML = getModelLoader(ERD);
	private static final Camera ERD_CAMERA = new Camera();
	// ==================================================
	public static final @Beta int DEFAULT_TEXT_SIDE_OFFSET = 5;
	public static final @Beta int DEFAULT_TEXT_COLOR = 16777215;
	public static final @Beta int DEFAULT_ERROR_COLOR = Color.MAGENTA.getRGB();
	public static final @Beta Identifier TEXTURE_FILL = new Identifier(getModID(), "textures/gui/fill.png");
	public static final @Beta Identifier TEXTURE_ICONS = new Identifier(getModID(), "textures/gui/icons.png");
	// ==================================================
	protected final ColorStack colorStack = new ColorStack();
	protected final TextScaleStack textScaleStack = new TextScaleStack();
	protected final MinecraftClient client;
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
	protected TDrawContext(DrawContext drawContext, int mouseX, int mouseY, float deltaTime)
	{
		//copy variables from given draw context into this draw context
		super(null, null);
		final var mixin_this = ((AccessorDrawContext)(Object)this);
		final var mixin_that = ((AccessorDrawContext)drawContext);
		mixin_this.setClient(this.client = mixin_that.getClient());
		mixin_this.setMatrices(mixin_that.getMatrices());
		mixin_this.setVertexConsumers(mixin_that.getVertexConsumers());
		
		//initialize variables
		//this.colorStack = new ColorStack();
		//this.textSizeStack = new TextSizeStack();
		this.currentTarget = null;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.deltaTime = deltaTime;
	}
	
	/**
	 * Creates a new {@link TDrawContext} instance and returns it.
	 * @param drawContext The "parent" draw context.
	 * @param mouseX The current mouse cursor X position.
	 * @param mouseY The current mouse cursor Y position.
	 * @param deltaTime Time elapsed between the current frame and the last frame.
	 */
	public static TDrawContext of(DrawContext drawContext, int mouseX, int mouseY, float deltaTime)
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
	public final @Override void fill(RenderLayer layer, int x1, int x2, int y1, int y2, int z, int color)
	{
		//verify position validity
		if (x1 < y1) { int i = x1; x1 = y1; y1 = i; }
		if (x2 < y2) { int i = x2; x2 = y2; y2 = i; }
		
		//calculate the color parameters, and blend with the color stack (use the 'multiply' blending method)
		final var calc = this.colorStack.calculate();
		float a = (ColorHelper.Argb.getAlpha(color) / 255.0f) * calc.a; //note: 255 has to be a decimal
		float r = (ColorHelper.Argb.getRed(color) / 255.0f) * calc.r;
		float g = (ColorHelper.Argb.getGreen(color) / 255.0f) * calc.g;
		float b = (ColorHelper.Argb.getBlue(color) / 255.0f) * calc.b;
		
		//define the fill vertices
		final var matrix4f = getMatrices().peek().getPositionMatrix();
		final var vertexConsumer = getVertexConsumers().getBuffer(layer);
	    vertexConsumer.vertex(matrix4f, x1, x2, z).color(r, g, b, a).next();
	    vertexConsumer.vertex(matrix4f, x1, y2, z).color(r, g, b, a).next();
	    vertexConsumer.vertex(matrix4f, y1, y2, z).color(r, g, b, a).next();
	    vertexConsumer.vertex(matrix4f, y1, x2, z).color(r, g, b, a).next();
	    
	    //draw the fill vertices
	    RenderSystem.disableDepthTest();
	    getVertexConsumers().draw();
	    RenderSystem.enableDepthTest();
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
	public final @Override void setShaderColor(float red, float green, float blue, float alpha)
	{
		//this must be a final override, so the color stack can work properly,
		//and so there's always a way to set shader color without using the color stack
		super.setShaderColor(red, green, blue, alpha);
	}
	// --------------------------------------------------
	public final void pushTTextScale(float scale) { this.textScaleStack.push(scale); applyTTextScale(); }
	public final void popTTextScale() { this.textScaleStack.pop(); applyTTextScale(); }
	public final void applyTTextScale() { this.textScaleStack.apply(this); }
	// ==================================================
	/**
	 * Draws a display {@link Text} for the {@link #currentTarget}.
	 * @param text The {@link Text} to draw.
	 * @param horizontalAlgnment The {@link HorizontalAlignment} in which to draw the {@link Text}.
	 */
	public final void drawTElementTextTH(Text text, HorizontalAlignment horizontalAlgnment)
	{
		drawTElementTextTHSC(text, horizontalAlgnment, DEFAULT_TEXT_SIDE_OFFSET, DEFAULT_TEXT_COLOR);
	}
	
	/**
	 * Draws a display {@link Text} for the {@link #currentTarget}.
	 * @param text The {@link Text} to draw.
	 * @param horizontalAlgnment The {@link HorizontalAlignment} in which to draw the {@link Text}.
	 * @param color The {@link Text} color.
	 */
	public final void drawTElementTextTHC(Text text, HorizontalAlignment horizontalAlgnment, int color)
	{
		drawTElementTextTHSC(text, horizontalAlgnment, DEFAULT_TEXT_SIDE_OFFSET, color);
	}
	
	/**
	 * Draws a display {@link Text} for the {@link #currentTarget}.
	 * @param text The {@link Text} to draw.
	 * @param horizontalAlgnment The {@link HorizontalAlignment} in which to draw the {@link Text}.
	 * @param sideOffset If drawing left or right, the offset in pixels towards the center that will be applied.
	 * @param color The {@link Text} color.
	 */
	public final void drawTElementTextTHSC(Text text, HorizontalAlignment horizontalAlgnment, int sideOffset, int color)
	{
		drawTElementTextTHSCS(text, horizontalAlgnment, sideOffset, color, 1);
	}
	
	public final void drawTElementTextTHSS(Text text, HorizontalAlignment horizontalAlgnment, int sideOffset, float textScale)
	{
		drawTElementTextTHSCS(text, horizontalAlgnment, sideOffset, DEFAULT_TEXT_COLOR, textScale);
	}
	
	public final void drawTElementTextTHSCS(Text text, HorizontalAlignment horizontalAlgnment, int sideOffset, int color, float textScale)
	{
		//null-check the text and the alignment
		if(text == null || horizontalAlgnment == null)
			return;

		//obtain some info and do some calculations
		textScale *= this.textScale; //important part
		
		final TextRenderer txtR = this.client.textRenderer;
		final int textWidth = txtR.getWidth(text);
		final int targetCenter = this.currentTarget.getY() + (this.currentTarget.getHeight() / 2);

		//calculate text X and Y
		double textX, textY = targetCenter - (txtR.fontHeight * textScale / 2);
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
		getMatrices().push();
		getMatrices().translate(textX, textY, 0);
		getMatrices().scale(textScale, textScale, 1);
		drawTextWithShadow(txtR, text, 0, 0, color);
		getMatrices().pop();
	}
	// --------------------------------------------------
	/**
	 * Draws an inner outline around the {@link #currentTarget}.
	 * @param color The border color.
	 */
	public final void drawTBorder(int color)
	{
		drawBorder(
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
	 * @param textureId The texture {@link Identifier}.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param regionWidth The width of the texture's UV region, measured in pixels.
	 * @param regionHeight The height of the texture's UV region, measured in pixels.
	 * @param slicedBorderSize The size of the sliced pieces around the center piece.
	 */
	public final void drawTNineSlicedTexture(
			Identifier textureId,
			int u, int v, int regionWidth, int regionHeight,
			int slicedBorderSize)
	{
		drawTNineSlicedTexture(textureId, u, v, regionWidth, regionHeight, 256, 256, slicedBorderSize);
	}
	
	/**
	 * Draws a nine-sliced texture on top of the {@link #currentTarget}.
	 * @param textureId The texture {@link Identifier}.
	 * @param u The horizontal position of the texture's UV coordinate, measured in pixels.
	 * @param v The vertical position of the texture's UV coordinate, measured in pixels.
	 * @param uvW The width of the texture's UV region, measured in pixels.
	 * @param uvH The height of the texture's UV region, measured in pixels.
	 * @param tW The width of the texture image, measured in pixels.
	 * @param tH The height of the texture image, measured in pixels.
	 * @param s The size of the sliced pieces around the center piece.
	 */
	public final void drawTNineSlicedTexture(
			Identifier textureId,
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
	 * @param textureId The texture {@link Identifier}.
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
			Identifier textureId,
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
			drawTexture(textureId, x, y, s, s, u, v, s, s, tW, tH);
			drawTexture(textureId, x + w - s, y, s, s, u + uvW - s, v, s, s, tW, tH);
			drawTexture(textureId, x, y + h - s, s, s, u, v + uvH - s, s, s, tW, tH);
			drawTexture(textureId, x + w - s, y + h - s, s, s, u + uvW - s, v + uvH - s, s, s, tW, tH);
			
			//the four sides
			drawTexture(textureId, x + s, y, w - s2, s, u + s, v, uvW - s2, s, tW, tH);
			drawTexture(textureId, x, y + s, s, h - s2, u, v + s, s, uvH - s2, tW, tH);	
			drawTexture(textureId, x + w - s, y + s, s, h - s2, u + uvW - s, v + s, s, uvH - s2, tW, tH);
			drawTexture(textureId, x + s, y + h - s, w - s2, s, u + s, v + uvH - s, uvW - s2, s, tW, tH);
			
			//the middle
			drawTRepeatingTexture(textureId, x + s, y + s, w - s2, h - s2, u + s, v + s, uvW - s2, uvH - s2, tW, tH);
		}
		//...else draw the full texture
		else
		{
			//if the slicing is larger than the element itself, then draw
			//the full texture in one single draw without slicing
			drawTexture(textureId, x, y, w, h, u, v, uvW, uvH, tW, tH);
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
			Identifier textureId,
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
			drawTexture(textureId, x1, y1, nextW, nextH, u, v, nextW, nextH, textureWidth, textureHeight);
		}
	}
	// --------------------------------------------------
	/**
	 * Draws a GUI button on top of the {@link #currentTarget}.
	 * @param yImage See {@link TClickableWidget#getButtonTextureY(boolean, boolean)}
	 * @see TClickableWidget#getButtonTextureY(boolean, boolean)
	 */
	public final void drawTButton(int yImage)
	{
		drawTNineSlicedTexture(ClickableWidget.WIDGETS_TEXTURE,
				0, yImage, 200, 20,
				BUTTON_TEXTURE_SLICE_SIZE);
	}
	// ==================================================
	/*
	 * Draws a {@link PlayerBadge} over the {@link #currentTarget}.
	 * @param playerBadge The {@link PlayerBadge} to draw.
	 * @see #drawTPlayerBadge(PlayerBadge, int, int, int, int)
	 *
	@Deprecated
	public final <T extends PlayerBadge> void drawTPlayerBadge(T playerBadge)
	{
		drawTPlayerBadge(
				playerBadge,
				this.currentTarget.getX(), this.currentTarget.getY(),
				this.currentTarget.getWidth(), this.currentTarget.getHeight());
	}*/
	
	/*
	 * Draws a {@link PlayerBadge} at the given coordinates, using the
	 * {@link PlayerBadge}'s corresponding {@link PlayerBadgeRenderer} that is
	 * registered in {@link TClientRegistries#PLAYER_BADGE_RENDERER}.
	 * @param playerBadge The {@link PlayerBadge} to render.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param width The visual size of the {@link PlayerBadge} (width).
	 * @param height The visual size of the {@link PlayerBadge} (height).
	 * @throws NoSuchElementException If a corresponding {@link PlayerBadgeRenderer} isn't registered.
	 * @throws ClassCastException If the corresponding {@link PlayerBadgeRenderer}'s generic type doesn't match.
	 *
	@Deprecated //for performance reasons, performing a renderer lookup every frame isn't a good idea
	public final <T extends PlayerBadge> void drawTPlayerBadge
	(T playerBadge, int x, int y, int width, int height) throws NoSuchElementException, ClassCastException
	{
		final var playerBadgeId = playerBadge.getId();
		@SuppressWarnings("unchecked")
		final var br = (PlayerBadgeRenderer<T>)TClientRegistries.PLAYER_BADGE_RENDERER.getValue(playerBadgeId).get();
		br.render(this, x, y, width, height, this.mouseX, this.mouseY, this.deltaTime);
	}*/
	// ==================================================
	/**
	 * Draws an {@link Entity} on the GUI screen.
	 * @param entity The {@link Entity} to draw on the screen
	 * @param x The X coordinate of the bottom of the {@link Entity}'s feet
	 * @param y The Y coordinate of the bottom of the {@link Entity}'s feet
	 * @param size The {@link Entity} size
	 * @param followCursor If true, the {@link Entity} will face towards the mouse cursor
	 * @apiNote Still in {@link Beta}. May cause issues and crashes.
	 * @apiNote Some {@link EntityRenderer}s <b>do not support rendering</b>
	 * their corresponding {@link Entity}s <b>while not in-game</b>.
	 */
	@Experimental
	public final void drawTEntity(Entity entity, int x, int y, int size, boolean followCursor)
	{
		//null check
		if(entity == null) return;
		
		//prepare to handle living entities as well
		int mouseX = followCursor ? this.mouseX + (size/2) : x + 100;
		int mouseY = followCursor ? this.mouseY + (size/2) : y + 50;
		final @Nullable LivingEntity livingEntity = (entity instanceof LivingEntity) ? (LivingEntity)entity : null;
		if(livingEntity != null && MC_CLIENT.world != null /*client world is required for vanilla rendering*/)
		{
			//use Vanilla rendering for living entities, so i don't have to keep that part up-to-date by myself
			InventoryScreen.drawEntity(this, x, y, size, x - mouseX, y - mouseY, livingEntity);
			return;
		}
		
		//prepare the context - push stuff
		//(position and rotate the entity appropriately)
		float atanMouseX40 = (float)Math.atan(((mouseX - x) / 40.0F));
		float atanMouseY40 = -(float)Math.atan(((mouseY - y) / 40.0F));
		Quaternionf quaternionf = new Quaternionf().rotateZ(3.1415927F).rotateY(3.1415927F);
		Quaternionf quaternionf2 = (new Quaternionf()).rotateX(atanMouseY40 * 20.0F * 0.017453292F);
		quaternionf.mul((Quaternionfc)quaternionf2);
		
		float i = entity.getYaw(), j = entity.getPitch();
		entity.setYaw(180.0F + atanMouseX40 * 40.0F);
		entity.setPitch(-atanMouseY40 * 20.0F);
		float h = 0, k = 0, l = 0;
		if(livingEntity != null)
		{
			h = livingEntity.bodyYaw;
			k = livingEntity.prevHeadYaw;
			l = livingEntity.headYaw;
			livingEntity.bodyYaw = 180.0F + atanMouseX40 * 20.0F;
			livingEntity.headYaw = entity.getYaw();
			livingEntity.prevHeadYaw = entity.getYaw();
		}
		
		//render
		__drawTEntity(x, y, size, quaternionf, quaternionf2, entity);
		
		//un-prepare the context - pop stuff
		//(return the entity back to its initial state)
		entity.setYaw(i);
		entity.setPitch(j);
		if(livingEntity != null)
		{
			livingEntity.bodyYaw = h;
			livingEntity.prevHeadYaw = k;
			livingEntity.headYaw = l;
		}
	}
	
	//note: don't forget to make sure this method is up-to-date with InventoryScreen#drawEntity
	private final @Internal void __drawTEntity
	(int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, Entity entity)
	{
		//push matrices
		final var matrices = getMatrices();
		matrices.push();
		matrices.translate(x, y, 50);
		matrices.multiplyPositionMatrix(new Matrix4f().scaling(size, size, size));
		matrices.multiply(quaternionf);
		DiffuseLighting.method_34742();
		
		//do the vanilla rendering method
		if(quaternionf2 != null)
		{
			quaternionf2.conjugate();
			ERD.setRotation(quaternionf2);
		}
		if(ERD.camera == null) ERD.camera = ERD_CAMERA;
		
		ERD.setRenderShadows(false);
		ERD.render(entity, 0, 0, 0, 0, 1, getMatrices(), getVertexConsumers(), 15728880);
		draw();
		ERD.setRenderShadows(true);
		
		//pop matrices
		matrices.pop();
		DiffuseLighting.enableGuiDepthLighting();
	}
	// ==================================================
}