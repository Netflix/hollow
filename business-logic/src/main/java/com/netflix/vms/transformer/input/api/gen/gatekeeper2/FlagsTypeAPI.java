package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class FlagsTypeAPI extends HollowObjectTypeAPI {

    private final FlagsDelegateLookupImpl delegateLookupImpl;

    public FlagsTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "searchOnly",
            "localText",
            "languageOverride",
            "localAudio",
            "goLive",
            "goLiveFlipDate",
            "autoPlay",
            "firstDisplayDate",
            "firstDisplayDates",
            "grandfatheredLanguages",
            "liveOnSite",
            "liveOnSiteFlipDate",
            "offsiteReasons",
            "contentApproved",
            "allowIncomplete",
            "goLivePartialSubDubIgnored",
            "alternateLanguage",
            "hasRequiredLanguages",
            "hasRequiredStreams",
            "releaseAsAvailable",
            "removeAsset",
            "removeFromWebsiteOverride",
            "requiredLangs",
            "searchOnlyOverride",
            "allowPartialSubsDubsOverride",
            "ignoreLanguageRequirementOverride",
            "subsRequired",
            "dubsRequired"
        });
        this.delegateLookupImpl = new FlagsDelegateLookupImpl(this);
    }

    public boolean getSearchOnly(int ordinal) {
        if(fieldIndex[0] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "searchOnly"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]));
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getLocalText(int ordinal) {
        if(fieldIndex[1] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "localText"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]));
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localText");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public boolean getLanguageOverride(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "languageOverride"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "languageOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public boolean getLocalAudio(int ordinal) {
        if(fieldIndex[3] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "localAudio"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]));
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localAudio");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public boolean getGoLive(int ordinal) {
        if(fieldIndex[4] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "goLive"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]));
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLive");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public int getGoLiveFlipDateOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "goLiveFlipDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public DateTypeAPI getGoLiveFlipDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public boolean getAutoPlay(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "autoPlay"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "autoPlay");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public int getFirstDisplayDateOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "firstDisplayDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public DateTypeAPI getFirstDisplayDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "firstDisplayDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public MapOfFlagsFirstDisplayDatesTypeAPI getFirstDisplayDatesTypeAPI() {
        return getAPI().getMapOfFlagsFirstDisplayDatesTypeAPI();
    }

    public int getGrandfatheredLanguagesOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "grandfatheredLanguages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public SetOfStringTypeAPI getGrandfatheredLanguagesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public boolean getLiveOnSite(int ordinal) {
        if(fieldIndex[10] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "liveOnSite"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]));
    }

    public Boolean getLiveOnSiteBoxed(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "liveOnSite");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]);
    }



    public int getLiveOnSiteFlipDateOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "liveOnSiteFlipDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public DateTypeAPI getLiveOnSiteFlipDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getOffsiteReasonsOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "offsiteReasons");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public ListOfStringTypeAPI getOffsiteReasonsTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public boolean getContentApproved(int ordinal) {
        if(fieldIndex[13] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "contentApproved"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[13]));
    }

    public Boolean getContentApprovedBoxed(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "contentApproved");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[13]);
    }



    public boolean getAllowIncomplete(int ordinal) {
        if(fieldIndex[14] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "allowIncomplete"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[14]));
    }

    public Boolean getAllowIncompleteBoxed(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "allowIncomplete");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[14]);
    }



    public boolean getGoLivePartialSubDubIgnored(int ordinal) {
        if(fieldIndex[15] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "goLivePartialSubDubIgnored"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[15]));
    }

    public Boolean getGoLivePartialSubDubIgnoredBoxed(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLivePartialSubDubIgnored");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[15]);
    }



    public int getAlternateLanguageOrdinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "alternateLanguage");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public StringTypeAPI getAlternateLanguageTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getHasRequiredLanguages(int ordinal) {
        if(fieldIndex[17] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredLanguages"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[17]));
    }

    public Boolean getHasRequiredLanguagesBoxed(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredLanguages");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[17]);
    }



    public boolean getHasRequiredStreams(int ordinal) {
        if(fieldIndex[18] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredStreams"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[18]));
    }

    public Boolean getHasRequiredStreamsBoxed(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredStreams");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[18]);
    }



    public boolean getReleaseAsAvailable(int ordinal) {
        if(fieldIndex[19] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "releaseAsAvailable"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[19]));
    }

    public Boolean getReleaseAsAvailableBoxed(int ordinal) {
        if(fieldIndex[19] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "releaseAsAvailable");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[19]);
    }



    public int getRemoveAssetOrdinal(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "removeAsset");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[20]);
    }

    public StringTypeAPI getRemoveAssetTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getRemoveFromWebsiteOverride(int ordinal) {
        if(fieldIndex[21] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "removeFromWebsiteOverride"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[21]));
    }

    public Boolean getRemoveFromWebsiteOverrideBoxed(int ordinal) {
        if(fieldIndex[21] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "removeFromWebsiteOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[21]);
    }



    public int getRequiredLangsOrdinal(int ordinal) {
        if(fieldIndex[22] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "requiredLangs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[22]);
    }

    public SetOfStringTypeAPI getRequiredLangsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public boolean getSearchOnlyOverride(int ordinal) {
        if(fieldIndex[23] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "searchOnlyOverride"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[23]));
    }

    public Boolean getSearchOnlyOverrideBoxed(int ordinal) {
        if(fieldIndex[23] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnlyOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[23]);
    }



    public boolean getAllowPartialSubsDubsOverride(int ordinal) {
        if(fieldIndex[24] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "allowPartialSubsDubsOverride"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[24]));
    }

    public Boolean getAllowPartialSubsDubsOverrideBoxed(int ordinal) {
        if(fieldIndex[24] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "allowPartialSubsDubsOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[24]);
    }



    public boolean getIgnoreLanguageRequirementOverride(int ordinal) {
        if(fieldIndex[25] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "ignoreLanguageRequirementOverride"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[25]));
    }

    public Boolean getIgnoreLanguageRequirementOverrideBoxed(int ordinal) {
        if(fieldIndex[25] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "ignoreLanguageRequirementOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[25]);
    }



    public boolean getSubsRequired(int ordinal) {
        if(fieldIndex[26] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "subsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[26]));
    }

    public Boolean getSubsRequiredBoxed(int ordinal) {
        if(fieldIndex[26] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "subsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[26]);
    }



    public boolean getDubsRequired(int ordinal) {
        if(fieldIndex[27] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Flags", ordinal, "dubsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[27]));
    }

    public Boolean getDubsRequiredBoxed(int ordinal) {
        if(fieldIndex[27] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "dubsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[27]);
    }



    public FlagsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}