package com.netflix.vmsserver.videocollectionsdata;

import com.netflix.vms.transformer.hollowinput.RolloutPhaseTrailerHollow;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.CountryVideoDisplaySetHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualTrailerHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TrailerHollow;
import com.netflix.vms.transformer.hollowinput.TrailerThemeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDisplaySetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeMediaHollow;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vmsserver.index.IndexSpec;
import com.netflix.vmsserver.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoCollectionsBuilder {


    private static final Strings POST_PLAY = new Strings("postPlay");
    private static final Strings TYPE = new Strings("type");
    private static final Strings TRAILER = new Strings("trailer");
    private static final Strings SUB_TYPE = new Strings("subType");
    private static final Strings IDENTIFIER = new Strings("identifier");
    private static final Strings THEMES = new Strings("themes");
    private static final Strings USAGES = new Strings("usages");

    private final VMSHollowVideoInputAPI videoAPI;
    private final HollowPrimaryKeyIndex supplementalIndex;
    private final HollowHashIndex videoTypeCountryIndex;
    private final HollowPrimaryKeyIndex videoRightsIndex;
    private final HollowPrimaryKeyIndex rolloutVideoTypeIndex;
    private final Set<Integer> supplementalIds;

    public VideoCollectionsBuilder(VMSHollowVideoInputAPI videoAPI, VMSTransformerIndexer indexer) {
        this.videoAPI = videoAPI;
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.videoTypeCountryIndex = indexer.getHashIndex(IndexSpec.VIDEO_TYPE_COUNTRY);
        this.videoRightsIndex = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);
        this.rolloutVideoTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);

        //indexer.getPrimaryKeyIndex()

        this.supplementalIds = findAllSupplementalVideoIds(videoAPI);
    }

    private Set<Integer> findAllSupplementalVideoIds(VMSHollowVideoInputAPI videoAPI) {
        Set<Integer> ids = new HashSet<Integer>();

        for(IndividualTrailerHollow supplemental : videoAPI.getAllIndividualTrailerHollow())
            ids.add((int)supplemental._getMovieId());

        for(RolloutPhaseTrailerHollow rolloutTrailer : videoAPI.getAllRolloutPhaseTrailerHollow())
            ids.add((int)rolloutTrailer._getTrailerMovieId());



        return ids;
    }

    public Map<String, VideoCollectionsDataHierarchy> buildVideoCollectionsDataByCountry(VideoDisplaySetHollow displaySet) {

        long topNodeId = displaySet._getTopNodeId();
        if(supplementalIds.contains((int)topNodeId))
            return null;

        Map<ShowHierarchy, VideoCollectionsDataHierarchy> uniqueVideoCollectionsHierarchies = new HashMap<ShowHierarchy, VideoCollectionsDataHierarchy>();
        Map<String, VideoCollectionsDataHierarchy> videoCollectionsDataByCountry = new HashMap<String, VideoCollectionsDataHierarchy>();
        VideoCollectionsDataHierarchy standaloneHierarchy = null;


        for(CountryVideoDisplaySetHollow set : displaySet._getSets()) {
            String countryCode = set._getCountryCode()._getValue();

            /*if(topNodeId == 60034584 && "AO".equals(countryCode))
                System.out.println("asdf");*/

            if(!isTopNodeIncluded(topNodeId, countryCode))
                continue;

            VideoCollectionsDataHierarchy hierarchy = null;

            if(set._getSetType()._isValueEqual("std_show")) {
                ShowHierarchy showHierarchy = new ShowHierarchy((int)topNodeId, set, countryCode, this);
                hierarchy = uniqueVideoCollectionsHierarchies.get(showHierarchy);
                if(hierarchy == null) {
                    hierarchy = new VideoCollectionsDataHierarchy((int)topNodeId, false, getSupplementalVideos(topNodeId, topNodeId, Integer.MIN_VALUE, countryCode));

                    for(int i=0;i<showHierarchy.getSeasonIds().length;i++) {
                        int seasonId = showHierarchy.getSeasonIds()[i];
                        hierarchy.addSeason(seasonId, getSupplementalVideos(seasonId, topNodeId, i, countryCode));

                        for(int j=0;j<showHierarchy.getEpisodeIds()[i].length;j++) {
                            int episodeId = showHierarchy.getEpisodeIds()[i][j];
                            hierarchy.addEpisode(episodeId);
                        }
                    }

                    uniqueVideoCollectionsHierarchies.put(showHierarchy, hierarchy);
                }
            } else if(set._getSetType()._isValueEqual("Standalone")){
                ///TODO: Supplementals is country specific!
                if(standaloneHierarchy == null)
                    standaloneHierarchy = new VideoCollectionsDataHierarchy((int)topNodeId, true, getSupplementalVideos(topNodeId, topNodeId, Integer.MIN_VALUE, countryCode));
                hierarchy = standaloneHierarchy;
            }

            if(hierarchy != null)
                videoCollectionsDataByCountry.put(countryCode, hierarchy);
        }

        return videoCollectionsDataByCountry;
    }

    private List<SupplementalVideo> getSupplementalVideos(long videoId, long parentVideoId, int seasonNumber, String countryCode) {
        int supplementalsOrdinal = supplementalIndex.getMatchingOrdinal(videoId);

        if(supplementalsOrdinal == -1)
            return new ArrayList<SupplementalVideo>();

        List<SupplementalVideo> supplementalVideos = new ArrayList<SupplementalVideo>();

        TrailerHollow supplementals = videoAPI.getTrailerHollow(supplementalsOrdinal);
        for(IndividualTrailerHollow supplemental : supplementals._getTrailers()) {
            if(isChildNodeIncluded(supplemental._getMovieId(), countryCode)) {

                SupplementalVideo supp = new SupplementalVideo();
                supp.id = new Video((int) supplemental._getMovieId());
                supp.parent = new Video((int) parentVideoId);
                supp.sequenceNumber = (int)supplemental._getSequenceNumber();
                supp.seasonNumber = seasonNumber;
                supp.attributes = new HashMap<Strings, Strings>();
                supp.multiValueAttributes = new HashMap<Strings, List<Strings>>();
                supp.attributes.put(POST_PLAY, new Strings(supplemental._getPostPlay()._getValue()));
                supp.attributes.put(TYPE, TRAILER);
                supp.attributes.put(SUB_TYPE, new Strings(supplemental._getSubType()._getValue()));
                StringHollow identifier = supplemental._getIdentifier();
                if(identifier != null)
                    supp.attributes.put(IDENTIFIER, new Strings(identifier._getValue()));
                // StringHollow usages = supplemental._  ///TODO: What to do about exotic 'attribute' parsing logic?
                List<Strings> themesList = getThemesList(supplemental);
                if(themesList != null)
                    supp.multiValueAttributes.put(THEMES, themesList);
                supplementalVideos.add(supp);

            }
       }

        return supplementalVideos;
    }

    private List<Strings> getThemesList(IndividualTrailerHollow trailer) {
        List<TrailerThemeHollow> themes = trailer._getThemes();
        if(themes == null || themes.isEmpty())
            return null;

        List<Strings> list = new ArrayList<Strings>();

        for(TrailerThemeHollow theme : themes) {
            list.add(new Strings(theme._getValue()._getValue()));
        }

        return list;
    }

    boolean isTopNodeIncluded(long videoId, String countryCode) {
        if(!isContentApproved(videoId, countryCode))
            return false;

        if(isGoLiveOrHasFirstDisplayDate(videoId, countryCode) || isDVDData(videoId, countryCode) || hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
            return true;

        return false;
    }

    boolean isChildNodeIncluded(long videoId, String countryCode) {
        if(!isContentApproved(videoId, countryCode))
            return false;

        if(isGoLiveOrHasFirstDisplayDate(videoId, countryCode) || hasCurrentOrFutureRollout(videoId, "DISPLAY_PAGE", countryCode))
            return true;

        return false;
    }

    boolean isDVDData(long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);

        int ordinal = queryResult.iterator().next();

        VideoTypeDescriptorHollow countryType = videoAPI.getVideoTypeDescriptorHollow(ordinal);
        if(countryType._getIsCanon() || countryType._getIsExtended())
            return true;

        for(VideoTypeMediaHollow media : countryType._getMedia()) {
            if(media._getValue()._isValueEqual("PLASTIC"))
                return true;
        }

        return false;
    }

    boolean isContentApproved(long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);

        if(queryResult == null || queryResult.numResults() == 0)
            return false;

        int ordinal = queryResult.iterator().next();

        VideoTypeDescriptorHollow countryType = videoAPI.getVideoTypeDescriptorHollow(ordinal);
        return countryType._getIsContentApproved();
    }

    boolean isGoLiveOrHasFirstDisplayDate(long videoId, String countryCode) {
        int rightsOrdinal = videoRightsIndex.getMatchingOrdinal(videoId, countryCode);

        if(rightsOrdinal == -1)
            return false;


        VideoRightsHollow videoRights = videoAPI.getVideoRightsHollow(rightsOrdinal);
        if(videoRights._getFlags()._getGoLive())
            return true;

        if(videoRights._getFlags()._getFirstDisplayDate() != null)
            return true;

        Set<VideoRightsWindowHollow> windowSet = videoRights._getRights()._getWindows();

        if(!windowSet.isEmpty())
            return true;

        videoRights._getRights()._getContracts();

        return false;
    }

    boolean hasCurrentOrFutureRollout(long videoId, String rolloutType, String country) {
        int rolloutOrdinal = rolloutVideoTypeIndex.getMatchingOrdinal(videoId, rolloutType);

        if(rolloutOrdinal == -1)
            return false;

        RolloutHollow rollout = videoAPI.getRolloutHollow(rolloutOrdinal);

        for(RolloutPhaseHollow phase : rollout._getPhases()) {
            for(Map.Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phase._getWindows().entrySet()) {
                if(entry.getKey()._isValueEqual(country)) {
                    return entry.getValue()._getEndDate()._getValue() >= System.currentTimeMillis();
                }
            }
        }

        return false;
    }
}