package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.api.events.server.MinecraftServerEvent;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{
	@Inject(method = "tickServer", at = @At("RETURN"))
	public void onTickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
	{
		//invoke the event
		MinecraftServerEvent.TICKED_WORLDS.invoker().invoke((MinecraftServer)(Object)this);
	}
}