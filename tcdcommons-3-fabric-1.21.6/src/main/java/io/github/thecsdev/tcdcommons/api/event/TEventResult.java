package io.github.thecsdev.tcdcommons.api.event;

public enum TEventResult
{
	CANCEL_EVENT,
	CANCEL_PROPAGATION,
	CANCEL_NONE,
	CANCEL_ALL;
	
	public final boolean isEventCancelled() { return (this == CANCEL_EVENT || this == CANCEL_ALL); }
	public final boolean isPropagationCancelled() { return (this == CANCEL_PROPAGATION || this == CANCEL_ALL); }
	public TEventResult combine(TEventResult other)
	{
		if(other == null || other == CANCEL_NONE) return this;
		else if(this == CANCEL_NONE) return other;
		else if(this == other) return this;
		else return CANCEL_ALL;
	}
}