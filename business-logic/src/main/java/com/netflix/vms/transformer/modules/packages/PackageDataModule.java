package com.netflix.vms.transformer.modules.packages;

import com.netflix.vms.transformer.hollowoutput.DashStreamBoxInfo;

import com.netflix.vms.transformer.hollowinput.StreamBoxInfoHollow;
import com.netflix.vms.transformer.hollowinput.SetOfStreamBoxInfoHollow;
import com.netflix.vms.transformer.hollowinput.DashStreamHeaderDataHollow;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.hollowinput.ChunkDurationsStringHollow;
import com.netflix.vms.transformer.hollowinput.CodecPrivateDataStringHollow;
import com.netflix.vms.transformer.hollowinput.DeployablePackagesHollow;
import com.netflix.vms.transformer.hollowinput.DrmHeaderInfoHollow;
import com.netflix.vms.transformer.hollowinput.DrmHeaderInfoListHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.ListOfPackageTagsHollow;
import com.netflix.vms.transformer.hollowinput.PackageDrmInfoHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoStreamInfoHollow;
import com.netflix.vms.transformer.hollowoutput.ChunkDurationsString;
import com.netflix.vms.transformer.hollowoutput.CodecPrivateDataString;
import com.netflix.vms.transformer.hollowoutput.DownloadableId;
import com.netflix.vms.transformer.hollowoutput.DrmHeader;
import com.netflix.vms.transformer.hollowoutput.DrmInfo;
import com.netflix.vms.transformer.hollowoutput.DrmInfoData;
import com.netflix.vms.transformer.hollowoutput.DrmKey;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.FileEncodingData;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import com.netflix.vms.transformer.hollowoutput.WmDrmKey;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.contracts.ContractRestrictionModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;

public class PackageDataModule {

    static final int WMDRMKEY_GROUP = 1;

    private final VMSHollowInputAPI api;
    private final HollowObjectMapper mapper;
    private final CycleConstants cycleConstants;
    private final HollowPrimaryKeyIndex streamProfileIdx;
    private final HollowHashIndex packagesByVideoIdx;
    private final HollowPrimaryKeyIndex deployablePackagesIdx;

    private final Map<Integer, Object> drmKeysByGroupId;
    private final Map<Integer, DrmInfo> drmInfoByGroupId;

    private final StreamDataModule streamDataModule;
    private final ContractRestrictionModule contractRestrictionModule;
    private final EncodeSummaryDescriptorModule encodeSummaryModule;
    private final TransformerContext ctx;

    public PackageDataModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper objectMapper, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        this.api = api;
        this.ctx = ctx;
        this.mapper = objectMapper;
        this.cycleConstants = cycleConstants;
        this.packagesByVideoIdx = indexer.getHashIndex(IndexSpec.PACKAGES_BY_VIDEO);
        this.deployablePackagesIdx = indexer.getPrimaryKeyIndex(IndexSpec.DEPLOYABLE_PACKAGES);
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.drmKeysByGroupId = new HashMap<Integer, Object>();
        this.drmInfoByGroupId = new HashMap<Integer, DrmInfo>();

