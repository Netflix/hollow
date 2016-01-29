package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoArtWorkSourceAttributesTypeAPI extends HollowObjectTypeAPI {

    private final VideoArtWorkSourceAttributesDelegateLookupImpl delegateLookupImpl;

    VideoArtWorkSourceAttributesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "REEXPLORE",
            "source_file_id",
            "IDENTIFIERS",
            "original_source_file_id",
            "GROUP_ID",
            "file_seq",
            "themes",
            "AWARD_CAMPAIGNS",
            "TONE",
            "LABEL",
            "AWARD_CAMPAIGN",
            "SOURCE_MOVIE_ID",
            "APPROVAL_STATE",
            "FOCAL_POINT",
            "PERSON_IDS"
        });
        this.delegateLookupImpl = new VideoArtWorkSourceAttributesDelegateLookupImpl(this);
    }

    public int getREEXPLOREOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "REEXPLORE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getREEXPLORETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSource_file_idOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "source_file_id");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getSource_file_idTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getIDENTIFIERSOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "IDENTIFIERS");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI getIDENTIFIERSTypeAPI() {
        return getAPI().getVideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI();
    }

    public int getOriginal_source_file_idOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "original_source_file_id");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getOriginal_source_file_idTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getGROUP_IDOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "GROUP_ID");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getGROUP_IDTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getFile_seqOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "file_seq");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getFile_seqTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getThemesOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "themes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public VideoArtWorkSourceAttributesArrayOfThemesTypeAPI getThemesTypeAPI() {
        return getAPI().getVideoArtWorkSourceAttributesArrayOfThemesTypeAPI();
    }

    public int getAWARD_CAMPAIGNSOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "AWARD_CAMPAIGNS");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI getAWARD_CAMPAIGNSTypeAPI() {
        return getAPI().getVideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI();
    }

    public int getTONEOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "TONE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StringTypeAPI getTONETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLABELOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "LABEL");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StringTypeAPI getLABELTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAWARD_CAMPAIGNOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "AWARD_CAMPAIGN");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getAWARD_CAMPAIGNTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSOURCE_MOVIE_IDOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "SOURCE_MOVIE_ID");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public StringTypeAPI getSOURCE_MOVIE_IDTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAPPROVAL_STATEOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "APPROVAL_STATE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public StringTypeAPI getAPPROVAL_STATETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getFOCAL_POINTOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "FOCAL_POINT");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public StringTypeAPI getFOCAL_POINTTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPERSON_IDSOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkSourceAttributes", ordinal, "PERSON_IDS");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI getPERSON_IDSTypeAPI() {
        return getAPI().getVideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI();
    }

    public VideoArtWorkSourceAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}