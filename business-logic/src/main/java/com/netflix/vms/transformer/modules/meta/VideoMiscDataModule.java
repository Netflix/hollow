package com.netflix.vms.transformer.modules.meta;

import static com.netflix.vms.transformer.index.IndexSpec.CSM_REVIEW;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_AWARD;
import static com.netflix.vms.transformer.index.IndexSpec.VMS_AWARD;

import com.netflix.config.FastProperty;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.hollowinput.CSMReviewHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSAwardHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoAwardHollow;
import com.netflix.vms.transformer.hollowinput.VideoAwardListHollow;
import com.netflix.vms.transformer.hollowinput.VideoAwardMappingHollow;
import com.netflix.vms.transformer.hollowoutput.ICSMReview;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoAward;
import com.netflix.vms.transformer.hollowoutput.VideoAwardFestival;
import com.netflix.vms.transformer.hollowoutput.VideoAwardType;
import com.netflix.vms.transformer.hollowoutput.VideoMiscData;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.OutputUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoMiscDataModule {
    private final VMSHollowInputAPI api;
    Map<Integer, VideoMiscData> videoMiscMap = new HashMap<>();
    private final HollowPrimaryKeyIndex videoAwardIdx;
    private final HollowPrimaryKeyIndex csmReviewIdx;
    private final HollowPrimaryKeyIndex awardIdx;
    private static FastProperty.BooleanProperty DISABLE_VIDEO_AWARDS = new FastProperty.BooleanProperty("transformer.videoMisc.disable.awardAttributes", true);

    public VideoMiscDataModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoAwardIdx = indexer.getPrimaryKeyIndex(VIDEO_AWARD);
        this.awardIdx = indexer.getPrimaryKeyIndex(VMS_AWARD);
        this.csmReviewIdx = indexer.getPrimaryKeyIndex(CSM_REVIEW);
    }

    public Map<Integer, VideoMiscData> buildVideoMiscDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, TransformedVideoData transformedVideoData) {
        videoMiscMap.clear();

        for(Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            for(VideoHierarchy showHierarchy : entry.getValue()) {
                addVideoMiscData(showHierarchy.getTopNodeId(), transformedVideoData);
                int[][] seasonEpisodesIds = showHierarchy.getEpisodeIds();
                for(int seasonIdx = 0; seasonIdx < showHierarchy.getSeasonIds().length; seasonIdx++) {
                    addVideoMiscData(showHierarchy.getSeasonIds()[seasonIdx], transformedVideoData);
    
                    for(int episodeIdx = 0; episodeIdx < seasonEpisodesIds[seasonIdx].length; episodeIdx++) {
                        addVideoMiscData(seasonEpisodesIds[seasonIdx][episodeIdx], transformedVideoData);
                    }
                }
    
                for(int i=0;i<showHierarchy.getSupplementalIds().length;i++) {
                    addVideoMiscData(showHierarchy.getSupplementalIds()[i], transformedVideoData);
                }
            }
        }

        return videoMiscMap;
    }

    private void addVideoMiscData(int videoId, TransformedVideoData transformedVideoData) {
        VideoMiscData miscData = videoMiscMap.get(videoId);
        if(miscData == null) {
            miscData = createMiscData(videoId);
            videoMiscMap.put(videoId, miscData);
            transformedVideoData.getVideoData(videoId).setVideoMiscData(miscData);
        }
    }

    private VideoMiscData createMiscData(int videoId) {
        VideoMiscData miscData = new VideoMiscData();
        miscData.videoAwards = getVideoAwards(videoId);
        miscData.cSMReview = getCSMReview(videoId);
        return miscData;
    }

    private ICSMReview getCSMReview(int videoId) {
        int csmReviewOrdinal = csmReviewIdx.getMatchingOrdinal((long)videoId);
        if(csmReviewOrdinal != -1) {
            CSMReviewHollow csmReviewHollow = api.getCSMReviewHollow(csmReviewOrdinal);
            ICSMReview csmReview = new ICSMReview();
            csmReview.ageExplanation = getStrings(csmReviewHollow._getAgeExplanation());
            csmReview.ageRecommendation = (int)csmReviewHollow._getAgeRecommendation();
            csmReview.castMemberNames = getStrings(csmReviewHollow._getCastMemberNames());
            csmReview.consumerism = getStrings(csmReviewHollow._getConsumerism());
            csmReview.consumerismAlert = (int)csmReviewHollow._getConsumerismAlert();
            csmReview.dat = getStrings(csmReviewHollow._getDat());
            csmReview.datAlert = (int)csmReviewHollow._getDatAlert();
            csmReview.directorNames = getStrings(csmReviewHollow._getDirectorNames());
            csmReview.genre = getStrings(csmReviewHollow._getGenre());
            csmReview.greenBeginsAge = (int)csmReviewHollow._getGreenBeginsAge();
            csmReview.isItAnyGood = getStrings(csmReviewHollow._getIsItAnyGood());
            csmReview.languageAlert = (int)csmReviewHollow._getLanguageAlert();
            csmReview.languageNote = getStrings(csmReviewHollow._getLanguageNote());
            csmReview.link = getStrings(csmReviewHollow._getLink());
            csmReview.mediaType = getStrings(csmReviewHollow._getMediaType());
            csmReview.messageAlert = (int)csmReviewHollow._getMessageAlert();
            csmReview.movieID = (int)csmReviewHollow._getVideoId();
            csmReview.mPAAExplanation = getStrings(csmReviewHollow._getMpaaExplanation());
            csmReview.mPAARating = getStrings(csmReviewHollow._getMpaaRating());
            csmReview.oneLiner = getStrings(csmReviewHollow._getOneLiner());
            csmReview.otherChoices = getStrings(csmReviewHollow._getOtherChoices());
            csmReview.parentsNeedToKnow = getStrings(csmReviewHollow._getParentsNeedToKnow());
            csmReview.plasticReleaseDate = OutputUtil.getRoundedDate(csmReviewHollow._getPlasticReleaseDate());
            csmReview.redEndsAge = (int)csmReviewHollow._getRedEndsAge();
            csmReview.releaseDate = OutputUtil.getRoundedDate(csmReviewHollow._getReleaseDate());
            csmReview.reviewerName = getStrings(csmReviewHollow._getReviewerName());
            csmReview.runtimeInMins = (int)csmReviewHollow._getRuntimeInMins();
            csmReview.sexualContent = getStrings(csmReviewHollow._getSexualContent());
            csmReview.sexualContentAlert = (int)csmReviewHollow._getSexualContentAlert();
            csmReview.socialBehavior = getStrings(csmReviewHollow._getSocialBehavior());
            csmReview.socialBehaviorAlert = (int)csmReviewHollow._getSocialBehaviorAlert();
            csmReview.stars = (int)csmReviewHollow._getStars();
            csmReview.studio = getStrings(csmReviewHollow._getStudio());
            csmReview.title = getStrings(csmReviewHollow._getTitle());
            csmReview.violenceAlert = (int)csmReviewHollow._getViolenceAlert();
            csmReview.violenceNote = getStrings(csmReviewHollow._getViolenceNote());
            csmReview.whatsTheStory = getStrings(csmReviewHollow._getWhatsTheStory());
            return csmReview;
        }

        return null;
    }

    private Strings getStrings(StringHollow stringHollow) {
        return (stringHollow != null && stringHollow._getValue() != null) ? new Strings(stringHollow._getValue().toCharArray()) : null;
    }

    private List<VideoAward> getVideoAwards(int videoId){
        // If disabled, return empty
        if(DISABLE_VIDEO_AWARDS.getValue()){
            return new ArrayList<>();
        }
        return getAwards(videoId);
    }

    private List<VideoAward> getAwards(int videoId) {
        List<VideoAward> videoAwards = new ArrayList<>();
        int videoAwardsOrdinal = videoAwardIdx.getMatchingOrdinal((long)videoId);
        VideoAwardHollow awardInput = null;
        if(videoAwardsOrdinal != -1) {
            awardInput = api.getVideoAwardHollow(videoAwardsOrdinal);
            VideoAwardListHollow awardInfoList = awardInput._getAward();
            if(awardInfoList != null && awardInfoList.size() > 0) {
                Iterator<VideoAwardMappingHollow> iterator = awardInfoList.iterator();
                while(iterator.hasNext()) {
                    VideoAwardMappingHollow awardInfo = iterator.next();
                    VMSAwardHollow vmsAwardInput = null;
                    int awardsOrdinal = awardIdx.getMatchingOrdinal(awardInfo._getAwardId());
                    if(awardsOrdinal != -1) {
                        vmsAwardInput = api.getVMSAwardHollow(awardsOrdinal);
                        VideoAward awardOutput = new VideoAward();
                        awardOutput.awardType = new VideoAwardType();
                        awardOutput.awardType.id = (int)awardInfo._getAwardId();
                        awardOutput.awardType.festival = new VideoAwardFestival();
                        awardOutput.awardType.festival.id = (int)vmsAwardInput._getFestivalId();
                        awardOutput.isWinner = awardInfo._getWinner();
                        awardOutput.sequenceNumber = (int)awardInfo._getSequenceNumber();
                        if((int)awardInfo._getYear() > 0) {
                            awardOutput.year = (int)awardInfo._getYear();
                        }
                        awardOutput.video = new Video(videoId);
                        if((int)awardInfo._getPersonId() > 0) {
                            awardOutput.person = new VPerson((int)awardInfo._getPersonId());
                        }
                        videoAwards.add(awardOutput);
                    }
                }
            }
        }
        
        Collections.sort(videoAwards, VIDEO_AWARD_COMPARATOR);
        
        return videoAwards;
    }
    
    private static final Comparator<VideoAward> VIDEO_AWARD_COMPARATOR = new Comparator<VideoAward>() {
        public int compare(VideoAward o1, VideoAward o2) {
            return Integer.compare(o1.sequenceNumber, o2.sequenceNumber);
        }
    };

}
