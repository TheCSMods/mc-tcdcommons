package io.github.thecsdev.tcdcommons.mixin.__;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.TCDCommonsFabric;
import io.github.thecsdev.tcdcommons.api.util.integrity.SelfDefense;

@Mixin(value = TCDCommonsFabric.class, priority = 9001)
public abstract class MixinModLoader
{
	//`require = 0` is required, so as to only inject when there is one in the first place
	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true, require = 0)
	private static void onClassInit(CallbackInfo callback)
	{
		/* IMPORTANT NOTE: if you have a static constructor defined, or a static field defined,
		 * this code WILL end up always executing, even when it isn't supposed to, which is bad.
		 * 
		 * if you absolutely have to have a static field, then try defining the static variable
		 * WITHOUT initializing it by giving it a value. if that doesn't work, then neither will this.
		 */
		SelfDefense.reportClassInitializer(TCDCommonsFabric.class);
		callback.cancel();
	}
}