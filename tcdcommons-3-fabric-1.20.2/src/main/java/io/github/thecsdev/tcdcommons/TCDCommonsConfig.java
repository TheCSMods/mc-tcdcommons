package io.github.thecsdev.tcdcommons;

import io.github.thecsdev.tcdcommons.api.config.AutoConfig;
import io.github.thecsdev.tcdcommons.api.config.annotation.SerializedAs;

public class TCDCommonsConfig extends AutoConfig
{
	// ==================================================
	//mitigate side-effects of field renaming by using `@SerializedAs`
	public @SerializedAs("server-enablePlayerBadges") boolean enablePlayerBadges = false;
	public @SerializedAs("server-broadcastEarningPlayerBadges") boolean broadcastEarningPlayerBadges = true;
	public @SerializedAs("common-enableHttpUtils") boolean enableHttpUtils = true;
	// ==================================================
	public TCDCommonsConfig(String name) { super(name); }
	// ==================================================
}