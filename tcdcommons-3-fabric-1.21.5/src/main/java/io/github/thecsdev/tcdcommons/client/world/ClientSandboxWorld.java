package io.github.thecsdev.tcdcommons.client.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.client.world.chunk.ClientSandboxChunkManager;
import io.github.thecsdev.tcdcommons.client.world.registry.ClientSandboxWorldDRM;
import io.github.thecsdev.tcdcommons.client.world.registry.DirectRegistryEntry;
import io.github.thecsdev.tcdcommons.client.world.tick.EmptyClientTickScheduler;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.map.MapState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay.Grouping;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;

@Beta
public @Virtual class ClientSandboxWorld extends World
{
	// ==================================================
	static
	{
		//define local stuff
		final var dim_type = new DimensionType(
				OptionalLong.of(6000), true, true, false, true,
				1d, true, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionTypes.OVERWORLD_ID, 0f,
				new DimensionType.MonsterSettings(false, true, UniformIntProvider.create(0, 7), 0));
		final var dim_reg_en = new DirectRegistryEntry<>(dim_type);
		//final var comb_dyn_reg = ClientDynamicRegistryType.createCombinedDynamicRegistries();
		
		//define static variables
		PROPERTIES = new ClientWorld.Properties(Difficulty.HARD, false, true);
		//COMBINED_DYNAMIC_REGISTRIES = comb_dyn_reg;
		DIMENSION_TYPE = dim_type;
		DIMENSION_TYPE_REGISTRY_ENTRY = dim_reg_en;
		IS_CLIENT = true;
		DEBUG_WORLD = true;
		BIOME_ACCESS = 0;
		MAX_CHAINED_NEIGHBOR_UPDATES = 1000000;
	}
	// --------------------------------------------------
	static final ClientWorld.Properties PROPERTIES;
	//static final CombinedDynamicRegistries<ClientDynamicRegistryType> COMBINED_DYNAMIC_REGISTRIES;
	static final DimensionType DIMENSION_TYPE;
	static final RegistryEntry<DimensionType> DIMENSION_TYPE_REGISTRY_ENTRY;
	static final boolean IS_CLIENT;
	static final boolean DEBUG_WORLD;
	static final long BIOME_ACCESS;
	static final int MAX_CHAINED_NEIGHBOR_UPDATES;
	// ==================================================
	protected final ClientSandboxChunkManager chunkManager;
	protected final List<PlayerEntity> players;
	protected final RecipeManager recipeManager;
	protected final ClientEntityManager<Entity> entityManager;
	protected final Map<MapIdComponent, MapState> mapStates;
	protected final QueryableTickScheduler<Block> blockTickScheduler;
	protected final QueryableTickScheduler<Fluid> fluidTickScheduler;
	protected final TickManager tickManager;
	protected final BrewingRecipeRegistry brewingRecipeRegistry;
	protected final FuelRegistry fuelRegistry;
	protected final List<EnderDragonPart> enderDragonParts = new ArrayList<EnderDragonPart>();
	// --------------------------------------------------
	protected Scoreboard scoreboard;
	// ==================================================
	public ClientSandboxWorld()
	{
		super(
				PROPERTIES,
				World.OVERWORLD,
				new ClientSandboxWorldDRM(),
				DIMENSION_TYPE_REGISTRY_ENTRY,
				IS_CLIENT,
				DEBUG_WORLD,
				BIOME_ACCESS,
				MAX_CHAINED_NEIGHBOR_UPDATES);
		this.chunkManager = new ClientSandboxChunkManager(this);
		this.players = new ArrayList<>();
		this.recipeManager = new RecipeManager()
		{
			public Grouping<StonecuttingRecipe> getStonecutterRecipes() { return Grouping.empty(); }
			public RecipePropertySet getPropertySet(RegistryKey<RecipePropertySet> key) { return RecipePropertySet.EMPTY; }
		};
		this.entityManager = new ClientSandboxEntityManager();
		this.mapStates = new HashMap<>();
		this.blockTickScheduler = new EmptyClientTickScheduler<>();
		this.fluidTickScheduler = new EmptyClientTickScheduler<>();
		//
		this.scoreboard = new Scoreboard();
		//
		this.tickManager = new TickManager();
		this.brewingRecipeRegistry = BrewingRecipeRegistry.EMPTY;
		//this.fuelRegistry = FuelRegistry.createDefault(getRegistryManager(), getEnabledFeatures()); -- doesn't work
		{
			final var fr_clinit = FuelRegistry.class.getDeclaredConstructors()[0];
			fr_clinit.setAccessible(true);
			FuelRegistry fr_instance = null;
			try { fr_instance = (FuelRegistry)fr_clinit.newInstance(Object2IntSortedMaps.emptyMap()); }
			catch(Exception e) { throw new RuntimeException("Failed to create a 'FuelRegistry' instance.", e); }
			this.fuelRegistry = fr_instance;
		}
	}
	// ==================================================
	public final @Override void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, Emitter emitter) {}
	public final @Override ChunkManager getChunkManager() { return this.chunkManager; }
	public final @Override void syncWorldEvent(Entity arg0, int arg1, BlockPos arg2, int arg3) {}
	public final @Override List<? extends PlayerEntity> getPlayers() { return this.players; }
	public final @Override FeatureSet getEnabledFeatures() { return FeatureFlags.DEFAULT_ENABLED_FEATURES; }
	public final @Override float getBrightness(Direction direction, boolean shaded) { return 1; }
	//public final @Override MapIdComponent increaseAndGetMapId() { return new MapIdComponent(0); }
	public final @Override Scoreboard getScoreboard() { return this.scoreboard; }
	public final void setScoreboard(Scoreboard scoreboard)
	{
		if(scoreboard == null) scoreboard = new Scoreboard();
		this.scoreboard = scoreboard;
	}
	public final @Override void playSound(Entity arg0, double arg1, double arg2, double arg3, RegistryEntry<SoundEvent> arg4, SoundCategory arg5, float arg6, float arg7, long arg8) {}
	public final @Override void playSoundFromEntity(Entity arg0, Entity arg1, RegistryEntry<SoundEvent> arg2, SoundCategory arg3, float arg4, float arg5, long arg6) {}
	public final @Override String asString() { return "Chunks[C] W: " + this.chunkManager.getDebugString() + " E: " + this.entityManager.getDebugString(); }
	public final @Override void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {}
	public final @Override void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {}
	public final @Override RecipeManager getRecipeManager() { return this.recipeManager; }
	public final @Override MapState getMapState(MapIdComponent id) { return this.mapStates.get(id); }
	//public final @Override void putMapState(MapIdComponent id, MapState state) { this.mapStates.put(id, state); }
	public final @Override QueryableTickScheduler<Block> getBlockTickScheduler() { return this.blockTickScheduler; }
	public final @Override QueryableTickScheduler<Fluid> getFluidTickScheduler() { return this.fluidTickScheduler; }
	protected final @Override EntityLookup<Entity> getEntityLookup() { return this.entityManager.getLookup(); }
	public final @Override Entity getEntityById(int id) { return getEntityLookup().get(id); }
	public final @Override RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) { return getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS); }
	public final @Override TickManager getTickManager() { return this.tickManager; }
	public final @Override BrewingRecipeRegistry getBrewingRecipeRegistry() { return this.brewingRecipeRegistry; }
	public final @Override int getSeaLevel() { return 0; }
	public final @Override void createExplosion(Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x,
			double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType,
			ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry<SoundEvent> soundEvent) {}
	public final @Override FuelRegistry getFuelRegistry() { return this.fuelRegistry; }
	public final @Override Collection<EnderDragonPart> getEnderDragonParts() { return this.enderDragonParts; }
	// ==================================================
}