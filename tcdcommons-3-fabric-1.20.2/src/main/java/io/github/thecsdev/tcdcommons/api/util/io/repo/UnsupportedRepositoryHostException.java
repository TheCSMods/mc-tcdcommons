package io.github.thecsdev.tcdcommons.api.util.io.repo;

/**
 * An {@link Exception} raised by a {@link RepositoryInfoProvider} that
 * doesn't support interactions with a given host's APIs.
 */
public class UnsupportedRepositoryHostException extends Exception
{
	// ==================================================
	private static final long serialVersionUID = 9149191200729728875L;
	// ==================================================
	public UnsupportedRepositoryHostException(String message) { super(message); }
	// ==================================================
}