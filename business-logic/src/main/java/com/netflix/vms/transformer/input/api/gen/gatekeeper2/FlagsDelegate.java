package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

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

    public long getGoLiveFlipDate(int ordinal);

    public Long getGoLiveFlipDateBoxed(int ordinal);

    public int getGoLiveFlipDateOrdinal(int ordinal);

    public boolean getAutoPlay(int ordinal);

    public Boolean getAutoPlayBoxed(int ordinal);

    public long getFirstDisplayDate(int ordinal);

    public Long getFirstDisplayDateBoxed(int ordinal);

    public int getFirstDisplayDateOrdinal(int ordinal);

    public int getFirstDisplayDatesOrdinal(int ordinal);

    public int getGrandfatheredLanguagesOrdinal(int ordinal);

    public boolean getLiveOnSite(int ordinal);

    public Boolean getLiveOnSiteBoxed(int ordinal);

    public long getLiveOnSiteFlipDate(int ordinal);

    public Long getLiveOnSiteFlipDateBoxed(int ordinal);

    public int getLiveOnSiteFlipDateOrdinal(int ordinal);

    public int getOffsiteReasonsOrdinal(int ordinal);

    public boolean getContentApproved(int ordinal);

    public Boolean getContentApprovedBoxed(int ordinal);

    public boolean getAllowIncomplete(int ordinal);

    public Boolean getAllowIncompleteBoxed(int ordinal);

    public boolean getGoLivePartialSubDubIgnored(int ordinal);

    public Boolean getGoLivePartialSubDubIgnoredBoxed(int ordinal);

    public String getAlternateLanguage(int ordinal);

    public boolean isAlternateLanguageEqual(int ordinal, String testValue);

    public int getAlternateLanguageOrdinal(int ordinal);

    public boolean getHasRequiredLanguages(int ordinal);

    public Boolean getHasRequiredLanguagesBoxed(int ordinal);

    public boolean getHasRequiredStreams(int ordinal);

    public Boolean getHasRequiredStreamsBoxed(int ordinal);

    public boolean getReleaseAsAvailable(int ordinal);

    public Boolean getReleaseAsAvailableBoxed(int ordinal);

    public String getRemoveAsset(int ordinal);

    public boolean isRemoveAssetEqual(int ordinal, String testValue);

    public int getRemoveAssetOrdinal(int ordinal);

    public boolean getRemoveFromWebsiteOverride(int ordinal);

    public Boolean getRemoveFromWebsiteOverrideBoxed(int ordinal);

    public int getRequiredLangsOrdinal(int ordinal);

    public boolean getSearchOnlyOverride(int ordinal);

    public Boolean getSearchOnlyOverrideBoxed(int ordinal);

    public boolean getAllowPartialSubsDubsOverride(int ordinal);

    public Boolean getAllowPartialSubsDubsOverrideBoxed(int ordinal);

    public boolean getIgnoreLanguageRequirementOverride(int ordinal);

    public Boolean getIgnoreLanguageRequirementOverrideBoxed(int ordinal);

    public boolean getSubsRequired(int ordinal);

    public Boolean getSubsRequiredBoxed(int ordinal);

    public boolean getDubsRequired(int ordinal);

    public Boolean getDubsRequiredBoxed(int ordinal);

    public FlagsTypeAPI getTypeAPI();

}