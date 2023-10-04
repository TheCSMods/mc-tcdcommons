package io.github.thecsdev.tcdcommons.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.thecsdev.tcdcommons.test.client.gui.screen.TestTScreen;

public final class TModMenuApiImpl implements ModMenuApi
{
	public final @Override ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return parent -> new TestTScreen(parent).getAsScreen();
	}
}