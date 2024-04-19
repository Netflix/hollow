package com.netflix.hollow.core.write;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MissingHashKeyInWriteStateTest {
  @Test
  public void shouldSucceedLikeNormalIfEverythingIsWritten_setWithHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithHashKeySet.class);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(dataset.getSchemas().toArray(new HollowSchema[0]));

    long v1 = p1.runCycle(ws -> {
      Set<Actor> actors = new HashSet<>();
      actors.add(new Actor(1, "actor1"));
      MovieWithHashKeySet movie = new MovieWithHashKeySet(1, "title1", actors);
      ws.add(movie);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("SetOfActor").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("Actor").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldSucceedLikeNormalIfEverythingIsWritten_setWithoutHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithoutHashKeySet.class);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(dataset.getSchemas().toArray(new HollowSchema[0]));

    long v1 = p1.runCycle(ws -> {
      Set<Actor> actors = new HashSet<>();
      actors.add(new Actor(1, "actor1"));
      MovieWithoutHashKeySet movie = new MovieWithoutHashKeySet(1, "title1", actors);
      ws.add(movie);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("SetOfActor").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("Actor").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldNotFailProducerCycleIfSetIsNotPopulated_withHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithHashKeySet.class);

    HollowSchema[] schemasWithoutActor =
        dataset.getSchemas()
            .stream()
            .filter(schema -> !schema.getName().equals("Actor") && !schema.getName().equals("ActorName"))
            .toArray(HollowSchema[]::new);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(schemasWithoutActor);

    long v1 = p1.runCycle(ws -> {
      HollowWriteStateEngine stateEngine = ws.getStateEngine();
      HollowObjectWriteRecord titleRec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("MovieTitle"));
      titleRec.setString("value", "title1");
      int titleOrdinal = stateEngine.add("MovieTitle", titleRec);

      HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("Movie"));
      rec.setInt("id", 1);
      rec.setReference("title", titleOrdinal);
      rec.setNull("actors");
      stateEngine.add("Movie", rec);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(0, consumer.getStateEngine().getTypeState("SetOfActor").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldNotFailProducerCycleIfSetIsNotPopulated_withoutHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithoutHashKeySet.class);

    HollowSchema[] schemasWithoutActor =
        dataset.getSchemas()
            .stream()
            .filter(schema -> !schema.getName().equals("Actor") && !schema.getName().equals("ActorName"))
            .toArray(HollowSchema[]::new);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(schemasWithoutActor);

    long v1 = p1.runCycle(ws -> {
      HollowWriteStateEngine stateEngine = ws.getStateEngine();
      HollowObjectWriteRecord titleRec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("MovieTitle"));
      titleRec.setString("value", "title1");
      int titleOrdinal = stateEngine.add("MovieTitle", titleRec);

      HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("Movie"));
      rec.setInt("id", 1);
      rec.setReference("title", titleOrdinal);
      rec.setNull("actors");
      stateEngine.add("Movie", rec);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(0, consumer.getStateEngine().getTypeState("SetOfActor").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldSucceedLikeNormalIfEverythingIsWritten_mapWithHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithHashKeyMap.class);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(dataset.getSchemas().toArray(new HollowSchema[0]));

    long v1 = p1.runCycle(ws -> {
      Map<Actor, Award> awardMap = new HashMap<>();
        awardMap.put(new Actor(1, "actor1"), new Award(1, "award1"));
      MovieWithHashKeyMap movie = new MovieWithHashKeyMap(1, "title1", awardMap);
      ws.add(movie);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MapOfActorToAward").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("Actor").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("Award").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldSucceedLikeNormalIfEverythingIsWritten_mapWithoutHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithoutHashKeyMap.class);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(dataset.getSchemas().toArray(new HollowSchema[0]));

    long v1 = p1.runCycle(ws -> {
      Map<Actor, Award> awardMap = new HashMap<>();
      awardMap.put(new Actor(1, "actor1"), new Award(1, "award1"));
      MovieWithoutHashKeyMap movie = new MovieWithoutHashKeyMap(1, "title1", awardMap);
      ws.add(movie);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MapOfActorToAward").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("Actor").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("Award").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldNotFailProducerCycleIfMapIsNotPopulated_withHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithHashKeyMap.class);

    HollowSchema[] schemasWithoutActorOrAward =
        dataset.getSchemas()
            .stream()
            .filter(schema ->
                !schema.getName().equals("Actor") &&
                    !schema.getName().equals("ActorName") &&
                    !schema.getName().equals("Award") &&
                    !schema.getName().equals("AwardName"))
            .toArray(HollowSchema[]::new);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(schemasWithoutActorOrAward);

    long v1 = p1.runCycle(ws -> {
      HollowWriteStateEngine stateEngine = ws.getStateEngine();
      HollowObjectWriteRecord titleRec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("MovieTitle"));
      titleRec.setString("value", "title1");
      int titleOrdinal = stateEngine.add("MovieTitle", titleRec);

      HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("Movie"));
      rec.setInt("id", 1);
      rec.setReference("title", titleOrdinal);
      rec.setNull("awardMap");
      stateEngine.add("Movie", rec);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(0, consumer.getStateEngine().getTypeState("MapOfActorToAward").getPopulatedOrdinals().cardinality());
  }

  @Test
  public void shouldNotFailProducerCycleIfMapIsNotPopulated_withoutHashKey() {
    HollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(MovieWithoutHashKeyMap.class);
    HollowSchema[] schemasWithoutActorOrAward =
        dataset.getSchemas()
            .stream()
            .filter(schema ->
                !schema.getName().equals("Actor") &&
                    !schema.getName().equals("ActorName") &&
                    !schema.getName().equals("Award") &&
                    !schema.getName().equals("AwardName"))
            .toArray(HollowSchema[]::new);

    InMemoryBlobStore blobStore = new InMemoryBlobStore();
    HollowInMemoryBlobStager blobStager = new HollowInMemoryBlobStager();

    HollowProducer p1 = HollowProducer.withPublisher(blobStore).withBlobStager(blobStager).build();
    p1.initializeDataModel(schemasWithoutActorOrAward);

    long v1 = p1.runCycle(ws -> {
      HollowWriteStateEngine stateEngine = ws.getStateEngine();
      HollowObjectWriteRecord titleRec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("MovieTitle"));
      titleRec.setString("value", "title1");
      int titleOrdinal = stateEngine.add("MovieTitle", titleRec);

      HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) dataset.getSchema("Movie"));
      rec.setInt("id", 1);
      rec.setReference("title", titleOrdinal);
      rec.setNull("awardMap");
      stateEngine.add("Movie", rec);
    });

    HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
        .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
          @Override
          public boolean allowDoubleSnapshot() {
            return false;
          }
          @Override
          public int maxDeltasBeforeDoubleSnapshot() {
            return Integer.MAX_VALUE;
          }
        })
        .build();
    consumer.triggerRefreshTo(v1);

    assertEquals(v1, consumer.getCurrentVersionId());
    assertEquals(1, consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals().cardinality());
    assertEquals(1, consumer.getStateEngine().getTypeState("MovieTitle").getPopulatedOrdinals().cardinality());
    assertEquals(0, consumer.getStateEngine().getTypeState("MapOfActorToAward").getPopulatedOrdinals().cardinality());
  }

  @HollowTypeName(name="Movie")
  @HollowPrimaryKey(fields="id")
  private static class MovieWithoutHashKeySet {
    int id;
    @HollowTypeName(name="MovieTitle")
    String title;
    Set<Actor> actors;

    private MovieWithoutHashKeySet(int id, String title, Set<Actor> actors) {
      this.id = id;
      this.title = title;
      this.actors = actors;
    }
  }

  @HollowTypeName(name="Movie")
  @HollowPrimaryKey(fields="id")
  private static class MovieWithHashKeySet {
    int id;
    @HollowTypeName(name="MovieTitle")
    String title;
    @HollowHashKey(fields="id")
    Set<Actor> actors;

    private MovieWithHashKeySet(int id, String title, Set<Actor> actors) {
      this.id = id;
      this.title = title;
      this.actors = actors;
    }
  }

  @HollowTypeName(name="Movie")
  @HollowPrimaryKey(fields="id")
  private static class MovieWithoutHashKeyMap {
    int id;
    @HollowTypeName(name="MovieTitle")
    String title;
    Map<Actor, Award> awardMap;

    private MovieWithoutHashKeyMap(int id, String title, Map<Actor, Award> awardMap) {
      this.id = id;
      this.title = title;
      this.awardMap = awardMap;
    }
  }

  @HollowTypeName(name="Movie")
  @HollowPrimaryKey(fields="id")
  private static class MovieWithHashKeyMap {
    int id;
    @HollowTypeName(name="MovieTitle")
    String title;
    @HollowHashKey(fields="id")
    Map<Actor, Award> awardMap;

    private MovieWithHashKeyMap(int id, String title, Map<Actor, Award> awardMap) {
      this.id = id;
      this.title = title;
      this.awardMap = awardMap;
    }
  }

  private static class Actor {
    int id;
    @HollowTypeName(name="ActorName")
    String name;

    private Actor(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  private static class Award {
    int id;
    @HollowTypeName(name="AwardName")
    String name;

    private Award(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }
}
