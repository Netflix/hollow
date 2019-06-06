package com.netflix.hollow.core.index;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.HollowReadStateEngineBuilder;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

// TODO(timt): incorporate into HollowPrimaryKeyIndexTest (or other appropriate test)
@RunWith(Parameterized.class)
public class Issue399 {
    private final long id1;
    private final long id2;
    private final long id3;

    @Parameters(name = "{0} | {1} | {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {                   5L, 0L, 3L },
                { 9223372034562340851L, 0L, 3L },
                { 9223372034562340851L, 1L, 3L }
        });
    }

    public Issue399(long id1, long id2, long id3) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
    }

    @Test
    public void test() {
        HollowReadStateEngine readStateEngine = new HollowReadStateEngineBuilder()
                .add(new TypeA(id1, "The Matrix", 1999))
                .add(new TypeA(id2, "Beasts of No Nation", 2015))
                .add(new TypeA(id3, "Pulp Fiction", 1994))
                .build();
        PrimaryKey key = ((HollowObjectSchema) readStateEngine.getSchema("TypeA")).getPrimaryKey();

        HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(readStateEngine, key);

        assertEquals("The Matrix", findMatch(readStateEngine, index, id1).getObject("a2").getString("value"));
        assertEquals("Beasts of No Nation", findMatch(readStateEngine, index, id2).getObject("a2").getString("value"));
        assertEquals("Pulp Fiction", findMatch(readStateEngine, index, id3).getObject("a2").getString("value"));
    }

    private GenericHollowObject findMatch(HollowReadStateEngine readStateEngine, HollowPrimaryKeyIndex index, long id) {
        int ordinal = index.getMatchingOrdinal(id);
        GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TypeA", ordinal);
        return obj;
    }

    @HollowPrimaryKey(fields={"a1"})
    private static final class TypeA {
        long a1;
        String a2;
        int a3;

        public TypeA(long a1, String a2, int a3) {
            this.a1 = a1;
            this.a2 = a2;
            this.a3 = a3;
        }
    }
}
