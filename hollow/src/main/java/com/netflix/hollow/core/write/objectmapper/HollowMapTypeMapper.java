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

import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class HollowMapTypeMapper extends HollowTypeMapper {

    private static final String NULL_KEY_MESSAGE =
            "Null key contained in instance of a Map with schema \"%s\". Maps cannot contain null keys or values";

    private static final String NULL_VALUE_MESSAGE =
            "Null value contained in instance of a Map with schema \"%s\". Maps cannot contain null keys or values";

    private final HollowMapSchema schema;
    private final HollowMapTypeWriteState writeState;

    private final HollowObjectHashCodeFinder hashCodeFinder;
    
    private HollowTypeMapper keyMapper;
    private HollowTypeMapper valueMapper;

    public HollowMapTypeMapper(HollowObjectMapper parentMapper, ParameterizedType type, String declaredName, String[] hashKeyFieldPaths, int numShards, HollowWriteStateEngine stateEngine, boolean useDefaultHashKeys, Set<Type> visited) {
        this.keyMapper = parentMapper.getTypeMapper(type.getActualTypeArguments()[0], null, null, -1, visited);
        this.valueMapper = parentMapper.getTypeMapper(type.getActualTypeArguments()[1], null, null, -1, visited);
        String typeName = declaredName != null ? declaredName : getDefaultTypeName(type);
        
        if(hashKeyFieldPaths == null && useDefaultHashKeys && (keyMapper instanceof HollowObjectTypeMapper))
            hashKeyFieldPaths = ((HollowObjectTypeMapper)keyMapper).getDefaultElementHashKey();
        
        this.schema = new HollowMapSchema(typeName, keyMapper.getTypeName(), valueMapper.getTypeName(), hashKeyFieldPaths);
        this.hashCodeFinder = stateEngine.getHashCodeFinder();

        HollowMapTypeWriteState typeState = (HollowMapTypeWriteState) parentMapper.getStateEngine().getTypeState(typeName);
        this.writeState = typeState != null ? typeState : new HollowMapTypeWriteState(schema, numShards);
    }

    @Override
    protected String getTypeName() {
        return schema.getName();
    }

    @Override
    protected int write(Object obj) {
        if(obj instanceof MemoizedMap) {
            long assignedOrdinal = ((MemoizedMap<?, ?>)obj).__assigned_ordinal;
            
            if((assignedOrdinal & ASSIGNED_ORDINAL_CYCLE_MASK) == cycleSpecificAssignedOrdinalBits())
                return (int)assignedOrdinal & Integer.MAX_VALUE;
        }

        Map<?, ?> m = (Map<?, ?>)obj;

        HollowMapWriteRecord rec = copyToWriteRecord(m, null);

        int assignedOrdinal = writeState.add(rec);
        
        if(obj instanceof MemoizedMap) {
            ((MemoizedMap<?, ?>)obj).__assigned_ordinal = (long)assignedOrdinal | cycleSpecificAssignedOrdinalBits();
        }
        
        return assignedOrdinal;
    }
    
    @Override
    protected int writeFlat(Object obj, FlatRecordWriter flatRecordWriter) {
    	HollowMapWriteRecord rec = copyToWriteRecord((Map<?,?>)obj, flatRecordWriter);
    	return flatRecordWriter.write(schema, rec);
    }

    private HollowMapWriteRecord copyToWriteRecord(Map<?, ?> m, FlatRecordWriter flatRecordWriter) {
        HollowMapWriteRecord rec = (HollowMapWriteRecord) writeRecord();
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            Object key = entry.getKey();
            if (key == null) {
                throw new NullPointerException(String.format(NULL_KEY_MESSAGE, schema));
            }
            Object value = entry.getValue();
            if (value == null) {
                throw new NullPointerException(String.format(NULL_VALUE_MESSAGE, schema));
            }

            int keyOrdinal, valueOrdinal;
            if (flatRecordWriter == null) {
                keyOrdinal = keyMapper.write(key);
                valueOrdinal = valueMapper.write(value);
            } else {
                keyOrdinal = keyMapper.writeFlat(key, flatRecordWriter);
                valueOrdinal = valueMapper.writeFlat(value, flatRecordWriter);
            }

            int hashCode = hashCodeFinder.hashCode(keyMapper.getTypeName(), keyOrdinal, key);

            rec.addEntry(keyOrdinal, valueOrdinal, hashCode);
        }
        return rec;
    }

    @Override
    protected HollowWriteRecord newWriteRecord() {
        return new HollowMapWriteRecord();
    }

    @Override
    protected HollowTypeWriteState getTypeWriteState() {
        return writeState;
    }

}
