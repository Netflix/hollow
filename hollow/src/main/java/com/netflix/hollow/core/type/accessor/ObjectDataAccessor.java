/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.type.accessor;

import static java.util.Objects.requireNonNull;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.function.BiObjectIntFunction;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ObjectDataAccessor<T> extends AbstractHollowDataAccessor<T> {

    private final HollowAPI api;
    private final BiObjectIntFunction<HollowAPI, T> extractor;

    private ObjectDataAccessor(Builder<T> builder) {
        super(builder.readStateEngine, builder.typeName, builder.key);

        this.api = builder.api;
        this.extractor = builder.extractor;
    }

    /**
     * Returns an builder for an ObjectDataAccessor that will operate on {@link GenericHollowObject}s
     *
     * @param consumer
     *
     * @return a builder
     */
    public static Builder<GenericHollowObject> from(HollowConsumer consumer) {
        return new Builder<>(
                consumer.getAPI(),
                consumer.getStateEngine(),
                GenericHollowObject.class
        );
    }

    /**
     * Returns an builder for an ObjectDataAccessor that will operate on {@link GenericHollowObject}s
     *
     * @param api
     * @param readStateEngine
     *
     * @return a builder
     */
    public static Builder<GenericHollowObject> from(HollowAPI api, HollowReadStateEngine readStateEngine) {
        return new Builder<>(api, readStateEngine, GenericHollowObject.class);
    }

    /**
     *
     * @param consumer
     * @param type type of {@code HollowObject} to operate on
     * @param <T> a {@link HollowObject} type
     *
     * @return
     */
    public static <T> Builder<T> from(HollowConsumer consumer, Class<T> type) {
        return new Builder<>(consumer.getAPI(), consumer.getStateEngine(), type);
    }

    /**
     *
     * @param api
     * @param readStateEngine
     * @param type type of {@code HollowObject} to operate on
     * @param <T> a {@link HollowObject} type
     *
     * @return
     */
    public static <T> Builder<T> from(HollowAPI api,
            HollowReadStateEngine readStateEngine,
            Class<T> type) {
        return new Builder<>(api, readStateEngine, type);
    }

    /**
     * The builder of a {@link ObjectDataAccessor}.
     *
     * @param <T> type of {@link HollowObject} the accessor will operate on
     */
    public static final class Builder<T> {
        private final HollowAPI api;
        private final HollowReadStateEngine readStateEngine;
        final Class<T> type;
        String typeName;
        private PrimaryKey key;
        private BiObjectIntFunction<HollowAPI, T> extractor;

        Builder(HollowAPI api, HollowReadStateEngine readStateEngine, Class<T> type) {
            requireNonNull(api);
            requireNonNull(readStateEngine);
            requireNonNull(type);

            this.api = api;
            this.readStateEngine = readStateEngine;
            this.type = type;
            this.typeName = type.getSimpleName();
        }

        public ObjectDataAccessor<T> bindToPrimaryKey() {
            if (GenericHollowObject.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Cannot derive key; type=GenericHollowObject");
            }
            HollowSchema schema = readStateEngine.getNonNullSchema(typeName);

            assert schema.getSchemaType() == HollowSchema.SchemaType.OBJECT;

            key = ((HollowObjectSchema) schema).getPrimaryKey();
            if (key == null) {
                throw new IllegalArgumentException(String.format("No primary key associated; type=%s", typeName));
            }
            return build();
        }

        public ObjectDataAccessor<T> usingKey(PrimaryKey key) {
            requireNonNull(key);
            this.typeName = key.getType();
            this.key = key;
            return build();
        }

        public ObjectDataAccessor<T> usingPath(String typeName, String... fieldPaths) {
            requireNonNull(fieldPaths);
            this.typeName = typeName;
            this.key = new PrimaryKey(typeName, fieldPaths);
            return build();
        }

        private ObjectDataAccessor<T> build() {
            if (GenericHollowObject.class.isAssignableFrom(type)) {
                extractor = (a, o) -> {
                    @SuppressWarnings("unchecked")
                    T t = (T) new GenericHollowObject(a.getDataAccess(), typeName, o);
                    return t;
                };
                return new ObjectDataAccessor<>(this);
            } else {
                MethodHandle objectInstantiate;
                Class<?> apiType = api.getClass();
                try {
                    objectInstantiate = MethodHandles.lookup().findVirtual(
                            apiType,
                            "get" + typeName,
                            MethodType.methodType(type, int.class));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            String.format("Type %s is not associated with API %s",
                                    type.getName(), apiType.getName()),
                            e);
                }

                extractor =
                        (a, i) -> {
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

                return new ObjectDataAccessor<>(this);
            }
        }
    }

    @Override
    public T getRecord(int ordinal) {
        return extractor.apply(api, ordinal);
    }

}
