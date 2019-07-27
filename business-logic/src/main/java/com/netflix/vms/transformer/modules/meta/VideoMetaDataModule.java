package com.netflix.vms.transformer.modules.meta;

import static com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.CONVERTER;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.GATEKEEPER2;
import static com.netflix.vms.transformer.index.IndexSpec.L10N_STORIES_SYNOPSES;
import static com.netflix.vms.transformer.index.IndexSpec.PERSONS_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.PERSON_ROLES_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;
import static com.netflix.vms.transformer.modules.countryspecific.VMSAvailabilityWindowModule.ONE_THOUSAND_YEARS;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.data.VideoDataCollection;
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
import com.netflix.vms.transformer.hollowinput.VideoGeneralEpisodeTypeHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralTitleTypeHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.Hook;
import com.netflix.vms.transformer.hollowoutput.HookType;
import com.netflix.vms.transformer.hollowoutput.InteractiveData;
import com.netflix.vms.transformer.hollowoutput.MerchBehavior;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.VRole;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Flags;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.ListOfRightsWindow;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsWindow;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import com.netflix.vms.transformer.input.datasets.ConverterDataset;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
import com.netflix.vms.transformer.util.DeprecationUtil;
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

//TODO: enable me once we can turn on the new data set including follow vip functionality
//import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.DatasetIdentifier.OSCAR;


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
    private final Gatekeeper2Dataset gk2Dataset;
    private final HollowPrimaryKeyIndex storiesSynopsesIdx;

