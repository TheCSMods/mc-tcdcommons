package io.github.thecsdev.tcdcommons;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.github.thecsdev.tcdcommons.api.util.io.cache.CachedResourceManager;
import io.github.thecsdev.tcdcommons.network.TCDCommonsNetwork;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class TCDCommons extends Object
{
	// ==================================================
	public static final @Internal Gson GSON = new Gson();
	// ==================================================
	public static final Logger LOGGER = LoggerFactory.getLogger(getModID());
	// --------------------------------------------------
	private static final String ModID = "tcdcommons";
	private static TCDCommons Instance;
	// --------------------------------------------------
	protected final ModContainer modInfo;
	private final TCDCommonsConfig config;
	public final String userAgent;
	// ==================================================
	public TCDCommons()
	{
		//validate instance first
		if(isModInitialized())
			throw new IllegalStateException(getModID() + " has already been initialized.");
		else if(!isInstanceValid(this))
			throw new UnsupportedOperationException("Invalid " + getModID() + " type: " + this.getClass().getName());
		
		//assign instance
		Instance = this;
		this.modInfo = FabricLoader.getInstance().getModContainer(getModID()).get();
		this.userAgent = this.modInfo.getMetadata().getContact().get("user_agent").orElse(getModID());
		
		//log stuff
		LOGGER.info("Initializing '" + getModName() + "' " + modInfo.getMetadata().getVersion() +
				" as '" + getClass().getSimpleName() + "'.");
		//LOGGER.info("Initializing '" + getModID() + "' as '" + getClass().getSimpleName() + "'.");
		
		//load config
		this.config = new TCDCommonsConfig(getModID());
		this.config.loadFromFileOrCrash(true); //important to crash on fail, as there are important config variables
		
		//init stuff
		TCDCommonsNetwork.init();
		CachedResourceManager.init();
		
		//FIXME - Important checklist that is MUST-VERIFY before porting to higher version:
		//- Ensure mixin.events.MixinItemEntity is working as intended, as it uses @ModifyVariable
	}
	// ==================================================
	public static TCDCommons getInstance() { return Instance; }
	public ModContainer getModInfo() { return modInfo; }
	public TCDCommonsConfig getConfig() { return this.config; }
	// --------------------------------------------------
	public static String getModName() { return getInstance().getModInfo().getMetadata().getName(); }
	public static String getModID() { return ModID; }
	// --------------------------------------------------
	public static boolean isModInitialized() { return isInstanceValid(Instance); }
	private static boolean isInstanceValid(TCDCommons instance) { return isServer(instance) || isClient(instance); }
	// --------------------------------------------------
	public static boolean isServer() { return isServer(Instance); }
	public static boolean isClient() { return isClient(Instance); }
	
	private static boolean isServer(TCDCommons arg0) { return arg0 instanceof io.github.thecsdev.tcdcommons.server.TCDCommonsServer; }
	private static boolean isClient(TCDCommons arg0) { return arg0 instanceof io.github.thecsdev.tcdcommons.client.TCDCommonsClient; }
	// ==================================================
}