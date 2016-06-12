package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class EpisodesTypeAPI extends HollowObjectTypeAPI {

    private final EpisodesDelegateLookupImpl delegateLookupImpl;

    EpisodesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "episodeName",
            "movieId",
            "episodeId"
        });
        this.delegateLookupImpl = new EpisodesDelegateLookupImpl(this);
    }

    public int getEpisodeNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Episodes", ordinal, "episodeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public TranslatedTextTypeAPI getEpisodeNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("Episodes", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Episodes", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getEpisodeId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Episodes", ordinal, "episodeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getEpisodeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Episodes", ordinal, "episodeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public EpisodesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}