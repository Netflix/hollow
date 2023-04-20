# Indexing/Querying
Hollow supports indexing of your dataset for quick retrieval. This guide demonstrates on usages of different indexes available and how they could be used to query for faster retrieval.

## A Data Model

For the purposes of these examples, let's imagine we have a data model defined by the following Objects:

```java
@HollowPrimaryKey(fields="id")
public class Movie {
    int id;
    String title;

    @HollowHashKey(fields="actor.actorId")
    Set<ActorRole> cast;

    CountryCode releaseCountry;
}

public class ActorRole {
    Actor actor;
    int movieId;
    String characterName;
}

@HollowPrimaryKey(fields="actorId")
public class Actor {
    int actorId;
    String name;
}

enum CountryCode {
    US, CA, ME
}
```

## Primary Key Indexes

When we [generate a client API](getting-started.md#consumer-api-generation), each type in our data model gets a custom index class called `<typename>PrimaryKeyIndex`.  We can use these classes to look up records based on _primary key_ values.

### Default Primary Keys

Once we have loaded a dataset into a `HollowConsumer`, we can use the `Movie` index to retrieve data by the default primary key `id`:

```java

HollowConsumer consumer = ...;

MoviePrimaryKeyIndex idx = new MoviePrimaryKeyIndex(consumer);

int knownMovieId = ...;

Movie movie = idx.findMatch(knownMovieId);

```

Just as the `HollowConsumer` will automatically stay up-to-date as your dataset updates, a primary key index will also stay up-to-date with the `HollowConsumer` with which it is backed.

!!! hint "Share Indexes"
    Queries to indexes are thread-safe.  We should create each of the indexes we need only once, and share them everywhere they are needed.

### Consumer-specified Primary Keys

In the prior example, our primary key index was using the default primary key defined in the data model.  A primary key index is not restricted to just default primary keys.  For example, we could _also_ index movies by their title:

```java
MoviePrimaryKeyIndex idx = new MoviePrimaryKeyIndex(consumer, "title");

String knownMovieTitle = ...;

Movie movie = idx.findMatch(knownMovieTitle);

```

!!! note "Primary Keys"
    A primary key index should be used when there is a one-to-one mapping between records and key values.  A primary key can only return one record per key, and if multiple records exist for a given key, then an arbitrary match will be returned.

### Compound Primary Keys

A primary key index may also be specified over multiple fields.  For example, we can define a primary key index for the `ActorRole` type above:

```java
ActorRolePrimaryKeyIndex idx = new ActorRolePrimaryKeyIndex(consumer, "actor.id", "movieId");

int knownActorId = ...;
int knownMovieId = ...;

ActorRole actorRoleInMovie = idx.findMatch(knownActorId, knownMovieId);
```

In the above example, we are looking for the actor role which matches _both_ the actor ID and the movie ID.  Note that the actor id was specified with dot-notation as `actor.id`.  This is a _field path_, and indicates that the actual value we're indexing belongs to a _referenced_ record.  Note that for a primary key index, we can only traverse through referenced `Object` records, not `List`, `Set`, or `Map` records.  We'll cover more about field paths [a bit further down](#field-paths).

## Hash Indexes

If we want to find records based on keys for which there is not a one-to-one mapping between records and key values, we want a _hash index_.  With our generated client API, we have a single class `<API classname>HashIndex`.  We can use instances of this class to specify hash indexes.  A hash index must specify each of a _query type_, a _select field_, and one or more _match fields_.  If we want to _select_ the same type we are using to search, we should specify our _select field_ as and empty String `""`.

For example, if we want to match `Movie` records which had characters with some name, we can use the following:

```java
MovieAPIHashIndex idx = new MovieAPIHashIndex("Movie", "", "cast.element.characterName.value");

String knownCharacterName = ...;

for(Movie movie : idx.findMovieMatches(knownCharacterName)) {
    System.out.println("The movie " + movie.getTitle().getValue() +
                       " has a character named " + knownCharacterName);
}
```

Above, we are _selecting_ the same type from which our query is derived.  However, if we wanted to find `Actor` records which starred in `Movie` records that have a specific title, we need to formulate our query at the `Movie` level, but we are _selecting_ a different node:

```java
MovieAPIHashIndex idx = new MovieAPIHashIndex("Movie", "cast.element.actor", "title.value");

String knownMovieTitle = ...;

for(Actor actor : idx.findActorMatches(knownMovieTitle)) {
    System.out.println("The actor " + actor.getName().getValue() +
                       " starred in " + knownMovieTitle);
}
```

We can also match at multiple places in a type hierarchy.  For example, if we want to find the `ActorRole` by actor id and movie title, we can use the following:

```java
MovieAPIHashIndex idx = new MovieAPIHashIndex("Movie", "cast.element",
                                              "cast.element.actor.actorId", "title.value");

String knownMovieTitle = ...;
int knownActorId = ...;

for(ActorRole role : idx.findActorRoleMatches(knownActorId, knownMovieTitle)) {
    System.out.println("The actor " + role.getActor().getName().getValue() +
                       " starred in " + knownMovieTitle +
                       " as " + role.getCharacterName().getValue());
}

```
Similarly, if we want to include an enum type like releaseCountry in the fields, then its field path in the index construction can be specified as `releaseCountry._name`. Note, in field paths, an enum type is treated slightly differently from a String reference type which is expanded using `.value`.

## Prefix Index

A prefix index is used for indexing string values to records containing them. Prefix index in hollow also supports partial matching of string values enabling quick development of features like auto-complete, spell-checkers and others. In order to create a new prefix index, use this class by providing the following arguments in the constructor:
- An instance of `HollowReadStateEngine`
- A type on which the index will record the ordinals
- A field path that leads to a string value.

For example, in order to build a prefix index of movie titles to retrieve `Movie` records, we can create the prefix index as follows:

```java

HollowPrefixIndex prefixIndex = new HollowPrefixIndex(readStateEngine, "Movie", "title.value");
HollowOrdinalIterator it = prefixIndex.findKeysWithPrefix("A");

MovieAPI movieApi = (MovieAPI) consumer.getAPI();
int ordinal = it.next();
while(ordinal != HollowOrdinalIterator.NO_MORE_ORDINAL)
    MovieHollow movieHollow = movieApi.getMovieHollow(ordinal);
    System.out.println(movieHollow.getTitle().getValue());
    ordinal = it.next();
}

```
The above code will print out all the movie titles that begin with the letter "A". Field path could be a reference to an `OBJECT`, `LIST`, or a `SET`, it has to ultimately lead to a String type.

You can also keep this index updated when a new delta blob is received on the consumer. When a new delta is available, a new prefix is built completely from scratch. While a new prefix index is being built, the current index can continue to answer queries. The implementation of the index takes care of swapping the new updated index with old one. In order to keep your index updated with delta changes, use the following:

```java
// add the index object as listener for delta updates for type "Movie".
prefixIndex.listenForDeltaUpdates();

// remove the index object as listener for delta updates for type "Movie".
prefixIndex.detachFromDeltaUpdates();

```


## Field Paths

A field path indicates how to traverse through a type hierarchy. It contains multiple parts delimited by `.`, and we need one part per type through which we're traversing. Each part corresponding to an `OBJECT` type should be equal to the name of a field in that type.

### Primary and hash keys

Primary key and hash key field paths may only span through `OBJECT` types.  These field paths will be automatically expanded if they end in a `REFERENCE` field which points to a type that has only a single field, or a type which has a primary key with only a single field defined.  If auto-expansion is not desired, the field path should terminate with a `!` character.  For example, in our data model example above, the following field paths for the type `Movie` are equivalent: `title`, `title.value`.  If we actually want the field path to terminate at the `REFERENCE` field `title`, we can specify the field path as `title!`.

### Hash indexes

Hash index field paths may span through any type.  Each part corresponding to a `LIST` or `SET` type should be specified as `element`. Similarly, each part corresponding to a `MAP` type should be specified as either `key` or `value`.  Hash index field paths are never auto-expanded.
When providing an enum type in the field path, use enum field name followed by `._name`. For example,
```
HashIndex hashIndex = HashIndex
.from(consumer, Movie.class)
.usingPath("releaseCountry._name", String.class);
```

## Hash Keys

Notice that in the POJOs of our data model defined at the beginning of this topic, we annotated the `Set<ActorRole>` in the `Movie` type with `@HollowHashKey(fields="actor.actorId")`.  This means that for each of these sets, the data will be hashed by the actor ID in the contained record.  In our generated API, we can easily find any record by actor ID using the `findElement()` method.  For example:

```java
Movie movie = ...;
int knownActorId = ...;

ActorRole role = movie.getCast().findElement(knownActorId);
```

In this way, each of our set records can be indexed by any field, or combination of fields, for O(1) retrieval of contained records.  The rules for defining a hash key are similar to the rules for defining a primary key:

* Compound hash keys may be defined by specifying multiple fields.
* Field paths may only span through `OBJECT` types.
* Field paths will be auto-expanded if they terminate in a `REFERENCE` field.
* Should be used when there is a one-to-one mapping between records and keys _per set_.  If duplicates exist, an arbitrary valid match will be returned.

If defined on a __set__ type, hash key field paths should be defined starting from the element type.

Hash keys may also be defined on __map__ record types.  When defined on a __map__ record, the field paths should be defined starting from the key type.  The methods `findKey()`, `findValue()`, and `findElement()` are available on __map__ types in the generated API for consumers to look up records by hash key values.  

If using the `HollowObjectMapper`, unspecified hash keys will be automatically selected if an element or key type contain a single non-reference field.  Addionally, if a `Set` or `Map` references `Object` elements with a defined _primary key_, then the _hash key_ will default to the _primary key_ of the element type.  Alternatively, _hash keys_ can be explicitly defined using the `@HollowHashKey` annotation in POJOs for `Set` schemas by specifying one or more fields from the element type, or for `Map` schemas by specifying one or more fields from the key type.  See our [data model example](#a-data-model) at the beginning of this section for an example.




## Field Match Scan Queries

Each of the examples above pre-index your dataset to achieve O(1) lookup times.  These are very efficient, but require pre-knowledge of what you're searching for. Given that all of hollow datasets exist in memory, for some use cases it is reasonable to scan through the entire dataset looking for matches.

The `HollowFieldMatchQuery` can be used to accommodate these use cases.  The Hollow Explorer UI, for example, uses this mechanism to provide a powerful "search-for-anything" capability with reasonable response times for low-volume query traffic.

## Diving Deeper

Lower-level interfaces are available to index data in the absence of a generated API.  See [Diving Deeper: Indexing Data for Retrieval](diving-deeper.md#indexing-data-for-retrieval) for a detailed look.
