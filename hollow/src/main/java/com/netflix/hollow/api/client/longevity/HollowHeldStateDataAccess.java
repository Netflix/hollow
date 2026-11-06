/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.client.longevity;

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link HollowDataAccess} of a held version, through {@link DirectLookup}: each record is read from the copy of the
 * first delta since the version that removed it, or from the state engine if no delta did.
 */
final class HollowHeldStateDataAccess implements HollowDataAccess {
    private final HollowReadStateEngine stateEngine;
    private final Map<String, HollowHeldTypeDataAccess> typeDataAccessMap = new HashMap<>();

    HollowHeldStateDataAccess(DirectLookup lookup, long position) {
        this.stateEngine = lookup.getStateEngine();
        for (HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            String type = typeState.getSchema().getName();
            DirectLookup.TypeLookup typeLookup = lookup.type(type);
            switch (typeState.getSchema().getSchemaType()) {
                case OBJECT:
                    typeDataAccessMap.put(type, new HollowHeldObjectDataAccess(this, typeLookup, position));
                    break;
                case LIST:
                    typeDataAccessMap.put(type, new HollowHeldListDataAccess(this, typeLookup, position));
                    break;
                case SET:
                    typeDataAccessMap.put(type, new HollowHeldSetDataAccess(this, typeLookup, position));
                    break;
                case MAP:
                    typeDataAccessMap.put(type, new HollowHeldMapDataAccess(this, typeLookup, position));
                    break;
            }
        }
        for (HollowHeldTypeDataAccess typeDataAccess : typeDataAccessMap.values()) {
            if (typeDataAccess instanceof HollowHeldSetDataAccess)
                ((HollowHeldSetDataAccess) typeDataAccess).buildKeyMatcher();
            else if (typeDataAccess instanceof HollowHeldMapDataAccess)
                ((HollowHeldMapDataAccess) typeDataAccess).buildKeyMatcher();
        }
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName) {
        return typeDataAccessMap.get(typeName);
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName, int ordinal) {
        return typeDataAccessMap.get(typeName);
    }

    @Override
    public Collection<String> getAllTypes() {
        return typeDataAccessMap.keySet();
    }

    @Override
    public List<HollowSchema> getSchemas() {
        return stateEngine.getSchemas();
    }

    @Override
    public HollowSchema getSchema(String name) {
        return stateEngine.getSchema(name);
    }

    @Override
    public HollowSchema getNonNullSchema(String name) {
        return stateEngine.getNonNullSchema(name);
    }

    @Override
    public HollowObjectHashCodeFinder getHashCodeFinder() {
        return stateEngine.getHashCodeFinder();
    }

    @Override
    public MissingDataHandler getMissingDataHandler() {
        return stateEngine.getMissingDataHandler();
    }

    @Override
    public void resetSampling() {
        for (HollowHeldTypeDataAccess typeDataAccess : typeDataAccessMap.values())
            typeDataAccess.getSampler().reset();
    }

    @Override
    public boolean hasSampleResults() {
        for (HollowHeldTypeDataAccess typeDataAccess : typeDataAccessMap.values()) {
            if (typeDataAccess.getSampler().hasSampleResults())
                return true;
        }
        return false;
    }
}
