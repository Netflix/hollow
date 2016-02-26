package com.netflix.vmsserver;

import com.netflix.hollow.diff.HollowDiff;
import com.netflix.hollow.diff.HollowTypeDiff;
import com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import java.util.Random;

public class ShowMeTheProgressDiffTool {

    private static final String BROWSER_COMMAND = "google-chrome";

    public static void startTheDiff(HollowReadStateEngine expected, HollowReadStateEngine actual) throws Exception {
        HollowDiff diff = new HollowDiff(expected, actual);
        addTypeDiff(diff, "CompleteVideo", "id.value", "country.id");
        addTypeDiff(diff, "DrmSystem", "id");
        addTypeDiff(diff, "ArtWorkImageFormatEntry", "nameStr");
        addTypeDiff(diff, "DeploymentIntent", "profileId", "bitrate", "country.id");

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
