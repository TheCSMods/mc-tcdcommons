package io.github.thecsdev.tcdcommons.api.util.io.repo;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;

/**
 * Represents information about a user that is registered on a repository hosting service.
 */
public abstract class RepositoryUserInfo
{
	// ==================================================
	/**
	 * A {@link String} representation of the unique ID assigned to the user.<br/>
	 * May be {@code null} if the user does not have a unique ID.
	 * @apiNote Not to be confused with the user's "unique account name"!
	 */
	public abstract @Nullable String getID();
	// --------------------------------------------------
	/**
	 * The "unique account name" of the user. If the hosting platform does not
	 * support or have a feature where accounts can have "unique" names, then
	 * return {@code null}.
	 */
	public abstract @Nullable String getName();
	
	/**
	 * The "display" name of the user's account, for example a username. This
	 * type of name does not have to be "unique" like how {@link #getName()} does.
	 */
	public abstract @Nullable Text getDisplayName();
	
	/**
	 * Typically the user's "biography"/"about me", if one is defined.
	 */
	public abstract @Nullable Text getDescription();
	// --------------------------------------------------
	/**
	 * If the hosting platform supports accounts having "profile pictures",
	 * then this should point to the URL endpoint where a copy of the
	 * user's "profile picture" may be obtained.
	 */
	public abstract @Nullable String getAvatarURL();
	// ==================================================
}