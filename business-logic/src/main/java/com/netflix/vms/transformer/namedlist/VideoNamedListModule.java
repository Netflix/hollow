package com.netflix.vms.transformer.namedlist;

import java.util.GregorianCalendar;

import java.util.Calendar;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import java.util.Map;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.hollow.bitsandbytes.ThreadSafeBitSet;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.VideoNodeType;
import java.util.concurrent.ConcurrentHashMap;

public class VideoNamedListModule {

    private static final long MS_IN_DAY = 1000 * 60 * 60 * 24;

    private final TransformerContext ctx;
    private final CycleConstants constants;

    private final VideoOrdinalTracker videoOrdinalTracker;
    private final ConcurrentHashMap<String, ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet>> videoListsByCountryAndName;

    private final ConcurrentHashMap<String, ThreadSafeBitSet> episodeListByCountry;

    private final ThreadLocal<VideoNamedListPopulator> populatorRef;

    public VideoNamedListModule(TransformerContext ctx, CycleConstants constants, HollowObjectMapper objectMapper) {
        this.ctx = ctx;
        this.constants = constants;
        this.videoListsByCountryAndName = new ConcurrentHashMap<>();
        this.episodeListByCountry = new ConcurrentHashMap<>();
        this.videoOrdinalTracker = new VideoOrdinalTracker(objectMapper);
        this.populatorRef = new ThreadLocal<>();
    }

    public VideoNamedListPopulator getPopulator() {
        VideoNamedListPopulator populator = populatorRef.get();
        if(populator == null) {
            populator = new VideoNamedListPopulator();
            populatorRef.set(populator);
        }
        return populator;
    }

    public VideoOrdinalTracker getVideoOrdinalTracker() {
        return videoOrdinalTracker;
    }

    public Map<String, ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet>> getVideoListsByCountryAndName() {
        return videoListsByCountryAndName;
    }

    public ThreadSafeBitSet getEpisodeListForCountry(String country) {
        return episodeListByCountry.get(country);
    }

    public class VideoNamedListPopulator {

    	private String country;
        private ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> listsByName = null;
        private ThreadSafeBitSet episodeList;
        private CompleteVideo video;
        private int topNodeVideoIdOrdinal;
        private int videoIdOrdinal;
        private boolean isAvailableForED;
        private boolean isAvailableIn3D;
        private boolean isSupplemental;
        private long currentAvailabilityDate;
        private final Calendar calendar = new GregorianCalendar();
        private final int currentYear;

        private VideoNamedListPopulator() { 
        	calendar.setTimeInMillis(ctx.getNowMillis());
        	currentYear = calendar.get(Calendar.YEAR);
        }

        public void setCountry(String country) {
        	this.country = country;
            listsByName = videoListsByCountryAndName.get(country);
            if(listsByName == null) {
                listsByName = new ConcurrentHashMap<>();
                ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> existingListMap = videoListsByCountryAndName.putIfAbsent(country, listsByName);
                if(existingListMap != null) {
                    listsByName = existingListMap;
                } else {
                    addEmptyLists(listsByName);
                }
            }

            episodeList = episodeListByCountry.get(country);
            if(episodeList == null) {
                episodeList = new ThreadSafeBitSet();
                ThreadSafeBitSet existingEpisodeList = episodeListByCountry.putIfAbsent(country, episodeList);
                if(existingEpisodeList != null)
                    episodeList = existingEpisodeList;
            }
        }
        
        public String getCountry() {
        	return country;
        }

        public void addCompleteVideo(CompleteVideo video, boolean isTopNode) {
            this.video = video;
            this.videoIdOrdinal = videoOrdinalTracker.getVideoOrdinal(video.id);
            if(isTopNode)
                this.topNodeVideoIdOrdinal = this.videoIdOrdinal;

            setMediaAvailabilityBooleans(video);
            
            this.isSupplemental = video.facetData.videoCollectionsData.nodeType == constants.SUPPLEMENTAL;

            process();
        }

