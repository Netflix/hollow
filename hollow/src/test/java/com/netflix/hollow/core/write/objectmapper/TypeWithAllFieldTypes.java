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

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import java.util.Arrays;

@SuppressWarnings("deprecation")
public class TypeWithAllFieldTypes {
    
    boolean bool;
    int i;
    byte b;
    short s;
    char c;
    long l;
    float f;
    double d;
    @HollowInline Boolean inlinedBoolean;
    @HollowInline Integer inlinedInt;
    @HollowInline Byte inlinedByte;
    @HollowInline Short inlinedShort;
    @HollowInline Character inlinedChar;
    @HollowInline Long inlinedLong;
    @HollowInline Float inlinedFloat;
    @HollowInline Double inlinedDouble;
    @HollowInline String inlinedString;
    char[] charArray;
    byte[] byteArray;
    NullablePrimitiveBoolean nullablePrimitiveBoolean;
    Integer referencedInteger;
    
    public TypeWithAllFieldTypes(int value) {
        this.bool = (value & 1) == 1;
        this.i = value;
        this.b = (byte)value;
        this.s = (short)value;
        this.c = (char)value;
        this.l = value;
        this.f = (float)value;
        this.d = (double)value;
        this.inlinedBoolean = Boolean.valueOf(bool);
        this.inlinedInt = Integer.valueOf(i);
        this.inlinedByte = Byte.valueOf(b);
        this.inlinedShort = Short.valueOf(s);
        this.inlinedChar = Character.valueOf(c);
        this.inlinedLong = Long.valueOf(l);
        this.inlinedFloat = Float.valueOf(f);
        this.inlinedDouble = Double.valueOf(d);
        this.inlinedString = String.valueOf(value);
        this.charArray = inlinedString.toCharArray();
        this.byteArray = inlinedString.getBytes();
        this.nullablePrimitiveBoolean = bool ? NullablePrimitiveBoolean.FALSE : NullablePrimitiveBoolean.TRUE;
        this.referencedInteger = inlinedInt;
    }
    
    public TypeWithAllFieldTypes(GenericHollowObject obj) {
        this.bool = obj.getBoolean("bool");
        this.i = obj.getInt("i");
        this.b = (byte)obj.getInt("b");
        this.s = (short)obj.getInt("s");
        this.c = (char)obj.getInt("c");
        this.l = obj.getLong("l");
        this.f = obj.getFloat("f");
        this.d = obj.getDouble("d");
        this.inlinedBoolean = obj.isNull("inlinedBoolean") ? null : obj.getBoolean("inlinedBoolean");
        this.inlinedInt = obj.isNull("inlinedInt") ? null : obj.getInt("inlinedInt");
        this.inlinedByte = obj.isNull("inlinedByte") ? null : (byte)obj.getInt("inlinedByte");
        this.inlinedShort = obj.isNull("inlinedShort") ? null : (short)obj.getInt("inlinedShort");
        this.inlinedChar = obj.isNull("inlinedChar") ? null : (char)obj.getInt("inlinedChar");
        this.inlinedLong = obj.isNull("inlinedLong") ? null : obj.getLong("inlinedLong");
        this.inlinedFloat = obj.isNull("inlinedFloat") ? null : obj.getFloat("inlinedFloat");
        this.inlinedDouble = obj.isNull("inlinedDouble") ? null : obj.getDouble("inlinedDouble");
        this.inlinedString = obj.isNull("inlinedString") ? null : obj.getString("inlinedString");
        this.charArray = obj.isNull("charArray") ? null : obj.getString("charArray").toCharArray();
        this.byteArray = obj.isNull("byteArray") ? null : obj.getBytes("byteArray");
        this.nullablePrimitiveBoolean = obj.isNull("nullablePrimitiveBoolean") ? null : obj.getBoolean("nullablePrimitiveBoolean") ? NullablePrimitiveBoolean.TRUE : NullablePrimitiveBoolean.FALSE;
        this.referencedInteger = obj.isNull("referencedInteger") ? null : obj.getObject("referencedInteger").getInt("value");
    }
    
    void nullFirstHalf() {
        inlinedBoolean = null;
        inlinedByte = null;
        inlinedChar = null;
        inlinedFloat = null;
        inlinedString = null;
        byteArray = null;
        referencedInteger = null;
    }
    
    void nullSecondHalf() {
        inlinedInt = null;
        inlinedShort = null;
        inlinedLong = null;
        inlinedDouble = null;
        charArray = null;
        nullablePrimitiveBoolean = null;
    }

    @Override
    @SuppressWarnings("EqualsHashCode")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeWithAllFieldTypes other = (TypeWithAllFieldTypes) obj;
        if (b != other.b)
            return false;
        if (bool != other.bool)
            return false;
        if (!Arrays.equals(byteArray, other.byteArray))
            return false;
        if (c != other.c)
            return false;
        if (!Arrays.equals(charArray, other.charArray))
            return false;
        if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
            return false;
        if (Float.floatToIntBits(f) != Float.floatToIntBits(other.f))
            return false;
        if (i != other.i)
            return false;
        if (inlinedBoolean == null) {
            if (other.inlinedBoolean != null)
                return false;
        } else if (!inlinedBoolean.equals(other.inlinedBoolean))
            return false;
        if (inlinedByte == null) {
            if (other.inlinedByte != null)
                return false;
        } else if (!inlinedByte.equals(other.inlinedByte))
            return false;
        if (inlinedChar == null) {
            if (other.inlinedChar != null)
                return false;
        } else if (!inlinedChar.equals(other.inlinedChar))
            return false;
        if (inlinedDouble == null) {
            if (other.inlinedDouble != null)
                return false;
        } else if (!inlinedDouble.equals(other.inlinedDouble))
            return false;
        if (inlinedFloat == null) {
            if (other.inlinedFloat != null)
                return false;
        } else if (!inlinedFloat.equals(other.inlinedFloat))
            return false;
        if (inlinedInt == null) {
            if (other.inlinedInt != null)
                return false;
        } else if (!inlinedInt.equals(other.inlinedInt))
            return false;
        if (inlinedLong == null) {
            if (other.inlinedLong != null)
                return false;
        } else if (!inlinedLong.equals(other.inlinedLong))
            return false;
        if (inlinedShort == null) {
            if (other.inlinedShort != null)
                return false;
        } else if (!inlinedShort.equals(other.inlinedShort))
            return false;
        if (inlinedString == null) {
            if (other.inlinedString != null)
                return false;
        } else if (!inlinedString.equals(other.inlinedString))
            return false;
        if (l != other.l)
            return false;
        if (nullablePrimitiveBoolean != other.nullablePrimitiveBoolean)
            return false;
        if (referencedInteger == null) {
            if (other.referencedInteger != null)
                return false;
        } else if (!referencedInteger.equals(other.referencedInteger))
            return false;
        if (s != other.s)
            return false;
        return true;
    }

}
