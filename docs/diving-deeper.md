# Diving Deeper

The following section details some lower-level concepts which will provide the backdrop for some more advanced usage of Hollow.  If we didn't have a `HollowProducer` or `HollowConsumer`, this section details how we would use Hollow.

## State Engines

Both the `HollowProducer` and `HollowConsumer` handle datasets with a _state engine_.  A state engine can be transitioned between data states.  A producer uses a _write state engine_ and a consumer uses a _read state engine_.  

* A `HollowReadStateEngine` can be obtained from a `HollowConsumer` via the method `getStateEngine()`.
* A `HollowWriteStateEngine` can be obtained from a `HollowProducer` via the method `getWriteEngine()`.

## Ordinals

Each record in a Hollow data state is assigned to a specific _ordinal_, which is an integer value. An _ordinal_:

* is a unique identifier of the record within a type.
* is sufficient to locate the record within a type.

Ordinals are automatically assigned by Hollow. They lie in the range of 0-n, where n is generally not much larger than the total number of records for the type.  In lower-level usage of Hollow, ordinals are often used as proxies for handles to specific records.

Given a `HollowReadStateEngine`, you can retrieve the set of currently populated ordinals using the call `stateEngine.getTypeState("TypeName").getPopulatedOrdinals()`.  A `BitSet` containing all of the populated ordinals is returned.  Similarly, the ordinals which were populated prior to the last delta transition can be obtained using `stateEngine.getTypeState("TypeName").getPreviousOrdinals()`.

!!! warning "Populated Ordinals"
    Never modify the `BitSet` returned from `getPopulatedOrdinals()` or `getPreviousOrdinals()`.  Modifying these may corrupt the data store.

It's useful to note that records in Hollow are immutable.  They will never be _modified_, only removed and added.  A _modification_ probably means that within the same delta there was a removal of a record keyed by some value and an addition of a new record keyed by the same value.

Ordinals have some useful properties:

* It is guaranteed that if an exactly equivalent record exists in two adjacent states, then that record will retain the same ordinal. If, on the other hand, a record does not have an exact equivalent in an adjacent state, then its ordinal will not be populated in the state in which it does not exist.
* After a single delta transition has been applied which removes a record, that record will be marked as not populated, but the data for that record will still be accessible at that ordinal until the _next_ delta transition.  We call these records _ghost records_.

## Writing a Data Snapshot

Let's assume we have a POJO class `Movie`:
```java
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

In order to create a new data state and write it to disk, we can use a `HollowWriteStateEngine` directly:
```java
HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

for(Movie movie : movies)
    mapper.addObject(movie);

OutputStream os = ...; /// where to write the blob
HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
writer.writeSnapshot(os);
```

A `HollowWriteStateEngine` is the main handle to a Hollow dataset for a data producer.  A `HollowObjectMapper` is one of a few different ways to populate a `HollowWriteStateEngine` with data.  When starting with POJOs, it's the easiest way.

We'll use a `HollowBlobWriter` to write the current state of a `HollowWriteStateEngine` to an `OutputStream`.  We call the data which gets written to the `OutputStream` a _blob_.  

## Reading a Data Snapshot

A data consumer can load a snapshot created by the producer into memory:
```java
HollowReadStateEngine readEngine = new HollowReadStateEngine();
HollowBlobReader reader = new HollowBlobReader(readEngine);

InputStream is = /// where to load the snapshot from
reader.readSnapshot(is);
```

A `HollowReadStateEngine` is our main handle to a Hollow dataset as a consumer.  A `HollowBlobReader` is used to consume blobs into a `HollowReadStateEngine`.  Above, we're consuming a snapshot blob in order to initialize our state engine.  

Once this dataset is loaded into memory, we can access the data for any records using our [generated API](getting-started.md#consumer-api-generation):
```java
MovieAPI movieApi = new MovieAPI(readEngine);

for(Movie movie : movieApi.getAllMovieHollow()) {
    /// do something for each Movie record
}
```

## Writing a Delta

Some time has passed and the dataset has evolved.  The producer, with the same `HollowWriteStateEngine` in memory, needs to communicate this updated dataset to consumers.  The data for the new state must be added to the state engine, after which a transition from the previous state to the new state can be written as a _delta_ blob:
```java
writeEngine.prepareForNextCycle();

