package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FlagsHollow extends HollowObject {

    public FlagsHollow(FlagsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getSearchOnly() {
        return delegate().getSearchOnly(ordinal);
    }

    public Boolean _getSearchOnlyBoxed() {
        return delegate().getSearchOnlyBoxed(ordinal);
    }

    public boolean _getLocalText() {
        return delegate().getLocalText(ordinal);
    }

    public Boolean _getLocalTextBoxed() {
        return delegate().getLocalTextBoxed(ordinal);
    }

    public boolean _getLanguageOverride() {
        return delegate().getLanguageOverride(ordinal);
    }

    public Boolean _getLanguageOverrideBoxed() {
        return delegate().getLanguageOverrideBoxed(ordinal);
    }

    public boolean _getLocalAudio() {
        return delegate().getLocalAudio(ordinal);
    }

    public Boolean _getLocalAudioBoxed() {
        return delegate().getLocalAudioBoxed(ordinal);
    }

    public boolean _getGoLive() {
        return delegate().getGoLive(ordinal);
    }

    public Boolean _getGoLiveBoxed() {
        return delegate().getGoLiveBoxed(ordinal);
    }

    public boolean _getContentApproved() {
        return delegate().getContentApproved(ordinal);
    }

    public Boolean _getContentApprovedBoxed() {
        return delegate().getContentApprovedBoxed(ordinal);
    }

    public boolean _getAutoPlay() {
        return delegate().getAutoPlay(ordinal);
    }

    public Boolean _getAutoPlayBoxed() {
        return delegate().getAutoPlayBoxed(ordinal);
    }

    public DateHollow _getFirstDisplayDate() {
        int refOrdinal = delegate().getFirstDisplayDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public MapOfFlagsFirstDisplayDatesHollow _getFirstDisplayDates() {
        int refOrdinal = delegate().getFirstDisplayDatesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfFlagsFirstDisplayDatesHollow(refOrdinal);
    }

    public boolean _getLiveOnSite() {
        return delegate().getLiveOnSite(ordinal);
    }

    public Boolean _getLiveOnSiteBoxed() {
        return delegate().getLiveOnSiteBoxed(ordinal);
    }

    public ListOfStringHollow _getOffsiteReasons() {
        int refOrdinal = delegate().getOffsiteReasonsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfStringHollow(refOrdinal);
    }

    public boolean _getContentApproved() {
        return delegate().getContentApproved(ordinal);
    }

    public Boolean _getContentApprovedBoxed() {
        return delegate().getContentApprovedBoxed(ordinal);
    }

    public boolean _getAllowIncomplete() {
        return delegate().getAllowIncomplete(ordinal);
    }

    public Boolean _getAllowIncompleteBoxed() {
        return delegate().getAllowIncompleteBoxed(ordinal);
    }

    public boolean _getGoLivePartialSubDubIgnored() {
        return delegate().getGoLivePartialSubDubIgnored(ordinal);
    }

    public Boolean _getGoLivePartialSubDubIgnoredBoxed() {
        return delegate().getGoLivePartialSubDubIgnoredBoxed(ordinal);
    }

    public StringHollow _getAlternateLanguage() {
        int refOrdinal = delegate().getAlternateLanguageOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getHasRequiredLanguages() {
        return delegate().getHasRequiredLanguages(ordinal);
    }

    public Boolean _getHasRequiredLanguagesBoxed() {
        return delegate().getHasRequiredLanguagesBoxed(ordinal);
    }

    public boolean _getHasRequiredStreams() {
        return delegate().getHasRequiredStreams(ordinal);
    }

    public Boolean _getHasRequiredStreamsBoxed() {
        return delegate().getHasRequiredStreamsBoxed(ordinal);
    }

    public boolean _getReleaseAsAvailable() {
        return delegate().getReleaseAsAvailable(ordinal);
    }

    public Boolean _getReleaseAsAvailableBoxed() {
        return delegate().getReleaseAsAvailableBoxed(ordinal);
    }

    public StringHollow _getRemoveAsset() {
        int refOrdinal = delegate().getRemoveAssetOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getRemoveFromWebsiteOverride() {
        return delegate().getRemoveFromWebsiteOverride(ordinal);
    }

    public Boolean _getRemoveFromWebsiteOverrideBoxed() {
        return delegate().getRemoveFromWebsiteOverrideBoxed(ordinal);
    }

    public SetOfStringHollow _getRequiredLangs() {
        int refOrdinal = delegate().getRequiredLangsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfStringHollow(refOrdinal);
    }

    public boolean _getSearchOnlyOverride() {
        return delegate().getSearchOnlyOverride(ordinal);
    }

    public Boolean _getSearchOnlyOverrideBoxed() {
        return delegate().getSearchOnlyOverrideBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public FlagsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FlagsDelegate delegate() {
        return (FlagsDelegate)delegate;
    }

}