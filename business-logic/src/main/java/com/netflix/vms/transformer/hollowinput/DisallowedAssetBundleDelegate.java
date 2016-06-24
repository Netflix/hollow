package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DisallowedAssetBundleDelegate extends HollowObjectDelegate {

    public boolean getForceSubtitle(int ordinal);

    public Boolean getForceSubtitleBoxed(int ordinal);

    public int getAudioLanguageCodeOrdinal(int ordinal);

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal);

    public DisallowedAssetBundleTypeAPI getTypeAPI();

}