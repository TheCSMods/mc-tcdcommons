package io.github.thecsdev.tcdcommons.api.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;

/**
 * Inspired by Google's {@link Cache}, except it's not a {@link Map}, it's a {@link Set}.<br/>
 * This {@link Set}'s entries will expire after a given amount of time.
 */
public final class CachedSet<E> extends AbstractSet<E>
{
	// ==================================================
    private final Set<E> set = new HashSet<>();
    private final Map<E, Long> timestamps = new HashMap<>();
    private final long duration;
    private final TimeUnit unit;
    // ==================================================
    public CachedSet(long duration, TimeUnit unit)
    {
        this.duration = duration;
        this.unit = unit;
        timestamps.values();
    }
    // ==================================================
    public @Override Iterator<E> iterator()
    {
        cleanUp();
        return set.iterator();
    }
    
    public @Override int size()
    {
        cleanUp();
        return set.size();
    }
    
    public @Override boolean add(E e)
    {
        cleanUp();
        timestamps.put(e, System.currentTimeMillis());
        return set.add(e);
    }
    
    public @Override boolean remove(Object o)
    {
        timestamps.remove(o);
        return set.remove(o);
    }
    
    public @Override boolean contains(Object o)
    {
        cleanUp();
        return set.contains(o);
    }
    // ==================================================
    public void cleanUp()
    {
        final long expirationTime = System.currentTimeMillis() - unit.toMillis(duration);
        timestamps.entrySet().removeIf(entry -> entry.getValue() < expirationTime);
        final var timestampValues = this.timestamps.values();
        this.set.removeIf(entry -> !timestampValues.contains(entry));
    }
 // ==================================================
}
