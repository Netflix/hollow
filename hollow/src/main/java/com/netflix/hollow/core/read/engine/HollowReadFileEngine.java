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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.api.error.SchemaNotFoundException;
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.missing.DefaultMissingDataHandler;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A HollowReadStateEngine is our main handle to the current state of a Hollow dataset as a data consumer.
 * <p>
 * A dataset changes over time.  A core concept in Hollow is that the timeline for a changing dataset can be 
 * broken down into discrete data states, each of which is a complete snapshot of the data at a particular point in time.
 * Data consumers handle data states with a HollowReadStateEngine.
 */
public class HollowReadFileEngine implements HollowStateEngine, HollowDataAccess {

    private final Map<String, HollowTypeReadState> typeStates;
    private final Map<String, List<HollowTypeStateListener>> listeners;
    private final HollowObjectHashCodeFinder hashCodeFinder;
    private final boolean listenToAllPopulatedOrdinals;
    private ArraySegmentRecycler memoryRecycler;
    private Map<String,String> headerTags;
    private Set<String> typesWithDefinedHashCodes = new HashSet<String>();

    private long currentRandomizedTag;

    private MissingDataHandler missingDataHandler = new DefaultMissingDataHandler();

    public HollowReadFileEngine() {
        this(DefaultHashCodeFinder.INSTANCE, true, new RecyclingRecycler());
    }

    public HollowReadFileEngine(boolean listenToAllPopulatedOrdinals) {
        this(DefaultHashCodeFinder.INSTANCE, listenToAllPopulatedOrdinals, new RecyclingRecycler());
    }

    public HollowReadFileEngine(ArraySegmentRecycler recycler) {
        this(DefaultHashCodeFinder.INSTANCE, true, recycler);
    }

    public HollowReadFileEngine(boolean listenToAllPopulatedOrdinals, ArraySegmentRecycler recycler) {
        this(DefaultHashCodeFinder.INSTANCE, listenToAllPopulatedOrdinals, recycler);
    }

    @Deprecated
    public HollowReadFileEngine(HollowObjectHashCodeFinder hashCodeFinder) {
        this(hashCodeFinder, true, new RecyclingRecycler());
    }

    @Deprecated
    public HollowReadFileEngine(HollowObjectHashCodeFinder hashCodeFinder, boolean listenToAllPopulatedOrdinals, ArraySegmentRecycler recycler) {
        this.typeStates = new HashMap<String, HollowTypeReadState>();
        this.listeners = new HashMap<String, List<HollowTypeStateListener>>();
        this.hashCodeFinder = hashCodeFinder;
        this.memoryRecycler = recycler;
        this.listenToAllPopulatedOrdinals = listenToAllPopulatedOrdinals;
    }

    @Override
    public HollowObjectHashCodeFinder getHashCodeFinder() {
        return hashCodeFinder;
    }

    protected void addTypeState(HollowTypeReadState typeState) {
        typeStates.put(typeState.getSchema().getName(), typeState);

        if(listenToAllPopulatedOrdinals) {
            typeState.addListener(new PopulatedOrdinalListener());
        }

        List<HollowTypeStateListener> list = listeners.get(typeState.getSchema().getName());
        if(list != null) {
            for(HollowTypeStateListener listener : list)
                typeState.addListener(listener);
        }
    }

    /**
     * Add a {@link HollowTypeStateListener} to a type.
     *
     * @param typeName the type name
     * @param listener the listener to add
     */
    public void addTypeListener(String typeName, HollowTypeStateListener listener) {
        List<HollowTypeStateListener> list = listeners.get(typeName);
        if(list == null) {
            list = new ArrayList<HollowTypeStateListener>();
            listeners.put(typeName, list);
        }

        list.add(listener);

        HollowTypeReadState typeState = typeStates.get(typeName);
        if(typeState != null)
            typeState.addListener(listener);
    }

    void wireTypeStatesToSchemas() {
        for(HollowTypeReadState state : typeStates.values()) {
            switch(state.getSchema().getSchemaType()) {
            case OBJECT:
                HollowObjectSchema objSchema = (HollowObjectSchema)state.getSchema();
                for(int i=0;i<objSchema.numFields();i++) {
                    if(objSchema.getReferencedType(i) != null)
                        objSchema.setReferencedTypeState(i, typeStates.get(objSchema.getReferencedType(i)));
                }
                break;
            case LIST:
                HollowListSchema listSchema = (HollowListSchema)state.getSchema();
                listSchema.setElementTypeState(typeStates.get(listSchema.getElementType()));
                break;
            case SET:
                HollowSetSchema setSchema = (HollowSetSchema)state.getSchema();
                setSchema.setElementTypeState(typeStates.get(setSchema.getElementType()));
                ((HollowSetTypeReadState)state).buildKeyDeriver();
                break;
            case MAP:
                HollowMapSchema mapSchema = (HollowMapSchema)state.getSchema();
                mapSchema.setKeyTypeState(typeStates.get(mapSchema.getKeyType()));
                mapSchema.setValueTypeState(typeStates.get(mapSchema.getValueType()));
                ((HollowMapTypeReadState)state).buildKeyDeriver();
                break;
            }
        }
    }

