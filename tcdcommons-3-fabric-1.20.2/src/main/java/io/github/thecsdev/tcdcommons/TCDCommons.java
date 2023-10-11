package io.github.thecsdev.tcdcommons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.thecsdev.tcdcommons.network.TCDCommonsNetworkHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class TCDCommons extends Object
{
	// ==================================================
	public static final Logger LOGGER = LoggerFactory.getLogger(getModID());
	// --------------------------------------------------
	private static final String ModID = "tcdcommons";
	private static TCDCommons Instance;
	// --------------------------------------------------
	protected final ModContainer modInfo;
	private final TCDCommonsConfig config;
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
		modInfo = FabricLoader.getInstance().getModContainer(getModID()).get();
		
		//log stuff
		LOGGER.info("Initializing '" + getModName() + "' " + modInfo.getMetadata().getVersion() +
				" as '" + getClass().getSimpleName() + "'.");
		//LOGGER.info("Initializing '" + getModID() + "' as '" + getClass().getSimpleName() + "'.");
		
		//load config
		this.config = new TCDCommonsConfig(getModID());
		this.config.loadFromFileOrCrash(true); //important to crash on fail, as there are important config variables
		
		//init stuff
		TCDCommonsNetworkHandler.init();
		
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