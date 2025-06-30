package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NullPrimaryKeyFieldValidatorTest {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void testValidPrimaryKey() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new NullPrimaryKeyFieldValidator(TypeWithSinglePrimaryKey.class)).build();
        try {
            producer.runCycle(state -> {
                TypeWithSinglePrimaryKey nullSinglePrimaryKey = new TypeWithSinglePrimaryKey(1);
                state.add(nullSinglePrimaryKey);
            });
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testInvalidNoSchema() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new NullPrimaryKeyFieldValidator(TypeWithSinglePrimaryKey.class)).build();
        try {
            producer.runCycle(state -> {
                state.add("hello");
            });
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("NullPrimaryKeyFieldValidator defined for data type TypeWithSinglePrimaryKey "+
                            "but schema not found. Please check that the HollowProducer is initialized "+
                            "with the data type's schema (see initializeDataModel)",
                    expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }

    @Test
    public void testInvalidNoPrimaryKey() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new NullPrimaryKeyFieldValidator("TypeWithoutPrimaryKey")).build();
        try {
            producer.runCycle(state -> {
                state.add(new TypeWithoutPrimaryKey(1));
            });
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("NullPrimaryKeyFieldValidator defined but unable to find primary key "+
                            "for data type TypeWithoutPrimaryKey. Please check schema definition.",
                    expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }

    @Test
    public void testInvalidNullSinglePrimaryKey() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new NullPrimaryKeyFieldValidator(TypeWithSinglePrimaryKey.class)).build();
        try {
            producer.runCycle(state -> {
                TypeWithSinglePrimaryKey nullSinglePrimaryKey = new TypeWithSinglePrimaryKey(null);
                state.add(nullSinglePrimaryKey);
            });
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Null primary key fields found for type TypeWithSinglePrimaryKey. "+
                    "Primary Key in schema is [id]. Null records: [(ordinal=0, key=[null])]",
                    expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }

    @Test
    public void testInvalidNullMultiPrimaryKeys() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new NullPrimaryKeyFieldValidator(TypeWithMultiplePrimaryKeys.class)).build();
        try {
            producer.runCycle(state -> {
                TypeWithMultiplePrimaryKeys nullMultiPrimaryKey = new TypeWithMultiplePrimaryKeys(1, null);
                state.add(nullMultiPrimaryKey);
            });
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Null primary key fields found for type TypeWithMultiplePrimaryKeys. "+
                            "Primary Key in schema is [id, name]. Null records: [(ordinal=0, key=[1, null])]",
                    expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }

    @Test
    public void testInvalidNullNestedPrimaryKey() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new NullPrimaryKeyFieldValidator(TypeWithNestedPrimaryKey.class)).build();

        // Case where the referenced object itself is null.
        try {
            producer.runCycle(state -> {
                TypeWithNestedPrimaryKey nestedPrimaryKey = new TypeWithNestedPrimaryKey(null);
                state.add(nestedPrimaryKey);
            });
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Null primary key fields found for type TypeWithNestedPrimaryKey. "+
                            "Primary Key in schema is [nested.id]. Null records: [(ordinal=0, key=[null])]",
                    expected.getValidationStatus().getResults().get(0).getMessage());
        }

        // Case where the nested object key field is null.
        try {
            producer.runCycle(state -> {
                TypeWithNestedPrimaryKey nestedPrimaryKey = new TypeWithNestedPrimaryKey(new TypeWithoutPrimaryKey(null));
                state.add(nestedPrimaryKey);
            });
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof ValidationStatusException);
            ValidationStatusException expected = (ValidationStatusException) e;
            assertEquals(1, expected.getValidationStatus().getResults().size());
            assertEquals("Null primary key fields found for type TypeWithNestedPrimaryKey. "+
                            "Primary Key in schema is [nested.id]. Null records: [(ordinal=0, key=[null])]",
                    expected.getValidationStatus().getResults().get(0).getMessage());
        }
    }


    @HollowPrimaryKey(fields = "id")
    static class TypeWithSinglePrimaryKey {
        private final Integer id;

        public TypeWithSinglePrimaryKey(Integer id) {
            this.id = id;
        }
    }

    @HollowPrimaryKey(fields = {"id", "name"})
    static class TypeWithMultiplePrimaryKeys {
        private final Integer id;
        private final String name;

        public TypeWithMultiplePrimaryKeys(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @HollowPrimaryKey(fields = {"nested.id"})
    static class TypeWithNestedPrimaryKey {
        private final TypeWithoutPrimaryKey nested;

        public TypeWithNestedPrimaryKey(TypeWithoutPrimaryKey nested) {
            this.nested = nested;
        }
    }

    static class TypeWithoutPrimaryKey {
        private final Integer id;

        public TypeWithoutPrimaryKey(Integer id) {
            this.id = id;
        }
    }
}
