package io.github.thecsdev.tcdcommons.api.util.interfaces;

public interface IEnableStateProviderSetter extends IEnableStateProvider
{
	/**
	 * Sets the "enabled" state for this {@link Object}.
	 * @param enabled Whether or not this {@link Object} will be enabled.
	 */
	public void setEnabled(boolean enabled);
}