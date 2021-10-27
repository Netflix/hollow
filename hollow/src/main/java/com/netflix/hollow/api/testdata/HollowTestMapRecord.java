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

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.List;

public abstract class HollowTestMapRecord<T> extends HollowTestRecord<T> {

    private final List<Entry<?>> entries = new ArrayList<>();

    protected HollowTestMapRecord(T parent) {
        super(parent);
    }
    
    protected void addEntry(Entry<? extends HollowTestRecord<?>> entry) {
        entries.add(entry);
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    public <T> Entry<T> getEntry(int idx) {
        return (Entry<T>) entries.get(idx);
    }
    
    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(Entry<?> entry : entries) {
            int keyOrdinal = entry.key.addTo(writeEngine);
            int valueOrdinal = entry.value.addTo(writeEngine);
            rec.addEntry(keyOrdinal, valueOrdinal);
        }
        return rec;
    }
    
    public static class Entry<T> extends HollowTestRecord<T> {

        private HollowTestRecord<?> key;
        private HollowTestRecord<?> value;
        
        public Entry(T parent) {
            super(parent);
        }
        
        protected void setKey(HollowTestRecord<?> key) {
            this.key = key;
        }
        
        protected void setValue(HollowTestRecord<?> value) {
            this.value = value;
        }
        
        @SuppressWarnings({ "hiding", "unchecked" })
        public <T extends HollowTestRecord<?>> T getKey() {
            return (T)key;
        }

        @SuppressWarnings({ "hiding", "unchecked" })
        public <T extends HollowTestRecord<?>> T getValue() {
            return (T)value;
        }

        @Override
        protected HollowSchema getSchema() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
            throw new UnsupportedOperationException();
        }
    }
    
}
