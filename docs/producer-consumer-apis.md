# The Producer/Consumer APIs

In [Getting Started](getting-started.md) we encountered basic usage of the `HollowProducer` and `HollowConsumer` APIs.  This basic usage implies some default behavior which, if desired, may be customized to better suit your purposes.  A more in-depth exploration of the available customizable features of these APIs follows.

## The HollowProducer

Generally, a producer runs a repeating _cycle_.  At the end of each cycle, the producer has created a _data state_, published the artifacts necessary for consumers to bring their in-memory data stores to that _data state_, and announced the availability of the _state_.

The `HollowProducer` encapsulates the details of publishing, announcing, validating, and (if necessary) rollback of data states.  In order to accomplish this, a few infrastructure hooks should be injected:

```java
HollowProducer
   .withPublisher(publisher)         /// required: a BlobPublisher
   .withAnnouncer(announcer)         /// optional: an Announcer
   .withValidators(validators)       /// optional: one or more Validator
   .withListeners(listeners)         /// optional: one or more HollowProducerListeners
   .withBlobStagingDir(dir)          /// optional: a java.io.File
   .withBlobCompressor(compressor)   /// optional: a BlobCompressor
   .withBlobStager(stager)           /// optional: a BlobStager
   .withSnapshotPublishExecutor(e)   /// optional: a java.util.concurrent.Executor
   .withNumStatesBetweenSnapshots(n) /// optional: an int
   .withTargetMaxTypeShardSize(size) /// optional: a long
   .withBlobStorageCleaner(blobStorageCleaner) //optional: a BlobStorageCleaner
   .withMetricsCollector(hollowMetricsCollector) //optional: a HollowMetricsCollector<HollowProducerMetrics>
```

Let's examine each of the injected configurations into the `HollowProducer`:

