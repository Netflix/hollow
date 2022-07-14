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
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.zeno.fastblob.record.schema.TypedFieldDefinition;
import com.netflix.zeno.serializer.FrameworkSerializer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HollowFrameworkSerializer extends FrameworkSerializer<HollowSerializationRecord> {

    final HollowObjectHashCodeFinder hasher;

    protected HollowFrameworkSerializer(HollowSerializationFramework framework, HollowObjectHashCodeFinder hasher) {
        super(framework);
        this.hasher = hasher;
    }

    @Override
    public void serializePrimitive(HollowSerializationRecord rec, String fieldName, Object value) {
        if(value == null)
            return;

        if(value instanceof Integer) {
            serializePrimitive(rec, fieldName, ((Integer) value).intValue());
        } else if(value instanceof Long) {
            serializePrimitive(rec, fieldName, ((Long) value).longValue());
        } else if(value instanceof Float) {
            serializePrimitive(rec, fieldName, ((Float) value).floatValue());
        } else if(value instanceof Double) {
            serializePrimitive(rec, fieldName, ((Double) value).doubleValue());
        } else if(value instanceof Boolean) {
            serializePrimitive(rec, fieldName, ((Boolean) value).booleanValue());
        } else if(value instanceof String) {
            serializeString(rec, fieldName, (String) value);
        } else if(value instanceof byte[]) {
            serializeBytes(rec, fieldName, (byte[]) value);
        } else {
            throw new RuntimeException("Primitive type " + value.getClass().getSimpleName() + " not supported!");
        }
    }

    @Override
    public void serializePrimitive(HollowSerializationRecord rec, String fieldName, int value) {
        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setInt(fieldName, value);
    }

    @Override
    public void serializePrimitive(HollowSerializationRecord rec, String fieldName, long value) {
        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setLong(fieldName, value);
    }

    @Override
    public void serializePrimitive(HollowSerializationRecord rec, String fieldName, float value) {
        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setFloat(fieldName, value);
    }

    @Override
    public void serializePrimitive(HollowSerializationRecord rec, String fieldName, double value) {
        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setDouble(fieldName, value);
    }

    @Override
    public void serializePrimitive(HollowSerializationRecord rec, String fieldName, boolean value) {
        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setBoolean(fieldName, value);
    }

    @Override
    public void serializeBytes(HollowSerializationRecord rec, String fieldName, byte[] value) {
        if(value == null)
            return;

        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setBytes(fieldName, value);
    }

    public void serializeString(HollowSerializationRecord rec, String fieldName, String value) {
        if(value == null)
            return;

        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        writeRec.setString(fieldName, value);
    }

    @Override
    @Deprecated
    public void serializeObject(HollowSerializationRecord rec, String fieldName, String typeName, Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serializeObject(HollowSerializationRecord rec, String fieldName, Object obj) {
        if(obj == null)
            return;

        HollowObjectWriteRecord writeRec = (HollowObjectWriteRecord) rec.getHollowWriteRecord();

        int position = rec.getSchema().getPosition(fieldName);
        TypedFieldDefinition fieldDef = (TypedFieldDefinition) rec.getSchema().getFieldDefinition(position);

        int ordinal = getFramework().add(fieldDef.getSubType(), obj);

        writeRec.setReference(fieldName, ordinal);
    }

    @Override
    public <T> void serializeList(HollowSerializationRecord rec, String fieldName, String typeName, Collection<T> obj) {
        if(obj == null)
            return;

        String subType = getSubType(rec.getSchema().getName(), fieldName);
        HollowSerializationRecord subRec = getRec(subType);
        int ordinal = serializeList(subRec, typeName, obj);
        ((HollowObjectWriteRecord) rec.getHollowWriteRecord()).setReference(fieldName, ordinal);
    }

    <T> int serializeList(HollowSerializationRecord rec, String typeName, Collection<T> obj) {
        HollowListWriteRecord listRec = (HollowListWriteRecord) rec.getHollowWriteRecord();
        listRec.reset();

        for(T t : obj) {
            if(t != null)
                listRec.addElement(getFramework().add(typeName, t));
        }

        return getFramework().getStateEngine().add(rec.getTypeName(), listRec);
    }

    @Override
    public <T> void serializeSet(HollowSerializationRecord rec, String fieldName, String typeName, Set<T> obj) {
        if(obj == null)
            return;

        String subType = getSubType(rec.getSchema().getName(), fieldName);
        HollowSerializationRecord subRec = getRec(subType);
        int ordinal = serializeSet(subRec, typeName, obj);
        ((HollowObjectWriteRecord) rec.getHollowWriteRecord()).setReference(fieldName, ordinal);
    }

    <T> int serializeSet(HollowSerializationRecord rec, String typeName, Set<T> obj) {
        HollowSetWriteRecord setRec = (HollowSetWriteRecord) rec.getHollowWriteRecord();
        setRec.reset();

        for(T t : obj) {
            if(t != null) {
                int ordinal = getFramework().add(typeName, t);
                int hashCode = hasher.hashCode(typeName, ordinal, t);
                setRec.addElement(ordinal, hashCode);
            }
        }

        return getFramework().getStateEngine().add(rec.getTypeName(), setRec);
    }

    @Override
    public <K, V> void serializeMap(HollowSerializationRecord rec, String fieldName, String keyTypeName, String valueTypeName, Map<K, V> obj) {
        if(obj == null)
            return;

        String subType = getSubType(rec.getSchema().getName(), fieldName);
        HollowSerializationRecord subRec = getRec(subType);
        int ordinal = serializeMap(subRec, keyTypeName, valueTypeName, obj);
        ((HollowObjectWriteRecord) rec.getHollowWriteRecord()).setReference(fieldName, ordinal);
    }

    <K, V> int serializeMap(HollowSerializationRecord rec, String keyTypeName, String valueTypeName, Map<K, V> obj) {
        HollowMapWriteRecord mapRec = (HollowMapWriteRecord) rec.getHollowWriteRecord();
        mapRec.reset();

        for(Map.Entry<K, V> entry : obj.entrySet()) {
            if(entry.getKey() != null && entry.getValue() != null) {
                int keyOrdinal = getFramework().add(keyTypeName, entry.getKey());
                int valueOrdinal = getFramework().add(valueTypeName, entry.getValue());
                int hashCode = hasher.hashCode(keyTypeName, keyOrdinal, entry.getKey());
                mapRec.addEntry(keyOrdinal, valueOrdinal, hashCode);
            }
        }

        return getFramework().getStateEngine().add(rec.getTypeName(), mapRec);
    }

    private final ThreadLocal<Map<String, HollowSerializationRecord>> serializationRecordHandle = new ThreadLocal<Map<String, HollowSerializationRecord>>();

    public HollowSerializationRecord getRec(String type) {
        Map<String, HollowSerializationRecord> map = serializationRecordHandle.get();
        if(map == null) {
            map = new HashMap<String, HollowSerializationRecord>();
            serializationRecordHandle.set(map);
        }

        HollowSerializationRecord rec = map.get(type);
        if(rec == null) {
            rec = createRec(type);
            map.put(type, rec);
        }

        return rec;
    }

    private HollowSerializationRecord createRec(String type) {
        HollowSchema schema = ((HollowSerializationFramework) framework).getHollowSchema(type);

        if(schema instanceof HollowListSchema) {
            return new HollowSerializationRecord(new HollowListWriteRecord(), type);
        } else if(schema instanceof HollowSetSchema) {
            return new HollowSerializationRecord(new HollowSetWriteRecord(), type);
        } else if(schema instanceof HollowMapSchema) {
            return new HollowSerializationRecord(new HollowMapWriteRecord(), type);
        }

        HollowSerializationRecord rec = new HollowSerializationRecord(new HollowObjectWriteRecord((HollowObjectSchema) schema), type);
        rec.setSchema(framework.getSerializer(type).getFastBlobSchema());
        return rec;
    }

    private HollowSerializationFramework getFramework() {
        return (HollowSerializationFramework) framework;
    }


    Map<String, Map<String, String>> subTypeMap = new ConcurrentHashMap<String, Map<String, String>>();

    private String getSubType(String typeName, String fieldName) {
        Map<String, String> map = subTypeMap.get(typeName);
        if(map == null) {
            map = new ConcurrentHashMap<String, String>();
            subTypeMap.put(typeName, map);
        }

        String subtype = map.get(fieldName);
        if(subtype == null) {
            subtype = typeName + "_" + fieldName;
            map.put(fieldName, subtype);
        }

        return subtype;
    }

}

