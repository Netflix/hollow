package com.netflix.vms.transformer.modules.meta;

import static com.netflix.vms.transformer.index.IndexSpec.PERSONS_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashMap;
import java.util.Map;

public class VideoMetaDataModule {

    private final VMSHollowVideoInputAPI api;

    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowPrimaryKeyIndex videoTypeIdx;
    private final HollowHashIndex videoPersonIdx;

    private final Map<Integer, VideoMetaData> uniqueMap = new HashMap<Integer, VideoMetaData>();


    public VideoMetaDataModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.videoTypeIdx = indexer.getPrimaryKeyIndex(VIDEO_TYPE);
        this.videoPersonIdx = indexer.getHashIndex(PERSONS_BY_VIDEO_ID);
    }

    public Map<String, Map<Integer, VideoMetaData>> buildVideoMetaDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Map<String, Map<Integer, VideoMetaData>> allVideoMetaDataMap = new HashMap<String, Map<Integer,VideoMetaData>>();

        Map<Integer, VideoMetaData> uniqueMap = new HashMap<Integer, VideoMetaData>();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            Map<Integer, VideoMetaData> countryMap = new HashMap<Integer, VideoMetaData>();
            allVideoMetaDataMap.put(entry.getKey(), countryMap);

            ShowHierarchy hierarchy = entry.getValue();

            convert(hierarchy.getTopNodeId(), countryMap);

            for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                convert(hierarchy.getSeasonIds()[i], countryMap);

                for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                    convert(hierarchy.getEpisodeIds()[i][j], countryMap);
                }
            }
        }

        return allVideoMetaDataMap;
    }

    private void convert(Integer id, Map<Integer, VideoMetaData> countryMap) {
        VideoMetaData vmd = uniqueMap.get(id);

        if(vmd == null) {
            vmd = new VideoMetaData();



        }

        countryMap.put(id, vmd);
    }

}
