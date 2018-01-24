package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class FlagsTypeAPI extends HollowObjectTypeAPI {

    private final FlagsDelegateLookupImpl delegateLookupImpl;

    public FlagsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "searchOnly",
            "localText",
            "languageOverride",
            "localAudio",
            "goLive",
            "autoPlay",
            "firstDisplayDate",
            "firstDisplayDates",
            "liveOnSite",
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
            "subsRequiredLanguages",
            "dubsRequiredLanguages",
            "localizationRequiredLanguages"
        });
        this.delegateLookupImpl = new FlagsDelegateLookupImpl(this);
    }

    public boolean getSearchOnly(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnly") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getSearchOnlyBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getLocalText(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localText") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getLocalTextBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localText");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public boolean getLanguageOverride(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "languageOverride") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]) == Boolean.TRUE;
    }

    public Boolean getLanguageOverrideBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "languageOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public boolean getLocalAudio(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localAudio") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getLocalAudioBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "localAudio");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public boolean getGoLive(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLive") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getGoLiveBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLive");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public boolean getAutoPlay(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "autoPlay") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]) == Boolean.TRUE;
    }

    public Boolean getAutoPlayBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "autoPlay");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public int getFirstDisplayDateOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "firstDisplayDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DateTypeAPI getFirstDisplayDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getFirstDisplayDatesOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "firstDisplayDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public MapOfFlagsFirstDisplayDatesTypeAPI getFirstDisplayDatesTypeAPI() {
        return getAPI().getMapOfFlagsFirstDisplayDatesTypeAPI();
    }

    public boolean getLiveOnSite(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "liveOnSite") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]) == Boolean.TRUE;
    }

    public Boolean getLiveOnSiteBoxed(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "liveOnSite");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]);
    }



    public int getOffsiteReasonsOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "offsiteReasons");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public ListOfStringTypeAPI getOffsiteReasonsTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public boolean getContentApproved(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "contentApproved") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]) == Boolean.TRUE;
    }

    public Boolean getContentApprovedBoxed(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "contentApproved");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]);
    }



    public boolean getAllowIncomplete(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "allowIncomplete") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]) == Boolean.TRUE;
    }

    public Boolean getAllowIncompleteBoxed(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "allowIncomplete");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]);
    }



    public boolean getGoLivePartialSubDubIgnored(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLivePartialSubDubIgnored") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[12]) == Boolean.TRUE;
    }

    public Boolean getGoLivePartialSubDubIgnoredBoxed(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "goLivePartialSubDubIgnored");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[12]);
    }



    public int getAlternateLanguageOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "alternateLanguage");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public StringTypeAPI getAlternateLanguageTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getHasRequiredLanguages(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredLanguages") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[14]) == Boolean.TRUE;
    }

    public Boolean getHasRequiredLanguagesBoxed(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredLanguages");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[14]);
    }



    public boolean getHasRequiredStreams(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredStreams") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[15]) == Boolean.TRUE;
    }

    public Boolean getHasRequiredStreamsBoxed(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "hasRequiredStreams");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[15]);
    }



    public boolean getReleaseAsAvailable(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "releaseAsAvailable") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[16]) == Boolean.TRUE;
    }

    public Boolean getReleaseAsAvailableBoxed(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "releaseAsAvailable");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[16]);
    }



    public int getRemoveAssetOrdinal(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "removeAsset");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[17]);
    }

    public StringTypeAPI getRemoveAssetTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getRemoveFromWebsiteOverride(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "removeFromWebsiteOverride") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[18]) == Boolean.TRUE;
    }

    public Boolean getRemoveFromWebsiteOverrideBoxed(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "removeFromWebsiteOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[18]);
    }



    public int getRequiredLangsOrdinal(int ordinal) {
        if(fieldIndex[19] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "requiredLangs");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[19]);
    }

    public SetOfStringTypeAPI getRequiredLangsTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public boolean getSearchOnlyOverride(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnlyOverride") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[20]) == Boolean.TRUE;
    }

    public Boolean getSearchOnlyOverrideBoxed(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleBoolean("Flags", ordinal, "searchOnlyOverride");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[20]);
    }



    public int getSubsRequiredLanguagesOrdinal(int ordinal) {
        if(fieldIndex[21] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "subsRequiredLanguages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[21]);
    }

    public SetOfStringTypeAPI getSubsRequiredLanguagesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getDubsRequiredLanguagesOrdinal(int ordinal) {
        if(fieldIndex[22] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "dubsRequiredLanguages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[22]);
    }

    public SetOfStringTypeAPI getDubsRequiredLanguagesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public int getLocalizationRequiredLanguagesOrdinal(int ordinal) {
        if(fieldIndex[23] == -1)
            return missingDataHandler().handleReferencedOrdinal("Flags", ordinal, "localizationRequiredLanguages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[23]);
    }

    public SetOfStringTypeAPI getLocalizationRequiredLanguagesTypeAPI() {
        return getAPI().getSetOfStringTypeAPI();
    }

    public FlagsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}