//    private final OscarDataset oscarDataset;

    private final int newContentFlagDuration;

    Map<Integer, VideoMetaData> countryAgnosticMap = new HashMap<>();
    Map<Integer, Map<VideoMetaDataCountrySpecificDataKey, VideoMetaData>> countrySpecificMap = new HashMap<>();

    private final Map<String, HookType> hookTypeMap = new HashMap<>();

    public VideoMetaDataModule(UpstreamDatasetHolder upstream, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        ConverterDataset converterDataset = upstream.getDataset(CONVERTER);
        this.api = converterDataset.getAPI();
        this.gk2Dataset = upstream.getDataset(GATEKEEPER2);

        //TODO: enable me once we can turn on the new data set including follow vip functionality
        //this.oscarDataset = upstream.getDataset(OSCAR);
        this.ctx = ctx;
        this.constants = constants;
        this.indexer = indexer;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.personVideoIdx = indexer.getHashIndex(PERSONS_BY_VIDEO_ID);
        this.personVideoRoleIdx = indexer.getHashIndex(PERSON_ROLES_BY_VIDEO_ID);
        this.showCountryLabelIdx = indexer.getHashIndex(IndexSpec.SHOW_COUNTRY_LABEL);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
        this.storiesSynopsesIdx = indexer.getPrimaryKeyIndex(L10N_STORIES_SYNOPSES);

        this.newContentFlagDuration = ctx.getConfig().getNewContentFlagDuration();

        hookTypeMap.put("TV Ratings Hook", new HookType("TV_RATINGS"));
        hookTypeMap.put("Awards/Critical Praise Hook", new HookType("AWARDS_CRITICAL_PRAISE"));
        hookTypeMap.put("Box Office Hook", new HookType("BOX_OFFICE"));
        hookTypeMap.put("Talent/Actors Hook", new HookType("TALENT_ACTORS"));
        hookTypeMap.put("Unknown", new HookType("UNKNOWN"));
    }

    public Map<String, Map<Integer, VideoMetaData>> buildVideoMetaDataByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, TransformedVideoData transformedVideoData) {
        countryAgnosticMap.clear();
        countrySpecificMap.clear();

        Map<String, Map<Integer, VideoMetaData>> allVideoMetaDataMap = new HashMap<>();

        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            VideoDataCollection videoDataCollection = transformedVideoData.getVideoDataCollection(countryCode);

            for (VideoHierarchy hierarchy : entry.getValue()) {
                VideoMetaDataRollupValues rollup = new VideoMetaDataRollupValues();
                int topNodeId = hierarchy.getTopNodeId();
                VideoMetaDataRolldownValues rolldown = new VideoMetaDataRolldownValues(topNodeId);
                rolldown.setShowMemberTypeId(getShowMemberTypeId(topNodeId, countryCode));

                for (int i = 0; i < hierarchy.getSeasonIds().length; i++) {
                    rollup.newSeason();

                    for (int j = 0; j < hierarchy.getEpisodeIds()[i].length; j++) {
                        rollup.setDoEpisode(true);
                        rolldown.setDoEpisode(true);
                        convert(hierarchy.getEpisodeIds()[i][j], countryCode, videoDataCollection, rollup, rolldown);
                        populateEpisodeMerchBehavior(hierarchy.getEpisodeIds()[i][j], i, j, hierarchy, videoDataCollection);
                        rollup.setDoEpisode(false);
                        rolldown.setDoEpisode(false);
                    }

                    rollup.setDoSeason(true);
                    rolldown.setDoSeason(true);
                    convert(hierarchy.getSeasonIds()[i], countryCode, videoDataCollection, rollup, rolldown);
                    populateSeasonMerchBehavior(hierarchy.getSeasonIds()[i], i, hierarchy, videoDataCollection);
                    rollup.setDoSeason(false);
                    rolldown.setDoSeason(false);
                }

                rollup.setDoShow(true);
                rolldown.setDoShow(true);
                convert(hierarchy.getTopNodeId(), countryCode, videoDataCollection, rollup, rolldown);
                populateShowMerchBehavior(hierarchy.getTopNodeId(), hierarchy, videoDataCollection);
                rollup.setDoShow(false);
                rolldown.setDoShow(false);

                for (int i = 0; i < hierarchy.getSupplementalIds().length; i++) {
                    convert(hierarchy.getSupplementalIds()[i], countryCode, videoDataCollection, rollup, rolldown);
                }
            }
        }

        return allVideoMetaDataMap;
    }

    /// Here is a good pattern for processing country-specific data
    private void convert(Integer videoId, String countryCode, VideoDataCollection videoDataCollection, VideoMetaDataRollupValues rollup, VideoMetaDataRolldownValues rolldown) {
        // first create the country specific key
        VideoMetaDataCountrySpecificDataKey countrySpecificKey = createCountrySpecificKey(videoId, countryCode, rollup, rolldown);
        // then try to get the country specific clone, return it if it exists
        Map<VideoMetaDataCountrySpecificDataKey, VideoMetaData> countrySpecificMap = this.countrySpecificMap.get(videoId);
        if (countrySpecificMap == null) {
            countrySpecificMap = new HashMap<>();
            this.countrySpecificMap.put(videoId, countrySpecificMap);
        }
        VideoMetaData countrySpecificClone = countrySpecificMap.get(countrySpecificKey);
        if (countrySpecificClone != null) {
            videoDataCollection.addVideoMetaData(videoId, countrySpecificClone);
            return;
        }
        /// get the country agnostic data
        VideoMetaData countryAgnosticVMD = getCountryAgnosticClone(videoId);
        /// clone the country agnostic data
        countrySpecificClone = countryAgnosticVMD.clone();
        
        // Add the episode type override for the country if it exists

        //TODO: enable me once we can turn on the new data set including follow vip functionality
//        Strings epTypeOverride = (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral())?
//                getEpisodeTypeOverrideOscar(videoId, countryCode)
//                : getEpisodeTypeOverride(videoId, countryCode);

        Strings epTypeOverride = getEpisodeTypeOverride(videoId, countryCode);

        if(epTypeOverride != null)
        	countrySpecificClone.episodeTypes.add(epTypeOverride);

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
        videoDataCollection.addVideoMetaData(videoId, countrySpecificClone);
    }
    
    private Strings getEpisodeTypeOverride(Integer videoId, String countryCode) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
        if (ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
            
            List<VideoGeneralEpisodeTypeHollow> inputEpisodeTypes = general._getEpisodeTypes();

            if (inputEpisodeTypes != null && !inputEpisodeTypes.isEmpty()) {
                for (VideoGeneralEpisodeTypeHollow epType : inputEpisodeTypes) {
                	// Country is non null and also matches the country code 
                	if(epType._getCountry() != null && epType._getCountry()._getValue().equals(countryCode)) {
                		return new Strings(epType._getValue()._getValue());                		
                	}
                }
            }            
        }
        
        return null;
    }