        public void process() {
            addToList(VideoNamedListType.VALID_VIDEOS);

            VideoNodeType nodeType = video.facetData.videoCollectionsData.nodeType;
            boolean isTopNode = nodeType == constants.MOVIE || nodeType == constants.SHOW;

            if(isAvailableForED)
                addToList(VideoNamedListType.ED_VIDEOS);

            if(isTopNode) {
                addToList(VideoNamedListType.VALID_TOP_NODES);
                if(isAvailableForED)
                    addToList(VideoNamedListType.ED_TOP_NODES);
            }

            if(isAvailableForED && nodeType == constants.SHOW)
                addToList(VideoNamedListType.ED_SHOWS);
            else if(isAvailableForED && nodeType == constants.EPISODE)
                addToList(VideoNamedListType.ED_EPISODES);
            else if(isAvailableForED && nodeType == constants.SEASON)
                addToList(VideoNamedListType.ED_SEASONS);
            else if(isAvailableForED && nodeType == constants.MOVIE)
                addToList(VideoNamedListType.ED_MOVIES);

            if(isSupplemental) {
                addToList(VideoNamedListType.VALID_SUPPLEMENTALS);
                if(isAvailableForED)
                    addToList(VideoNamedListType.ED_SUPPLEMENTALS);
            }

            boolean isViewable = nodeType == constants.EPISODE || nodeType == constants.MOVIE || nodeType == constants.SUPPLEMENTAL;

            if(isAvailableForED && isViewable)
                addToList(VideoNamedListType.ED_VIEWABLES);

            if(isAvailableIn3D)
                addToList(VideoNamedListType.VALID_3D_VIDEOS);

            if(isAvailableForED && isAvailableIn3D)
                addToList(VideoNamedListType.ED_3D_VIDEOS);

            for(VideoSetType setType : video.facetData.videoMetaData.videoSetTypes) {
                if(setType == constants.PRESENT) {
                    addToList(VideoNamedListType.DEBUG_PRESENT_VIDEOS);
                    addToList(VideoNamedListType.VALID_ED_VIDEOS);
                } else if(setType == constants.PAST) {
                    addToList(VideoNamedListType.DEBUG_PAST_VIDEOS);
                    addToList(VideoNamedListType.VALID_ED_VIDEOS);
                } else if(setType == constants.FUTURE) {
                    addToList(VideoNamedListType.DEBUG_FUTURE_VIDEOS);
                    addToList(VideoNamedListType.VALID_ED_VIDEOS);
                } else if(setType == constants.EXTENDED) {
                    addToList(VideoNamedListType.EXTENDED_VIDEOS);
                }
            }

            boolean isSensitive = video.countrySpecificData.metadataAvailabilityDate == null || video.countrySpecificData.metadataAvailabilityDate.val > ctx.getNowMillis();

            if(isSensitive)
                addToList(VideoNamedListType.SENSITIVE_VIDEOS);

            boolean isOriginal = video.facetData.videoMediaData != null && video.facetData.videoMediaData.isOriginal;

            if(isOriginal) {
                addToList(VideoNamedListType.VALID_ORIGINAL_VIDEOS);
                if(isTopNode)
                    addToList(VideoNamedListType.VALID_ORIGINAL_TOP_NODES);
            }

            if(video.facetData.videoMediaData != null && !video.facetData.videoMediaData.isAutoPlayEnabled) {
                addToList(VideoNamedListType.AUTO_PLAY_DISABLED);
            }

            final boolean isRecentlyAdded = video.countrySpecificData.firstDisplayDate != null && video.countrySpecificData.firstDisplayDate.val > (ctx.getNowMillis() - (MS_IN_DAY * 30));
            boolean isTV = video.facetData.videoMetaData.isTV;

            if(isAvailableForED) {
                long theatricalReleaseDate = video.facetData.videoMetaData.theatricalReleaseDate == null ? 0 : video.facetData.videoMetaData.theatricalReleaseDate.val;
                long dvdReleaseDate = video.facetData.videoMediaData.dvdReleaseDate == null ? 0 : video.facetData.videoMediaData.dvdReleaseDate.val;
                long broadcastReleaseDate = video.facetData.videoMetaData.broadcastReleaseDate == null ? 0 : video.facetData.videoMetaData.broadcastReleaseDate.val;
                
                boolean hasNoReleaseDate = false;
                if (video.facetData.videoMetaData.theatricalReleaseDate == null && video.facetData.videoMetaData.broadcastReleaseDate == null) {
                    hasNoReleaseDate = true;
                } else if(video.facetData.videoMetaData.broadcastReleaseDate != null) {
                    theatricalReleaseDate = broadcastReleaseDate;
                }

                long theatricalReleaseDaysAgo = (ctx.getNowMillis() - theatricalReleaseDate) / MS_IN_DAY;
                long dvdReleaseDaysAgo = (ctx.getNowMillis() - dvdReleaseDate) / MS_IN_DAY;
                long currentAvailabilityDaysAgo = (ctx.getNowMillis() - currentAvailabilityDate) / MS_IN_DAY;


                if(dvdReleaseDaysAgo <= -1000)
                    dvdReleaseDaysAgo = 1000;

                if(currentAvailabilityDaysAgo < 60) {
                    if(theatricalReleaseDaysAgo < (15 * 30)) {
                        if(isTV) {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_TV_EPISODES);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES);
                        } else {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_HOLLYWOOD);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES);
                        }
                    } 

