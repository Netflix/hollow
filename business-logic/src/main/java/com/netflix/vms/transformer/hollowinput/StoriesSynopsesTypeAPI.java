package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StoriesSynopsesTypeAPI extends HollowObjectTypeAPI {

    private final StoriesSynopsesDelegateLookupImpl delegateLookupImpl;

    public StoriesSynopsesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "narrativeText",
            "hooks"
        });
        this.delegateLookupImpl = new StoriesSynopsesDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("StoriesSynopses", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("StoriesSynopses", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getNarrativeTextOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StoriesSynopses", ordinal, "narrativeText");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getNarrativeTextTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getHooksOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StoriesSynopses", ordinal, "hooks");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StoriesSynopsesHookListTypeAPI getHooksTypeAPI() {
        return getAPI().getStoriesSynopsesHookListTypeAPI();
    }

    public StoriesSynopsesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}