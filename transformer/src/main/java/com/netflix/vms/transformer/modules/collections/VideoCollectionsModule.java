package com.netflix.vms.transformer.modules.collections;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.IndividualTrailerHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.PassthroughDataHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TrailerHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCollectionsModule {

    private final Strings TYPE = new Strings("type");
    private final Strings TRAILER = new Strings("trailer");
    private final Strings IDENTIFIER = new Strings("identifier");

    private final VMSHollowInputAPI videoAPI;
    private final HollowPrimaryKeyIndex supplementalIndex;

    public VideoCollectionsModule(VMSHollowInputAPI videoAPI, VMSTransformerIndexer indexer) {
        this.videoAPI = videoAPI;
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
    }

    public Map<String, VideoCollectionsDataHierarchy> buildVideoCollectionsDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {

        Map<ShowHierarchy, VideoCollectionsDataHierarchy> uniqueHierarchies = new HashMap<ShowHierarchy, VideoCollectionsDataHierarchy>();

        Map<String, VideoCollectionsDataHierarchy> countryHierarchies = new HashMap<String, VideoCollectionsDataHierarchy>();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            ShowHierarchy showHierarchy = entry.getValue();
            int topNodeId = showHierarchy.getTopNodeId();

            VideoCollectionsDataHierarchy alreadyBuiltHierarchy = uniqueHierarchies.get(showHierarchy);
            if(alreadyBuiltHierarchy != null) {
                countryHierarchies.put(entry.getKey(), alreadyBuiltHierarchy);
                continue;
            }

            VideoCollectionsDataHierarchy hierarchy = new VideoCollectionsDataHierarchy(topNodeId, showHierarchy.isStandalone(), getSupplementalVideos(showHierarchy, topNodeId));
            for(int i=0;i<showHierarchy.getSeasonIds().length;i++) {
                int seasonId = showHierarchy.getSeasonIds()[i];
                int seasonSequenceNumber = showHierarchy.getSeasonSequenceNumbers()[i];
                hierarchy.addSeason(seasonId, seasonSequenceNumber, getSupplementalVideos(showHierarchy, seasonId));

                for(int j=0;j<showHierarchy.getEpisodeIds()[i].length;j++) {
                    int episodeId = showHierarchy.getEpisodeIds()[i][j];
                    int episodeSequenceNumber = showHierarchy.getEpisodeSequenceNumbers()[i][j];
                    hierarchy.addEpisode(episodeId, episodeSequenceNumber);
                }
            }

            countryHierarchies.put(countryCode, hierarchy);
            uniqueHierarchies.put(showHierarchy, hierarchy);
        }

        return countryHierarchies;
    }

    private List<SupplementalVideo> getSupplementalVideos(ShowHierarchy hierarchy, long videoId) {
        int supplementalsOrdinal = supplementalIndex.getMatchingOrdinal(videoId);

        if(supplementalsOrdinal == -1)
            return new ArrayList<SupplementalVideo>();

        List<SupplementalVideo> supplementalVideos = new ArrayList<SupplementalVideo>();

        TrailerHollow supplementals = videoAPI.getTrailerHollow(supplementalsOrdinal);
        for(IndividualTrailerHollow supplemental : supplementals._getTrailers()) {
            int supplementalId = (int)supplemental._getMovieId();
            if(hierarchy.includesSupplementalId(supplementalId)) {

                SupplementalVideo supp = new SupplementalVideo();
                supp.id = new Video(supplementalId);
                supp.parent = new Video((int) videoId);
                supp.sequenceNumber = (int)supplemental._getSequenceNumber();
                //supp.seasonNumber = seasonNumber;
                supp.attributes = new HashMap<Strings, Strings>();
                supp.multiValueAttributes = new HashMap<Strings, List<Strings>>();
                
                PassthroughDataHollow passthrough = supplemental._getPassthrough();
                
                for(Map.Entry<MapKeyHollow, ListOfStringHollow> entry : passthrough._getMultiValues().entrySet()) {
                    List<Strings> valueList = new ArrayList<Strings>();
                    for(StringHollow str : entry.getValue()) {
                        valueList.add(new Strings(str._getValue()));
                    }
                    
                    supp.multiValueAttributes.put(new Strings(entry.getKey()._getValue()), valueList);
                }
                
                for(Map.Entry<MapKeyHollow, StringHollow> entry : passthrough._getSingleValues().entrySet()) {
                    supp.attributes.put(new Strings(entry.getKey()._getValue()), new Strings(entry.getValue()._getValue()));
                }
                
                supp.attributes.put(TYPE, TRAILER);
                
                ////TODO: This should just be a passthrough.
                if(supplemental._getIdentifier() != null) {
                    supp.attributes.put(IDENTIFIER, new Strings(supplemental._getIdentifier()._getValue()));
                }

                supplementalVideos.add(supp);
            }
       }

        return supplementalVideos;
    }

}