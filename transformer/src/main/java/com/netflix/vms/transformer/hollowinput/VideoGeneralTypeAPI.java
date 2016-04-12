package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoGeneralTypeAPI extends HollowObjectTypeAPI {

    private final VideoGeneralDelegateLookupImpl delegateLookupImpl;

    VideoGeneralTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "aliases",
            "videoType",
            "runtime",
            "supplementalSubType",
            "testTitle",
            "originalLanguageBcpCode",
            "titleTypes",
            "originCountryCode",
            "originalTitle",
            "countryOfOriginNameLocale",
            "internalTitle",
            "episodeTypes"
        });
        this.delegateLookupImpl = new VideoGeneralDelegateLookupImpl(this);
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoGeneral", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoGeneral", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getAliasesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "aliases");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoGeneralAliasListTypeAPI getAliasesTypeAPI() {
        return getAPI().getVideoGeneralAliasListTypeAPI();
    }

    public int getVideoTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "videoType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getVideoTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getRuntime(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoGeneral", ordinal, "runtime");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getRuntimeBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoGeneral", ordinal, "runtime");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSupplementalSubTypeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "supplementalSubType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getSupplementalSubTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getTestTitle(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoGeneral", ordinal, "testTitle") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]) == Boolean.TRUE;
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("VideoGeneral", ordinal, "testTitle");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originalLanguageBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getOriginalLanguageBcpCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTitleTypesOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "titleTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public VideoGeneralTitleTypeListTypeAPI getTitleTypesTypeAPI() {
        return getAPI().getVideoGeneralTitleTypeListTypeAPI();
    }

    public int getOriginCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originCountryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StringTypeAPI getOriginCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StringTypeAPI getOriginalTitleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCountryOfOriginNameLocaleOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "countryOfOriginNameLocale");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getCountryOfOriginNameLocaleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getInternalTitleOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "internalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public StringTypeAPI getInternalTitleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getEpisodeTypesOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "episodeTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public VideoGeneralEpisodeTypeListTypeAPI getEpisodeTypesTypeAPI() {
        return getAPI().getVideoGeneralEpisodeTypeListTypeAPI();
    }

    public VideoGeneralDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}