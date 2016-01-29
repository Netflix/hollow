package com.netflix.vmsserver;

import com.netflix.hollow.diff.HollowDiff;
import com.netflix.hollow.diff.HollowTypeDiff;
import com.netflix.hollow.diff.ui.jetty.HollowDiffUIServer;
import com.netflix.hollow.filter.HollowFilterConfig;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

public class TestDiff {

    @Test
    public void testDiff() throws Exception {
        HollowFilterConfig filter = new HollowFilterConfig();
        filter.addField("CompleteVideo", "id");
        filter.addField("CompleteVideo", "country");
        filter.addField("CompleteVideo", "facetData");
        filter.addType("Video");
        filter.addType("ISOCountry");
        filter.addField("CompleteVideoFacetData", "videoCollectionsData");
        filter.addField("VideoCollectionsData", "videoEpisodes");
        filter.addField("VideoCollectionsData", "nodeType");
        filter.addField("VideoCollectionsData", "topNodeType");
        filter.addField("VideoCollectionsData", "showChildren");
        filter.addField("VideoCollectionsData", "seasonChildren");
        filter.addField("VideoCollectionsData", "supplementalVideoParents");
        filter.addField("VideoCollectionsData", "supplementalVideos");
        filter.addField("VideoCollectionsData", "showParent");
        filter.addField("VideoCollectionsData", "seasonParent");
        filter.addField("VideoCollectionsData", "topNode");
        filter.addType("VideoNodeType");
        filter.addType("VideoEpisode");
        filter.addType("ListOfVideo");
        filter.addType("ListOfVideoEpisode");
        filter.addType("ListOfSupplementalVideo");
        filter.addType("SupplementalVideo");
        //filter.addType("SortedMapOfIntegerToListOfVideoEpisode");
        //filter.addType("SortedMapOfIntegerToListOfVideoEpisode_map");  ///TODO: HollowObjectMapper turns this into "MapOfIntegerToListOfVideoEpisode"
        filter.addType("MapOfStringsToStrings");
        filter.addType("Strings");
        filter.addType("MapOfStringsToListOfStrings");
        filter.addType("ListOfStrings");
        filter.addType("Integer");

        HollowReadStateEngine from = loadStateEngine("/space/simplified-server-diff/iceland-snapshot.hollow", filter);
        HollowReadStateEngine to = loadStateEngine("/space/simplified-server-diff/VideoCollectionsData.hollow", filter);


        HollowDiff diff = new HollowDiff(from, to);
        HollowTypeDiff typeDiff = diff.addTypeDiff("CompleteVideo");
        typeDiff.addMatchPath("id.value");
        typeDiff.addMatchPath("country.id");

        diff.calculateDiffs();

        HollowDiffUIServer server = new HollowDiffUIServer(7777);
        server.addDiff("diff", diff);

        server.start();
        server.join();
    }

    private HollowReadStateEngine loadStateEngine(String snapshotFilename, HollowFilterConfig filter) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();

        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(new BufferedInputStream(new FileInputStream(snapshotFilename)), filter);

        return stateEngine;

    }


}
