package com.netflix.vms.transformer.modules.meta;

import static com.netflix.hollow.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;
import static com.netflix.vms.transformer.index.IndexSpec.PERSONS_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.PERSON_ROLES_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.STORIES_SYNOPSES;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_RIGHTS;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.ReleaseDateHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHookHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralAliasHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralTitleTypeHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonCastHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.Hook;
import com.netflix.vms.transformer.hollowoutput.HookType;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.VideoDateUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoMetaDataModule {

    private static final int ACTOR_ROLE_ID = 103;
    private static final int DIRECTOR_ROLE_ID = 104;
    private static final int CREATOR_ROLE_ID = 108;

    private final VideoSetType PAST = new VideoSetType("Past");
    private final VideoSetType PRESENT = new VideoSetType("Present");
    private final VideoSetType FUTURE = new VideoSetType("Future");
    private final VideoSetType CANON = new VideoSetType("Canon");
    private final VideoSetType EXTENDED = new VideoSetType("Extended");

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;

    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowPrimaryKeyIndex videoTypeIdx;
    private final HollowHashIndex videoTypeCountryIdx;
    private final HollowHashIndex videoDateIdx;
    private final HollowHashIndex videoPersonIdx;
    private final HollowHashIndex videoPersonRoleIdx;
    private final HollowPrimaryKeyIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex storiesSynopsesIdx;

    Map<Integer, VideoMetaData> countryAgnosticMap = new HashMap<Integer, VideoMetaData>();
    Map<Integer, Map<VideoMetaDataCountrySpecificDataKey, VideoMetaData>> countrySpecificMap = new HashMap<Integer, Map<VideoMetaDataCountrySpecificDataKey,VideoMetaData>>();

    private final Map<String, HookType> hookTypeMap = new HashMap<String, HookType>();

    public VideoMetaDataModule(VMSHollowInputAPI api, TransformerContext ctx, VMSTransformerIndexer indexer) {
        this.api = api;
        this.ctx = ctx;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.videoTypeIdx = indexer.getPrimaryKeyIndex(VIDEO_TYPE);
        this.videoPersonIdx = indexer.getHashIndex(PERSONS_BY_VIDEO_ID);
        this.videoPersonRoleIdx = indexer.getHashIndex(PERSON_ROLES_BY_VIDEO_ID);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(VIDEO_RIGHTS);
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
        this.storiesSynopsesIdx = indexer.getPrimaryKeyIndex(STORIES_SYNOPSES);

        hookTypeMap.put("TV Ratings Hook", new HookType("TV_RATINGS"));
        hookTypeMap.put("Awards/Critical Praise Hook", new HookType("AWARDS_CRITICAL_PRAISE"));
        hookTypeMap.put("Box Office Hook", new HookType("BOX_OFFICE"));
        hookTypeMap.put("Talent/Actors Hook", new HookType("TALENT_ACTORS"));
        hookTypeMap.put("Unknown", new HookType("UNKNOWN"));
    }

    public Map<String, Map<Integer, VideoMetaData>> buildVideoMetaDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        countryAgnosticMap.clear();
        countrySpecificMap.clear();

        Map<String, Map<Integer, VideoMetaData>> allVideoMetaDataMap = new HashMap<String, Map<Integer,VideoMetaData>>();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            VideoMetaDataRollupValues rollup = new VideoMetaDataRollupValues();
            Map<Integer, VideoMetaData> countryMap = new HashMap<Integer, VideoMetaData>();
            allVideoMetaDataMap.put(entry.getKey(), countryMap);

            ShowHierarchy hierarchy = entry.getValue();

            for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                rollup.newSeason();

                for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                    convert(hierarchy.getEpisodeIds()[i][j], countryCode, countryMap, rollup);
                }

                rollup.setDoSeason(true);
                convert(hierarchy.getSeasonIds()[i], countryCode, countryMap, rollup);
                rollup.setDoSeason(false);
            }

            rollup.setDoShow(true);
            convert(hierarchy.getTopNodeId(), countryCode, countryMap, rollup);
            rollup.setDoShow(false);

            for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                convert(hierarchy.getSupplementalIds()[i], countryCode, countryMap, rollup);
            }
        }

        return allVideoMetaDataMap;
    }

    /// Here is a good pattern for processing country-specific data
    private void convert(Integer videoId, String countryCode, Map<Integer, VideoMetaData> countryMap, VideoMetaDataRollupValues rollup) {
        /// first create the country specific key
        VideoMetaDataCountrySpecificDataKey countrySpecificKey = createCountrySpecificKey(videoId, countryCode, rollup);

        /// then try to get the country specific clone, return it if it exists
        Map<VideoMetaDataCountrySpecificDataKey, VideoMetaData> countrySpecificMap = this.countrySpecificMap.get(videoId);
        if(countrySpecificMap == null) {
            countrySpecificMap = new HashMap<VideoMetaDataCountrySpecificDataKey, VideoMetaData>();
            this.countrySpecificMap.put(videoId, countrySpecificMap);
        }

        VideoMetaData countrySpecificClone = countrySpecificMap.get(countrySpecificKey);
        if(countrySpecificClone != null) {
            countryMap.put(videoId, countrySpecificClone);
            return;
        }

        /// get the country agnostic data
        VideoMetaData countryAgnosticVMD = getCountryAgnosticClone(videoId);

        /// clone the country agnostic data
        countrySpecificClone = countryAgnosticVMD.clone();

        /// set the country specific data
        countrySpecificClone.isSearchOnly = countrySpecificKey.isSearchOnly;
        countrySpecificClone.isTheatricalRelease = countrySpecificKey.isTheatricalRelease;
        countrySpecificClone.theatricalReleaseDate = countrySpecificKey.theatricalReleaseDate;
        countrySpecificClone.broadcastReleaseDate = countrySpecificKey.broadcastReleaseDate;
        countrySpecificClone.broadcastReleaseYear = countrySpecificKey.broadcastYear;
        countrySpecificClone.year = countrySpecificKey.year;
        countrySpecificClone.latestYear = countrySpecificKey.latestYear;
        countrySpecificClone.videoSetTypes = countrySpecificKey.videoSetTypes;
        countrySpecificClone.showMemberTypeId = countrySpecificKey.showMemberTypeId;
        countrySpecificClone.copyright = countrySpecificKey.copyright;
        countrySpecificClone.hasNewContent = countrySpecificKey.hasNewContent;

        /// return the country specific clone
        countrySpecificMap.put(countrySpecificKey, countrySpecificClone);

        countryMap.put(videoId, countrySpecificClone);
    }

    private VideoMetaDataCountrySpecificDataKey createCountrySpecificKey(Integer videoId, String countryCode, VideoMetaDataRollupValues rollup) {
        VideoMetaDataCountrySpecificDataKey countrySpecificKey = new VideoMetaDataCountrySpecificDataKey();

        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal((long)videoId, countryCode);
        VideoRightsHollow rights = null;
        if(rightsOrdinal != -1) {
            rights = api.getVideoRightsHollow(rightsOrdinal);
            countrySpecificKey.isSearchOnly = rights._getFlags()._getSearchOnly();
        }

        populateSetTypes(videoId, countryCode, rights, countrySpecificKey);
        populateDates(videoId, countryCode, rollup, rights, countrySpecificKey);

        boolean isGoLive = false;
        boolean hasFirstDisplayDate = false;
        boolean isInWindow = false;
        long firstDisplayTimestamp = -1;

        if(rights != null) {
            VideoRightsFlagsHollow flags = rights._getFlags();
            if(flags != null) {
                isGoLive = flags._getGoLive();
                DateHollow firstDisplayDate = flags._getFirstDisplayDate();
                if(firstDisplayDate != null) {
                    firstDisplayTimestamp = firstDisplayDate._getValue();
                    hasFirstDisplayDate = true;
                }

            }

            Set<VideoRightsWindowHollow> windows = rights._getRights()._getWindows();
            for(VideoRightsWindowHollow window : windows) {
                if(!window._getOnHold() && window._getStartDate()._getValue() < ctx.getNowMillis() && window._getEndDate()._getValue() > ctx.getNowMillis()) {
                    isInWindow = true;
                    break;
                }
            }
        }

        if(isGoLive && hasFirstDisplayDate)
            rollup.newPotentiallyEarliestFirstDisplayDate(firstDisplayTimestamp);
        if(isGoLive && isInWindow && hasFirstDisplayDate)
            rollup.newPotentiallyLatestFirstDisplayDate(firstDisplayTimestamp);
        if(hasFirstDisplayDate || (isGoLive && isInWindow))
            rollup.newLatestYear(countrySpecificKey.latestYear);

        if(rollup.doSeason()) {
            if(rollup.getSeasonLatestYear() != 0)
                countrySpecificKey.latestYear = rollup.getSeasonLatestYear();
        } else if(rollup.doShow()) {
            if(rollup.getShowLatestYear() != 0)
                countrySpecificKey.latestYear = rollup.getShowLatestYear();
        }

        countrySpecificKey.hasNewContent = hasNewContent(rollup);



        return countrySpecificKey;
    }

    private VideoMetaData getCountryAgnosticClone(Integer videoId) {
        VideoMetaData vmd = countryAgnosticMap.get(videoId);
        if(vmd != null)
            return vmd;

        vmd = new VideoMetaData();
        populateGeneral(videoId, vmd);
        populateCastLists(videoId, vmd);
        populateHooks(videoId, vmd);

        int genOrdinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
        if (genOrdinal != -1)
            vmd.isTV = api.getVideoGeneralTypeAPI().getTv(genOrdinal);

        countryAgnosticMap.put(videoId, vmd);

        return vmd;
    }

    private void populateSetTypes(Integer videoId, String countryCode, VideoRightsHollow rights, VideoMetaDataCountrySpecificDataKey vmd) {
        HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches((long)videoId, countryCode);
        VideoTypeDescriptorHollow typeDescriptor = null;
        if(videoTypeMatches != null)
            typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());

        boolean isInWindow = false;
        boolean isInFuture = false;
        boolean isExtended = false;
        boolean isCanon = false;

        if(rights != null) {
            Set<VideoRightsWindowHollow> windows = rights._getRights()._getWindows();
            for(VideoRightsWindowHollow window : windows) {
                long windowStart = window._getStartDate()._getValue();
                if(windowStart < ctx.getNowMillis() && window._getEndDate()._getValue() > ctx.getNowMillis()) {
                    isInWindow = true;
                    break;
                } else if(windowStart > ctx.getNowMillis()) {
                    isInFuture = true;
                }
            }
        }

        if(typeDescriptor != null) {
            isExtended = typeDescriptor._getExtended();
        }

        Set<VideoSetType> setOfVideoSetType = new HashSet<VideoSetType>();

        if(isInWindow) {
            setOfVideoSetType.add(PRESENT);
        } else if(isInFuture) {
            setOfVideoSetType.add(FUTURE);
        } else if(isExtended) {
            setOfVideoSetType.add(EXTENDED);
        }

        if(setOfVideoSetType.isEmpty())
            setOfVideoSetType.add(PAST);

        vmd.videoSetTypes = setOfVideoSetType;
        long showMemberTypeId = 0; //@TODO: typeDescriptor._getShowMemberTypeId();
        if(showMemberTypeId != Long.MIN_VALUE)
            vmd.showMemberTypeId = (int)showMemberTypeId;

        StringHollow copyright = typeDescriptor._getCopyright();
        if(copyright != null) {
            vmd.copyright = new Strings(copyright._getValue());
        }
    }

    private void populateGeneral(Integer videoId, VideoMetaData vmd) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal((long)videoId);
        if(ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);

            StringHollow origCountry = general._getOriginCountryCode();
            if(origCountry != null)
                vmd.countryOfOrigin = new ISOCountry(origCountry._getValue());
            vmd.countryOfOriginNameLocale = new NFLocale(general._getOriginalTitleBcpCode()._getValue().replace('-', '_'));
            StringHollow origLang = general._getOriginalLanguageBcpCode();
            if(origLang != null)
                vmd.originalLanguageBcp47code = new Strings(origLang._getValue());

            List<VideoGeneralAliasHollow> inputAliases = general._getAliases();

            if(inputAliases != null) {
                Set<Strings> aliasList = new HashSet<Strings>();
                for(VideoGeneralAliasHollow alias : inputAliases) {
                    aliasList.add(new Strings(alias._getValue()._getValue()));
                }
                vmd.aliases = aliasList;
            }

            List<VideoGeneralTitleTypeHollow> inputTitleTypes = general._getTestTitleTypes();

            if(inputTitleTypes != null) {
                Set<Strings> titleTypes = new HashSet<Strings>();
                for(VideoGeneralTitleTypeHollow titleType : inputTitleTypes) {
                    titleTypes.add(new Strings(titleType._getValue()._getValue()));
                }

                vmd.titleTypes = titleTypes;
            }


            vmd.isTestTitle = general._getTestTitle();
        }
    }

    private void populateDates(Integer videoId, String countryCode, VideoMetaDataRollupValues rollup, VideoRightsHollow rights, VideoMetaDataCountrySpecificDataKey vmd) {
        HollowHashIndexResult dateResult = videoDateIdx.findMatches((long)videoId, countryCode);

        if(dateResult != null) {
            int ordinal = dateResult.iterator().next();
            VideoDateWindowHollow dateWindow = api.getVideoDateWindowHollow(ordinal);
            ReleaseDateHollow theatricalReleaseDate = VideoDateUtil.getReleaseDateType("Theatrical", dateWindow);
            ReleaseDateHollow broadcastReleaseDate = VideoDateUtil.getReleaseDateType("Broadcast", dateWindow);

            vmd.isTheatricalRelease = theatricalReleaseDate != null;
            vmd.year = theatricalReleaseDate == null ? 0 : theatricalReleaseDate._getYear();
            vmd.latestYear = vmd.year;
            vmd.theatricalReleaseDate = VideoDateUtil.convertToHollowOutputDate(theatricalReleaseDate);
            vmd.broadcastYear = broadcastReleaseDate == null ? 0 : broadcastReleaseDate._getYear();
            vmd.broadcastReleaseDate = VideoDateUtil.convertToHollowOutputDate(broadcastReleaseDate);
        } else {
            vmd.year = 0;
            vmd.latestYear = 0;
        }
    }

    private void populateCastLists(Integer videoId, VideoMetaData vmd) {
        List<VPerson> actorList = new ArrayList<VPerson>();
        List<VPerson> directorList = new ArrayList<VPerson>();
        List<VPerson> creatorList = new ArrayList<VPerson>();

        HollowHashIndexResult personMatches = videoPersonIdx.findMatches((long)videoId);
        if(personMatches != null) {
            HollowOrdinalIterator iter = personMatches.iterator();

            int personOrdinal = iter.next();
            while(personOrdinal != NO_MORE_ORDINALS) {
                VideoPersonHollow person = api.getVideoPersonHollow(personOrdinal);

                long personId = person._getPersonId();

                HollowHashIndexResult roleMatches = videoPersonRoleIdx.findMatches(personId, (long)videoId);
                HollowOrdinalIterator roleIter = roleMatches.iterator();

                int roleOrdinal = roleIter.next();
                while(roleOrdinal != NO_MORE_ORDINALS) {
                    VideoPersonCastHollow role = api.getVideoPersonCastHollow(roleOrdinal);

                    if(role._getRoleTypeId() == ACTOR_ROLE_ID) {
                        actorList.add(new VPerson((int)personId));
                    } else if(role._getRoleTypeId() == DIRECTOR_ROLE_ID) {
                        directorList.add(new VPerson((int)personId));
                    } else if(role._getRoleTypeId() == CREATOR_ROLE_ID) {
                        creatorList.add(new VPerson((int)personId));
                    }

                    roleOrdinal = roleIter.next();
                }

                personOrdinal = iter.next();
            }
        }

        vmd.actorList = actorList;
        vmd.directorList = directorList;
        vmd.creatorList = creatorList;
    }

    private void populateHooks(Integer videoId, VideoMetaData vmd) {
        int storiesSynopsesOrdinal = storiesSynopsesIdx.getMatchingOrdinal((long)videoId);

        if(storiesSynopsesOrdinal != -1) {
            StoriesSynopsesHollow synopses = api.getStoriesSynopsesHollow(storiesSynopsesOrdinal);

            List<Hook> hooks = new ArrayList<Hook>();

            for(StoriesSynopsesHookHollow hook : synopses._getHooks()) {
                String type = hook._getType()._getValue();
                int rank = Integer.parseInt(hook._getRank()._getValue());

                Hook outputHook = new Hook();
                outputHook.type = hookTypeMap.get(type);
                outputHook.rank = rank;
                outputHook.video = new Video(videoId);

                hooks.add(outputHook);
            }

            vmd.hooks = hooks;
        }
    }


    private static final int NEW_CONTENT_MIN_DAYS_ON_SITE = 30;
    private static final int NUM_DAYS_BEFORE_NOT_NEW_CONTENT = 30;

    public boolean hasNewContent(VideoMetaDataRollupValues rollup) {
        if(rollup.doShow()) {
            if(daysAgo(rollup.getEarliestFirstDisplayDate()) > NEW_CONTENT_MIN_DAYS_ON_SITE && daysAgo(rollup.getLatestLiveFirstDisplayDate()) <= NUM_DAYS_BEFORE_NOT_NEW_CONTENT)
                return true;
        }

        return false;
    }

    private int daysAgo(long timestamp) {
        return (int)((ctx.getNowMillis() - timestamp) / (24 * 60 * 60 * 1000));
    }



}