                    if(video.facetData.videoMetaData.theatricalReleaseDate == null) {
                        if(dvdReleaseDaysAgo < (6 * 30) && !isTV) {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_DIRECT_TO_DVD);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES);
                        }
                    }
                }

                if(currentAvailabilityDaysAgo < 1000) {
                    if(isTV && theatricalReleaseDaysAgo < (9 * 30)) {
                        addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_TV_EPISODES_SHORTEN);
                    }

                    if(theatricalReleaseDaysAgo < (12 * 30)) {
                        if(isTV) {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_TV_EPISODES_EXTENDED_WITH_THEATRICAL_12MO);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_EXTENDED_WITH_THEATRICAL_12MO);
                        } else {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_HOLLYWOOD_EXTENDED_WITH_THEATRICAL_12MO);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_EXTENDED_WITH_THEATRICAL_12MO);
                        }
                    }

                    if(theatricalReleaseDaysAgo < (16 * 30)) {
                        if(isTV) {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_TV_EPISODES_EXTENDED);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_EXTENDED);
                        } else {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_HOLLYWOOD_EXTENDED);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_EXTENDED);
                        }
                    } 
                    
                    if(video.facetData.videoMetaData.theatricalReleaseDate == null) {
                        if(dvdReleaseDaysAgo < (6 * 30) && !isTV) {
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_DIRECT_TO_DVD_EXTENDED);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_EXTENDED);
                            addTopNodeToList(VideoNamedListType.ED_NEW_RELEASES_EXTENDED_WITH_THEATRICAL_12MO);
                        }
                    }
                }

                if(!isTV && nodeType != constants.EPISODE) {
                    int theatricalReleaseYear = video.facetData.videoMetaData.year; 
                    if(theatricalReleaseYear >= (currentYear-3)) {
                        addTopNodeToList(VideoNamedListType.RECENT_THEATRICAL_RELEASES_NON_TV_ED_VIDEOS);
                    }
                }

            }
            
            if(isRecentlyAdded && topNodeVideoIdOrdinal == videoIdOrdinal) {
                addTopNodeToList(VideoNamedListType.RECENTLY_ADDED_ED_VIDEOS);
                
                if(isTV) {
                    addTopNodeToList(VideoNamedListType.RECENTLY_ADDED_TV_ED_VIDEOS);
                } else {
                    addTopNodeToList(VideoNamedListType.RECENTLY_ADDED_NON_TV_ED_VIDEOS);
                }
            }

            if(topNodeVideoIdOrdinal == videoIdOrdinal) {
                for(VideoEpisode ep : video.facetData.videoCollectionsData.videoEpisodes) {
                    int episodeOrdinal = videoOrdinalTracker.getVideoOrdinal(ep.deliverableVideo);
                    episodeList.set(episodeOrdinal);
                }
            }
        }

        private void addToList(VideoNamedListType type) {
            ThreadSafeBitSet list = getNamedList(listsByName, type);
            list.set(videoIdOrdinal);
        }

        private void addTopNodeToList(VideoNamedListType type) {
        	if(!isSupplemental) {
	            ThreadSafeBitSet list = getNamedList(listsByName, type);
	            list.set(topNodeVideoIdOrdinal);
        	}
        }

        private ThreadSafeBitSet getNamedList(ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> videoLists, VideoNamedListType type) {
            ThreadSafeBitSet list = videoLists.get(type);
            if(list == null) {
                list = new ThreadSafeBitSet();
                ThreadSafeBitSet existingList = videoLists.putIfAbsent(type, list);
                if(existingList != null)
                    list = existingList;
            }
            return list;
        }

        private void setMediaAvailabilityBooleans(CompleteVideo video) {
            isAvailableForED = false;
            isAvailableIn3D = false;
            currentAvailabilityDate = 0;
            
            if(video.facetData.videoMediaData != null && video.facetData.videoMediaData.isGoLive) {
                for(VMSAvailabilityWindow window : video.countrySpecificData.mediaAvailabilityWindows) {
                    if(window.startDate.val <= ctx.getNowMillis() && window.endDate.val >= ctx.getNowMillis()) {
                        int maxPackageId = -1;
                        VideoPackageInfo maxPackageInfo = null;

                        for(Map.Entry<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo> entry : window.windowInfosByPackageId.entrySet()) {
                            if(entry.getKey().val > maxPackageId) {
                                maxPackageId = entry.getKey().val;
                                maxPackageInfo = entry.getValue().videoPackageInfo;
                            }
                        }

                        isAvailableForED = true;
                        if(maxPackageInfo != null) {
                            isAvailableIn3D = maxPackageInfo.isAvailableIn3D;
                        }

                        currentAvailabilityDate = window.startDate.val;
                        break;
                    }
                }
            }
        }
    }

    private void addEmptyLists(ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> listsByName) {
        listsByName.put(VideoNamedListType.CANON_VIDEOS, new ThreadSafeBitSet());
        listsByName.put(VideoNamedListType.DEBUG_UNKNOWN_VIDEOS, new ThreadSafeBitSet());
        listsByName.put(VideoNamedListType.ED_UNKNOWN_NODE_TYPE, new ThreadSafeBitSet());
    }
}
