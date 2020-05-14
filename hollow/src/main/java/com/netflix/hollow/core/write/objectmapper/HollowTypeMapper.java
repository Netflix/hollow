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

import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HollowTypeMapper {

    public static final long ASSIGNED_ORDINAL_CYCLE_MASK = 0xFFFFFFFF00000000L;

    private final ThreadLocal<HollowWriteRecord> writeRec = new ThreadLocal<>();
    
    private final ThreadLocal<ByteDataBuffer> flatRecBuffer = new ThreadLocal<>();

    protected abstract String getTypeName();

    protected abstract int write(Object obj);

    protected abstract int writeFlat(Object obj, FlatRecordWriter flatRecordWriter);
    
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
    
    protected ByteDataBuffer flatRecBuffer() {
    	ByteDataBuffer buf = flatRecBuffer.get();
    	if(buf == null) {
    		buf = new ByteDataBuffer();
    		flatRecBuffer.set(buf);
    	}
    	buf.reset();
    	return buf;
    }

    /**
     * Calculates the type name from a given type.
     * <p>
     * If the type is annotated with {@link HollowTypeName} then the type name
     * is the value of the {@code HollowTypeName.name} attribute. Otherwise
     * the type name is derived from the type itself.
     * If the type is a {@code Class} then the type name is the simple name of
     * that class.
     * If the type is a parameterized type and is assignable to a class of {@code List},
     * {@code Set}, or {@code Map} then the type name begins with the simple class name of
     * the parameterized type's raw type, followed by "Of", followed by the result of
     * calling this method with the associated parameterized types (in order, in-fixed by "To").
     * Otherwise, the type name is the simple class name of the parameterized type's raw type.
     * <p>
     * The translation from type to type name is lossy since the simple class name of a class
     * is used.  This means that no two types, from different packages, but with the same simple
     * name can be utilized.
     *
     * @param type the type
     * @return the type name.
     */
    public static String getDefaultTypeName(Type type) {
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
