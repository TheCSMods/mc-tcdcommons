package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryUserInfo;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * A {@link RepositoryInfoProvider} for GitHub.
 */
@Deprecated(since = "v3.12", forRemoval = true)
public final class GitHubRepositoryInfoProvider extends RepositoryInfoProvider
{
	// ==================================================
	private static final GitHubRepositoryInfoProvider INSTANCE = new GitHubRepositoryInfoProvider();
	// ==================================================
	private GitHubRepositoryInfoProvider() {}
	public static final GitHubRepositoryInfoProvider getInstance() { return INSTANCE; }
	// --------------------------------------------------
	/**
	 * Returns {@code true} if the host returned by {@link URI#getHost()} is
	 * supported by this {@link GitHubRepositoryInfoProvider}.
	 */
	private static final @Internal boolean assertSupportedHost(URI uri) throws NullPointerException
	{
		final @Nullable var host = Objects.requireNonNull(uri).getHost();
		if(!Objects.equals(host, "github.com") && !Objects.equals(host, "www.github.com"))
			return false;
		else return true;
	}
	// ==================================================
	public final @Nullable @Override RepositoryInfo fetchRepoInfoSync(URI repoUri)
			throws NullPointerException, IOException
	{
		//return null if unsupported
		if(!assertSupportedHost(repoUri)) return null;
		
		//obtain and validate path
		final var path = getUriPathEntries(repoUri);
		if(path.length < 2)
			throw new IOException(
					"Unable to fetch GitHub repository info.",
					new IllegalArgumentException(String.format(
							"The given URI's path is invalid '%s'.", repoUri.getPath())));
		
		//fetch
		return GitHubHostInfo.getInstance().fetchRepoInfoByNameSync(path[0], path[1]);
	}
	// --------------------------------------------------
	public final @Nullable @Override RepositoryUserInfo fetchUserInfoSync(URI userUri)
			throws NullPointerException, IOException
	{
		//return null if unsupported
		if(!assertSupportedHost(userUri)) return null;

		//obtain and validate path
		final var path = getUriPathEntries(userUri);
		if(path.length < 1)
			throw new IOException(
					"Unable to fetch GitHub user info.",
					new IllegalArgumentException(String.format(
							"The given URI's path is invalid '%s'.", userUri.getPath())));
		
		//fetch
		return GitHubHostInfo.getInstance().fetchUserInfoByNameSync(path[0]);
	}
	// ==================================================
}