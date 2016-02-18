package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesHollow extends HollowObject {

    public VideoArtWorkSourceAttributesHollow(VideoArtWorkSourceAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getREEXPLORE() {
        int refOrdinal = delegate().getREEXPLOREOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSource_file_id() {
        int refOrdinal = delegate().getSource_file_idOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getOriginal_source_file_id() {
        int refOrdinal = delegate().getOriginal_source_file_idOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getGROUP_ID() {
        int refOrdinal = delegate().getGROUP_IDOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getFile_seq() {
        int refOrdinal = delegate().getFile_seqOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getTONE() {
        int refOrdinal = delegate().getTONEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getLABEL() {
        int refOrdinal = delegate().getLABELOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getAWARD_CAMPAIGN() {
        int refOrdinal = delegate().getAWARD_CAMPAIGNOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSOURCE_MOVIE_ID() {
        int refOrdinal = delegate().getSOURCE_MOVIE_IDOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getAPPROVAL_STATE() {
        int refOrdinal = delegate().getAPPROVAL_STATEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getFOCAL_POINT() {
        int refOrdinal = delegate().getFOCAL_POINTOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VideoArtWorkMultiValueAttributeHollow _getIDENTIFIERS() {
        int refOrdinal = delegate().getIDENTIFIERSOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkMultiValueAttributeHollow(refOrdinal);
    }

    public VideoArtWorkMultiValueAttributeHollow _getThemes() {
        int refOrdinal = delegate().getThemesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkMultiValueAttributeHollow(refOrdinal);
    }

    public VideoArtWorkMultiValueAttributeHollow _getAWARD_CAMPAIGNS() {
        int refOrdinal = delegate().getAWARD_CAMPAIGNSOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkMultiValueAttributeHollow(refOrdinal);
    }

    public VideoArtWorkMultiValueAttributeHollow _getPERSON_IDS() {
        int refOrdinal = delegate().getPERSON_IDSOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoArtWorkMultiValueAttributeHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkSourceAttributesDelegate delegate() {
        return (VideoArtWorkSourceAttributesDelegate)delegate;
    }

}