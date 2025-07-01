package io.github.thecsdev.tcdcommons.mixin.events;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.api.events.server.MinecraftServerEvent;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{
	@Inject(method = "tickWorlds", at = @At("RETURN"))
	public void onTickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
	{
		//invoke the event
		MinecraftServerEvent.TICKED_WORLDS.invoker().invoke((MinecraftServer)(Object)this);
	}
}