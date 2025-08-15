package io.github.thecsdev.tcdcommons.api.util.io.cache;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;

import java.io.IOException;

/**
 * Represents a {@link CachedResource} fetching task used by the
 * {@link CachedResourceManager} to fetch the given resource.
 */
public interface IResourceFetchTask<R>
{
	// ==================================================
	/**
	 * Returns the {@link Class} representing the type of {@link CachedResource}
	 * this {@link IResourceFetchTask} will fetch.<br/>
	 * Used by {@link CachedResourceManager} to enforce type-safety with generics.
	 * @apiNote For example, if the cached resource is a {@link String}, then return
	 * "{@link String}{@code .class}".
	 */
	public Class<R> getResourceType();
	
	/**
	 * Returns an instance of the {@link Minecraft} or the {@link MinecraftServer}.
	 * Which of the two is returned depends on the side on which the task should be performed.
	 */
	public BlockableEventLoop<?> getMinecraftClientOrServer();
	// ==================================================
	/**
	 * Synchronously fetches the resource.
	 * @throws IOException If an {@link Exception} is raised while fetching the resource.
	 * @apiNote This method is invoked in a separate and unique {@link Thread}.
	 */
	public CachedResource<R> fetchResourceSync() throws Exception;
	// --------------------------------------------------
	/**
	 * Called when the resource is successfully fetched.
	 * @apiNote Invoked on the {@link #getMinecraftClientOrServer()}'s main {@link Thread}.
	 */
	public void onReady(R resource);
	
	/**
	 * Called when fetching the resource fails due to a raised {@link Exception}.
	 * @apiNote Invoked on the {@link #getMinecraftClientOrServer()}'s main {@link Thread}.
	 */
	public void onError(Exception exception);
	// ==================================================
}