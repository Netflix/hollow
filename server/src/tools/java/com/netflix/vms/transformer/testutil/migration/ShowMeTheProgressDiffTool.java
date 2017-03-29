package com.netflix.vms.transformer.testutil.migration;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.diff.ui.HollowDiffUI;
import com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import java.util.Random;

public class ShowMeTheProgressDiffTool {

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean isMac = OS != null && (OS.indexOf("mac") >= 0);
    private static final String BROWSER_COMMAND = isMac ? "open" : "google-chrome";

    public static void startTheDiff(HollowReadStateEngine expected, HollowReadStateEngine actual) throws Exception {
        HollowDiff diff = new HollowDiff(expected, actual);
        addTypeDiff(diff, "CompleteVideo", "id.value", "country.id");
        addTypeDiff(diff, "FallbackUSArtwork", "id.value");
        addTypeDiff(diff, "VideoEpisode_CountryList", "country.id", "item.deliverableVideo.value");
        addTypeDiff(diff, "PackageData", "id");
        addTypeDiff(diff, "NamedCollectionHolder", "country.id");
        addTypeDiff(diff, "MulticatalogCountryData", "videoId.value", "country.id");
        addTypeDiff(diff, "EncodingProfile", "id");
        addTypeDiff(diff, "OriginServer", "nameStr");
        addTypeDiff(diff, "LanguageRights", "contractId", "videoId.value");
        addTypeDiff(diff, "DeploymentIntent", "profileId", "bitrate", "country.id");
        addTypeDiff(diff, "LanguageDescriptor", "languageId");
        addTypeDiff(diff, "GlobalPerson", "id");
        addTypeDiff(diff, "GlobalVideo", "completeVideo.id.value");
        addTypeDiff(diff, "PersonImages", "id");
        addTypeDiff(diff, "ArtWorkImageFormatEntry", "nameStr");
        addTypeDiff(diff, "ArtWorkImageTypeEntry", "nameStr");
        addTypeDiff(diff, "ArtWorkImageRecipe", "recipeNameStr");
        addTypeDiff(diff, "DrmKey", "keyId");
        addTypeDiff(diff, "WmDrmKey", "downloadableId");
        addTypeDiff(diff, "DrmInfoData", "packageId");
        addTypeDiff(diff, "DrmSystem", "id");
        addTypeDiff(diff, "L10NResources", "resourceIdStr");
        addTypeDiff(diff, "EncodingProfileGroup", "groupNameStr");
        addTypeDiff(diff, "CharacterImages", "id");
        addTypeDiff(diff, "FileEncodingData", "downloadableId");
        addTypeDiff(diff, "RolloutVideo", "video.value");
        addTypeDiff(diff, "TopNVideoData", "countryId");
        diff.calculateDiffs();

        int port = randomPort();

        HollowDiffUIServer server = new HollowDiffUIServer(port);
        HollowDiffUI ui = server.addDiff("diff", diff, "EXPECTED", "ACTUAL");
        ui.addMatchHint(new PrimaryKey("Artwork", "sourceFileId"));
        ui.addMatchHint(new PrimaryKey("ArtworkDerivative", "recipeDesc", "type.nameStr", "format.nameStr"));
        ui.addMatchHint(new PrimaryKey("Integer", "val"));
        ui.addMatchHint(new PrimaryKey("ArtWorkImageFormatEntry", "nameStr"));
        ui.addMatchHint(new PrimaryKey("VideoImage", "videoId", "videoMoment.msOffset"));
        ui.addMatchHint(new PrimaryKey("StreamData", "downloadableId"));

        server.start();

        if (BROWSER_COMMAND != null) {
            String command = BROWSER_COMMAND + " http://localhost:" + port + "/diff";
            System.out.println("Executing command: " + command);
            Runtime.getRuntime().exec(command);
        }

        server.join();
    }

    private static HollowTypeDiff addTypeDiff(HollowDiff diff, String type, String... keyFields) {
        HollowTypeDiff typeDiff = diff.addTypeDiff(type);

        for(String keyField : keyFields) {
            typeDiff.addMatchPath(keyField);
        }
        
        return typeDiff;
    }

    private static int randomPort() {
        return new Random().nextInt(16383) + 16384;
    }
}
