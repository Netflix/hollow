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
