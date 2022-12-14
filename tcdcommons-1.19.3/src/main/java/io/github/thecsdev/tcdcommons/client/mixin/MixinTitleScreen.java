package io.github.thecsdev.tcdcommons.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.TextUtils;
import io.github.thecsdev.tcdcommons.test.client.gui.screen.TestTScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen
{
	protected MixinTitleScreen(Text title) { super(title); }

	@Inject(method = "init", at = @At("TAIL"))
	public void onInit(CallbackInfo callback)
	{
		//this will only be available in development
		if(!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
		
		//add a testing button
		MutableText msg = TextUtils.fLiteral("§e" + TCDCommons.getModName());
		PressAction onPress = arg0 -> { client.setScreen(new TestTScreen(this)); };
		ButtonWidget btn = ButtonWidget.builder(msg, onPress).dimensions(10, this.height - 50, 125, 20).build();;
		addDrawableChild(btn);
	}
}