//    private Strings getEpisodeTypeOverrideOscar(long videoId, String countryCode) {
//        if (!oscarDataset.movieExists(videoId)) {
//            return null;
//        }
//        Stream<MovieExtension> meStream = oscarDataset.getMovieExtensions(videoId, OscarDataset.MovieExtensionAttributeName.EPISODE_TYPE);
//        Optional<MovieExtensionOverride> meOverride = meStream.flatMap(me->me.getOverrides().stream())
//                .filter(override -> override.getAttributeValue()!=null
//                        && OscarDataset.MovieExtensionOverrideEntityType.COUNTRY.name().equals(override.getEntityType())
//                        && override.getEntityValue() != null
//                        && override.getEntityValue().equals(countryCode))
//                .findFirst();
//        return meOverride.map(meo->new Strings(meo.getAttributeValue())).orElse(null);
//    }

    private VideoMetaDataCountrySpecificDataKey createCountrySpecificKey(Integer videoId, String countryCode, VideoMetaDataRollupValues rollup, VideoMetaDataRolldownValues rolldown) {
        VideoMetaDataCountrySpecificDataKey countrySpecificKey = new VideoMetaDataCountrySpecificDataKey();

        Status status = gk2Dataset.getStatus(videoId.longValue(), countryCode);
        if (status != null) {
            countrySpecificKey.isSearchOnly = status.getFlags().getSearchOnly();
        }

        populateSetTypes(videoId, countryCode, status, countrySpecificKey);
        populateDates(videoId, countryCode, rollup, status, countrySpecificKey);

        boolean isGoLive = false;
        boolean hasFirstDisplayDate = false;
        boolean isInWindow = false;
        long firstDisplayTimestamp = -1;

        if (status != null) {
            Flags flags = status.getFlags();
            if (flags != null) {
                isGoLive = flags.getGoLive();
                long firstDisplayDate = flags.getFirstDisplayDate();
                if (firstDisplayDate != Long.MIN_VALUE) {
                    firstDisplayTimestamp = firstDisplayDate;
                    hasFirstDisplayDate = true;
                }

            }

            ListOfRightsWindow windows = status.getRights().getWindows();
            for (RightsWindow window : windows) {
                long startDate = window.getStartDate();
                long endDate = window.getEndDate();
                if (window.getOnHold()) {
                    startDate += ONE_THOUSAND_YEARS;
                    endDate += ONE_THOUSAND_YEARS;
                }

                if (startDate < ctx.getNowMillis() && endDate > ctx.getNowMillis()) {
                    isInWindow = true;
                    break;
                }
            }
        }

        if (isGoLive && hasFirstDisplayDate)
            rollup.newPotentiallyEarliestFirstDisplayDate(firstDisplayTimestamp);
        if (isGoLive && isInWindow && hasFirstDisplayDate)
            rollup.newPotentiallyLatestFirstDisplayDate(firstDisplayTimestamp);
        if (hasFirstDisplayDate || (isGoLive && isInWindow))
            rollup.newLatestYear(countrySpecificKey.latestYear);

        if (rollup.doSeason()) {
            if (rollup.getSeasonLatestYear() != 0)
                countrySpecificKey.latestYear = rollup.getSeasonLatestYear();
        } else if (rollup.doShow()) {
            if (rollup.getShowLatestYear() != 0)
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
        if (vmd != null)
            return vmd;

        vmd = new VideoMetaData();
        populateGeneral(videoId, vmd);
        populateRoleLists(videoId, vmd);
        populateHooks(videoId, vmd);

        setIsTv(videoId,vmd);

//        if (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) {
//            populateGeneralOscar(videoId, vmd);
//            setIsTvOscar(videoId,vmd);
//        } else {
//            populateGeneral(videoId, vmd);
//            setIsTv(videoId,vmd);
//        }

        countryAgnosticMap.put(videoId, vmd);

        return vmd;
    }

    private void setIsTv(long videoId, VideoMetaData vmd) {
        int genOrdinal = videoGeneralIdx.getMatchingOrdinal(videoId);
        if (genOrdinal != -1)
            vmd.isTV = api.getVideoGeneralTypeAPI().getTv(genOrdinal);
    }

//    private void setIsTvOscar(long videoId, VideoMetaData vmd) {
//        oscarDataset.execWithMovieIfExists(videoId,(movie -> vmd.isTV = movie.getTv()));
//    }

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

    private void populateShowMerchBehavior(int videoId, VideoHierarchy hierarchy, VideoDataCollection videoDataCollection) {
        VideoHierarchy.ShowMerchBehavior merchingBehaviour = hierarchy.getShowMerchBehavior();
        if (merchingBehaviour != null) {
            VideoMetaData vmd = videoDataCollection.getVideoMetaData(videoId);
            vmd.merchBehavior = new MerchBehavior();
            vmd.merchBehavior.merchOrder = merchingBehaviour.merchOrder;
            vmd.merchBehavior.episodicNewBadge = merchingBehaviour.episodicNewBadge;
            vmd.merchBehavior.hideSeasonNumbers = merchingBehaviour.hideSeasonNumbers;
        }
    }

    private void populateSeasonMerchBehavior(int videoId, int seasonNum, VideoHierarchy hierarchy, VideoDataCollection videoDataCollection) {

        VideoHierarchy.SeasonMerchBehavior merchingBehaviour = hierarchy.getSeasonMerchingBehaviour(seasonNum);
        if (merchingBehaviour != null) {
            VideoMetaData vmd = videoDataCollection.getVideoMetaData(videoId);
            vmd.merchBehavior = new MerchBehavior();
            vmd.merchBehavior.hideEpisodeNumbers = merchingBehaviour.hideEpisodeNumbers;
            vmd.merchBehavior.episodeSkipping = merchingBehaviour.episodeSkipping;
            vmd.merchBehavior.episodicNewBadge = merchingBehaviour.episodicNewBadge;
            vmd.merchBehavior.filterUnavailableEpisodes = merchingBehaviour.filterUnavailableEpisodes;
            vmd.merchBehavior.useLatestEpisodeAsDefault = merchingBehaviour.useLatestEpisodeAsDefault;
            vmd.merchBehavior.merchOrder = merchingBehaviour.merchOrder;
        }
    }

    private void populateEpisodeMerchBehavior(int videoId, int seasonNum, int episodeNum, VideoHierarchy hierarchy, VideoDataCollection videoDataCollection) {

        VideoHierarchy.EpisodeMerchBehavior merchingBehaviour = hierarchy.getEpisodeMerchingBehaviour(seasonNum, episodeNum);
        if (merchingBehaviour != null) {
            VideoMetaData vmd = videoDataCollection.getVideoMetaData(videoId);
            vmd.merchBehavior = new MerchBehavior();
            vmd.merchBehavior.midSeason = merchingBehaviour.midSeason;
            vmd.merchBehavior.seasonFinale = merchingBehaviour.seasonFinale;
            vmd.merchBehavior.showFinale = merchingBehaviour.showFinale;
        }
    }

    private void populateSetTypes(Integer videoId, String countryCode, Status rights, VideoMetaDataCountrySpecificDataKey vmd) {
        HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches((long) videoId, countryCode);
        VideoTypeDescriptorHollow typeDescriptor = null;
        if (videoTypeMatches != null) {
            typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());
        }

        vmd.videoSetTypes = VideoSetTypeUtil.computeSetTypes(videoId, countryCode, rights, typeDescriptor, api, ctx, constants, indexer, gk2Dataset);

        StringHollow copyright = typeDescriptor == null ? null : typeDescriptor._getCopyright();
        if (copyright != null) {
            vmd.copyright = new Strings(copyright._getValue());
        }
    }

    private void populateGeneral(Integer videoId, VideoMetaData vmd) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal((long) videoId);
        if (ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);

            StringHollow origCountry = general._getOriginCountryCode();
            if (origCountry != null)
                vmd.countryOfOrigin = constants.getISOCountry(origCountry._getValue());
            vmd.countryOfOriginNameLocale = new NFLocale(general._getOriginalTitleBcpCode()._getValue().replace('-', '_'));
            StringHollow origLang = general._getOriginalLanguageBcpCode();
            if (origLang != null)
                vmd.originalLanguageBcp47code = new Strings(origLang._getValue());

            // marked for removal
            if (!DeprecationUtil.disableVideoGeneralAliases()) {
                List<VideoGeneralAliasHollow> inputAliases = general._getAliases();

                if (inputAliases != null) {
                    Set<Strings> aliasList = new HashSet<>();
                    for (VideoGeneralAliasHollow alias : inputAliases) {
                        aliasList.add(new Strings(alias._getValue()._getValue()));
                    }
                    vmd.aliases = aliasList;
                }
            }

            List<VideoGeneralTitleTypeHollow> inputTitleTypes = general._getTestTitleTypes();

            if (inputTitleTypes != null) {
                Set<Strings> titleTypes = new HashSet<>();
                for (VideoGeneralTitleTypeHollow titleType : inputTitleTypes) {
                    titleTypes.add(new Strings(titleType._getValue()._getValue()));
                }

                vmd.titleTypes = titleTypes;
            }

            List<VideoGeneralEpisodeTypeHollow> inputEpisodeTypes = general._getEpisodeTypes();

            if (inputEpisodeTypes != null) {
                Set<Strings> epTypes = new HashSet<>();
                for (VideoGeneralEpisodeTypeHollow epType : inputEpisodeTypes) {
                	// the episode type with the non-null country code are country specific
                	// they are handled seperately
                	if(epType._getCountry() == null) {
                		epTypes.add(new Strings(epType._getValue()._getValue()));                		
                	}
                }

                vmd.episodeTypes = epTypes;
            }

            vmd.isTestTitle = general._getTestTitle();
            vmd.metadataReleaseDays = OutputUtil.getNullableInteger(general._getMetadataReleaseDays());

            // yes the long to int downcast isn't the best, but it follows the precedent (of modeling with long and casting to int
            // ref VideoMediaDataModule:  vmd.approximateRuntimeInSeconds = (int) general._getRuntime();
            vmd.displayRuntimeInSeconds = OutputUtil.getNullableInteger((int)general._getDisplayRuntime());
            if (general._getInteractiveData()!=null) {
                vmd.interactiveData = new InteractiveData();
                if (general._getInteractiveData()._getInteractiveType()!=null) {
                    vmd.interactiveData.interactiveType = general._getInteractiveData()._getInteractiveType()._getValue();
                }
            }
        }

        if (vmd.titleTypes == null) vmd.titleTypes = Collections.emptySet();
        if (vmd.episodeTypes == null) vmd.episodeTypes = Collections.emptySet();
        if (vmd.aliases == null) vmd.aliases = Collections.emptySet();
    }

