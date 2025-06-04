# Validation

Hollow supports user-defined validation of data state.
Validation can ensure that bad data does not propagate to consumers.

Validation occurs after data state is published (as blobs) and before the availability of 
the data state is announced, thereby allowing inspection of erroneous but published data state.

## Implementing and registering a validator

A validator is created by implementing the interface `com.netflix.hollow.api.producer.validation.ValidatorListener`. 

The following is an example validator that checks if there are two or more objects that 
have the same primary key:


```java
public static class DuplicateValidator implements ValidatorListener {

    private final String dataTypeName;

    public DuplicateDataDetectionValidator(String dataTypeName) {
        this.dataTypeName = Objects.requireNonNull(dataTypeName);
    }

    @Override
    public String getName() {
        return this.getClass().getName() + ":" + dataTypeName;
    }

    @Override
    public ValidationResult onValidate(HollowProducer.ReadState dataState) {
        ValidationResult.ValidationResultBuilder vrb = ValidationResult.from(this);
        vrb.detail("Type", dataTypeName);

        HollowSchema schema = dataState.getStateEngine().getSchema(dataTypeName);
        if (schema.getSchemaType() != HollowSchema.SchemaType.OBJECT) {
            return vrb.failed("Bad configuration: data type is not an Object");
        }

        HollowObjectSchema oSchema = (HollowObjectSchema) schema;
        PrimaryKey primaryKey = oSchema.getPrimaryKey();
        if (primaryKey == null) {
            return vrb.failed("Bad configuration: data type does not have a primary key");
        }
        vrb.detail("PrimaryKey", primaryKey);

        Collection<Object[]> duplicateKeys = getDuplicateKeys(dataState.getStateEngine(), primaryKey);
        if (!duplicateKeys.isEmpty()) {
            return vrb
                    .detail("Objects",
                            duplicateKeys.stream().map(Arrays::toString).collect(Collectors.joining(",")))
                    .failed("Duplicate objects with the same primary key");
        }

        return vrb.passed();
    }

    private Collection<Object[]> getDuplicateKeys(HollowReadStateEngine stateEngine, PrimaryKey primaryKey) {
        ...
    }
}

```
The following highlights some important aspects (without going into the specific details of how 
this validator detects duplicate objects with the same primary key):

- A validator has state, in this case the name of data type of object instances it is checking.  
The name of the validator, returned from the call to `getName`, should include pertinent state to 
differentiate between two or more registered instances of the same validator class.

- When validation is to be performed the producer will emit a validate event by calling 
the `onValidate` method, with the data state, for all registered validators.

- A validator builds and returns a `ValidationResult` reporting the name of the validator and
whether validation passed or failed.
Details may be included to provide additional information, such as if the duplicate objects
to help resolve the problem of the bad data.

- If the validator throws an unexpected runtime exception then it is as if a 
`ValidationResult` is returned reporting error with that exception.

The example validator presented above may be registered when building a `HollowProducer`:

```java
HollowProducer producer = HollowProducer.withPublisher(publisher)
                                        .withAnnouncer(announcer)
                                        .withValidator(new DuplicateValidator("Movie"))
                                        .build();

producer.runCycle(state -> {
    for(Movie movie : movies)
        state.add(movie);
});
```


## Pre-defined validators

It is not necessarily easy to write a validator operating on the data state using an 
instance of `ReadState`.  To make it easier for developers Hollow provides a few
pre-defined validators for common use cases:

1. A duplicate object validator (similar to the example above), `DuplicateDataDetectionValidator`.
2. A record cound variance validator, `RecordCountVarianceValidator`.  
This validator can be configured to check if cardinality of objects varies within a required 
percentage.  This is useful to detect if the number of objects of a particular data type 
unexpectedly decreases or increases.
3. An object modification validator, `ObjectModificationValidator`.
This validator can be configured to compare the state of objects (with the same primary key)
that have been modified (not added or removed).
4. A minimum record count validator, `MinimumRecordCountValidator`.
This validator can be configured to guard against large deletions, by setting a threshold for 
allowable minimum number of records in a data type.


## Using the generated object API

Ordinarily the generated object API is utilized by consumers, but there is no inherent reason
why it cannot also be utilized by the producer.  This can make it significantly easier to write
a custom validator.  For example, the `MovieAPI` may be created from data state and all movies
traversed as follows:

```java
public static class MyValidator implements ValidatorListener {

    @Override
    public String getName() { ... }

    @Override
    public ValidationResult onValidate(HollowProducer.ReadState dataState) {
        MovieAPI api = new MovieAPI(dataState);
        
        for (Movie m : api.getAllMovie()) {
            ...
        }
    }
}

```

The `ObjectModificationValidator` is designed to be used with a generated object API.

## Listening to events emitted by the producer for other stages

A registered validator, implementing `ValidatorListener`, may also receive events for other 
stages by implementing other listener interfaces.  For example implementing the `CycleListener` 
will enable the validator to receive events for when a production cycle is started and completed.
Receiving an event when the cycle starts (a call to the `onCycleStart`) method may enable
the validator to load validator configuration state dynamically and freeze that state for the 
duration of the cycle.


## Integrity checking

Hollow additionally supports a special form of validation, integrity checking of data state, 
that occurs before the user-defined validation of data state.  

The integrity checking ensures that the data state is not corrupted, perhaps due to a bug.
Failure is likely rare but an important safety check to ensure corrupt data is not propagated
to consumers.
