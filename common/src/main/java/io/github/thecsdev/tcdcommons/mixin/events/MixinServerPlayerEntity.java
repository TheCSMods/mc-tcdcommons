package io.github.thecsdev.tcdcommons.mixin.events;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.events.entity.EntityEvent;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerEntity
{
	// ==================================================
	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void onReadCustomData(ValueInput view, CallbackInfo callback)
	{
		final var self = (ServerPlayer)(Object)this;
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
			CrashReport crashReport = CrashReport.forThrowable(throwable, "[" + modId + "] Loading player custom NBT");
			CrashReportCategory crashReportSection = crashReport.addCategory("Entity being loaded");
			self.fillCrashReportCategory(crashReportSection);
			throw new ReportedException(crashReport);
		}
	}
	// --------------------------------------------------
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void onWriteCustomDataToNbt(ValueOutput view, CallbackInfo callback)
	{
		final var self = (ServerPlayer)(Object)this;
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
			CrashReport crashReport = CrashReport.forThrowable(throwable, "[" + modId + "] Saving player custom NBT");
			CrashReportCategory crashReportSection = crashReport.addCategory("Entity being saved");
			self.fillCrashReportCategory(crashReportSection);
			throw new ReportedException(crashReport);
		}
	}
	// ==================================================
}