//    private void populateGeneralOscar(long videoId, VideoMetaData vmd) {
//        if (!oscarDataset.movieExists(videoId)) {
//            Movie movie = oscarDataset.getMovie(videoId);
//            String origCountry = movie.getCountryOfOrigin();
//            if (origCountry != null) {
//                vmd.countryOfOrigin = constants.getISOCountry(origCountry);
//            }
//            String origLang = movie.getOriginalLanguageBcpCode();
//            vmd.countryOfOriginNameLocale = new NFLocale(movie.getOriginalTitleBcpCode().replace('-', '_'));
//            if (origLang != null) {
//                vmd.originalLanguageBcp47code = new Strings(origLang);
//            }
//
//            // TODO: remove this or wrap with FP check if alias is deprecated, depending on whether/when that PR is merged and acted upon
//            vmd.aliases = oscarDataset.getMovieTitleAkas(videoId)
//                    .map(mta->new Strings(mta.getAlias()))
//                    .collect(Collectors.toSet());
//
//            vmd.titleTypes = oscarDataset.getSetStringsFromMovieExtensions(videoId,OscarDataset.MovieExtensionAttributeName.TEST_TITLE_TYPE);
//            vmd.episodeTypes = oscarDataset.getSetStringsFromMovieExtensions(videoId,OscarDataset.MovieExtensionAttributeName.EPISODE_TYPE);
//
//            vmd.isTestTitle = movie.getTestTitle();
//            vmd.metadataReleaseDays = OutputUtil.getNullableInteger(movie.getMetadataReleaseDays());
//
//            vmd.displayRuntimeInSeconds = OutputUtil.getNullableInteger(movie.getDisplayRunLengthBoxed());
//            if (movie.getInteractive()) {
//                vmd.interactiveData = new InteractiveData();
//                if (movie.getInteractiveType()!=null) {
//                    vmd.interactiveData.interactiveType = movie.getInteractiveType();
//                }
//            }
//
//        }
//    }

    private void populateDates(Integer videoId, String countryCode, VideoMetaDataRollupValues rollup, Status rights, VideoMetaDataCountrySpecificDataKey vmd) {
        HollowHashIndexResult dateResult = videoDateIdx.findMatches((long) videoId, countryCode);
        if (dateResult != null) {
            int ordinal = dateResult.iterator().next();
            VideoDateWindowHollow dateWindow = api.getVideoDateWindowHollow(ordinal);

            List<ReleaseDateHollow> releaseDates = dateWindow._getReleaseDates();
            if (releaseDates != null) {
                for (ReleaseDateHollow releaseDate : releaseDates) {
                    String releaseDateType = releaseDate._getReleaseDateType()._getValue();
                    if (releaseDateType.equals(VideoDateUtil.ReleaseDateType.Theatrical.toString())) {
                        vmd.isTheatricalRelease = true;
                        vmd.theatricalReleaseDate = VideoDateUtil.convertToHollowOutputDate(releaseDate);
                    } else if (releaseDateType.equals(VideoDateUtil.ReleaseDateType.Broadcast.toString())) {
                        vmd.broadcastYear = releaseDate._getYear();
                        vmd.broadcastReleaseDate = VideoDateUtil.convertToHollowOutputDate(releaseDate);
                        StringHollow distributorName = releaseDate._getDistributorName();
                        if (distributorName != null)
                            vmd.broadcastDistributorName = new Strings(distributorName._getValue());
                    }
                }
            }
        }

        populateVideoMetaDataCountrySpecificDataKeyYears(videoId,vmd);

//        if (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) {
//            populateVideoMetaDataCountrySpecificDataKeyYearsOscar(videoId,vmd);
//        } else {
//            populateVideoMetaDataCountrySpecificDataKeyYears(videoId,vmd);
//        }

    }

    private void populateVideoMetaDataCountrySpecificDataKeyYears(long videoId, VideoMetaDataCountrySpecificDataKey vmd) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal(videoId);
        if (ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
            vmd.year = (int) general._getFirstReleaseYear();
            vmd.latestYear = vmd.year;
        }
    }

