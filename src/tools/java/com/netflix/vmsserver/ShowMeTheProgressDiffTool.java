package com.netflix.vmsserver;

import com.netflix.hollow.diff.HollowDiff;
import com.netflix.hollow.diff.HollowTypeDiff;
import com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import java.util.Random;

public class ShowMeTheProgressDiffTool {

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean isMac = OS != null && (OS.indexOf("mac") >= 0);
    private static final String BROWSER_COMMAND = isMac ? "open" : "google-chrome";

    public static void startTheDiff(HollowReadStateEngine expected, HollowReadStateEngine actual) throws Exception {
        HollowDiff diff = new HollowDiff(expected, actual);
        addTypeDiff(diff, "CompleteVideo", "id.value", "country.id");
        //addTypeDiff(diff, "PackageData", "id");
        //addTypeDiff(diff, "DrmSystem", "id");
        addTypeDiff(diff, "OriginServer", "name");
        //addTypeDiff(diff, "EncodingProfile", "id");

        addTypeDiff(diff, "ArtWorkImageFormatEntry", "nameStr");
        addTypeDiff(diff, "DeploymentIntent", "profileId", "bitrate", "country.id");
        addTypeDiff(diff, "TopNVideoData", "countryId");
        addTypeDiff(diff, "RolloutCharacter", "id");
        addTypeDiff(diff, "RolloutVideo", "video.value");
        //addTypeDiff(diff, "DrmKey", "keyId");
        //addTypeDiff(diff, "WmDrmKey", "downloadableId");
        //addTypeDiff(diff, "DrmInfoData", "packageId");
        //addTypeDiff(diff, "EncodingProfileGroup", "groupNameStr"); // TODO: zero-diff
        addTypeDiff(diff, "GlobalPerson", "id");
        addTypeDiff(diff, "PersonImages", "id");
        addTypeDiff(diff, "CharacterImages", "id");
        //addTypeDiff(diff, "FileEncodingData", "downloadableId");

        addTypeDiff(diff, "VideoEpisode_CountryList", "country.id", "item.deliverableVideo.value");

        diff.calculateDiffs();

        int port = randomPort();

        HollowDiffUIServer server = new HollowDiffUIServer(port);
        server.addDiff("diff", diff, "EXPECTED", "ACTUAL");

        server.start();

        if(BROWSER_COMMAND != null)
            Runtime.getRuntime().exec(BROWSER_COMMAND + " http://localhost:" + port + "/diff");

        server.join();
    }

    private static void addTypeDiff(HollowDiff diff, String type, String... keyFields) {
        HollowTypeDiff typeDiff = diff.addTypeDiff(type);

        for(String keyField : keyFields) {
            typeDiff.addMatchPath(keyField);
        }
    }

    private static int randomPort() {
        return new Random().nextInt(16383) + 16384;
    }
}
