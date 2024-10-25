package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;


import com.netflix.hollow.core.schema.SimpleHollowDataset;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FakeHollowIdentifierMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FakeHollowSchemaIdentifierMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.core.write.objectmapper.flatrecords.HollowSchemaIdentifierMapper;
import com.netflix.hollow.test.dto.movie.Award;
import com.netflix.hollow.test.dto.movie.CastMember;
import com.netflix.hollow.test.dto.movie.CastRole;
import com.netflix.hollow.test.dto.movie.Country;
import com.netflix.hollow.test.dto.movie.MaturityRating;
import com.netflix.hollow.test.dto.movie.Movie;
import com.netflix.hollow.test.dto.movie.Tag;
import com.netflix.hollow.test.dto.movie.TagValue;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FlatRecordTraversalObjectNodeEqualityTest {
    // Mapper and writer for types of version 1
    private HollowObjectMapper mapper1;
    private FlatRecordWriter writer1;

    // Mapper and writer for types of version 2
    private HollowObjectMapper mapper2;
    private FlatRecordWriter writer2;
    @Before
    public void beforeEach() {
        HollowSchemaIdentifierMapper idMapper = new FakeHollowIdentifierMapper();

        mapper1 = new HollowObjectMapper(new HollowWriteStateEngine());
        mapper1.initializeTypeState(TypeState1.class);
        mapper1.initializeTypeState(IntSetTypeState1.class);
        mapper1.initializeTypeState(IntTypeState1.class);
        mapper1.initializeTypeState(RecordWithSubObject1.class);
        writer1 = new FlatRecordWriter(mapper1.getStateEngine(), idMapper);

        mapper2 = new HollowObjectMapper(new HollowWriteStateEngine());
        mapper2.initializeTypeState(TypeState2.class);
        mapper2.initializeTypeState(IntSetTypeState2.class);
        mapper2.initializeTypeState(IntTypeState2.class);
        mapper2.initializeTypeState(RecordWithSubObject2.class);
        writer2 = new FlatRecordWriter(mapper2.getStateEngine(), idMapper);
    }
    @Test
    public void shouldEqualOnTheSameFlatRecord() {
        FlatRecord flatRecord1 = createTestFlatRecord1();
        FlatRecord flatRecord2 = createTestFlatRecord1();
        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(flatRecord1), new FlatRecordTraversalObjectNode(flatRecord2))).isTrue();
    }

    @Test
    public void shouldFailOnTheDifferentFlatRecord() {
        FlatRecord flatRecord1 = createTestFlatRecord1();
        FlatRecord flatRecord2 = createTestFlatRecord2();
        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(flatRecord1), new FlatRecordTraversalObjectNode(flatRecord2))).isFalse();
    }

    @Test
    public void differentMap() {
        SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Movie.class);
        FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
        HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
        FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);

        Movie movie1 = new Movie();
        movie1.tags = new HashMap<>();
        movie1.tags.put(new Tag("Type"), new TagValue("Movie"));
        movie1.tags.put(new Tag("Genre"), new TagValue("action"));

        Movie movie2 = new Movie();
        movie2.tags = new HashMap<>();
        movie2.tags.put(new Tag("Type"), new TagValue("Movie"));
        movie2.tags.put(new Tag("Genre"), new TagValue("comedy"));

        flatRecordWriter.reset();
        objMapper.writeFlat(movie1, flatRecordWriter);
        FlatRecord flatRecord1 = flatRecordWriter.generateFlatRecord();

        flatRecordWriter.reset();
        objMapper.writeFlat(movie2, flatRecordWriter);
        FlatRecord flatRecord2 = flatRecordWriter.generateFlatRecord();

        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(flatRecord1), new FlatRecordTraversalObjectNode(flatRecord2))).isFalse();
        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(flatRecord2), new FlatRecordTraversalObjectNode(flatRecord1))).isFalse();
    }

    @Test
    public void differentSet() {
        SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Movie.class);
        FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
        HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
        FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);

        Movie movie1 = new Movie();
        movie1.countries = new HashSet<>();
        movie1.countries.add(new Country("US"));
        movie1.countries.add(new Country("CA"));

        Movie movie2 = new Movie();
        movie2.countries = new HashSet<>();
        movie2.countries.add(new Country("US"));
        movie2.countries.add(new Country("CB"));

        flatRecordWriter.reset();
        objMapper.writeFlat(movie1, flatRecordWriter);
        FlatRecord flatRecord1 = flatRecordWriter.generateFlatRecord();

        flatRecordWriter.reset();
        objMapper.writeFlat(movie2, flatRecordWriter);
        FlatRecord flatRecord2 = flatRecordWriter.generateFlatRecord();

        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(flatRecord1), new FlatRecordTraversalObjectNode(flatRecord2))).isFalse();
    }

    @Test
    public void differentList() {
        SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Movie.class);
        FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
        HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
        FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);

        Movie movie1 = new Movie();
        movie1.awardsReceived = new ArrayList<>();
        movie1.awardsReceived.add(new Award("Oscar", 2020));
        movie1.awardsReceived.add(new Award("Golden Globe", 2025));

        Movie movie2 = new Movie();
        movie2.awardsReceived = new ArrayList<>();
        movie2.awardsReceived.add(new Award("Oscar", 2020));
        movie2.awardsReceived.add(new Award("Golden Globe", 2026));

        flatRecordWriter.reset();
        objMapper.writeFlat(movie1, flatRecordWriter);
        FlatRecord flatRecord1 = flatRecordWriter.generateFlatRecord();

        flatRecordWriter.reset();
        objMapper.writeFlat(movie2, flatRecordWriter);
        FlatRecord flatRecord2 = flatRecordWriter.generateFlatRecord();

        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(flatRecord1), new FlatRecordTraversalObjectNode(flatRecord2))).isFalse();
    }


    private FlatRecord createTestFlatRecord1() {
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

    @Test
    public void shouldFindRecordsEqualOnDifferentDataModelsWithDifferentValues() {
        RecordWithSubObject1 left = new RecordWithSubObject1();
        left.id = "ID";
        left.intField = 1;
        left.subObject = new RecordSubObject();
        left.subObject.stringField = "A";
        left.subObject.intField = 1;
        writer1.reset();
        mapper1.writeFlat(left, writer1);
        FlatRecord leftRec = writer1.generateFlatRecord();

        // RecordWithSubObject2 does not have a subObject field
        RecordWithSubObject2 right = new RecordWithSubObject2();
        right.id = "ID";
        right.intField = 1;
        writer2.reset();
        mapper2.writeFlat(right, writer2);
        FlatRecord rightRec = writer2.generateFlatRecord();
        // With fuzzy matching, the records are equal if the intersection of the schemas have the same fields.
        // In this case, `RecordWithSubObject2` does not know about `subObject` so it's not considered in the
        // equality check.
        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(leftRec), new FlatRecordTraversalObjectNode(rightRec))).isTrue();
        Assertions.assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(rightRec), new FlatRecordTraversalObjectNode(leftRec))).isTrue();
    }

    @Test
    public void shouldFindRecordsUnequalOnTheSameDataModelWithAnObjectFieldNotSetOnOne() {
        HollowSchemaIdentifierMapper schemaMapper = new FakeHollowIdentifierMapper();

        HollowObjectMapper objectMapper = new HollowObjectMapper(new HollowWriteStateEngine());
        objectMapper.initializeTypeState(RecordWithSubObject1.class);
        objectMapper.initializeTypeState(RecordSubObject.class);
        FlatRecordWriter flatRecordWriter = new FlatRecordWriter(objectMapper.getStateEngine(), schemaMapper);

        RecordWithSubObject1 left = new RecordWithSubObject1();
        left.id = "ID";
        left.intField = 1;
        left.subObject = new RecordSubObject();
        left.subObject.stringField = "A";
        left.subObject.intField = 1;

        flatRecordWriter.reset();
        objectMapper.writeFlat(left, flatRecordWriter);
        FlatRecord leftRec = flatRecordWriter.generateFlatRecord();

        RecordWithSubObject1 right = new RecordWithSubObject1();
        right.id = "ID";
        right.intField = 1;
        flatRecordWriter.reset();
        objectMapper.writeFlat(right, flatRecordWriter);
        FlatRecord rightRec = flatRecordWriter.generateFlatRecord();

        // Even with fuzzy matching these records not equal bc "right" does not have `subObject`
        // it's defined in both schemas
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(leftRec), new FlatRecordTraversalObjectNode(rightRec))).isFalse();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(new FlatRecordTraversalObjectNode(rightRec), new FlatRecordTraversalObjectNode(leftRec))).isFalse();
    }

    @Test
    public void shouldFindTwoPrimitiveSetsToBeEqualRegardlessOfOrder() {
        IntSetTypeState1 intTypeState1 = new IntSetTypeState1();
        intTypeState1.id = "ID";
        intTypeState1.intSet = new HashSet<>(Arrays.asList(15, 5));

        writer1.reset();
        mapper1.writeFlat(intTypeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        IntSetTypeState2 intTypeState2 = new IntSetTypeState2();
        intTypeState2.id = "ID";
        intTypeState2.intSet = new HashSet<>(Arrays.asList(5, 15));
        writer2.reset();
        mapper2.writeFlat(intTypeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();

        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        // The order of the elements in the Set should not matter
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isTrue();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isTrue();
    }

    @Test
    public void shouldFindTwoPrimitiveSetsToBeDifferentIfContentIsDifferent() {
        IntSetTypeState1 intTypeState1 = new IntSetTypeState1();
        intTypeState1.id = "ID";
        intTypeState1.intSet = new HashSet<>(Arrays.asList(15, 5));

        writer1.reset();
        mapper1.writeFlat(intTypeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        IntSetTypeState2 intTypeState2 = new IntSetTypeState2();
        intTypeState2.id = "ID";
        intTypeState2.intSet = new HashSet<>(Arrays.asList(5, 20));
        writer2.reset();
        mapper2.writeFlat(intTypeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();

        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        // The order of the elements in the Set should not matter
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isFalse();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isFalse();
    }

    @Test
    public void shouldProvideCollisionGuaranteesForIntegerCollisions_onObjects() {
        IntTypeState1 intTypeState1 = new IntTypeState1();
        intTypeState1.intA = 15;
        intTypeState1.intB = 5;

        writer1.reset();
        mapper1.writeFlat(intTypeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        IntTypeState2 intTypeState2 = new IntTypeState2();
        intTypeState2.intA = 13;
        intTypeState2.intB = 7;
        writer2.reset();
        mapper2.writeFlat(intTypeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();

        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isFalse();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isFalse();
    }

    @Test
    public void shouldUseExactFlagToConsiderExtraFieldsInEquality_usingReferences() {
        TypeState1 typeState1 = new TypeState1();
        typeState1.longField = 1L;
        typeState1.stringField = "A";
        typeState1.doubleField = 1.0;
        typeState1.basicIntField = 1;
        typeState1.basicIntFieldOnlyInTypeState1 = 1; // This field being set should make the records unequal.

        writer1.reset();
        mapper1.writeFlat(typeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        TypeState2 typeState2 = new TypeState2();
        typeState2.longField = 1L;
        typeState2.stringField = "A";
        typeState2.doubleField = 1.0;
        typeState2.basicIntField = 1;
        writer2.reset();
        mapper2.writeFlat(typeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();

        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isTrue();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isTrue();
    }

    @Test
    public void shouldUseExactFlagToConsiderExtraFieldsInEquality_usingPrimitives() {
        TypeState1 typeState1 = new TypeState1();
        typeState1.longField = 1L;
        typeState1.stringField = "A";
        typeState1.doubleField = 1.0;
        typeState1.basicIntField = 1;
        typeState1.valueOnlyInTypeState1 = "A"; // This field being set should make the records unequal.

        writer1.reset();
        mapper1.writeFlat(typeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        TypeState2 typeState2 = new TypeState2();
        typeState2.longField = 1L;
        typeState2.stringField = "A";
        typeState2.doubleField = 1.0;
        typeState2.basicIntField = 1;
        writer2.reset();
        mapper2.writeFlat(typeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();

        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isTrue();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isTrue();
    }

    @Test
    public void shouldFindThatRecordsAreNotEqualBecauseMapValuesDiffer() {
        TypeState1 typeState1 = new TypeState1();
        typeState1.longField = 1L;
        Map<String, SubValue> map1 = new HashMap<>();
        map1.put("A", new SubValue("A", "AA"));
        map1.put("B", new SubValue("B", "BB"));
        map1.put("D", new SubValue("D", "DD"));
        typeState1.simpleMapField = map1;

        writer1.reset();
        mapper1.writeFlat(typeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        TypeState2 typeState2 = new TypeState2();
        typeState2.longField = 1L;
        Map<String, SubValue> map2 = new HashMap<>();
        map2.put("A", new SubValue("A", "AA"));
        map2.put("B", new SubValue("B", "BB"));
        map2.put("C", new SubValue("C", "CC"));
        typeState2.simpleMapField = map2;

        writer2.reset();
        mapper2.writeFlat(typeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();

        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isFalse();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isFalse();
    }

    @Test
    public void shouldFindSetAndMapFieldsAreEqualEvenIfOrderIsDifferent() {
        TypeState1 typeState1 = new TypeState1();
        typeState1.longField = 1L;
        typeState1.setOfScalars = new HashSet<>(Arrays.asList("B", "A"));
        typeState1.setOfObjects = new HashSet<>(Arrays.asList(
                new SubValue("B", "BB"),
                new SubValue("C", "CC"),
                new SubValue("A", "AA"))
        );
        typeState1.simpleMapField =
               new HashMap<String, SubValue>() {{
                   put("B", new SubValue("B", "BB"));
                     put("C", new SubValue("C", "CC"));
                        put("A", new SubValue("A", "AA"));
               }};

        writer1.reset();
        mapper1.writeFlat(typeState1, writer1);
        FlatRecord rec1 = writer1.generateFlatRecord();

        TypeState2 typeState2 = new TypeState2();
        typeState2.longField = 1L;
        typeState2.setOfScalars = new HashSet<>(Arrays.asList("A", "B"));
        typeState2.setOfObjects = new HashSet<>(Arrays.asList(
                new SubValue("A", "AA"),
                new SubValue("B", "BB"),
                new SubValue("C", "CC"))
        );
        typeState2.simpleMapField = new HashMap<String, SubValue>() {{
            put("A", new SubValue("A", "AA"));
            put("B", new SubValue("B", "BB"));
            put("C", new SubValue("C", "CC"));
        }};

        writer2.reset();
        mapper2.writeFlat(typeState2, writer2);
        FlatRecord rec2 = writer2.generateFlatRecord();
        FlatRecordTraversalObjectNode leftNode = new FlatRecordTraversalObjectNode(rec1);
        FlatRecordTraversalObjectNode rightNode = new FlatRecordTraversalObjectNode(rec2);

        assertThat(FlatRecordTraversalObjectNodeEquality.equals(leftNode, rightNode)).isTrue();
        assertThat(FlatRecordTraversalObjectNodeEquality.equals(rightNode, leftNode)).isTrue();
    }




    private FlatRecord createTestFlatRecord2() {
        Movie movie = new Movie();
        movie.id = 1;
        movie.title = "Movie1";
        movie.releaseYear = 2020;
        movie.primaryGenre = "action";
        movie.maturityRating = new MaturityRating("PG", "Some advisory");
        movie.countries = new HashSet<>();
        movie.countries.add(new Country("US"));
        movie.countries.add(new Country("CB"));
        movie.tags = new HashMap<>();
        movie.tags.put(new Tag("Type"), new TagValue("Movie"));
        movie.tags.put(new Tag("Genre"), new TagValue("action"));
        movie.cast = new HashSet<>();
        movie.cast.add(new CastMember(1, "Benedict Cumberbatch", CastRole.ACTOR));
        movie.cast.add(new CastMember(2, "Martin Freeman", CastRole.ACTOR));
        movie.cast.add(new CastMember(2, "Quentin Tarantino", CastRole.DIRECTOR));
        movie.awardsReceived = new ArrayList<>();
        movie.awardsReceived.add(new Award("Oscar", 2020));
        movie.awardsReceived.add(new Award("Golden Globe", 2025));

        SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Movie.class);
        FakeHollowSchemaIdentifierMapper idMapper = new FakeHollowSchemaIdentifierMapper(dataset);
        HollowObjectMapper objMapper = new HollowObjectMapper(HollowWriteStateCreator.createWithSchemas(dataset.getSchemas()));
        FlatRecordWriter flatRecordWriter = new FlatRecordWriter(dataset, idMapper);

        flatRecordWriter.reset();
        objMapper.writeFlat(movie, flatRecordWriter);
        return flatRecordWriter.generateFlatRecord();
    }


    @HollowTypeName(name = "TypeState")
    @HollowPrimaryKey(fields = "longField")
    public static class TypeState1 {
        public Long longField;
        public String stringField;
        @HollowInline
        public String inlineStringField;
        public String emptyStringField;
        public Double doubleField;
        public int basicIntField = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow
        public SubValue objectField;
        public List<String> listOfScalars;
        public Set<String> setOfScalars;
        public List<SubValue> listOfObjects;
        public Set<SubValue> setOfObjects;
        public Map<String, SubValue> simpleMapField;

        // For testing differences between type versions
        public String valueOnlyInTypeState1;
        public int basicIntFieldOnlyInTypeState1 = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow
    }

    public static class SubValue {
        public String value;
        @HollowInline
        public String anotherValue;

        public SubValue(String value) {
            this.value = value;
        }

        public SubValue(String value, String anotherValue) {
            this.value = value;
            this.anotherValue = anotherValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubValue)) return false;
            SubValue subValue = (SubValue) o;
            return Objects.equals(value, subValue.value) &&
                    Objects.equals(anotherValue, subValue.anotherValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, anotherValue);
        }
    }

    @HollowTypeName(name = "TypeState")
    @HollowPrimaryKey(fields = "longField")
    public static class TypeState2 {
        public Long longField;
        public String stringField;
        @HollowInline
        public String inlineStringField;
        public String emptyStringField;
        public Double doubleField;
        public int basicIntField = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow
        public SubValue objectField;
        public List<String> listOfScalars;
        public Set<String> setOfScalars;
        public List<SubValue> listOfObjects;
        public Set<SubValue> setOfObjects;
        public Map<String, SubValue> simpleMapField;

        // For testing differences between type versions
        public String valueOnlyInTypeState2;
    }

    @HollowTypeName(name = "IntSetTypeState")
    @HollowPrimaryKey(fields = "id")
    public static class IntSetTypeState1 {
        public String id;
        public Set<Integer> intSet;
    }

    @HollowTypeName(name = "IntSetTypeState")
    @HollowPrimaryKey(fields = "id")
    public static class IntSetTypeState2 {
        public String id;
        public Set<Integer> intSet;
    }

    @HollowTypeName(name = "IntTypeState")
    @HollowPrimaryKey(fields = "intA")
    public static class IntTypeState1 {
        public int intA = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow;
        public int intB = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow;
    }

    @HollowTypeName(name = "IntTypeState")
    @HollowPrimaryKey(fields = "intA")
    public static class IntTypeState2 {
        public int intA = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow;
        public int intB = Integer.MIN_VALUE; // MIN_VALUE == null in Hollow;
    }

    @HollowTypeName(name = "RecordWithSubObject")
    @HollowPrimaryKey(fields = "id")
    public static class RecordWithSubObject1 {
        public String id;
        public int intField;
        public RecordSubObject subObject;
    }

    @HollowTypeName(name = "RecordWithSubObject")
    @HollowPrimaryKey(fields = "id")
    public static class RecordWithSubObject2 {
        public String id;
        public int intField;
    }

    @HollowTypeName(name = "RecordSubObject")
    public static class RecordSubObject {
        public String stringField;
        public int intField;
    }

}