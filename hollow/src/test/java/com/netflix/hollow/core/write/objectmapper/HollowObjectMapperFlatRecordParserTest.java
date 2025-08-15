package com.netflix.hollow.core.write.objectmapper;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FakeHollowSchemaIdentifierMapper;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectMapperFlatRecordParserTest {
  private HollowObjectMapper mapper;
  private FlatRecordWriter flatRecordWriter;

  @Before
  public void setUp() {
    mapper = new HollowObjectMapper(new HollowWriteStateEngine());
    mapper.initializeTypeState(TypeWithAllSimpleTypes.class);
    mapper.initializeTypeState(InternalTypeA.class);
    mapper.initializeTypeState(TypeWithCollections.class);
    mapper.initializeTypeState(VersionedType2.class);
    mapper.initializeTypeState(SpecialWrapperTypesTest.class);

    flatRecordWriter = new FlatRecordWriter(
        mapper.getStateEngine(), new FakeHollowSchemaIdentifierMapper(mapper.getStateEngine()));
  }

  @Test
  public void testSpecialWrapperTypes() {
    SpecialWrapperTypesTest wrapperTypesTest = new SpecialWrapperTypesTest();
    wrapperTypesTest.id = 8797182L;
    wrapperTypesTest.type = AnEnum.SOME_VALUE_C;
    wrapperTypesTest.complexEnum = ComplexEnum.SOME_VALUE_A;
    wrapperTypesTest.dateCreated = new Date();

    flatRecordWriter.reset();
    mapper.writeFlat(wrapperTypesTest, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    SpecialWrapperTypesTest result = mapper.readFlat(fr);

    Assert.assertEquals(wrapperTypesTest, result);
    Assert.assertEquals(wrapperTypesTest.complexEnum.value, result.complexEnum.value);
    Assert.assertEquals(wrapperTypesTest.complexEnum.anotherValue, result.complexEnum.anotherValue);
  }

  @Test
  public void roundTripPreservesNullability() {
    TypeWithAllSimpleTypes obj = new TypeWithAllSimpleTypes();
    // set PK
    obj.boxedIntegerField = 1;
    obj.stringField = "hello";
    // all inlined values are null

    flatRecordWriter.reset();
    mapper.writeFlat(obj, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    TypeWithAllSimpleTypes result = mapper.readFlat(fr);
    Assert.assertEquals(obj, result);

    // round trip the record: obj -> flatRecord -> obj -> flatRecord -> obj
    flatRecordWriter.reset();
    mapper.writeFlat(result, flatRecordWriter);
    FlatRecord roundTrippedFr = flatRecordWriter.generateFlatRecord();

    TypeWithAllSimpleTypes roundTrippedResult = mapper.readFlat(roundTrippedFr);
    Assert.assertEquals(obj, roundTrippedResult);
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
    typeWithAllSimpleTypes.primitiveIntegerField = 1;
    typeWithAllSimpleTypes.primitiveBooleanField = true;
    typeWithAllSimpleTypes.primitiveDoubleField = 1.0;
    typeWithAllSimpleTypes.primitiveFloatField = 1.0f;
    typeWithAllSimpleTypes.primitiveLongField = 1L;
    typeWithAllSimpleTypes.primitiveShortField = (short) 1;
    typeWithAllSimpleTypes.primitiveByteField = (byte) 1;
    typeWithAllSimpleTypes.primitiveCharField = 'a';
    typeWithAllSimpleTypes.stringField = "string";
    typeWithAllSimpleTypes.inlinedIntegerField = 1;
    typeWithAllSimpleTypes.inlinedBooleanField = true;
    typeWithAllSimpleTypes.inlinedDoubleField = 1.0;
    typeWithAllSimpleTypes.inlinedFloatField = 1.0f;
    typeWithAllSimpleTypes.inlinedLongField = 1L;
    typeWithAllSimpleTypes.inlinedShortField = (short) 1;
    typeWithAllSimpleTypes.inlinedByteField = (byte) 1;
    typeWithAllSimpleTypes.inlinedCharField = 'a';
    typeWithAllSimpleTypes.inlinedStringField = "inlinedstring";
    typeWithAllSimpleTypes.internalTypeAField = new InternalTypeA(1, "name");
    typeWithAllSimpleTypes.internalTypeCField = new InternalTypeC("data");

    flatRecordWriter.reset();
    mapper.writeFlat(typeWithAllSimpleTypes, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    TypeWithAllSimpleTypes result = mapper.readFlat(fr);

    Assert.assertEquals(typeWithAllSimpleTypes, result);
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

    flatRecordWriter.reset();
    mapper.writeFlat(type, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    TypeWithCollections result = mapper.readFlat(fr);

    Assert.assertEquals(type, result);
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

    mapper.writeFlat(versionedType2, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    VersionedType1 result = readerMapper.readFlat(fr);

    Assert.assertEquals(null, result.stringField); // stringField is not present in VersionedType1
    Assert.assertEquals(versionedType2.boxedIntegerField, result.boxedIntegerField);
    Assert.assertEquals(versionedType2.primitiveDoubleField, result.primitiveDoubleField, 0);
    Assert.assertEquals(null, result.internalTypeAField); // internalTypeAField is not present in VersionedType1
    Assert.assertEquals(versionedType2.stringSet, result.stringSet);
  }

  @Test
  public void shouldMapNonPrimitiveWrapperToPrimitiveWrapperIfCommonFieldIsTheSame() {
    HollowObjectMapper writerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    writerMapper.initializeTypeState(TypeStateA1.class);

    FlatRecordWriter flatRecordWriter = new FlatRecordWriter(
            writerMapper.getStateEngine(), new FakeHollowSchemaIdentifierMapper(writerMapper.getStateEngine()));

    TypeStateA1 typeStateA1 = new TypeStateA1();
    typeStateA1.id = 1;
    typeStateA1.subValue = new SubValue();
    typeStateA1.subValue.value = "value";

    writerMapper.writeFlat(typeStateA1, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    HollowObjectMapper readerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    readerMapper.initializeTypeState(TypeStateA2.class);

    TypeStateA2 result = readerMapper.readFlat(fr);

    Assert.assertEquals("value", result.subValue);
  }

  @Test
  public void shouldMapPrimitiveWrapperToNonPrimitiveWrapperIfCommonFieldIsTheSame() {
    HollowObjectMapper writerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    writerMapper.initializeTypeState(TypeStateA2.class);

    FlatRecordWriter flatRecordWriter = new FlatRecordWriter(
            writerMapper.getStateEngine(), new FakeHollowSchemaIdentifierMapper(writerMapper.getStateEngine()));

    TypeStateA2 typeStateA2 = new TypeStateA2();
    typeStateA2.id = 1;
    typeStateA2.subValue = "value";

    writerMapper.writeFlat(typeStateA2, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();

    HollowObjectMapper readerMapper = new HollowObjectMapper(new HollowWriteStateEngine());
    readerMapper.initializeTypeState(TypeStateA1.class);

    TypeStateA1 result = readerMapper.readFlat(fr);

    Assert.assertEquals("value", result.subValue.value);
  }

  @Test
  public void testReadPrimitivesPersistedWithSentinalValues() {
    TypeWithAllSimpleTypes
        typeWithAllSimpleTypes = new TypeWithAllSimpleTypes();
    typeWithAllSimpleTypes.boxedIntegerField = 10;
    typeWithAllSimpleTypes.stringField = "stringField";
    typeWithAllSimpleTypes.primitiveIntegerField = Integer.MIN_VALUE; //write sentinal
    typeWithAllSimpleTypes.primitiveFloatField = Float.NaN;
    typeWithAllSimpleTypes.primitiveDoubleField = Double.NaN;
    typeWithAllSimpleTypes.primitiveLongField = Long.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveShortField = Short.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveByteField = Byte.MIN_VALUE;
    typeWithAllSimpleTypes.primitiveCharField = Character.MIN_VALUE;
    flatRecordWriter.reset();
    mapper.writeFlat(typeWithAllSimpleTypes, flatRecordWriter);
    FlatRecord fr = flatRecordWriter.generateFlatRecord();
    TypeWithAllSimpleTypes result = mapper.readFlat(fr);
    Assert.assertEquals(Integer.MIN_VALUE, typeWithAllSimpleTypes.primitiveIntegerField);
    Assert.assertEquals(Long.MIN_VALUE, typeWithAllSimpleTypes.primitiveLongField);
    Assert.assertEquals(Short.MIN_VALUE, typeWithAllSimpleTypes.primitiveShortField);
    Assert.assertEquals(Byte.MIN_VALUE, typeWithAllSimpleTypes.primitiveByteField);
    Assert.assertEquals(Character.MIN_VALUE, typeWithAllSimpleTypes.primitiveCharField);
    Assert.assertTrue(Float.isNaN(typeWithAllSimpleTypes.primitiveFloatField));
    Assert.assertTrue(Double.isNaN(typeWithAllSimpleTypes.primitiveDoubleField));
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
    InternalTypeA internalTypeAField;
    InternalTypeC internalTypeCField;

    public TypeWithAllSimpleTypes() {}

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
            ", stringField='" + stringField + '\'' +
            ", inlinedIntegerField=" + inlinedIntegerField +
            ", inlinedBooleanField=" + inlinedBooleanField +
            ", inlinedDoubleField=" + inlinedDoubleField +
            ", inlinedFloatField=" + inlinedFloatField +
            ", inlinedLongField=" + inlinedLongField +
            ", inlinedShortField=" + inlinedShortField +
            ", inlinedByteField=" + inlinedByteField +
            ", inlinedCharField=" + inlinedCharField +
            ", internalTypeAField=" + internalTypeAField +
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

    public TypeWithCollections() {}

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
            Objects.equals(internalTypeAStringMap, other.internalTypeAStringMap);
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

    public VersionedType1() {}

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

    public VersionedType2() {}

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

    public InternalTypeA() {}

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

    public InternalTypeB() {}

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
      
      public InternalTypeC() { }
      
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
      if(o instanceof SpecialWrapperTypesTest) {
        SpecialWrapperTypesTest other = (SpecialWrapperTypesTest)o;
        return id == other.id && complexEnum == other.complexEnum && type == other.type && dateCreated.equals(other.dateCreated);
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
}
