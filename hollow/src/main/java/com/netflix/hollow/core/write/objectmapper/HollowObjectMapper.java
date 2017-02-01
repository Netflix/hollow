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

import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HollowObjectMapper {

    private final HollowWriteStateEngine stateEngine;

    private final ConcurrentHashMap<String, HollowTypeMapper> typeMappers;

    private AtomicInteger unassignedTypeCounter = new AtomicInteger(0);

    private boolean ignoreListOrdering = false;
    private boolean useDefaultHashKeys = true;

    public HollowObjectMapper(HollowWriteStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.typeMappers = new ConcurrentHashMap<String, HollowTypeMapper>();
    }

    public void ignoreListOrdering() {
        this.ignoreListOrdering = true;
    }
    
    public void useDefaultHashKeys() {
        this.useDefaultHashKeys = true;
    }
    
    public void doNotUseDefaultHashKeys() {
        this.useDefaultHashKeys = false;
    }

    public int add(Object o) {
        HollowTypeMapper typeMapper = getTypeMapper(o.getClass(), null, null);
        return typeMapper.write(o);
    }

    /** @deprecated use {@link #add(Object)}. */
    @Deprecated
    public int addObject(Object o) {
        return add(o);
    }
    
    public void initializeTypeState(Class<?> clazz) {
        getTypeMapper(clazz, null, null);
    }

    HollowTypeMapper getTypeMapper(Type type, String declaredName, String[] hashKeyFieldPaths) {
        return getTypeMapper(type, declaredName, hashKeyFieldPaths, -1, null);
    }
    
    HollowTypeMapper getTypeMapper(Type type, String declaredName, String[] hashKeyFieldPaths, int numShards, Set<Type> visited) {
        String typeName = declaredName != null ? declaredName : HollowObjectTypeMapper.getDefaultTypeName(type);

        HollowTypeMapper typeMapper = typeMappers.get(typeName);

        if(typeMapper == null) {
            
            if(visited == null)
                visited = new HashSet<Type>();

            if(type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)type;
                Class<?> clazz = (Class<?>) parameterizedType.getRawType();

                if(List.class.isAssignableFrom(clazz)) {
                    typeMapper = new HollowListTypeMapper(this, parameterizedType, declaredName, numShards, ignoreListOrdering, visited);
                } else if(Set.class.isAssignableFrom(clazz)) {
                    typeMapper = new HollowSetTypeMapper(this, parameterizedType, declaredName, hashKeyFieldPaths, numShards, stateEngine, useDefaultHashKeys, visited);
                } else if(Map.class.isAssignableFrom(clazz)) {
                    typeMapper = new HollowMapTypeMapper(this, parameterizedType, declaredName, hashKeyFieldPaths, numShards, stateEngine, useDefaultHashKeys, visited);
                } else {
                    return getTypeMapper(clazz, declaredName, hashKeyFieldPaths, -1, visited);
                }

            } else {
                typeMapper = new HollowObjectTypeMapper(this, (Class<?>)type, declaredName, visited);
            }

            HollowTypeMapper existing = typeMappers.putIfAbsent(typeName, typeMapper);
            if(existing != null) {
                typeMapper = existing;
            } else {
                typeMapper.addTypeState(stateEngine);
            }
        }

        return typeMapper;
    }

    int nextUnassignedTypeId() {
        return unassignedTypeCounter.getAndIncrement();
    }

    public HollowWriteStateEngine getStateEngine() {
        return stateEngine;
    }

}
