package io.github.thecsdev.tcdcommons.mixin.hooks;

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

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

/**
 * I copied this {@link Mixin} from Fabric-API, and also slightly modified it.<br/>
 * The reason it was copied it is to avoid dependence on Fabric-API, which of course would cause
 * complications like platform portability issues, having to install the dependencies, and so on...
 * @see net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor
 */
@Mixin(ArgumentTypes.class)
public interface MixinArgumentTypes
{
	@Accessor("CLASS_MAP")
	static Map<Class<?>, ArgumentSerializer<?, ?>> tcdcommons_getClassMap()
	{
		throw new AssertionError("");
	}
}