package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsRightsContractsDisallowedAssetBundlesDelegate extends HollowObjectDelegate {

    public boolean getForceSubtitle(int ordinal);

    public Boolean getForceSubtitleBoxed(int ordinal);

    public int getAudioLanguageCodeOrdinal(int ordinal);

    public int getDisallowedSubtitleLangCodesOrdinal(int ordinal);

    public VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI getTypeAPI();

}