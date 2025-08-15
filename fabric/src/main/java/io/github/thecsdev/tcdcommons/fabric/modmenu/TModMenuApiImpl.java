package io.github.thecsdev.tcdcommons.fabric.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.thecsdev.tcdcommons.test.client.gui.screen.TestTScreen;
import net.fabricmc.loader.api.FabricLoader;

public final class TModMenuApiImpl implements ModMenuApi
{
	public final @Override ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return parent -> FabricLoader.getInstance().isDevelopmentEnvironment() ?
				new TestTScreen(parent).getAsScreen() : null;
	}
}