//    private void populateVideoMetaDataCountrySpecificDataKeyYearsOscar(long videoId, VideoMetaDataCountrySpecificDataKey vmd) {
//        oscarDataset.execWithMovieIfExists(videoId,(movie)->{
//            vmd.year = movie.getFirstReleaseYear();
//            vmd.latestYear = vmd.year;
//        });
//    }

    private void populateRoleLists(Integer videoId, VideoMetaData vmd) {
        Map<VRole, List<SequencedVPerson>> roles = new HashMap<>();
        HollowHashIndexResult personMatches = personVideoIdx.findMatches((long) videoId);
        if (personMatches != null) {
            HollowOrdinalIterator iter = personMatches.iterator();

            int personOrdinal = iter.next();
            while (personOrdinal != NO_MORE_ORDINALS) {
                PersonVideoHollow person = api.getPersonVideoHollow(personOrdinal);

                long personId = person._getPersonId();

                HollowHashIndexResult roleMatches = personVideoRoleIdx.findMatches(personId, (long) videoId);
                HollowOrdinalIterator roleIter = roleMatches.iterator();

                int roleOrdinal = roleIter.next();
                while (roleOrdinal != NO_MORE_ORDINALS) {
                    PersonVideoRoleHollow role = api.getPersonVideoRoleHollow(roleOrdinal);

                    VRole vRole = new VRole(role._getRoleTypeId());
                    List<SequencedVPerson> list = roles.get(vRole);
                    if (list == null) {
                        list = new ArrayList<>();
                        roles.put(vRole, list);
                    }
                    VPerson vPerson = new VPerson((int) personId);
                    list.add(new SequencedVPerson(vPerson, role._getSequenceNumber()));

                    roleOrdinal = roleIter.next();
                }

                personOrdinal = iter.next();
            }
        }

        vmd.roles = getSortedRoleMap(roles);
        vmd.actorList = getRoles(ACTOR_ROLE_ID, vmd.roles);
        vmd.directorList = getRoles(DIRECTOR_ROLE_ID, vmd.roles);
        vmd.creatorList = getRoles(CREATOR_ROLE_ID, vmd.roles);
    }

    private static class SequencedVPerson implements Comparable<SequencedVPerson> {
        private final VPerson person;
        private final int sequenceNumber;

        public SequencedVPerson(VPerson person, int sequenceNumber) {
            this.person = person;
            this.sequenceNumber = sequenceNumber;
        }

        public VPerson getPerson() {
            return person;
        }

        @Override
        public int compareTo(SequencedVPerson other) {
            return Integer.compare(sequenceNumber, other.sequenceNumber);
        }
    }

    private Map<VRole, List<VPerson>> getSortedRoleMap(Map<VRole, List<SequencedVPerson>> sequencedMap) {
        Map<VRole, List<VPerson>> map = new HashMap<>();

        for (Map.Entry<VRole, List<SequencedVPerson>> entry : sequencedMap.entrySet()) {
            Collections.sort(entry.getValue());
            List<VPerson> personList = new ArrayList<>();
            for (SequencedVPerson person : entry.getValue()) {
                personList.add(person.getPerson());
            }
            map.put(entry.getKey(), personList);
        }

        return map;
    }

    private List<VPerson> getRoles(int roleId, Map<VRole, List<VPerson>> roles) {
        VRole vRole = new VRole(roleId);
        List<VPerson> list = roles.get(vRole);
        if (list != null) return list;

        return Collections.emptyList();
    }

    private void populateHooks(Integer videoId, VideoMetaData vmd) {
        int storiesSynopsesOrdinal = storiesSynopsesIdx.getMatchingOrdinal((long) videoId);

        if (storiesSynopsesOrdinal != -1) {
            StoriesSynopsesHollow synopses = api.getStoriesSynopsesHollow(storiesSynopsesOrdinal);

            List<Hook> hooks = new ArrayList<Hook>();

            for (StoriesSynopsesHookHollow hook : synopses._getHooks()) {
                String type = hook._getType()._getValue();
                int rank = Integer.parseInt(hook._getRank()._getValue());

                Hook outputHook = new Hook();
                outputHook.type = hookTypeMap.get(type);
                outputHook.rank = rank;
                outputHook.video = new Video(videoId);

                hooks.add(outputHook);
            }

            vmd.hooks = hooks;
        } else {
            vmd.hooks = Collections.emptyList();
        }
    }

    public boolean hasNewContent(VideoMetaDataRollupValues rollup) {
        if (rollup.doShow()) {
            if (daysAgo(rollup.getEarliestFirstDisplayDate()) > newContentFlagDuration && daysAgo(rollup.getLatestLiveFirstDisplayDate()) <= newContentFlagDuration)
                return true;
        }

        return false;
    }

    private int daysAgo(long timestamp) {
        return (int) ((ctx.getNowMillis() - timestamp) / (24 * 60 * 60 * 1000));
    }


}
