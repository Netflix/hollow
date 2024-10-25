package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FakeHollowSchemaIdentifierMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.test.dto.kitchensink.KitchenSink;
import com.netflix.hollow.test.dto.movie.Award;
import com.netflix.hollow.test.dto.movie.CastMember;
import com.netflix.hollow.test.dto.movie.CastRole;
import com.netflix.hollow.test.dto.movie.Country;
import com.netflix.hollow.test.dto.movie.MaturityRating;
import com.netflix.hollow.test.dto.movie.Movie;
import com.netflix.hollow.test.dto.movie.Tag;
import com.netflix.hollow.test.dto.movie.TagValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public class FlatRecordTraversalNodeTest {
  @Test
  public void testWalkFlatRecordThroughTheNodes() {
    FlatRecord flatRecord = createMovieFlatRecord();
    FlatRecordTraversalObjectNode node = new FlatRecordTraversalObjectNode(flatRecord);

    // primitives
    assertThat(node.getFieldValue("id")).isEqualTo(1);
    assertThat(node.getFieldValue("releaseYear")).isEqualTo(2020);

    // title
    FlatRecordTraversalObjectNode titleNode = (FlatRecordTraversalObjectNode) node.getFieldNode("title");
    assertThat(titleNode.getFieldValue("value")).isEqualTo("Movie1");

    // primaryGenre
    FlatRecordTraversalObjectNode primaryGenreNode = (FlatRecordTraversalObjectNode) node.getFieldNode("primaryGenre");
    assertThat(primaryGenreNode.getFieldValue("value")).isEqualTo("action");

    // maturityRating
    FlatRecordTraversalObjectNode maturityRatingNode = (FlatRecordTraversalObjectNode) node.getFieldNode("maturityRating");
    FlatRecordTraversalObjectNode maturityRatingNodeRating = (FlatRecordTraversalObjectNode) maturityRatingNode.getFieldNode("rating");
    assertThat(maturityRatingNodeRating.getFieldValue("value")).isEqualTo("PG");
    FlatRecordTraversalObjectNode maturityRatingNodeAdvisory = (FlatRecordTraversalObjectNode) maturityRatingNode.getFieldNode("advisory");
    assertThat(maturityRatingNodeAdvisory.getFieldValue("value")).isEqualTo("Some advisory");

    // countries
    FlatRecordTraversalSetNode countriesNode = (FlatRecordTraversalSetNode) node.getFieldNode("countries");
    assertThat(countriesNode)
        .extracting(n -> ((FlatRecordTraversalObjectNode) n).getFieldValue("value"))
        .containsExactlyInAnyOrder("US", "CA");

    // tags
    FlatRecordTraversalMapNode tagsNode = (FlatRecordTraversalMapNode) node.getFieldNode("tags");
    assertThat(tagsNode).hasSize(2);
    assertThat(tagsNode.entrySet())
        .extracting(
            entry -> ((FlatRecordTraversalObjectNode) entry.getKey()).getFieldValue("value"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValue("value"))
        .containsExactlyInAnyOrder(
            tuple("Type", "Movie"),
            tuple("Genre", "action"));

    // cast
    FlatRecordTraversalSetNode castNode = (FlatRecordTraversalSetNode) node.getFieldNode("cast");
    assertThat(castNode)
        .extracting(
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValue("id"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValue("name"),
            n -> {
              FlatRecordTraversalObjectNode elementNode = (FlatRecordTraversalObjectNode) n;
              FlatRecordTraversalObjectNode roleNode = (FlatRecordTraversalObjectNode) elementNode.getFieldNode("role");
              return roleNode.getFieldValue("_name");
            })
        .containsExactlyInAnyOrder(
            tuple(1, "Benedict Cumberbatch", "ACTOR"),
            tuple(2, "Martin Freeman", "ACTOR"),
            tuple(2, "Quentin Tarantino", "DIRECTOR")
        );

    // awardsReceived
    FlatRecordTraversalListNode awardsReceivedNode = (FlatRecordTraversalListNode) node.getFieldNode("awardsReceived");
    assertThat(awardsReceivedNode)
        .extracting(
            n -> {
              FlatRecordTraversalObjectNode awardNode = (FlatRecordTraversalObjectNode) n;
              FlatRecordTraversalObjectNode awardNameNode = (FlatRecordTraversalObjectNode) awardNode.getFieldNode("name");
              return awardNameNode.getFieldValue("value");
            },
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValue("year"))
        .containsExactlyInAnyOrder(
            tuple("Oscar", 2020),
            tuple("Golden Globe", 2025)
        );
  }

  @Test
  public void testWalkFlatRecordUsingSchemaHints() {
    FlatRecord flatRecord = createMovieFlatRecord();
    FlatRecordTraversalObjectNode node = new FlatRecordTraversalObjectNode(flatRecord);

    // primitives
    assertThat(node.getFieldValueInt("id")).isEqualTo(1);
    assertThat(node.getFieldValueIntBoxed("id")).isEqualTo(1);
    assertThat(node.getFieldValueInt("releaseYear")).isEqualTo(2020);
    assertThat(node.getFieldValueIntBoxed("releaseYear")).isEqualTo(2020);

    // title
    FlatRecordTraversalObjectNode titleNode = node.getObjectFieldNode("title");
    assertThat(titleNode.getFieldValueString("value")).isEqualTo("Movie1");

    // primaryGenre
    FlatRecordTraversalObjectNode primaryGenreNode = node.getObjectFieldNode("primaryGenre");
    assertThat(primaryGenreNode.getFieldValueString("value")).isEqualTo("action");

    // maturityRating
    FlatRecordTraversalObjectNode maturityRatingNode = node.getObjectFieldNode("maturityRating");
    FlatRecordTraversalObjectNode maturityRatingNodeRating = maturityRatingNode.getObjectFieldNode("rating");
    assertThat(maturityRatingNodeRating.getFieldValueString("value")).isEqualTo("PG");
    FlatRecordTraversalObjectNode maturityRatingNodeAdvisory = maturityRatingNode.getObjectFieldNode("advisory");
    assertThat(maturityRatingNodeAdvisory.getFieldValueString("value")).isEqualTo("Some advisory");

    // countries
    FlatRecordTraversalSetNode countriesNode = node.getSetFieldNode("countries");
    Iterable<FlatRecordTraversalObjectNode> countryNodes = countriesNode::objects;
    assertThat(countryNodes)
        .extracting(n -> n.getFieldValueString("value"))
        .containsExactlyInAnyOrder("US", "CA");

    // tags
    FlatRecordTraversalMapNode tagsNode = node.getMapFieldNode("tags");
    assertThat(tagsNode).hasSize(2);
    Iterable<Map.Entry<FlatRecordTraversalObjectNode, FlatRecordTraversalObjectNode>> tagNodes = tagsNode::entrySetIterator;
    assertThat(tagNodes)
        .extracting(
            entry -> entry.getKey().getFieldValue("value"),
            entry -> entry.getValue().getFieldValue("value"))
        .containsExactlyInAnyOrder(
            tuple("Type", "Movie"),
            tuple("Genre", "action"));

    // cast
    FlatRecordTraversalSetNode castNode = node.getSetFieldNode("cast");
    Iterable<FlatRecordTraversalObjectNode> castNodes = castNode::objects;
    assertThat(castNodes)
        .extracting(
            n -> n.getFieldValueInt("id"),
            n -> n.getFieldValueString("name"),
            n -> n.getObjectFieldNode("role").getFieldValueString("_name"))
        .containsExactlyInAnyOrder(
            tuple(1, "Benedict Cumberbatch", "ACTOR"),
            tuple(2, "Martin Freeman", "ACTOR"),
            tuple(2, "Quentin Tarantino", "DIRECTOR")
        );

    // awardsReceived
    FlatRecordTraversalListNode awardsReceivedNode = node.getListFieldNode("awardsReceived");
    assertThat(awardsReceivedNode.getObject(0))
        .extracting(
            n -> n.getObjectFieldNode("name").getFieldValueString("value"),
            n -> n.getFieldValueInt("year"))
        .containsExactly("Oscar", 2020);
    assertThat(awardsReceivedNode.getObject(1))
        .extracting(
            n -> n.getObjectFieldNode("name").getFieldValueString("value"),
            n -> n.getFieldValueInt("year"))
        .containsExactly("Golden Globe", 2025);
  }

  @Test
  public void testWalkFlatRecordThroughTheNodesWithNulls() {
    FlatRecord flatRecord = createMovieFlatRecord();
    FlatRecordTraversalObjectNode node = new FlatRecordTraversalObjectNode(flatRecord);

    // nulls
    assertThat(node.getFieldValue("nonExistentField")).isNull();
    assertThat(node.getFieldNode("nonExistentField")).isNull();
  }

  @Test
  public void testWalkKitchenSinkFlatRecordThroughTheNodes() {
    FlatRecord flatRecord = createKitchenSinkFlatRecord();
    FlatRecordTraversalObjectNode node = new FlatRecordTraversalObjectNode(flatRecord);

    // primitives
    assertThat(node.getFieldValue("intVal")).isEqualTo(1);
    assertThat(node.getFieldValue("longVal")).isEqualTo(2L);
    assertThat(node.getFieldValue("floatVal")).isEqualTo(3.0f);
    assertThat(node.getFieldValue("doubleVal")).isEqualTo(4.0);
    assertThat(node.getFieldValue("booleanVal")).isEqualTo(true);
    assertThat(node.getFieldValue("byteVal")).isEqualTo(5);
    assertThat(node.getFieldValue("shortVal")).isEqualTo(6);
    assertThat(node.getFieldValue("charVal")).isEqualTo(55); // '7' as int
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedIntVal")).getFieldValue("value")).isEqualTo(8);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedLongVal")).getFieldValue("value")).isEqualTo(9L);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedFloatVal")).getFieldValue("value")).isEqualTo(10.0f);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedDoubleVal")).getFieldValue("value")).isEqualTo(11.0);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedBooleanVal")).getFieldValue("value")).isEqualTo(true);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedByteVal")).getFieldValue("value")).isEqualTo(12);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedShortVal")).getFieldValue("value")).isEqualTo(13);
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("boxedCharVal")).getFieldValue("value")).isEqualTo(49); // '1' as int
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("stringVal")).getFieldValue("value")).isEqualTo("15");
    assertThat(node.getFieldValue("inlineIntVal")).isEqualTo(16);
    assertThat(node.getFieldValue("inlineLongVal")).isEqualTo(17L);
    assertThat(node.getFieldValue("inlineFloatVal")).isEqualTo(18.0f);
    assertThat(node.getFieldValue("inlineDoubleVal")).isEqualTo(19.0);
    assertThat(node.getFieldValue("inlineBooleanVal")).isEqualTo(true);
    assertThat(node.getFieldValue("inlineByteVal")).isEqualTo(20);
    assertThat(node.getFieldValue("inlineShortVal")).isEqualTo(21);
    assertThat(node.getFieldValue("inlineCharVal")).isEqualTo(50); // '2' as int
    assertThat(node.getFieldValue("inlineStringVal")).isEqualTo("23");
    assertThat(node.getFieldValue("bytesVal")).isEqualTo(new byte[]{24, 25, 26});
    assertThat(((FlatRecordTraversalObjectNode) node.getFieldNode("customTypeVal")).getFieldValue("value")).isEqualTo("27");

    // subType
    FlatRecordTraversalObjectNode subTypeNode = (FlatRecordTraversalObjectNode) node.getFieldNode("subType");
    assertThat(subTypeNode.getFieldValue("intVal")).isEqualTo(28);
    assertThat(subTypeNode.getFieldValue("longVal")).isEqualTo(29L);
    assertThat(subTypeNode.getFieldValue("floatVal")).isEqualTo(30.0f);

    // subTypeList
    FlatRecordTraversalListNode subTypeListNode = (FlatRecordTraversalListNode) node.getFieldNode("subTypeList");
    assertThat(subTypeListNode.getObject(0).getFieldValue("intVal")).isEqualTo(31);
    assertThat(subTypeListNode.getObject(0).getFieldValue("longVal")).isEqualTo(32L);
    assertThat(subTypeListNode.getObject(0).getFieldValue("floatVal")).isEqualTo(33.0f);
    assertThat(subTypeListNode.getObject(1).getFieldValue("intVal")).isEqualTo(34);
    assertThat(subTypeListNode.getObject(1).getFieldValue("longVal")).isEqualTo(35L);
    assertThat(subTypeListNode.getObject(1).getFieldValue("floatVal")).isEqualTo(36.0f);

    // subTypeSet
    FlatRecordTraversalSetNode subTypeSetNode = (FlatRecordTraversalSetNode) node.getFieldNode("subTypeSet");
    assertThat(subTypeSetNode)
        .extracting(
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("intVal"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueLong("longVal"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueFloat("floatVal"))
        .containsExactlyInAnyOrder(
            tuple(37, 38L, 39.0f),
            tuple(40, 41L, 42.0f));

    // subTypeMap
    FlatRecordTraversalMapNode subTypeMapNode = (FlatRecordTraversalMapNode) node.getFieldNode("subTypeMap");
    assertThat(subTypeMapNode).hasSize(2);
    assertThat(subTypeMapNode.entrySet())
        .extracting(
            entry -> ((FlatRecordTraversalObjectNode) entry.getKey()).getFieldValueInt("value"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValueInt("intVal"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValueLong("longVal"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValueFloat("floatVal"))
        .containsExactlyInAnyOrder(
            tuple(43, 44, 45L, 46.0f),
            tuple(47, 48, 49L, 50.0f));

    // complexMapKeyMap
    FlatRecordTraversalMapNode complexMapKeyMapNode = (FlatRecordTraversalMapNode) node.getFieldNode("complexMapKeyMap");
    assertThat(complexMapKeyMapNode).hasSize(2);
    assertThat(complexMapKeyMapNode.entrySet())
        .extracting(
            entry -> {
              FlatRecordTraversalObjectNode keyNode = (FlatRecordTraversalObjectNode) entry.getKey();
              return keyNode.getFieldValueInt("value1");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueInt("intVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueLong("longVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueFloat("floatVal");
            })
        .containsExactlyInAnyOrder(
            tuple(51, 53, 54L, 55.0f),
            tuple(56, 58, 59L, 60.0f));

    // hashableSet
    FlatRecordTraversalSetNode hashableSetNode = (FlatRecordTraversalSetNode) node.getFieldNode("hashableSet");
    assertThat(hashableSetNode)
        .extracting(
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("value1"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("value2"))
        .containsExactlyInAnyOrder(
            tuple(61, 62),
            tuple(63, 64));

    // hashableMap
    FlatRecordTraversalMapNode hashableMapNode = (FlatRecordTraversalMapNode) node.getFieldNode("hashableMap");
    assertThat(hashableMapNode).hasSize(2);
    assertThat(hashableMapNode.entrySet())
        .extracting(
            entry -> {
              FlatRecordTraversalObjectNode keyNode = (FlatRecordTraversalObjectNode) entry.getKey();
              return keyNode.getFieldValueInt("value1");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueInt("intVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueLong("longVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueFloat("floatVal");
            })
        .containsExactlyInAnyOrder(
            tuple(65, 67, 68L, 69.0f),
            tuple(70, 72, 73L, 74.0f));
  }

  @Test
  public void testWalkKitchenSinkFlatRecordUsingSchemaHints() {
    FlatRecord flatRecord = createKitchenSinkFlatRecord();
    FlatRecordTraversalObjectNode node = new FlatRecordTraversalObjectNode(flatRecord);

    // primitives
    assertThat(node.getFieldValueInt("intVal")).isEqualTo(1);
    assertThat(node.getFieldValueLong("longVal")).isEqualTo(2L);
    assertThat(node.getFieldValueFloat("floatVal")).isEqualTo(3.0f);
    assertThat(node.getFieldValueDouble("doubleVal")).isEqualTo(4.0);
    assertThat(node.getFieldValueBoolean("booleanVal")).isTrue();
    assertThat(node.getFieldValueInt("byteVal")).isEqualTo((byte) 5);
    assertThat(node.getFieldValueInt("shortVal")).isEqualTo((short) 6);
    assertThat(node.getFieldValueInt("charVal")).isEqualTo('7');
    assertThat(node.getObjectFieldNode("boxedIntVal").getFieldValueInt("value")).isEqualTo(8);
    assertThat(node.getObjectFieldNode("boxedLongVal").getFieldValueLong("value")).isEqualTo(9L);
    assertThat(node.getObjectFieldNode("boxedFloatVal").getFieldValueFloat("value")).isEqualTo(10.0f);
    assertThat(node.getObjectFieldNode("boxedDoubleVal").getFieldValueDouble("value")).isEqualTo(11.0);
    assertThat(node.getObjectFieldNode("boxedBooleanVal").getFieldValueBoolean("value")).isTrue();
    assertThat(node.getObjectFieldNode("boxedByteVal").getFieldValueInt("value")).isEqualTo((byte) 12);
    assertThat(node.getObjectFieldNode("boxedShortVal").getFieldValueInt("value")).isEqualTo((short) 13);
    assertThat(node.getObjectFieldNode("boxedCharVal").getFieldValueInt("value")).isEqualTo('1');
    assertThat(node.getObjectFieldNode("stringVal").getFieldValueString("value")).isEqualTo("15");
    assertThat(node.getFieldValueInt("inlineIntVal")).isEqualTo(16);
    assertThat(node.getFieldValueLong("inlineLongVal")).isEqualTo(17L);
    assertThat(node.getFieldValueFloat("inlineFloatVal")).isEqualTo(18.0f);
    assertThat(node.getFieldValueDouble("inlineDoubleVal")).isEqualTo(19.0);
    assertThat(node.getFieldValueBoolean("inlineBooleanVal")).isTrue();
    assertThat(node.getFieldValueInt("inlineByteVal")).isEqualTo((byte) 20);
    assertThat(node.getFieldValueInt("inlineShortVal")).isEqualTo((short) 21);
    assertThat(node.getFieldValueInt("inlineCharVal")).isEqualTo('2');
    assertThat(node.getFieldValueString("inlineStringVal")).isEqualTo("23");
    assertThat(node.getFieldValueBytes("bytesVal")).containsExactly((byte) 24, (byte) 25, (byte) 26);
    assertThat(node.getObjectFieldNode("customTypeVal").getFieldValueString("value")).isEqualTo("27");

    // subType
    FlatRecordTraversalObjectNode subTypeNode = node.getObjectFieldNode("subType");
    assertThat(subTypeNode.getFieldValueInt("intVal")).isEqualTo(28);
    assertThat(subTypeNode.getFieldValueLong("longVal")).isEqualTo(29L);
    assertThat(subTypeNode.getFieldValueFloat("floatVal")).isEqualTo(30.0f);

    // subTypeList
    FlatRecordTraversalListNode subTypeListNode = node.getListFieldNode("subTypeList");
    assertThat(subTypeListNode)
        .extracting(
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("intVal"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueLong("longVal"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueFloat("floatVal"))
        .containsExactlyInAnyOrder(
            tuple(31, 32L, 33.0f),
            tuple(34, 35L, 36.0f));

    // subTypeSet
    FlatRecordTraversalSetNode subTypeSetNode = node.getSetFieldNode("subTypeSet");
    assertThat(subTypeSetNode)
        .extracting(
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("intVal"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueLong("longVal"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueFloat("floatVal"))
        .containsExactlyInAnyOrder(
            tuple(37, 38L, 39.0f),
            tuple(40, 41L, 42.0f));

    // subTypeMap
    FlatRecordTraversalMapNode subTypeMapNode = node.getMapFieldNode("subTypeMap");
    assertThat(subTypeMapNode).hasSize(2);
    assertThat(subTypeMapNode.entrySet())
        .extracting(
            entry -> ((FlatRecordTraversalObjectNode) entry.getKey()).getFieldValueInt("value"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValueInt("intVal"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValueLong("longVal"),
            entry -> ((FlatRecordTraversalObjectNode) entry.getValue()).getFieldValueFloat("floatVal"))
        .containsExactlyInAnyOrder(
            tuple(43, 44, 45L, 46.0f),
            tuple(47, 48, 49L, 50.0f));

    // complexMapKeyMap
    FlatRecordTraversalMapNode complexMapKeyMapNode = node.getMapFieldNode("complexMapKeyMap");
    assertThat(complexMapKeyMapNode).hasSize(2);
    assertThat(complexMapKeyMapNode.entrySet())
        .extracting(
            entry -> {
              FlatRecordTraversalObjectNode keyNode = (FlatRecordTraversalObjectNode) entry.getKey();
              return keyNode.getFieldValueInt("value1");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueInt("intVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueLong("longVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueFloat("floatVal");
            })
        .containsExactlyInAnyOrder(
            tuple(51, 53, 54L, 55.0f),
            tuple(56, 58, 59L, 60.0f));

    // hashableSet
    FlatRecordTraversalSetNode hashableSetNode = node.getSetFieldNode("hashableSet");
    assertThat(hashableSetNode)
        .extracting(
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("value1"),
            n -> ((FlatRecordTraversalObjectNode) n).getFieldValueInt("value2"))
        .containsExactlyInAnyOrder(
            tuple(61, 62),
            tuple(63, 64));

    // hashableMap
    FlatRecordTraversalMapNode hashableMapNode = node.getMapFieldNode("hashableMap");
    assertThat(hashableMapNode).hasSize(2);
    assertThat(hashableMapNode.entrySet())
        .extracting(
            entry -> {
              FlatRecordTraversalObjectNode keyNode = (FlatRecordTraversalObjectNode) entry.getKey();
              return keyNode.getFieldValueInt("value1");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueInt("intVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueLong("longVal");
            },
            entry -> {
              FlatRecordTraversalObjectNode valueNode = (FlatRecordTraversalObjectNode) entry.getValue();
              return valueNode.getFieldValueFloat("floatVal");
            })
        .containsExactlyInAnyOrder(
            tuple(65, 67, 68L, 69.0f),
            tuple(70, 72, 73L, 74.0f));
  }


  private FlatRecord createMovieFlatRecord() {
    Movie movie1 = new Movie();
    movie1.id = 1;
    movie1.title = "Movie1";
    movie1.releaseYear = 2020;
    movie1.primaryGenre = "action";
    movie1.maturityRating = new MaturityRating("PG", "Some advisory");
    movie1.countries = new HashSet<>();
    movie1.countries.add(new Country("US"));
    movie1.countries.add(new Country("CA"));
    movie1.tags = new HashMap<>();
    movie1.tags.put(new Tag("Type"), new TagValue("Movie"));
    movie1.tags.put(new Tag("Genre"), new TagValue("action"));
    movie1.cast = new HashSet<>();
    movie1.cast.add(new CastMember(1, "Benedict Cumberbatch", CastRole.ACTOR));
    movie1.cast.add(new CastMember(2, "Martin Freeman", CastRole.ACTOR));
    movie1.cast.add(new CastMember(2, "Quentin Tarantino", CastRole.DIRECTOR));
    movie1.awardsReceived = new ArrayList<>();
    movie1.awardsReceived.add(new Award("Oscar", 2020));
    movie1.awardsReceived.add(new Award("Golden Globe", 2025));

    SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Movie.class);
    FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
    HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
    FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);

    flatRecordWriter.reset();
    objMapper.writeFlat(movie1, flatRecordWriter);
    return flatRecordWriter.generateFlatRecord();
  }

  private FlatRecord createKitchenSinkFlatRecord() {
    KitchenSink sink = new KitchenSink();
    sink.intVal = 1;
    sink.longVal = 2L;
    sink.floatVal = 3.0f;
    sink.doubleVal = 4.0;
    sink.booleanVal = true;
    sink.byteVal = 5;
    sink.shortVal = 6;
    sink.charVal = '7';
    sink.boxedIntVal = 8;
    sink.boxedLongVal = 9L;
    sink.boxedFloatVal = 10.0f;
    sink.boxedDoubleVal = 11.0;
    sink.boxedBooleanVal = true;
    sink.boxedByteVal = 12;
    sink.boxedShortVal = 13;
    sink.boxedCharVal = '1';
    sink.stringVal = "15";
    sink.inlineIntVal = 16;
    sink.inlineLongVal = 17L;
    sink.inlineFloatVal = 18.0f;
    sink.inlineDoubleVal = 19.0;
    sink.inlineBooleanVal = true;
    sink.inlineByteVal = 20;
    sink.inlineShortVal = 21;
    sink.inlineCharVal = '2';
    sink.inlineStringVal = "23";
    sink.bytesVal = new byte[]{24, 25, 26};
    sink.customTypeVal = "27";
    sink.subType = new KitchenSink.SubType();
    sink.subType.intVal = 28;
    sink.subType.longVal = 29L;
    sink.subType.floatVal = 30.0f;
    sink.subTypeList = new ArrayList<>();
    KitchenSink.SubType subTypeListElement1 = new KitchenSink.SubType();
    subTypeListElement1.intVal = 31;
    subTypeListElement1.longVal = 32L;
    subTypeListElement1.floatVal = 33.0f;
    KitchenSink.SubType subTypeListElement2 = new KitchenSink.SubType();
    subTypeListElement2.intVal = 34;
    subTypeListElement2.longVal = 35L;
    subTypeListElement2.floatVal = 36.0f;
    sink.subTypeList.add(subTypeListElement1);
    sink.subTypeList.add(subTypeListElement2);
    sink.subTypeSet = new HashSet<>();
    KitchenSink.SubType subTypeSetElement1 = new KitchenSink.SubType();
    subTypeSetElement1.intVal = 37;
    subTypeSetElement1.longVal = 38L;
    subTypeSetElement1.floatVal = 39.0f;
    KitchenSink.SubType subTypeSetElement2 = new KitchenSink.SubType();
    subTypeSetElement2.intVal = 40;
    subTypeSetElement2.longVal = 41L;
    subTypeSetElement2.floatVal = 42.0f;
    sink.subTypeSet.add(subTypeSetElement1);
    sink.subTypeSet.add(subTypeSetElement2);
    sink.subTypeMap = new HashMap<>();
    KitchenSink.MapKey mapKey1 = new KitchenSink.MapKey();
    mapKey1.value = 43;
    KitchenSink.SubType subTypeMapElement1 = new KitchenSink.SubType();
    subTypeMapElement1.intVal = 44;
    subTypeMapElement1.longVal = 45L;
    subTypeMapElement1.floatVal = 46.0f;
    KitchenSink.MapKey mapKey2 = new KitchenSink.MapKey();
    mapKey2.value = 47;
    KitchenSink.SubType subTypeMapElement2 = new KitchenSink.SubType();
    subTypeMapElement2.intVal = 48;
    subTypeMapElement2.longVal = 49L;
    subTypeMapElement2.floatVal = 50.0f;
    sink.subTypeMap.put(mapKey1, subTypeMapElement1);
    sink.subTypeMap.put(mapKey2, subTypeMapElement2);
    sink.complexMapKeyMap = new HashMap<>();
    KitchenSink.ComplexMapKey complexMapKey1 = new KitchenSink.ComplexMapKey();
    complexMapKey1.value1 = 51;
    complexMapKey1.value2 = 52;
    KitchenSink.SubType complexMapKeyMapElement1 = new KitchenSink.SubType();
    complexMapKeyMapElement1.intVal = 53;
    complexMapKeyMapElement1.longVal = 54L;
    complexMapKeyMapElement1.floatVal = 55.0f;
    KitchenSink.ComplexMapKey complexMapKey2 = new KitchenSink.ComplexMapKey();
    complexMapKey2.value1 = 56;
    complexMapKey2.value2 = 57;
    KitchenSink.SubType complexMapKeyMapElement2 = new KitchenSink.SubType();
    complexMapKeyMapElement2.intVal = 58;
    complexMapKeyMapElement2.longVal = 59L;
    complexMapKeyMapElement2.floatVal = 60.0f;
    sink.complexMapKeyMap.put(complexMapKey1, complexMapKeyMapElement1);
    sink.complexMapKeyMap.put(complexMapKey2, complexMapKeyMapElement2);
    sink.hashableSet = new HashSet<>();
    KitchenSink.HashableKey hashableSetElement1 = new KitchenSink.HashableKey();
    hashableSetElement1.value1 = 61;
    hashableSetElement1.value2 = 62;
    KitchenSink.HashableKey hashableSetElement2 = new KitchenSink.HashableKey();
    hashableSetElement2.value1 = 63;
    hashableSetElement2.value2 = 64;
    sink.hashableSet.add(hashableSetElement1);
    sink.hashableSet.add(hashableSetElement2);
    sink.hashableMap = new HashMap<>();
    KitchenSink.HashableKey hashableMapKey1 = new KitchenSink.HashableKey();
    hashableMapKey1.value1 = 65;
    hashableMapKey1.value2 = 66;
    KitchenSink.SubType hashableMapElement1 = new KitchenSink.SubType();
    hashableMapElement1.intVal = 67;
    hashableMapElement1.longVal = 68L;
    hashableMapElement1.floatVal = 69.0f;
    KitchenSink.HashableKey hashableMapKey2 = new KitchenSink.HashableKey();
    hashableMapKey2.value1 = 70;
    hashableMapKey2.value2 = 71;
    KitchenSink.SubType hashableMapElement2 = new KitchenSink.SubType();
    hashableMapElement2.intVal = 72;
    hashableMapElement2.longVal = 73L;
    hashableMapElement2.floatVal = 74.0f;
    sink.hashableMap.put(hashableMapKey1, hashableMapElement1);
    sink.hashableMap.put(hashableMapKey2, hashableMapElement2);

    SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(KitchenSink.class);
    FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
    HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
    FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);

    flatRecordWriter.reset();
    objMapper.writeFlat(sink, flatRecordWriter);
    return flatRecordWriter.generateFlatRecord();
  }
}
