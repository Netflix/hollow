package com.netflix.hollow.tools.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.test.model.Movie;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class SearchUtilsTest {

    @Mock
    HollowReadStateEngine stateEngine;

    @Mock
    PrimaryKey primaryKey;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(primaryKey.numFields()).thenReturn(2);
        when(primaryKey.getFieldType(eq(stateEngine), anyInt())).thenReturn(HollowObjectSchema.FieldType.STRING);
    }

    @Test
    public void testParseKey() {
        String keyString = "a:b";
        Object[] key = SearchUtils.parseKey(stateEngine, primaryKey, keyString);
        assertEquals(2, key.length);
        assertEquals("a", key[0]);
        assertEquals("b", key[1]);

        // two fields, where the second field contains a ':' char
        // NOTE that this split based on delimiter works even without escaping the delimiter because
        // string split is performed based on no. of fields in the key. So if delimiter exists in the
        // last field then the parsing logic doesn't break
        keyString = "a:b1:b2";
        key = SearchUtils.parseKey(stateEngine, primaryKey, keyString);
        assertEquals(2, key.length);
        assertEquals("a", key[0]);
        assertEquals("b1:b2", key[1]);

        // again two fields, where the second field contains a ':' char
        keyString = "a:b1\\:b2";
        key = SearchUtils.parseKey(stateEngine, primaryKey, keyString);
        assertEquals(2, key.length);
        assertEquals("a", key[0]);
        assertEquals("b1:b2", key[1]);

        // two fields, where the first field contains a ':' char
        keyString = "a1\\:a2:b";
        key = SearchUtils.parseKey(stateEngine, primaryKey, keyString);
        assertEquals(2, key.length);
        assertEquals("a1:a2", key[0]);
        assertEquals("b", key[1]);
    }

//    @Test
//    public void generateAPI() throws IOException {
//        new HollowAPIGenerator.Builder()
//                .withAPIClassname("MovieAPI")
//                .withPackageName("com.netflix.hollow.test.generated")
//                .withDestination("/Users/xsun/workspace/hollow/hollow/src/test/java/com/netflix/hollow/test/generated")
//                .withDataModel(Movie.class)
//                .build().generateSourceFiles();
//    }

}
