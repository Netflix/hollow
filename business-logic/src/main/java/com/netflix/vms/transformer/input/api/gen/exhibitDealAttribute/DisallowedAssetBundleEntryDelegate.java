package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DisallowedAssetBundleEntryDelegate extends HollowObjectDelegate {

    public String getAudioLanguageCode(int ordinal);

    public boolean isAudioLanguageCodeEqual(int ordinal, String testValue);

    public int getAudioLanguageCodeOrdinal(int ordinal);

    public boolean getForceSubtitle(int ordinal);

    public Boolean getForceSubtitleBoxed(int ordinal);

    public int getForceSubtitleOrdinal(int ordinal);

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal);

    public DisallowedAssetBundleEntryTypeAPI getTypeAPI();

}