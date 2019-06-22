package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FlagsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, FlagsDelegate {

    private final Boolean searchOnly;
    private final Boolean localText;
    private final Boolean languageOverride;
    private final Boolean localAudio;
    private final Boolean goLive;
    private final Long goLiveFlipDate;
    private final int goLiveFlipDateOrdinal;
    private final Boolean autoPlay;
    private final Long firstDisplayDate;
    private final int firstDisplayDateOrdinal;
    private final int firstDisplayDatesOrdinal;
    private final int grandfatheredLanguagesOrdinal;
    private final Boolean liveOnSite;
    private final Long liveOnSiteFlipDate;
    private final int liveOnSiteFlipDateOrdinal;
    private final int offsiteReasonsOrdinal;
    private final Boolean contentApproved;
    private final Boolean allowIncomplete;
    private final Boolean goLivePartialSubDubIgnored;
    private final String alternateLanguage;
    private final int alternateLanguageOrdinal;
    private final Boolean hasRequiredLanguages;
    private final Boolean hasRequiredStreams;
    private final Boolean releaseAsAvailable;
    private final String removeAsset;
    private final int removeAssetOrdinal;
    private final Boolean removeFromWebsiteOverride;
    private final int requiredLangsOrdinal;
    private final Boolean searchOnlyOverride;
    private final Boolean allowPartialSubsDubsOverride;
    private final Boolean ignoreLanguageRequirementOverride;
    private final Boolean subsRequired;
    private final Boolean dubsRequired;
    private FlagsTypeAPI typeAPI;

    public FlagsDelegateCachedImpl(FlagsTypeAPI typeAPI, int ordinal) {
        this.searchOnly = typeAPI.getSearchOnlyBoxed(ordinal);
        this.localText = typeAPI.getLocalTextBoxed(ordinal);
        this.languageOverride = typeAPI.getLanguageOverrideBoxed(ordinal);
        this.localAudio = typeAPI.getLocalAudioBoxed(ordinal);
        this.goLive = typeAPI.getGoLiveBoxed(ordinal);
        this.goLiveFlipDateOrdinal = typeAPI.getGoLiveFlipDateOrdinal(ordinal);
        int goLiveFlipDateTempOrdinal = goLiveFlipDateOrdinal;
        this.goLiveFlipDate = goLiveFlipDateTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(goLiveFlipDateTempOrdinal);
        this.autoPlay = typeAPI.getAutoPlayBoxed(ordinal);
        this.firstDisplayDateOrdinal = typeAPI.getFirstDisplayDateOrdinal(ordinal);
        int firstDisplayDateTempOrdinal = firstDisplayDateOrdinal;
        this.firstDisplayDate = firstDisplayDateTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(firstDisplayDateTempOrdinal);
        this.firstDisplayDatesOrdinal = typeAPI.getFirstDisplayDatesOrdinal(ordinal);
        this.grandfatheredLanguagesOrdinal = typeAPI.getGrandfatheredLanguagesOrdinal(ordinal);
        this.liveOnSite = typeAPI.getLiveOnSiteBoxed(ordinal);
        this.liveOnSiteFlipDateOrdinal = typeAPI.getLiveOnSiteFlipDateOrdinal(ordinal);
        int liveOnSiteFlipDateTempOrdinal = liveOnSiteFlipDateOrdinal;
        this.liveOnSiteFlipDate = liveOnSiteFlipDateTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(liveOnSiteFlipDateTempOrdinal);
        this.offsiteReasonsOrdinal = typeAPI.getOffsiteReasonsOrdinal(ordinal);
        this.contentApproved = typeAPI.getContentApprovedBoxed(ordinal);
        this.allowIncomplete = typeAPI.getAllowIncompleteBoxed(ordinal);
        this.goLivePartialSubDubIgnored = typeAPI.getGoLivePartialSubDubIgnoredBoxed(ordinal);
        this.alternateLanguageOrdinal = typeAPI.getAlternateLanguageOrdinal(ordinal);
        int alternateLanguageTempOrdinal = alternateLanguageOrdinal;
        this.alternateLanguage = alternateLanguageTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(alternateLanguageTempOrdinal);
        this.hasRequiredLanguages = typeAPI.getHasRequiredLanguagesBoxed(ordinal);
        this.hasRequiredStreams = typeAPI.getHasRequiredStreamsBoxed(ordinal);
        this.releaseAsAvailable = typeAPI.getReleaseAsAvailableBoxed(ordinal);
        this.removeAssetOrdinal = typeAPI.getRemoveAssetOrdinal(ordinal);
        int removeAssetTempOrdinal = removeAssetOrdinal;
        this.removeAsset = removeAssetTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(removeAssetTempOrdinal);
        this.removeFromWebsiteOverride = typeAPI.getRemoveFromWebsiteOverrideBoxed(ordinal);
        this.requiredLangsOrdinal = typeAPI.getRequiredLangsOrdinal(ordinal);
        this.searchOnlyOverride = typeAPI.getSearchOnlyOverrideBoxed(ordinal);
        this.allowPartialSubsDubsOverride = typeAPI.getAllowPartialSubsDubsOverrideBoxed(ordinal);
        this.ignoreLanguageRequirementOverride = typeAPI.getIgnoreLanguageRequirementOverrideBoxed(ordinal);
        this.subsRequired = typeAPI.getSubsRequiredBoxed(ordinal);
        this.dubsRequired = typeAPI.getDubsRequiredBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getSearchOnly(int ordinal) {
        if(searchOnly == null)
            return false;
        return searchOnly.booleanValue();
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        return searchOnly;
    }

    public boolean getLocalText(int ordinal) {
        if(localText == null)
            return false;
        return localText.booleanValue();
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        return localText;
    }

    public boolean getLanguageOverride(int ordinal) {
        if(languageOverride == null)
            return false;
        return languageOverride.booleanValue();
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        return languageOverride;
    }

    public boolean getLocalAudio(int ordinal) {
        if(localAudio == null)
            return false;
        return localAudio.booleanValue();
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        return localAudio;
    }

    public boolean getGoLive(int ordinal) {
        if(goLive == null)
            return false;
        return goLive.booleanValue();
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        return goLive;
    }

    public long getGoLiveFlipDate(int ordinal) {
        if(goLiveFlipDate == null)
            return Long.MIN_VALUE;
        return goLiveFlipDate.longValue();
    }

    public Long getGoLiveFlipDateBoxed(int ordinal) {
        return goLiveFlipDate;
    }

    public int getGoLiveFlipDateOrdinal(int ordinal) {
        return goLiveFlipDateOrdinal;
    }

    public boolean getAutoPlay(int ordinal) {
        if(autoPlay == null)
            return false;
        return autoPlay.booleanValue();
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        return autoPlay;
    }

    public long getFirstDisplayDate(int ordinal) {
        if(firstDisplayDate == null)
            return Long.MIN_VALUE;
        return firstDisplayDate.longValue();
    }

    public Long getFirstDisplayDateBoxed(int ordinal) {
        return firstDisplayDate;
    }

    public int getFirstDisplayDateOrdinal(int ordinal) {
        return firstDisplayDateOrdinal;
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        return firstDisplayDatesOrdinal;
    }

    public int getGrandfatheredLanguagesOrdinal(int ordinal) {
        return grandfatheredLanguagesOrdinal;
    }

    public boolean getLiveOnSite(int ordinal) {
        if(liveOnSite == null)
            return false;
        return liveOnSite.booleanValue();
    }

    public Boolean getLiveOnSiteBoxed(int ordinal) {
        return liveOnSite;
    }

    public long getLiveOnSiteFlipDate(int ordinal) {
        if(liveOnSiteFlipDate == null)
            return Long.MIN_VALUE;
        return liveOnSiteFlipDate.longValue();
    }

    public Long getLiveOnSiteFlipDateBoxed(int ordinal) {
        return liveOnSiteFlipDate;
    }

    public int getLiveOnSiteFlipDateOrdinal(int ordinal) {
        return liveOnSiteFlipDateOrdinal;
    }

    public int getOffsiteReasonsOrdinal(int ordinal) {
        return offsiteReasonsOrdinal;
    }

    public boolean getContentApproved(int ordinal) {
        if(contentApproved == null)
            return false;
        return contentApproved.booleanValue();
    }

    public Boolean getContentApprovedBoxed(int ordinal) {
        return contentApproved;
    }

    public boolean getAllowIncomplete(int ordinal) {
        if(allowIncomplete == null)
            return false;
        return allowIncomplete.booleanValue();
    }

    public Boolean getAllowIncompleteBoxed(int ordinal) {
        return allowIncomplete;
    }

    public boolean getGoLivePartialSubDubIgnored(int ordinal) {
        if(goLivePartialSubDubIgnored == null)
            return false;
        return goLivePartialSubDubIgnored.booleanValue();
    }

    public Boolean getGoLivePartialSubDubIgnoredBoxed(int ordinal) {
        return goLivePartialSubDubIgnored;
    }

    public String getAlternateLanguage(int ordinal) {
        return alternateLanguage;
    }

    public boolean isAlternateLanguageEqual(int ordinal, String testValue) {
        if(testValue == null)
            return alternateLanguage == null;
        return testValue.equals(alternateLanguage);
    }

    public int getAlternateLanguageOrdinal(int ordinal) {
        return alternateLanguageOrdinal;
    }

    public boolean getHasRequiredLanguages(int ordinal) {
        if(hasRequiredLanguages == null)
            return false;
        return hasRequiredLanguages.booleanValue();
    }

    public Boolean getHasRequiredLanguagesBoxed(int ordinal) {
        return hasRequiredLanguages;
    }

    public boolean getHasRequiredStreams(int ordinal) {
        if(hasRequiredStreams == null)
            return false;
        return hasRequiredStreams.booleanValue();
    }

    public Boolean getHasRequiredStreamsBoxed(int ordinal) {
        return hasRequiredStreams;
    }

    public boolean getReleaseAsAvailable(int ordinal) {
        if(releaseAsAvailable == null)
            return false;
        return releaseAsAvailable.booleanValue();
    }

    public Boolean getReleaseAsAvailableBoxed(int ordinal) {
        return releaseAsAvailable;
    }

    public String getRemoveAsset(int ordinal) {
        return removeAsset;
    }

    public boolean isRemoveAssetEqual(int ordinal, String testValue) {
        if(testValue == null)
            return removeAsset == null;
        return testValue.equals(removeAsset);
    }

    public int getRemoveAssetOrdinal(int ordinal) {
        return removeAssetOrdinal;
    }

    public boolean getRemoveFromWebsiteOverride(int ordinal) {
        if(removeFromWebsiteOverride == null)
            return false;
        return removeFromWebsiteOverride.booleanValue();
    }

    public Boolean getRemoveFromWebsiteOverrideBoxed(int ordinal) {
        return removeFromWebsiteOverride;
    }

    public int getRequiredLangsOrdinal(int ordinal) {
        return requiredLangsOrdinal;
    }

    public boolean getSearchOnlyOverride(int ordinal) {
        if(searchOnlyOverride == null)
            return false;
        return searchOnlyOverride.booleanValue();
    }

    public Boolean getSearchOnlyOverrideBoxed(int ordinal) {
        return searchOnlyOverride;
    }

    public boolean getAllowPartialSubsDubsOverride(int ordinal) {
        if(allowPartialSubsDubsOverride == null)
            return false;
        return allowPartialSubsDubsOverride.booleanValue();
    }

    public Boolean getAllowPartialSubsDubsOverrideBoxed(int ordinal) {
        return allowPartialSubsDubsOverride;
    }

    public boolean getIgnoreLanguageRequirementOverride(int ordinal) {
        if(ignoreLanguageRequirementOverride == null)
            return false;
        return ignoreLanguageRequirementOverride.booleanValue();
    }

    public Boolean getIgnoreLanguageRequirementOverrideBoxed(int ordinal) {
        return ignoreLanguageRequirementOverride;
    }

    public boolean getSubsRequired(int ordinal) {
        if(subsRequired == null)
            return false;
        return subsRequired.booleanValue();
    }

    public Boolean getSubsRequiredBoxed(int ordinal) {
        return subsRequired;
    }

    public boolean getDubsRequired(int ordinal) {
        if(dubsRequired == null)
            return false;
        return dubsRequired.booleanValue();
    }

    public Boolean getDubsRequiredBoxed(int ordinal) {
        return dubsRequired;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public FlagsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (FlagsTypeAPI) typeAPI;
    }

}