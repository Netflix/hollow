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

import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class HollowTypeMapper {

    public static final long ASSIGNED_ORDINAL_CYCLE_MASK = 0xFFFFFFFF00000000L;

    private final ThreadLocal<HollowWriteRecord> writeRec = new ThreadLocal<HollowWriteRecord>();

    protected abstract String getTypeName();

    protected abstract int write(Object obj);

    protected abstract HollowWriteRecord newWriteRecord();

    protected abstract HollowTypeWriteState getTypeWriteState();

    protected void addTypeState(HollowWriteStateEngine stateEngine) {
        if(stateEngine.getTypeState(getTypeName()) == null)
            stateEngine.addTypeState(getTypeWriteState());
    }

    protected HollowWriteRecord writeRecord() {
        HollowWriteRecord rec = writeRec.get();
        if(rec == null) {
            rec = newWriteRecord();
            writeRec.set(rec);
        }
        rec.reset();
        return rec;
    }

    protected static String getDefaultTypeName(Type type) {
        if(type instanceof Class) {
            Class<?> clazz = (Class<?>)type;
            HollowTypeName explicitTypeName = clazz.getAnnotation(HollowTypeName.class);
            if(explicitTypeName != null)
                return explicitTypeName.name();
            return clazz.getSimpleName();
        }

        ParameterizedType parameterizedType = (ParameterizedType)type;
        Class<?> clazz = (Class<?>)parameterizedType.getRawType();

        if(List.class.isAssignableFrom(clazz))
            return "ListOf" + getDefaultTypeName(parameterizedType.getActualTypeArguments()[0]);
        if(Set.class.isAssignableFrom(clazz))
            return "SetOf" + getDefaultTypeName(parameterizedType.getActualTypeArguments()[0]);
        if(Map.class.isAssignableFrom(clazz))
            return "MapOf" + getDefaultTypeName(parameterizedType.getActualTypeArguments()[0]) + "To" + getDefaultTypeName(parameterizedType.getActualTypeArguments()[1]);

        return clazz.getSimpleName();
    }
    
    protected long cycleSpecificAssignedOrdinalBits() {
        return getTypeWriteState().getStateEngine().getNextStateRandomizedTag() & ASSIGNED_ORDINAL_CYCLE_MASK;
    }
}
