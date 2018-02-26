package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface FlagsDelegate extends HollowObjectDelegate {

    public boolean getSearchOnly(int ordinal);

    public Boolean getSearchOnlyBoxed(int ordinal);

    public boolean getLocalText(int ordinal);

    public Boolean getLocalTextBoxed(int ordinal);

    public boolean getLanguageOverride(int ordinal);

    public Boolean getLanguageOverrideBoxed(int ordinal);

    public boolean getLocalAudio(int ordinal);

    public Boolean getLocalAudioBoxed(int ordinal);

    public boolean getGoLive(int ordinal);

    public Boolean getGoLiveBoxed(int ordinal);

    public boolean getAutoPlay(int ordinal);

    public Boolean getAutoPlayBoxed(int ordinal);

    public int getFirstDisplayDateOrdinal(int ordinal);

    public int getFirstDisplayDatesOrdinal(int ordinal);

    public boolean getLiveOnSite(int ordinal);

    public Boolean getLiveOnSiteBoxed(int ordinal);

    public int getOffsiteReasonsOrdinal(int ordinal);

    public boolean getContentApproved(int ordinal);

    public Boolean getContentApprovedBoxed(int ordinal);

    public boolean getAllowIncomplete(int ordinal);

    public Boolean getAllowIncompleteBoxed(int ordinal);

    public boolean getGoLivePartialSubDubIgnored(int ordinal);

    public Boolean getGoLivePartialSubDubIgnoredBoxed(int ordinal);

    public int getAlternateLanguageOrdinal(int ordinal);

    public boolean getHasRequiredLanguages(int ordinal);

    public Boolean getHasRequiredLanguagesBoxed(int ordinal);

    public boolean getHasRequiredStreams(int ordinal);

    public Boolean getHasRequiredStreamsBoxed(int ordinal);

    public boolean getReleaseAsAvailable(int ordinal);

    public Boolean getReleaseAsAvailableBoxed(int ordinal);

    public int getRemoveAssetOrdinal(int ordinal);

    public boolean getRemoveFromWebsiteOverride(int ordinal);

    public Boolean getRemoveFromWebsiteOverrideBoxed(int ordinal);

    public int getRequiredLangsOrdinal(int ordinal);

    public boolean getSearchOnlyOverride(int ordinal);

    public Boolean getSearchOnlyOverrideBoxed(int ordinal);

    public int getTextRequiredLanguagesOrdinal(int ordinal);

    public int getAudioRequiredLanguagesOrdinal(int ordinal);

    public int getLocalizationRequiredLanguagesOrdinal(int ordinal);

    public FlagsTypeAPI getTypeAPI();

}