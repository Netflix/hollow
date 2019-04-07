# Getting Started

In the [Quick Start](quick-start.md) guide, we got a reference implementation of Hollow up and running, with a mock data 
model that can be easily modified to suit any use case.  After reading this section, you'll have an understanding of the 
basic usage patterns for Hollow, and how each of the core pieces fit together.

## Core Concepts

Hollow manages datasets which are built by a single _producer_, and disseminated to one or many _consumers_ for 
read-only access.  A dataset changes over time.  The timeline for a changing dataset can be broken down into discrete 
_data states_, each of which is a complete snapshot of the data at a particular point in time.

## Producing a Data Snapshot

Let's assume we have a POJO class `Movie`:
```java
@HollowPrimaryKey(fields="id")
public class Movie {
    long id;
    String title;
    int releaseYear;

    public Movie(long id, String title, int releaseYear) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
    }
}
```

And that many `Movie`s exist which comprise a dataset that needs to be disseminated:
```java
List<Movie> movies = Arrays.asList(
        new Movie(1, "The Matrix", 1999),
        new Movie(2, "Beasts of No Nation", 2015),
        new Movie(3, "Pulp Fiction", 1994)
);
```

We'll need a data _producer_ to create a data state which will be transmitted to consumers:
```java
File localPublishDir = new File("/path/to/local/disk/publish/dir");

HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

HollowProducer producer = HollowProducer
        .withPublisher(publisher)
        .withAnnouncer(announcer)
        .build();

producer.runCycle(state -> {
    for(Movie movie : movies)
        state.add(movie);
});
```

This producer runs a single _cycle_ and produces a data state.  Once this runs, you should have a _snapshot_ blob file 
on your local disk.  

