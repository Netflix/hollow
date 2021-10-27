/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.testdata;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.HashMap;
import java.util.Map;

public abstract class HollowTestObjectRecord<T> extends HollowTestRecord<T> {
    
    private final Map<String, Field> fields;
    
    protected HollowTestObjectRecord(T parent) {
        super(parent);
        this.fields = new HashMap<>();
    }
    
    protected void addField(String fieldName, Object value) {
        if(value == null) {
            fields.remove(fieldName);
        } else {
            addField(new Field(fieldName, value));
        }
    }
    
    protected void addField(Field field) {
        if(field.value == null)
            this.fields.remove(field.name);
        else
            this.fields.put(field.name, field);
    }
    
    protected Field getField(String name) {
        return fields.get(name);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(getSchema());
        
        for(Map.Entry<String, Field> entry : fields.entrySet()) {
            Field field = entry.getValue();
            if(field.value instanceof Integer) {
                rec.setInt(field.name, (Integer)field.value);
            } else if(field.value instanceof Long) {
                rec.setLong(field.name, (Long)field.value);
            } else if(field.value instanceof Float) {
                rec.setFloat(field.name, (Float)field.value);
            } else if(field.value instanceof Boolean) {
                rec.setBoolean(field.name, (Boolean)field.value);
            } else if(field.value instanceof String) {
                rec.setString(field.name, (String)field.value);
            } else if(field.value instanceof byte[]) {
                rec.setBytes(field.name, (byte[])field.value);
            } else if(field.value instanceof HollowTestRecord) {
                rec.setReference(field.name, ((HollowTestRecord)field.value).addTo(writeEngine));
            } else {
                throw new IllegalStateException("Unknown field type: " + field.value.getClass());
            }
        }
        
        return rec;
    }
    
    protected abstract HollowObjectSchema getSchema();
    
    protected static class Field {
        public final String name;
        public final Object value;
        
        public Field(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

}
