package com.netflix.vms.transformer.modules.packages;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.google.common.annotations.VisibleForTesting;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.data.CupTokenFetcher;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.hollowinput.ChunkDurationsStringHollow;
import com.netflix.vms.transformer.hollowinput.CodecPrivateDataStringHollow;
import com.netflix.vms.transformer.hollowinput.DashStreamHeaderDataHollow;
import com.netflix.vms.transformer.hollowinput.DrmHeaderInfoHollow;
import com.netflix.vms.transformer.hollowinput.DrmHeaderInfoListHollow;
import com.netflix.vms.transformer.hollowinput.ListOfPackageTagsHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.PackageDrmInfoHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageMovieDealCountryGroupHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.SetOfStreamBoxInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamBoxInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TimecodeAnnotationsListHollow;
import com.netflix.vms.transformer.hollowinput.TimecodedMomentAnnotationHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoStreamInfoHollow;
import com.netflix.vms.transformer.hollowoutput.ChunkDurationsString;
import com.netflix.vms.transformer.hollowoutput.CodecPrivateDataString;
import com.netflix.vms.transformer.hollowoutput.ContractRestriction;
import com.netflix.vms.transformer.hollowoutput.DashStreamBoxInfo;
import com.netflix.vms.transformer.hollowoutput.DownloadableId;
import com.netflix.vms.transformer.hollowoutput.DrmHeader;
import com.netflix.vms.transformer.hollowoutput.DrmInfo;
import com.netflix.vms.transformer.hollowoutput.DrmInfoData;
import com.netflix.vms.transformer.hollowoutput.DrmKey;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.FileEncodingData;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TimecodeAnnotation;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.WmDrmKey;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.contracts.ContractRestrictionModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;

public class PackageDataModule {

    static final int WMDRMKEY_GROUP = 1;
	private static final String DEFAULT_ENCODING_ALGORITHM = "default";
    

    private final VMSHollowInputAPI api;
    private final HollowObjectMapper mapper;
    private final CycleConstants cycleConstants;
    private final HollowPrimaryKeyIndex streamProfileIdx;
    private final HollowPrimaryKeyIndex packageMovieDealCountryGroupIndex;
    private final HollowHashIndex packagesByVideoIdx;

    private final Map<Integer, Object> drmKeysByGroupId;
    private final Map<Integer, DrmInfo> drmInfoByGroupId;

    private Set<Integer> fourKProfileIds;
    private Set<Integer> hdrProfileIds;
    private Set<Integer> atmosStreamProfileIds;
    private Map<Integer, Strings> soundTypesMap;

    private final StreamDataModule streamDataModule;
    private final ContractRestrictionModule contractRestrictionModule;
    private final EncodeSummaryDescriptorModule encodeSummaryModule;
    private final TransformerContext ctx;

