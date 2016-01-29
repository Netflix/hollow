package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class Stories_SynopsesTypeAPI extends HollowObjectTypeAPI {

    private final Stories_SynopsesDelegateLookupImpl delegateLookupImpl;

    Stories_SynopsesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "narrativeText",
            "movieId",
            "hooks"
        });
        this.delegateLookupImpl = new Stories_SynopsesDelegateLookupImpl(this);
    }

    public int getNarrativeTextOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Stories_Synopses", ordinal, "narrativeText");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public Stories_SynopsesNarrativeTextTypeAPI getNarrativeTextTypeAPI() {
        return getAPI().getStories_SynopsesNarrativeTextTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("Stories_Synopses", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Stories_Synopses", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getHooksOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Stories_Synopses", ordinal, "hooks");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public Stories_SynopsesArrayOfHooksTypeAPI getHooksTypeAPI() {
        return getAPI().getStories_SynopsesArrayOfHooksTypeAPI();
    }

    public Stories_SynopsesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}