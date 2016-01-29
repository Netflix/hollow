package com.netflix.vmsserver;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.hollowoutput.pojos.CompleteVideo;
import com.netflix.vms.hollowoutput.pojos.CompleteVideoFacetData;
import com.netflix.vms.hollowoutput.pojos.ISOCountry;
import com.netflix.vms.hollowoutput.pojos.Video;
import com.netflix.vms.hollowoutput.pojos.VideoCollectionsData;
import com.netflix.vms.videos.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.videos.hollowinput.VideoDisplaySetHollow;
import com.netflix.vmsserver.videocollectionsdata.VideoCollectionsBuilder;
import com.netflix.vmsserver.videocollectionsdata.VideoCollectionsDataHierarchy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SimpleServerPrototype {

    @Test
    public void test() throws Exception {
        HollowReadStateEngine videosStateEngine = loadStateEngine("/space/hollowinput/VMSInputVideosData.hollow");
        VMSHollowVideoInputAPI videosAPI = new VMSHollowVideoInputAPI(videosStateEngine);

        final VideoCollectionsBuilder collectionsBuilder = new VideoCollectionsBuilder(videosAPI);

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();  //TODO: Need to define a HashCodeFinder.
        final HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);

        SimultaneousExecutor executor = new SimultaneousExecutor();
        
        long startTime = System.currentTimeMillis();
        
        for(final VideoDisplaySetHollow displaySet : videosAPI.getAllVideoDisplaySetHollow()) {
            executor.execute(new Runnable() {
                public void run() {
                    Map<String, VideoCollectionsDataHierarchy> vcdByCountry = collectionsBuilder.buildVideoCollectionsDataByCountry(displaySet);

                    if(vcdByCountry != null)
                        writeJustTheVideoCollectionsDatas(vcdByCountry, objectMapper);
                }
            });
        }
        
        executor.awaitSuccessfulCompletion();

        long endTime = System.currentTimeMillis();
        System.out.println("Processed all videos in " + (endTime - startTime) + "ms");

        HollowBlobWriter writer = new HollowBlobWriter(writeStateEngine);
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream("/space/simplified-server-diff/VideoCollectionsData.hollow"));
        writer.writeSnapshot(os);
        os.close();
    }


    private HollowReadStateEngine loadStateEngine(String snapshotFilename) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();

        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(new BufferedInputStream(new FileInputStream(snapshotFilename)));

        return stateEngine;
    }


    private void writeJustTheVideoCollectionsDatas(Map<String, VideoCollectionsDataHierarchy> vcdByCountry, HollowObjectMapper objectMapper) {
        for(Map.Entry<String, VideoCollectionsDataHierarchy> countryHierarchyEntry : vcdByCountry.entrySet()) {
            ISOCountry country = getCountry(countryHierarchyEntry.getKey());
            VideoCollectionsDataHierarchy hierarchy = countryHierarchyEntry.getValue();

            CompleteVideo topNode = new CompleteVideo();
            topNode.country = country;
            topNode.facetData = new CompleteVideoFacetData();
            topNode.facetData.videoCollectionsData = hierarchy.getTopNode();
            topNode.id = topNode.facetData.videoCollectionsData.topNode;

            objectMapper.addObject(topNode);

            if(topNode.facetData.videoCollectionsData.nodeType == VideoCollectionsDataHierarchy.SHOW) {
                int sequenceNumber = 0;

                for(Map.Entry<Integer, VideoCollectionsData> showEntry : hierarchy.getOrderedSeasons().entrySet()) {
                    CompleteVideo showNode = new CompleteVideo();
                    showNode.country = country;
                    showNode.id = new Video(showEntry.getKey().intValue());
                    showNode.facetData = new CompleteVideoFacetData();
                    showNode.facetData.videoCollectionsData = showEntry.getValue();

                    objectMapper.addObject(showNode);

                    for(Map.Entry<Integer, VideoCollectionsData> episodeEntry : hierarchy.getOrderedSeasonEpisodes(++sequenceNumber).entrySet()) {
                        CompleteVideo episodeNode = new CompleteVideo();
                        episodeNode.country = country;
                        episodeNode.id = new Video(episodeEntry.getKey().intValue());
                        episodeNode.facetData = new CompleteVideoFacetData();
                        episodeNode.facetData.videoCollectionsData = episodeEntry.getValue();

                        objectMapper.addObject(episodeNode);
                    }
                }
            }
        }
    }

    private Map<String, ISOCountry> countries = new HashMap<String, ISOCountry>();
    private ISOCountry getCountry(String id) {
        ISOCountry country = countries.get(id);
        if(country == null) {
            country = new ISOCountry(id);
            countries.put(id, country);
        }
        return country;
    }

}
