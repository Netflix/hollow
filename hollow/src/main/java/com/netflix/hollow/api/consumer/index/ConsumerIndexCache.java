package com.netflix.hollow.api.consumer.index;

import com.netflix.hollow.api.consumer.HollowConsumer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ConsumerIndexCache<I extends AbstractHollowUniqueKeyIndex<?,?>> {
    private final Map<CacheKey, I> cache = new ConcurrentHashMap<>();
    private final HollowConsumer consumer;

    public ConsumerIndexCache(HollowConsumer consumer) {
        this.consumer = consumer;
    }

    public I uniqueKeyIndex(Class<?> indexClass, String name, String... fieldPaths) {
        return cache.computeIfAbsent(new CacheKey(indexClass, name), k -> {{
            try {
                // TODO(timt): gross
                Class<I> clazz = (Class<I>) indexClass;

                Constructor<I> ctor = clazz.getConstructor(HollowConsumer.class, String[].class);
                I index = ctor.newInstance(consumer, fieldPaths);
                index.listenToDataRefresh();
                return index;
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                        "expected an index class with a constructor that accepts (HollowConsumer, String[]); class="
                                + indexClass.getName());
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new IllegalArgumentException("error constructing index; class=" + indexClass.getName(), e);
            }
        }

        });
    }


    public I uniqueKeyIndex(Class<?> indexClass) {
        return cache.computeIfAbsent(new CacheKey(indexClass, "<default>"), k -> {
            try {
                // TODO(timt): gross
                Class<I> clazz = (Class<I>) indexClass;

                Constructor<I> ctor = clazz.getConstructor(HollowConsumer.class);
                I index = ctor.newInstance(consumer);
                index.listenToDataRefresh();
                return index;
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                        "expected an index class with a constructor that accepts (HollowConsumer); class="
                                + indexClass.getName());
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new IllegalArgumentException("error constructing index; class=" + indexClass.getName(), e);
            }
        });
    }

    private static final class CacheKey<I> {
        final Class<I> clazz;
        final String name;

        private CacheKey(Class<I> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            CacheKey that = (CacheKey) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(clazz, that.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, clazz);
        }
    }
}
