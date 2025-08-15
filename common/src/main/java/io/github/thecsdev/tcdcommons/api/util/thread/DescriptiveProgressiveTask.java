package io.github.thecsdev.tcdcommons.api.util.thread;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link ProgressiveTask} that also provides descriptions on "what is
 * currently happening as the task is executing".
 */
public abstract class DescriptiveProgressiveTask<T> extends ProgressiveTask<T>
{
	// ==================================================
	volatile @Nullable Component __description;
	// --------------------------------------------------
	/**
	 * A {@link TEvent} that is invoked whenever {@link #setProgressDescription(Component)} is called.
	 * @apiNote Depending on the {@link Thread} this {@link ProgressiveTask} is
	 * running on, this {@link TEvent} may be invoked off-{@link Thread}.
	 */
	public final TEvent<DptPdc> eProgressDescriptionChanged = TEventFactory.createLoop();
	// ==================================================
	/**
	 * Returns a user-friendly {@link Component} that describes what is
	 * currently going on as the task is executing.
	 */
	public final @Nullable Component getProgressDescription() { return this.__description; }
	
	/**
	 * Sets the user-friendly {@link Component} that describes what is
	 * currently going on as the task is executing.
	 * @param description The {@link Component}.
	 */
	protected final void setProgressDescription(@Nullable Component description)
	{
		//ignore objects with same pointers
		if(this.__description == description) return;
		//assign new value
		this.__description = description;
		//invoke the event, but suppress any errors
		try { eProgressDescriptionChanged.invoker().invoke(description); }
		catch(Exception e) {}
	}
	// ==================================================
	/**
	 * @see DescriptiveProgressiveTask#eProgressDescriptionChanged
	 */
	public static interface DptPdc { public void invoke(@Nullable Component description); }
	// ==================================================
}