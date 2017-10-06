package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class FlagsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, FlagsDelegate {

    private final Boolean searchOnly;
    private final Boolean localText;
    private final Boolean languageOverride;
    private final Boolean localAudio;
    private final Boolean goLive;
    private final Boolean autoPlay;
    private final int firstDisplayDateOrdinal;
    private final int firstDisplayDatesOrdinal;
    private final Boolean liveOnSite;
    private final int offsiteReasonsOrdinal;
    private final Boolean contentApproved;
    private final Boolean allowIncomplete;
    private final Boolean goLivePartialSubDubIgnored;
    private final int alternateLanguageOrdinal;
    private final Boolean hasRequiredLanguages;
    private final Boolean hasRequiredStreams;
    private final Boolean releaseAsAvailable;
    private final int removeAssetOrdinal;
    private final Boolean removeFromWebsiteOverride;
    private final int requiredLangsOrdinal;
    private final Boolean searchOnlyOverride;
    private FlagsTypeAPI typeAPI;

    public FlagsDelegateCachedImpl(FlagsTypeAPI typeAPI, int ordinal) {
        this.searchOnly = typeAPI.getSearchOnlyBoxed(ordinal);
        this.localText = typeAPI.getLocalTextBoxed(ordinal);
        this.languageOverride = typeAPI.getLanguageOverrideBoxed(ordinal);
        this.localAudio = typeAPI.getLocalAudioBoxed(ordinal);
        this.goLive = typeAPI.getGoLiveBoxed(ordinal);
        this.autoPlay = typeAPI.getAutoPlayBoxed(ordinal);
        this.firstDisplayDateOrdinal = typeAPI.getFirstDisplayDateOrdinal(ordinal);
        this.firstDisplayDatesOrdinal = typeAPI.getFirstDisplayDatesOrdinal(ordinal);
        this.liveOnSite = typeAPI.getLiveOnSiteBoxed(ordinal);
        this.offsiteReasonsOrdinal = typeAPI.getOffsiteReasonsOrdinal(ordinal);
        this.contentApproved = typeAPI.getContentApprovedBoxed(ordinal);
        this.allowIncomplete = typeAPI.getAllowIncompleteBoxed(ordinal);
        this.goLivePartialSubDubIgnored = typeAPI.getGoLivePartialSubDubIgnoredBoxed(ordinal);
        this.alternateLanguageOrdinal = typeAPI.getAlternateLanguageOrdinal(ordinal);
        this.hasRequiredLanguages = typeAPI.getHasRequiredLanguagesBoxed(ordinal);
        this.hasRequiredStreams = typeAPI.getHasRequiredStreamsBoxed(ordinal);
        this.releaseAsAvailable = typeAPI.getReleaseAsAvailableBoxed(ordinal);
        this.removeAssetOrdinal = typeAPI.getRemoveAssetOrdinal(ordinal);
        this.removeFromWebsiteOverride = typeAPI.getRemoveFromWebsiteOverrideBoxed(ordinal);
        this.requiredLangsOrdinal = typeAPI.getRequiredLangsOrdinal(ordinal);
        this.searchOnlyOverride = typeAPI.getSearchOnlyOverrideBoxed(ordinal);
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

    public boolean getAutoPlay(int ordinal) {
        if(autoPlay == null)
            return false;
        return autoPlay.booleanValue();
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        return autoPlay;
    }

    public int getFirstDisplayDateOrdinal(int ordinal) {
        return firstDisplayDateOrdinal;
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        return firstDisplayDatesOrdinal;
    }

    public boolean getLiveOnSite(int ordinal) {
        if(liveOnSite == null)
            return false;
        return liveOnSite.booleanValue();
    }

    public Boolean getLiveOnSiteBoxed(int ordinal) {
        return liveOnSite;
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