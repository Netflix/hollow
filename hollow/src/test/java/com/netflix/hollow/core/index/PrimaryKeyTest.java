package com.netflix.hollow.core.index;

import java.util.List;
import org.junit.Before;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import org.junit.Assert;
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
        } catch(IllegalArgumentException expected) {
            Assert.assertEquals("Invalid field path declaration for type TypeWithTraversablePrimaryKey: subType.nofield.  " +
                                "At element 1, the field nofield was not found in type SubTypeWithTraversablePrimaryKey.",
                                expected.getMessage());
        }
        
        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType.id.value.alldone");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
        } catch(IllegalArgumentException expected) {
            Assert.assertEquals("Invalid field path declaration for type TypeWithTraversablePrimaryKey: subType.id.value.alldone.  " +
                                "No available traversal after element 2: value.",
                                expected.getMessage());
        }
        
        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "subType2");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
        } catch(IllegalArgumentException expected) {
            Assert.assertEquals("Invalid field path declaration for type TypeWithTraversablePrimaryKey: subType2.  " +
                                "This path ends in a REFERENCE field which is not auto-traversable.  " +
                                "If this is intended to actually indicate a REFERENCE field, specify the field path as \"subType2!\".",
                                expected.getMessage());
        }
        
        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("TypeWithTraversablePrimaryKey", "strList.element.value");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
        } catch(IllegalArgumentException expected) {
            Assert.assertEquals("Invalid field path declaration for type TypeWithTraversablePrimaryKey: strList.element.value.  " +
                                "Field paths may only traverse through OBJECT types, but this declaration passes through a LIST type (ListOfString).",
                                expected.getMessage());
        }
        
        try {
            PrimaryKey invalidFieldDefinition = new PrimaryKey("UnknownType", "id");
            invalidFieldDefinition.getFieldPathIndex(writeEngine, 0);
        } catch(IllegalArgumentException expected) {
            Assert.assertEquals("Invalid field path declaration for type UnknownType: id.  The type UnknownType is unavailable.",
                                expected.getMessage());
        }
    }
    
    
    @HollowPrimaryKey(fields={"pk1", "subType", "intId"})
    private static class TypeWithTraversablePrimaryKey {
        String pk1;
        SubTypeWithTraversablePrimaryKey subType;
        SubTypeWithNonTraversablePrimaryKey subType2;
        int intId;
        List<String> strList;
    }
    
    @HollowPrimaryKey(fields="id")
    private static class SubTypeWithTraversablePrimaryKey {
        String id;
        int anotherField;
    }
    
    @HollowPrimaryKey(fields={"id1", "id2"})
    private static class SubTypeWithNonTraversablePrimaryKey {
        long id1;
        float id2;
    }

}
