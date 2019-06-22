package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Flags extends HollowObject {

    public Flags(FlagsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean getSearchOnly() {
        return delegate().getSearchOnly(ordinal);
    }

    public Boolean getSearchOnlyBoxed() {
        return delegate().getSearchOnlyBoxed(ordinal);
    }

    public boolean getLocalText() {
        return delegate().getLocalText(ordinal);
    }

    public Boolean getLocalTextBoxed() {
        return delegate().getLocalTextBoxed(ordinal);
    }

    public boolean getLanguageOverride() {
        return delegate().getLanguageOverride(ordinal);
    }

    public Boolean getLanguageOverrideBoxed() {
        return delegate().getLanguageOverrideBoxed(ordinal);
    }

    public boolean getLocalAudio() {
        return delegate().getLocalAudio(ordinal);
    }

    public Boolean getLocalAudioBoxed() {
        return delegate().getLocalAudioBoxed(ordinal);
    }

    public boolean getGoLive() {
        return delegate().getGoLive(ordinal);
    }

    public Boolean getGoLiveBoxed() {
        return delegate().getGoLiveBoxed(ordinal);
    }

    public Long getGoLiveFlipDateBoxed() {
        return delegate().getGoLiveFlipDateBoxed(ordinal);
    }

    public long getGoLiveFlipDate() {
        return delegate().getGoLiveFlipDate(ordinal);
    }

    public Date getGoLiveFlipDateHollowReference() {
        int refOrdinal = delegate().getGoLiveFlipDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public boolean getAutoPlay() {
        return delegate().getAutoPlay(ordinal);
    }

    public Boolean getAutoPlayBoxed() {
        return delegate().getAutoPlayBoxed(ordinal);
    }

    public Long getFirstDisplayDateBoxed() {
        return delegate().getFirstDisplayDateBoxed(ordinal);
    }

    public long getFirstDisplayDate() {
        return delegate().getFirstDisplayDate(ordinal);
    }

    public Date getFirstDisplayDateHollowReference() {
        int refOrdinal = delegate().getFirstDisplayDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public MapOfFlagsFirstDisplayDates getFirstDisplayDates() {
        int refOrdinal = delegate().getFirstDisplayDatesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfFlagsFirstDisplayDates(refOrdinal);
    }

    public SetOfString getGrandfatheredLanguages() {
        int refOrdinal = delegate().getGrandfatheredLanguagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public boolean getLiveOnSite() {
        return delegate().getLiveOnSite(ordinal);
    }

    public Boolean getLiveOnSiteBoxed() {
        return delegate().getLiveOnSiteBoxed(ordinal);
    }

    public Long getLiveOnSiteFlipDateBoxed() {
        return delegate().getLiveOnSiteFlipDateBoxed(ordinal);
    }

    public long getLiveOnSiteFlipDate() {
        return delegate().getLiveOnSiteFlipDate(ordinal);
    }

    public Date getLiveOnSiteFlipDateHollowReference() {
        int refOrdinal = delegate().getLiveOnSiteFlipDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public ListOfString getOffsiteReasons() {
        int refOrdinal = delegate().getOffsiteReasonsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfString(refOrdinal);
    }

    public boolean getContentApproved() {
        return delegate().getContentApproved(ordinal);
    }

    public Boolean getContentApprovedBoxed() {
        return delegate().getContentApprovedBoxed(ordinal);
    }

    public boolean getAllowIncomplete() {
        return delegate().getAllowIncomplete(ordinal);
    }

    public Boolean getAllowIncompleteBoxed() {
        return delegate().getAllowIncompleteBoxed(ordinal);
    }

    public boolean getGoLivePartialSubDubIgnored() {
        return delegate().getGoLivePartialSubDubIgnored(ordinal);
    }

    public Boolean getGoLivePartialSubDubIgnoredBoxed() {
        return delegate().getGoLivePartialSubDubIgnoredBoxed(ordinal);
    }

    public String getAlternateLanguage() {
        return delegate().getAlternateLanguage(ordinal);
    }

    public boolean isAlternateLanguageEqual(String testValue) {
        return delegate().isAlternateLanguageEqual(ordinal, testValue);
    }

    public HString getAlternateLanguageHollowReference() {
        int refOrdinal = delegate().getAlternateLanguageOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public boolean getHasRequiredLanguages() {
        return delegate().getHasRequiredLanguages(ordinal);
    }

    public Boolean getHasRequiredLanguagesBoxed() {
        return delegate().getHasRequiredLanguagesBoxed(ordinal);
    }

    public boolean getHasRequiredStreams() {
        return delegate().getHasRequiredStreams(ordinal);
    }

    public Boolean getHasRequiredStreamsBoxed() {
        return delegate().getHasRequiredStreamsBoxed(ordinal);
    }

    public boolean getReleaseAsAvailable() {
        return delegate().getReleaseAsAvailable(ordinal);
    }

    public Boolean getReleaseAsAvailableBoxed() {
        return delegate().getReleaseAsAvailableBoxed(ordinal);
    }

    public String getRemoveAsset() {
        return delegate().getRemoveAsset(ordinal);
    }

    public boolean isRemoveAssetEqual(String testValue) {
        return delegate().isRemoveAssetEqual(ordinal, testValue);
    }

    public HString getRemoveAssetHollowReference() {
        int refOrdinal = delegate().getRemoveAssetOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public boolean getRemoveFromWebsiteOverride() {
        return delegate().getRemoveFromWebsiteOverride(ordinal);
    }

    public Boolean getRemoveFromWebsiteOverrideBoxed() {
        return delegate().getRemoveFromWebsiteOverrideBoxed(ordinal);
    }

    public SetOfString getRequiredLangs() {
        int refOrdinal = delegate().getRequiredLangsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public boolean getSearchOnlyOverride() {
        return delegate().getSearchOnlyOverride(ordinal);
    }

    public Boolean getSearchOnlyOverrideBoxed() {
        return delegate().getSearchOnlyOverrideBoxed(ordinal);
    }

    public boolean getAllowPartialSubsDubsOverride() {
        return delegate().getAllowPartialSubsDubsOverride(ordinal);
    }

    public Boolean getAllowPartialSubsDubsOverrideBoxed() {
        return delegate().getAllowPartialSubsDubsOverrideBoxed(ordinal);
    }

    public boolean getIgnoreLanguageRequirementOverride() {
        return delegate().getIgnoreLanguageRequirementOverride(ordinal);
    }

    public Boolean getIgnoreLanguageRequirementOverrideBoxed() {
        return delegate().getIgnoreLanguageRequirementOverrideBoxed(ordinal);
    }

    public boolean getSubsRequired() {
        return delegate().getSubsRequired(ordinal);
    }

    public Boolean getSubsRequiredBoxed() {
        return delegate().getSubsRequiredBoxed(ordinal);
    }

    public boolean getDubsRequired() {
        return delegate().getDubsRequired(ordinal);
    }

    public Boolean getDubsRequiredBoxed() {
        return delegate().getDubsRequiredBoxed(ordinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public FlagsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FlagsDelegate delegate() {
        return (FlagsDelegate)delegate;
    }

}