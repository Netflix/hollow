package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoArtWorkSourceAttributesDelegate extends HollowObjectDelegate {

    public int getREEXPLOREOrdinal(int ordinal);

    public int getSource_file_idOrdinal(int ordinal);

    public int getOriginal_source_file_idOrdinal(int ordinal);

    public int getGROUP_IDOrdinal(int ordinal);

    public int getFile_seqOrdinal(int ordinal);

    public int getTONEOrdinal(int ordinal);

    public int getLABELOrdinal(int ordinal);

    public int getAWARD_CAMPAIGNOrdinal(int ordinal);

    public int getSOURCE_MOVIE_IDOrdinal(int ordinal);

    public int getAPPROVAL_STATEOrdinal(int ordinal);

    public int getFOCAL_POINTOrdinal(int ordinal);

    public int getIDENTIFIERSOrdinal(int ordinal);

    public int getThemesOrdinal(int ordinal);

    public int getAWARD_CAMPAIGNSOrdinal(int ordinal);

    public int getPERSON_IDSOrdinal(int ordinal);

    public VideoArtWorkSourceAttributesTypeAPI getTypeAPI();

}