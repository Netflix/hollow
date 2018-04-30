_adjacent state_

> If state `A` is connected via a single delta to state `B`, then `A` and `B` are adjacent to each other.

_announce_

> After the blobs for a state have been published to a blob store by a producer, the state must be _announced_ to consumers.  The announcement signals to consumers that they should transition to the announced state.

_blob_

> A blob is a file used by consumers to update their dataset.  A blob will be either a snapshot, delta, or reverse delta

_blob store_

> A blob store is a file store to which blobs can be published by a producer and retrieved by a consumer. 

_broken delta chain_

> When a blob namespace contains a state which is not adjacent to any prior states, the delta chain is said to be broken.  In this scenario, consumers may need to load a double snapshot.

_consumer_

> One of many machines on which a dataset is made accessible.  Consumers are updated in lock-step based on the actions of the producer.

_cycle_

> A producer runs in an infinite loop.  Each exection of the loop is called a cycle.  Each cycle produces a single data state.

_data model_

> A data model defines the structure of a dataset.  It is specified with a set of schemas.

_data state_

> A dataset changes over time.  The timeline for a changing dataset can be broken down into discrete data states, each of which is a complete snapshot of the data at a particular point in time.

_deduplication_

> Two records which have identical data in Hollow will be consolidated into a single record.  Any references to duplicate records will be mapped to the canonical one when a dataset is represented with Hollow.

_delta_

> A set of encoded instructions to transition from one data state to an adjacent state.  Deltas are encoded as a set of ordinals to remove and a set of ordinals to add, along with the accompanying data to add.  'Delta' may refer specifically to a transition between an earlier state and a later state, contrasted with 'reverse delta', which specifically refers to a transition between a later state and an earlier state.

_delta chain_

> A series of states which are all connected via contiguous deltas.

_diff_

> A comprehensive accounting for the differences between two data states.

_double snapshot_

> When a consumer already has an initialized state and an announcement signals to move to a new state for which a path of deltas is not available, the consumer may transition to that state via a snapshot.  In this scenario two full copies of the dataset must be loaded in memory.

_field_

> A single value encoded inside of a Hollow record.

_hash key_

> A user-defined specification of one or more fields used to hash elements into a set or entries into a map.

_ingestion_

> Gathering data from a source of truth and importing it into Hollow.

_inline_

> A field for which the value is encoded directly into a record, as opposed to referenced via another record.

_namespace (blobs)_

> An addressable, logical separation of both published artifacts in a blob store and announcement location.  Used to allow multiple publishers to communicate on separate channels to specific groups of consumers.

_namespace (references)_

> The deliberate creation of a type to hold a specific referenced field's data in order to reduce the cardinality of the referenced records.

_object longevity_

> A technique used to ensure that stale references to Hollow Objects always return the same data they did initially upon creation.  Configured via the `HollowObjectMemoryConfig`.

_ordinal_

> An integer value uniquely identifying a record within a type.  Because records are represented with a fixed-length number of bits, the only necessary information to locate a record in memory is the record's type and ordinal.  Ordinals are automatically assigned by Hollow, and are recycled as records are removed and added.  Consequently, they lie in the range of 0-n, where n is generally not much larger than the total number of records for the type.

_patch (states)_

> Creating a series of two deltas between states in a delta chain.

_pinning_

> Overriding the state version announcement from the producer, to force clients to go back to or stay at an older state.

_primary key_

> A user-defined specification of one or more fields used to uniquely identify a record within a type.

_producer_

> A single machine that retrieves all data from a source of truth and produces a delta chain.

_publish_

> Writing blobs to a blob store.

_read state engine_

> A `HollowReadStateEngine`, the root handle to a Hollow dataset as a consumer.

_record_

> A strongly-typed collection of fields or references, the structure of which is specified by a schema.

_reference_

> A field type which indicates a pointer to another field.  Can also refer to the technique of pulling out a specific field into a record type of its own to deliberately allow Hollow to deduplicate the values.

_restore_

> Initializing a `HollowWriteStateEngine` with data from a previously produced state so that a delta may be created during a producer's first cycle.

_reverse delta_

> A delta from a later state to an earlier state.  Generally used during pinning scenarios.

_schema_

> Metadata about a Hollow type which defines the structure of the records.

_snapshot_

> A blob type which contains a serialization of all of the records in a type.  Consumed during initialization, and possibly in a broken delta chain scenario.

_state_

> See _data state_.

_state version_

> A unique identifier for a state.  Should by monotonically increasing as time passes.

_state engine_

> Both the producer and consumers handle datasets with a state engine.  A state engine can be transitioned between data states.  A producer uses a _write state engine_ and a consumer uses a _read state engine_

_type_

> A collection of records all conforming to a specific schema.

_write state engine_

> A `HollowWriteStateEngine`, the root handle to a Hollow dataset as a consumer.












