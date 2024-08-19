package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FakeHollowSchemaIdentifierMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.test.dto.Award;
import com.netflix.hollow.test.dto.CastMember;
import com.netflix.hollow.test.dto.CastRole;
import com.netflix.hollow.test.dto.Country;
import com.netflix.hollow.test.dto.MaturityRating;
import com.netflix.hollow.test.dto.Movie;
import com.netflix.hollow.test.dto.Tag;
import com.netflix.hollow.test.dto.TagValue;
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
    FlatRecord flatRecord = createTestFlatRecord();
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
    FlatRecord flatRecord = createTestFlatRecord();
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
    FlatRecord flatRecord = createTestFlatRecord();
    FlatRecordTraversalObjectNode node = new FlatRecordTraversalObjectNode(flatRecord);

    // nulls
    assertThat(node.getFieldValue("nonExistentField")).isNull();
    assertThat(node.getFieldNode("nonExistentField")).isNull();
  }

  private FlatRecord createTestFlatRecord() {
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
}
