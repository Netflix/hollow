package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoArtWorkSourceAttributesDelegate {

    private final VideoArtWorkSourceAttributesTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesDelegateLookupImpl(VideoArtWorkSourceAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getREEXPLOREOrdinal(int ordinal) {
        return typeAPI.getREEXPLOREOrdinal(ordinal);
    }

    public int getSource_file_idOrdinal(int ordinal) {
        return typeAPI.getSource_file_idOrdinal(ordinal);
    }

    public int getIDENTIFIERSOrdinal(int ordinal) {
        return typeAPI.getIDENTIFIERSOrdinal(ordinal);
    }

    public int getOriginal_source_file_idOrdinal(int ordinal) {
        return typeAPI.getOriginal_source_file_idOrdinal(ordinal);
    }

    public int getGROUP_IDOrdinal(int ordinal) {
        return typeAPI.getGROUP_IDOrdinal(ordinal);
    }

    public int getFile_seqOrdinal(int ordinal) {
        return typeAPI.getFile_seqOrdinal(ordinal);
    }

    public int getThemesOrdinal(int ordinal) {
        return typeAPI.getThemesOrdinal(ordinal);
    }

    public int getAWARD_CAMPAIGNSOrdinal(int ordinal) {
        return typeAPI.getAWARD_CAMPAIGNSOrdinal(ordinal);
    }

    public int getTONEOrdinal(int ordinal) {
        return typeAPI.getTONEOrdinal(ordinal);
    }

    public int getLABELOrdinal(int ordinal) {
        return typeAPI.getLABELOrdinal(ordinal);
    }

    public int getAWARD_CAMPAIGNOrdinal(int ordinal) {
        return typeAPI.getAWARD_CAMPAIGNOrdinal(ordinal);
    }

    public int getSOURCE_MOVIE_IDOrdinal(int ordinal) {
        return typeAPI.getSOURCE_MOVIE_IDOrdinal(ordinal);
    }

    public int getAPPROVAL_STATEOrdinal(int ordinal) {
        return typeAPI.getAPPROVAL_STATEOrdinal(ordinal);
    }

    public int getFOCAL_POINTOrdinal(int ordinal) {
        return typeAPI.getFOCAL_POINTOrdinal(ordinal);
    }

    public int getPERSON_IDSOrdinal(int ordinal) {
        return typeAPI.getPERSON_IDSOrdinal(ordinal);
    }

    public VideoArtWorkSourceAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}