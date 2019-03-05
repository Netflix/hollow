package com.netflix.hollow.core.type.accessor;

import static java.util.Objects.requireNonNull;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ObjectDataAccessor<A extends HollowAPI, T extends HollowObject> extends AbstractHollowDataAccessor<T> {

    private final Class<A> apiType;
    private final HollowConsumer consumer;
    private final BiObjectIntFunction<A, T> extractor;

    private ObjectDataAccessor(Class<A> apiType, HollowConsumer consumer, Class<T> objectType,
            BiObjectIntFunction<A, T> extractor) {
        super(consumer, objectType.getSimpleName());

        this.apiType = apiType;
        this.consumer = consumer;
        this.extractor = extractor;
    }

    public static <A extends HollowAPI> Builder<A> from(Class<A> apiType, HollowConsumer consumer) {
        requireNonNull(apiType);
        requireNonNull(consumer);
        // TODO(timt): do we allow `Class<HollowAPI>`?
        return new Builder<>(apiType, consumer);
    }

    /**
     * The builder of a {@link ObjectDataAccessor}.
     *
     * @param <A> the generated {@link HollowAPI}
     */
    public static final class Builder<A extends HollowAPI> {
        final Class<A> apiType;
        final HollowConsumer consumer;

        Builder(Class<A> apiType, HollowConsumer consumer) {
            this.apiType = apiType;
            this.consumer = consumer;
        }

        public <T extends HollowObject> ObjectDataAccessor<A, T> bind(Class<T> objectType) {
            // TODO(timt): copied from SelectFieldPathResultExtractor
            if (GenericHollowObject.class.isAssignableFrom(objectType)) {
                BiObjectIntFunction<A, T> extractor =
                        (a, o) -> {
                            @SuppressWarnings("unchecked")
                            T t = (T) new GenericHollowObject(a.getDataAccess(), objectType.getSimpleName(), o);
                            return t;
                        };
                return new ObjectDataAccessor<>(apiType, consumer, objectType, extractor);
            } else {
                MethodHandle objectInstantiate;
                try {
                    objectInstantiate = MethodHandles.lookup().findVirtual(
                            apiType,
                            "get" + objectType.getSimpleName(),
                            MethodType.methodType(objectType, int.class));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            String.format("Select type %s is not associated with API %s",
                                    objectType.getName(), apiType.getName()),
                            e);
                }

                BiObjectIntFunction<A, T> extractor = (a, i) -> {
                    try {
                        @SuppressWarnings("unchecked")
                        T s = (T) objectInstantiate.invoke(a, i);
                        return s;
                    } catch (RuntimeException | Error e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };

                return new ObjectDataAccessor<>(apiType, consumer, objectType, extractor);
            }
        }
    }

    @Override
    public T getRecord(int ordinal) {
        return extractor.apply(consumer.getAPI(apiType), ordinal);
    }

    // TODO(timt): copied from SelectFieldPathResultExtractor
    interface BiObjectIntFunction<T, R> {
        R apply(T t, int i);
    }
}
