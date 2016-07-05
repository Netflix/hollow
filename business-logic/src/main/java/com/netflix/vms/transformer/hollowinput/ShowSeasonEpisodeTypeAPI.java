package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ShowSeasonEpisodeTypeAPI extends HollowObjectTypeAPI {

    private final ShowSeasonEpisodeDelegateLookupImpl delegateLookupImpl;

    ShowSeasonEpisodeTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "displaySetId",
            "countryCodes",
            "seasons"
        });
        this.delegateLookupImpl = new ShowSeasonEpisodeDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("ShowSeasonEpisode", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("ShowSeasonEpisode", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getDisplaySetId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("ShowSeasonEpisode", ordinal, "displaySetId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getDisplaySetIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("ShowSeasonEpisode", ordinal, "displaySetId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("ShowSeasonEpisode", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ISOCountryListTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getISOCountryListTypeAPI();
    }

    public int getSeasonsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("ShowSeasonEpisode", ordinal, "seasons");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public SeasonListTypeAPI getSeasonsTypeAPI() {
        return getAPI().getSeasonListTypeAPI();
    }

    public ShowSeasonEpisodeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}