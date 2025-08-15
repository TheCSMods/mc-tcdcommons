/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.thecsdev.tcdcommons.mixin.events;

import com.mojang.brigadier.CommandDispatcher;
import io.github.thecsdev.tcdcommons.api.events.server.command.CommandManagerEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * I copied this {@link Mixin} from Fabric-API, and also slightly modified it.<br/>
 * The reason it was copied it is to avoid dependence on Fabric-API, which of course would cause
 * complications like platform portability issues, having to install the dependencies, and so on...
 * <p>
 * net.fabricmc.fabric.mixin.command.CommandManagerMixin
 */
@Mixin(Commands.class)
public abstract class MixinCommandManager
{
	private @Final @Shadow CommandDispatcher<CommandSourceStack> dispatcher;
	
	@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V", remap = false), method = "<init>")
	private void fabric_addCommands(Commands.CommandSelection environment, CommandBuildContext registryAccess, CallbackInfo ci)
	{
		//register the player badge command
		//PlayerBadgeCommand.register(dispatcher); -- moved to another mod
		
		//invoke the event
		CommandManagerEvent.COMMAND_REGISTRATION_CALLBACK.invoker().invoke(dispatcher, registryAccess, environment);
	}
}