package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TopNTypeAPI extends HollowObjectTypeAPI {

    private final TopNDelegateLookupImpl delegateLookupImpl;

    public TopNTypeAPI(TopNAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "attributes"
        });
        this.delegateLookupImpl = new TopNDelegateLookupImpl(this);
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("TopN", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("TopN", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getAttributesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopN", ordinal, "attributes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public SetOfTopNAttributeTypeAPI getAttributesTypeAPI() {
        return getAPI().getSetOfTopNAttributeTypeAPI();
    }

    public TopNDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public TopNAPI getAPI() {
        return (TopNAPI) api;
    }

}