!!! note "Publishing Blobs"
    Note that the example code above is writing data to local disk.  This is a great way to start testing.  In a 
    production scenario, data can be written to a remote file store such as Amazon S3 for retrieval by consumers.  See 
    the [reference implementation](https://github.com/Netflix/hollow-reference-implementation) and the 
    [quick start guide](quick-start.md) for a scalable example using AWS.


## Consumer API Generation

Once the data has been populated into a producer, that producer's _state engine_ is aware of the data model, and can be 
used to automatically produce a client API.  We can also initialize the data model from a brand new _state engine_ using 
our POJOs:

```java
HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
mapper.initializeTypeState(Movie.class);

HollowAPIGenerator generator =
       new HollowAPIGenerator.Builder().withAPIClassname("MovieAPI")
                                       .withPackageName("how.hollow.example")
                                       .withDataModel(writeEngine)
                                       .build();

generator.generateFiles("/path/to/java/api/files");
```

After this code executes, a set of Java files will be written to the location `/path/to/java/api/files`.  These java 
files will be a generated API based on the data model defined by the schemas in our state engine, and will provide 
convenient methods to access that data.

!!! hint "Initializing multiple types"
    If we have multiple top-level types, we should call `initializeTypeState()` multiple times, once for each class.

## Consuming a Data Snapshot

A data consumer can load a snapshot created by the producer into memory:
```java
File localPublishDir = new File("/path/to/local/disk/publish/dir");

HollowFilesystemBlobRetriever blobRetriever =
                                new HollowFilesystemBlobRetriever(localPublishDir);

HollowFilesystemAnnouncementWatcher announcementWatcher =
                                new HollowFilesystemAnnouncementWatcher(localPublishDir);

HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobRetriever)
                                        .withAnnouncementWatcher(announcementWatcher)
                                        .withGeneratedAPIClass(MovieAPI.class)
                                        .build();

consumer.triggerRefresh();
```

The `HollowConsumer` will retrieve data using the provided `BlobRetrievier`, and will load the latest _data state_ 
currently announced by the `AnnouncementWatcher`.

Once this dataset is loaded into memory, we can access the data for any records using our generated API.  Below, we're 
iterating over all records:
```java
MovieAPI movieApi = (MovieAPI)consumer.getAPI();

for(MovieHollow movie : movieApi.getAllMovieHollow()) {
    System.out.println(movie.getId() + ", " +
                       movie.getTitle().getValue() + ", " +
                       movie.getReleaseYear());
}
```

The output of the above code will be:
```
1, The Matrix, 1999
2, Beasts of No Nation, 2015
3, Pulp Fiction, 1994
```

!!! note "Integrating with Infrastructure"
    In order to integrate with your infrastructure, you only need to provide Hollow with four implementations of simple 
    interfaces:

    * The `HollowProducer` needs a `Publisher` and `Announcer`
    * The `HollowConsumer` needs a `BlobRetriever` and `AnnouncementWatcher`

    Your `BlobRetriever` and `AnnouncementWatcher` implementations should be mirror your `Publisher` and `Announcer` 
    interfaces.   Here, we're publishing and retrieving from local disk.  In production, we'll be publishing to and 
    retrieving from a remote file store.  We'll discuss in more detail how to integrate with your specific 
    infrastructure in [Infrastructure Integration](infrastructure.md#infrastructure-integration).


## Producing a Delta

Some time has passed and the dataset has evolved.  It now contains these records:
```java
List<Movie> movies = Arrays.asList(
        new Movie(1, "The Matrix", 1999),
        new Movie(2, "Beasts of No Nation", 2015),
        new Movie(4, "Goodfellas", 1990),
        new Movie(5, "Inception", 2010)
);
```

The producer, needs to communicate this updated dataset to consumers.  We're going to create a brand new state, and the 
entirety of the data for the new state must be added to the state engine in a new _cycle_.   When the cycle runs, a new 
data state will be _published_, and the new data state's (automatically generated) version identifier will be 
_announced_.

Using the same `HollowProducer` in memory, we can use the following code:

```java
producer.runCycle(state -> {
    for(Movie movie : movies)
        state.add(movie);
});
```

Let's take a closer look at what the above code does.  The same `HollowProducer` which was used to produce the 
_snapshot_ blob is used -- it already knows everything about the prior state and can be transitioned to the next state.  
When creating a new state, __all of the movies currently in our dataset are re-added again.__  It's not necessary to 
figure out which records were added, removed, or modified -- that's Hollow's job.

Each time we call `runCycle` we will be producing a _data state_.  For each state after the first, the `HollowProducer` 
will publish three artifacts: a _snapshot_, a _delta_, and a _reverse delta_.  Encoded into the _delta_ is a set of 
instructions to update a consumer’s data store from the previous state to the current state.  Inversely, encoded into 
each _reverse delta_ is a set of instructions to update a consumer in reverse -- from the current state to the previous 
state.  Consumers may use the _reverse delta_ later if we need to [pin](infrastructure.md#pinning-consumers).

When consumers initialize, they will use the most recent _snapshot_ to initialize their data store.  After 
initialization, consumers will keep up to date using _deltas_.

!!! note "Producer Cycles"
    We call what the producer does to create a data state a _cycle_.  During each _cycle_, you’ll want to add 
    _every record_ from your source of truth.  Hollow will handle the details of publishing a delta for all of your 
    established consumer instances, and a snapshot to initialize any consumer instances which start up before your next 
    cycle.

## Consuming a Delta

No manual intervention is necessary to consume the delta you produced.  The `HollowConsumer` will automatically stay 
up-to-date.  

!!! hint "Announcements keep consumers updated"
    When the producer runs a cycle, it _announces_ the latest version.  The `AnnouncementWatcher` implementation 
    provided to the `HollowConsumer` will listen for changes to the announced version -- and when updates occur notify 
    the `HollowConsumer` by calling `triggerAsyncRefresh()`.  See the source of the 
    `HollowFilesystemAnnouncementWatcher`, or the 
    [two](https://github.com/Netflix/hollow-reference-implementation/blob/master/src/main/java/how/hollow/consumer/infrastructure/S3AnnouncementWatcher.java) 
    separate [examples](https://github.com/Netflix/hollow-reference-implementation/blob/master/src/main/java/how/hollow/consumer/infrastructure/DynamoDBAnnouncementWatcher.java) 
    in the reference implementation.

After this delta has been applied, the consumer is at the new state.  If the generated API is used to iterate over the 
movies again as shown in the prior consumer example, the new output will be:

```
1, The Matrix, 1999
2, Beasts of No Nation, 2015
4, Goodfellas, 1990
5, Inception, 2010
```

!!! hint "Thread Safety"
    It is safe to use Hollow to retrieve data while a delta transition is in progress.

!!! note "Adjacent States"
    We refer to states which are directly connected via single delta transitions as _adjacent_ states, and a continuous 
    set of adjacent states as a _delta chain_


### Incremental Production

If it is known what changes are applied to a dataset then incremental production may be utilized.  This can be
more efficient than providing the whole dataset on each cycle.  An incremental producer is built in a similar manner
to a producer:

```java
File localPublishDir = new File("/path/to/local/disk/publish/dir");

HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(localPublishDir);
HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(localPublishDir);

HollowProducer.Incremental incProducer = HollowProducer
        .withPublisher(publisher)
        .withAnnouncer(announcer)
        .buildIncremental();

incProducer.runIncrementalCycle(istate -> {
    for(Movie movie : modifiedMovies)
        state.addOrModify(movie);
    for(Movie movie : deletedMovies)
        state.delete(movie);
});
```

This incremental producer runs a cycle for the changes to set of movies (those which are new, have changed, or have been 
removed).  Other than that an incremental producer behaves the same as a producer and a consumer will not know the
difference.

!!! note "Adjacent States"
    Any record added, modified or removed must have a primary key, since this determines the
    record's identity


## Indexing Data for Retrieval

In prior examples the generated Hollow API was used by the data consumer to iterate over all `Movie` records in the 
dataset.  Most often, however, it isn’t desirable to iterate over the entire dataset — instead, specific records will be 
accessed based on some known key.  The `Movie`’s id is a known key (since it is annotated with `@HollowPrimaryKey`).

After a `HollowConsumer` has been initialized, any type can be indexed.  For example, we can index `Movie` records by 
`id`:
```java
HollowConsumer consumer = ...;

consumer.triggerRefresh();

UniqueKeyIndex<Movie, Integer> idx = Movie.uniqueIndex(consumer);
```

This index can be held in memory and then used in conjunction with the generated Hollow API to retrieve Movie records by 
id:
```
Movie movie = idx.findMatch(2);
if(movie != null)
    System.out.println("Found Movie: " + movie.getTitle().getValue());
```

Which outputs:
```
Found Movie: Beasts of No Nation
```

In our generated API, each type annotated with `@HollowPrimaryLey` has a static method to obtain a `UniqueIndex`.  For
primary keys with multiple fields a _bean_ class is also generated to hold key values.

A `UniqueIndex` may be also created explicitly to index by any field, or multiple fields.

!!! hint "Reuse Indexes"
    Retrieval from an index is extremely cheap, and indexing is (relatively) expensive.  You should create your indexes 
    when the `HollowConsumer` is initialized and share them thereafter.  Indexes will automatically stay up-to-date with 
    the `HollowConsumer`.

!!! hint "Thread Safety"
    Retrievals from Hollow indexes are thread-safe.  They are safe to use across multiple threads, and it is safe to 
    query while a transition is in progress.

We've just begun to scratch the surface of what indexes can do.  See [Indexing/Querying](indexing-querying.md) for an 
in-depth exploration of this topic.

## Hierarchical Data Models

Our data models can be much richer than in the prior example.  Assume an updated `Movie` class:
```java
public class Movie {
    long id;
    String title;
    int releaseYear;
    List<Actor> actors;

    public Movie(long id, String title, int year, List<Actor> actors) {
        this.id = id;
        this.title = title;
        this.releaseYear = year;
        this.actors = actors;
    }
}
```

Which references `Actor` records:
```java
@HollowPrimaryKey(field="actorId")
public class Actor {
    long actorId;
    String actorName;

    public Actor(long actorId, String actorName) {
        this.actorId = actorId;
        this.actorName = actorName;
    }
}
```

Some records are added to a `HollowProducer`:
```java
List<Movie> movies = Arrays.asList(
        new Movie(1, "The Matrix", 1999, Arrays.asList(
                new Actor(101, "Keanu Reeves"),
                new Actor(102, "Laurence Fishburne"),
                new Actor(103, "Carrie-Ann Moss"),
                new Actor(104, "Hugo Weaving")
        )),
        new Movie(6, "Event Horizon", 1997, Arrays.asList(
                new Actor(102, "Laurence Fishburne"),
                new Actor(105, "Sam Neill")
        ))
);

producer.runCycle(state -> {
    for(Movie movie : movies)
        state.addObject(movie);
});
```

When we add these movies to the dataset, Hollow will traverse everything referenced by the provided records and add them 
to the state as well.  Consequently, both a type `Movie` and a type `Actor` will exist in the data model after the above 
code runs.  

!!! hint "Deduplication"
    Laurence Fishburne starred in both of these films.  Rather than creating two `Actor` records for Mr. Fishburne, 
    a single record will be created and assigned to both of our `Movie` records.  This _deduplication_ happens 
    automatically by virtue of having the exact same data contained in both Actor inputs.

Consumers of this dataset may want to also create an index for `Actor` records.  For example:
```java
UniqueKeyIndex<Actor, Integer> idx = Actor.uniqueIndex(consumer);

Actor actor = actorIdx.findMatch(102);
if(actor != null)
    System.out.println("Found Actor: " + actor.getActorName().getValue());
```

Outputs:
```
Found Actor: Laurence Fishburne
```

## Restoring at Startup

From time to time, we need to redeploy our producer.  When we first create a `HollowProducer` and run a cycle it will 
not be able to produce a delta, because it does not know anything about the prior _data state_.  If no action is taken, 
a new state with only a snapshot will be produced and announced, and clients will load that data state with an operation 
called a [double snapshot](advanced-topics.md#double-snapshot), which has potentially undesirable performance 
characteristics.  

We can remedy this situation by _restoring_ our newly created producer with the last announced data state.  For example:

```java
Publisher publisher = ...
Announcer announcer = ...
BlobRetriever blobRetriever = ...
AnnouncementWatcher announcementWatcher = ...

HollowProducer producer = HollowProducer.withPublisher(publisher)
                                        .withAnnouncer(announcer)
                                        .build();

producer.initializeDataModel(Movie.class);

long latestAnnouncedVersion = announcementWatcher.getLatestVersion();
producer.restore(latestAnnouncedVersion, blobRetriever);

producer.runCycle(state -> {
   ...
});

```

In the above code, we first _initialize_ the data model by providing the set of classes we will add during the cycle.  
After that, we _restore_ by providing our `BlobRetriever` implementation, along with the version which should be 
restored.  The `HollowProducer` will use the `BlobRetriever` to load the desired state, then use it to _restore_ itself.  
In this way, a delta can be produced at startup, and consumers will not have to load a snapshot to get up-to-date.

!!! hint "Initializing the data model"
    Before _restoring_, we must always _initialize_ our data model.  When a data model changes between deployments, 
    Hollow will automatically merge records of types which have changed.  In order to do this correctly, Hollow needs to 
    know about the current data model before the restore operation begins.
