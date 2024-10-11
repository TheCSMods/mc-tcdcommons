package io.github.thecsdev.tcdcommons.api.client.gui.other;

import static io.github.thecsdev.tcdcommons.client.ClientEntitySandbox.getCachedEntityFromType;
import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.client.gui.util.TDrawContext;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.client.font.MultilineText;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;

public @Virtual class TEntityRendererElement extends TBlankElement
{
	// ==================================================
	protected @Nullable Entity entity;
	protected @Nullable MultilineText entityTypeName;
	protected boolean entityErrorState; //when true, entity won't render, and its name will render instead
	
	/** The cached center XY coordinates for rendering the {@link #entity}. <b>tY</b> is for text. */
	protected int entityTextX, entityTextY;
	/** The cached calculated size at which the entity will render. */
	protected int entityDisplaySize;
	// --------------------------------------------------
	protected double entityScale;
	protected boolean followsCursor;
	// ==================================================
	public TEntityRendererElement(int x, int y, int width, int height) { this(x, y, width, height, null); }
	public TEntityRendererElement(int x, int y, int width, int height, EntityType<?> entityType)
	{
		super(x, y, width, height);
		setEntityScale(0.9);
		setFollowsCursor(true);
		setEntity(entityType); //set this last
	}
	// --------------------------------------------------
	public @Virtual @Override void setSize(int width, int height, int flags)
	{
		super.setSize(width, height, flags);
		recalcCache_mobSize();
	}
	// ==================================================
	public final @Nullable Entity getEntity() { return this.entity; }
	public final @Nullable EntityType<?> getEntityType() { return (this.entity != null ? this.entity.getType() : null); }
	public final void setEntity(@Nullable EntityType<?> entityType)
	{
		final var entity = getCachedEntityFromType(entityType);
		setEntity(entity);
		if(entity == null)
		{
			this.entityErrorState = true;
			//make sure the name is present in the event of errors
			if(entityType != null)
				updateEntityTypeName(entityType);
		}
	}
	public final void setEntity(@Nullable Entity entity)
	{
		//assign entity
		this.entity = entity;
		
		//assign entity type name
		updateEntityTypeName(getEntityType());
		
		//set-up flags and re-calculate size
		this.entityErrorState = false;
		recalcCache_mobSize();
	}
	//
	protected final @Internal void updateEntityTypeName(EntityType<?> entityType)
	{
		final var textRenderer = MC_CLIENT.textRenderer;
		if(entityType != null)
			this.entityTypeName = MultilineText.create(textRenderer, entityType.getName(), this.width);
		else this.entityTypeName = MultilineText.create(textRenderer, TextUtils.literal("-"));
	}
	//
	public final boolean getFollowsCursor() { return this.followsCursor; }
	public @Virtual void setFollowsCursor(boolean followsCursor) { this.followsCursor = followsCursor; }
	//
	public final double getEntityScale() { return this.entityScale; }
	public final void setEntityScale(double entityScale)
	{
		this.entityScale = MathHelper.clamp(entityScale, 0.1, 5);
		recalcCache_mobSize();
	}
	// --------------------------------------------------
	/**
	 * Recalculates the value of {@link #entityDisplaySize}.
	 */
	protected final void recalcCache_mobSize()
	{
		//null check (default size)
		if(this.entity == null) { this.entityDisplaySize = 30; return; }
		//calculate mob size
		int viewportSize = Math.min(this.width, this.height);
		this.entityDisplaySize = (int)(calculateEntityGUISize(this.entity, viewportSize) * this.entityScale);
	}
	
	/**
	 * Recalculates the values of {@link #entityTextX} and {@link #entityTextY}.
	 */
	protected final void recalcCache_cXY()
	{
		//calculate center XY
		this.entityTextX = (this.x + (this.width / 2));
		//calculate text Y for the entity name text
		if(this.entityTypeName != null)
		{
			final int fh = MC_CLIENT.textRenderer.fontHeight;
			this.entityTextY =
				(getY() + (getHeight() / 2)) +
				(fh / 2) -
				(this.entityTypeName.count() * fh);
		}
		else this.entityTextY = (getEndY() - (this.height / 4));
	}
	// ==================================================
	public @Virtual @Override void render(TDrawContext pencil)
	{
		recalcCache_cXY();
		
		//in the event of an error, render the entity's name text
		if(this.entityErrorState || this.entity == null)
		{
			if(this.entityTypeName != null)
				this.entityTypeName.drawCenterWithShadow(pencil, this.entityTextX, this.entityTextY);
			else pencil.drawTFill(TDrawContext.DEFAULT_ERROR_COLOR);
			return;
		}
		
		//render the entity normally here
		try
		{
			pencil.drawTEntity(
					this.entity,
					this.x, this.y, this.width, this.height,
					this.entityDisplaySize,
					this.followsCursor);
		}
		catch(Exception e) { this.entityErrorState = true; }
	}
	// ==================================================
	/**
	 * Contains a set of size offsets to apply to entities
	 * rendered on the screen with the {@link TEntityRendererElement}.
	 * @apiNote The type of this {@link Map} should be a {@link HashMap}, so as to not keep the insertion order.
	 */
	public static final Map<EntityType<?>, Supplier<Double>> EntityGuiSizeOffsets;
	static
	{
		EntityGuiSizeOffsets = new HashMap<>(); //do not keep order
		EntityGuiSizeOffsets.put(EntityType.ENDER_DRAGON, () -> 4d);
	}
	// --------------------------------------------------
	/**
	 * Returns an entity size offset using {@link #EntityGuiSizeOffsets} for
	 * when an entity is rendered using {@link TEntityRendererElement}.
	 */
	public static double getEntityGuiSizeOffset(EntityType<?> entityType)
	{
		return EntityGuiSizeOffsets.getOrDefault(entityType, () -> 1d).get();
	}
	
	/**
	 * Calculates the GUI {@link Entity} size given the viewport size
	 * and the entity rendering size offsets.
	 * @param entity The target {@link Entity}
	 * @param viewportSize The smallest viewport size, be it the width or the height.
	 */
	public static int calculateEntityGUISize(Entity entity, int viewportSize)
	{
		//null check
		final int maxVal = (int) (50 * ((float)viewportSize / 80));
		if(entity == null) return maxVal;
		
		//calculate default gui size
		int result = maxVal;
		{
			//return size based on entity model size
			float f1 = entity.getType().getDimensions().width(), f2 = entity.getType().getDimensions().height();
			double d0 = Math.sqrt((f1 * f1) + (f2 * f2));
			
			//calculate and return
			if(d0 == 0) d0 = 0.1;
			result = (int) (maxVal / d0);
		}
		
		//apply any offsets
		result *= getEntityGuiSizeOffset(entity.getType());
		
		//return the result
		return result;
	}
	// ==================================================
}