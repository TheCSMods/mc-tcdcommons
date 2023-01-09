package io.github.thecsdev.tcdcommons.api.client.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import io.github.thecsdev.tcdcommons.api.registry.TCDCommonsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.client.gui.GuiUtils;

public final class TCDCommonsClientRegistry extends TCDCommonsRegistry
{
	// ==================================================
	protected TCDCommonsClientRegistry() {}
	// ==================================================
	/**
	 * Contains a set of size offsets to apply to entities
	 * rendered on the screen with the {@link TEntityRendererElement}.
	 */
	public static final HashMap<Class<? extends Entity>, Supplier<Double>> TEntityRenderer_SizeOffsets;
	
	/**
	 * Contains a set of {@link Screen}s that will be rendered
	 * on the {@link InGameHud}. Keep in mind that rendering a
	 * screen on the HUD is only a visual thing, and not functional.
	 * In other words, a {@link Screen} will not be able to handle user input.<br/>
	 * <br/>
	 * <b>Note:</b> Please remember to initialize the screen after adding it
	 * here, as it will not be initialized manually.
	 * @see {@link GuiUtils#initScreen(Screen)}
	 */
	public static final HashMap<ResourceLocation, Screen> InGameHud_Screens;
	// --------------------------------------------------
	/**
	 * Calls the static constructor for this class
	 * if it hasn't been called yet.
	 */
	public static void init() {}
	static
	{
		//define the registries
		TEntityRenderer_SizeOffsets = Maps.newHashMap();
		InGameHud_Screens = Maps.newHashMap();
		
		//the default settings
		TEntityRenderer_SizeOffsets.put(EnderDragon.class, () -> 4d);
	}
	// ==================================================
	/**
	 * Returns an entity size offset using {@link #TEntityRenderer_SizeOffsets} for
	 * when an entity is rendered using {@link TEntityRendererElement}.
	 */
	public static <T extends Entity> double getEntityRendererSizeOffset(Class<T> entityClass)
	{
		//default outcome is 1d (100%), aka no offset
		var supplier = TEntityRenderer_SizeOffsets.getOrDefault(entityClass, () -> 1d);
		return supplier.get();
	}
	// --------------------------------------------------
	/**
	 * Re-initializes all screens in {@link #InGameHud_Screens}.
	 */
	public static void reInitHudScreens()
	{
		//get needed variables
		Minecraft c = Minecraft.getInstance();
		int w = c.getWindow().getGuiScaledWidth();
		int h = c.getWindow().getGuiScaledHeight();
		//iterate all in game hud screens
		for(Screen screen : InGameHud_Screens.values())
			if(screen != null)
				screen.init(c, w, h);
	}
	// ==================================================
}
