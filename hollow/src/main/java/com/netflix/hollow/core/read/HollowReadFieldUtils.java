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
package com.netflix.hollow.core.read;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.util.Arrays;

/**
 * Useful utility methods for interacting with a {@link HollowDataAccess}. 
 */
public class HollowReadFieldUtils {

    /**
     * Hash a field in an OBJECT record.
     *
     * @param typeAccess the data access
     * @param ordinal the ordinal
     * @param fieldPosition the field position
     * @return the hash code
     */
    public static int fieldHashCode(HollowObjectTypeDataAccess typeAccess, int ordinal, int fieldPosition) {
        HollowObjectSchema schema = typeAccess.getSchema();

        switch(schema.getFieldType(fieldPosition)) {
            case BOOLEAN:
                Boolean bool = typeAccess.readBoolean(ordinal, fieldPosition);
                return booleanHashCode(bool);
            case BYTES:
            case STRING:
                return typeAccess.findVarLengthFieldHashCode(ordinal, fieldPosition);
            case DOUBLE:
                double d = typeAccess.readDouble(ordinal, fieldPosition);
                return doubleHashCode(d);
            case FLOAT:
                float f = typeAccess.readFloat(ordinal, fieldPosition);
                return floatHashCode(f);
            case INT:
                return intHashCode(typeAccess.readInt(ordinal, fieldPosition));
            case LONG:
                long l = typeAccess.readLong(ordinal, fieldPosition);
                return longHashCode(l);
            case REFERENCE:
                return typeAccess.readOrdinal(ordinal, fieldPosition);
        }

        throw new IllegalStateException("I don't know how to hash a " + schema.getFieldType(fieldPosition));
    }

    /**
     * Determine whether two OBJECT field records are exactly equal.
     *
     * @param typeAccess1 the first type access
     * @param ordinal1 the first ordinal
     * @param fieldPosition1 the first field positiom
     * @param typeAccess2 the second type access
     * @param ordinal2 the second ordinal
     * @param fieldPosition2 the second field position
     *
     * @return if the two OBJECT field records are exactly equal
     */
    public static boolean fieldsAreEqual(HollowObjectTypeDataAccess typeAccess1, int ordinal1, int fieldPosition1, HollowObjectTypeDataAccess typeAccess2, int ordinal2, int fieldPosition2) {
        HollowObjectSchema schema1 = typeAccess1.getSchema();

        switch(schema1.getFieldType(fieldPosition1)) {
            case BOOLEAN:
                Boolean bool1 = typeAccess1.readBoolean(ordinal1, fieldPosition1);
                Boolean bool2 = typeAccess2.readBoolean(ordinal2, fieldPosition2);
                return bool1 == bool2;
            case BYTES:
                byte[] data1 = typeAccess1.readBytes(ordinal1, fieldPosition1);
                byte[] data2 = typeAccess2.readBytes(ordinal2, fieldPosition2);
                return Arrays.equals(data1, data2);
            case DOUBLE:
                double d1 = typeAccess1.readDouble(ordinal1, fieldPosition1);
                double d2 = typeAccess2.readDouble(ordinal2, fieldPosition2);
                return Double.compare(d1, d2) == 0;
            case FLOAT:
                float f1 = typeAccess1.readFloat(ordinal1, fieldPosition1);
                float f2 = typeAccess2.readFloat(ordinal2, fieldPosition2);
                return Float.compare(f1, f2) == 0;
            case INT:
                int i1 = typeAccess1.readInt(ordinal1, fieldPosition1);
                int i2 = typeAccess2.readInt(ordinal2, fieldPosition2);
                return i1 == i2;
            case LONG:
                long l1 = typeAccess1.readLong(ordinal1, fieldPosition1);
                long l2 = typeAccess2.readLong(ordinal2, fieldPosition2);
                return l1 == l2;
            case STRING:
                String s1 = typeAccess1.readString(ordinal1, fieldPosition1);
                return typeAccess2.isStringFieldEqual(ordinal2, fieldPosition2, s1);
            case REFERENCE:
                if(typeAccess1 == typeAccess2 && fieldPosition1 == fieldPosition2)
                    return typeAccess1.readOrdinal(ordinal1, fieldPosition1) == typeAccess2.readOrdinal(ordinal2, fieldPosition2);
            default:
        }

        throw new IllegalStateException("I don't know how to test equality for a " + schema1.getFieldType(fieldPosition1));
    }

    /**
     * @param typeAccess the type access
     * @param ordinal the ordinal
     * @param fieldPosition the field position
     * @return a displayable String for a field from an OBJECT record. 
     */
    public static String displayString(HollowObjectTypeDataAccess typeAccess, int ordinal, int fieldPosition) {
        HollowObjectSchema schema = typeAccess.getSchema();

        switch(schema.getFieldType(fieldPosition)) {
            case BOOLEAN:
                Boolean bool = typeAccess.readBoolean(ordinal, fieldPosition);
                return String.valueOf(bool);
            case BYTES:
            case STRING:
                return typeAccess.readString(ordinal, fieldPosition);
            case DOUBLE:
                double d = typeAccess.readDouble(ordinal, fieldPosition);
                return String.valueOf(d);
            case FLOAT:
                float f = typeAccess.readFloat(ordinal, fieldPosition);
                return String.valueOf(f);
            case INT:
                return String.valueOf(typeAccess.readInt(ordinal, fieldPosition));
            case LONG:
                long l = typeAccess.readLong(ordinal, fieldPosition);
                return String.valueOf(l);
            default:
        }

        throw new IllegalStateException("I don't know how to display a " + schema.getFieldType(fieldPosition));
    }