    /**
     * Calculates the data size of a read state engine which is defined as the approximate heap footprint by iterating
     * over the read state shards in each type state
     * @return the heap footprint of the read state engine
     */
    public long calcApproxDataSize() {
        return this.getAllTypes()
                .stream()
                .map(this::getTypeState)
                .mapToLong(HollowTypeReadState::getApproximateHeapFootprintInBytes)
                .sum();
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String type) {
        return typeStates.get(type);
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String type, int ordinal) {
        return typeStates.get(type);
    }

    @Override
    public Collection<String> getAllTypes() {
        return typeStates.keySet();
    }

    public HollowTypeReadState getTypeState(String type) {
        return typeStates.get(type);
    }

    public Collection<HollowTypeReadState> getTypeStates() {
        return typeStates.values();
    }

    public ArraySegmentRecycler getMemoryRecycler() {
        return memoryRecycler;
    }

    public boolean isListenToAllPopulatedOrdinals() {
        return listenToAllPopulatedOrdinals;
    }

    @Override
    public List<HollowSchema> getSchemas() {
        List<HollowSchema> schemas = new ArrayList<HollowSchema>();

        for(Map.Entry<String, HollowTypeReadState> entry : typeStates.entrySet()) {
            schemas.add(entry.getValue().getSchema());
        }

        return schemas;
    }
    
    @Override
    public HollowSchema getSchema(String type) {
        HollowTypeReadState typeState = getTypeState(type);
        return typeState == null ? null : typeState.getSchema();
    }

    @Override
    public HollowSchema getNonNullSchema(String type) {
        HollowSchema schema = getSchema(type);
        if (schema == null) {
            throw new SchemaNotFoundException(type, getAllTypes());
        }
        return schema;
    }

    protected void afterInitialization() { }

    public void setMissingDataHandler(MissingDataHandler handler) {
        this.missingDataHandler = handler;
    }

    @Override
    public MissingDataHandler getMissingDataHandler() {
        return missingDataHandler;
    }

    public void setHeaderTags(Map<String, String> headerTags) {
        this.headerTags = headerTags;
        populatedDefinedHashCodesTypesIfHeaderTagIsPresent();
    }

    @Override
    public Map<String, String> getHeaderTags() {
        return headerTags;
    }

    @Override
    public String getHeaderTag(String name) {
        return headerTags.get(name);
    }

    public void invalidate() {
        listeners.clear();

        for(Map.Entry<String, HollowTypeReadState> entry : typeStates.entrySet())
            entry.getValue().invalidate();

        memoryRecycler = null;
    }

    @Override
    public void resetSampling() {
        for(Map.Entry<String, HollowTypeReadState> entry : typeStates.entrySet())
            entry.getValue().getSampler().reset();
    }

    @Override
    public boolean hasSampleResults() {
        for(Map.Entry<String, HollowTypeReadState> entry : typeStates.entrySet())
            if(entry.getValue().getSampler().hasSampleResults())
                return true;
        return false;
    }

    public boolean updatedLastCycle() {
        for(Map.Entry<String, HollowTypeReadState> entry : typeStates.entrySet()) {
            if(entry.getValue().getListener(PopulatedOrdinalListener.class).updatedLastCycle())
                return true;
        }
        return false;
    }

    public Set<String> getTypesWithDefinedHashCodes() {
        return typesWithDefinedHashCodes;
    }

    public long getCurrentRandomizedTag() {
        return currentRandomizedTag;
    }

    public void setCurrentRandomizedTag(long currentRandomizedTag) {
        this.currentRandomizedTag = currentRandomizedTag;
    }

    private void populatedDefinedHashCodesTypesIfHeaderTagIsPresent() {
        String definedHashCodesTag = headerTags.get(HollowObjectHashCodeFinder.DEFINED_HASH_CODES_HEADER_NAME);
        if(definedHashCodesTag == null || "".equals(definedHashCodesTag)) {
            this.typesWithDefinedHashCodes = Collections.<String>emptySet();
        } else {
            Set<String>definedHashCodeTypes = new HashSet<String>();
            for(String type : definedHashCodesTag.split(","))
                definedHashCodeTypes.add(type);
            this.typesWithDefinedHashCodes = definedHashCodeTypes;
        }
    }

}
