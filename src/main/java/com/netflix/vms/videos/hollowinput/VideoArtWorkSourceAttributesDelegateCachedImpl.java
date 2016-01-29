package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoArtWorkSourceAttributesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtWorkSourceAttributesDelegate {

    private final int REEXPLOREOrdinal;
    private final int source_file_idOrdinal;
    private final int IDENTIFIERSOrdinal;
    private final int original_source_file_idOrdinal;
    private final int GROUP_IDOrdinal;
    private final int file_seqOrdinal;
    private final int themesOrdinal;
    private final int AWARD_CAMPAIGNSOrdinal;
    private final int TONEOrdinal;
    private final int LABELOrdinal;
    private final int AWARD_CAMPAIGNOrdinal;
    private final int SOURCE_MOVIE_IDOrdinal;
    private final int APPROVAL_STATEOrdinal;
    private final int FOCAL_POINTOrdinal;
    private final int PERSON_IDSOrdinal;
   private VideoArtWorkSourceAttributesTypeAPI typeAPI;

    public VideoArtWorkSourceAttributesDelegateCachedImpl(VideoArtWorkSourceAttributesTypeAPI typeAPI, int ordinal) {
        this.REEXPLOREOrdinal = typeAPI.getREEXPLOREOrdinal(ordinal);
        this.source_file_idOrdinal = typeAPI.getSource_file_idOrdinal(ordinal);
        this.IDENTIFIERSOrdinal = typeAPI.getIDENTIFIERSOrdinal(ordinal);
        this.original_source_file_idOrdinal = typeAPI.getOriginal_source_file_idOrdinal(ordinal);
        this.GROUP_IDOrdinal = typeAPI.getGROUP_IDOrdinal(ordinal);
        this.file_seqOrdinal = typeAPI.getFile_seqOrdinal(ordinal);
        this.themesOrdinal = typeAPI.getThemesOrdinal(ordinal);
        this.AWARD_CAMPAIGNSOrdinal = typeAPI.getAWARD_CAMPAIGNSOrdinal(ordinal);
        this.TONEOrdinal = typeAPI.getTONEOrdinal(ordinal);
        this.LABELOrdinal = typeAPI.getLABELOrdinal(ordinal);
        this.AWARD_CAMPAIGNOrdinal = typeAPI.getAWARD_CAMPAIGNOrdinal(ordinal);
        this.SOURCE_MOVIE_IDOrdinal = typeAPI.getSOURCE_MOVIE_IDOrdinal(ordinal);
        this.APPROVAL_STATEOrdinal = typeAPI.getAPPROVAL_STATEOrdinal(ordinal);
        this.FOCAL_POINTOrdinal = typeAPI.getFOCAL_POINTOrdinal(ordinal);
        this.PERSON_IDSOrdinal = typeAPI.getPERSON_IDSOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getREEXPLOREOrdinal(int ordinal) {
        return REEXPLOREOrdinal;
    }

    public int getSource_file_idOrdinal(int ordinal) {
        return source_file_idOrdinal;
    }

    public int getIDENTIFIERSOrdinal(int ordinal) {
        return IDENTIFIERSOrdinal;
    }

    public int getOriginal_source_file_idOrdinal(int ordinal) {
        return original_source_file_idOrdinal;
    }

    public int getGROUP_IDOrdinal(int ordinal) {
        return GROUP_IDOrdinal;
    }

    public int getFile_seqOrdinal(int ordinal) {
        return file_seqOrdinal;
    }

    public int getThemesOrdinal(int ordinal) {
        return themesOrdinal;
    }

    public int getAWARD_CAMPAIGNSOrdinal(int ordinal) {
        return AWARD_CAMPAIGNSOrdinal;
    }

    public int getTONEOrdinal(int ordinal) {
        return TONEOrdinal;
    }

    public int getLABELOrdinal(int ordinal) {
        return LABELOrdinal;
    }

    public int getAWARD_CAMPAIGNOrdinal(int ordinal) {
        return AWARD_CAMPAIGNOrdinal;
    }

    public int getSOURCE_MOVIE_IDOrdinal(int ordinal) {
        return SOURCE_MOVIE_IDOrdinal;
    }

    public int getAPPROVAL_STATEOrdinal(int ordinal) {
        return APPROVAL_STATEOrdinal;
    }

    public int getFOCAL_POINTOrdinal(int ordinal) {
        return FOCAL_POINTOrdinal;
    }

    public int getPERSON_IDSOrdinal(int ordinal) {
        return PERSON_IDSOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoArtWorkSourceAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtWorkSourceAttributesTypeAPI) typeAPI;
    }

}