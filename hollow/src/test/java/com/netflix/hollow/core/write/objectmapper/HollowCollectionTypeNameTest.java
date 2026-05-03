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
 *
 */
package com.netflix.hollow.core.write.objectmapper;

import com.netflix.hollow.api.objects.generic.GenericHollowList;
import com.netflix.hollow.api.objects.generic.GenericHollowMap;
import com.netflix.hollow.api.objects.generic.GenericHollowSet;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import org.junit.Assert;
import org.junit.Test;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HollowCollectionTypeNameTest extends AbstractStateEngineTest {

    @Override
    protected void initializeTypeStates() { }

    // -------------------------------------------------------------------------
    // Feature flag: annotations are ignored when flag is off (default)
    // -------------------------------------------------------------------------

    // These tests verify the permanent flag-off behavior: annotations are completely
    // ignored when enableCollectionTypeNaming() has not been called. They serve as
    // regression tests confirming that element/key/value type name wiring is properly
    // gated behind the feature flag.

    @Test
    public void flagOff_listAnnotationIgnored() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(TypeWithAnnotatedList.class);

        HollowListSchema schema = (HollowListSchema) writeStateEngine.getTypeState("ListOfInteger").getSchema();
        Assert.assertEquals("Integer", schema.getElementType());
        Assert.assertNull(writeStateEngine.getTypeState("MovieId"));
    }

    @Test
    public void flagOff_setAnnotationIgnored() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(TypeWithAnnotatedSet.class);

        HollowSetSchema schema = (HollowSetSchema) writeStateEngine.getTypeState("SetOfString").getSchema();
        Assert.assertEquals("String", schema.getElementType());
        Assert.assertNull(writeStateEngine.getTypeState("TagString"));
    }

    @Test
    public void flagOff_mapAnnotationIgnored() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(TypeWithAnnotatedMap.class);

        HollowMapSchema schema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertEquals("String", schema.getValueType());
        Assert.assertNull(writeStateEngine.getTypeState("MapKey"));
        Assert.assertNull(writeStateEngine.getTypeState("MapValue"));
    }

    // -------------------------------------------------------------------------
    // Schema generation with flag on
    // -------------------------------------------------------------------------

    @Test
    public void flagOn_listElementTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithAnnotatedList.class);

        HollowListSchema schema = (HollowListSchema) writeStateEngine.getTypeState("ListOfInteger").getSchema();
        Assert.assertEquals("MovieId", schema.getElementType());
        Assert.assertNotNull("Dedicated 'MovieId' type state should exist",
                writeStateEngine.getTypeState("MovieId"));
        Assert.assertNull("Global 'Integer' type state should NOT exist",
                writeStateEngine.getTypeState("Integer"));
    }

    @Test
    public void flagOn_setElementTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithAnnotatedSet.class);

        HollowSetSchema schema = (HollowSetSchema) writeStateEngine.getTypeState("SetOfString").getSchema();
        Assert.assertEquals("TagString", schema.getElementType());
        Assert.assertNotNull(writeStateEngine.getTypeState("TagString"));
        Assert.assertNull(writeStateEngine.getTypeState("String"));
    }

    @Test
    public void flagOn_mapKeyAndValueTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithAnnotatedMap.class);

        HollowMapSchema schema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("MapKey", schema.getKeyType());
        Assert.assertEquals("MapValue", schema.getValueType());
        Assert.assertNotNull(writeStateEngine.getTypeState("MapKey"));
        Assert.assertNotNull(writeStateEngine.getTypeState("MapValue"));
        Assert.assertNull("Named key/value types should not share global 'String' pool",
                writeStateEngine.getTypeState("String"));
    }

    @Test
    public void flagOn_mapKeyOnlyNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithKeyOnlyAnnotation.class);

        HollowMapSchema schema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("SubTypeKey", schema.getKeyType());
        Assert.assertEquals("String", schema.getValueType());
    }

    @Test
    public void flagOn_mapValueOnlyNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithValueOnlyAnnotation.class);

        HollowMapSchema schema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertEquals("SubTypeVal", schema.getValueType());
        Assert.assertNotNull(writeStateEngine.getTypeState("SubTypeVal"));
    }

    @Test
    public void flagOn_outerTypeNameComposesWithElementTypeName() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithBothAnnotations.class);

        Assert.assertNotNull("Outer type 'MyMovieIds' should exist",
                writeStateEngine.getTypeState("MyMovieIds"));
        HollowListSchema schema = (HollowListSchema) writeStateEngine.getTypeState("MyMovieIds").getSchema();
        Assert.assertEquals("MovieId", schema.getElementType());
    }

    @Test
    public void flagOn_twoFieldsSameNameSameType_shareOneTypeState() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithTwoSameElementNameFields.class);

        Assert.assertNotNull(writeStateEngine.getTypeState("MovieId"));
    }

    // -------------------------------------------------------------------------
    // Write/read roundtrip correctness
    // -------------------------------------------------------------------------

    @Test
    public void flagOn_list_writesAndReadsCorrectly() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();

        TypeWithAnnotatedList obj = new TypeWithAnnotatedList();
        obj.ids = new ArrayList<>(Arrays.asList(10, 20, 30));
        mapper.add(obj);

        roundTripSnapshot();

        Assert.assertNotNull(readStateEngine.getTypeState("MovieId"));
        GenericHollowList list = new GenericHollowList(readStateEngine, "ListOfInteger", 0);
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void flagOn_map_writesAndReadsCorrectly() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();

        TypeWithAnnotatedMap obj = new TypeWithAnnotatedMap();
        obj.data = new HashMap<>();
        obj.data.put("hello", "world");
        obj.data.put("hello", "hollow");
        obj.data.put("hellow", "hollow");
        mapper.add(obj);

        roundTripSnapshot();

        Assert.assertNotNull(readStateEngine.getTypeState("MapKey"));
        Assert.assertNotNull(readStateEngine.getTypeState("MapValue"));
        GenericHollowMap map = new GenericHollowMap(readStateEngine, "MapOfStringToString", 0);
        Assert.assertEquals(2, map.size());
    }

    @Test
    public void flagOn_set_writesAndReadsCorrectly() throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();

        TypeWithAnnotatedSet obj = new TypeWithAnnotatedSet();
        obj.tags = new HashSet<>(Arrays.asList("action", "drama"));
        mapper.add(obj);

        roundTripSnapshot();

        Assert.assertNotNull(readStateEngine.getTypeState("TagString"));
        GenericHollowSet set = new GenericHollowSet(readStateEngine, "SetOfString", 0);
        Assert.assertEquals(2, set.size());
    }

    // -------------------------------------------------------------------------
    // Compression improvement: named type has fewer ordinals than global pool
    // -------------------------------------------------------------------------

    @Test
    public void namedElementType_hasFewerOrdinalsThanGlobalPool() {
        // Model WITHOUT annotation: map keys go into global "String" pool shared with other strings
        HollowWriteStateEngine engineWithout = new HollowWriteStateEngine();
        HollowObjectMapper mapperWithout = new HollowObjectMapper(engineWithout);

        // Model WITH annotation: map keys go into dedicated "MapKey" pool
        HollowWriteStateEngine engineWith = new HollowWriteStateEngine();
        HollowObjectMapper mapperWith = new HollowObjectMapper(engineWith);
        mapperWith.enableCollectionTypeNaming();

        String[] mapKeys = {"action", "drama", "comedy", "thriller", "sci-fi"};

        for (String key : mapKeys) {
            TypeWithGlobalStringPool objWithout = new TypeWithGlobalStringPool();
            objWithout.data = new HashMap<>();
            objWithout.data.put(key, "someValue");
            objWithout.noise = "unrelated-string-" + (key.length() % 500);
            mapperWithout.add(objWithout);

            TypeWithNamedMapKey objWith = new TypeWithNamedMapKey();
            objWith.data = new HashMap<>();
            objWith.data.put(key, "someValue");
            objWith.noise = "unrelated-string-" + (key.length() % 500);
            mapperWith.add(objWith);
        }
        // Populate 5000 additional strings into the global pool
        for (int i = 0; i < 5000; i++) {
            TypeWithGlobalStringPool obj = new TypeWithGlobalStringPool();
            obj.data = new HashMap<>();
            obj.noise = "unrelated-string-" + i;
            mapperWithout.add(obj);

            TypeWithNamedMapKey obj2 = new TypeWithNamedMapKey();
            obj2.data = new HashMap<>();
            obj2.noise = "unrelated-string-" + i;
            mapperWith.add(obj2);
        }

        int globalStringPoolSize = engineWithout.getTypeState("String").getPopulatedBitSet().cardinality();
        int namedKeyPoolSize = engineWith.getTypeState("MapKey").getPopulatedBitSet().cardinality();

        Assert.assertTrue(namedKeyPoolSize < globalStringPoolSize);
        Assert.assertEquals(mapKeys.length, namedKeyPoolSize);
    }

    // -------------------------------------------------------------------------
    // Validation / error tests (flag on)
    // -------------------------------------------------------------------------

    @Test(expected = IllegalStateException.class)
    public void flagOn_mapAnnotationOnListField_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithMapAnnotationOnList.class);
    }

    @Test(expected = IllegalStateException.class)
    public void flagOn_mapAnnotationOnSetField_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithMapAnnotationOnSet.class);
    }

    @Test(expected = IllegalStateException.class)
    public void flagOn_collectionAnnotationOnMapField_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithCollectionAnnotationOnMap.class);
    }

    @Test(expected = IllegalStateException.class)
    public void flagOn_collectionAnnotationOnStringField_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithCollectionAnnotationOnString.class);
    }

    @Test(expected = IllegalStateException.class)
    public void flagOn_sameNameDifferentJavaTypes_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithConflictingTypeNames.class);
    }

    // -------------------------------------------------------------------------
    // Helper model classes
    // -------------------------------------------------------------------------

    static class TypeWithAnnotatedList {
        @HollowCollectionTypeName(elementTypeName = "MovieId")
        List<Integer> ids;
    }

    static class TypeWithAnnotatedSet {
        @HollowCollectionTypeName(elementTypeName = "TagString")
        Set<String> tags;
    }

    static class TypeWithAnnotatedMap {
        @HollowMapTypeName(keyTypeName = "MapKey", valueTypeName = "MapValue")
        Map<String, String> data;
    }

    static class TypeWithKeyOnlyAnnotation {
        @HollowMapTypeName(keyTypeName = "SubTypeKey")
        Map<String, String> data;
    }

    static class TypeWithValueOnlyAnnotation {
        @HollowMapTypeName(valueTypeName = "SubTypeVal")
        Map<String, String> data;
    }

    static class TypeWithBothAnnotations {
        @HollowTypeName(name = "MyMovieIds")
        @HollowCollectionTypeName(elementTypeName = "MovieId")
        List<Integer> ids;
    }

    static class TypeWithTwoSameElementNameFields {
        @HollowCollectionTypeName(elementTypeName = "MovieId")
        List<Integer> primaryIds;

        @HollowCollectionTypeName(elementTypeName = "MovieId")
        Set<Integer> secondaryIds;
    }

    static class TypeWithMapAnnotationOnList {
        @HollowMapTypeName(keyTypeName = "K")
        List<Integer> ids;
    }

    static class TypeWithMapAnnotationOnSet {
        @HollowMapTypeName(keyTypeName = "K")
        Set<String> tags;
    }

    static class TypeWithCollectionAnnotationOnMap {
        @HollowCollectionTypeName(elementTypeName = "Elem")
        Map<String, String> data;
    }

    static class TypeWithCollectionAnnotationOnString {
        @HollowCollectionTypeName(elementTypeName = "SomeName")
        String value;
    }

    static class TypeWithConflictingTypeNames {
        @HollowCollectionTypeName(elementTypeName = "Conflict")
        List<Integer> intIds;

        @HollowCollectionTypeName(elementTypeName = "Conflict")
        List<String> strNames;
    }

    static class TypeWithGlobalStringPool {
        Map<String, String> data;
        String noise;
    }

    static class TypeWithNamedMapKey {
        @HollowMapTypeName(keyTypeName = "MapKey")
        Map<String, String> data;
        String noise;
    }

    // -------------------------------------------------------------------------
    // Hash key interaction
    // -------------------------------------------------------------------------

    @Test
    public void flagOn_hashKeyOnNamedElementType_worksUnchanged() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithHashKeyOnNamedSet.class);

        // The Set schema should reference the named element type
        HollowSetSchema setSchema = (HollowSetSchema) writeStateEngine.getTypeState("SetOfString").getSchema();
        Assert.assertEquals("TagString", setSchema.getElementType());

        // The hash key field path "value" is preserved in the schema
        Assert.assertNotNull(setSchema.getHashKey());
        Assert.assertEquals(1, setSchema.getHashKey().numFields());
        Assert.assertEquals("value", setSchema.getHashKey().getFieldPath(0));
    }

    @Test
    public void flagOn_hashKeyOnNamedMapKeyType_worksUnchanged() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithHashKeyOnNamedMap.class);

        HollowMapSchema mapSchema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("SubTypeKey", mapSchema.getKeyType());

        Assert.assertNotNull(mapSchema.getHashKey());
        Assert.assertEquals(1, mapSchema.getHashKey().numFields());
        Assert.assertEquals("value", mapSchema.getHashKey().getFieldPath(0));
    }

    static class TypeWithHashKeyOnNamedSet {
        @HollowHashKey(fields = "value")
        @HollowCollectionTypeName(elementTypeName = "TagString")
        Set<String> tags;
    }

    static class TypeWithHashKeyOnNamedMap {
        @HollowHashKey(fields = "value")
        @HollowMapTypeName(keyTypeName = "SubTypeKey")
        Map<String, String> data;
    }

    // -------------------------------------------------------------------------
    // Schema equivalence: annotation vs wrapper-POJO workaround
    // -------------------------------------------------------------------------

    @Test
    public void annotationProducesSameSchemaAsWrapperPojoWorkaround() {
        // --- Wrapper-POJO approach (old workaround) ---
        HollowWriteStateEngine pojoEngine = new HollowWriteStateEngine();
        HollowObjectMapper pojoMapper = new HollowObjectMapper(pojoEngine);
        pojoMapper.initializeTypeState(TypeUsingWrapperPojo.class);

        // --- Annotation approach (new feature) ---
        HollowWriteStateEngine annotationEngine = new HollowWriteStateEngine();
        HollowObjectMapper annotationMapper = new HollowObjectMapper(annotationEngine);
        annotationMapper.enableCollectionTypeNaming();
        annotationMapper.initializeTypeState(TypeUsingAnnotation.class);

        // Both produce a map whose key type is "SubTypeKey"
        HollowMapSchema pojoMapSchema = (HollowMapSchema) pojoEngine.getTypeState("MapOfSubTypeKeyToString").getSchema();
        HollowMapSchema annotationMapSchema = (HollowMapSchema) annotationEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("SubTypeKey", pojoMapSchema.getKeyType());
        Assert.assertEquals("SubTypeKey", annotationMapSchema.getKeyType());

        // The SubTypeKey object schema is structurally identical: one field "value" of type STRING
        HollowObjectSchema pojoKeySchema = (HollowObjectSchema) pojoEngine.getTypeState("SubTypeKey").getSchema();
        HollowObjectSchema annotationKeySchema = (HollowObjectSchema) annotationEngine.getTypeState("SubTypeKey").getSchema();
        Assert.assertEquals(pojoKeySchema, annotationKeySchema);
    }

    // Wrapper-POJO workaround: a single-field class with @HollowInline
    @HollowTypeName(name = "SubTypeKey")
    static class SubTypeKeyPojo {
        @HollowInline String value;
    }

    static class TypeUsingWrapperPojo {
        Map<SubTypeKeyPojo, String> data;
    }

    // New annotation approach: annotate the Map field directly
    static class TypeUsingAnnotation {
        @HollowMapTypeName(keyTypeName = "SubTypeKey")
        Map<String, String> data;
    }

    // -------------------------------------------------------------------------
    // Migration: custom @HollowTypeName pins outer name across workaround→annotation
    // -------------------------------------------------------------------------

    @Test
    public void migration_customOuterTypeName_preservedWhenSwitchingFromWrapperPojo() {
        // Wrapper-POJO approach with a custom outer collection name
        HollowWriteStateEngine pojoEngine = new HollowWriteStateEngine();
        HollowObjectMapper pojoMapper = new HollowObjectMapper(pojoEngine);
        pojoMapper.initializeTypeState(TypeUsingWrapperPojoWithCustomName.class);

        // Annotation approach with the same custom outer collection name
        HollowWriteStateEngine annotationEngine = new HollowWriteStateEngine();
        HollowObjectMapper annotationMapper = new HollowObjectMapper(annotationEngine);
        annotationMapper.enableCollectionTypeNaming();
        annotationMapper.initializeTypeState(TypeUsingAnnotationWithCustomName.class);

        // Both produce the same outer collection name
        Assert.assertNotNull(pojoEngine.getTypeState("MyCustomMap"));
        Assert.assertNotNull(annotationEngine.getTypeState("MyCustomMap"));

        // Both produce identical map schemas (key type, value type, outer name)
        HollowMapSchema pojoSchema = (HollowMapSchema) pojoEngine.getTypeState("MyCustomMap").getSchema();
        HollowMapSchema annotationSchema = (HollowMapSchema) annotationEngine.getTypeState("MyCustomMap").getSchema();
        Assert.assertEquals(pojoSchema.getKeyType(), annotationSchema.getKeyType());
        Assert.assertEquals(pojoSchema.getValueType(), annotationSchema.getValueType());
    }

    static class TypeUsingWrapperPojoWithCustomName {
        @HollowTypeName(name = "MyCustomMap")
        Map<SubTypeKeyPojo, String> data;
    }

    static class TypeUsingAnnotationWithCustomName {
        @HollowTypeName(name = "MyCustomMap")
        @HollowMapTypeName(keyTypeName = "SubTypeKey")
        Map<String, String> data;
    }

    // -------------------------------------------------------------------------
    // End-to-end migration: workaround cycle → annotation cycle, no schema change
    // -------------------------------------------------------------------------

    @Test
    public void migration_endToEnd_noSchemaChange() throws IOException {
        // --- Cycle 1: wrapper-POJO workaround (no @HollowTypeName on the collection fields) ---
        HollowObjectMapper cycle1Mapper = new HollowObjectMapper(writeStateEngine);
        TypeUsingWrapperPojoAutoName obj1 = new TypeUsingWrapperPojoAutoName();

        obj1.mapData = new HashMap<>();
        SubTypeKeyPojoAutoName key = new SubTypeKeyPojoAutoName();
        key.value = "hello";
        obj1.mapData.put(key, "world");

        obj1.listData = new ArrayList<>();
        SubTypeElemPojoAutoName elem = new SubTypeElemPojoAutoName();
        elem.value = "item1";
        obj1.listData.add(elem);

        obj1.setData = new HashSet<>();
        SubTypeElemPojoAutoName setElem = new SubTypeElemPojoAutoName();
        setElem.value = "tag1";
        obj1.setData.add(setElem);

        cycle1Mapper.add(obj1);
        roundTripSnapshot();

        // Verify cycle 1 schemas — auto-generated names include the element types
        Assert.assertEquals("SubTypeKeyAutoName",
                ((HollowMapSchema) readStateEngine.getTypeState("MapOfSubTypeKeyAutoNameToString").getSchema()).getKeyType());
        Assert.assertEquals("SubTypeElemAutoName",
                ((HollowListSchema) readStateEngine.getTypeState("ListOfSubTypeElemAutoName").getSchema()).getElementType());
        Assert.assertEquals("SubTypeElemAutoName",
                ((HollowSetSchema) readStateEngine.getTypeState("SetOfSubTypeElemAutoName").getSchema()).getElementType());

        // --- Cycle 2: annotation approach ---
        // @HollowTypeName on each field pins the outer collection name to match cycle 1's auto-generated name.
        // @HollowMapTypeName / @HollowCollectionTypeName match the POJO's @HollowTypeName names.
        HollowObjectMapper cycle2Mapper = new HollowObjectMapper(writeStateEngine);
        cycle2Mapper.enableCollectionTypeNaming();
        TypeUsingAnnotationAutoName obj2 = new TypeUsingAnnotationAutoName();
        obj2.mapData = new HashMap<>();
        obj2.mapData.put("hello", "world");
        obj2.listData = new ArrayList<>(Arrays.asList("item1"));
        obj2.setData = new HashSet<>(Arrays.asList("tag1"));
        cycle2Mapper.add(obj2);
        roundTripDelta();

        // The collection and element schemas are unchanged — consumers see no schema change.
        // (In a real migration the container class has the same name too; here we use two
        // different Java classes to simulate the before/after state, so we assert only the
        // schemas that consumers of the collection depend on.)
        Assert.assertEquals(
                readStateEngine.getTypeState("MapOfSubTypeKeyAutoNameToString").getSchema(),
                writeStateEngine.getTypeState("MapOfSubTypeKeyAutoNameToString").getSchema());
        Assert.assertEquals(
                readStateEngine.getTypeState("SubTypeKeyAutoName").getSchema(),
                writeStateEngine.getTypeState("SubTypeKeyAutoName").getSchema());

        Assert.assertEquals(
                readStateEngine.getTypeState("ListOfSubTypeElemAutoName").getSchema(),
                writeStateEngine.getTypeState("ListOfSubTypeElemAutoName").getSchema());
        Assert.assertEquals(
                readStateEngine.getTypeState("SubTypeElemAutoName").getSchema(),
                writeStateEngine.getTypeState("SubTypeElemAutoName").getSchema());

        Assert.assertEquals(
                readStateEngine.getTypeState("SetOfSubTypeElemAutoName").getSchema(),
                writeStateEngine.getTypeState("SetOfSubTypeElemAutoName").getSchema());

        // Data reads back correctly after migration
        Assert.assertEquals(1, new GenericHollowMap(readStateEngine, "MapOfSubTypeKeyAutoNameToString", 0).size());
        Assert.assertEquals(1, new GenericHollowList(readStateEngine, "ListOfSubTypeElemAutoName", 0).size());
        Assert.assertEquals(1, new GenericHollowSet(readStateEngine, "SetOfSubTypeElemAutoName", 0).size());
    }

    @HollowTypeName(name = "SubTypeKeyAutoName")
    static class SubTypeKeyPojoAutoName {
        @HollowInline String value;
    }

    @HollowTypeName(name = "SubTypeElemAutoName")
    static class SubTypeElemPojoAutoName {
        @HollowInline String value;
    }

    static class TypeUsingWrapperPojoAutoName {
        Map<SubTypeKeyPojoAutoName, String> mapData;
        List<SubTypeElemPojoAutoName> listData;
        Set<SubTypeElemPojoAutoName> setData;
    }

    static class TypeUsingAnnotationAutoName {
        @HollowTypeName(name = "MapOfSubTypeKeyAutoNameToString")
        @HollowMapTypeName(keyTypeName = "SubTypeKeyAutoName")
        Map<String, String> mapData;

        @HollowTypeName(name = "ListOfSubTypeElemAutoName")
        @HollowCollectionTypeName(elementTypeName = "SubTypeElemAutoName")
        List<String> listData;

        @HollowTypeName(name = "SetOfSubTypeElemAutoName")
        @HollowCollectionTypeName(elementTypeName = "SubTypeElemAutoName")
        Set<String> setData;
    }

    // -------------------------------------------------------------------------
    // Concrete collection subtypes (ArrayList, LinkedList, HashSet, HashMap, TreeMap)
    // isAssignableFrom fix: concrete implementations were previously rejected
    // -------------------------------------------------------------------------

    @Test
    public void flagOn_arrayList_elementTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithArrayList.class);

        HollowListSchema schema = (HollowListSchema) writeStateEngine.getTypeState("ListOfInteger").getSchema();
        Assert.assertEquals("ConcreteElem", schema.getElementType());
        Assert.assertNotNull(writeStateEngine.getTypeState("ConcreteElem"));
    }

    @Test
    public void flagOn_linkedList_elementTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithLinkedList.class);

        HollowListSchema schema = (HollowListSchema) writeStateEngine.getTypeState("ListOfInteger").getSchema();
        Assert.assertEquals("LinkedElem", schema.getElementType());
        Assert.assertNotNull(writeStateEngine.getTypeState("LinkedElem"));
    }

    @Test
    public void flagOn_hashSet_elementTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithHashSet.class);

        HollowSetSchema schema = (HollowSetSchema) writeStateEngine.getTypeState("SetOfString").getSchema();
        Assert.assertEquals("ConcreteSetElem", schema.getElementType());
        Assert.assertNotNull(writeStateEngine.getTypeState("ConcreteSetElem"));
    }

    @Test
    public void flagOn_hashMap_keyAndValueTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithHashMap.class);

        HollowMapSchema schema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("ConcreteKey", schema.getKeyType());
        Assert.assertEquals("ConcreteValue", schema.getValueType());
        Assert.assertNotNull(writeStateEngine.getTypeState("ConcreteKey"));
        Assert.assertNotNull(writeStateEngine.getTypeState("ConcreteValue"));
    }

    @Test
    public void flagOn_treeMap_keyTypeNamed() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithTreeMap.class);

        HollowMapSchema schema = (HollowMapSchema) writeStateEngine.getTypeState("MapOfStringToString").getSchema();
        Assert.assertEquals("TreeKey", schema.getKeyType());
        Assert.assertNotNull(writeStateEngine.getTypeState("TreeKey"));
    }

    // Validation still fires correctly for concrete types

    @Test(expected = IllegalStateException.class)
    public void flagOn_mapAnnotationOnArrayList_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithMapAnnotationOnArrayList.class);
    }

    @Test(expected = IllegalStateException.class)
    public void flagOn_collectionAnnotationOnHashMap_throws() {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.enableCollectionTypeNaming();
        mapper.initializeTypeState(TypeWithCollectionAnnotationOnHashMap.class);
    }

    static class TypeWithArrayList {
        @HollowCollectionTypeName(elementTypeName = "ConcreteElem")
        ArrayList<Integer> ids;
    }

    static class TypeWithLinkedList {
        @HollowCollectionTypeName(elementTypeName = "LinkedElem")
        LinkedList<Integer> ids;
    }

    static class TypeWithHashSet {
        @HollowCollectionTypeName(elementTypeName = "ConcreteSetElem")
        HashSet<String> tags;
    }

    static class TypeWithHashMap {
        @HollowMapTypeName(keyTypeName = "ConcreteKey", valueTypeName = "ConcreteValue")
        HashMap<String, String> data;
    }

    static class TypeWithTreeMap {
        @HollowMapTypeName(keyTypeName = "TreeKey")
        TreeMap<String, String> data;
    }

    static class TypeWithMapAnnotationOnArrayList {
        @HollowMapTypeName(keyTypeName = "K")
        ArrayList<Integer> ids;
    }

    static class TypeWithCollectionAnnotationOnHashMap {
        @HollowCollectionTypeName(elementTypeName = "E")
        HashMap<String, String> data;
    }
}
