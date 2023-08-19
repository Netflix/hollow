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
    dataset,
    "some.package.name", 
    "InputTestData",
    "/path/to/generated/sources");
```
where `dataset` could be a snapshot published by a prior producer run if available, or it could be initialized from the 
data model classes by doing `SimpleHollowDataset.fromClassDefinitions(Movie.class)` where `Movie.class` is the 
top-level Class in the data model. In this example an instance of the generated class `InputTestData` can then be used 
to construct a state for the consumer with dummy data for the test for e.g.-
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

And a `HollowConsumer` can be initialized with the data state like-
```java
HollowConsumer consumer = input
    .newConsumerBuilder()
    .withGeneratedAPIClass(ClientAPI.class) // the client API used to access data in a Hollow Consumer and not the test data generator API that was generated in this example
    .build();
input.buildSnapshot(consumer);
```

### Fake dataset
A pre-generated fake dataset is available for download publicly in an AWS S3 bucket. 
Alternatively, the code for generating that dataset is available under the `hollow-fakedata` module of this repo and it 
can be run locally to produce a fake dataset for testing. There are some instructions in the README. 

For reusing the pre-generated fake dataset, download from S3 to your local machine by installing the aws cli and running this command:
```
aws s3 cp s3://hollow-oss-public/fakedata /tmp/download --recursive  --no-sign-request
```
This will download around 500MB of data comprising a 100 version delta chain for a fake Book catalog. 
These Hollow snapshot and delta blobs can be consumed in your test using the various Hollow APIs available, for e.g. 
this code can consume those blobs and run the hollow history server over that delta chain: 
```java
@Test
public void fakeHistory() throws Exception {
    String path = "/tmp/fakedata";  // path to downloaded blobs
    HollowConsumer c = HollowConsumer
        .withBlobRetriever(new HollowFilesystemBlobRetriever(Paths.get(path)))
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
            @Override
            public boolean allowDoubleSnapshot() {
                    return false;   // disable double snapshots to get history for each state in delta chain
                }
            @Override                
            public int maxDeltasBeforeDoubleSnapshot() { 
                    return Integer.MAX_VALUE;
                }
            })
        .build();
    c.triggerRefreshTo(20230808144752001l);     // first version in the delta chain
    HollowHistoryUIServer historyUIServer = new HollowHistoryUIServer(c, 7001);
    historyUIServer.start();
    System.out.println("History server started at http://localhost:7001/"); // open in browser to see history of changes

    c.triggerRefreshTo(20230808145930100l);     // 100th version in delta chain, apply delta transitions and computing history for each state 
    
    historyUIServer.join(); // block forever
}
```
