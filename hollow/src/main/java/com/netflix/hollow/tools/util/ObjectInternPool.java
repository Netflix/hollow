package com.netflix.hollow.tools.util;

import java.util.Map;
import java.util.HashMap;

// This class memoizes types by returning references to existing objects, or storing
// Objects if they are not currently in the pool
public class ObjectInternPool {
    final private GenericInternPool<Integer> integerInternPool = new GenericInternPool<Integer>();
    final private GenericInternPool<Float> floatInternPool = new GenericInternPool<Float>();
    final private GenericInternPool<Double> doubleInternPool = new GenericInternPool<Double>();
    // Only two possible values (and technically null), no reason for hashmap
    final Boolean falseBool = false;
    final Boolean trueBool = true;

    public Object intern(Object objectToIntern) {
        if(objectToIntern==null) {
            throw new IllegalArgumentException("Cannot intern null objects");
        }

        if(objectToIntern instanceof Float) {
            return floatInternPool.intern((Float) objectToIntern);
        } else if(objectToIntern instanceof Double) {
            return doubleInternPool.intern((Double) objectToIntern);
        } else if(objectToIntern instanceof Integer) {
            return integerInternPool.intern((Integer) objectToIntern);
        } else if(objectToIntern instanceof String) {
            //just use Java's builtin intern function
            return ((String) objectToIntern).intern();
        } else if(objectToIntern instanceof Boolean) {
            return (Boolean) objectToIntern ? trueBool : falseBool;
        } else {
            String className = objectToIntern.getClass().getName();
            throw new IllegalArgumentException("Cannot intern object of type " + className);
        }
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