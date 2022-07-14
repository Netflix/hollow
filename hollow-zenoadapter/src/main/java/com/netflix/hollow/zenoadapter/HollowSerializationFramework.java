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
package com.netflix.hollow.zenoadapter;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.zenoadapter.util.ObjectIdentityOrdinalMap;
import com.netflix.hollow.zenoadapter.util.ObjectIdentityOrdinalMap.Entry;
import com.netflix.zeno.fastblob.record.schema.FastBlobSchema;
import com.netflix.zeno.fastblob.record.schema.FieldDefinition;
import com.netflix.zeno.fastblob.record.schema.MapFieldDefinition;
import com.netflix.zeno.fastblob.record.schema.TypedFieldDefinition;
import com.netflix.zeno.serializer.NFTypeSerializer;
import com.netflix.zeno.serializer.SerializationFramework;
import com.netflix.zeno.serializer.SerializerFactory;
import com.netflix.zeno.serializer.common.ListSerializer;
import com.netflix.zeno.serializer.common.MapSerializer;
import com.netflix.zeno.serializer.common.SetSerializer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HollowSerializationFramework extends SerializationFramework {

    private final HollowWriteStateEngine stateEngine;

    private final ConcurrentHashMap<String, ObjectIdentityOrdinalMap> objectIdentityOrdinalMaps;

    public HollowSerializationFramework(SerializerFactory serializerFactory, HollowObjectHashCodeFinder hashCodeFinder) {
        super(serializerFactory);
        this.frameworkSerializer = new HollowFrameworkSerializer(this, hashCodeFinder);

        this.stateEngine = new HollowWriteStateEngine(hashCodeFinder);
        this.objectIdentityOrdinalMaps = new ConcurrentHashMap<String, ObjectIdentityOrdinalMap>();

        populateStateEngineTypes();
    }

    public HollowObjectHashCodeFinder getHollowObjectHasher() {
        return ((HollowFrameworkSerializer) getFrameworkSerializer()).hasher;
    }

    public void prepareForNextCycle() {
        stateEngine.prepareForNextCycle();
        objectIdentityOrdinalMaps.clear();
    }

    public int add(String type, Object o) {
        ObjectIdentityOrdinalMap identityOrdinalMap = getIdentityOrdinalMap(type);
        Entry entry = identityOrdinalMap.getEntry(o);
        if(entry != null) {
            return entry.getOrdinal();
        }

        NFTypeSerializer<Object> serializer = getSerializer(type);
        int ordinal = add(type, o, serializer);
        identityOrdinalMap.put(o, ordinal);
        return ordinal;
    }

    private int add(String type, Object o, NFTypeSerializer<Object> serializer) {
        if(serializer instanceof ListSerializer) {
            return addList(type, o, serializer);
        } else if(serializer instanceof SetSerializer) {
            return addSet(type, o, serializer);
        } else if(serializer instanceof MapSerializer) {
            return addMap(type, o, serializer);
        } else {
            return addObject(type, o, serializer);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int addList(String type, Object o, NFTypeSerializer<Object> serializer) {
        int ordinal;
        String elementType = ((TypedFieldDefinition) serializer.getFastBlobSchema().getFieldDefinition(0)).getSubType();
        ordinal = frameworkSerializer().serializeList(getRec(type), elementType, (Collection) o);
        return ordinal;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int addSet(String type, Object o, NFTypeSerializer<Object> serializer) {
        int ordinal;
        String elementType = ((TypedFieldDefinition) serializer.getFastBlobSchema().getFieldDefinition(0)).getSubType();
        ordinal = frameworkSerializer().serializeSet(getRec(type), elementType, (Set) o);
        return ordinal;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int addMap(String type, Object o, NFTypeSerializer<Object> serializer) {
        int ordinal;
        String keyType = ((MapFieldDefinition) serializer.getFastBlobSchema().getFieldDefinition(0)).getKeyType();
        String valueType = ((MapFieldDefinition) serializer.getFastBlobSchema().getFieldDefinition(0)).getValueType();
        ordinal = frameworkSerializer().serializeMap(getRec(type), keyType, valueType, (Map) o);
        return ordinal;
    }

    private int addObject(String type, Object o, NFTypeSerializer<Object> serializer) {
        int ordinal;
        HollowSerializationRecord rec = getRec(type);
        serializer.serialize(o, rec);
        ordinal = stateEngine.add(type, rec.getHollowWriteRecord());
        return ordinal;
    }

    private HollowSerializationRecord getRec(String type) {
        HollowSerializationRecord rec = frameworkSerializer().getRec(type);
        rec.reset();
        return rec;
    }

    private HollowFrameworkSerializer frameworkSerializer() {
        return (HollowFrameworkSerializer) frameworkSerializer;
    }

    public HollowWriteStateEngine getStateEngine() {
        return stateEngine;
    }

    public HollowSchema getHollowSchema(String schemaName) {
        return stateEngine.getSchema(schemaName);
    }

    private void populateStateEngineTypes() {
        for(NFTypeSerializer<?> serializer : getOrderedSerializers()) {
            if(serializer instanceof ListSerializer) {
                ListSerializer<?> listSerializer = (ListSerializer<?>) serializer;
                TypedFieldDefinition elementFieldDef = (TypedFieldDefinition) listSerializer.getFastBlobSchema().getFieldDefinition(0);
                HollowListSchema listSchema = new HollowListSchema(serializer.getName(), elementFieldDef.getSubType());
                HollowTypeWriteState writeState = new HollowListTypeWriteState(listSchema);
                stateEngine.addTypeState(writeState);
            } else if(serializer instanceof SetSerializer) {
                SetSerializer<?> setSerializer = (SetSerializer<?>) serializer;
                TypedFieldDefinition elementFieldDef = (TypedFieldDefinition) setSerializer.getFastBlobSchema().getFieldDefinition(0);
                HollowSetSchema setSchema = new HollowSetSchema(serializer.getName(), elementFieldDef.getSubType());
                HollowTypeWriteState writeState = new HollowSetTypeWriteState(setSchema);
                stateEngine.addTypeState(writeState);
            } else if(serializer instanceof MapSerializer) {
                MapSerializer<?, ?> mapSerializer = (MapSerializer<?, ?>) serializer;
                MapFieldDefinition fieldDef = (MapFieldDefinition) mapSerializer.getFastBlobSchema().getFieldDefinition(0);
                HollowMapSchema mapSchema = new HollowMapSchema(serializer.getName(), fieldDef.getKeyType(), fieldDef.getValueType());
                HollowTypeWriteState writeState = new HollowMapTypeWriteState(mapSchema);
                stateEngine.addTypeState(writeState);
            } else {
                HollowObjectSchema objectSchema = getHollowObjectSchema(serializer.getFastBlobSchema());
                HollowTypeWriteState writeState = new HollowObjectTypeWriteState(objectSchema);
                stateEngine.addTypeState(writeState);
            }
        }
    }

    private HollowObjectSchema getHollowObjectSchema(FastBlobSchema schema) {
        HollowObjectSchema hollowSchema = new HollowObjectSchema(schema.getName(), schema.numFields());

        for(int i = 0; i < schema.numFields(); i++) {
            FieldDefinition def = schema.getFieldDefinition(i);
            switch(def.getFieldType()) {
                case OBJECT:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.REFERENCE, ((TypedFieldDefinition) def).getSubType());
                    break;
                case LIST:
                    String listTypeName = schema.getName() + "_" + schema.getFieldName(i);
                    String listElementType = ((TypedFieldDefinition) def).getSubType();
                    hollowSchema.addField(schema.getFieldName(i), FieldType.REFERENCE, listTypeName);
                    HollowListSchema listSchema = new HollowListSchema(listTypeName, listElementType);
                    HollowTypeWriteState listWriteState = new HollowListTypeWriteState(listSchema);
                    stateEngine.addTypeState(listWriteState);
                    break;
                case SET:
                    String setTypeName = schema.getName() + "_" + schema.getFieldName(i);
                    String setElementType = ((TypedFieldDefinition) def).getSubType();
                    hollowSchema.addField(schema.getFieldName(i), FieldType.REFERENCE, setTypeName);
                    HollowSetSchema setSchema = new HollowSetSchema(setTypeName, setElementType);
                    HollowTypeWriteState setWriteState = new HollowSetTypeWriteState(setSchema);
                    stateEngine.addTypeState(setWriteState);
                    break;
                case MAP:
                    String mapTypeName = schema.getName() + "_" + schema.getFieldName(i);
                    String keyType = ((MapFieldDefinition) def).getKeyType();
                    String valueType = ((MapFieldDefinition) def).getValueType();
                    hollowSchema.addField(schema.getFieldName(i), FieldType.REFERENCE, mapTypeName);
                    HollowMapSchema mapSchema = new HollowMapSchema(mapTypeName, keyType, valueType);
                    HollowTypeWriteState mapWriteState = new HollowMapTypeWriteState(mapSchema);
                    stateEngine.addTypeState(mapWriteState);
                    break;
                case BOOLEAN:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.BOOLEAN);
                    break;
                case BYTES:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.BYTES);
                    break;
                case DOUBLE:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.DOUBLE);
                    break;
                case FLOAT:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.FLOAT);
                    break;
                case INT:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.INT);
                    break;
                case LONG:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.LONG);
                    break;
                case STRING:
                    hollowSchema.addField(schema.getFieldName(i), FieldType.STRING);
                    break;
                default:
                    throw new IllegalArgumentException("Field " + schema.getName() + "." + schema.getFieldName(i) + " is declared with illegal type " + schema.getFieldType(i));
            }
        }

        return hollowSchema;
    }

    private ObjectIdentityOrdinalMap getIdentityOrdinalMap(String type) {
        ObjectIdentityOrdinalMap objectIdentityOrdinalMap = objectIdentityOrdinalMaps.get(type);
        if(objectIdentityOrdinalMap == null) {
            objectIdentityOrdinalMap = new ObjectIdentityOrdinalMap();
            ObjectIdentityOrdinalMap existing = objectIdentityOrdinalMaps.putIfAbsent(type, objectIdentityOrdinalMap);
            if(existing != null)
                objectIdentityOrdinalMap = existing;
        }
        return objectIdentityOrdinalMap;
    }

}
