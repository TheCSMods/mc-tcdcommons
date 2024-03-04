package io.github.thecsdev.tcdcommons;

import java.util.Locale;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;
import io.github.thecsdev.tcdcommons.api.config.annotation.NonSerialized;
import io.github.thecsdev.tcdcommons.api.config.annotation.SerializedAs;

public class TCDCommonsConfig extends AutoConfig
{
	// ==================================================
	/**
	 * Defines whether or not certain features are available for
	 * a given user, such as specific API endpoints, and the
	 * user being shown certain links in GUI interfaces.
	 */
	public static final @NonSerialized boolean RESTRICTED_MODE;
	// ==================================================
	//mitigate side-effects of field renaming by using `@SerializedAs`
	public @SerializedAs("server-enablePlayerBadges") boolean enablePlayerBadges = false;
	public @SerializedAs("server-broadcastEarningPlayerBadges") boolean broadcastEarningPlayerBadges = true;
	public @SerializedAs("common-enableHttpUtils") boolean enableHttpUtils = true;
	// ==================================================
	public TCDCommonsConfig(String name) { super(name); }
	static
	{
		//define RESTRICTED_MODE value
		{
			final var lc = Locale.getDefault().getCountry().toLowerCase();
			final var a = "cn".equals(lc);
			RESTRICTED_MODE = a; //TODO - How do I check if the client is authenticated with Mojang?
		}
	}
	// ==================================================
}