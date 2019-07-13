package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ShowSeasonEpisodeTypeAPI extends HollowObjectTypeAPI {

    private final ShowSeasonEpisodeDelegateLookupImpl delegateLookupImpl;

    public ShowSeasonEpisodeTypeAPI(ShowSeasonEpisodeAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "displaySetId",
            "countryCodes",
            "seasons",
            "hideSeasonNumbers",
            "episodicNewBadge",
            "merchOrder"
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

    public boolean getHideSeasonNumbers(int ordinal) {
        if(fieldIndex[4] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("ShowSeasonEpisode", ordinal, "hideSeasonNumbers"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]));
    }

    public Boolean getHideSeasonNumbersBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("ShowSeasonEpisode", ordinal, "hideSeasonNumbers");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public boolean getEpisodicNewBadge(int ordinal) {
        if(fieldIndex[5] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("ShowSeasonEpisode", ordinal, "episodicNewBadge"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]));
    }

    public Boolean getEpisodicNewBadgeBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("ShowSeasonEpisode", ordinal, "episodicNewBadge");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public int getMerchOrderOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("ShowSeasonEpisode", ordinal, "merchOrder");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getMerchOrderTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public ShowSeasonEpisodeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public ShowSeasonEpisodeAPI getAPI() {
        return (ShowSeasonEpisodeAPI) api;
    }

}