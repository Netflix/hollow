package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;

import java.util.ArrayList;
import java.util.List;

public class IndexDuplicateChecker {

    private final HollowReadStateEngine stateEngine;
    private final List<String> dupKeyInTypeList;

    public IndexDuplicateChecker(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.dupKeyInTypeList = new ArrayList<>();
    }

    public void checkIndex(String type, String... keyFieldPaths) {
        if (new HollowPrimaryKeyIndex(stateEngine, type, keyFieldPaths).containsDuplicates())
            dupKeyInTypeList.add(type);
    }

    public void checkDuplicates() {
        checkIndex("CompleteVideo", "id.value", "country");
        checkIndex("VideoEpisode_CountryList", "country", "item.deliverableVideo.value");
        checkIndex("PackageData", "id");
        checkIndex("StreamData", "downloadableId");
        checkIndex("NamedCollectionHolder", "country");
        checkIndex("EncodingProfile", "id");
        checkIndex("OriginServer", "nameStr");
        checkIndex("LanguageRights", "contractId", "videoId.value");
        checkIndex("DeploymentIntent", "profileId", "bitrate", "country.id");
        checkIndex("GlobalPerson", "id");
        checkIndex("GlobalVideo", "completeVideo.id.value");
        checkIndex("PersonImages", "id");
        checkIndex("ArtWorkImageFormatEntry", "nameStr");
        checkIndex("ArtWorkImageTypeEntry", "nameStr");
        checkIndex("ArtWorkImageRecipe", "recipeNameStr");
        checkIndex("DefaultExtensionRecipe", "extensionStr");
        checkIndex("DrmKey", "keyId");
        checkIndex("WmDrmKey", "downloadableId");
        checkIndex("DrmInfoData", "packageId");
        checkIndex("DrmSystem", "id");
        checkIndex("L10NResources", "resourceIdStr");
        checkIndex("EncodingProfileGroup", "groupNameStr");
        checkIndex("CharacterImages", "id");
        checkIndex("FileEncodingData", "downloadableId");
        checkIndex("RolloutVideo", "video.value");
    }

    public boolean wasDupKeysDetected() {
        return !dupKeyInTypeList.isEmpty();
    }

    /**
     * Return Empty List if there are no duplicate keys detected; otherwise, the list with the type
     */
    public List<String> getResults() {
        return dupKeyInTypeList;
    }
}