    public PackageDataModule(VMSHollowInputAPI api, TransformerContext ctx,
            HollowObjectMapper objectMapper, CycleConstants cycleConstants,
            VMSTransformerIndexer indexer, GatekeeperStatusRetriever statusRetriever, 
            CupTokenFetcher cupTokenFetcher) {
        this.api = api;
        this.ctx = ctx;
        this.mapper = objectMapper;
        this.cycleConstants = cycleConstants;
        this.packageMovieDealCountryGroupIndex =
                indexer.getPrimaryKeyIndex(IndexSpec.PACKAGE_MOVIE_DEAL_COUNTRY_GROUP);
        this.packagesByVideoIdx = indexer.getHashIndex(IndexSpec.PACKAGES_BY_VIDEO);
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.drmKeysByGroupId = new HashMap<>();
        this.drmInfoByGroupId = new HashMap<>();

        this.streamDataModule = new StreamDataModule(api, ctx, cycleConstants, indexer, objectMapper, drmKeysByGroupId, drmInfoByGroupId);
        this.contractRestrictionModule = new ContractRestrictionModule(api, ctx, cycleConstants, indexer, statusRetriever, cupTokenFetcher);
        this.encodeSummaryModule = new EncodeSummaryDescriptorModule(api, indexer);

        this.hdrProfileIds = getEncodingProfileIds(api, indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE_GROUP), "HDR");
        this.fourKProfileIds = getEncodingProfileIds(api, indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE_GROUP), "4K");
        this.atmosStreamProfileIds = getAtmosStreamProfileIds(api);
        this.soundTypesMap = getSoundTypesMap();
    }

    public void transform(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, Set<Integer> extraVideoIds, TransformedVideoData transformedVideoData) {

        Set<Integer> videoIds = gatherVideoIds(showHierarchiesByCountry, extraVideoIds);
        for (Integer videoId : videoIds) {
            try {
                HollowHashIndexResult packagesForVideo = packagesByVideoIdx.findMatches((long) videoId);
                if (packagesForVideo != null) {
                    HollowOrdinalIterator iter = packagesForVideo.iterator();
                    Set<PackageDataCollection> allPackageDataCollection = new HashSet<>();

                    int packageOrdinal = iter.next();
                    while (packageOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                        drmKeysByGroupId.clear();
                        drmInfoByGroupId.clear();
                        PackageHollow packages = api.getPackageHollow(packageOrdinal);
                        populateDrmKeysByGroupId(packages, videoId);
                        PackageDataCollection packageDataCollection = convertPackage(packages, videoId);
                        if (packageDataCollection != null) {
                            allPackageDataCollection.add(packageDataCollection);
                            mapper.add(packageDataCollection.getPackageData());
                        }
                        packageOrdinal = iter.next();
                    }

                    VideoPackageData videoPackageData = new VideoPackageData();
                    videoPackageData.videoId = new Video(videoId);
                    videoPackageData.packages = allPackageDataCollection.stream().map(c -> c.getPackageData()).collect(Collectors.toSet());

                    mapper.add(videoPackageData);
                    transformedVideoData.getTransformedPackageData(videoId).setPackageDataCollectionMap(allPackageDataCollection);
                }
            } catch (RuntimeException e) {
                throw new RuntimeException("Error transforming video " + videoId, e);
            }
        }
    }

    private void populateDrmKeysByGroupId(PackageHollow packageInput, Integer videoId) {
        for (PackageDrmInfoHollow inputDrmInfo : packageInput._getDrmInfo()) {
            int drmKeyGroup = (int) inputDrmInfo._getDrmKeyGroup();
            if (drmKeyGroup == WMDRMKEY_GROUP) {
                WmDrmKey wmDrmKey = new WmDrmKey();
                wmDrmKey.contentPackagerPublicKey = new DrmKeyString(inputDrmInfo._getContentPackagerPublicKey()._getValue());
                wmDrmKey.encryptedContentKey = new DrmKeyString(inputDrmInfo._getKeySeed()._getValue());
                drmKeysByGroupId.put(Integer.valueOf(drmKeyGroup), wmDrmKey);
            } else {
                DrmKey drmKey = new DrmKey();
                drmKey.keyId = inputDrmInfo._getKeyId();
                drmKey.encryptedContentKey = new DrmKeyString(inputDrmInfo._getKey()._getValue());
                drmKey.keyDecrypted = false;
                drmKey.videoId = new Video(videoId);
                drmKey.keyDecrypted = inputDrmInfo._getKeyDecrypted();
                drmKeysByGroupId.put(Integer.valueOf(drmKeyGroup), drmKey);

                DrmInfo drmInfo = new DrmInfo();
                drmInfo.drmKeyGroup = drmKeyGroup;
                drmInfo.drmKey = drmKey;
                drmInfo.drmHeaders = new HashMap<>();
                DrmHeaderInfoListHollow drmHeaderInfo = inputDrmInfo._getDrmHeaderInfo();
                if (drmHeaderInfo != null) {
                    for (DrmHeaderInfoHollow header : drmHeaderInfo) {
                        DrmHeader outputHeader = new DrmHeader();

                        StringHollow checksum = header._getChecksum();
                        if (checksum != null)
                            outputHeader.checksum = DatatypeConverter.parseHexBinary(checksum._getValue()); // not correct

                        outputHeader.drmSystemId = (int) header._getDrmSystemId();
                        outputHeader.keyId = DatatypeConverter.parseHexBinary(header._getKeyId()._getValue());
                        outputHeader.attributes = Collections.emptyMap();
                        drmInfo.drmHeaders.put(new com.netflix.vms.transformer.hollowoutput.Integer(outputHeader.drmSystemId), outputHeader);
                    }
                }
                drmInfoByGroupId.put(Integer.valueOf(drmKeyGroup), drmInfo);
            }
        }
    }

    @VisibleForTesting
    PackageDataCollection convertPackage(PackageHollow packages, int videoId) {
        int packageMovieDealCountryGroupOrdinal = packageMovieDealCountryGroupIndex.getMatchingOrdinal(
                (long) videoId, packages._getPackageId());
        if (packageMovieDealCountryGroupOrdinal == ORDINAL_NONE) {
            return null; // package must exist in packageMovieDealCountryGroup feed
        }

        PackageDataCollection packageDataCollection = new PackageDataCollection(ctx, fourKProfileIds, hdrProfileIds, atmosStreamProfileIds, soundTypesMap, cycleConstants);
        PackageData pkg = packageDataCollection.getPackageData();

        pkg.id = (int) packages._getPackageId();
        pkg.video = new Video((int) packages._getMovieId());
        pkg.isPrimaryPackage = true;
        
        pkg.recipeGroups = new ArrayList<>();
        ListOfStringHollow recipeGroups = packages._getRecipeGroups();
        if(recipeGroups != null) {
            for(StringHollow recipeGroup : recipeGroups) {
                pkg.recipeGroups.add(new Strings(recipeGroup._getValue()));
            }
        }
        
        /// also create the DrmInfoData while iterating over the streams
        DrmInfoData drmInfoData = new DrmInfoData();
        drmInfoData.packageId = pkg.id;
        drmInfoData.downloadableIdToDrmInfoMap = new HashMap<>();
        
        // Fill in timecodes for this package
        List<TimecodeAnnotation> timecodes = new ArrayList<>();
        TimecodeAnnotationsListHollow inputTimecodes = packages._getTimecodeAnnotations();
        if(inputTimecodes != null) {
        	for(TimecodedMomentAnnotationHollow inputTimecode : inputTimecodes) {
        		TimecodeAnnotation timecode = new TimecodeAnnotation();
        		timecode.type = inputTimecode._getType()._getValue().toCharArray();
        		timecode.startMillis = inputTimecode._getStartMillis();
        		timecode.endMillis = inputTimecode._getEndMillis();
        		StringHollow algo = inputTimecode._getEncodingAlgorithmHash();
        		if(algo != null)
        			timecode.encodingAlgorithmHash = new Strings(algo._getValue());
        		else
        			timecode.encodingAlgorithmHash = new Strings(DEFAULT_ENCODING_ALGORITHM);
        		timecodes.add(timecode);
        	}
        }
        pkg.timecodes = timecodes;


        /////////// CONTRACT RESTRICTIONS /////////////////

        pkg.contractRestrictions = contractRestrictionModule.getContractRestrictions(packages);
        Map<ISOCountry, Set<DownloadableId>> excludedDownloadables = getExcludedDownloadablesByCountry(pkg.contractRestrictions);

        /////////// STREAMS ///////////
        pkg.streams = new HashSet<>();

        for (PackageStreamHollow inputStream : packages._getDownloadables()) {
            StreamData outputStream = streamDataModule.convertStreamData(packages, inputStream, drmInfoData);

            if (outputStream != null) {
                pkg.streams.add(outputStream);
                int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long) outputStream.downloadDescriptor.encodingProfileId);
                StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
                packageDataCollection.processStreamData(outputStream, profile, excludedDownloadables, videoId, inputStream);
            }

            addFileEncodingDataForStream(inputStream);
        }
        calculateRuntimeInSeconds(pkg);

        //////////// DEPLOYABLE PACKAGES //////////////
        PackageMovieDealCountryGroupHollow packageMovieDealCountryGroup =
                api.getPackageMovieDealCountryGroupHollow(packageMovieDealCountryGroupOrdinal);
        pkg.tags = new ArrayList<>();
        pkg.packageTags = new HashSet<>();
        pkg.allDeployableCountries = new HashSet<>();
        pkg.isDefaultPackage = packageMovieDealCountryGroup._getDefaultPackage();
        ListOfPackageTagsHollow packageTags = packageMovieDealCountryGroup._getTags();
        if (packageTags != null) {
            for (StringHollow tag : packageTags) {
                ctx.getLogger().info(TransformerLogTag.InteractivePackage, "package={}, video={}, tag={}", pkg.id, pkg.video.value, tag._getValue());
                pkg.tags.add(new Strings(tag._getValue()));
                pkg.packageTags.add(new Strings(tag._getValue()));
            }
        }
        Set<String> countries = new HashSet<>();
        if (packageMovieDealCountryGroup != null && packageMovieDealCountryGroup._getDealCountryGroups() != null) {
            packageMovieDealCountryGroup._getDealCountryGroups().forEach(deal -> {
                if (deal._getCountryWindow() != null) {
                    deal._getCountryWindow().forEach((country, deployable) -> {
                        if (deployable._getValue()) {
                            countries.add(country._getValue());
                        }
                    });
                }
            });
        }
        if (countries.isEmpty()) {
            return null; // no deployable countries, we want to drop the PackageDataCollection
        }
        countries.forEach(c -> pkg.allDeployableCountries.add(cycleConstants.getISOCountry(c)));

        //////////// ENCODE SUMMARY DESCRIPTORS /////////////////

        encodeSummaryModule.summarize(pkg);
        
        mapper.add(drmInfoData);

        return packageDataCollection;
    }


    private void calculateRuntimeInSeconds(PackageData packageData) {
        long longestRuntimeInSeconds = 0;

        for (StreamData streamData : packageData.streams) {
            int encodingProfileId = streamData.downloadDescriptor.encodingProfileId;
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long) encodingProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            if ("VIDEO".equals(streamProfileType) && streamData.streamDataDescriptor.runTimeInSeconds > longestRuntimeInSeconds) {
                longestRuntimeInSeconds = streamData.streamDataDescriptor.runTimeInSeconds;
            }
        }

        packageData.runtimeInSeconds = (int) longestRuntimeInSeconds;
    }

    private void addFileEncodingDataForStream(PackageStreamHollow inputStream) {
        StreamNonImageInfoHollow nonImageInfo = inputStream._getNonImageInfo();
        if (nonImageInfo != null) {
            FileEncodingData encodingData = new FileEncodingData();
            encodingData.downloadableId = new DownloadableId(inputStream._getDownloadableId());
            CodecPrivateDataStringHollow codecPrivateData = nonImageInfo._getCodecPrivateData();
            ChunkDurationsStringHollow chunkDurations = nonImageInfo._getChunkDurations();

            if (codecPrivateData != null)
                encodingData.codecPrivateData = new CodecPrivateDataString(codecPrivateData._getValue());
            if (chunkDurations != null)
                encodingData.chunkDurations = new ChunkDurationsString(chunkDurations._getValue());

            VideoStreamInfoHollow videoInfo = nonImageInfo._getVideoInfo();
            if (videoInfo != null) {
                encodingData.dashHeaderSize = videoInfo._getDashHeaderSize();
                encodingData.dashMediaStartByteOffset = videoInfo._getDashMediaStartByteOffset();
                DashStreamHeaderDataHollow dashHeaderData = videoInfo._getDashStreamHeaderData();
                if (dashHeaderData != null) {
                    SetOfStreamBoxInfoHollow setOfBoxInfos = dashHeaderData._getBoxInfo();
                    if (setOfBoxInfos != null) {
                        encodingData.dashStreamBoxInfo = new HashSet<>();

                        for (StreamBoxInfoHollow boxInfo : setOfBoxInfos) {
                            if (boxInfo._getKey() != null) {
                                DashStreamBoxInfo dashStreamBoxInfo = new DashStreamBoxInfo();
                                dashStreamBoxInfo.key = boxInfo._getKey()._getValue();
                                dashStreamBoxInfo.offset = boxInfo._getBoxOffset();
                                dashStreamBoxInfo.size = boxInfo._getBoxSize();
                                encodingData.dashStreamBoxInfo.add(dashStreamBoxInfo);
                            }
                        }
                    }
                }
            }

            if (encodingData.codecPrivateData != null || encodingData.chunkDurations != null
                    || encodingData.dashHeaderSize != Long.MIN_VALUE || encodingData.dashMediaStartByteOffset != Long.MIN_VALUE
                    || (encodingData.dashStreamBoxInfo != null && !encodingData.dashStreamBoxInfo.isEmpty()))
                mapper.add(encodingData);
        }
    }

    private Set<Integer> gatherVideoIds(Map<String, Set<VideoHierarchy>> showHierarchyByCountry, Set<Integer> extraVideoIds) {
        if (showHierarchyByCountry == null) return extraVideoIds;

        Set<Integer> videoIds = new HashSet<Integer>(extraVideoIds);
        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchyByCountry.entrySet()) {
            for (VideoHierarchy showHierarchy : entry.getValue()) {
                videoIds.add(showHierarchy.getTopNodeId());

                for (int i = 0; i < showHierarchy.getSeasonIds().length; i++) {
                    videoIds.add(showHierarchy.getSeasonIds()[i]);

                    for (int j = 0; j < showHierarchy.getEpisodeIds()[i].length; j++) {
                        videoIds.add(showHierarchy.getEpisodeIds()[i][j]);
                    }
                }

                for (int i = 0; i < showHierarchy.getSupplementalIds().length; i++) {
                    videoIds.add(showHierarchy.getSupplementalIds()[i]);
                }
            }
        }

        return videoIds;
    }

    private Set<Integer> getAtmosStreamProfileIds(VMSHollowInputAPI api) {
        return api.getAllStreamProfilesHollow().stream().filter(profile -> profile._getDescription()._getValue().toLowerCase().contains("atmos"))
                .map(profile -> (int) profile._getId()).collect(Collectors.toSet());
    }

    private Set<Integer> getEncodingProfileIds(VMSHollowInputAPI api, HollowPrimaryKeyIndex index, String type) {
        int ordinal = index.getMatchingOrdinal(type);
        if (ordinal == ORDINAL_NONE) {
            return new HashSet<>();
        }
        return api.getStreamProfileGroupsHollow(ordinal)._getStreamProfileIds().stream()
            .map(id -> (int) id._getValue()).collect(Collectors.toSet());
    }

    private Map<Integer, Strings> getSoundTypesMap() {
        Map<Integer, Strings> map = new HashMap<>();
        map.put(1, new Strings("1.0"));
        map.put(2, new Strings("2.0"));
        map.put(6, new Strings("5.1"));
        map.put(8, new Strings("8.1"));
        return map;
    }

    private Map<ISOCountry, Set<DownloadableId>> getExcludedDownloadablesByCountry(Map<ISOCountry, Set<ContractRestriction>> contractRestrictions) {
        Map<ISOCountry, Set<DownloadableId>> excludedDownloadablesByCountry = new HashMap<>();
        Set<ISOCountry> countries = contractRestrictions.keySet();
        for (ISOCountry country : countries) {
            Set<ContractRestriction> countryContractRestrictions = contractRestrictions.get(country);
            long now = ctx.getNowMillis();
            Set<DownloadableId> nextExcludedDownloadables = Collections.emptySet();
            long nextStartDate = Long.MAX_VALUE;

            for (ContractRestriction restriction : countryContractRestrictions) {
                if (now > restriction.availabilityWindow.startDate.val && now < restriction.availabilityWindow.endDate.val) {
                    excludedDownloadablesByCountry.putIfAbsent(country, restriction.excludedDownloadables);
                    break;
                } else if (now < restriction.availabilityWindow.startDate.val) {
                    if (nextStartDate > restriction.availabilityWindow.startDate.val) {
                        nextStartDate = restriction.availabilityWindow.startDate.val;
                        nextExcludedDownloadables = restriction.excludedDownloadables;
                    }
                }
            }
            excludedDownloadablesByCountry.putIfAbsent(country, nextExcludedDownloadables);
        }
        return excludedDownloadablesByCountry;
    }

}
