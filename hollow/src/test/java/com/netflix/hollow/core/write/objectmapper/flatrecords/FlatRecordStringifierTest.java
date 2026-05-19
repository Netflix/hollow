package com.netflix.hollow.core.write.objectmapper.flatrecords;

import static org.assertj.core.api.Assertions.assertThat;

import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.Award;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.CastMember;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.CastRole;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.Country;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.MaturityRating;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.Movie;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.Tag;
import com.netflix.hollow.core.write.objectmapper.flatrecords.dto.TagValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FlatRecordStringifierTest {

  @Test
  public void stringifiesScalarFieldsAndLeavesUnsetFieldsNull() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.title = "Movie1";
    movie.releaseYear = 2020;
    movie.primaryGenre = "action";

    String result = stringify(movie);

    assertThat(result).contains("id: 1");
    assertThat(result).contains("title: Movie1");
    assertThat(result).contains("releaseYear: 2020");
    assertThat(result).contains("primaryGenre: action");

    // Unset reference fields are stringified as null
    assertThat(result).contains("name: null");
    assertThat(result).contains("duration: null");
    assertThat(result).contains("isReleased: null");
    assertThat(result).contains("popularity: null");
    assertThat(result).contains("maturityRating: null");
    assertThat(result).contains("countries: null");
    assertThat(result).contains("tags: null");
    assertThat(result).contains("cast: null");
    assertThat(result).contains("awardsReceived: null");
    assertThat(result).contains("mapOfCountryToCastMembers: null");

    // Unset primitive fields use Java defaults
    assertThat(result).contains("price: 0.0");
    assertThat(result).contains("averageRating: 0.0");
    assertThat(result).contains("bytes: 0");

    // Schema field order is preserved (id before title before releaseYear).
    assertThat(result).containsSubsequence("id: 1", "title: Movie1", "releaseYear: 2020");
  }

  @Test
  public void stringifiesNestedObjectFieldsWithIndentation() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.maturityRating = new MaturityRating("PG", "Some advisory");

    String result = stringify(movie);

    assertThat(result).containsSubsequence(
        "maturityRating: ",
        "rating: PG",
        "advisory: Some advisory");
  }

  @Test
  public void stringifiesListsInInsertionOrderWithIndexedKeys() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.awardsReceived = new ArrayList<>();
    movie.awardsReceived.add(new Award("Oscar", 2020));
    movie.awardsReceived.add(new Award("Golden Globe", 2025));

    String result = stringify(movie);

    // Lists preserve insertion order and use indexed (e0, e1) prefixes
    assertThat(result).containsSubsequence(
        "awardsReceived: ",
        "e0: ",
        "name: Oscar",
        "year: 2020",
        "e1: ",
        "name: Golden Globe",
        "year: 2025");
  }

  @Test
  public void stringifiesSetElementsWithUnorderedPrefix() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.countries = new HashSet<>();
    movie.countries.add(new Country("US"));
    movie.countries.add(new Country("CA"));

    String result = stringify(movie);

    // Sets emit each element with an unordered "e: " prefix; iteration order is
    // not guaranteed, so assert presence of both elements rather than position.
    assertThat(result).contains("countries: ");
    assertThat(result).contains("e: US");
    assertThat(result).contains("e: CA");
  }

  @Test
  public void stringifiesMapEntriesAsKeyValuePairs() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.tags = new HashMap<>();
    movie.tags.put(new Tag("Type"), new TagValue("Movie"));
    movie.tags.put(new Tag("Genre"), new TagValue("action"));

    String result = stringify(movie);

    assertThat(result).contains("tags: ");
    // Each entry produces k:/v: lines; iteration order is not guaranteed,
    // but k must immediately precede its v.
    assertThat(result).containsSubsequence("k: Type", "v: Movie");
    assertThat(result).containsSubsequence("k: Genre", "v: action");
  }

  @Test
  public void stringifiesNestedObjectFieldsAsSubrecords() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.cast = new HashSet<>();
    movie.cast.add(new CastMember(1, "Benedict Cumberbatch", CastRole.ACTOR));

    String result = stringify(movie);

    assertThat(result).containsSubsequence(
        "cast: ",
        "e: ",
        "id: 1",
        "name: Benedict Cumberbatch",
        "role: ACTOR");
  }

  @Test
  public void excludeObjectTypesReplacesMatchingNodesWithNull() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.maturityRating = new MaturityRating("PG", "Some advisory");
    movie.countries = new HashSet<>();
    movie.countries.add(new Country("US"));

    FlatRecord flatRecord = createFlatRecord(movie);
    FlatRecordStringifier stringifier = new FlatRecordStringifier()
        .addExcludeObjectTypes("MaturityRating");
    String result = stringifier.stringify(flatRecord);

    // The MaturityRating reference is collapsed to null
    assertThat(result).contains("maturityRating: null");
    // Other fields still stringify normally
    assertThat(result).contains("id: 1");
    assertThat(result).contains("e: US");
    // The inner MaturityRating fields no longer appear at all
    assertThat(result).doesNotContain("rating: PG");
    assertThat(result).doesNotContain("advisory: Some advisory");
  }

  @Test
  public void stringifiesAllSupportedFieldTypesInASingleRecord() {
    Movie movie = new Movie();
    movie.id = 1;
    movie.title = "Movie1";
    movie.releaseYear = 2020;
    movie.primaryGenre = "action";
    movie.maturityRating = new MaturityRating("PG", "Some advisory");
    movie.countries = new HashSet<>();
    movie.countries.add(new Country("US"));
    movie.countries.add(new Country("CA"));
    movie.tags = new HashMap<>();
    movie.tags.put(new Tag("Type"), new TagValue("Movie"));
    movie.tags.put(new Tag("Genre"), new TagValue("action"));
    movie.cast = new HashSet<>();
    movie.cast.add(new CastMember(1, "Benedict Cumberbatch", CastRole.ACTOR));
    movie.cast.add(new CastMember(2, "Martin Freeman", CastRole.ACTOR));
    movie.cast.add(new CastMember(3, "Quentin Tarantino", CastRole.DIRECTOR));
    movie.awardsReceived = new ArrayList<>();
    movie.awardsReceived.add(new Award("Oscar", 2020));
    movie.awardsReceived.add(new Award("Golden Globe", 2025));

    String result = stringify(movie);

    // Top-level schema fields are emitted in declaration order.
    assertThat(result).containsSubsequence(
        "id: 1",
        "title: Movie1",
        "releaseYear: 2020",
        "primaryGenre: action",
        "maturityRating: ",
        "countries: ",
        "tags: ",
        "cast: ",
        "awardsReceived: ");

    // Nested reference fields render under their parent.
    assertThat(result).containsSubsequence(
        "maturityRating: ",
        "rating: PG",
        "advisory: Some advisory");

    // Awards list keeps insertion order with indexed prefixes.
    assertThat(result).containsSubsequence(
        "awardsReceived: ",
        "e0: ",
        "name: Oscar",
        "e1: ",
        "name: Golden Globe");

    // Set/map members are present (order is not guaranteed).
    assertThat(result).contains("e: US");
    assertThat(result).contains("e: CA");
    assertThat(result).containsSubsequence("k: Type", "v: Movie");
    assertThat(result).containsSubsequence("k: Genre", "v: action");
    assertThat(result).contains("Benedict Cumberbatch");
    assertThat(result).contains("Martin Freeman");
    assertThat(result).contains("Quentin Tarantino");
  }

  private String stringify(Movie movie) {
    return new FlatRecordStringifier().stringify(createFlatRecord(movie));
  }

  private FlatRecord createFlatRecord(Movie movie) {
    SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Movie.class);
    FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
    HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
    FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);
    flatRecordWriter.reset();
    objMapper.writeFlat(movie, flatRecordWriter);
    return flatRecordWriter.generateFlatRecord();
  }
}
