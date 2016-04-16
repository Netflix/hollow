package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;

public class DuplicateDetectionCircuitBreaker extends HollowCircuitBreaker {

    public DuplicateDetectionCircuitBreaker(PublishWorkflowContext ctx, long versionId) {
        super(ctx, versionId);
    }

    @Override
    public String getRuleName() {
        return "DuplicateDetection";
    }

    @Override
    public CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        IndexDuplicateChecker dupChecker = new IndexDuplicateChecker(stateEngine);

        dupChecker.checkIndex("CompleteVideo", "id.value", "country");
        dupChecker.checkIndex("VideoEpisode_CountryList", "country", "item.deliverableVideo.value");
        dupChecker.checkIndex("PackageData", "id");
        dupChecker.checkIndex("StreamData", "downloadableId");
        dupChecker.checkIndex("NamedCollectionHolder", "country");
        dupChecker.checkIndex("EncodingProfile", "id");
        dupChecker.checkIndex("OriginServer", "nameStr");
        dupChecker.checkIndex("LanguageRights", "contractId", "videoId.value");
        dupChecker.checkIndex("DeploymentIntent", "profileId", "bitrate", "country.id");
        dupChecker.checkIndex("GlobalPerson", "id");
        dupChecker.checkIndex("GlobalVideo", "completeVideo.id.value");
        dupChecker.checkIndex("PersonImages", "id");
        dupChecker.checkIndex("ArtWorkImageFormatEntry", "nameStr");
        dupChecker.checkIndex("ArtWorkImageTypeEntry", "nameStr");
        dupChecker.checkIndex("ArtWorkImageRecipe", "recipeNameStr");
        dupChecker.checkIndex("DefaultExtensionRecipe", "extensionStr");
        dupChecker.checkIndex("DrmKey", "keyId");
        dupChecker.checkIndex("WmDrmKey", "downloadableId");
        dupChecker.checkIndex("DrmInfoData", "packageId");
        dupChecker.checkIndex("DrmSystem", "id");
        dupChecker.checkIndex("L10NResources", "resourceIdStr");
        dupChecker.checkIndex("EncodingProfileGroup", "groupNameStr");
        dupChecker.checkIndex("CharacterImages", "id");
        dupChecker.checkIndex("FileEncodingData", "downloadableId");
        dupChecker.checkIndex("RolloutVideo", "video.value");
        dupChecker.checkIndex("RolloutCharacter", "id");

        CircuitBreakerResults results = dupChecker.getResults();

        if(results == null)
            return PASSED;

        return results;
    }

    private static class IndexDuplicateChecker {
        private final HollowReadStateEngine stateEngine;
        private final CircuitBreakerResults results;

        public IndexDuplicateChecker(HollowReadStateEngine stateEngine) {
            this.stateEngine = stateEngine;
            this.results = new CircuitBreakerResults();
        }

        public void checkIndex(String type, String... keyFieldPaths) {
            if(new HollowPrimaryKeyIndex(stateEngine, type, keyFieldPaths).containsDuplicates())
                results.addResult(false, "Duplicate keys found for type: " + type);
        }

        public CircuitBreakerResults getResults() {
            if(results.iterator().hasNext())
                return results;
            return null;
        }

    }

}
