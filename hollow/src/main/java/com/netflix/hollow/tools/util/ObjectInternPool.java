package com.netflix.hollow.tools.util;

import java.util.Map;
import java.util.HashMap;

// This class memoizes types by returning references to existing objects, or storing
// Objects if they are not currently in the pool
public class ObjectInternPool {
    final private GenericInternPool<Integer> integerInternPool = new GenericInternPool<>();
    final private GenericInternPool<Float> floatInternPool = new GenericInternPool<>();
    final private GenericInternPool<Double> doubleInternPool = new GenericInternPool<>();
    final private GenericInternPool<Long> longInternPool = new GenericInternPool<>();

    public Object intern(Object objectToIntern) {
        if(objectToIntern==null) {
            throw new IllegalArgumentException("Cannot intern null objects");
        }

        // Automatically handles booleans and integers within cached range
        if(objectAutomaticallyCached(objectToIntern)) {
            return objectToIntern;
        }

        if(objectToIntern instanceof Float) {
            return floatInternPool.intern((Float) objectToIntern);
        } else if(objectToIntern instanceof Double) {
            return doubleInternPool.intern((Double) objectToIntern);
        } else if(objectToIntern instanceof Integer) {
            return integerInternPool.intern((Integer) objectToIntern);
        } else if(objectToIntern instanceof Long) {
            return longInternPool.intern((Long) objectToIntern);
        } else if(objectToIntern instanceof String) {
            // Use Java's builtin intern function
            return ((String) objectToIntern).intern();
        } else {
            String className = objectToIntern.getClass().getName();
            throw new IllegalArgumentException("Cannot intern object of type " + className);
        }
    }

    private boolean objectAutomaticallyCached(Object objectToIntern) {
        if(objectToIntern instanceof Boolean) {
            return true;
        } else if(objectToIntern instanceof Integer) {
            return -128 <= (Integer) objectToIntern && (Integer) objectToIntern <= 127;
        }
        return false;
    }
}

class GenericInternPool<T> {
    private final Map<T, T> pool = new HashMap<>();

    public T intern(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot intern null objects");
        }

        synchronized (pool) {
            T interned = pool.get(object);
            if (interned == null) {
                interned = object;
                pool.put(object, object);
            }
            return interned;
        }
    }
}