for(Movie movie : movies)
    mapper.addObject(movie);

OutputStream os = ....; /// where to write the delta blob
writer.writeDelta(os);
```

Let's take a closer look at what the above code does.  The same `HollowWriteStateEngine` which was used to produce the _snapshot_ blob is used -- it already knows everything about the prior state and can be transitioned to the next state.  We call `prepareForNextCycle()` to inform the state engine that the writing of blobs from the prior state is complete, and populating data into the next state is about to begin.  When creating a new state, all of the movies currently in our dataset are re-added again.  It's not necessary to figure out which records were added, removed, or modified -- that's Hollow's job.

We can (but don't have to) use the same `HollowObjectMapper` and/or `HollowBlobWriter` as we used in the prior _cycle_ to create the initial snapshot.  

The call to `writeDelta()` records a _delta_ blob to the `OutputStream`.  Encoded into the delta is a set of instructions to update a consumer’s read state engine from the previous state to the current state.

!!! hint "Reverse Deltas"
    Just as you can call `writeDelta()` to write a delta from one state to the next, you can also call `writeReverseDelta()` to write the reverse operation which will take you from the next state to the prior state.

## Reading a Delta

Once a delta is available the HollowReadStateEngine can be updated on the client:
```java
InputStream is = /// where to load the delta from
HollowBlobReader reader = new HollowBlobReader(readEngine);
reader.applyDelta(is);
```

The same `HollowReadStateEngine` into which our snapshot was consumed must be reused to consume a _delta_ blob.  This state engine knows everything about the current state and can use the instructions in a delta to transition to the next state.  We can (but don't have to) reuse the same `HollowBlobReader`.

After this delta has been applied, the read state engine is at the new state.  

!!! hint "Thread Safety"
    It is safe to use the HollowReadStateEngine to retrieve data while a delta transition is in progress.

!!! danger "Delta Mismatch"
    If a delta application is attempted onto a `HollowReadStateEngine` which is at a state from which the delta did not originate, then an exception is thrown and the state engine remains safely unchanged.

## Indexing Data for Retrieval

In prior examples the generated Hollow API was used by the data consumer to iterate over all `Movie` records in the dataset.  Most often, however, it isn’t desirable to iterate over the entire dataset — instead, specific records will be accessed based on some known key.  Let’s assume that the `Movie`’s id is a known key.

After consumers have populated a `HollowReadStateEngine`, the data can be indexed:
```java
HollowPrimaryKeyIndex idx =
                      new HollowPrimaryKeyIndex(readEngine, "Movie", "id");

idx.listenForDeltaUpdates();
```

This index can be held in memory and then used in conjunction with the generated Hollow API to retrieve Movie records by id:
```
int movieOrdinal = idx.getMatchingOrdinal(2);
if(movieOrdinal != -1) {
    MovieHollow movie = movieApi.getMovieHollow(movieOrdinal);
    System.out.println("Found Movie: " + movie._getTitle()._getValue());
}
```

Which outputs:
```
Found Movie: Beasts of No Nation
```

!!! warning "Keeping an Index Up To Date"
    The call to `listenForDeltaUpdates()` will cause a `HollowPrimaryKeyIndex` to automatically stay updated when deltas are applied to the indexed `HollowReadStateEngine`, but this should only be called if you intend to keep the index around.  See the Indexing / Querying section for usage details.

!!! hint "Thread Safety"
    Retrievals from a `HollowPrimaryKeyIndex` are thread-safe.  It is safe to use a `HollowPrimaryKeyIndex` from multiple threads, and it is safe to query while a transition is in progress.

!!! note "Ordinals"
    See [ordinals](#ordinals) for a discussion about ordinals.


### HollowPrimaryKeyIndex

In the above example, the primary key is defined for `Movie` as its `id` field.  A primary key can also be defined over multiple and/or hierarchical fields.  Imagine that `Movie` additionally had a `country` field defined in its schema, and that across countries, `Movie` `id`s may be duplicated, but that there will never exist two `Movie` records with the same id and country:
```java
@HollowPrimaryKey(fields={"id", "country.id"})
public class Movie {
    long id;
    Country country;
    ...
}

