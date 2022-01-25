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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.api.client.StackTraceRecorder;
import com.netflix.hollow.api.error.SchemaNotFoundException;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.tools.combine.OrdinalRemapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link HollowDataAccess} from a historical state.
 */
public class HollowHistoricalStateDataAccess implements HollowDataAccess {

    private final HollowHistory totalHistory;
    private long version;
    private final OrdinalRemapper removedCopyOrdinalMapping;
    private final Map<String, HollowHistoricalSchemaChange> schemaChanges;
    private final Map<String, HollowHistoricalTypeDataAccess> typeDataAccessMap;

    private final HollowObjectHashCodeFinder hashCodeFinder;
    private final MissingDataHandler missingDataHandler;

    private HollowDataAccess nextState;

    private StackTraceRecorder stackTraceRecorder;

    public HollowHistoricalStateDataAccess(HollowHistory totalHistory, long version, HollowReadStateEngine removedRecordCopies, OrdinalRemapper removedCopyOrdinalMappings, Map<String, HollowHistoricalSchemaChange> schemaChanges) {
        this(totalHistory, version, removedRecordCopies, removedRecordCopies.getTypeStates(), removedCopyOrdinalMappings, schemaChanges);
    }

    public HollowHistoricalStateDataAccess(HollowHistory totalHistory, long version, HollowReadStateEngine removedRecordCopies, Collection<HollowTypeReadState> typeStates, OrdinalRemapper removedCopyOrdinalMappings, Map<String, HollowHistoricalSchemaChange> schemaChanges) {
        this.totalHistory = totalHistory;
        this.version = version;
        this.hashCodeFinder = removedRecordCopies.getHashCodeFinder();
        this.missingDataHandler = removedRecordCopies.getMissingDataHandler();

        this.removedCopyOrdinalMapping = removedCopyOrdinalMappings;
        this.schemaChanges = schemaChanges;

        Map<String, HollowHistoricalTypeDataAccess> typeDataAccessMap = new HashMap<String, HollowHistoricalTypeDataAccess>();

        for(HollowTypeReadState typeState : typeStates) {
            String typeName = typeState.getSchema().getName();

            switch(typeState.getSchema().getSchemaType()) {
            case OBJECT:
                typeDataAccessMap.put(typeName, new HollowHistoricalObjectDataAccess(this, typeState));
                break;
            case LIST:
                typeDataAccessMap.put(typeName, new HollowHistoricalListDataAccess(this, typeState));
                break;
            case SET:
                typeDataAccessMap.put(typeName, new HollowHistoricalSetDataAccess(this, typeState));
                break;
            case MAP: 
                typeDataAccessMap.put(typeName, new HollowHistoricalMapDataAccess(this, typeState));
                break;
            }
        }

        this.typeDataAccessMap = typeDataAccessMap;

        for(Map.Entry<String, HollowHistoricalTypeDataAccess> entry : typeDataAccessMap.entrySet()) {
            HollowHistoricalTypeDataAccess typeDataAccess = entry.getValue();
            switch(typeDataAccess.getSchema().getSchemaType()) {
            case MAP:
                ((HollowHistoricalMapDataAccess)typeDataAccess).buildKeyMatcher();
                break;
            case SET:
                ((HollowHistoricalSetDataAccess)typeDataAccess).buildKeyMatcher();
                break;
            default:
            }
        }
    }

    public HollowHistory getTotalHistory() {
        return totalHistory;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long newVersion) {
         version = newVersion;
    }

    public void setNextState(HollowDataAccess nextState) {
        this.nextState = nextState;
    }

    public HollowDataAccess getNextState() {
        return nextState;
    }

    public OrdinalRemapper getOrdinalMapping() {
        return removedCopyOrdinalMapping;
    }

    public Map<String, HollowHistoricalSchemaChange> getSchemaChanges() {
        return schemaChanges;
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName) {
        HollowDataAccess state = this;

        HollowTypeDataAccess typeDataAccess = typeDataAccessMap.get(typeName);
        if(typeDataAccess != null)
            return typeDataAccess;

        while(state instanceof HollowHistoricalStateDataAccess) {
            HollowHistoricalStateDataAccess historicalState = (HollowHistoricalStateDataAccess)state;
            typeDataAccess = historicalState.typeDataAccessMap.get(typeName);
            if(typeDataAccess != null)
                return typeDataAccess;
            state = historicalState.getNextState();
        }

        return state.getTypeDataAccess(typeName);
    }

    @Override
    public Collection<String> getAllTypes() {
        return typeDataAccessMap.keySet();
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName, int ordinal) {
        HollowDataAccess state = this;

        while(state instanceof HollowHistoricalStateDataAccess) {
            HollowHistoricalStateDataAccess historicalState = (HollowHistoricalStateDataAccess)state;
            if(historicalState.getOrdinalMapping().ordinalIsMapped(typeName, ordinal))
                return state.getTypeDataAccess(typeName);
            state = historicalState.getNextState();
        }

        return state.getTypeDataAccess(typeName);
    }

    @Override
    public HollowObjectHashCodeFinder getHashCodeFinder() {
        return hashCodeFinder;
    }

    @Override
    public MissingDataHandler getMissingDataHandler() {
        return missingDataHandler;
    }

    @Override
    public void resetSampling() {
        for(Map.Entry<String, HollowHistoricalTypeDataAccess> entry : typeDataAccessMap.entrySet())
            entry.getValue().getSampler().reset();
    }

    @Override
    public boolean hasSampleResults() {
        for(Map.Entry<String, HollowHistoricalTypeDataAccess> entry : typeDataAccessMap.entrySet())
            if(entry.getValue().getSampler().hasSampleResults())
                return true;
        return false;
    }

    public void setStackTraceRecorder(StackTraceRecorder recorder) {
        this.stackTraceRecorder = recorder;
    }

    StackTraceRecorder getStackTraceRecorder() {
        return stackTraceRecorder;
    }

    public List<HollowSchema> getSchemas() {
        List<HollowSchema> schemas = new ArrayList<HollowSchema>(typeDataAccessMap.size());
        for(Map.Entry<String, HollowHistoricalTypeDataAccess> entry : typeDataAccessMap.entrySet())
            schemas.add(entry.getValue().getSchema());
        return schemas;
    }
    
    @Override
    public HollowSchema getSchema(String name) {
        return getTypeDataAccess(name).getSchema();
    }

    @Override
    public HollowSchema getNonNullSchema(String name) {
        HollowSchema schema = getSchema(name);
        if (schema == null) {
            throw new SchemaNotFoundException(name, getAllTypes());
        }
        return schema;
    }
}
