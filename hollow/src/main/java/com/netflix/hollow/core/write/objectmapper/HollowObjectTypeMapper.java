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

    private final long assignedOrdinalFieldOffset;

    private final List<MappedField> mappedFields;

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
                mappedFields.add(new MappedField(SpecialField.DATE_TIME));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            /// gather fields from type hierarchy
            Class<?> currentClass = clazz;
            
            while(currentClass != Object.class) {
                Field[] declaredFields = currentClass.getDeclaredFields();
    
                for(int i=0;i<declaredFields.length;i++) {
                    if(!Modifier.isTransient(declaredFields[i].getModifiers()) && 
                       !Modifier.isStatic(declaredFields[i].getModifiers()) && 
                       !"__assigned_ordinal".equals(declaredFields[i].getName())) {
                        
                        mappedFields.add(new MappedField(declaredFields[i], visited));
                    }
                }
    
                if(currentClass.isEnum())
                    mappedFields.add(new MappedField(SpecialField.ENUM_NAME));
                
                currentClass = currentClass.getSuperclass();
            }
        }

        this.schema = new HollowObjectSchema(typeName, mappedFields.size(), getKeyFieldPaths(clazz));

        for(MappedField field : mappedFields) {
            if(field.getFieldType() == FieldType.REFERENCE) {
                schema.addField(field.getFieldName(), field.getFieldType(), field.getReferencedTypeName());
            } else {
                schema.addField(field.getFieldName(), field.getFieldType());
            }
        }

        HollowObjectTypeWriteState existingWriteState = (HollowObjectTypeWriteState) parentMapper.getStateEngine().getTypeState(typeName);
        this.writeState = existingWriteState != null ? existingWriteState : new HollowObjectTypeWriteState(schema, getNumShards(clazz));

        long assignedOrdinalFieldOffset = -1;
        try {
            Field declaredField = clazz.getDeclaredField("__assigned_ordinal");
            assignedOrdinalFieldOffset = unsafe.objectFieldOffset(declaredField);
        } catch (Exception ignore) { }
        this.assignedOrdinalFieldOffset = assignedOrdinalFieldOffset;
    }

    private static String[] getKeyFieldPaths(Class<?> clazz) {
        HollowPrimaryKey primaryKey = clazz.getAnnotation(HollowPrimaryKey.class);
        while(primaryKey == null && clazz != Object.class) {
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
        if(assignedOrdinalFieldOffset != -1) {
            int assignedOrdinal = unsafe.getInt(obj, assignedOrdinalFieldOffset);
            if(assignedOrdinal != -1)
                return assignedOrdinal;
        }

        if(obj.getClass() != clazz && !clazz.isAssignableFrom(obj.getClass()))
            throw new IllegalArgumentException("Attempting to write unexpected class!  Expected " + clazz + " but object was " + obj.getClass());

        HollowObjectWriteRecord rec = (HollowObjectWriteRecord)writeRecord();

        for(int i=0;i<mappedFields.size();i++) {
            mappedFields.get(i).copy(obj, rec);
        }

        int assignedOrdinal = writeState.add(rec);
        if(assignedOrdinalFieldOffset != -1) {
            unsafe.putInt(obj, assignedOrdinalFieldOffset, assignedOrdinal);
        }
        return assignedOrdinal;
    }

    public String[] getDefaultElementHashKey() {
        PrimaryKey pKey = schema.getPrimaryKey();
        if (pKey != null) return pKey.getFieldPaths();

        if(mappedFields.size() == 1) {
            MappedField singleField = mappedFields.get(0);
            if(singleField.getFieldType() != FieldType.REFERENCE)
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

    private class MappedField {

        private final String fieldName;
        private final long fieldOffset;
        private final Type type;
        private final FieldType fieldType;
        private final HollowTypeMapper subTypeMapper;
        private final HollowTypeName typeNameAnnotation;
        private final HollowHashKey hashKeyAnnotation;
        private final HollowShardLargeType numShardsAnnotation;
        private final SpecialField specialField;

        private MappedField(Field f) {
            this(f, new HashSet<Type>());
        }
        private MappedField(Field f, Set<Type> visitedTypes) {
            this.fieldOffset = unsafe.objectFieldOffset(f);
            this.fieldName = f.getName();
            this.type = f.getGenericType();
            this.typeNameAnnotation = f.getAnnotation(HollowTypeName.class);
            this.hashKeyAnnotation = f.getAnnotation(HollowHashKey.class);
            this.numShardsAnnotation = f.getAnnotation(HollowShardLargeType.class);
            this.specialField = null;

            HollowTypeMapper subTypeMapper = null;

            if(type == int.class || type == short.class || type == byte.class || type == char.class) {
                fieldType = FieldType.INT;
            } else if(type == long.class) {
                fieldType = FieldType.LONG;
            } else if(type == boolean.class
                    || type == NullablePrimitiveBoolean.class) {
                fieldType = FieldType.BOOLEAN;
            } else if(type == float.class) {
                fieldType = FieldType.FLOAT;
            } else if(type == double.class) {
                fieldType = FieldType.DOUBLE;
            } else if(type == byte[].class) {
                fieldType = FieldType.BYTES;
            } else if(type == char[].class){
                fieldType = FieldType.STRING;
            } else {
                fieldType = FieldType.REFERENCE;
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

        private MappedField(SpecialField specialField) {
            this.fieldOffset = -1;
            this.type = null;
            this.typeNameAnnotation = null;
            this.hashKeyAnnotation = null;
            this.numShardsAnnotation = null;
            this.fieldName = specialField.getFieldName();
            this.fieldType = specialField.getFieldType();
            this.specialField = specialField;
            this.subTypeMapper = null;
        }

        public String getFieldName() {
            return fieldName;
        }

        public FieldType getFieldType() {
            return fieldType;
        }

        public String getReferencedTypeName() {
            if(typeNameAnnotation != null)
                return typeNameAnnotation.name();
            return subTypeMapper.getTypeName();
        }

        public void copy(Object obj, HollowObjectWriteRecord rec) {
            switch(fieldType) {
                case BOOLEAN:
                    if(type == boolean.class) {
                        rec.setBoolean(fieldName, unsafe.getBoolean(obj, fieldOffset));
                    } else if(type == NullablePrimitiveBoolean.class) {
                        NullablePrimitiveBoolean value = (NullablePrimitiveBoolean) unsafe.getObject(obj, fieldOffset);
                        if(value != null) {
                            rec.setBoolean(fieldName, value.getBooleanValue());
                        }
                    }
                    break;
                case BYTES:
                    Object byteArray = unsafe.getObject(obj, fieldOffset);
                    if(byteArray != null)
                        rec.setBytes(fieldName, (byte[])byteArray);
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
                case INT:
                    if(type == int.class)
                        rec.setInt(fieldName, unsafe.getInt(obj, fieldOffset));
                    else if(type == short.class)
                        rec.setInt(fieldName, unsafe.getShort(obj, fieldOffset));
                    else if(type == byte.class)
                        rec.setInt(fieldName, unsafe.getByte(obj, fieldOffset));
                    else if(type == char.class)
                        rec.setInt(fieldName, unsafe.getChar(obj, fieldOffset));
                    break;
                case LONG:
                    if(specialField == SpecialField.DATE_TIME) {
                        rec.setLong(fieldName, ((Date)obj).getTime());
                    } else {
                        rec.setLong(fieldName, unsafe.getLong(obj, fieldOffset));
                    }
                    break;
                case STRING:
                    if(specialField == SpecialField.ENUM_NAME) {
                        rec.setString(fieldName, ((Enum<?>)obj).name());
                    } else {
                        Object charArray = unsafe.getObject(obj, fieldOffset);
                        if(charArray != null)
                            rec.setString(fieldName, new String((char[])charArray));
                    }
                    break;
                case REFERENCE:
                    Object subObject = unsafe.getObject(obj, fieldOffset);
                    if(subObject != null)
                        rec.setReference(fieldName, subTypeMapper.write(subObject));
                    break;
            }
        }
        
    }

    private static enum SpecialField {
        ENUM_NAME(FieldType.STRING, "_name"),
        DATE_TIME(FieldType.LONG, "value");
        
        private final FieldType fieldType;
        private final String fieldName;
        
        private SpecialField(FieldType fieldType, String fieldName) {
            this.fieldType = fieldType;
            this.fieldName = fieldName;
        }
        
        public FieldType getFieldType() {
            return fieldType;
        }
        
        public String getFieldName() {
            return fieldName;
        }
    }
}
