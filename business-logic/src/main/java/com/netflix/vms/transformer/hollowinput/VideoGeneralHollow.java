package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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

    public boolean _getTv() {
        return delegate().getTv(ordinal);
    }

    public Boolean _getTvBoxed() {
        return delegate().getTvBoxed(ordinal);
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

    public long _getFirstReleaseYear() {
        return delegate().getFirstReleaseYear(ordinal);
    }

    public Long _getFirstReleaseYearBoxed() {
        return delegate().getFirstReleaseYearBoxed(ordinal);
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

    public int _getMetadataReleaseDays() {
        return delegate().getMetadataReleaseDays(ordinal);
    }

    public Integer _getMetadataReleaseDaysBoxed() {
        return delegate().getMetadataReleaseDaysBoxed(ordinal);
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

    public VideoGeneralTitleTypeListHollow _getTestTitleTypes() {
        int refOrdinal = delegate().getTestTitleTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralTitleTypeListHollow(refOrdinal);
    }

    public StringHollow _getOriginalTitleBcpCode() {
        int refOrdinal = delegate().getOriginalTitleBcpCodeOrdinal(ordinal);
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

    public SetOfStringHollow _getRegulatoryAdvisories() {
        int refOrdinal = delegate().getRegulatoryAdvisoriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralDelegate delegate() {
        return (VideoGeneralDelegate)delegate;
    }

}