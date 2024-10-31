# Data Modeling

This section describes details on schemas used in Hollow and how your data model maps to them.

## Schemas

A Hollow data model is a set of schemas, which are usually defined by the POJOs used on the producer to [populate the data](getting-started.md#producing-a-data-snapshot).  This section will use POJOs as examples, but there are other ways to define schemas -- for example you could ingest a text file and use the [schema parser](advanced-topics.md#schema-parser).

!!! note "Schemas Define the Data Model"
    A hollow dataset is comprised of one or more data _types_.  The _data model_ for a dataset is defined by the schemas describing those types.

## Object Schemas

Each POJO class you define will result in an `Object` schema, which is a fixed set of strongly typed fields.  The fields will be based on the member variables in the class.  For example, the class `Movie` will define an `Object` schema with three fields:

```java
public class Movie {
    int movieId;
    String title;
    Set<Actor> actors;
}

```

Each schema has a _type name_.  The name of the type will default to the simple name of your POJO -- in this case `Movie`.  

Each schema field has a _field name_, which will default to the same name as the field in the POJO -- in this case `movieId`, `title`, and `actors`.  Each field also has a _field type_, which is in this case `INT`, `REFERENCE`, and `REFERENCE`, respectively.  Each `REFERENCE` field also indicates the _referenced type_, which for our reference fields above default to `String` and `SetOfActor`.

The possible field types are:

* `INT`: An integer value up to 32-bits. Integer.MIN_VALUE is reserved for a sentinel value indicating null.
* `LONG`: An integer value up to 64-bits. Long.MIN_VALUE is reserved for a sentinel value indicating null.
* `FLOAT`: A 32-bit floating-point value
* `DOUBLE`: A 64-bit floating-point value
* `BOOLEAN`: `true` or `false`
* `STRING`: An array of characters
* `BYTES`: An array of bytes
* `REFERENCE`: A reference to another specific type.  The referenced type must be defined by the schema.

Notice that since the reference type is __defined by the schema__, data models must be strongly typed.  Each reference in your data model must point to a specific concrete implementation.  References to interfaces, abstract classes, or `java.lang.Object` are not supported.

### Primary Keys

`Object` schemas may specify a primary key.  This is accomplished by using the `@HollowPrimaryKey` annotation and specifying the fields.

```java
@HollowPrimaryKey(fields={"movieId"})
public class Movie {
    int movieId;
    String title;
    Set<Actor> actors;
}
```

When defined in the schema, primary keys are a part of your data model and drive useful functionality and default configuration in the [hollow explorer](tooling.md#hollow-explorer), [hollow history](tooling.md#history-tool), and [diff ui](tooling.md#diff-tool).  They also provide a shortcut when creating a [primary key index](indexing-querying.md#default-primary-keys).

Primary keys defined in the schema follow the same convention as primary keys defined for indexes.  They consist of one or more [field paths](indexing-querying.md#field-paths), which will auto-expand if they terminate in a `REFERENCE` field.

!!! warning "Null values are not supported"
	Primary key field(s) cannot have null values. This is not supported as it was not needed. Please be mindful when adding values to primary key fields. This will result in exception similar to below. 

	```java
	java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.RuntimeException: Attempting to read null value as int
	at com.netflix.hollow.core.write.HollowBlobWriter.writeSnapshot(HollowBlobWriter.java:69)
	at com.netflix.hollow.api.producer.fs.HollowFilesystemBlobStager$FilesystemBlob
	.write(HollowFilesystemBlobStager.java:117)
	```

### Inlined vs Referenced Fields

We can _inline_ some fields in our POJOs so that they are no longer `REFERENCE` fields, but instead encode their data directly in each record:

```java
public class Movie {
    int movieId;
    @HollowInline String title;
    Set<Actor> actors;
}
```
In the above example, our fields are now of type `INT`, `STRING`, and `REFERENCE`.

While modeling data, we choose whether or not to inline a field for efficiency.  Consider the following type:

```
public class Award {
    String awardName;
    long movieId;
    long actorId;
}
```

In this case, imagine `awardName` is something like “Best Supporting Actress”.  Over the years, many such awards will be given, so we’ll have a lot of records which share that value.  If we use an _inlined_ `STRING` field, then the value "Best Supporting Actress" will be repeated for every such award record.  However, if we reference a separate record type, all such awards will reference the same child record with that value.  If the `awardName` values have a lot of repetition, then this can result in a significant savings.

!!! hint "Deduplication"
    Record deduplication happens automatically at the _record_ granularity in Hollow.  Try to model your data such that when there is a lot of repetition in records, the repetitive fields are encapsulated into their own types.

To consider the opposite case, let’s examine the following `Actor` type:
```
public class Actor {
    long id;
    @HollowInline String actorName;
}
```

The `actorName` is unlikely to be repeated often.  In this case, if we reference a separate record type, we have to retain roughly the same number of unique character strings __plus__ we need to retain references to those records.  In this case, we end up saving space by using an inlined `STRING` field instead of a reference to a separate type.

!!! warning "Reference Costs"
    A `REFERENCE` field isn't free, and therefore we shouldn't necessarily try to encapsulate fields inside their own record types where we won't benefit from deduplication.  These fields should instead be _inlined_.

We refer to fields which are defined with native Hollow types as _inlined_ fields, and fields which are defined as references to types with a single field as _referenced_ fields.

### Namespaced Record Type Names

In order to be very efficient, referenced types sometimes should be _namespaced_ so that fields with like values may reference the same _record type_, but reference fields of the same _primitive type_ elsewhere in the data model use different _record types_.  For example, consider our `Award` type again, but this time, we’ll reference a type called `AwardName`, instead of `String`.  We can explicitly name the _type_ of a field with the `@HollowTypeName` annotation:
```
public class Award {
    @HollowTypeName(name="AwardName")
    String awardName;
    long movieId;
    long actorId;
}

```

Other types in our data model which reference award names can reuse the `AwardName` type.  Other referenced string fields in our data model, which are unrelated to award names, should use different types corresponding to the semantics of their values.  

Namespacing fields saves space because references to types with a lower cardinality use fewer bits than references to types with a higher cardinality.  The reason for this can be gleaned from the [In-Memory Data Layout](advanced-topics.md#in-memory-data-layout) topic underneath the [Advanced Topics](advanced-topics.md) section.

Namespacing fields is also useful if some consumers don't need the contents of a specific referenced field.  If a type is namespaced, it can be selectively [filtered](tooling.md#filtering), whereas if it is grouped with other fields which _are_ needed by all consumers, then it cannot be selected for filtering.

!!! note "Namespacing Reduces Reference Costs"
    Using an appropriately _namespaced_ type reduces the heap footprint cost of `REFERENCE` fields.

!!! hint "Changing default _type names_"
    The `@HollowTypeName` annotation can also be used at the class level to select a default type name for a class other than its simple name. Custom type names should begin with an upper case character to avoid ambiguity in naming in the generated API, although this is not enforced by Hollow due to backwards compatibility reasons.

### Grouping Associated Fields

Referencing fields can save space because the same field values do not have to be repeated for every record in which they occur.  Similarly, we can _group_ fields which have covarying values, and pull these out from larger objects as their own types.  For example, imagine we started with a `Movie` type which included the following fields:
```
public class Movie {
    long id;
    String title;
    String maturityRating;
    String advisories;
}
```

We might notice that the `maturityRating` and `advisories` fields vary together, and are often the repeated across many `Movie` records.  We can pull out a separate type for these fields:
```
public class Movie {
    long id;
    String title;
    MaturityRating maturityRating;
}

public class MaturityRating {
    string rating;
    string advisories;
}
```

We could have referenced these fields separately.  If we had done so, each `Movie` record, of which there are probably many, would have had to contain two separate references for these fields.  Instead, by recognizing that these fields were associated and pulling them together, space is saved because each `Movie` record now only contains one reference for this data.

### Transient Fields

A transient field is ignored and will not be included in an `Object` Schema.  A transient field is a field declared
with the `transient` Java keyword or annotated with the `@HollowTransient` annotation.  The latter may be used for
cases when the use of the `transient` Java keyword has unwanted side-effects, such as when POJOs defining the data 
model are also consumed by tools, other than Hollow, for which the field is not transient.

## List Schemas

You can define `List` schemas by adding a member variable of type `List` in your data model.  For example:

```java
public class Movie {
    long id;
    String title;
    List<Award> awardsReceived;
}

```

The `List` must explicitly define its parameterized element type.  The default _type name_ of the above `List` schema will be `ListOfAward`.  

A `List` schema indicates a record type which is an ordered collection of `REFERENCE` fields.  Each record will have a variable number of references.  The referenced type must be defined by the schema, and all references in all records will encode only the _ordinals_ of the referenced records as the values for these references.

## Set Schemas

You can define `Set` schemas by adding a member variable of type `Set` in your data model.  The `Set` must explicitly define its parameterized element type.  For example:

```java
public class Movie {
    long id;
    String title;

    @HollowHashKey(fields={"firstName", "lastName"}) /// hash key is optional
    Set<Actor> cast;
}
```

A `Set` schema indicates a record type which is an unordered collection of `REFERENCE` fields.  Each record will have a variable number of references, and the referenced type must be defined by the schema.  Within a single set record, each reference must be unique.  

References in `Set` records can be hashed by some specific element fields for O(1) retrieval.  In order to enable this feature, a `Set` schema will define an optional _hash key_, which defines how its elements are hashed/indexed.

## Map Schemas

You can define `Map` schemas by adding a member variable of type `Map` in your data model.  The `Map` must explicitly define it parameterized key and values types.  For example:

```java
public class Movie {
    long id;
    String title;

    @HollowHashKey(fields="actorId") /// hash key is optional
    Map<Actor, Role> cast;
}
```

A `Map` schema indicates a record type which is an unordered collection of pairs of `REFERENCE` fields, used to represent a key/value mapping.  Each record will have a variable number of key/value pairs.  Both the key reference type and the value reference type must be defined by the schema.  The key reference type does not have to be the same as the value reference type.  Within a single map record, each key reference must be unique.  

Entries in `Map` records can be hashed by some specific key fields for O(1) retrieval of the keys, values, and/or entries.  In order to enable this feature, a `Map` schema will define an optional _hash key_, which defines how its entries are hashed/indexed.

## Hash Keys

Each `Map` and `Set` schema may optionally define a _hash key_.  A _hash key_ specifies one or more user-defined fields used to hash entries into the collection.  When a hash key is defined on a `Set`, each set record becomes like a primary key index; records in the set can be efficiently retrieved by matching the specified _hash key_ fields.  Similarly, when a hash key is defined on a `Map`, each map record becomes like an index over the keys in the key/value pairs contained in the map record.

See [Hash Keys](indexing-querying.md#hash-keys) for a detailed discussion of hash keys.


## Circular References

Circular references are not allowed in Hollow.  A type may not reference itself, either directly or transitively.

## Object Memory Layout

On consumers, `INT` and `LONG` fields are each represented by a number of bits exactly sufficient to represent the maximum value for the field across all records.  `FLOAT`, `DOUBLE`, and `BOOLEAN` fields are represented by 32, 64, and 2 bits, respectively.  `STRING` and `BYTES` fields use a variable number of bytes for each record.  `REFERENCE` fields encode the _ordinal_ of referenced records, and are represented by a number of bits exactly sufficient to encode the maximum ordinal of the referenced type.  See [In-memory Data Layout](advanced-topics.md#in-memory-data-layout) for more details.

!!! hint "Avoid Outlier Values"
    Try to model your data such that there aren't any outlier values for `INT` and `LONG` fields.  Also, avoid `FLOAT` and `DOUBLE` fields where possible, since these field types are relatively expensive.



## Maintaining Backwards Compatibility

A data model will evolve over time.  The following operations will not impact the interoperability between existing clients and new data:

* Adding a new type
* Removing an existing type
* Adding a new field to an existing type
* Removing an existing field from an existing type.

When adding new fields or types, existing generated client APIs will ignore the new fields, and all of the fields which existed at the time of API generation will still be visible using the same methods.  When removing fields, existing generated client APIs will see null values if the methods corresponding to the removed fields are called.  When removing types, existing generated client APIs will see removed types as having no records.

It is not backwards compatible to change the type of an existing field.  The client behavior when calling a method corresponding to a field with a changed type is undefined.

It is not backwards compatible to change the primary key or hash key for any type.

Beyond the specification of Hollow itself, backwards compatibility often has a lot to do with the use case and semantics of the data. Hollow will always behave in the stated way for evolving data models, but it’s possible that consumers require a field which starts returning null once it gets removed.  For this reason, additional caution should be exercised when removing types and fields.

!!! hint "Backwards-incompatible data remodeling"
    Every so often, it may be required or desirable to make changes to the data model which are incompatible with prior versions.  In this case, an older producer, which produces the older data model, should run in parallel with the newer producer, producing the newer incompatible data model.  Each producer should write its blobs to a different [namespace](infrastructure.md#blob-namespaces), so that older consumers can read from the old data model, and newer consumers can read from the newer data model.  Once all consumers are upgraded and reading from the newer data model, the older producer can be decommissioned.
