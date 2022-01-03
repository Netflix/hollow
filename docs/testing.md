# Unit testing with Hollow objects

Hollow provides a `hollow-test` jar that contains some testing utilities to facilitate unit testing with Hollow objects.

### HollowReadStateEngineBuilder

This class allows easy creation of `HollowReadStateEngine` objects for use in unit tests. This is useful when paired with a `TestHollowConsumer` (see below), by itself while using lower-level Hollow APIs, or as a mechanism to create Hollow objects for use in test cases.

```java
HollowReadStateEngine engine = new HollowReadStateEngineBuilder()
    .add(new Movie("foo"))
    .add(new Actor("bar"))
    .build()
```

### TestHollowConsumer

A common pattern during unit or integration tests is to require an instance of a HollowConsumer that can be manipulated, similar to how test fixtures or mocks are manipulated. `hollow-test` provides a `TestHollowConsumer` class to facilitate this.

A simple example is shown below. See the unit tests in `TestHollowConsumerTest` for further examples.

```java
HollowWriteStateEngine stateEngine = new HollowWriteStateEngineBuilder()
    .add("somedata")
    .add(new MyDataModelType("somestuff", 2))
    .build();
// we will add the snapshot with a version, and make the announcementWatcher see this version
long latestVersion = 1L;
TestHollowConsumer consumer = new TestHollowConsumer.Builder()
       .withAnnouncementWatcher(new TestAnnouncementWatcher().setLatestVersion(latestVersion))
       .withBlobRetriever(new TestBlobRetriever())
       .withGeneratedAPIClass(MyApiClass.class)
       .build();
consumer.addSnapshot(latestVersion, stateEngine);
consumer.triggerRefresh();
```

### HollowTestDataAPIGenerator

Hollow Test Data API eases the creation of dummy data for testing. 

The Test API can be generated using the `HollowTestDataAPIGenerator` class by passing in a `HollowDataset` or using the data model like- 

```java
HollowTestDataAPIGenerator.generate(
    readStateEngine, // from a pre-published state or from the data model using SimpleHollowDataset.fromClassDefinitions(Movie.class) 
    "some.package.name", 
    "InputTestData",
    "/path/to/generated/sources/");
```

Then dummy data can be initialized using the generated API, for e.g.-
```java
InputTestData input = new InputTestData();
input
    .Movie() 
        .id(1L)
        .title("foo")
        .countries() // is a set 
            .String("US")
            .String("CA")
            .up() // to parent
        .tags() // is a map
            .entry(TYPE, "Movie")
            .entry(GENRE, "action");
```

And a `HollowConsumer` can be initialized with the dummy data like- 
```java
HollowConsumer consumer = input
    .newConsumerBuilder()
    .withGeneratedAPIClass(ClientAPI.class) // note this is the client API used by consumers (and not the Test API generated above)
    .build();
input.buildSnapshot(consumer);
```
