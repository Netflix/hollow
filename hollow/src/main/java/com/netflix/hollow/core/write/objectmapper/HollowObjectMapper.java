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

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalNode;
import com.netflix.hollow.core.write.objectmapper.flatrecords.traversal.FlatRecordTraversalObjectNode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HollowObjectMapper {

    private final HollowWriteStateEngine stateEngine;

    private final ConcurrentHashMap<Type, String> typeNameMappers;
    private final ConcurrentHashMap<String, HollowTypeMapper> typeMappers;

    private AtomicInteger unassignedTypeCounter = new AtomicInteger(0);

    private boolean ignoreListOrdering = false;
    private boolean useDefaultHashKeys = true;

    public HollowObjectMapper(HollowWriteStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.typeNameMappers = new ConcurrentHashMap<>();
        this.typeMappers = new ConcurrentHashMap<>();
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

    /**
     * Adds the specified POJO to the state engine.
     * <p>
     * Unless previously initialized with {@link #initializeTypeState(Class)},
     * the first time an instance of a particular type is added
     * its schema is derived and added to the data model.
     *
     * @param o the POJO to add
     * @return the ordinal assigned to the newly added object
     */
    public int add(Object o) {
        HollowTypeMapper typeMapper = getTypeMapper(o.getClass(), null, null);
        return typeMapper.write(o);
    }

    public <T> T readHollowRecord(HollowRecord record) {
        HollowTypeMapper typeMapper = typeMappers.get(record.getSchema().getName());
        if (typeMapper == null) {
            throw new IllegalArgumentException("No type mapper found for schema " + record.getSchema().getName());
        }
        return (T) typeMapper.parseHollowRecord(record);
    }
    
    public void writeFlat(Object o, FlatRecordWriter flatRecordWriter) {
    	HollowTypeMapper typeMapper = getTypeMapper(o.getClass(), null, null);
    	typeMapper.writeFlat(o, flatRecordWriter);
    }

    public <T> T readFlat(FlatRecordTraversalNode node) {
        String schemaName = node.getSchema().getName();
        HollowTypeMapper typeMapper = typeMappers.get(schemaName);
        if (typeMapper == null) {
            throw new IllegalArgumentException("No type mapper found for schema " + schemaName);
        }
        Object obj = typeMapper.parseFlatRecord(node);
        return (T) obj;
    }

    public <T> T readFlat(FlatRecord record) {
        FlatRecordTraversalNode node = new FlatRecordTraversalObjectNode(record);
        return readFlat(node);
    }
    
    /**
     * Extracts the primary key from the specified POJO.
     *
     * @param o the POJO
     * @return the primary key
     * @throws IllegalArgumentException if the POJO does not have primary key defined
     */
    public RecordPrimaryKey extractPrimaryKey(Object o) {
        HollowObjectTypeMapper typeMapper = (HollowObjectTypeMapper) getTypeMapper(o.getClass(), null, null);
        return new RecordPrimaryKey(typeMapper.getTypeName(), typeMapper.extractPrimaryKey(o));
    }

    /**
     * @param o the object to add
     * @return the ordinal assigned to the newly added object
     * @deprecated use {@link #add(Object)} instead.
     */
    @Deprecated
    public int addObject(Object o) {
        return add(o);
    }

    /**
     * Initializes the schema for the specified type in the data model.
     * <p>
     * The schema will be derived from the field and type names in
     * <code>clazz</code> and added to the state engine's data model;
     * schemas of types referenced from <code>clazz</code> will also be added.
     * This can be used to add a type's schema to the state engine
     * without having to add any data for that type.
     *
     * @param clazz type whose schema to derive and add to the data model
     * @see #add(Object)
     */
    public void initializeTypeState(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        getTypeMapper(clazz, null, null);
    }

    HollowTypeMapper getTypeMapper(Type type, String declaredName, String[] hashKeyFieldPaths) {
        return getTypeMapper(type, declaredName, hashKeyFieldPaths, -1, false, null);
    }

    HollowTypeMapper getTypeMapper(
            Type type, String declaredName, String[] hashKeyFieldPaths, int numShards, Set<Type> visited) { // SNAP: refactor all to below usage
        return getTypeMapper(type, declaredName, hashKeyFieldPaths, numShards, false, visited);
    }

    HollowTypeMapper getTypeMapper(
            Type type, String declaredName, String[] hashKeyFieldPaths, int numShards, boolean isNumShardsPinned, Set<Type> visited) {

        // Compute the type name
        String typeName = declaredName != null
                ? declaredName
                : findTypeName(type);
        HollowTypeMapper typeMapper = typeMappers.get(typeName);

        if (typeMapper == null) {
            if (visited == null) {
                // Used to detect circular references in the model
                // See HollowObjectTypeMapper and MappedField
                visited = new HashSet<>();
            }

            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> clazz = (Class<?>) parameterizedType.getRawType();

                if (List.class.isAssignableFrom(clazz)) {
                    typeMapper = new HollowListTypeMapper(this, parameterizedType, typeName,
                            numShards, isNumShardsPinned, ignoreListOrdering, visited);
                } else if (Set.class.isAssignableFrom(clazz)) {
                    typeMapper = new HollowSetTypeMapper(this, parameterizedType, typeName, hashKeyFieldPaths,
                            numShards, isNumShardsPinned, stateEngine, useDefaultHashKeys, visited);
                } else if (Map.class.isAssignableFrom(clazz)) {
                    typeMapper = new HollowMapTypeMapper(this, parameterizedType, typeName, hashKeyFieldPaths,
                            numShards, isNumShardsPinned, stateEngine, useDefaultHashKeys, visited);
                } else {
                    typeMapper = new HollowObjectTypeMapper(this, clazz, typeName, visited);
                }
            } else {
                typeMapper = new HollowObjectTypeMapper(this, (Class<?>) type, typeName, visited);
            }

            HollowTypeMapper existing = typeMappers.putIfAbsent(typeName, typeMapper);
            if (existing != null) {
                typeMapper = existing;
            } else {
                typeMapper.addTypeState(stateEngine);
            }
        }

        return typeMapper;
    }

    private String findTypeName(Type type) {
        String typeName = typeNameMappers.get(type);
        if(typeName == null) {
            typeName = HollowObjectTypeMapper.getDefaultTypeName(type);
            typeNameMappers.putIfAbsent(type, typeName);
        }
        return typeName;
    }

    int nextUnassignedTypeId() {
        return unassignedTypeCounter.getAndIncrement();
    }

    public HollowWriteStateEngine getStateEngine() {
        return stateEngine;
    }

}
