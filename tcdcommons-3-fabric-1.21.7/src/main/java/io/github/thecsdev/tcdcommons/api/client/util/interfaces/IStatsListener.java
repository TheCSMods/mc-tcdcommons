package io.github.thecsdev.tcdcommons.api.client.util.interfaces;

import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;

/**
 * Allows {@link Screen}s and {@link TScreen}s to listen for statistics updates.
 * @implNote Mojang, why'd you remove the "StatsListener" interface from the vanilla game?
 */
public interface IStatsListener
{
	/**
	 * Executed when the client receives a {@link StatisticsS2CPacket}.
	 */
	public void onStatsReady();
}