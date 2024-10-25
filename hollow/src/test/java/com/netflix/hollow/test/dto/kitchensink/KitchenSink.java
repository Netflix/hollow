package com.netflix.hollow.test.dto.kitchensink;


import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

import java.util.List;
import java.util.Map;
import java.util.Set;

@HollowPrimaryKey(fields = {"intVal"})
public class KitchenSink {
  public int intVal;
  public long longVal;
  public float floatVal;
  public double doubleVal;
  public boolean booleanVal;
  public byte byteVal;
  public short shortVal;
  public char charVal;

  public Integer boxedIntVal;
  public Long boxedLongVal;
  public Float boxedFloatVal;
  public Double boxedDoubleVal;
  public Boolean boxedBooleanVal;
  public Byte boxedByteVal;
  public Short boxedShortVal;
  public Character boxedCharVal;
  public String stringVal;

  @HollowInline
  public Integer inlineIntVal;
  @HollowInline
  public Long inlineLongVal;
  @HollowInline
  public Float inlineFloatVal;
  @HollowInline
  public Double inlineDoubleVal;
  @HollowInline
  public Boolean inlineBooleanVal;
  @HollowInline
  public Byte inlineByteVal;
  @HollowInline
  public Short inlineShortVal;
  @HollowInline
  public Character inlineCharVal;
  @HollowInline
  public String inlineStringVal;

  public byte[] bytesVal;

  @HollowTypeName(name = "CustomType")
  public String customTypeVal;

  public SubType subType;

  public List<SubType> subTypeList;
  public Set<SubType> subTypeSet;

  public Map<MapKey, SubType> subTypeMap;
  public Map<ComplexMapKey, SubType> complexMapKeyMap;


  @HollowHashKey(fields = {"value1"})
  public Set<HashableKey> hashableSet;
  @HollowHashKey(fields = {"value1"})
  public Map<HashableKey, SubType> hashableMap;

  public static class SubType {
    public int intVal;
    public long longVal;
    public float floatVal;
  }

  public static class MapKey {
    public int value;
  }

  public static class ComplexMapKey {
    public int value1;
    public int value2;
  }

  public static class HashableKey {
    public int value1;
    public int value2;
  }
}
