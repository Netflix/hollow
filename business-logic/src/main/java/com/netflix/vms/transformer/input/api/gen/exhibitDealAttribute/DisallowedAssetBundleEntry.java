package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class DisallowedAssetBundleEntry extends HollowObject {

    public DisallowedAssetBundleEntry(DisallowedAssetBundleEntryDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getAudioLanguageCode() {
        return delegate().getAudioLanguageCode(ordinal);
    }

    public boolean isAudioLanguageCodeEqual(String testValue) {
        return delegate().isAudioLanguageCodeEqual(ordinal, testValue);
    }

    public HString getAudioLanguageCodeHollowReference() {
        int refOrdinal = delegate().getAudioLanguageCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public Boolean getForceSubtitleBoxed() {
        return delegate().getForceSubtitleBoxed(ordinal);
    }

    public boolean getForceSubtitle() {
        return delegate().getForceSubtitle(ordinal);
    }

    public HBoolean getForceSubtitleHollowReference() {
        int refOrdinal = delegate().getForceSubtitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHBoolean(refOrdinal);
    }

    public SetOfString getDisallowedSubtitleLangCodes() {
        int refOrdinal = delegate().getDisallowedSubtitleLangCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public ExhibitDealAttributeV1API api() {
        return typeApi().getAPI();
    }

    public DisallowedAssetBundleEntryTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DisallowedAssetBundleEntryDelegate delegate() {
        return (DisallowedAssetBundleEntryDelegate)delegate;
    }

}