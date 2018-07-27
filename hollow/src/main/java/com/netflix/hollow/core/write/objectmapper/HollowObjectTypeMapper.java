/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class HollowObjectTypeMapper extends HollowTypeMapper {
    
    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();
    private final HollowObjectMapper parentMapper;

    private final String typeName;
    private final Class<?> clazz;
    private final HollowObjectSchema schema;
    private final HollowObjectTypeWriteState writeState;

    private final AssignedOrdinalType assignedOrdinalType;
    private final long assignedOrdinalFieldOffset;

    private final List<MappedField> mappedFields;
    
    private volatile int primaryKeyFieldPathIdx[][];

    public HollowObjectTypeMapper(HollowObjectMapper parentMapper, Class<?> clazz, String declaredTypeName, Set<Type> visited) {
        this.parentMapper = parentMapper;
        this.clazz = clazz;
        this.typeName = declaredTypeName != null ? declaredTypeName : getDefaultTypeName(clazz);
        this.mappedFields = new ArrayList<MappedField>();
        
        if(clazz == String.class) {
            try {
                mappedFields.add(new MappedField(clazz.getDeclaredField("value")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if(clazz == Date.class) {
            try {
                mappedFields.add(new MappedField(MappedFieldType.DATE_TIME));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            /// gather fields from type hierarchy
            Class<?> currentClass = clazz;
            
            while(currentClass != Object.class && currentClass != Enum.class) {
                if(currentClass.isInterface()) {
                    throw new IllegalArgumentException("Unexpected interface " + currentClass.getSimpleName() + " passed as field.");
                } if (currentClass.isArray()) {
                    throw new IllegalArgumentException("Unexpected array " + currentClass.getSimpleName() + " passed as field. Consider using collections or marking as transient.");
                }
                Field[] declaredFields = currentClass.getDeclaredFields();
    
                for(int i=0;i<declaredFields.length;i++) {
                    if(!Modifier.isTransient(declaredFields[i].getModifiers()) &&
                       !Modifier.isStatic(declaredFields[i].getModifiers()) && 
                       !"__assigned_ordinal".equals(declaredFields[i].getName()) &&
                       declaredFields[i].getAnnotation(HollowTransient.class) == null) {

                        mappedFields.add(new MappedField(declaredFields[i], visited));
                    }
                }
    
                if(currentClass.isEnum())
                    mappedFields.add(new MappedField(MappedFieldType.ENUM_NAME));
                
                currentClass = currentClass.getSuperclass();
            }
        }

        this.schema = new HollowObjectSchema(typeName, mappedFields.size(), getKeyFieldPaths(clazz));

        for(MappedField field : mappedFields) {
            if(field.getFieldType() == MappedFieldType.REFERENCE) {
                schema.addField(field.getFieldName(), field.getFieldType().getSchemaFieldType(), field.getReferencedTypeName());
            } else {
                schema.addField(field.getFieldName(), field.getFieldType().getSchemaFieldType());
            }
        }

        HollowObjectTypeWriteState existingWriteState = (HollowObjectTypeWriteState) parentMapper.getStateEngine().getTypeState(typeName);
        this.writeState = existingWriteState != null ? existingWriteState : new HollowObjectTypeWriteState(schema, getNumShards(clazz));

        AssignedOrdinalType assignedOrdinalType = AssignedOrdinalType.NONE;
        long assignedOrdinalFieldOffset = -1;
        try {
            Field declaredField = clazz.getDeclaredField("__assigned_ordinal");
            if(declaredField.getType() == int.class || declaredField.getType() == long.class)
                assignedOrdinalFieldOffset = unsafe.objectFieldOffset(declaredField);
            if(declaredField.getType() == long.class)
                assignedOrdinalType = AssignedOrdinalType.LONG;
            else if(declaredField.getType() == int.class)
                assignedOrdinalType = AssignedOrdinalType.INT;
            
        } catch (Exception ignore) { }
        this.assignedOrdinalFieldOffset = assignedOrdinalFieldOffset;
        this.assignedOrdinalType = assignedOrdinalType;
    }

    private static String[] getKeyFieldPaths(Class<?> clazz) {
        HollowPrimaryKey primaryKey = clazz.getAnnotation(HollowPrimaryKey.class);
        while(primaryKey == null && clazz != Object.class && clazz.isInterface()) {
            clazz = clazz.getSuperclass();
            primaryKey = clazz.getAnnotation(HollowPrimaryKey.class);
        }
        return primaryKey == null ? null : primaryKey.fields();
    }
    
    private static int getNumShards(Class<?> clazz) {
        HollowShardLargeType numShardsAnnotation = clazz.getAnnotation(HollowShardLargeType.class);
        if(numShardsAnnotation != null)
            return numShardsAnnotation.numShards();
        return -1;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int write(Object obj) {
        switch(assignedOrdinalType) {
        case LONG:
            long assignedOrdinal = unsafe.getLong(obj, assignedOrdinalFieldOffset);
            if((assignedOrdinal & ASSIGNED_ORDINAL_CYCLE_MASK) == cycleSpecificAssignedOrdinalBits())
                return (int)assignedOrdinal & Integer.MAX_VALUE;
            break;
        case INT:
            int intAssignedOrdinal = unsafe.getInt(obj, assignedOrdinalFieldOffset);
            if(intAssignedOrdinal != -1)
                return intAssignedOrdinal;
            break;
        case NONE:
            break;
        }

        if(obj.getClass() != clazz && !clazz.isAssignableFrom(obj.getClass()))
            throw new IllegalArgumentException("Attempting to write unexpected class!  Expected " + clazz + " but object was " + obj.getClass());

        HollowObjectWriteRecord rec = (HollowObjectWriteRecord)writeRecord();

        for(int i=0;i<mappedFields.size();i++) {
            mappedFields.get(i).copy(obj, rec);
        }

        int assignedOrdinal = writeState.add(rec);
        switch(assignedOrdinalType) {
        case LONG:
            unsafe.putLong(obj, assignedOrdinalFieldOffset, (long)assignedOrdinal | cycleSpecificAssignedOrdinalBits());
            break;
        case INT:
            unsafe.putInt(obj, assignedOrdinalFieldOffset, assignedOrdinal);
            break;
        case NONE:
            break;
        }
        return assignedOrdinal;
    }

    Object[] extractPrimaryKey(Object obj) {
        int[][] primaryKeyFieldPathIdx = this.primaryKeyFieldPathIdx;
        
        if(primaryKeyFieldPathIdx == null) {
            primaryKeyFieldPathIdx = calculatePrimaryKeyFieldPathIdx(primaryKeyFieldPathIdx);
            this.primaryKeyFieldPathIdx = primaryKeyFieldPathIdx;
        }
        
        Object key[] = new Object[primaryKeyFieldPathIdx.length];
        
        for(int i=0;i<key.length;i++) {
            key[i] = retrieveFieldValue(obj, primaryKeyFieldPathIdx[i], 0);
        }
        
        return key;
    }

    private int[][] calculatePrimaryKeyFieldPathIdx(int[][] primaryKeyFieldPathIdx) {
        if(schema.getPrimaryKey() == null)
            throw new IllegalArgumentException("Type " + typeName + " does not have a primary key defined");
        
        primaryKeyFieldPathIdx = new int[schema.getPrimaryKey().numFields()][];
        
        for(int i=0;i<primaryKeyFieldPathIdx.length;i++)
            primaryKeyFieldPathIdx[i] = schema.getPrimaryKey().getFieldPathIndex(parentMapper.getStateEngine(), i);
        
        return primaryKeyFieldPathIdx;
    }
    
    String[] getDefaultElementHashKey() {
        PrimaryKey pKey = schema.getPrimaryKey();
        if (pKey != null) return pKey.getFieldPaths();

        if(mappedFields.size() == 1) {
            MappedField singleField = mappedFields.get(0);
            if(singleField.getFieldType() != MappedFieldType.REFERENCE)
                return new String[] { singleField.getFieldName() };
        }
        return null;
    }

    @Override
    protected HollowWriteRecord newWriteRecord() {
        return new HollowObjectWriteRecord(schema);
    }

    @Override
    protected HollowTypeWriteState getTypeWriteState() {
        return writeState;
    }

    private Object retrieveFieldValue(Object obj, int[] fieldPathIdx, int idx) {
        return mappedFields.get(fieldPathIdx[idx]).retrieveFieldValue(obj, fieldPathIdx, idx);
    }

    private class MappedField {

        private final String fieldName;
        private final long fieldOffset;
        private final Type type;
        private final MappedFieldType fieldType;
        private final HollowTypeMapper subTypeMapper;
        private final HollowTypeName typeNameAnnotation;
        private final HollowHashKey hashKeyAnnotation;
        private final HollowShardLargeType numShardsAnnotation;
        private final boolean isInlinedField;

        private MappedField(Field f) {
            this(f, new HashSet<Type>());
        }
        
        @SuppressWarnings("deprecation")
        private MappedField(Field f, Set<Type> visitedTypes) {
            this.fieldOffset = unsafe.objectFieldOffset(f);
            this.fieldName = f.getName();
            this.type = f.getGenericType();
            this.typeNameAnnotation = f.getAnnotation(HollowTypeName.class);
            this.hashKeyAnnotation = f.getAnnotation(HollowHashKey.class);
            this.numShardsAnnotation = f.getAnnotation(HollowShardLargeType.class);
            this.isInlinedField = f.isAnnotationPresent(HollowInline.class);
            

            HollowTypeMapper subTypeMapper = null;
            
            if(type == int.class) {
                fieldType = MappedFieldType.INT;
            } else if(type == short.class) {
                fieldType = MappedFieldType.SHORT;
            } else if(type == byte.class) {
                fieldType = MappedFieldType.BYTE;
            } else if(type == char.class) {
                fieldType = MappedFieldType.CHAR;
            } else if(type == long.class) {
                fieldType = MappedFieldType.LONG;
            } else if(type == boolean.class) {
                fieldType = MappedFieldType.BOOLEAN;
            } else if(type == float.class) {
                fieldType = MappedFieldType.FLOAT;
            } else if(type == double.class) {
                fieldType = MappedFieldType.DOUBLE;
            } else if (type == byte[].class && clazz == String.class) {
                fieldType = MappedFieldType.STRING;
            } else if(type == byte[].class) {
                fieldType = MappedFieldType.BYTES;
            } else if(type == char[].class) {
                fieldType = MappedFieldType.STRING;
            } else if(isInlinedField && type == Integer.class) {
                fieldType = MappedFieldType.INLINED_INT;
            } else if(isInlinedField && type == Short.class) {
                fieldType = MappedFieldType.INLINED_SHORT;
            } else if(isInlinedField && type == Byte.class) {
                fieldType = MappedFieldType.INLINED_BYTE;
            } else if(isInlinedField && type == Character.class) {
                fieldType = MappedFieldType.INLINED_CHAR;
            } else if(isInlinedField && type == Long.class) {
                fieldType = MappedFieldType.INLINED_LONG;
            } else if(isInlinedField && type == Boolean.class) {
                fieldType = MappedFieldType.INLINED_BOOLEAN;
            } else if(isInlinedField && type == Float.class) {
                fieldType = MappedFieldType.INLINED_FLOAT;
            } else if(isInlinedField && type == Double.class) {
                fieldType = MappedFieldType.INLINED_DOUBLE;
            } else if(isInlinedField && type == String.class) {
                fieldType = MappedFieldType.INLINED_STRING;
            } else if(type == NullablePrimitiveBoolean.class) {
                fieldType = MappedFieldType.NULLABLE_PRIMITIVE_BOOLEAN;
            } else {
                if(isInlinedField)
                    throw new IllegalStateException("@HollowInline annotation defined on field " + f + ", which is not either a String or boxed primitive.");
                
                fieldType = MappedFieldType.REFERENCE;
                if(visitedTypes.contains(this.type)){
                    throw new IllegalStateException("circular reference detected on field " + f + "; this type of relationship is not supported");
                }
                // guard recursion here
                visitedTypes.add(this.type);
                subTypeMapper = parentMapper.getTypeMapper(type, 
                        typeNameAnnotation != null ? typeNameAnnotation.name() : null, 
                                hashKeyAnnotation != null ? hashKeyAnnotation.fields() : null, 
                                        numShardsAnnotation != null ? numShardsAnnotation.numShards() : -1, 
                                                visitedTypes);
                
                // once we've safely returned from a leaf node in recursion, we can remove this MappedField's type
                visitedTypes.remove(this.type);
            }

            this.subTypeMapper = subTypeMapper;
        }

        private MappedField(MappedFieldType specialField) {
            this.fieldOffset = -1;
            this.type = null;
            this.typeNameAnnotation = null;
            this.hashKeyAnnotation = null;
            this.numShardsAnnotation = null;
            this.fieldName = specialField.getSpecialFieldName();
            this.fieldType = specialField;
            this.subTypeMapper = null;
            this.isInlinedField = false;
        }

        public String getFieldName() {
            return fieldName;
        }

        public MappedFieldType getFieldType() {
            return fieldType;
        }

        public String getReferencedTypeName() {
            if(typeNameAnnotation != null)
                return typeNameAnnotation.name();
            return subTypeMapper.getTypeName();
        }

        @SuppressWarnings("deprecation")
        public void copy(Object obj, HollowObjectWriteRecord rec) {
            Object fieldObject;
            
            switch(fieldType) {
                case BOOLEAN:
                    rec.setBoolean(fieldName, unsafe.getBoolean(obj, fieldOffset));
                    break;
                case INT:
                    rec.setInt(fieldName, unsafe.getInt(obj, fieldOffset));
                    break;
                case SHORT:
                    rec.setInt(fieldName, unsafe.getShort(obj, fieldOffset));
                    break;
                case BYTE:
                    rec.setInt(fieldName, unsafe.getByte(obj, fieldOffset));
                    break;
                case CHAR:
                    rec.setInt(fieldName, unsafe.getChar(obj, fieldOffset));
                    break;
                case LONG:
                    rec.setLong(fieldName, unsafe.getLong(obj, fieldOffset));
                    break;
                case DOUBLE:
                    double d = unsafe.getDouble(obj, fieldOffset);
                    if(!Double.isNaN(d))
                        rec.setDouble(fieldName, d);
                    break;
                case FLOAT:
                    float f = unsafe.getFloat(obj, fieldOffset);
                    if(!Float.isNaN(f))
                        rec.setFloat(fieldName, f);
                    break;
                case STRING:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setString(fieldName, getStringFromField(obj, fieldObject));
                    break;
                case BYTES:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setBytes(fieldName, (byte[])fieldObject);
                    break;
                case INLINED_BOOLEAN:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setBoolean(fieldName, ((Boolean)fieldObject).booleanValue());
                    break;
                case INLINED_INT:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setInt(fieldName, ((Integer)fieldObject).intValue());
                    break;
                case INLINED_SHORT:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setInt(fieldName, ((Short)fieldObject).intValue());
                    break;
                case INLINED_BYTE:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setInt(fieldName, ((Byte)fieldObject).intValue());
                    break;
                case INLINED_CHAR:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setInt(fieldName, (int)((Character)fieldObject).charValue());
                    break;
                case INLINED_LONG:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setLong(fieldName, ((Long)fieldObject).longValue());
                    break;
                case INLINED_DOUBLE:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setDouble(fieldName, ((Double)fieldObject).doubleValue());
                    break;
                case INLINED_FLOAT:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setFloat(fieldName, ((Float)fieldObject).floatValue());
                    break;
                case INLINED_STRING:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setString(fieldName, (String)fieldObject);
                    break;
                case NULLABLE_PRIMITIVE_BOOLEAN:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setBoolean(fieldName, ((NullablePrimitiveBoolean)fieldObject).getBooleanValue());
                    break;
                case DATE_TIME:
                    rec.setLong(fieldName, ((Date)obj).getTime());
                    break;
                case ENUM_NAME:
                    rec.setString(fieldName, ((Enum<?>)obj).name());
                    break;
                case REFERENCE:
                    fieldObject = unsafe.getObject(obj, fieldOffset);
                    if(fieldObject != null)
                        rec.setReference(fieldName, subTypeMapper.write(fieldObject));
                    break;
            }
        }

        public Object retrieveFieldValue(Object obj, int[] fieldPathIdx, int idx) {
            Object fieldObject;

            if(idx < fieldPathIdx.length - 1) {
                if(fieldType != MappedFieldType.REFERENCE)
                    throw new IllegalArgumentException("Expected REFERENCE mapped field type but found " + fieldType);
                fieldObject = unsafe.getObject(obj, fieldOffset);
                if(fieldObject == null)
                    return null;
                return ((HollowObjectTypeMapper)subTypeMapper).retrieveFieldValue(fieldObject, fieldPathIdx, idx+1);
            }
            

            switch(fieldType) {
            case BOOLEAN:
                return unsafe.getBoolean(obj, fieldOffset);
            case INT:
                return Integer.valueOf(unsafe.getInt(obj, fieldOffset));
            case SHORT:
                return Integer.valueOf(unsafe.getShort(obj, fieldOffset));
            case BYTE:
                return Integer.valueOf(unsafe.getByte(obj, fieldOffset));
            case CHAR:
                return Integer.valueOf(unsafe.getChar(obj, fieldOffset));
            case LONG:
                return Long.valueOf(unsafe.getLong(obj, fieldOffset));
            case DOUBLE:
                double d = unsafe.getDouble(obj, fieldOffset);
                if(Double.isNaN(d))
                    return null;
                return Double.valueOf(d);
            case FLOAT:
                float f = unsafe.getFloat(obj, fieldOffset);
                if(Float.isNaN(f))
                    return null;
                return Float.valueOf(f);
            case STRING:
                fieldObject = unsafe.getObject(obj, fieldOffset);
                return fieldObject == null ? null : getStringFromField(obj, fieldObject);
            case BYTES:
                fieldObject = unsafe.getObject(obj, fieldOffset);
                return fieldObject == null ? null : (byte[])fieldObject;
            case INLINED_BOOLEAN:
            case INLINED_INT:
            case INLINED_LONG:
            case INLINED_DOUBLE:
            case INLINED_FLOAT:
            case INLINED_STRING:
                return unsafe.getObject(obj, fieldOffset);
            case INLINED_SHORT:
                fieldObject = unsafe.getObject(obj, fieldOffset);
                return fieldObject == null ? null : Integer.valueOf((Short)fieldObject);
            case INLINED_BYTE:
                fieldObject = unsafe.getObject(obj, fieldOffset);
                return fieldObject == null ? null : Integer.valueOf((Byte)fieldObject);
            case INLINED_CHAR:
                fieldObject = unsafe.getObject(obj, fieldOffset);
                return fieldObject == null ? null : Integer.valueOf((Character)fieldObject);
            case NULLABLE_PRIMITIVE_BOOLEAN:
                fieldObject = unsafe.getObject(obj, fieldOffset);
                return fieldObject == null ? null : Boolean.valueOf(((NullablePrimitiveBoolean)fieldObject).getBooleanValue());
            case DATE_TIME:
                return Long.valueOf(((Date)obj).getTime());
            case ENUM_NAME:
                return String.valueOf(((Enum<?>)obj).name());
            default:
                throw new IllegalArgumentException("Cannot extract POJO primary key from a " + fieldType + " mapped field type");
            }
        }

        private String getStringFromField(Object obj, Object fieldObject) {
            if (obj instanceof String) {
                return (String) obj;
            } else if (fieldObject instanceof char[]) {
                return new String((char[]) fieldObject);
            }
            throw new IllegalArgumentException("Expected char[] or String value container for STRING.");
        }
    }
    
    private static enum AssignedOrdinalType {
        INT,
        LONG,
        NONE
    }

    private static enum MappedFieldType {
        BOOLEAN(FieldType.BOOLEAN),
        NULLABLE_PRIMITIVE_BOOLEAN(FieldType.BOOLEAN),
        BYTES(FieldType.BYTES),
        DOUBLE(FieldType.DOUBLE),
        FLOAT(FieldType.FLOAT),
        INT(FieldType.INT),
        SHORT(FieldType.INT),
        BYTE(FieldType.INT),
        CHAR(FieldType.INT),
        LONG(FieldType.LONG),
        STRING(FieldType.STRING),
        INLINED_BOOLEAN(FieldType.BOOLEAN),
        INLINED_DOUBLE(FieldType.DOUBLE),
        INLINED_FLOAT(FieldType.FLOAT),
        INLINED_INT(FieldType.INT),
        INLINED_SHORT(FieldType.INT),
        INLINED_BYTE(FieldType.INT),
        INLINED_CHAR(FieldType.INT),
        INLINED_LONG(FieldType.LONG),
        INLINED_STRING(FieldType.STRING),
        REFERENCE(FieldType.REFERENCE),
        ENUM_NAME(FieldType.STRING, "_name"),
        DATE_TIME(FieldType.LONG, "value");
        
        private final FieldType schemaFieldType;
        private final String specialFieldName;
        
        private MappedFieldType(FieldType schemaFieldType) {
            this.specialFieldName = null;
            this.schemaFieldType = schemaFieldType;
        }
        
        private MappedFieldType(FieldType schemaFieldType, String specialFieldName) {
            this.schemaFieldType = schemaFieldType;
            this.specialFieldName = specialFieldName;
        }
        
        public String getSpecialFieldName() {
            return specialFieldName;
        }
        
        public FieldType getSchemaFieldType() {
            return schemaFieldType;
        }
    }
}
