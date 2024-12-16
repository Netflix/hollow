package com.netflix.hollow.core.write.objectmapper;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HollowObjectMapperHollowRecordParserTest {
  private HollowObjectMapper mapper;

  @Before
  public void setUp() {
    mapper = new HollowObjectMapper(new HollowWriteStateEngine());
    mapper.initializeTypeState(TypeWithAllSimpleTypes.class);
    mapper.initializeTypeState(InternalTypeA.class);
    mapper.initializeTypeState(TypeWithCollections.class);
    mapper.initializeTypeState(VersionedType2.class);
    mapper.initializeTypeState(SpecialWrapperTypesTest.class);
  }

  @Test
  public void testSpecialWrapperTypes() {
    SpecialWrapperTypesTest wrapperTypesTest = new SpecialWrapperTypesTest();
    wrapperTypesTest.id = 8797182L;
    wrapperTypesTest.type = AnEnum.SOME_VALUE_C;
    wrapperTypesTest.complexEnum = ComplexEnum.SOME_VALUE_A;
    wrapperTypesTest.dateCreated = new Date();

    HollowReadStateEngine stateEngine = createReadStateEngine(wrapperTypesTest);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "SpecialWrapperTypesTest", 0);
    SpecialWrapperTypesTest result = mapper.readHollowRecord(obj);

    Assert.assertEquals(wrapperTypesTest, result);
    Assert.assertEquals(wrapperTypesTest.complexEnum.value, result.complexEnum.value);
    Assert.assertEquals(wrapperTypesTest.complexEnum.anotherValue, result.complexEnum.anotherValue);
  }

  @Test
  public void testNullableSpecialWrapperTypes() {
    SpecialWrapperTypesTest wrapperTypesTest = new SpecialWrapperTypesTest();
    wrapperTypesTest.id = 8797182L;
    wrapperTypesTest.type = AnEnum.SOME_VALUE_C;

    HollowReadStateEngine stateEngine = createReadStateEngine(wrapperTypesTest);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "SpecialWrapperTypesTest", 0);
    SpecialWrapperTypesTest result = mapper.readHollowRecord(obj);

    Assert.assertEquals(wrapperTypesTest, result);
    Assert.assertNull(wrapperTypesTest.complexEnum);
    Assert.assertNull(wrapperTypesTest.dateCreated);
  }

  @Test
  public void testSimpleTypes() {
    TypeWithAllSimpleTypes typeWithAllSimpleTypes = new TypeWithAllSimpleTypes();
    typeWithAllSimpleTypes.boxedIntegerField = 1;
    typeWithAllSimpleTypes.boxedBooleanField = true;
    typeWithAllSimpleTypes.boxedDoubleField = 1.0;
    typeWithAllSimpleTypes.boxedFloatField = 1.0f;
    typeWithAllSimpleTypes.boxedLongField = 1L;
    typeWithAllSimpleTypes.boxedShortField = (short) 1;
    typeWithAllSimpleTypes.boxedByteField = (byte) 1;
    typeWithAllSimpleTypes.boxedCharField = 'a';
    typeWithAllSimpleTypes.primitiveIntegerField = 2;
    typeWithAllSimpleTypes.primitiveBooleanField = false;
    typeWithAllSimpleTypes.primitiveDoubleField = 2.0;
    typeWithAllSimpleTypes.primitiveFloatField = 2.0f;
    typeWithAllSimpleTypes.primitiveLongField = 2L;
    typeWithAllSimpleTypes.primitiveShortField = (short) 2;
    typeWithAllSimpleTypes.primitiveByteField = (byte) 2;
    typeWithAllSimpleTypes.primitiveCharField = 'b';
    typeWithAllSimpleTypes.byteArrayField = new byte[]{1, 2, 3};
    typeWithAllSimpleTypes.stringField = "string";
    typeWithAllSimpleTypes.inlinedIntegerField = 3;
    typeWithAllSimpleTypes.inlinedBooleanField = true;
    typeWithAllSimpleTypes.inlinedDoubleField = 3.0;
    typeWithAllSimpleTypes.inlinedFloatField = 3.0f;
    typeWithAllSimpleTypes.inlinedLongField = 3L;
    typeWithAllSimpleTypes.inlinedShortField = (short) 3;
    typeWithAllSimpleTypes.inlinedByteField = (byte) 3;
    typeWithAllSimpleTypes.inlinedCharField = 'c';
    typeWithAllSimpleTypes.inlinedStringField = "inlinedstring";
    typeWithAllSimpleTypes.namedIntegerField = 4;
    typeWithAllSimpleTypes.namedBooleanField = false;
    typeWithAllSimpleTypes.namedDoubleField = 4.0;
    typeWithAllSimpleTypes.namedFloatField = 4.0f;
    typeWithAllSimpleTypes.namedLongField = 4L;
    typeWithAllSimpleTypes.namedShortField = (short) 4;
    typeWithAllSimpleTypes.namedByteField = (byte) 4;
    typeWithAllSimpleTypes.namedCharField = 'd';
    typeWithAllSimpleTypes.namedByteArrayField = new byte[]{2, 4, 6};
    typeWithAllSimpleTypes.namedStringField = "namedstring";
    typeWithAllSimpleTypes.internalTypeAField = new InternalTypeA(1, "name");
    typeWithAllSimpleTypes.internalTypeCField = new InternalTypeC("data");

    HollowReadStateEngine stateEngine = createReadStateEngine(typeWithAllSimpleTypes);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeWithAllSimpleTypes", 0);

    TypeWithAllSimpleTypes result = mapper.readHollowRecord(obj);
    Assert.assertEquals(typeWithAllSimpleTypes, result);
  }

  @Test
  public void testReadPrimitivesPersistedWithSentinalValues() {
    TypeWithAllSimpleTypes typeWithAllSimpleTypes = new TypeWithAllSimpleTypes();
    typeWithAllSimpleTypes.primitiveIntegerField = Integer.MIN_VALUE; //write sentinal
    typeWithAllSimpleTypes.primitiveFloatField = Float.NaN;
    typeWithAllSimpleTypes.primitiveDoubleField = Double.NaN;
    typeWithAllSimpleTypes.primitiveLongField = Long.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveShortField = Short.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveByteField = Byte.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveCharField = Character.MIN_VALUE;
    HollowReadStateEngine stateEngine = createReadStateEngine(typeWithAllSimpleTypes);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeWithAllSimpleTypes", 0);
    TypeWithAllSimpleTypes result = mapper.readHollowRecord(obj);
    Assert.assertEquals(result.primitiveByteField, Byte.MIN_VALUE);
    Assert.assertEquals(result.primitiveCharField, Character.MIN_VALUE);
    Assert.assertEquals(result.primitiveIntegerField, Integer.MIN_VALUE);
    Assert.assertEquals(result.primitiveShortField, Short.MIN_VALUE);
    Assert.assertEquals(0, Double.compare(result.primitiveDoubleField, Double.NaN));
    Assert.assertEquals(result.primitiveLongField, Long.MIN_VALUE);
    Assert.assertEquals(0, Float.compare(result.primitiveFloatField, Float.NaN));
    Assert.assertEquals(result.primitiveIntegerField, Integer.MIN_VALUE);
  }

  @Test
  public void testNullablesSimpleTypes() {
    TypeWithAllSimpleTypes typeWithAllSimpleTypes = new TypeWithAllSimpleTypes();
    typeWithAllSimpleTypes.boxedIntegerField = 1;
    typeWithAllSimpleTypes.boxedCharField = 'a';
    typeWithAllSimpleTypes.primitiveIntegerField = Integer.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveDoubleField = Double.NaN;
    typeWithAllSimpleTypes.inlinedLongField = 4L;

    HollowReadStateEngine stateEngine = createReadStateEngine(typeWithAllSimpleTypes);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeWithAllSimpleTypes", 0);

    TypeWithAllSimpleTypes result = mapper.readHollowRecord(obj);
    Assert.assertEquals(Integer.valueOf(1), result.boxedIntegerField);
    Assert.assertEquals(Character.valueOf('a'), result.boxedCharField);
    Assert.assertNull(result.boxedBooleanField);
    Assert.assertNull(result.boxedDoubleField);
    Assert.assertNull(result.boxedFloatField);
    Assert.assertNull(result.boxedLongField);
    Assert.assertNull(result.boxedShortField);
    Assert.assertNull(result.boxedByteField);
    Assert.assertEquals(Integer.MIN_VALUE, result.primitiveIntegerField);
    Assert.assertFalse(result.primitiveBooleanField);
    Assert.assertEquals(Double.NaN, result.primitiveDoubleField, 0);
    Assert.assertEquals(0.0f, result.primitiveFloatField, 0);
    Assert.assertEquals(0L, result.primitiveLongField);
    Assert.assertEquals(0, result.primitiveShortField);
    Assert.assertEquals(0, result.primitiveByteField);
    Assert.assertNull(result.inlinedBooleanField);
    Assert.assertEquals(Long.valueOf(4L), result.inlinedLongField);
    Assert.assertNull(result.inlinedIntegerField);
    Assert.assertNull(result.inlinedDoubleField);
    Assert.assertNull(result.inlinedFloatField);
    Assert.assertNull(result.inlinedShortField);
    Assert.assertNull(result.inlinedByteField);
    Assert.assertNull(result.inlinedCharField);
    Assert.assertNull(result.inlinedStringField);
  }

  @Test
  public void testCollections() {
    TypeWithCollections type = new TypeWithCollections();
    type.id = 1;
    type.stringList = Arrays.asList("a", "b", "c");
    type.stringSet = new HashSet<>(type.stringList);
    type.integerStringMap = type.stringList.stream().collect(
        Collectors.toMap(
            s -> type.stringList.indexOf(s),
            s -> s
        )
    );
    type.internalTypeAList = Arrays.asList(new InternalTypeA(1), new InternalTypeA(2));
    type.internalTypeASet = new HashSet<>(type.internalTypeAList);
    type.integerInternalTypeAMap = type.internalTypeAList.stream().collect(
        Collectors.toMap(
            b -> b.id,
            b -> b
        )
    );
    type.internalTypeAStringMap = type.internalTypeAList.stream().collect(
        Collectors.toMap(
            b -> b,
            b -> b.name
        )
    );
    type.multiTypeMap = new HashMap<>();
    type.multiTypeMap.put(new TypeA10(1, "1", 1L), new TypeC10(new byte[]{1, 2, 3}));
    type.multiTypeMap.put(new TypeA10(2, "2", 2L), new TypeC10(new byte[]{4, 5, 6}));
    type.multiTypeMap.put(new TypeA10(2, "2", 2L), new TypeC10(new byte[]{7, 8, 9}));

    HollowReadStateEngine stateEngine = createReadStateEngine(type);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeWithCollections", 0);
    TypeWithCollections result = mapper.readHollowRecord(obj);

    Assert.assertEquals(type, result);
  }

  @Test
  public void testNullableCollections() {
    TypeWithCollections type = new TypeWithCollections();
    type.id = 1;
    type.stringList = Arrays.asList("a", "b", "c");
    type.integerStringMap = type.stringList.stream().collect(
        Collectors.toMap(
            s -> type.stringList.indexOf(s),
            s -> s
        )
    );

    HollowReadStateEngine stateEngine = createReadStateEngine(type);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeWithCollections", 0);
    TypeWithCollections result = mapper.readHollowRecord(obj);

    Assert.assertEquals(type, result);
    Assert.assertNull(result.stringSet);
    Assert.assertNull(result.internalTypeAList);
    Assert.assertNull(result.internalTypeASet);
    Assert.assertNull(result.integerInternalTypeAMap);
    Assert.assertNull(result.internalTypeAStringMap);
    Assert.assertNull(result.multiTypeMap);
  }

  @Test
  public void testMapFromVersionedTypes() {
    HollowObjectMapper readerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    readerMapper.initializeTypeState(TypeWithAllSimpleTypes.class);
    readerMapper.initializeTypeState(InternalTypeA.class);
    readerMapper.initializeTypeState(TypeWithCollections.class);
    readerMapper.initializeTypeState(VersionedType1.class);

    VersionedType2 versionedType2 = new VersionedType2();
    versionedType2.boxedIntegerField = 1;
    versionedType2.internalTypeBField = new InternalTypeB(1);
    versionedType2.charField = 'a';
    versionedType2.primitiveDoubleField = 1.0;
    versionedType2.stringSet = new HashSet<>(Arrays.asList("a", "b", "c"));

    HollowReadStateEngine stateEngine = createReadStateEngine(versionedType2);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "VersionedType", 0);

    VersionedType1 result = readerMapper.readHollowRecord(obj);

    Assert.assertEquals(null, result.stringField); // stringField is not present in VersionedType1
    Assert.assertEquals(versionedType2.boxedIntegerField, result.boxedIntegerField);
    Assert.assertEquals(versionedType2.primitiveDoubleField, result.primitiveDoubleField, 0);
    Assert.assertEquals(null, result.internalTypeAField); // internalTypeAField is not present in VersionedType1
    Assert.assertEquals(versionedType2.stringSet, result.stringSet);
  }

  @Test
  public void shouldMapNonPrimitiveWrapperToPrimitiveWrapperIfCommonFieldIsTheSame() {
    TypeStateA1 typeStateA1 = new TypeStateA1();
    typeStateA1.id = 1;
    typeStateA1.subValue = new SubValue();
    typeStateA1.subValue.value = "value";

    HollowReadStateEngine stateEngine = createReadStateEngine(typeStateA1);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeStateA", 0);

    HollowObjectMapper readerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    readerMapper.initializeTypeState(TypeStateA2.class);
    TypeStateA2 result = readerMapper.readHollowRecord(obj);

    Assert.assertEquals("value", result.subValue);
  }

  @Test
  public void shouldMapPrimitiveWrapperToNonPrimitiveWrapperIfCommonFieldIsTheSame() {
    TypeStateA2 typeStateA2 = new TypeStateA2();
    typeStateA2.id = 1;
    typeStateA2.subValue = "value";

    HollowReadStateEngine stateEngine = createReadStateEngine(typeStateA2);
    GenericHollowObject obj = new GenericHollowObject(stateEngine, "TypeStateA", 0);

    HollowObjectMapper readerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    readerMapper.initializeTypeState(TypeStateA1.class);
    TypeStateA1 result = readerMapper.readHollowRecord(obj);

    Assert.assertEquals("value", result.subValue.value);
  }

  private HollowReadStateEngine createReadStateEngine(Object... recs) {
    HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngineBuilder()
        .add(recs)
        .build();
    try {
      return StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @HollowPrimaryKey(fields={"boxedIntegerField", "stringField"})
  private static class TypeWithAllSimpleTypes {
    Integer boxedIntegerField;
    Boolean boxedBooleanField;
    Double boxedDoubleField;
    Float boxedFloatField;
    Long boxedLongField;
    Short boxedShortField;
    Byte boxedByteField;
    Character boxedCharField;

    int primitiveIntegerField;
    boolean primitiveBooleanField;
    double primitiveDoubleField;
    float primitiveFloatField;
    long primitiveLongField;
    short primitiveShortField;
    byte primitiveByteField;
    char primitiveCharField;
    byte[] byteArrayField;
    String stringField;

    @HollowInline
    Integer inlinedIntegerField;
    @HollowInline
    Boolean inlinedBooleanField;
    @HollowInline
    Double inlinedDoubleField;
    @HollowInline
    Float inlinedFloatField;
    @HollowInline
    Long inlinedLongField;
    @HollowInline
    Short inlinedShortField;
    @HollowInline
    Byte inlinedByteField;
    @HollowInline
    Character inlinedCharField;
    @HollowInline
    String inlinedStringField;

    @HollowTypeName(name = "NamedIntegerField")
    Integer namedIntegerField;
    @HollowTypeName(name = "NamedBooleanField")
    Boolean namedBooleanField;
    @HollowTypeName(name = "NamedDoubleField")
    Double namedDoubleField;
    @HollowTypeName(name = "NamedFloatField")
    Float namedFloatField;
    @HollowTypeName(name = "NamedLongField")
    Long namedLongField;
    @HollowTypeName(name = "NamedShortField")
    Short namedShortField;
    @HollowTypeName(name = "NamedByteField")
    Byte namedByteField;
    @HollowTypeName(name = "NamedCharField")
    Character namedCharField;
    @HollowTypeName(name = "NamedByteArrayField")
    byte[] namedByteArrayField;
    @HollowTypeName(name = "NamedStringField")
    String namedStringField;

    InternalTypeA internalTypeAField;
    InternalTypeC internalTypeCField;

    @Override
    public boolean equals(Object o) {
        if(o instanceof TypeWithAllSimpleTypes) {
            TypeWithAllSimpleTypes other = (TypeWithAllSimpleTypes)o;
            return Objects.equals(boxedIntegerField, other.boxedIntegerField) &&
                Objects.equals(boxedBooleanField, other.boxedBooleanField) &&
                Objects.equals(boxedDoubleField, other.boxedDoubleField) &&
                Objects.equals(boxedFloatField, other.boxedFloatField) &&
                Objects.equals(boxedLongField, other.boxedLongField) &&
                Objects.equals(boxedShortField, other.boxedShortField) &&
                Objects.equals(boxedByteField, other.boxedByteField) &&
                Objects.equals(boxedCharField, other.boxedCharField) &&
                primitiveIntegerField == other.primitiveIntegerField &&
                primitiveBooleanField == other.primitiveBooleanField &&
                primitiveDoubleField == other.primitiveDoubleField &&
                primitiveFloatField == other.primitiveFloatField &&
                primitiveLongField == other.primitiveLongField &&
                primitiveShortField == other.primitiveShortField &&
                primitiveByteField == other.primitiveByteField &&
                primitiveCharField == other.primitiveCharField &&
                Arrays.equals(byteArrayField, other.byteArrayField) &&
                Objects.equals(stringField, other.stringField) &&
                Objects.equals(inlinedIntegerField, other.inlinedIntegerField) &&
                Objects.equals(inlinedBooleanField, other.inlinedBooleanField) &&
                Objects.equals(inlinedDoubleField, other.inlinedDoubleField) &&
                Objects.equals(inlinedFloatField, other.inlinedFloatField) &&
                Objects.equals(inlinedLongField, other.inlinedLongField) &&
                Objects.equals(inlinedShortField, other.inlinedShortField) &&
                Objects.equals(inlinedByteField, other.inlinedByteField) &&
                Objects.equals(inlinedCharField, other.inlinedCharField) &&
                Objects.equals(inlinedStringField, other.inlinedStringField) &&
                Objects.equals(namedIntegerField, other.namedIntegerField) &&
                Objects.equals(namedBooleanField, other.namedBooleanField) &&
                Objects.equals(namedDoubleField, other.namedDoubleField) &&
                Objects.equals(namedFloatField, other.namedFloatField) &&
                Objects.equals(namedLongField, other.namedLongField) &&
                Objects.equals(namedShortField, other.namedShortField) &&
                Objects.equals(namedByteField, other.namedByteField) &&
                Objects.equals(namedCharField, other.namedCharField) &&
                Arrays.equals(namedByteArrayField, other.namedByteArrayField) &&
                Objects.equals(namedStringField, other.namedStringField) &&
                Objects.equals(internalTypeAField, other.internalTypeAField) &&
                Objects.equals(internalTypeCField, other.internalTypeCField);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TypeA{" +
            "boxedIntegerField=" + boxedIntegerField +
            ", boxedBooleanField=" + boxedBooleanField +
            ", boxedDoubleField=" + boxedDoubleField +
            ", boxedFloatField=" + boxedFloatField +
            ", boxedLongField=" + boxedLongField +
            ", boxedShortField=" + boxedShortField +
            ", boxedByteField=" + boxedByteField +
            ", boxedCharField=" + boxedCharField +
            ", primitiveIntegerField=" + primitiveIntegerField +
            ", primitiveBooleanField=" + primitiveBooleanField +
            ", primitiveDoubleField=" + primitiveDoubleField +
            ", primitiveFloatField=" + primitiveFloatField +
            ", primitiveLongField=" + primitiveLongField +
            ", primitiveShortField=" + primitiveShortField +
            ", primitiveByteField=" + primitiveByteField +
            ", primitiveCharField=" + primitiveCharField +
            ", byteArrayField=" + Arrays.toString(byteArrayField) +
            ", stringField='" + stringField + '\'' +
            ", inlinedIntegerField=" + inlinedIntegerField +
            ", inlinedBooleanField=" + inlinedBooleanField +
            ", inlinedDoubleField=" + inlinedDoubleField +
            ", inlinedFloatField=" + inlinedFloatField +
            ", inlinedLongField=" + inlinedLongField +
            ", inlinedShortField=" + inlinedShortField +
            ", inlinedByteField=" + inlinedByteField +
            ", inlinedCharField=" + inlinedCharField +
            ", inlinedStringField=" + inlinedStringField +
            ", namedIntegerField=" + namedIntegerField +
            ", namedBooleanField=" + namedBooleanField +
            ", namedDoubleField=" + namedDoubleField +
            ", namedFloatField=" + namedFloatField +
            ", namedLongField=" + namedLongField +
            ", namedShortField=" + namedShortField +
            ", namedByteField=" + namedByteField +
            ", namedCharField=" + namedCharField +
            ", namedByteArrayField=" + Arrays.toString(namedByteArrayField) +
            ", namedStringField='" + namedStringField + '\'' +
            ", internalTypeAField=" + internalTypeAField +
            ", internalTypeCField=" + internalTypeCField +
            '}';
    }
  }

  @HollowPrimaryKey(fields={"id"})
  private static class TypeWithCollections {
    int id;
    List<String> stringList;
    Set<String> stringSet;
    Map<Integer, String> integerStringMap;
    List<InternalTypeA> internalTypeAList;
    Set<InternalTypeA> internalTypeASet;
    Map<Integer, InternalTypeA> integerInternalTypeAMap;
    Map<InternalTypeA, String> internalTypeAStringMap;
    @HollowHashKey(fields="a2")
    public Map<TypeA10, TypeC10> multiTypeMap;

    @Override
    public boolean equals(Object o) {
      if(o instanceof TypeWithCollections) {
        TypeWithCollections other = (TypeWithCollections)o;
        return id == other.id &&
            Objects.equals(stringList, other.stringList) &&
            Objects.equals(stringSet, other.stringSet) &&
            Objects.equals(integerStringMap, other.integerStringMap) &&
            Objects.equals(internalTypeAList, other.internalTypeAList) &&
            Objects.equals(internalTypeASet, other.internalTypeASet) &&
            Objects.equals(integerInternalTypeAMap, other.integerInternalTypeAMap) &&
            Objects.equals(internalTypeAStringMap, other.internalTypeAStringMap) &&
            Objects.equals(multiTypeMap, other.multiTypeMap);
      }
      return false;
    }

    @Override
    public String toString() {
      return "TypeWithCollections{" +
          "id=" + id +
          ", stringList=" + stringList +
          ", stringSet=" + stringSet +
          ", integerStringMap=" + integerStringMap +
          ", internalTypeAList=" + internalTypeAList +
          ", internalTypeASet=" + internalTypeASet +
          ", integerInternalTypeAMap=" + integerInternalTypeAMap +
          ", internalTypeAStringMap=" + internalTypeAStringMap +
          ", multiTypeMap=" + multiTypeMap +
          '}';
    }
  }

  @HollowTypeName(name = "VersionedType")
  private static class VersionedType1 {
    String stringField;
    Integer boxedIntegerField;
    double primitiveDoubleField;
    InternalTypeA internalTypeAField;
    Set<String> stringSet;

    @Override
    public boolean equals(Object o) {
      if(o instanceof VersionedType1) {
        VersionedType1 other = (VersionedType1)o;
        return Objects.equals(stringField, other.stringField) &&
            Objects.equals(boxedIntegerField, other.boxedIntegerField) &&
            primitiveDoubleField == other.primitiveDoubleField &&
            Objects.equals(internalTypeAField, other.internalTypeAField) &&
            Objects.equals(stringSet, other.stringSet);
      }
      return false;
    }

    @Override
    public String toString() {
      return "VersionedType1{" +
          "stringField='" + stringField + '\'' +
          ", boxedIntegerField=" + boxedIntegerField +
          ", primitiveDoubleField=" + primitiveDoubleField +
          ", internalTypeAField=" + internalTypeAField +
          ", stringSet=" + stringSet +
          '}';
    }
  }

  @HollowTypeName(name = "VersionedType")
  private static class VersionedType2 {
    // No longer has the stringField
    Integer boxedIntegerField;
    double primitiveDoubleField;
    // No longer has the typeBField
    Set<String> stringSet;
    char charField; // Added a char field
    InternalTypeB internalTypeBField; // Added a new type field

    @Override
    public boolean equals(Object o) {
      if(o instanceof VersionedType2) {
        VersionedType2 other = (VersionedType2)o;
        return Objects.equals(boxedIntegerField, other.boxedIntegerField) &&
            primitiveDoubleField == other.primitiveDoubleField &&
            Objects.equals(stringSet, other.stringSet) &&
            charField == other.charField &&
            Objects.equals(internalTypeBField, other.internalTypeBField);
      }
      return false;
    }

    @Override
    public String toString() {
      return "VersionedType2{" +
          "boxedIntegerField=" + boxedIntegerField +
          ", primitiveDoubleField=" + primitiveDoubleField +
          ", stringSet=" + stringSet +
          ", charField=" + charField +
          ", internalTypeBField=" + internalTypeBField +
          '}';
    }
  }

  private static class InternalTypeA {
    Integer id;
    String name;

    public InternalTypeA(Integer id) {
      this(id, String.valueOf(id));
    }

    public InternalTypeA(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
      if(o instanceof InternalTypeA) {
        InternalTypeA other = (InternalTypeA)o;
        return id.equals(other.id) && name.equals(other.name);
      }
      return false;
    }

    @Override
    public String toString() {
      return "InternalTypeA{" +
          "id=" + id +
          ", name='" + name + '\'' +
          '}';
    }
  }

  private static class InternalTypeB {
    Integer id;
    String name;

    public InternalTypeB(Integer id) {
      this(id, String.valueOf(id));
    }

    public InternalTypeB(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
      if(o instanceof InternalTypeB) {
        InternalTypeB other = (InternalTypeB)o;
        return id.equals(other.id) && name.equals(other.name);
      }
      return false;
    }

    @Override
    public String toString() {
      return "InternalTypeB{" +
          "id=" + id +
          ", name='" + name + '\'' +
          '}';
    }
  }
  
  public static class InternalTypeC {
      @HollowInline
      String data;

      public InternalTypeC(String data) { 
          this.data = data;
      }
      
      @Override
      public boolean equals(Object o) {
        if(o instanceof InternalTypeC) {
          InternalTypeC other = (InternalTypeC)o;
          return data.equals(other.data);
        }
        return false;
      }
      
      @Override
      public String toString() {
        return "InternalTypeC{" +
            "data=" + data +
            '}';
      }
  }

  @HollowTypeName(name="TypeStateA")
  @HollowPrimaryKey(fields="id")
  public static class TypeStateA1 {
    public int id;
    public SubValue subValue;
  }

  @HollowTypeName(name="TypeStateA")
  @HollowPrimaryKey(fields="id")
  public static class TypeStateA2 {
    public int id;
    @HollowTypeName(name="SubValue")
    public String subValue;
  }

  public static class SubValue {
    @HollowInline
    public String value;
    @HollowInline
    public String anotherValue;
  }

  enum AnEnum {
    SOME_VALUE_A,
    SOME_VALUE_B,
    SOME_VALUE_C,
  }

  enum ComplexEnum {
    SOME_VALUE_A("A", 1),
    SOME_VALUE_B("B", 2),
    SOME_VALUE_C("C", 3);

    final String value;
    final int anotherValue;

    ComplexEnum(String value, int anotherValue) {
      this.value = value;
      this.anotherValue = anotherValue;
    }
  }

  @HollowTypeName(name = "SpecialWrapperTypesTest")
  @HollowPrimaryKey(fields = {"id"})
  static class SpecialWrapperTypesTest {
    long id;
    @HollowTypeName(name = "AnEnum")
    AnEnum type;
    @HollowTypeName(name = "ComplexEnum")
    ComplexEnum complexEnum;
    Date dateCreated;

    @Override
    public boolean equals(Object o) {
      if (o instanceof SpecialWrapperTypesTest) {
        SpecialWrapperTypesTest other = (SpecialWrapperTypesTest) o;
        return Objects.equals(id, other.id) &&
                Objects.equals(type, other.type) &&
                Objects.equals(complexEnum, other.complexEnum) &&
                Objects.equals(dateCreated, other.dateCreated);
      }
      return false;
    }

    @Override
    public String toString() {
      return "SpecialWrapperTypesTest{" +
              "id=" + id +
              ", type='" + type + '\'' +
              ", complexEnum='" + complexEnum + '\'' +
              ", dateCreated=" + dateCreated +
              '}';
    }
  }

  public static class TypeA10 {
    public int a1;
    @HollowInline
    public String a2;
    public long a3;

    public TypeA10(int a1, String a2, long a3) {
      this.a1 = a1;
      this.a2 = a2;
      this.a3 = a3;
    }

    @Override
    public String toString() {
      return "{" + a1 + "," + a2 + "," + a3 + "}";
    }

    @Override
    public int hashCode() {
      return Objects.hash(a1, a2, a3);
    }

    @Override
    public boolean equals(Object o) {
      if(o instanceof TypeA10) {
        TypeA10 other = (TypeA10)o;
        return a1 == other.a1 && a2.equals(other.a2) && a3 == other.a3;
      }
      return false;
    }
  }

  public static class TypeC10 {
    public byte[] c1;

    public TypeC10(byte[] c1) {
      this.c1 = c1;
    }

    @Override
    public String toString() {
      return Base64.getEncoder().encodeToString(c1);
    }

    @Override
    public boolean equals(Object o) {
      if(o instanceof TypeC10) {
        TypeC10 other = (TypeC10)o;
        return Arrays.equals(c1, other.c1);
      }
      return false;
    }
  }
}
