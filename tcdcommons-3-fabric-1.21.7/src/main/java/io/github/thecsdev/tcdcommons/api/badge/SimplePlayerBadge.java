package io.github.thecsdev.tcdcommons.api.badge;

import java.util.Objects;

import io.github.thecsdev.tcdcommons.api.util.annotations.Virtual;
import net.minecraft.text.Text;

/**
 * A simple {@link PlayerBadge} implementation that has a
 * constructor accepting two {@link Text}s.
 */
public @Virtual class SimplePlayerBadge extends PlayerBadge
{
	// ==================================================
	protected final Text name, description;
	// ==================================================
	public SimplePlayerBadge(Text name, Text description) throws NullPointerException
	{
		this.name = Objects.requireNonNull(name);
		this.description = Objects.requireNonNull(description);
	}
	// ==================================================
	public final @Override Text getName() { return this.name; }
	public final @Override Text getDescription() { return this.description; }
	public @Virtual @Override boolean shouldSave() { return true; }
	// ==================================================
}