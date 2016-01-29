package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsContractsDisallowedAssetBundlesHollow extends HollowObject {

    public VideoRightsRightsContractsDisallowedAssetBundlesHollow(VideoRightsRightsContractsDisallowedAssetBundlesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getForceSubtitle() {
        return delegate().getForceSubtitle(ordinal);
    }

    public Boolean _getForceSubtitleBoxed() {
        return delegate().getForceSubtitleBoxed(ordinal);
    }

    public StringHollow _getAudioLanguageCode() {
        int refOrdinal = delegate().getAudioLanguageCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow _getDisallowedSubtitleLangCodes() {
        int refOrdinal = delegate().getDisallowedSubtitleLangCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsContractsDisallowedAssetBundlesArrayOfDisallowedSubtitleLangCodesHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsRightsContractsDisallowedAssetBundlesDelegate delegate() {
        return (VideoRightsRightsContractsDisallowedAssetBundlesDelegate)delegate;
    }

}