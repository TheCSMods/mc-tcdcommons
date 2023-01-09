package io.github.thecsdev.tcdcommons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(TCDCommons.ModID)
public class TCDCommons extends Object
{
	// ==================================================
	private static TCDCommons Instance;
	// --------------------------------------------------
	public static final Logger LOGGER = LoggerFactory.getLogger(getModID());
	// --------------------------------------------------
	public static final String ModName = "TCDCommons API";
	public static final String ModID   = "tcdcommons";
	// --------------------------------------------------
	public final ModContainer ModInfo;
	// ==================================================
	public TCDCommons()
	{
		//assign final stuff
		ModInfo = ModList.get().getModContainerById(ModID).get();
		
		//validate instance
		if(isModInitialized())
			crash("Attempting to initialize " + ModID, new RuntimeException(ModID + " has already been initialized."));
		
		//on initialize
		if(getClass().equals(TCDCommons.class)) //check if not a subclass
		{
			//depending on the side, initialize NoUnusedChunks
			if(FMLEnvironment.dist.isClient())
				new io.github.thecsdev.tcdcommons.client.TCDCommonsClient();
			else if(FMLEnvironment.dist.isDedicatedServer())
				new io.github.thecsdev.tcdcommons.server.TCDCommonsServer();
			else
				crash("Attempting to initialize " + ModID, new RuntimeException("Invalid FMLEnvironment.dist()"));
			
			//do not proceed, return
			return;
		}
		
		//assign instance
		Instance = this;
		
		//log init
		LOGGER.info("Initializing '" + getModName() + "' as '" + getClass().getSimpleName() + "'.");
		
		//register this class as event listener
		MinecraftForge.EVENT_BUS.register(this);
	}
	// --------------------------------------------------
	/**
	 * Throws a {@link ReportedException} using a
	 * {@link CrashReport}.
	 */
	public static void crash(String crashMessage, Throwable exception)
	{
		CrashReport crashReport = CrashReport.forThrowable(exception, crashMessage);
		throw new ReportedException(crashReport);
	}
	// ==================================================
	public static String getModName() { return ModName; }
	public static String getModID() { return ModID; }
	public static TCDCommons getInstance() { return Instance; }
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
