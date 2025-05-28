package io.github.thecsdev.tcdcommons.mixin.events;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.events.entity.EntityEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity
{
	// ==================================================
	@Inject(method = "readCustomData", at = @At("RETURN"))
	public void onReadCustomData(ReadView view, CallbackInfo callback)
	{
		final var self = (ServerPlayerEntity)(Object)this;
		try
		{
			//read player badges
			//ServerPlayerBadgeHandler.getServerBadgeHandler(self).loadFromPlayerNbt(nbt); -- FIXME - No more player badges.
			//read other custom NBT data
			EntityEvent.SERVER_PLAYER_READ_NBT.invoker().invoke(self, view);
		}
		catch(Exception throwable)
		{
			var modId = TCDCommons.getModID();
			CrashReport crashReport = CrashReport.create(throwable, "[" + modId + "] Loading player custom NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
			self.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}
	// --------------------------------------------------
	@Inject(method = "writeCustomData", at = @At("RETURN"))
	public void onWriteCustomDataToNbt(WriteView view, CallbackInfo callback)
	{
		final var self = (ServerPlayerEntity)(Object)this;
		try
		{
			//write player badges
			//ServerPlayerBadgeHandler.getServerBadgeHandler(self).saveToPlayerNbt(nbt); -- FIXME - No more player badges.
			//write other custom NBT data
			EntityEvent.SERVER_PLAYER_WRITE_NBT.invoker().invoke(self, view);
		}
		catch(Exception throwable)
		{
			var modId = TCDCommons.getModID();
			CrashReport crashReport = CrashReport.create(throwable, "[" + modId + "] Saving player custom NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			self.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}
	// ==================================================
}