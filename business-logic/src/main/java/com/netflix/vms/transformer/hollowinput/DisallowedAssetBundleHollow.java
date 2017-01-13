package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisallowedAssetBundleHollow extends HollowObject {

    public DisallowedAssetBundleHollow(DisallowedAssetBundleDelegate delegate, int ordinal) {
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

    public DisallowedSubtitleLangCodesListHollow _getDisallowedSubtitleLangCodes() {
        int refOrdinal = delegate().getDisallowedSubtitleLangCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDisallowedSubtitleLangCodesListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DisallowedAssetBundleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DisallowedAssetBundleDelegate delegate() {
        return (DisallowedAssetBundleDelegate)delegate;
    }

}