        this.streamDataModule = new StreamDataModule(api, cycleConstants, indexer, objectMapper, drmKeysByGroupId, drmInfoByGroupId);
        this.contractRestrictionModule = new ContractRestrictionModule(api, ctx, cycleConstants, indexer);
        this.encodeSummaryModule = new EncodeSummaryDescriptorModule(api, indexer);
    }

    public Map<Integer, VideoPackageData> transform(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, Set<Integer> extraVideoIds) {
        Map<Integer, VideoPackageData> transformedPackages = new HashMap<Integer, VideoPackageData>();

        Set<Integer> videoIds = gatherVideoIds(showHierarchiesByCountry, extraVideoIds);
        for(Integer videoId : videoIds) {
            HollowHashIndexResult packagesForVideo = packagesByVideoIdx.findMatches((long)videoId);

            if(packagesForVideo != null) {
                HollowOrdinalIterator iter = packagesForVideo.iterator();

                VideoPackageData videoPackageData = new VideoPackageData();
                videoPackageData.videoId = new Video(videoId);
                videoPackageData.packages = new HashSet<PackageData>();

                int packageOrdinal = iter.next();
                while(packageOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    drmKeysByGroupId.clear();
                    drmInfoByGroupId.clear();

                    PackageHollow packages = api.getPackageHollow(packageOrdinal);
                    populateDrmKeysByGroupId(packages, videoId);
                    PackageData transformedPackage = convertPackage(packages);
                    if (transformedPackage != null) {
                        videoPackageData.packages.add(transformedPackage);
                    }

                    packageOrdinal = iter.next();
                }

                transformedPackages.put(videoId, videoPackageData);

                mapper.add(videoPackageData);
            }
        }

        return transformedPackages;
    }

    private void populateDrmKeysByGroupId(PackageHollow packageInput, Integer videoId) {
        for(PackageDrmInfoHollow inputDrmInfo : packageInput._getDrmInfo()) {
            int drmKeyGroup = (int)inputDrmInfo._getDrmKeyGroup();
            if(drmKeyGroup == WMDRMKEY_GROUP) {
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
                drmInfo.drmHeaders = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, DrmHeader>();
                DrmHeaderInfoListHollow drmHeaderInfo = inputDrmInfo._getDrmHeaderInfo();
                if(drmHeaderInfo != null) {
                    for(DrmHeaderInfoHollow header : drmHeaderInfo) {
                        DrmHeader outputHeader = new DrmHeader();

                        StringHollow checksum = header._getChecksum();
                        if(checksum != null)
                            outputHeader.checksum = DatatypeConverter.parseHexBinary(checksum._getValue()); // not correct

                        outputHeader.drmSystemId = (int)header._getDrmSystemId();
                        outputHeader.keyId = DatatypeConverter.parseHexBinary(header._getKeyId()._getValue());
                        outputHeader.attributes = Collections.emptyMap();
                        drmInfo.drmHeaders.put(new com.netflix.vms.transformer.hollowoutput.Integer(outputHeader.drmSystemId), outputHeader);
                    }
                }
                drmInfoByGroupId.put(Integer.valueOf(drmKeyGroup), drmInfo);
            }
        }
    }

    private PackageData convertPackage(PackageHollow packages) {
        int deployablePackagesOrdinal = deployablePackagesIdx.getMatchingOrdinal(packages._getPackageId());
        if (deployablePackagesOrdinal == -1) return null; // Pre-condition, package must exist in deployablePackagesFeed

        PackageData pkg = new PackageData();
        pkg.id = (int)packages._getPackageId();
        pkg.video = new Video((int)packages._getMovieId());
        pkg.isPrimaryPackage = true;

        /// also create the DrmInfoData while iterating over the streams
        DrmInfoData drmInfoData = new DrmInfoData();
        drmInfoData.packageId = pkg.id;
        drmInfoData.downloadableIdToDrmInfoMap = new HashMap<DownloadableId, DrmInfo>();


        /////////// CONTRACT RESTRICTIONS /////////////////

        pkg.contractRestrictions = contractRestrictionModule.getContractRestrictions(packages);

        /////////// STREAMS ///////////
        pkg.streams = new HashSet<StreamData>();

        for(PackageStreamHollow inputStream : packages._getDownloadables()) {
            StreamData outputStream = streamDataModule.convertStreamData(packages, inputStream, drmInfoData);

            if(outputStream != null)
                pkg.streams.add(outputStream);

            addFileEncodingDataForStream(inputStream);
        }
        calculateRuntimeInSeconds(pkg);

        //////////// DEPLOYABLE PACKAGES //////////////
        pkg.tags = new ArrayList<Strings>();
        pkg.packageTags = new HashSet<Strings>();
        if(deployablePackagesOrdinal != -1) {
            pkg.allDeployableCountries = new HashSet<ISOCountry>();
            DeployablePackagesHollow deployablePackages = api.getDeployablePackagesHollow(deployablePackagesOrdinal);
            pkg.isDefaultPackage = deployablePackages._getDefaultPackage();
            ListOfPackageTagsHollow packageTags = deployablePackages._getTags();
            if(packageTags != null) {
                for(StringHollow tag : packageTags) {
                    ctx.getLogger().info(TransformerLogTag.InteractivePackage, "package={}, video={}, tag={}", pkg.id, pkg.video.value, tag._getValue());
                    pkg.tags.add(new Strings(tag._getValue()));
                    pkg.packageTags.add(new Strings(tag._getValue()));
                }
            }
            for(ISOCountryHollow isoCountry : deployablePackages._getCountryCodes()) {
                pkg.allDeployableCountries.add(cycleConstants.getISOCountry(isoCountry._getValue()));
            }
        }

        //////////// ENCODE SUMMARY DESCRIPTORS /////////////////

        encodeSummaryModule.summarize(pkg);

        mapper.add(drmInfoData);

        return pkg;
    }

    
    private void calculateRuntimeInSeconds(PackageData packageData) {
        long longestRuntimeInSeconds = 0;

        for(StreamData streamData : packageData.streams) {
            int encodingProfileId = streamData.downloadDescriptor.encodingProfileId;
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long) encodingProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            if("VIDEO".equals(streamProfileType) && streamData.streamDataDescriptor.runTimeInSeconds > longestRuntimeInSeconds ) {
                longestRuntimeInSeconds = streamData.streamDataDescriptor.runTimeInSeconds;
            }
        }

        packageData.runtimeInSeconds = (int) longestRuntimeInSeconds;
    }

    private void addFileEncodingDataForStream(PackageStreamHollow inputStream) {
        StreamNonImageInfoHollow nonImageInfo = inputStream._getNonImageInfo();
        if(nonImageInfo != null) {
            FileEncodingData encodingData = new FileEncodingData();
            encodingData.downloadableId = new DownloadableId(inputStream._getDownloadableId());
            CodecPrivateDataStringHollow codecPrivateData = nonImageInfo._getCodecPrivateData();
            ChunkDurationsStringHollow chunkDurations = nonImageInfo._getChunkDurations();

            if(codecPrivateData != null)
                encodingData.codecPrivateData = new CodecPrivateDataString(codecPrivateData._getValue());
            if(chunkDurations != null)
                encodingData.chunkDurations = new ChunkDurationsString(chunkDurations._getValue());

            VideoStreamInfoHollow videoInfo = nonImageInfo._getVideoInfo();
            if(videoInfo != null) {
                encodingData.dashHeaderSize = videoInfo._getDashHeaderSize();
                encodingData.dashMediaStartByteOffset = videoInfo._getDashMediaStartByteOffset();
                DashStreamHeaderDataHollow dashHeaderData = videoInfo._getDashStreamHeaderData();
                if(dashHeaderData != null) {
                    SetOfStreamBoxInfoHollow setOfBoxInfos = dashHeaderData._getBoxInfo();
                    if(setOfBoxInfos != null) {
                        encodingData.dashStreamBoxInfo = new HashSet<>();
                        
                        for(StreamBoxInfoHollow boxInfo : setOfBoxInfos) {
                            if(boxInfo._getKey() != null) {
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

            if(encodingData.codecPrivateData != null || encodingData.chunkDurations != null
                    || encodingData.dashHeaderSize != Long.MIN_VALUE || encodingData.dashMediaStartByteOffset != Long.MIN_VALUE
                    || (encodingData.dashStreamBoxInfo != null && !encodingData.dashStreamBoxInfo.isEmpty()))
                mapper.add(encodingData);
        }
    }

    private Set<Integer> gatherVideoIds(Map<String, Set<VideoHierarchy>> showHierarchyByCountry, Set<Integer> extraVideoIds) {
        if (showHierarchyByCountry == null) return extraVideoIds;

        Set<Integer> videoIds = new HashSet<Integer>(extraVideoIds);
        for(Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchyByCountry.entrySet()) {
            for(VideoHierarchy showHierarchy : entry.getValue()) {
                videoIds.add(showHierarchy.getTopNodeId());

                for(int i=0;i<showHierarchy.getSeasonIds().length;i++) {
                    videoIds.add(showHierarchy.getSeasonIds()[i]);

                    for(int j=0;j<showHierarchy.getEpisodeIds()[i].length;j++) {
                        videoIds.add(showHierarchy.getEpisodeIds()[i][j]);
                    }
                }

                for(int i=0;i<showHierarchy.getSupplementalIds().length;i++) {
                    videoIds.add(showHierarchy.getSupplementalIds()[i]);
                }
            }
        }

        return videoIds;
    }


}
