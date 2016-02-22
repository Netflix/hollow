package com.netflix.vms.transformer.modules.collections;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.IndividualTrailerHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TrailerHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
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

    private static final Strings POST_PLAY = new Strings("postPlay");
    private static final Strings ASPECT_RATIO = new Strings("aspectRatio");
    private static final Strings TYPE = new Strings("type");
    private static final Strings TRAILER = new Strings("trailer");
    private static final Strings SUB_TYPE = new Strings("subType");
    private static final Strings IDENTIFIER = new Strings("identifier");
    private static final Strings THEMES = new Strings("themes");
    private static final Strings USAGES = new Strings("usages");

    private final VMSHollowVideoInputAPI videoAPI;
    private final HollowPrimaryKeyIndex supplementalIndex;

    public VideoCollectionsModule(VMSHollowVideoInputAPI videoAPI, VMSTransformerIndexer indexer) {
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
                supp.attributes.put(POST_PLAY, new Strings(supplemental._getPostPlay()._getValue()));
                supp.attributes.put(TYPE, TRAILER);
                StringHollow aspectRatio = supplemental._getAspectRatio();
                if(aspectRatio != null)
                    supp.attributes.put(ASPECT_RATIO, new Strings(aspectRatio._getValue()));
                supp.attributes.put(SUB_TYPE, new Strings(supplemental._getSubType()._getValue()));
                StringHollow identifier = supplemental._getIdentifier();
                if(identifier != null)
                    supp.attributes.put(IDENTIFIER, new Strings(identifier._getValue()));
                List<Strings> themesList = getListOfStrings(supplemental._getThemes());
                if(themesList != null)
                    supp.multiValueAttributes.put(THEMES, themesList);
                supplementalVideos.add(supp);
                List<Strings> usagesList = getListOfStrings(supplemental._getUsages());
                if(usagesList != null)
                    supp.multiValueAttributes.put(USAGES, usagesList);
            }
       }

        return supplementalVideos;
    }

    private List<Strings> getListOfStrings(List<StringHollow> themes) {
        if(themes == null)
            return null;

        List<Strings> list = new ArrayList<Strings>();

        for(StringHollow theme : themes) {
            list.add(new Strings(theme._getValue()));
        }

        return list;
    }

}