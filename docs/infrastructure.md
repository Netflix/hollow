# Infrastructure Integration

In order to wield Hollow effectively for your organization, you need only implement four interfaces to integrate with your infrastructure:

* A `Publisher` for the `HollowProducer`
* An `Announcer` for the `HollowProducer`
* A `BlobRetriever` for the `HollowConsumer`
* An `AnnouncementWatcher` for the `HollowConsumer`

Once you've implemented these four interfaces, Hollow can be used in many different contexts in your organization.  You'll never again have to write code to ship json or csv data from one machine to another -- and better yet, you'll gain visibility and insights into previously opaque and hard-to-debug datasets.

!!! hint "Think local first"
    The following sections describe how to plug Hollow into your infrastructure.  Now is the time to think about how you will debug your data later.  Consider making your implementations of these interfaces easily allow for (securely!) retrieving data from any environment, including production, right down to your local development box.

    If you take this step, you'll be giving yourself immense power to glean insight into your data and debug production issues.  Imagine it's 10am, and you suspect some issue surrounding some particular data was present at 4am this morning.  You can open up Eclipse or IntelliJ and write a main method which -- with a few lines of code -- pulls down the data _exactly_ as it existed on your production instances at that time.  You can write code against it to explore specific scenarios or feed it into the [explorer](tooling.md#hollow-explorer) to confirm or deny your suspicion in seconds.

## Storing the Blobs

Blobs are published to a file store which is accessible by consumers.  From this blob store, consumers must be able to query for and retrieve blobs in the following ways:

* __Snapshots__: Must be queryable based on the state identifier.  If a blob store is queried for a snapshot with an identifier which does not exist, the snapshot with the greatest identifier prior to the queried identifier should be retrieved.
* __Deltas__: Must be queryable based on the state identifier to which a delta should be applied (e.g. the delta's __from__ state identifier).
* __Reverse Deltas__: Must be queryable based on the state identifier to which a reverse delta should be applied (e.g. the reverse delta's __from__ state identifier).

The `Publisher` and `BlobRetriever` are opposite sides of your blob store (writer and reader, respectively).

Your `Publisher` implementation must only define a single method:

```java
    public void publish(HollowProducer.Blob blob);
```

The `Blob` passed to your `Publisher` should be published somewhere for retrieval by consumers.  The blob's data is retrieved for publish by calling either `newInputStream()` or `getFile()`.  The blob will be one of either a _snapshot_, _delta_, or _reverse delta_ -- the type can be determined by calling `getType()`.  The blob should be indexed for later retrieval as indicated above -- _snapshots_ by the result of `getToVersion()`, and _deltas_/_reversedeltas_ by the result of `getFromVersion()`.  Note that you will need to be able to distinguish between a _snapshot_, _delta_, and _reversedelta_ with the same version number.  

!!! note "Choosing a blob store"
    You can publish blobs anywhere -- S3, an FTP server, an NFS, etc -- so long as that selected blob store can scale to the necessary volume of concurrent consumer requests.  

    Note that if your announcement mechanism is instantaneous all consumers will attempt to retrieve the blob files simultaneously.

!!! warning "Blobs must be overwriteable"
    Your `Publisher` implementation must allow blobs to be overwritten.  If an attempt is made to publish a blob with to be indexed by a state identifier for which a corresponding artifact already exists, it must _overwrite_ the existing artifact previously published.  This happens routinely -- for example if a data state fails after publishing for any reason (e.g. validation fails), then the producer will automatically roll back the state and a delta will be re-published with the same _from_ version.

The `BlobRetriever` is the other side of the blob store equation.  Your implementation must define three methods:

```java
    public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion);

    public HollowConsumer.Blob retrieveDeltaBlob(long currentVersion);

    public HollowConsumer.Blob retrieveReverseDeltaBlob(long currentVersion);
```

The `Blob` you return will be a custom implementation for your blob store which extends `HollowConsumer.Blob` and implements the `getInputStream()` method.  

Your `retrieveSnapshotBlob(long desiredVersion)` implementation should return the blob which exactly matches the specified `desiredVersion` if it exists.  If no such version exists then the greatest available version which is _less than_ the specified `desiredVersion` should be returned.  If no such match exists, return null.

Your `retrieveDeltaBlob(long currentVersion)` and `retrieveReverseDeltaBlob(long currentVersion)` implementations should each return the blob which exactly matches the specified `currentVersion`.  If no such match exists, return null.

!!! hint "Scanning for snapshots"
    If an exact match for the requested snapshot doesn't exist, you'll need to scan the available versions for the closest match prior to the requested.  For this reason, if you have a large number of consumers, it makes sense to _index_ your available snapshot versions so this operation is fast.


## Announcing the State

Once the necessary transitions to bring clients up to date have been written to the blob store, the availability of the state must be _announced_ to clients.  This simply means that a centralized location must be maintained and updated by the producer which indicates the version of the currently available state.  

When this announced state is updated, usually it is desirable to have consumers realize this update as quickly as possible.  This can be accomplished either via a push notification to all consumers, or via frequent polling by consumers.

The `Announcer` and `AnnouncementWatcher` are opposite sides of your announcement mechanism (writer and reader, respectively).

Your `Announcer` implementation must only implement a single method:

```java
    public void announce(long stateVersion);
```

The `stateVersion` passed to your `Announcer` should be immediately communicated to your consumers.  You can use either a 'push' mechanism or a frequent 'polling' mechanism to minimize the time between when a producer announces a version, and all consumers receive that announcement.

Your `AnnouncementWatcher` implementation must implement two methods:

```java
    public long getLatestVersion();

    public void subscribeToUpdates(HollowConsumer consumer);
```

When your `AnnouncementWatcher` is initialized, you should immediately set up your selected announcement mechanism -- either subscribe to your push notifications or set up a thread to poll for updates.  

Implementations should maintain a list of subscribed `HollowConsumer`s, and each time `subcribeToUpdates(HollowConsumer consumer)` is called, you should add the provided `HollowConsumer` to your list.  When the announced version changes, call `triggerAsyncRefresh()` on each subscribed consumer.

Whether or not any `HollowConsumer`s are subscribed, implementations should return the latest announced version each time `getLatestVersion()` is called.

!!! note "HollowConsumer subscribes itself"
    A `HollowConsumer` will automatically call `subscribeToUpdates()` with itself for an `AnnouncementWatcher` with which it is initialized.


### Pinning Consumers

Mistakes happen.  What's important is that we can recover from them quickly.  If you accidentally publish bad data, you should be able to revert those changes quickly.  If you give your `AnnouncementWatcher` implementation an alternate location to read the announcement from, which _overrides_ the announcement from the consumer, then you can use this to quickly force clients to go back to any arbitrary state in the past.  We call setting a state version in this alternate location _pinning_ the consumers.

Implementing a pinning mechanism is extremely useful and highly recommended.  You can operationally reverse data issues immediately upon discovery, so that symptoms go away while you diagnose exactly what went wrong.  This can save an enormous amount of stress and money.

!!! danger "Unpinning"
    If you've pinned consumers due to a data issue, it's probably not desirable to simply 'unpin' them after the root cause is addressed.  Instead, restart the producer and instruct it to [restore](#restoring-at-startup) from the pinned state.  It should then produce a delta which skips over all of the bad states.  Only unpin after the delta from the pinned version to a bad version is overwritten with a delta from the pinned version to the good version.


### Blob Namespaces

Different use cases within your organization may want to reuse the same infrastructure integration.  You may want your `BlobRetriever` and `AnnouncementWatcher` to allow for multiple blob _namespaces_, one for each use case.
