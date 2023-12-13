package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import java.net.MalformedURLException;
import java.net.URL;

import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfoProvider;
import io.github.thecsdev.tcdcommons.api.util.io.repo.ugc.RepositoryInfo;

/**
 * A {@link RepositoryInfoProvider} for repositories hosted on GitHub.
 */
public final class GitHubRepositoryInfoProvider extends RepositoryInfoProvider
{
	public final @Override RepositoryInfo fetchRepositoryInfoSync(String repoUrl)
	{
		try
		{
			//check host
			final URL url = new URL(repoUrl);
			final String host = url.getHost().toLowerCase();
			if(!host.equals("github.com") && !host.equals("www.github.com"))
				return null;
			
			//obtain username and repository name
			final String[] pathSegments = url.getPath().split("/");
			if(pathSegments.length < 3) return null;
			final String owner_login = pathSegments[1];
			final String repo_name = pathSegments[2];
			
			//create and return the repository info
			return GitHubRepositoryInfo.fetchRepositoryInfoSync(owner_login, repo_name);
		}
		catch(MalformedURLException mue) {/*malformed URLs are just unsupported non-GitHub repositories*/}
		catch(Exception exc)
		{
			throw new RuntimeException(
					String.format("Failed to fetch repository info for '%s'.", repoUrl),
					exc);
		}
		return null;
	}
}