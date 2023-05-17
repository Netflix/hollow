package com.netflix.hollow.core.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.HollowReadStateEngineBuilder;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;

public class HollowSchemaHashTest {

    @HollowPrimaryKey(fields = "id")
    class Movie {
        int id;
        Map<Integer, String> aMap;
    }

    @Test
    public void schemaHashTest() {
        SimpleHollowDataset dataset = SimpleHollowDataset.fromClassDefinitions(Long.class, Movie.class);
        HollowReadStateEngine readState = new HollowReadStateEngineBuilder(Arrays.asList(Long.class, Movie.class)).build();

        HollowSchemaHash hollowSchemaHash1 = new HollowSchemaHash(dataset.getSchemas());
        HollowSchemaHash hollowSchemaHash2 = new HollowSchemaHash(readState.getSchemas());

        assertEquals(hollowSchemaHash1, hollowSchemaHash2);
        assertNotNull(hollowSchemaHash1.getHash());
        assertNotEquals("", hollowSchemaHash1.getHash());
    }
}
