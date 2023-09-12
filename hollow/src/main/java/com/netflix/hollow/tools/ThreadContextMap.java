package com.netflix.hollow.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ThreadContextMap {
    /**
     * Property name ({@value} ) for selecting {@code InheritableThreadLocal} (value "true") or plain
     * {@code ThreadLocal} (value is not "true") in the implementation.
     */
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";

    private final boolean useMap;
    private final ThreadLocal<Map<String, String>> localMap;

    private static boolean inheritableMap;

    // LOG4J2-479: by default, use a plain ThreadLocal, only use InheritableThreadLocal if configured.
    // (This method is package protected for JUnit tests.)
    static ThreadLocal<Map<String, String>> createThreadLocalMap(final boolean isMapEnabled) {
        if (inheritableMap) {
            return new InheritableThreadLocal<Map<String, String>>() {
                 @Override
                protected Map<String, String> childValue(final Map<String, String> parentValue) {
                    return parentValue != null && isMapEnabled //
                            ? Collections.unmodifiableMap(new HashMap<>(parentValue)) //
                            : null;
                }
            };
        }
        // if not inheritable, return plain ThreadLocal with null as initial value
        return new ThreadLocal<>();
    }

    public ThreadContextMap() {
        this(true);
    }

    public ThreadContextMap(final boolean useMap) {
        this.useMap = useMap;
        this.localMap = createThreadLocalMap(useMap);
    }

     
    public void put(final String key, final String value) {
        if (!useMap) {
            return;
        }
        Map<String, String> map = localMap.get();
        map = map == null ? new HashMap<>(1) : new HashMap<>(map);
        map.put(key, value);
        localMap.set(Collections.unmodifiableMap(map));
    }

    public void putAll(final Map<String, String> m) {
        if (!useMap) {
            return;
        }
        Map<String, String> map = localMap.get();
        map = map == null ? new HashMap<>(m.size()) : new HashMap<>(map);
        for (final Map.Entry<String, String> e : m.entrySet()) {
            map.put(e.getKey(), e.getValue());
        }
        localMap.set(Collections.unmodifiableMap(map));
    }

     
    public String get(final String key) {
        final Map<String, String> map = localMap.get();
        return map == null ? null : map.get(key);
    }

     
    public void remove(final String key) {
        final Map<String, String> map = localMap.get();
        if (map != null) {
            final Map<String, String> copy = new HashMap<>(map);
            copy.remove(key);
            localMap.set(Collections.unmodifiableMap(copy));
        }
    }

    public void removeAll(final Iterable<String> keys) {
        final Map<String, String> map = localMap.get();
        if (map != null) {
            final Map<String, String> copy = new HashMap<>(map);
            for (final String key : keys) {
                copy.remove(key);
            }
            localMap.set(Collections.unmodifiableMap(copy));
        }
    }

     
    public void clear() {
        localMap.remove();
    }

     
    public Map<String, String> toMap() {
        return getCopy();
    }

     
    public boolean containsKey(final String key) {
        final Map<String, String> map = localMap.get();
        return map != null && map.containsKey(key);
    }

    @SuppressWarnings("unchecked")
     
    public <V> V getValue(final String key) {
        final Map<String, String> map = localMap.get();
        return (V) (map == null ? null : map.get(key));
    }

     
    public Map<String, String> getCopy() {
        final Map<String, String> map = localMap.get();
        return map == null ? new HashMap<>() : new HashMap<>(map);
    }

     
    public Map<String, String> getImmutableMapOrNull() {
        return localMap.get();
    }

     
    public boolean isEmpty() {
        final Map<String, String> map = localMap.get();
        return map == null || map.isEmpty();
    }

     
    public int size() {
        final Map<String, String> map = localMap.get();
        return map == null ? 0 : map.size();
    }

     
    public String toString() {
        final Map<String, String> map = localMap.get();
        return map == null ? "{}" : map.toString();
    }

     
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final Map<String, String> map = this.localMap.get();
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        result = prime * result + Boolean.valueOf(this.useMap).hashCode();
        return result;
    }

     
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ThreadContextMap) {
            final ThreadContextMap other = (ThreadContextMap) obj;
            if (this.useMap != other.useMap) {
                return false;
            }
        }
        if (!(obj instanceof ThreadContextMap)) {
            return false;
        }
        final ThreadContextMap other = (ThreadContextMap) obj;
        final Map<String, String> map = this.localMap.get();
        final Map<String, String> otherMap = other.getImmutableMapOrNull();
        return Objects.equals(map, otherMap);
    }
}