    /**
     * @param typeAccess the type access
     * @param ordinal the ordinal
     * @param fieldPosition the field position
     * @return an appropriate Object representing a Hollow OBJECT record field's value
     */
    public static Object fieldValueObject(HollowObjectTypeDataAccess typeAccess, int ordinal, int fieldPosition) {
        HollowObjectSchema schema = typeAccess.getSchema();

        switch(schema.getFieldType(fieldPosition)) {
            case BOOLEAN:
                return typeAccess.readBoolean(ordinal, fieldPosition);
            case BYTES:
                return typeAccess.readBytes(ordinal, fieldPosition);
            case STRING:
                return typeAccess.readString(ordinal, fieldPosition);
            case DOUBLE:
                double d = typeAccess.readDouble(ordinal, fieldPosition);
                return Double.isNaN(d) ? null : Double.valueOf(d);
            case FLOAT:
                float f = typeAccess.readFloat(ordinal, fieldPosition);
                return Float.isNaN(f) ? null : Float.valueOf(f);
            case INT:
                int i = typeAccess.readInt(ordinal, fieldPosition);
                if(i == Integer.MIN_VALUE)
                    return null;
                return Integer.valueOf(i);
            case LONG:
                long l = typeAccess.readLong(ordinal, fieldPosition);
                if(l == Long.MIN_VALUE)
                    return null;
                return Long.valueOf(l);
            case REFERENCE:
                int refOrdinal = typeAccess.readOrdinal(ordinal, fieldPosition);
                if(refOrdinal < 0)
                    return null;
                return Integer.valueOf(refOrdinal);
            default:
        }

        throw new IllegalStateException("Can't handle fieldType=" + schema.getFieldType(fieldPosition) + " for schema=" + schema.getName() + ", fieldPosition=" + fieldPosition);
    }

    /**
     * @param typeAccess the type access
     * @param ordinal the ordinal
     * @param fieldPosition the field position
     * @param testObject the object to test
     * @return whether the provided Object matches a Hollow OBJECT record's value.
     */
    public static boolean fieldValueEquals(HollowObjectTypeDataAccess typeAccess, int ordinal, int fieldPosition, Object testObject) {
        HollowObjectSchema schema = typeAccess.getSchema();

        switch(schema.getFieldType(fieldPosition)) {
            case BOOLEAN:
                if(testObject instanceof Boolean)
                    return testObject.equals(typeAccess.readBoolean(ordinal, fieldPosition));
                return testObject == null && typeAccess.readBoolean(ordinal, fieldPosition) == null;
            case BYTES:
                if(testObject instanceof byte[])
                    return Arrays.equals(typeAccess.readBytes(ordinal, fieldPosition), (byte[])testObject);
                return testObject == null && typeAccess.readBytes(ordinal, fieldPosition) == null;
            case STRING:
                if(testObject instanceof String)
                    return testObject.equals(typeAccess.readString(ordinal, fieldPosition));
                return testObject == null && typeAccess.readString(ordinal, fieldPosition) == null;
            case DOUBLE:
                if(testObject instanceof Double)
                    return testObject.equals(typeAccess.readDouble(ordinal, fieldPosition));
                return testObject == null && !Double.isNaN(typeAccess.readDouble(ordinal, fieldPosition));
            case FLOAT:
                if(testObject instanceof Float)
                    return testObject.equals(typeAccess.readFloat(ordinal, fieldPosition));
                return testObject == null && !Float.isNaN(typeAccess.readFloat(ordinal, fieldPosition));
            case INT:
                if(testObject instanceof Integer)
                    return testObject.equals(typeAccess.readInt(ordinal, fieldPosition));
                return testObject == null && typeAccess.readInt(ordinal, fieldPosition) == Integer.MIN_VALUE;
            case LONG:
                if(testObject instanceof Long)
                    return testObject.equals(typeAccess.readLong(ordinal, fieldPosition));
                return testObject == null && typeAccess.readLong(ordinal, fieldPosition) == Long.MIN_VALUE;
            case REFERENCE:
                if(testObject instanceof Integer)
                    return testObject.equals(typeAccess.readOrdinal(ordinal, fieldPosition));
                return testObject == null && typeAccess.readOrdinal(ordinal, fieldPosition) < 0;
            default:
        }

        throw new IllegalStateException("I don't know how to test equality for a " + schema.getFieldType(fieldPosition));
    }

    /**
     * @param data the byte array
     * @return The hash code for a byte array which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int byteArrayHashCode(byte[] data) {
        return HashCodes.hashCode(data);
    }

    /**
     * @param str the string value
     * @return The hash code for a String which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int stringHashCode(String str) {
        return HashCodes.hashCode(str);
    }

    /**
     * @param bool the boolean value
     * @return The hash code for a boolean which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int booleanHashCode(Boolean bool) {
        return bool == null ? -1 : bool ? 1 : 0;
    }

    /**
     * @param l the long value
     * @return The hash code for a long which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int longHashCode(long l) {
        return (int)l ^ (int)(l >> 32);
    }

    /**
     * @param i the int value
     * @return The hash code for an int which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int intHashCode(int i) {
        return i;
    }

    /**
     * @param f the float value
     * @return The hash code for a float which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int floatHashCode(float f) {
        return Float.floatToIntBits(f);
    }

    /**
     * @param d the double value
     * @return The hash code for a double which would be returned from {@link #fieldHashCode(HollowObjectTypeDataAccess, int, int)}
     */
    public static int doubleHashCode(double d) {
        long bits = Double.doubleToLongBits(d);
        return longHashCode(bits);
    }
}
