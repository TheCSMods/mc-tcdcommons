package io.github.thecsdev.tcdcommons.api.util.integrity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.annotations.Beta;

import net.minecraft.util.Util;

/**
 * Utility class for antivirus operations.
 * 
 * @author TheCSDev
 */
@Beta
public final class AntivirusUtils
{
	// ==================================================
	private AntivirusUtils() {}
	// ==================================================
	/**
	 * Scans a specified path using the antivirus program appropriate for the current operating system.
	 *
	 * @param path The path to scan.
	 * @return true If the scan was started successfully, false otherwise.
	 */
	public static boolean scan(String path)
	{
		//log and prepare
		System.out.println("==============================");
		System.out.println(String.format("[%s] Scanning path: %s", AntivirusUtils.class.getSimpleName(), path));
		boolean result = false;
		
		// Verify path validity
		if (Files.exists(Paths.get(path)))
		{
			// Obtain the current OS info, and perform a scan depending on the current OS
			switch(Util.getOperatingSystem())
			{
				case WINDOWS:
					path = path.replace('/', '\\');
					if(!__winDefenderScan(path)) break;
					result = true;
				default:
					result = false;
					break;
			}
		}
		else result = false;
		
		// Finish
		if(result == false)
		{
			System.out.println(String.format("[%s] Failed to scan the path: %s",
				AntivirusUtils.class.getSimpleName(), path));
		}
		System.out.println("==============================");
		return result;
	}
	
	/**
	 * Scans the "mods" directory of the currently running instance of the program.
	 * 
	 * @return true If the scan was started successfully, false otherwise. 
	 */
	public static boolean scanMods() { return scan(System.getProperty("user.dir") + "/mods"); }
	// ==================================================
	/**
	 * Starts a scan of the specified path using Windows Defender.
	 *
	 * @param folderPath The path to scan.
	 * @return true If the scan was started successfully, false otherwise.
	 */
	private static boolean __winDefenderScan(String folderPath)
	{
		try
		{
			//prepare
			final String osDriveLetter = System.getProperty("user.home").substring(0, 1);
			final String avDir = osDriveLetter + ":\\\\Program Files\\Windows Defender\\";
			final String processArgs = "-Scan -ScanType 3 -File \"" + folderPath + "\"";
			
			//log
			System.out.println(String.format("[%s] MpCmdRun.exe %s", AntivirusUtils.class.getSimpleName(), processArgs));
			
			//execute process
			/*final var process = */new ProcessBuilder("\"" + avDir+ "MpCmdRun.exe\"", processArgs)
					.directory(new File(avDir))
					.start();
			
			// Read the output of the process
			// (used purely for debugging purposes, as the antivirus could take ages to finish scanning)
			/*System.out.println("");
			 BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
				System.err.println(line);
			System.out.println("");*/
			
		}
		catch (IOException e) { e.printStackTrace(); return false; }
		return true;
	}
	// ==================================================
}