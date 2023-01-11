package io.github.thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import io.github.thecsdev.tcdcommons.test.client.gui.screen.TestTScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

@Mixin(value = TitleScreen.class, remap = true)
public abstract class MixinTitleScreen extends Screen
{
	private static boolean TCDCommons_TestEnabled = false;
	protected MixinTitleScreen(Component title) { super(title); }

	@Inject(method = "init", at = @At("TAIL"))
	public void onInit(CallbackInfo callback)
	{
		if(!TCDCommons_TestEnabled) return;
		
		//add a testing button
		var msg = TextUtils.fLiteral("§e" + TCDCommons.getModName());
		Button.OnPress onPress = arg0 -> { minecraft.setScreen(new TestTScreen(this)); };
		var btn = new Button(10, this.height - 50, 125, 20, msg, onPress);
		addRenderableWidget(btn);
	}
}