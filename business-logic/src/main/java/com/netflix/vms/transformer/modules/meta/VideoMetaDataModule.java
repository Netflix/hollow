package com.netflix.vms.transformer.modules.meta;

import static com.netflix.hollow.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;
import static com.netflix.vms.transformer.index.IndexSpec.L10N_STORIES_SYNOPSES;
import static com.netflix.vms.transformer.index.IndexSpec.PERSONS_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.PERSON_ROLES_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_RIGHTS;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;

import com.netflix.vms.transformer.hollowinput.VideoGeneralEpisodeTypeHollow;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoRoleHollow;
import com.netflix.vms.transformer.hollowinput.ReleaseDateHollow;
import com.netflix.vms.transformer.hollowinput.ShowMemberTypeHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHookHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralAliasHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralTitleTypeHollow;
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
import com.netflix.vms.transformer.hollowoutput.VRole;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.VideoDateUtil;
import com.netflix.vms.transformer.util.VideoSetTypeUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoMetaDataModule {

    private static final int DEFAULT_SHOW_MEMBER_TYPE_ID = Integer.MIN_VALUE;
    private static final int ACTOR_ROLE_ID = 103;
    private static final int DIRECTOR_ROLE_ID = 104;
    private static final int CREATOR_ROLE_ID = 108;

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final CycleConstants constants;
    private final VMSTransformerIndexer indexer;

    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowHashIndex videoTypeCountryIdx;
    private final HollowHashIndex videoDateIdx;
    private final HollowHashIndex personVideoIdx;
    private final HollowHashIndex personVideoRoleIdx;
    private final HollowHashIndex showCountryLabelIdx;
    private final HollowPrimaryKeyIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex storiesSynopsesIdx;

    Map<Integer, VideoMetaData> countryAgnosticMap = new HashMap<Integer, VideoMetaData>();
    Map<Integer, Map<VideoMetaDataCountrySpecificDataKey, VideoMetaData>> countrySpecificMap = new HashMap<Integer, Map<VideoMetaDataCountrySpecificDataKey,VideoMetaData>>();

    private final Map<String, HookType> hookTypeMap = new HashMap<String, HookType>();

    public VideoMetaDataModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        this.api = api;
        this.ctx = ctx;
        this.constants = constants;
        this.indexer = indexer;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.personVideoIdx = indexer.getHashIndex(PERSONS_BY_VIDEO_ID);
        this.personVideoRoleIdx = indexer.getHashIndex(PERSON_ROLES_BY_VIDEO_ID);
        this.showCountryLabelIdx = indexer.getHashIndex(IndexSpec.SHOW_COUNTRY_LABEL);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(VIDEO_RIGHTS);
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
        this.storiesSynopsesIdx = indexer.getPrimaryKeyIndex(L10N_STORIES_SYNOPSES);

        hookTypeMap.put("TV Ratings Hook", new HookType("TV_RATINGS"));
        hookTypeMap.put("Awards/Critical Praise Hook", new HookType("AWARDS_CRITICAL_PRAISE"));
        hookTypeMap.put("Box Office Hook", new HookType("BOX_OFFICE"));
        hookTypeMap.put("Talent/Actors Hook", new HookType("TALENT_ACTORS"));
        hookTypeMap.put("Unknown", new HookType("UNKNOWN"));
    }

    public Map<String, Map<Integer, VideoMetaData>> buildVideoMetaDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry) {
        countryAgnosticMap.clear();
        countrySpecificMap.clear();

        Map<String, Map<Integer, VideoMetaData>> allVideoMetaDataMap = new HashMap<String, Map<Integer,VideoMetaData>>();

        for(Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            Map<Integer, VideoMetaData> countryMap = new HashMap<Integer, VideoMetaData>();
            allVideoMetaDataMap.put(entry.getKey(), countryMap);

            for(VideoHierarchy hierarchy : entry.getValue()) {
                VideoMetaDataRollupValues rollup = new VideoMetaDataRollupValues();
                int topNodeId = hierarchy.getTopNodeId();
                VideoMetaDataRolldownValues rolldown = new VideoMetaDataRolldownValues(topNodeId);
                rolldown.setShowMemberTypeId(getShowMemberTypeId(topNodeId, countryCode));

                for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                    rollup.newSeason();

                    for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                        rollup.setDoEpisode(true);
                        rolldown.setDoEpisode(true);
                        convert(hierarchy.getEpisodeIds()[i][j], countryCode, countryMap, rollup, rolldown);
                        rollup.setDoEpisode(false);
                        rolldown.setDoEpisode(false);
                    }

                    rollup.setDoSeason(true);
                    rolldown.setDoSeason(true);
                    convert(hierarchy.getSeasonIds()[i], countryCode, countryMap, rollup, rolldown);
                    rollup.setDoSeason(false);
                    rolldown.setDoSeason(false);
                }

                rollup.setDoShow(true);
                rolldown.setDoShow(true);
                convert(hierarchy.getTopNodeId(), countryCode, countryMap, rollup, rolldown);
                rollup.setDoShow(false);
                rolldown.setDoShow(false);

                for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                    convert(hierarchy.getSupplementalIds()[i], countryCode, countryMap, rollup, rolldown);
                }
            }
        }

        return allVideoMetaDataMap;
    }

    /// Here is a good pattern for processing country-specific data
    private void convert(Integer videoId, String countryCode, Map<Integer, VideoMetaData> countryMap, VideoMetaDataRollupValues rollup, VideoMetaDataRolldownValues rolldown) {
        /// first create the country specific key
        VideoMetaDataCountrySpecificDataKey countrySpecificKey = createCountrySpecificKey(videoId, countryCode, rollup, rolldown);

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
        countrySpecificClone.broadcastDistributorName = countrySpecificKey.broadcastDistributorName;
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

    private VideoMetaDataCountrySpecificDataKey createCountrySpecificKey(Integer videoId, String countryCode, VideoMetaDataRollupValues rollup, VideoMetaDataRolldownValues rolldown) {
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

        // Roll Down
        if (rolldown.doSeason()) {
            countrySpecificKey.showMemberTypeId = rolldown.getShowMemberTypeId();
        } else if (rolldown.doShow()) {
            countrySpecificKey.showMemberTypeId = rolldown.getShowMemberTypeId();
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
        populateRoleLists(videoId, vmd);
        populateHooks(videoId, vmd);

        int genOrdinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
        if (genOrdinal != -1)
            vmd.isTV = api.getVideoGeneralTypeAPI().getTv(genOrdinal);

        countryAgnosticMap.put(videoId, vmd);

        return vmd;
    }

    private int getShowMemberTypeId(Integer videoId, String countryCode) {
        HollowHashIndexResult showCountryLabelMatches = showCountryLabelIdx.findMatches((long) videoId, countryCode);
        if (showCountryLabelMatches != null) {
            HollowOrdinalIterator iter = showCountryLabelMatches.iterator();
            int ordinal = iter.next();
            while (ordinal != NO_MORE_ORDINALS) {
                ShowMemberTypeHollow data = api.getShowMemberTypeHollow(ordinal);
                if (data != null) {
                    return (int) data._getSequenceLabelId();
                }
            }
        }

        return DEFAULT_SHOW_MEMBER_TYPE_ID;
    }

    private void populateSetTypes(Integer videoId, String countryCode, VideoRightsHollow rights, VideoMetaDataCountrySpecificDataKey vmd) {
        HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches((long) videoId, countryCode);
        VideoTypeDescriptorHollow typeDescriptor = null;
        if (videoTypeMatches != null) {
            typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());
        }

        vmd.videoSetTypes = VideoSetTypeUtil.computeSetTypes(videoId, countryCode, rights, typeDescriptor, api, ctx, constants, indexer);

        StringHollow copyright = typeDescriptor == null ? null : typeDescriptor._getCopyright();
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
                Set<Strings> aliasList = new HashSet<>();
                for(VideoGeneralAliasHollow alias : inputAliases) {
                    aliasList.add(new Strings(alias._getValue()._getValue()));
                }
                vmd.aliases = aliasList;
            }

            List<VideoGeneralTitleTypeHollow> inputTitleTypes = general._getTestTitleTypes();

            if(inputTitleTypes != null) {
                Set<Strings> titleTypes = new HashSet<>();
                for(VideoGeneralTitleTypeHollow titleType : inputTitleTypes) {
                    titleTypes.add(new Strings(titleType._getValue()._getValue()));
                }

                vmd.titleTypes = titleTypes;
            }
            
            List<VideoGeneralEpisodeTypeHollow> inputEpisodeTypes = general._getEpisodeTypes();
            
            if(inputEpisodeTypes != null) {
                Set<Strings> epTypes = new HashSet<>();
                for(VideoGeneralEpisodeTypeHollow epType : inputEpisodeTypes) {
                    epTypes.add(new Strings(epType._getValue()._getValue()));
                }
                
                vmd.episodeTypes = epTypes;
            }

            vmd.isTestTitle = general._getTestTitle();
            vmd.metadataReleaseDays = OutputUtil.getNullableInteger(general._getMetadataReleaseDays());
        }
    }

    private void populateDates(Integer videoId, String countryCode, VideoMetaDataRollupValues rollup, VideoRightsHollow rights, VideoMetaDataCountrySpecificDataKey vmd) {
        HollowHashIndexResult dateResult = videoDateIdx.findMatches((long)videoId, countryCode);
        if(dateResult != null) {
            int ordinal = dateResult.iterator().next();
            VideoDateWindowHollow dateWindow = api.getVideoDateWindowHollow(ordinal);
            
            List<ReleaseDateHollow> releaseDates = dateWindow._getReleaseDates();
            if (releaseDates != null) {
                for (ReleaseDateHollow releaseDate : releaseDates) {
                    String releaseDateType = releaseDate._getReleaseDateType()._getValue();
                    if(releaseDateType.equals(VideoDateUtil.ReleaseDateType.Theatrical.toString())) {
                        vmd.isTheatricalRelease = true;
                        vmd.theatricalReleaseDate = VideoDateUtil.convertToHollowOutputDate(releaseDate);
                    } else if(releaseDateType.equals(VideoDateUtil.ReleaseDateType.Broadcast.toString())) {
                        vmd.broadcastYear = releaseDate._getYear();
                        vmd.broadcastReleaseDate = VideoDateUtil.convertToHollowOutputDate(releaseDate);
                        StringHollow distributorName = releaseDate._getDistributorName();
                        if(distributorName != null)
                            vmd.broadcastDistributorName = new Strings(distributorName._getValue());
                    }
                }            
            }
        }

        int ordinal = videoGeneralIdx.getMatchingOrdinal((long)videoId);
        if(ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
            vmd.year = (int) general._getFirstReleaseYear();
            vmd.latestYear = vmd.year;
        }
    }

    private void populateRoleLists(Integer videoId, VideoMetaData vmd) {
        Map<VRole, List<VPerson>> roles = new HashMap<>();
        HollowHashIndexResult personMatches = personVideoIdx.findMatches((long) videoId);
        if(personMatches != null) {
            HollowOrdinalIterator iter = personMatches.iterator();

            int personOrdinal = iter.next();
            while(personOrdinal != NO_MORE_ORDINALS) {
                PersonVideoHollow person = api.getPersonVideoHollow(personOrdinal);

                long personId = person._getPersonId();

                HollowHashIndexResult roleMatches = personVideoRoleIdx.findMatches(personId, (long) videoId);
                HollowOrdinalIterator roleIter = roleMatches.iterator();

                int roleOrdinal = roleIter.next();
                while(roleOrdinal != NO_MORE_ORDINALS) {
                    PersonVideoRoleHollow role = api.getPersonVideoRoleHollow(roleOrdinal);

                    VRole vRole = new VRole(role._getRoleTypeId());
                    List<VPerson> list = roles.get(vRole);
                    if (list == null) {
                        list = new ArrayList<>();
                        roles.put(vRole, list);
                    }
                    VPerson vPerson = new VPerson((int) personId);
                    list.add(vPerson);

                    roleOrdinal = roleIter.next();
                }

                personOrdinal = iter.next();
            }
        }

        vmd.roles = roles;
        vmd.actorList = getRoles(ACTOR_ROLE_ID, roles);
        vmd.directorList = getRoles(DIRECTOR_ROLE_ID, roles);
        vmd.creatorList = getRoles(CREATOR_ROLE_ID, roles);
    }

    private List<VPerson> getRoles(int roleId, Map<VRole, List<VPerson>> roles) {
        VRole vRole = new VRole(roleId);
        List<VPerson> list = roles.get(vRole);
        if (list != null) return list;

        return Collections.emptyList();
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
