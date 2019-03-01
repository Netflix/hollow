package com.netflix.hollow.api.producer.incubating;

import static java.util.Objects.requireNonNull;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.producer.HollowProducer;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class HollowProducerV2<A extends HollowAPI> extends HollowProducer {
    public static Builder<HollowAPI> newBuilder() {
        return new Builder<>();
    }

    protected HollowProducerV2(Builder<A> builder) {
        super(builder);
    }

    public static class Builder<A extends HollowAPI> extends HollowProducer.Builder<Builder<A>> {
        final Feature<Path> localBlobStore;
        final Feature<Class<A>> apiClass;

        HollowAPIFactory apiFactory;

        protected Builder() {
            localBlobStore          = feature();
            apiClass                = feature();
        }

        protected <T> Feature<T> feature() {
            return new Feature<>();
        }

        public synchronized Builder<A> withLocalBlobStore(Path localBlobStore) {
            requireNonNull(localBlobStore, "localBlobStore cannot be null; use localBlobStoreFeature().disable()");
            return this.localBlobStore.set(localBlobStore);
        }

        public Feature<Path> localBlobStoreFeature() {
            return localBlobStore;
        }

        /**
         * Provide the code generated API class that extends {@link HollowAPI}.
         *
         * @param apiClass the code generated API class
         * @return this builder
         * @throws IllegalArgumentException if provided API class is {@code HollowAPI} instead of a subclass
         */
        public synchronized <A2 extends A> Builder<A2> withAPI(Class<A2> apiClass) {
            /*
             * FIXME(timt): not great...
             *
             *    Builder<Alpha> b = new Builder<>().withAPI(Alpha.class);
             *
             *    Producer<Alpha> c1 = b.build(); // YAY
             *
             *    Producer<Bravo> c2 = b.withAPI(Bravo.class).build(); // sure...okay
             *
             *    b.withAPI(Charlie.class);
             *    Producer<Bravo> c3 = b.build(); // BOO
             *
             * Switch to immutable builder? That would confuse/break a lot of users who thought
             * this was a drop-in replacement.
             *
             * Require it on construction?
             *
             *    Builder<Alpha> b = new Builder<>(Alpha.class);
             */
            requireNonNull(apiClass, "apiClass cannot be null");
            if (HollowAPI.class.equals(apiClass))
                throw new IllegalArgumentException("must provide a subclass of HollowAPI; apiClassFeature().reset() to restore platform default");
            @SuppressWarnings("unchecked")
            Builder<A2> self = (Builder<A2>)this;
            self.apiClass.set(apiClass);
            return self;
        }

        public Feature<Class<A>> apiClassFeature() {
            return apiClass;
        }

        public HollowProducerV2<A> build() {
            // verify builder config
            // TODO(timt): collect all invalid configs and throw at the end
            if (!blobRetriever.hasValue() && !localBlobStore.hasValue())
                throw new IllegalStateException("A HollowBlobRetriever or local blob store path must be specified");
            if (apiClass.isDisabled())
                throw new IllegalStateException("An apiClass is required");

            // apply platform defaults
            if (localBlobStore.hasValue())
                blobRetriever.set(new HollowFilesystemBlobRetriever(localBlobStore.get(), blobRetriever.get()));

            apiFactory = apiClass.hasValue()
                    ? new HollowAPIFactory.ForGeneratedAPI<>(apiClass.get())
                    : HollowAPIFactory.DEFAULT_FACTORY;

            // build it
            return newProducer();
        }

        protected HollowProducerV2<A> newProducer() {
            return new HollowProducerV2<>(this);
        }

        // TODO(timt): duplicated in HollowConsumerV2.Builder.Feature
        public class Feature<T> {
            /*
             * This class tracks 3 feature states.
             *
             * | state        | state                                  |
             * |--------------|----------------------------------------|
             * | unset        | use default implementation             |
             * | set          | use provided component for the feature |
             * | disabled     | disable the feature                    |
             *
             * Having 3 states is useful for builder subclasses which may need to distinguish when a feature
             * hasn't been configured versus having been explicitly disabled.
             *
             * Required features are enforced when building, not when configuring the builder.
             */
            T value;
            boolean disabled;

            protected Feature() {
                value = null;
            }

            protected T get() {
                return value;
            }

            protected Builder<A> set(T value) {
                requireNonNull(value, "feature cannot be null; use disable()");
                synchronized (Builder.this) {
                    this.value = value;
                    disabled = false;
                    return Builder.this;
                }
            }

            /**
             * Reset this feature to the platform default.
             *
             * @return the builder to which this feature belongs
             */
            public Builder<A> reset() {
                synchronized (Builder.this) {
                    value = null;
                    disabled = false;
                    return Builder.this;
                }
            }

            /**
             * Disables this feature.
             *
             * @return the builder to which this feature belongs
             */
            public Builder<A> disable() {
                synchronized (Builder.this) {
                    value = null;
                    disabled = true;
                    return Builder.this;
                }
            }

            /**
             * Whether a custom value has been provided for this feature.
             *
             * @return {@code true} if this feature was customized; {@code false} if it's disabled or the platform
             *         default
             */
            public boolean hasValue() {
                synchronized (Builder.this) {
                    return value != null;
                }
            }

            /**
             * Whether this feature has been configured.
             *
             * @return {@code true} if this feature was set or disabled; {@code false} if it's the platform default
             */
            public boolean isSpecified() {
                synchronized (Builder.this) {
                    return disabled || value != null;
                }
            }

            /**
             * Whether this feature was disabled.
             *
             * @return {@code true} if this feature was disabled; {@code false} if it was set or is the
             *         platform default
             */
            public boolean isDisabled() {
                synchronized (Builder.this) {
                    return disabled;
                }
            }
        }
    }
}
