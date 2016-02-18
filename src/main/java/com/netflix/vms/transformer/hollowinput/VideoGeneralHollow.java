package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoGeneralHollow extends HollowObject {

    public VideoGeneralHollow(VideoGeneralDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VideoGeneralAliasListHollow _getAliases() {
        int refOrdinal = delegate().getAliasesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralAliasListHollow(refOrdinal);
    }

    public StringHollow _getVideoType() {
        int refOrdinal = delegate().getVideoTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getRuntime() {
        return delegate().getRuntime(ordinal);
    }

    public Long _getRuntimeBoxed() {
        return delegate().getRuntimeBoxed(ordinal);
    }

    public StringHollow _getSupplementalSubType() {
        int refOrdinal = delegate().getSupplementalSubTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getTestTitle() {
        return delegate().getTestTitle(ordinal);
    }

    public Boolean _getTestTitleBoxed() {
        return delegate().getTestTitleBoxed(ordinal);
    }

    public StringHollow _getOriginalLanguageBcpCode() {
        int refOrdinal = delegate().getOriginalLanguageBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VideoGeneralTitleTypeListHollow _getTitleTypes() {
        int refOrdinal = delegate().getTitleTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralTitleTypeListHollow(refOrdinal);
    }

    public StringHollow _getOriginCountryCode() {
        int refOrdinal = delegate().getOriginCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getOriginalTitle() {
        int refOrdinal = delegate().getOriginalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCountryOfOriginNameLocale() {
        int refOrdinal = delegate().getCountryOfOriginNameLocaleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getInternalTitle() {
        int refOrdinal = delegate().getInternalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VideoGeneralEpisodeTypeListHollow _getEpisodeTypes() {
        int refOrdinal = delegate().getEpisodeTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralEpisodeTypeListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralDelegate delegate() {
        return (VideoGeneralDelegate)delegate;
    }

}