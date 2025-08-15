package io.github.thecsdev.tcdcommons;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;
import io.github.thecsdev.tcdcommons.api.config.annotation.NonSerialized;
import io.github.thecsdev.tcdcommons.api.config.annotation.SerializedAs;

import java.util.Locale;

public class TCDCommonsConfig extends AutoConfig
{
	// ==================================================
	// FLAGS
	public static boolean DEV_ENV        = false;
	public static boolean ENABLE_NETWORK = false; //not ported to arch yet

	/**
	 * Defines whether or not certain features are available for
	 * a given user, such as specific API endpoints, and the
	 * user being shown certain links in GUI interfaces.
	 */
	public static final @NonSerialized boolean RESTRICTED_MODE;
	// ==================================================
	//mitigate side-effects of field renaming by using `@SerializedAs`
	public @SerializedAs("server-enablePlayerBadges")           boolean enablePlayerBadges           = false;
	public @SerializedAs("server-broadcastEarningPlayerBadges") boolean broadcastEarningPlayerBadges = true;
	public @SerializedAs("common-enableHttpUtils")              boolean enableHttpUtils              = true;
	// ==================================================
	public TCDCommonsConfig(String name) { super(name); }
	static
	{
		//define RESTRICTED_MODE value
		{
			boolean a = "cn,".contains(Locale.getDefault().getCountry().toLowerCase());
			/*boolean b = -- Crashes on Forge due to mod loading timing
					TCDCommons.isClient() &&
					Minecraft.getInstance().getUser().getType() == User.Type.LEGACY;*/
			RESTRICTED_MODE = a /*|| b*/;
		}
	}
	// ==================================================
}