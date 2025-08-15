package io.github.thecsdev.tcdcommons.api.util.thread;

import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;
import org.jetbrains.annotations.Nullable;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

/**
 * Represents a task that is happening or developing gradually or in stages.<br/>
 * The task is executed asynchronously, and its progress is tracked.
 * @param <T> The type of the result that is produced by this {@link ProgressiveTask}.
 */
public abstract class ProgressiveTask<T>
{
	// ==================================================
	private volatile boolean __isRunning  = false;
	private volatile float   __progress   = 0;
	private volatile @Nullable T         __result = null;
	private volatile @Nullable Throwable __error  = null;
	// --------------------------------------------------
	/**
	 * A {@link TEvent} that is invoked when {@link #setProgress(float)} is called.
	 * @apiNote Depending on the {@link Thread} this {@link ProgressiveTask} is
	 * running on, this {@link TEvent} may be invoked off-{@link Thread}.
	 */
	public final TEvent<PtPc> eProgressChanged = TEventFactory.createLoop();
	
	/**
	 * A {@link TEvent} that is invoked when this {@link ProgressiveTask}
	 * finishes its execution in {@link #executeSync()}.
	 * @apiNote Depending on the {@link Thread} this {@link ProgressiveTask} is
	 * running on, this {@link TEvent} may be invoked off-{@link Thread}.
	 */
	public final TEvent<PtFinished> eFinished = TEventFactory.createLoop();
	// ==================================================
	/**
	 * Retrieves the current progress of this {@link ProgressiveTask}'s execution.
	 * @see #isRunning()
	 * @see #isFinished()
	 * @apiNote Please also ensure you use the {@link #isRunning()}
	 * and {@link #isFinished()} checks as well.
	 */
	public final float getProgress() { return this.__progress; }
	
	/**
	 * Sets this {@link ProgressiveTask}'s current "progress" value. Used by
	 * {@link #onExecuteTask()} to indicate the current execution progress.
	 * @param progress The 0-1 value indicating this task's current execution progress.
	 * @apiNote The argument ranges from 0 to 1.
	 */
	protected final void setProgress(float progress)
	{
		//ignore same values
		if(this.__progress == progress) return;
		//clamp and assign new value
		this.__progress = (progress = Math.max(0, Math.min(1, progress)));
		//invoke the event, but suppress errors
		try { eProgressChanged.invoker().invoke(progress); }
		catch(Exception e) {}
	}
	// --------------------------------------------------
	/**
	 * Returns {@code true} if this {@link ProgressiveTask} is currently executing.
	 */
	public final boolean isRunning() { return this.__isRunning; }
	
	/**
	 * Returns {@code true} if this {@link ProgressiveTask} has finished executing.
	 */
	public final boolean isFinished() { return !this.__isRunning && this.__progress >= 1; }
	// --------------------------------------------------
	/**
	 * Returns the result of this {@link ProgressiveTask}.
	 * @see #getError()
	 * @apiNote {@link #isFinished()} must return {@code true} to use this.
	 */
	public final @Nullable T getResult() { return isFinished() ? this.__result : null; }
	
	/**
	 * If the task execution fails due a raised {@link Throwable}, there will be no {@link #getResult()},
	 * and instead, there will be a {@link Throwable} returned by {@link #getError()}.
	 * @see #getResult()
	 * @apiNote {@link #isFinished()} must return {@code true} to use this.
	 */
	public final @Nullable Throwable getError() { return isFinished() ? this.__error : null; }
	// --------------------------------------------------
	/**
	 * Clears all flags associated with this {@link ProgressiveTask},
	 * making it look as if it were never executed before.
	 * @throws IllegalStateException If the task is currently running.
	 * @see #isRunning()
	 */
	public final void clearFlags() throws IllegalStateException
	{
		//check if running
		if(this.__isRunning)
			throw new IllegalStateException("Cannot clear task flags while the task is running.");
		
		//clear flags
		this.__progress  = 0;
		this.__isRunning = true;
		this.__result    = null;
		this.__error     = null;
		if(this instanceof DescriptiveProgressiveTask<?> dpt)
			dpt.__description = null;
	}
	// ==================================================
	/**
	 * Synchronously executes this {@link ProgressiveTask}.
	 * @apiNote This task may only be executed once at a time. To execute this
	 * task again, you must wait for the currently ongoing execution to finish.
	 */
	public final synchronized void executeSync()
	{
		//clear flags
		this.__isRunning = false; //fail-safe; altho this method is synchronized
		clearFlags();
		
		//main task execution
		try
		{
			this.__isRunning = true;
			this.__result = onExecuteTask();
		}
		catch(Throwable e)
		{
			this.__error = e;
			if(this instanceof DescriptiveProgressiveTask<?> dpt)
				dpt.setProgressDescription(literal("")
						.append(translatable("mco.errorMessage.generic"))
						.append(e.getClass().getName() + ": ")
						.append(e.getLocalizedMessage()));
		}
		finally
		{
			//set ending flags
			setProgress(1); //invokes the event
			this.__isRunning = false;
		}
		
		//event handling, but suppress errors
		try { this.eFinished.invoker().invoke(); } catch(Throwable e) {}
	}
	// --------------------------------------------------
	/**
	 * The main logic responsible for executing this {@link ProgressiveTask}.<br/>
	 * This is where the task's main code goes.
	 * @return The result of this {@link ProgressiveTask} that is then
	 * obtained via {@link #getResult()}.
	 * @apiNote This code is executed off-thread!
	 * @apiNote Use {@link #setProgress(float)} as this task executes, to give any
	 * outsiders observing this task more insight into its progress.
	 */
	protected abstract @Nullable T onExecuteTask() throws Exception;
	// ==================================================
	/**
	 * @see ProgressiveTask#eProgressChanged
	 */
	public static interface PtPc { public void invoke(float progress); }
	
	/**
	 * @see ProgressiveTask#eFinished
	 */
	public static interface PtFinished { public void invoke(); }
	// ==================================================
}