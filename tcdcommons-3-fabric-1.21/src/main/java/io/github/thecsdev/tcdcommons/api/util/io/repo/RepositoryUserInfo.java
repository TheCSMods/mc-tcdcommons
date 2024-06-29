package io.github.thecsdev.tcdcommons.api.util.io.repo;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.Nullable;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import io.github.thecsdev.tcdcommons.api.util.io.HttpUtils;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResource;
import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.api.util.io.cache.IResourceFetchTask;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * Provides information about a given user whose account is registered on a repository hosting platform.
 * @see RepositoryHostInfo
 */
@Deprecated(since = "v3.12", forRemoval = true)
public abstract class RepositoryUserInfo
{
	// ==================================================
	/**
	 * Returns the {@link RepositoryHostInfo} about the host that hosts this user's account.
	 */
	public abstract RepositoryHostInfo getHost();
	
	/**
	 * Returns a {@link String} representation of the user's unique account identifier.
	 * @apiNote Not to be confused with the account's unique username.
	 */
	public abstract String getID();
	// --------------------------------------------------
	/**
	 * Returns the unique username of the account. This username has to
	 * be unique to this account on the entire repository platform.
	 */
	public abstract String getAccountName();
	
	/**
	 * Returns the "display name" for this account. Does not have to be "unique".
	 */
	public abstract String getDisplayName();
	
	/**
	 * Returns the user's "biography"/"about me" text, if there is one.
	 * May return {@code null} if the user doesn't have one.
	 */
	public abstract @Nullable String getBiography();
	// --------------------------------------------------
	/**
	 * Returns the {@link URI} that points to the resource that
	 * holds the user's avatar image. Could be a WWW URL or a file.
	 * May also be {@code null} if the user does not have an avatar image.
	 */
	public @Virtual @Nullable URI getAvatarImageURI() { return null; }
	
	/**
	 * Returns the number of "followers" this user has, or {@code null} if unsupported.
	 */
	public @Virtual @Nullable BigInteger getFollowerCount() { return null; }
	
	/**
	 * Returns the number of users this user "follows", or {@code null} if unsupported.
	 */
	public @Virtual @Nullable BigInteger getFollowingCount() { return null; }
	
	/**
	 * Returns the number of public repositories this user has, or {@code null} if unsupported.
	 */
	public @Virtual @Nullable BigInteger getRepositoryCount() { return null; }
	// ==================================================
	public @Override int hashCode() { return Objects.hash(getHost(), getID()); }
	public @Override boolean equals(Object obj)
	{
		if(obj == null || !Objects.equals(getClass(), obj.getClass()))
			return false;
		else if(obj == this) return true;
		
		final RepositoryUserInfo cObj = (RepositoryUserInfo)obj;
		return Objects.equals(getHost(), cObj.getHost()) &&
				Objects.equals(getID(), cObj.getID());
	}
	// ==================================================
	/**
	 * Asynchronously fetches the "png" image bytes of the user's avatar image.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @see #getAvatarImageURI()
	 */
	public final void getAvatarImageAsync(
			ThreadExecutor<?> minecraftClientOrServer,
			Consumer<byte[]> onReady,
			Consumer<Exception> onError) throws NullPointerException
	{
		RepositoryInfoProvider.getInfoAsync(
				minecraftClientOrServer,
				onReady, onError,
				this::fetchAvatarImageSync);
	}
	
	/**
	 * Synchronously fetches the "png" image bytes of the user's avatar image.
	 * @throws NullPointerException If a non-{@link Nullable} method in this {@link Object} returns {@code null}.
	 * @throws InvalidIdentifierException If {@link RepositoryHostInfo#getID()}
	 * returns a {@link String} that is incompatible with {@link Identifier} namespaces.
	 * @throws IOException If an {@link IOException} is raised while fetching.
	 * @see #getAvatarImageURI()
	 */
	public @Virtual byte[] fetchAvatarImageSync() throws NullPointerException, InvalidIdentifierException, IOException
	{
		//prepare to fetch; obtain needed variables
		final URI imgUri = Objects.requireNonNull(getAvatarImageURI());
		final Identifier imgId = Identifier.of(
				Objects.requireNonNull(getHost().getID()),
				"tcdcommons-user_avatars/" + Objects.requireNonNull(getID()) + ".png");
		
		//fetch the image
		final AtomicReference<byte[]> result = new AtomicReference<>();
		final AtomicReference<Exception> error = new AtomicReference<>();
		CachedResourceManager.getResourceSync(imgId, new IResourceFetchTask<byte[]>()
		{
			public Class<byte[]> getResourceType() { return byte[].class; }
			public ThreadExecutor<?> getMinecraftClientOrServer() { return null; }
			public void onReady(byte[] resource) { result.set(resource); }
			public void onError(Exception exception) { error.set(exception); }
			public CachedResource<byte[]> fetchResourceSync() throws Exception
			{
				final var bytes = HttpUtils.httpGetSyncB(imgUri, new BasicHeader("Accept", "image/png"));
				return CachedResource.ofBytes(bytes, Instant.now().plus(Duration.ofDays(7)));
			}
		});
		
		//handle the results
		if(error.get() != null)
			throw ((error.get()) instanceof IOException) ?
					(IOException)error.get() :
					new IOException("Failed to fetch user's avatar image.", error.get());
		return Objects.requireNonNull(result.get());
	}
	// ==================================================
}