@HollowPrimaryKey(fields={"id"})
public class Country {
    String id;
    String name;
}
```

A `HollowPrimaryKeyIndex` can be defined with a primary key consisting of both fields:
```java
HollowPrimaryKeyIndex idx =
            new HollowPrimaryKeyIndex(readEngine, "Movie", "id", "country.id.value");
idx.listenForDeltaUpdates();
```

And to query for a `Movie` based on its id and country:
```java
int movieOrdinal = idx.getMatchingOrdinal(2, "US");
if(movieOrdinal != -1) {
    Movie movie = movieApi.getMovie(movieOrdinal);
    System.out.println("Found Movie: " + movie.getTitle().getValue());
}
```
Notice that `Movie`’s country field in the above example is actually a `REFERENCE` field.  The defined key includes the id of the movie, and the value of the id String of the referenced country.  We denote this traversal using dot notation in the primary key definition.  The field definitions can be multiple references deep.

The requirement for a primary key definition is that no duplicates should exist for the defined combination of fields.  If this rule is violated, an arbitrary match will be returned for queries when multiple matches exist.  

!!! note "Primary Key Violations"
    Violations of the "no duplicate" primary key rule can be detected using the `getDuplicateKeys()` method on a `HollowPrimaryKeyIndex`, which returns a `Collection<Object[]>`.  If no duplicate keys exist, the returned Collection will be empty.  If they do, the returned values will indicate the keys for which duplicate records exist.

If a `HollowPrimaryKeyIndex` will be retained for a long duration, they should be kept updated as deltas are applied to the underlying `HollowReadStateEngine`.  This is accomplished with a single call after instantiation to the `listenForDeltaUpdates()` method.

!!! warning "Detaching Primary Key Indexes"
    If `listenForDeltaUpdates()` is called on a primary key index, then it cannot be garbage collected.  If you intend to drop an index which is listening for updates, first call `detachFromDeltaUpdates()` to prevent a memory leak.

Indexes which are listening for delta updates are updated after a dataset is updated.  In the brief interim time between when a dataset is updated and the index is updated, the index will point to the _ghost records_ located at tombstoned ordinals.  This helps guarantee that all in-flight operations will observe correct data.

### HollowHashIndex

It is sometimes desirable to index records by fields other than primary keys.  The `HollowHashIndex` allows for indexing records by fields or combinations of fields for which values may match multiple records, and records may match multiple values.

In our `Movie`/`Actor` example, we may want to index movies by their starring actors:
```java
HollowHashIndex idx =
            new HollowHashIndex(readEngine, "Movie", "", "cast.element.actor.actorId");
```

The `HollowHashIndex` expects in its constructor arguments a query start type, a select field, and a set of match fields.  The constructor arguments above indicate that queries will start with the `Movie` type, select the root of the query (indicated by the empty string), and match the id of any `Actor` record in the actors list.

To query this index:
```java
HollowHashIndexResult result = idx.findMatches(102);

if(result != null) {
    System.out.println("Found matches: " + result.numResults());

    HollowOrdinalIterator iter = result.iterator();
    int matchedOrdinal = iter.next();
    while(matchedOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
        Movie movie = api.getMovie(matchedOrdinal);
        System.out.println("Starred in: " + movie.getTitle().getValue());
        matchedOrdinal = iter.next();
    }
}
```

Alternatively, if the data model included the nationality of actors, and we needed to index actors by nationality and the titles of movies in which they starred:
```java
HollowHashIndex idx =
            new HollowHashIndex(readEngine, "Movie", "cast.element.actor",
                                            "title.value",
                                            "cast.element.actor.nationality.id.value");
```

In this case, the query start type is still `Movie`, but we’re selecting related `Actor` records.  Matches are selected based on the `Movie`’s title, and the actor’s nationality.  Using this index, one can query for Brazilian actors who starred in movies titled “Narcos”:
```java
HollowHashIndexResult result = idx.findMatches("Narcos", "BR");

if(result != null) {
    HollowOrdinalIterator iter = result.iterator();
    int matchedOrdinal = iter.next();
    while(matchedOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
        Actor actor = api.getMovie(matchedOrdinal);
        System.out.println("Matched actor: " +
                                      actor.getActorName().getValue());
        matchedOrdinal = iter.next();
    }
}
```

The `HollowHashIndex` does not yet have a facility for listening for delta updates.  If an index is necessary across multiple states, currently the index must be recreated on each update.
