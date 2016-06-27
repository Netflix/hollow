package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoGeneralTypeAPI extends HollowObjectTypeAPI {

    private final VideoGeneralDelegateLookupImpl delegateLookupImpl;

    VideoGeneralTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "tv",
            "aliases",
            "videoType",
            "runtime",
            "supplementalSubType",
            "firstReleaseYear",
            "testTitle",
            "originalLanguageBcpCode",
            "metadataReleaseDays",
            "originCountryCode",
            "originalTitle",
            "testTitleTypes",
            "originalTitleBcpCode",
            "internalTitle",
            "episodeTypes",
            "regulatoryAdvisories"
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



    public boolean getTv(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoGeneral", ordinal, "tv") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getTvBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoGeneral", ordinal, "tv");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getAliasesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "aliases");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoGeneralAliasListTypeAPI getAliasesTypeAPI() {
        return getAPI().getVideoGeneralAliasListTypeAPI();
    }

    public int getVideoTypeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "videoType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getVideoTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getRuntime(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("VideoGeneral", ordinal, "runtime");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getRuntimeBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("VideoGeneral", ordinal, "runtime");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSupplementalSubTypeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "supplementalSubType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getSupplementalSubTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getFirstReleaseYear(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleLong("VideoGeneral", ordinal, "firstReleaseYear");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
    }

    public Long getFirstReleaseYearBoxed(int ordinal) {
        long l;
        if(fieldIndex[6] == -1) {
            l = missingDataHandler().handleLong("VideoGeneral", ordinal, "firstReleaseYear");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[6]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getTestTitle(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("VideoGeneral", ordinal, "testTitle") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]) == Boolean.TRUE;
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("VideoGeneral", ordinal, "testTitle");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]);
    }



    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originalLanguageBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StringTypeAPI getOriginalLanguageBcpCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getMetadataReleaseDays(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleInt("VideoGeneral", ordinal, "metadataReleaseDays");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[9]);
    }

    public Integer getMetadataReleaseDaysBoxed(int ordinal) {
        int i;
        if(fieldIndex[9] == -1) {
            i = missingDataHandler().handleInt("VideoGeneral", ordinal, "metadataReleaseDays");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[9]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[9]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getOriginCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originCountryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getOriginCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public StringTypeAPI getOriginalTitleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTestTitleTypesOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "testTitleTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public VideoGeneralTitleTypeListTypeAPI getTestTitleTypesTypeAPI() {
        return getAPI().getVideoGeneralTitleTypeListTypeAPI();
    }

    public int getOriginalTitleBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "originalTitleBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public StringTypeAPI getOriginalTitleBcpCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getInternalTitleOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "internalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public StringTypeAPI getInternalTitleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getEpisodeTypesOrdinal(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "episodeTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[15]);
    }

    public VideoGeneralEpisodeTypeListTypeAPI getEpisodeTypesTypeAPI() {
        return getAPI().getVideoGeneralEpisodeTypeListTypeAPI();
    }

    public int getRegulatoryAdvisoriesOrdinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoGeneral", ordinal, "regulatoryAdvisories");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public SetOfStringTypeAPI getRegulatoryAdvisoriesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public VideoGeneralDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}