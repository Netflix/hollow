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
package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.tools.combine.OrdinalRemapper;
import java.util.HashMap;
import java.util.Map;

public class FlatRecordExtractor {
    
    private final HollowReadStateEngine extractFrom;
    private final FlatRecordWriter writer;
    private final ExtractorOrdinalRemapper ordinalRemapper;
    
    private final Map<String, HollowRecordCopier> recordCopiersByType;
    
    public FlatRecordExtractor(HollowReadStateEngine extractFrom, HollowSchemaIdentifierMapper schemaIdMapper) {
        this.extractFrom = extractFrom;
        this.writer = new FlatRecordWriter(extractFrom, schemaIdMapper);
        this.ordinalRemapper = new ExtractorOrdinalRemapper();
        this.recordCopiersByType = new HashMap<>();
    }

    public synchronized FlatRecord extract(String type, int ordinal) {
        ordinalRemapper.clear();
        writer.reset();
        
        HollowTypeReadState typeState = extractFrom.getTypeState(type);
        
        extractHollowRecord(type, typeState, ordinal);
        
        return writer.generateFlatRecord();
    }
    
    private void extractHollowRecord(String type, HollowTypeReadState typeState, int ordinal) {
        if (ordinal == -1) {
            return;
        }

        if (typeState == null) {
            throw new IllegalArgumentException("Type '" + type + "' not found in HollowReadStateEngine");
        }

        traverse(typeState, ordinal);
        
        HollowRecordCopier recordCopier = recordCopier(type);
        HollowWriteRecord rec = recordCopier.copy(ordinal);
        
        int flatOrdinal = writer.write(typeState.getSchema(), rec);
        ordinalRemapper.remapOrdinal(type, ordinal, flatOrdinal);
    }
    
    private void traverse(HollowTypeReadState typeState, int ordinal) {
        
        switch(typeState.getSchema().getSchemaType()) {
        case OBJECT:
            traverseObject((HollowObjectTypeReadState)typeState, ordinal);
            break;
        case LIST:
            traverseList((HollowListTypeReadState)typeState, ordinal);
            break;
        case SET:
            traverseSet((HollowSetTypeReadState)typeState, ordinal);
            break;
        case MAP:
            traverseMap((HollowMapTypeReadState)typeState, ordinal);
            break;
        }
    }
    
    private void traverseObject(HollowObjectTypeReadState typeState, int ordinal) {
        HollowObjectSchema schema = typeState.getSchema();
        
        for(int i=0;i<schema.numFields();i++) {
            if(schema.getFieldType(i) == FieldType.REFERENCE) {
                String refType = schema.getReferencedType(i);;
                HollowTypeReadState refTypeState = schema.getReferencedTypeState(i);
                int refOrdinal = typeState.readOrdinal(ordinal, i);
                extractHollowRecord(refType, refTypeState, refOrdinal);
            }
        }
    }
    
    private void traverseList(HollowListTypeReadState typeState, int ordinal) {
        HollowListSchema schema = typeState.getSchema();

        int size = typeState.size(ordinal);
        
        for(int i=0;i<size;i++) {
            int refOrdinal = typeState.getElementOrdinal(ordinal, i);
            if(refOrdinal != HollowConstants.ORDINAL_NONE)
                extractHollowRecord(schema.getElementType(), schema.getElementTypeState(), refOrdinal);
        }
    }
    
    private void traverseSet(HollowSetTypeReadState typeState, int ordinal) {
        HollowSetSchema schema = typeState.getSchema();
        
        HollowOrdinalIterator iter = typeState.ordinalIterator(ordinal);
        
        int refOrdinal = iter.next();
        while(refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            if(refOrdinal != HollowConstants.ORDINAL_NONE)
                extractHollowRecord(schema.getElementType(), schema.getElementTypeState(), refOrdinal);
            refOrdinal = iter.next();
        }
    }
    
    private void traverseMap(HollowMapTypeReadState typeState, int ordinal) {
        HollowMapSchema schema = typeState.getSchema();
        
        HollowMapEntryOrdinalIterator iter = typeState.ordinalIterator(ordinal);
        
        while(iter.next()) {
            if(iter.getKey() != HollowConstants.ORDINAL_NONE)
                extractHollowRecord(schema.getKeyType(), schema.getKeyTypeState(), iter.getKey());
            if(iter.getValue() != HollowConstants.ORDINAL_NONE)
                extractHollowRecord(schema.getValueType(), schema.getValueTypeState(), iter.getValue());
        }
    }
    
    private HollowRecordCopier recordCopier(String type) {
        HollowRecordCopier recordCopier = recordCopiersByType.get(type);
        if(recordCopier == null) {
            recordCopier = HollowRecordCopier.createCopier(extractFrom.getTypeState(type), ordinalRemapper, false);
            recordCopiersByType.put(type, recordCopier);
        }
        
        return recordCopier;
    }
    
    private static class ExtractorOrdinalRemapper implements OrdinalRemapper {

        private final Map<TypedOrdinal, Integer> mappedFlatOrdinals = new HashMap<>();
        
        @Override
        public int getMappedOrdinal(String type, int originalOrdinal) {
            return mappedFlatOrdinals.get(new TypedOrdinal(type, originalOrdinal));
        }

        @Override
        public void remapOrdinal(String type, int originalOrdinal, int mappedOrdinal) {
            mappedFlatOrdinals.put(new TypedOrdinal(type, originalOrdinal), mappedOrdinal);
        }

        @Override
        public boolean ordinalIsMapped(String type, int originalOrdinal) {
            throw new UnsupportedOperationException();
        }
        
        public void clear() {
            mappedFlatOrdinals.clear();
        }
        
        private static class TypedOrdinal {
            private final String type;
            private final int ordinal;
            
            public TypedOrdinal(String type, int ordinal) {
                this.type = type;
                this.ordinal = ordinal;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ordinal;
                result = prime * result + ((type == null) ? 0 : type.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                TypedOrdinal other = (TypedOrdinal) obj;
                if (ordinal != other.ordinal)
                    return false;
                if (type == null) {
                    if (other.type != null)
                        return false;
                } else if (!type.equals(other.type))
                    return false;
                return true;
            }
        }
        
    }
}
