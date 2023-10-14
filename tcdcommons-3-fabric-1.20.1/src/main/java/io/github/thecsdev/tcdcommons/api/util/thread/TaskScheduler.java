package io.github.thecsdev.tcdcommons.api.util.thread;

import static io.github.thecsdev.tcdcommons.TCDCommons.getModID;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import com.google.common.collect.Queues;

import io.github.thecsdev.tcdcommons.api.util.collections.IdealList;
import net.minecraft.util.thread.ReentrantThreadExecutor;

public final class TaskScheduler
{
	// ==================================================
	private static final String THREAD_NAME = getModID() + " task scheduler";
	private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(
			1,
			runnable -> new Thread(runnable, THREAD_NAME));
	//
	private static final Queue<Map.Entry<Runnable, BooleanSupplier>> CONDITIONAL_TASK_QUEUE =
			Queues.newConcurrentLinkedQueue();
	// --------------------------------------------------
	static
	{
		// ---------- CONDITIONAL ENTRIES EXECUTOR LOGIC
		//list has to be IdealList because that one supports concurrent modifications
		final var conditionalEntries = new IdealList<Map.Entry<Runnable, BooleanSupplier>>();
		final Runnable conditionalTaskExecutor = () ->
		{
			//add queue entries to the list of entries
			synchronized(CONDITIONAL_TASK_QUEUE)
			{
				//keep adding while there are entries
				while(!CONDITIONAL_TASK_QUEUE.isEmpty())
				{
					final var next = CONDITIONAL_TASK_QUEUE.poll();
					if(next != null) conditionalEntries.add(next);
				}
			}
			
			//now, time to iterate and execute
			for(final var entry : conditionalEntries)
			{
				//check if the condition finally applies, and continue if not
				try { if(!entry.getValue().getAsBoolean()) continue; }
				catch(Exception e)
				{
					//if a condition result supplier raises an exception; remove its task
					new RuntimeException("Failed to execute scheduled task.", e).printStackTrace();
					conditionalEntries.remove(entry);
					continue;
				}
				
				//if the condition finally applies;
				//- remove the task, and then
				//- execute the task
				conditionalEntries.remove(entry);
				entry.getKey().run();
			}
		};
		SCHEDULER.scheduleAtFixedRate(conditionalTaskExecutor, 1, 1, TimeUnit.SECONDS);
		// ----------
	}
	// ==================================================
	private TaskScheduler() {}
	// ==================================================
	/**
	 * Schedules a task to be executed on the {@link ReentrantThreadExecutor} some time in the future.
	 * @apiNote Note that the {@link ReentrantThreadExecutor} may shut down before the task gets to execute.
	 * @apiNote Also note that this system does not track game's ticks, aka it uses a time-frame independent from ticks.
	 */
	public static void scheduleTask(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final long delay,
			final TimeUnit unit,
			final Runnable command) throws NullPointerException
	{
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(unit);
		Objects.requireNonNull(command);
		SCHEDULER.schedule(() -> minecraftClientOrServer.executeSync(command), delay, unit);
	}
	
	/**
	 * Schedules a task to be executed on the {@link ReentrantThreadExecutor} once a given condition is
	 * met, aka once the {@link BooleanSupplier} returns {@code true}.
	 * Once the task is executed, it will never be executed again.
	 * @apiNote Note that the {@link ReentrantThreadExecutor} may shut down before the task gets to execute.
	 * @apiNote Also note that this system does not track game's ticks, aka it uses a time-frame independent from ticks.
	 */
	public static void executeOnce(
			final ReentrantThreadExecutor<?> minecraftClientOrServer,
			final BooleanSupplier condition,
			final Runnable command) throws NullPointerException
	{
		Objects.requireNonNull(minecraftClientOrServer);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(command);
		CONDITIONAL_TASK_QUEUE.add(new SimpleEntry<>(() -> minecraftClientOrServer.executeSync(command), condition));
	}
	// ==================================================
}