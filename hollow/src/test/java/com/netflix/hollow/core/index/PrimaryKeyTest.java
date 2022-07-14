/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class PrimaryKeyTest {

    HollowWriteStateEngine writeEngine;

    @Before
    public void setUp() {
        writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        mapper.initializeTypeState(TypeWithTraversablePrimaryKey.class);
    }

    @Test
    public void automaticallyTraversesSomeIncompletelyDefinedFieldPaths() {
        HollowObjectSchema schema = (HollowObjectSchema) writeEngine.getTypeState("TypeWithTraversablePrimaryKey").getSchema();
        PrimaryKey traversablePrimaryKey = schema.getPrimaryKey();

        Assert.assertEquals(2, traversablePrimaryKey.getFieldPathIndex(writeEngine, 0).length);
        Assert.assertEquals(3, traversablePrimaryKey.getFieldPathIndex(writeEngine, 1).length);
        Assert.assertEquals(1, traversablePrimaryKey.getFieldPathIndex(writeEngine, 2).length);

        PrimaryKey anotherTraversablePrimaryKey = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType.id");
        Assert.assertEquals(3, anotherTraversablePrimaryKey.getFieldPathIndex(writeEngine, 0).length);

        PrimaryKey hardStopPrimaryKey = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType.id!");
        Assert.assertEquals(2, hardStopPrimaryKey.getFieldPathIndex(writeEngine, 0).length);

        PrimaryKey hardStopPrimaryKey2 = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType2!");
        Assert.assertEquals(1, hardStopPrimaryKey2.getFieldPathIndex(writeEngine, 0).length);

        PrimaryKey hardStopPrimaryKey3 = new PrimaryKey("TypeWithTraversablePrimaryKey", "strList!");
        Assert.assertEquals(1, hardStopPrimaryKey3.getFieldPathIndex(writeEngine, 0).length);
    }

    @Test
    public void throwsMeaningfulExceptions() {
        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType.nofield");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
            Assert.fail("IllegalArgumentException expected");
        } catch (FieldPaths.FieldPathException expected) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_FOUND, expected.error);
            Assert.assertEquals(1, expected.fieldSegments.size());
            Assert.assertEquals(1, expected.segmentIndex);
            Assert.assertEquals("SubTypeWithTraversablePrimaryKey", expected.enclosingSchema.getName());
        }

        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType.id.value.alldone");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
            Assert.fail("IllegalArgumentException expected");
        } catch (FieldPaths.FieldPathException expected) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_TRAVERSABLE, expected.error);
            Assert.assertEquals(3, expected.fieldSegments.size());
            Assert.assertEquals(2, expected.segmentIndex);
            Assert.assertEquals("value", expected.fieldSegments.get(2).getName());
            Assert.assertEquals("String", expected.enclosingSchema.getName());
        }

        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType2");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
            Assert.fail("IllegalArgumentException expected");
        } catch (FieldPaths.FieldPathException expected) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_EXPANDABLE, expected.error);
            Assert.assertEquals(1, expected.fieldSegments.size());
            Assert.assertEquals("subType2", expected.fieldSegments.get(0).getName());
            Assert.assertEquals("SubTypeWithNonTraversablePrimaryKey", expected.enclosingSchema.getName());
        }

        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "strList.element.value");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
            Assert.fail("IllegalArgumentException expected");
        } catch (FieldPaths.FieldPathException expected) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_TRAVERSABLE, expected.error);
            Assert.assertEquals(1, expected.fieldSegments.size());
            Assert.assertEquals(1, expected.segmentIndex);
            Assert.assertEquals("element", expected.segments[expected.segmentIndex]);
            Assert.assertEquals("ListOfString", expected.enclosingSchema.getName());
        }

        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("UnknownType", "id");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
            Assert.fail("IllegalArgumentException expected");
        } catch (FieldPaths.FieldPathException expected) {
            Assert.assertEquals(FieldPaths.FieldPathException.ErrorKind.NOT_BINDABLE, expected.error);
            Assert.assertEquals(0, expected.fieldSegments.size());
            Assert.assertEquals(0, expected.segmentIndex);
            Assert.assertEquals("UnknownType", expected.rootType);
        }
    }


    @Test
    public void testAutoExpand() {
        { // verify fieldPath auto expand
            PrimaryKey autoExpandPK = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType");
            Assert.assertEquals(FieldType.STRING, autoExpandPK.getFieldType(writeEngine, 0));
            Assert.assertEquals(null, autoExpandPK.getFieldSchema(writeEngine, 0));
        }

        { // verify disabled fieldPath auto expand with ending "!" 
            PrimaryKey autoExpandPK = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType!");
            Assert.assertNotEquals(FieldType.STRING, autoExpandPK.getFieldType(writeEngine, 0));
            Assert.assertEquals(FieldType.REFERENCE, autoExpandPK.getFieldType(writeEngine, 0));
            Assert.assertEquals("SubTypeWithTraversablePrimaryKey", autoExpandPK.getFieldSchema(writeEngine, 0).getName());
        }
    }


    @HollowPrimaryKey(fields = {"pk1", "subType", "intId"})
    private static class TypeWithTraversablePrimaryKey {
        String pk1;
        SubTypeWithTraversablePrimaryKey subType;
        SubTypeWithNonTraversablePrimaryKey subType2;
        int intId;
        List<String> strList;
    }

    @HollowPrimaryKey(fields = "id")
    private static class SubTypeWithTraversablePrimaryKey {
        String id;
        int anotherField;
    }

    @HollowPrimaryKey(fields = {"id1", "id2"})
    private static class SubTypeWithNonTraversablePrimaryKey {
        long id1;
        float id2;
    }

}
