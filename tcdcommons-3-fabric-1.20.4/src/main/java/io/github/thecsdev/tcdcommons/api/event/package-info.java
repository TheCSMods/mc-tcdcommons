/**
 * Provides the classes necessary to create and manage a custom event system.
 * This package includes interfaces and factory classes for creating and
 * handling events and listeners.
 * 
 * <p>This event system is heavily inspired by the
 * <a href="https://github.com/architectury/architectury-api">Architectury API</a>
 * but has been created to allow greater customization and to avoid dependencies on other APIs.
 * 
 * <p>The main components of this package are:
 * <ul>
 *   <li>{@link io.github.thecsdev.tcdcommons.api.event.TEvent} - Interface for defining events.</li>
 *   <li>{@link io.github.thecsdev.tcdcommons.api.event.TKeyedEvent} - Interface for defining keyed events.</li>
 *   <li>{@link io.github.thecsdev.tcdcommons.api.event.TEventFactory} - Factory class for creating events.</li>
 * </ul>
 * 
 * <p>Together, these components provide a framework for
 * creating and handling events in a variety of contexts.
 * 
 * @author TheCSDev
 * @see io.github.thecsdev.tcdcommons.api.event.TEvent
 * @see io.github.thecsdev.tcdcommons.api.event.TKeyedEvent
 * @see io.github.thecsdev.tcdcommons.api.event.TEventFactory
 */
package io.github.thecsdev.tcdcommons.api.event;