* `BlobPublisher`: Implementations of this class define how to publish blob data to the blob store.
* `Announcer`: Implementations of this class define the announcement mechanism, which is used to track the version of the currently announced state.
* `Validator`: Implementations of this class allow for semantic validation of the data contained in a state prior to announcement.  If an Exception is thrown during validation, the state will not be announced, and the producer will be automatically rolled back to the prior state.
* `HollowProducerListener`: Listeners are notified about the progress and status of producer cycles throughout the various cycle stages.
* __Blob staging directory__: Before blobs are published, they must be written and inspected/validated.  A directory may be specified as a File to which these "staged" blobs will be written prior to publish.  Staged blobs will be cleaned up automatically after publish.
* `BlobCompressor`: Implementations of this class intercept blob input/output streams to allow for compression in the blob store.
* `BlobStager`: Implementations will define how to stage blobs, if the default behavior of staging blobs on local disk is not desirable.  If a custom `BlobStager` is provided, then neither a blob staging directory or `BlobCompressor` should be provided.
* __Snapshot publish__ `Executor`: When consumers start up, if the latest announced version does not have a snapshot, they can load an earlier snapshot and follow deltas to get up-to-date.  A state can therefore be available and announced prior to the availability of the snapshot.  If an Executor is supplied here, then it will be used to publish snapshots.  This can be useful if snapshot publishing takes a long time -- subsequent cycles may proceed while snapshot uploads are still in progress.
* __Number of cycles between snapshots__: Because snapshots are not necessary for a data state to be available and announced, they need not be published every cycle.  If this parameter is specified, then a snapshot will be produced only every `(n+1)th` cycle.
* `VersionMinter`: Allows for a custom version identifier minting strategy.
* __Target max type shard size__: Specify a [target max type shard size](advanced-topics.md#type-sharding).  Defaults to 16MB.
* `BlobStorageCleaner`: Using the [blob storage cleaning](tooling.md#blob-storage-manipulation) capability, it's possible to free up the blob storage and prevent running out of space because of old snapshots/deltas.
* `HollowMetricsCollector`: Implementing a [`HollowMetricsCollector`](tooling.md#metrics) allows to either store or publish those metrics to your preferred provider, such as Prometheus.

Each time a new _data state_ should be produced, users should call `.runCycle(Populator)`.  See [Getting Started](getting-started.md) for more basic usage details.


### Restoring At Startup

Ideally the same `HollowProducer` would be held in memory forever, and `runCycle()` would be called every so often to produce a never-ending intact _delta chain_.  However, this isn’t always possible; the producer will need to be restarted from time to time due to deployment or other operational circumstances.

In order to produce a delta between states produced by one `HollowProducer` and another, the producer can _restore_ the prior state upon restart, which will allow a delta and reverse delta to be produced.  See [Restoring at Startup](getting-started.md#restoring-at-startup) for usage.

Once we have _restored_ the prior state, we can produce a delta from our producer's first cycle.  The delta will be applicable to any consumers which are on the state from which we restored.  

!!! hint "Initializing Before Restore"
    Before _restoring_, we must always _initialize_ our data model.  A `HollowProducer`'s data model may be initialized:

    * via the `HollowObjectMapper` by calling `initTypeState()` with all top-level classes
    * via a set of schemas [loaded from a text file](advanced-topics.md#schema-parser) using the `HollowSchemaParser` and `HollowWriteStateCreator`

!!! note "Truncating a Delta Chain"
    If a problem occurs and you need to [pin back](#pinning-consumers) consumers, you _may_ want to restart your producer and explicitly restore from the pinned state.  Once the producer's first cycle completes, it will publish a delta from the pinned state to the newly produced state, _overwriting_ the previous delta from the pinned state.  In this way, when you unpin, consumers will automatically follow the _new_ delta, and the old forward-path from the pinned state will be _truncated_.

    If any consumers somehow did happen to remain on a _truncated_ state, the reverse delta out of the truncated chain is still intact -- they could be manually pinned back to the restored state, then unpinned to get back up-to-date.

### Rolling Back

While producing a new state, if the `HollowProducer` encounters an error during data state population or validation fails, the current _data state_ will be aborted and the underlying _state engine_ will be rolled back to the previous data state.  Any delta produced on the next cycle will be from the last _successful_ data state.

### Validating Data

It likely makes sense to perform some basic _validation_ on your produced data states before announcing them to clients.  If you provide one or more `Validator`s to the `HollowProducer`, these will be automatically executed prior to announcement.  Validation rules will be specific to the semantics of the dataset, and may include some heuristics-based metrics based on expectations about the dataset.  If your `Validator` throws an Exception, the `HollowProducer` will automatically roll back the state engine and the _next successful_ cycle will produce a delta from the prior successful state.  

### Compacting Data

It is possible to produce delta chains which extend over many thousands of states.  If during this delta chain an especially large delta happens for a specific type, it’s possible that many ordinal holes will be present in that type.  If over time multiple types go through especially large deltas, this can have an impact on a dataset’s heap footprint.

To reclaim heap space occupied by ordinal holes, a special _compaction cycle_ can be run on the `HollowProducer`.  During compaction, no record data will change, but identical records will be relocated off of the high end of the ordinal space into the ordinal holes.  This is accomplished by producing a new data state with no changes except for the more optimal ordinal assignments.

To run a _compaction cycle_, call `runCompactionCycle(config)` on the `HollowProducer`.  If this method returns a valid version identifier, then a compaction cycle occurred and produced a new data state.  If it returns `Long.MIN_VALUE`, then the compaction criteria specified in the `CompactionConfig` was not met and no action was taken.  See the `HollowCompactor` javadoc for more details.


## The HollowConsumer

Data consumers keep their local copy of a dataset current by ensuring that their state engine is always at the latest _announced_ data state. Consumers can arrive at a particular data state in a couple of different ways:

* At initialization time, they will load a snapshot, which is an entire copy of the dataset to be forklifted into memory.
* After initialization time, they will keep their local copy of the dataset current by applying delta transitions, which are the differences between adjacent data states.

The `HollowConsumer` encapsulates the details of initializing and keeping a dataset up to date.  In order to accomplish this task, a few infrastructure hooks should be injected:

```java
HollowConsumer
   .withBlobRetriever(blobRetriever)              /// required: a BlobRetriever
   .withLocalBlobStore(localDiskDir)              /// optional: a local disk location
   .withAnnouncementWatcher(announcementWatcher)  /// optional: a AnnouncementWatcher
   .withRefreshListener(refreshListener)          /// optional: a RefreshListener
   .withGeneratedAPIClass(MyGeneratedAPI.class)   /// optional: a generated client API class
   .withFilterConfig(filterConfig)                /// optional: a HollowFilterConfig
   .withDoubleSnapshotConfig(doubleSnapshotCfg)   /// optional: a DoubleSnapshotConfig
   .withObjectLongevityConfig(objectLongevityCfg) /// optional: an ObjectLongevityConfig
   .withObjectLongevityDetector(detector)         /// optional: an ObjectLongevityDetector
   .withRefreshExecutor(refreshExecutor)          /// optional: an Executor
   .withMetricsCollector(hollowMetricsCollector) //optional: a HollowMetricsCollector<HollowConsumerMetrics>
   .build();
```

Let's examine each the injected hooks to the `HollowConsumer`:

* `BlobRetriever`: The interface to the blob store.  This is the only hook for which a custom implementation is required.  Each of the other hooks have default implementations which may be used.  The `BlobRetriever` may be omitted only if a previously-populated local blob store is specified.
* __Local blob store__: A `File` which indicates where to record downloaded blobs and find previously downloaded blobs.  If specified along with a `BlobRetriever`, the `HollowConsumer` will prefer to use previously downloaded blobs where applicable, and otherwise write newly downloaded blobs to the specified directory.  If specified _without_ a `BlobRetriever`, only previously downloaded blobs will be available.
* `AnnouncementWatcher`: Provides an interface to the state announcement mechanism.  Often, announcement polling logic is encapsulated inside implementations.
* `RefreshListener`: Provides hooks so that actions may be taken during and after updates (e.g. indexing).
* __Generated API Class__: Specifies a [custom-generated Hollow API](getting-started.md#consumer-api-generation) to use.
* `HollowFilterConfig`:
* `DoubleSnapshotConfig`: Defines advanced settings related to [double snapshots](advanced-topics.md#double-snapshots).
* `ObjectLongevityConfig`: Defines advanced settings related to [object longevity](advanced-topics.md#object-longevity).
* `ObjectLongevityDetector`: Implementations are notified when stale hollow object existence and usage is detected.
* `RefreshExecutor`: An `Executor` to use when asynchronous updates are called via `triggerAsyncRefresh()`.
* `HollowMetricsCollector`: Implementing a [`HollowMetricsCollector`](tooling.md#metrics) allows to either store or publish those metrics to your preferred provider, such as Prometheus.

Each time the identifier of the currently announced state changes, `triggerRefresh()` should be called on the `HollowConsumer`.  This will bring the data up to date.

In general, the only requirement for getting Hollow consumers to work with your specific infrastructure is to implement a `BlobRetriever` and `AnnouncementWatcher`, and use them with a `HollowConsumer`.

!!! hint "Triggering Refresh"
    When implementing a `AnnouncementWatcher`, you will need to implement the method `subscribeToUpdates(HollowConsumer consumer)`.  When you
    create a `HollowConsumer` with an `AnnouncementWatcher`, it will automatically call back to this method with itself as the argument.  

    You should track all `HollowConsumer`s received by calls to this method.  When your announcement mechanism provides an updated value,
    you should notify each `HollowConsumer` via the `triggerAsyncRefresh()` method.

    In this way, your `HollowConsumer` injected with this `HollowAnnouncementWatcher` implementation will be automatically kept up-to-date.


### Dataset Consistency

If you have a long-running process which requires a consistent view of the dataset in a single state, you can prevent the `HollowConsumer` from updating while your process runs:

```java
HollowConsumer consumer = ...

consumer.getRefreshLock().lock();
try {
    /// run your process
} finally {
    consumer.getRefreshLock().unlock();
}
```

The `getRefreshLock()` call returns the read lock in a `ReadWriteLock`.  Refreshes use the write lock.
