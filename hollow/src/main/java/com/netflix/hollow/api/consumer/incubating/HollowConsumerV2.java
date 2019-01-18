package com.netflix.hollow.api.consumer.incubating;

import static java.lang.System.arraycopy;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.api.metrics.HollowMetricsCollector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class HollowConsumerV2<A extends HollowAPI> extends HollowConsumer {
    public static Builder<HollowAPI> newBuilder() {
        return new Builder<>();
    }

    private final HollowClientUpdaterV2<A> updaterV2;

    protected HollowConsumerV2(Builder<A> builder) {
        super(
                builder.announcementWatcher.get(),
                builder.updater,
                builder.refreshExecutor.get(),
                builder.metrics,
                builder.refreshLock
        );
        this.updaterV2 = builder.updater;
    }

    public CompletableFuture<HollowConsumerV2<A>> initialLoad() {
        return getInitialLoad().thenApply(l -> this);
    }

    @Override
    public A getAPI() {
        return updaterV2.getAPI();
    }

    /**
     * Equivalent to calling {@link #getAPI()} and casting to the specified API.
     *
     * @param apiClass the class of the API
     * @param <T> the type of the API
     * @return the API which wraps the underlying dataset
     *
     * @deprecated use type parameter on consumer, e.g. {@code HollowConsumerV2<T>} then call {@link #getAPI()}
     */
    @Override
    @Deprecated
    public <T extends HollowAPI> T getAPI(Class<T> apiClass) {
        return apiClass.cast(updater.getAPI());
    }

    public static class Builder<A extends HollowAPI> {
        final Feature<BlobRetriever> blobRetriever;
        final Feature<Path> localBlobStore;
        final Feature<AnnouncementWatcher> announcementWatcher;
        final Feature<Set<RefreshListener>> refreshListeners;
        final Feature<Class<A>> apiClass;
        final Feature<String[]> cachedTypes;
        final Feature<HollowFilterConfig> filterConfig;
        final Feature<DoubleSnapshotConfig> doubleSnapshotConfig;
        final Feature<ObjectLongevityConfig> objectLongevityConfig;
        final Feature<ObjectLongevityDetector> objectLongevityDetector;
        final Feature<Executor> refreshExecutor;
        final Feature<HollowMetricsCollector<HollowConsumerMetrics>> metricsCollector;

        HollowAPIFactory apiFactory;
        HollowConsumerMetrics metrics;
        ReadWriteLock refreshLock;
        HollowClientUpdaterV2<A> updater;
        HollowObjectHashCodeFinder hashCodeFinder;
        List<RefreshListener> refreshListenerList;

        protected Builder() {
            blobRetriever           = feature();
            localBlobStore          = feature();
            announcementWatcher     = feature();
            refreshListeners        = feature();
            apiClass                = feature();
            cachedTypes             = feature();
            filterConfig            = feature();
            doubleSnapshotConfig    = feature();
            objectLongevityConfig   = feature();
            objectLongevityDetector = feature();
            refreshExecutor         = feature();
            metricsCollector        = feature();
        }

        protected <T> Feature<T> feature() {
            return new Feature<>();
        }

        public synchronized Builder<A> withBlobRetriever(BlobRetriever blobRetriever) {
            requireNonNull(blobRetriever, "blobRetriever cannot be null; use blobRetrieverFeature().disable()");
            return this.blobRetriever.set(blobRetriever);
        }

        public Feature<BlobRetriever> blobRetrieverFeature() {
            return blobRetriever;
        }

        @Deprecated
        public synchronized Builder<A> withLocalBlobStore(File localBlobStore) {
            return withLocalBlobStore(localBlobStore.toPath());
        }

        public synchronized Builder<A> withLocalBlobStore(Path localBlobStore) {
            requireNonNull(localBlobStore, "localBlobStore cannot be null; use localBlobStoreFeature().disable()");
            return this.localBlobStore.set(localBlobStore);
        }

        public Feature<Path> localBlobStoreFeature() {
            return localBlobStore;
        }

        public synchronized Builder<A> withAnnouncementWatcher(AnnouncementWatcher announcementWatcher) {
            requireNonNull(announcementWatcher, "announcementWatcher cannot be null; use announcementWatcherFeature().disable()");
            return this.announcementWatcher.set(announcementWatcher);
        }

        public Feature<AnnouncementWatcher> announcementWatcherFeature() {
            return announcementWatcher;
        }

        public synchronized Builder<A> withRefreshListener(RefreshListener refreshListener) {
            requireNonNull(announcementWatcher, "refreshListener cannot be null");
            Set<RefreshListener> list = refreshListeners.hasValue()
                    ? refreshListeners.get()
                    : new LinkedHashSet<>();
            list.add(refreshListener);
            return refreshListeners.set(list);
        }

        public synchronized Builder<A> withRefreshListeners(RefreshListener... additionalListeners) {
            requireNonNull(announcementWatcher, "additionalListeners cannot be null");
            Set<RefreshListener> l = refreshListeners.hasValue()
                    ? refreshListeners.get()
                    : new LinkedHashSet<>();
            Collections.addAll(l, additionalListeners);
            return refreshListeners.set(l);
        }

        public Feature<Set<RefreshListener>> refreshListenersFeature() {
            return refreshListeners;
        }

        /**
         * Provide the code generated API class that extends {@link HollowAPI}.
         *
         * The instance returned from {@link HollowConsumer#getAPI()} will be of the provided type and can be cast
         * to access generated methods.
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
             *    Consumer<Alpha> c1 = b.build(); // YAY
             *
             *    Consumer<Bravo> c2 = b.withAPI(Bravo.class).build(); // sure...okay
             *
             *    b.withAPI(Charlie.class);
             *    Consumer<Bravo> c3 = b.build(); // BOO
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
            self.cachedTypes.reset();
            return self;
        }

        /**
         * Provide the code generated API class that extends {@link HollowAPI} with one or more types
         * cached for direct field reads.
         *
         * All {@HollowRecord} instances are created by one of two factories:
         *
         * <dl>
         *     <dt>{@link com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider}</dt>
         *     <dd>creates an instance of the corresponding {@code <Type>DelegateLookupImpl} (e.g.
         *     {@code StringDelegateLookupImpl} or {@code MovieDelegateLookupImpl} for core
         *     types or types in a generated client API respectively). Field accesses perform
         *     a lookup into the underlying high-density cache</dd>
         *
         *     <dt>{@link com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider}</dt>
         *     <dd>instantiates and caches the corresponding {@code <Type>DelegateCachedImpl} from
         *     the generated client API (e.g. {@code MovieDelegateCachedImpl}). For a given ordinal,
         *     the same {@code HollowRecord} instance is returned assuming the ordinal hasn't been removed.
         *     All of the type's fields are eagerly looked up from the high-density cache and stored as Java fields,
         *     aking field access in tight loops or the hottest code paths more CPU efficient.</dd>
         * </dl>
         *
         * Object caching should only be enabled for low cardinality, custom types in your data model.
         *
         * Use {@link #withAPI(Class)} to build a consumer with your custom client API and
         * using the default high-density cache for all types.
         * @param cachedType a type to enable object caching on
         * @param additionalCachedTypes additional types to enable object caching on
         * @return this builder
         *
         * @see <a href="https://hollow.how/advanced-topics/#caching">https://hollow.how/advanced-topics/#caching</a>
         */
        public synchronized <A2 extends A> Builder<A2> withAPI(Class<A2> apiClass,
                String cachedType, String... additionalCachedTypes) {
            requireNonNull(apiClass, "apiClass cannot be null; use apiClassFeature().disable()");
            if (HollowAPI.class.equals(apiClass))
                throw new IllegalArgumentException("must provide a code generated API class");

            String[] cachedTypes = new String[additionalCachedTypes.length + 1];
            cachedTypes[0] = cachedType;
            arraycopy(additionalCachedTypes, 0, cachedTypes, 1, additionalCachedTypes.length);

            for (String t : cachedTypes)
                requireNonNull(t, "cached types cannot be null; types=" + cachedTypes);

            @SuppressWarnings("unchecked")
            Builder<A2> self = (Builder<A2>)this;
            self.apiClass.set(apiClass);
            self.cachedTypes.set(cachedTypes);
            return self;
        }

        public Feature<Class<A>> apiClassFeature() {
            // FIXME: reseting or disabling this feature should reset cached types feature
            return apiClass;
        }

        public synchronized Builder<A> withFilterConfig(HollowFilterConfig filterConfig) {
            requireNonNull(filterConfig, "filterConfig cannot be null; use filterConfigFeature().disable()");
            return this.filterConfig.set(filterConfig);
        }

        public Feature<HollowFilterConfig> filterConfigFeature() {
            return filterConfig;
        }

        public synchronized Builder<A> withDoubleSnapshotConfig(DoubleSnapshotConfig doubleSnapshotConfig) {
            requireNonNull(doubleSnapshotConfig, "doubleSnapshotConfig cannot be null; use doubleSnapshotConfigFeature().disable()");
            return this.doubleSnapshotConfig.set(doubleSnapshotConfig);
        }

        public Feature<DoubleSnapshotConfig> doubleSnapshotConfigFeature() {
            return doubleSnapshotConfig;
        }

        public synchronized Builder<A> withObjectLongevityConfig(ObjectLongevityConfig objectLongevityConfig) {
            requireNonNull(objectLongevityConfig, "objectLongevityConfig cannot be null; use objectLongevityConfigFeature().disable()");
            return this.objectLongevityConfig.set(objectLongevityConfig);
        }

        public Feature<ObjectLongevityConfig> objectLongevityConfigFeature() {
            return objectLongevityConfig;
        }

        public synchronized Builder<A> withObjectLongevityDetector(ObjectLongevityDetector objectLongevityDetector) {
            requireNonNull(objectLongevityDetector, "objectLongevityDetector cannot be null; use objectLongevityDetectorFeature().disable()");
            return this.objectLongevityDetector.set(objectLongevityDetector);
        }

        public Feature<ObjectLongevityDetector> objectLongevityDetectorFeature() {
            return objectLongevityDetector;
        }

        public synchronized Builder<A> withRefreshExecutor(Executor refreshExecutor) {
            requireNonNull(refreshExecutor, "refreshExecutor cannot be null; use noRefreshExecutor()");
            return this.refreshExecutor.set(refreshExecutor);
        }

        public Feature<Executor> refreshExecutorFeature() {
            return refreshExecutor;
        }

        @Deprecated
        public synchronized Builder<A> withMetricsCollector(HollowMetricsCollector<HollowConsumerMetrics> metricsCollector) {
            requireNonNull(metricsCollector, "metricsCollector cannot be null; use metricsCollectorFeature().disable()");
            return this.metricsCollector.set(metricsCollector);
        }

        public Feature<HollowMetricsCollector<HollowConsumerMetrics>> metricsCollectorFeature() {
            return metricsCollector;
        }

        public synchronized HollowConsumerV2<A> buildSync() {
            HollowConsumerV2<A> consumer = build();

            // kickoff synchronous refresh
            // FIXME(timt): need a load that runs in caller's thread, not through background subscription
            throw new UnsupportedOperationException("unimplemented");
        }

        public synchronized CompletableFuture<? extends HollowConsumerV2<A>> buildAsync() {
            HollowConsumerV2<A> consumer = build();

            // TODO(timt): should it be an error to call buildAsync without an announcement watcher?
            // kickoff async refresh
            if (consumer.announcementWatcher != null)
                consumer.announcementWatcher.subscribeToUpdates(consumer);

            return consumer.initialLoad();
        }

        protected HollowConsumerV2<A> build() {
            // verify builder config
            // TODO(timt): collect all invalid configs and throw at the end
            if (!blobRetriever.hasValue() && !localBlobStore.hasValue())
                throw new IllegalStateException("A HollowBlobRetriever or local blob store path must be specified");
            if (apiClass.isDisabled())
                throw new IllegalStateException("An apiClass is required");
            if (cachedTypes.hasValue() && !apiClass.hasValue())
                throw new IllegalStateException("Must specify an apiClass when using cachedTypes");
            if (objectLongevityConfig.isDisabled() && objectLongevityDetector.hasValue())
                throw new IllegalStateException("Cannot disable objectLongevityConfig when specifying an objectLongevityDetector");
            // FIXME(timt): why not allow doubleSnapshotConfig.disable()? Ff disabled objectLongevityDetector means
            //              object longevity feature is turned off, then disabling doubleSnapshotConfig should
            //              disallow double snapshots.
            //              One discrepancy: object longevity is opt-in whereas double-snapshot is opt-out, at least
            //              with the current defaults.
            if (doubleSnapshotConfig.isDisabled())
                throw new IllegalStateException("Cannot disable doubleSnapshotConfig");
            if (refreshExecutor.isDisabled())
                throw new IllegalStateException("A refreshExecutor is required");
            // FIXME(timt): verify any remaining features

            // apply platform defaults
            if (localBlobStore.hasValue())
                blobRetriever.set(new HollowFilesystemBlobRetriever(localBlobStore.get(), blobRetriever.get()));

            if (refreshListeners.isSpecified())
                refreshListenerList = refreshListeners.get().stream().collect(toList());
            else
                refreshListenerList = emptyList();

            if (!cachedTypes.isSpecified())
                cachedTypes.set(new String[0]);
            apiFactory = apiClass.hasValue()
                    ? new HollowAPIFactory.ForGeneratedAPI<>(apiClass.get(), cachedTypes.get())
                    : HollowAPIFactory.DEFAULT_FACTORY;

            if (objectLongevityConfig.isDisabled() || objectLongevityDetector.isDisabled()) {
                objectLongevityConfig.disable();
                objectLongevityDetector.disable();
            }
            if (!objectLongevityConfig.isSpecified())
                objectLongevityConfig.set(ObjectLongevityConfig.DEFAULT_CONFIG);
            if (!objectLongevityDetector.isSpecified())
                objectLongevityDetector.set(ObjectLongevityDetector.DEFAULT_DETECTOR);

            if (!doubleSnapshotConfig.isSpecified())
                doubleSnapshotConfig.set(DoubleSnapshotConfig.DEFAULT_CONFIG);

            if (!refreshExecutor.isSpecified()) {
                ExecutorService es = Executors.newSingleThreadExecutor(r -> {
                    Thread t = new Thread(r, "hollow | consumer-refresh");
                    t.setDaemon(true);
                    return t;
                });
                refreshExecutor.set(es);
            }

            // build it
            // TODO: add to builder DSL
            metrics = new HollowConsumerMetrics();
            refreshLock = new ReentrantReadWriteLock();

            // leave out of builder DSL
            hashCodeFinder = new DefaultHashCodeFinder();
            updater = newClientUpdater();

            return newConsumer();
        }

        protected HollowClientUpdaterV2<A> newClientUpdater() {
            return new HollowClientUpdaterV2<>(this);
        }

        protected HollowConsumerV2<A> newConsumer() {
            return new HollowConsumerV2<>(this);
        }

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
