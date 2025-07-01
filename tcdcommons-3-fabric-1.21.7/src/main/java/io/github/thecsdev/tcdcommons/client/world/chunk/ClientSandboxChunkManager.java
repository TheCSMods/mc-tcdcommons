package io.github.thecsdev.tcdcommons.client.world.chunk;

import java.util.function.BooleanSupplier;

import org.slf4j.Logger;

import com.google.common.annotations.Beta;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

@Beta
public @Virtual class ClientSandboxChunkManager extends ChunkManager
{
	// ==================================================
	public static final Logger LOGGER = TCDCommons.LOGGER;
	// --------------------------------------------------
	protected final World world; //accept all world types
	//
	protected final WorldChunk emptyChunk;
	protected final LightingProvider lightingProvider;
	// ==================================================
	public ClientSandboxChunkManager(World world)
	{
		this.world = world;
		this.emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0), world.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
		this.lightingProvider = new LightingProvider(this, true, world.getDimension().hasSkyLight());
	}
	// ==================================================
	public final @Override BlockView getWorld() { return this.world; }
	public final @Override LightingProvider getLightingProvider() { return this.lightingProvider; }
	//
	public @Virtual @Override Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) { return this.emptyChunk; }
	public @Virtual @Override int getLoadedChunkCount() { return 0; }
	public @Virtual @Override void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {}
	public @Virtual @Override String getDebugString() { return getLoadedChunkCount() + ", " + getLoadedChunkCount(); }
	// ==================================================
}