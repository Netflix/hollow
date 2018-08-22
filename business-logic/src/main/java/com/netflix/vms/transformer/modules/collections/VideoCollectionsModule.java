package com.netflix.vms.transformer.modules.collections;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.data.VideoDataCollection;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.rollout.RolloutVideoModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoCollectionsModule {

    private final Strings TYPE = new Strings("type");
    private final Strings TRAILER = new Strings("trailer");

    public static final String POST_PLAY_ATTR = "postPlay";
    public static final String GENERAL_ATTR = "general";
    public static final String THEMATIC_ATTR = "thematic";
    public static final String SUB_TYPE_ATTR = "subType";

    public static final String IDENTIFIERS_ATTR = "identifiers";
    public static final String THEMES_ATTR = "themes";
    public static final String USAGES_ATTR = "usages";

    private final VMSHollowInputAPI videoAPI;
    private final TransformerContext ctx;
    private final HollowPrimaryKeyIndex supplementalIndex;
    private final CycleConstants cycleConstants;

    public VideoCollectionsModule(VMSHollowInputAPI videoAPI, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        this.videoAPI = videoAPI;
        this.ctx = ctx;
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.cycleConstants = constants;
    }

    public void buildVideoCollectionsDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, TransformedVideoData transformedVideoData) {

        Map<VideoHierarchy, VideoCollectionsDataHierarchy> uniqueHierarchies = new HashMap<>();
        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {

            String countryCode = entry.getKey();
            VideoDataCollection videoDataCollection = transformedVideoData.getVideoDataCollection(countryCode);
            Set<VideoCollectionsDataHierarchy> vcdHierarchies = new HashSet<>();

            for (VideoHierarchy showHierarchy : entry.getValue()) {
                int topNodeId = showHierarchy.getTopNodeId();

                VideoCollectionsDataHierarchy alreadyBuiltHierarchy = uniqueHierarchies.get(showHierarchy);
                if (alreadyBuiltHierarchy != null) {
                    vcdHierarchies.add(alreadyBuiltHierarchy);
                    continue;
                }

                VideoCollectionsDataHierarchy hierarchy = new VideoCollectionsDataHierarchy(ctx, topNodeId, showHierarchy.isStandalone(), getSupplementalVideos(showHierarchy, topNodeId), cycleConstants);
                for (int i = 0; i < showHierarchy.getSeasonIds().length; i++) {
                    int seasonId = showHierarchy.getSeasonIds()[i];
                    int seasonSequenceNumber = showHierarchy.getSeasonSequenceNumbers()[i];
                    hierarchy.addSeason(seasonId, seasonSequenceNumber, getSupplementalVideos(showHierarchy, seasonId));

                    for (int j = 0; j < showHierarchy.getEpisodeIds()[i].length; j++) {
                        int episodeId = showHierarchy.getEpisodeIds()[i][j];
                        int episodeSequenceNumber = showHierarchy.getEpisodeSequenceNumbers()[i][j];
                        hierarchy.addEpisode(episodeId, episodeSequenceNumber, getSupplementalVideos(showHierarchy, episodeId));
                    }
                }

                vcdHierarchies.add(hierarchy);
                uniqueHierarchies.put(showHierarchy, hierarchy);
            }

            videoDataCollection.setVideoCollectionsDataHierarchy(vcdHierarchies);
        }
    }

    private List<SupplementalVideo> getSupplementalVideos(VideoHierarchy hierarchy, long videoId) {
        int supplementalsOrdinal = supplementalIndex.getMatchingOrdinal(videoId);

        if (supplementalsOrdinal == -1)
            return new ArrayList<SupplementalVideo>();

        Map<Integer, Integer> supplementalSeasonSeqNumMap = hierarchy.getSupplementalSeasonSeqNumMap();
        List<SupplementalVideo> supplementalVideos = new ArrayList<SupplementalVideo>();

        SupplementalsHollow supplementals = videoAPI.getSupplementalsHollow(supplementalsOrdinal);
        for (IndividualSupplementalHollow supplemental : supplementals._getSupplementals()) {
            int supplementalId = (int) supplemental._getMovieId();
            if (hierarchy.includesSupplementalId(supplementalId)) {

                SupplementalVideo supp = new SupplementalVideo();
                supp.id = new Video(supplementalId);
                supp.parent = new Video((int) videoId);
                supp.sequenceNumber = (int) supplemental._getSequenceNumber();
                if (supplementalSeasonSeqNumMap.containsKey(supplementalId)) {
                    supp.seasonNumber = supplementalSeasonSeqNumMap.get(supplementalId);
                }


                supp.attributes = new HashMap<>();
                // Supplemental pass-through does not exists anymore, each field in the schema needs to be added.
                supp.attributes.put(new Strings(POST_PLAY_ATTR), new Strings(String.valueOf(supplemental._getPostplay())));
                supp.attributes.put(new Strings(GENERAL_ATTR), new Strings(String.valueOf(supplemental._getGeneral())));
                supp.attributes.put(new Strings(THEMATIC_ATTR), new Strings(String.valueOf(supplemental._getThematic())));
                if (supplemental._getSubType() != null && supplemental._getSubType()._getValue() != null) {
                    supp.attributes.put(new Strings(SUB_TYPE_ATTR), new Strings(supplemental._getSubType()._getValue()));
                }
                supp.attributes.put(TYPE, TRAILER);

                if (RolloutVideoModule.ADD_ASPECT_RATIO.get()) {
                    supp.attributes.put(new Strings("aspectRation"), new Strings(""));
                }

                // There are only two multi-values attributes in input.
                supp.multiValueAttributes = new HashMap<>();
                // process themes
                List<Strings> themes = new ArrayList<>();
                if (supplemental._getThemes() != null) {
                    Iterator<StringHollow> it = supplemental._getThemes().iterator();
                    while (it.hasNext()) {
                        themes.add(new Strings(it.next()._getValue()));
                    }
                }
                supp.multiValueAttributes.put(new Strings(THEMES_ATTR), themes);

                // process identifiers
                List<Strings> identifiers = new ArrayList<>();
                if (supplemental._getIdentifiers() != null) {
                    Iterator<StringHollow> it = supplemental._getIdentifiers().iterator();
                    while (it.hasNext()) {
                        identifiers.add(new Strings(it.next()._getValue()));
                    }
                }
                supp.multiValueAttributes.put(new Strings(IDENTIFIERS_ATTR), identifiers);

                // process usages
                List<Strings> usages = new ArrayList<>();
                if (supplemental._getUsages() != null) {
                    Iterator<StringHollow> it = supplemental._getUsages().iterator();
                    while (it.hasNext()) {
                        usages.add(new Strings(it.next()._getValue()));
                    }
                }
                supp.multiValueAttributes.put(new Strings(USAGES_ATTR), usages);

                supplementalVideos.add(supp);
            }
        }

        Collections.sort(supplementalVideos, SUPPLEMENTAL_VIDEO_COMPARATOR);

        return supplementalVideos;
    }

    private static final Comparator<SupplementalVideo> SUPPLEMENTAL_VIDEO_COMPARATOR = (o1, o2) -> {
        int x = o1.sequenceNumber;
        int y = o2.sequenceNumber;
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    };
}