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

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class HollowListTypeMapper extends HollowTypeMapper {

    private final HollowListSchema schema;
    private final HollowListTypeWriteState writeState;

    private final ThreadLocal<IntList> intList = new ThreadLocal<IntList>();
    private final boolean ignoreListOrdering;

    private final HollowTypeMapper elementMapper;

    public HollowListTypeMapper(HollowObjectMapper parentMapper, ParameterizedType type, String declaredName, int numShards, boolean ignoreListOrdering, Set<Type> visited) {
        this.elementMapper = parentMapper.getTypeMapper(type.getActualTypeArguments()[0], null, null, -1, visited);
        String typeName = declaredName != null ? declaredName : getDefaultTypeName(type);
        this.schema = new HollowListSchema(typeName, elementMapper.getTypeName());
        this.ignoreListOrdering = ignoreListOrdering;

        HollowListTypeWriteState existingTypeState = (HollowListTypeWriteState)parentMapper.getStateEngine().getTypeState(typeName);
        this.writeState = existingTypeState != null ? existingTypeState : new HollowListTypeWriteState(schema, numShards);
    }

    @Override
    public String getTypeName() {
        return schema.getName();
    }

    @Override
    public int write(Object obj) {
        if(obj instanceof MemoizedList) {
            long assignedOrdinal = ((MemoizedList<?>)obj).__assigned_ordinal;
            
            if((assignedOrdinal & ASSIGNED_ORDINAL_CYCLE_MASK) == cycleSpecificAssignedOrdinalBits())
                return (int)assignedOrdinal & Integer.MAX_VALUE;
        }

        List<?> l = (List<?>)obj;

        HollowListWriteRecord rec = (HollowListWriteRecord)writeRecord();
        if(ignoreListOrdering) {
            IntList ordinalList = getIntList();
            for(Object o : l) {
                int ordinal = elementMapper.write(o);
                ordinalList.add(ordinal);
            }
            ordinalList.sort();
            for(int i=0;i<ordinalList.size();i++)
                rec.addElement(ordinalList.get(i));
        } else {
            for(Object o : l) {
                int ordinal = elementMapper.write(o);
                rec.addElement(ordinal);
            }
        }

        int assignedOrdinal = writeState.add(rec);

        if(obj instanceof MemoizedList) {
            ((MemoizedList<?>)obj).__assigned_ordinal = (long)assignedOrdinal | cycleSpecificAssignedOrdinalBits();
        }

        return assignedOrdinal;
    }

    @Override
    protected HollowWriteRecord newWriteRecord() {
        return new HollowListWriteRecord();
    }

    private IntList getIntList() {
        IntList list = intList.get();
        if(list == null) {
            list = new IntList();
            intList.set(list);
        }
        list.clear();
        return list;
    }

    @Override
    protected HollowTypeWriteState getTypeWriteState() {
        return writeState;
    }

}
