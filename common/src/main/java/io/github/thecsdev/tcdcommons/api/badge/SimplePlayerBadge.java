package io.github.thecsdev.tcdcommons.api.badge;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.network.chat.Component;

import java.util.Objects;

/**
 * A simple {@link PlayerBadge} implementation that has a
 * constructor accepting two {@link Component}s.
 */
public @Virtual class SimplePlayerBadge extends PlayerBadge
{
	// ==================================================
	protected final Component name, description;
	// ==================================================
	public SimplePlayerBadge(Component name, Component description) throws NullPointerException
	{
		this.name = Objects.requireNonNull(name);
		this.description = Objects.requireNonNull(description);
	}
	// ==================================================
	public final @Override Component getName() { return this.name; }
	public final @Override Component getDescription() { return this.description; }
	public @Virtual @Override boolean shouldSave() { return true; }
	// ==================================================
}