package io.github.thecsdev.tcdcommons.client.world;

import static io.github.thecsdev.tcdcommons.client.TCDCommonsClient.MC_CLIENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.client.world.chunk.ClientSandboxChunkManager;
import io.github.thecsdev.tcdcommons.client.world.registry.ClientSandboxWorldDRM;
import io.github.thecsdev.tcdcommons.client.world.registry.DirectRegistryEntry;
import io.github.thecsdev.tcdcommons.client.world.tick.EmptyClientTickScheduler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
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
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
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
		PROFILER = () -> MC_CLIENT.getProfiler();
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
	static final Supplier<Profiler> PROFILER;
	static final boolean IS_CLIENT;
	static final boolean DEBUG_WORLD;
	static final long BIOME_ACCESS;
	static final int MAX_CHAINED_NEIGHBOR_UPDATES;
	// ==================================================
	protected final ClientSandboxChunkManager chunkManager;
	protected final List<PlayerEntity> players;
	protected final RecipeManager recipeManager;
	protected final ClientEntityManager<Entity> entityManager;
	protected final Map<String, MapState> mapStates;
	protected final QueryableTickScheduler<Block> blockTickScheduler;
	protected final QueryableTickScheduler<Fluid> fluidTickScheduler;
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
				PROFILER,
				IS_CLIENT,
				DEBUG_WORLD,
				BIOME_ACCESS,
				MAX_CHAINED_NEIGHBOR_UPDATES);
		this.chunkManager = new ClientSandboxChunkManager(this);
		this.players = new ArrayList<>();
		this.recipeManager = new RecipeManager();
		this.entityManager = new ClientSandboxEntityManager();
		this.mapStates = new HashMap<>();
		this.blockTickScheduler = new EmptyClientTickScheduler<>();
		this.fluidTickScheduler = new EmptyClientTickScheduler<>();
		//
		this.scoreboard = new Scoreboard();
	}
	// ==================================================
	public final @Override void emitGameEvent(GameEvent event, Vec3d emitterPos, Emitter emitter) {}
	public final @Override ChunkManager getChunkManager() { return this.chunkManager; }
	public final @Override void syncWorldEvent(PlayerEntity arg0, int arg1, BlockPos arg2, int arg3) {}
	public final @Override List<? extends PlayerEntity> getPlayers() { return this.players; }
	public final @Override FeatureSet getEnabledFeatures() { return FeatureFlags.DEFAULT_ENABLED_FEATURES; }
	public final @Override float getBrightness(Direction direction, boolean shaded) { return 1; }
	public final @Override int getNextMapId() { return 0; }
	public final @Override Scoreboard getScoreboard() { return this.scoreboard; }
	public final void setScoreboard(Scoreboard scoreboard)
	{
		if(scoreboard == null) scoreboard = new Scoreboard();
		this.scoreboard = scoreboard;
	}
	public final @Override void playSound(@Nullable PlayerEntity except, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {}
	public final @Override void playSoundFromEntity(@Nullable PlayerEntity except, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {}
	public final @Override String asString() { return "Chunks[C] W: " + this.chunkManager.getDebugString() + " E: " + this.entityManager.getDebugString(); }
	public final @Override void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {}
	public final @Override void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {}
	public final @Override RecipeManager getRecipeManager() { return this.recipeManager; }
	public final @Override MapState getMapState(String id) { return this.mapStates.get(id); }
	public final @Override void putMapState(String id, MapState state) { this.mapStates.put(id, state); }
	public final @Override QueryableTickScheduler<Block> getBlockTickScheduler() { return this.blockTickScheduler; }
	public final @Override QueryableTickScheduler<Fluid> getFluidTickScheduler() { return this.fluidTickScheduler; }
	protected final @Override EntityLookup<Entity> getEntityLookup() { return this.entityManager.getLookup(); }
	public final @Override Entity getEntityById(int id) { return getEntityLookup().get(id); }
	public final @Override RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) { return getRegistryManager().get(RegistryKeys.BIOME).entryOf(BiomeKeys.PLAINS); }
	public final @Override TickManager getTickManager() { return new TickManager(); }
	// ==================================================
}