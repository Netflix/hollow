package com.netflix.vms.transformer.modules.packages;

import com.netflix.config.FastProperty;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.hollowinput.AudioStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.ImageStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamAssetMetadataHollow;
import com.netflix.vms.transformer.hollowinput.StreamAssetTypeHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentLabelHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamFileIdentificationHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TextStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoStreamCropParamsHollow;
import com.netflix.vms.transformer.hollowinput.VideoStreamInfoHollow;
import com.netflix.vms.transformer.hollowoutput.AssetMetaData;
import com.netflix.vms.transformer.hollowoutput.AssetTypeDescriptor;
import com.netflix.vms.transformer.hollowoutput.DownloadDescriptor;
import com.netflix.vms.transformer.hollowoutput.DownloadLocation;
import com.netflix.vms.transformer.hollowoutput.DownloadLocationSet;
import com.netflix.vms.transformer.hollowoutput.DownloadableId;
import com.netflix.vms.transformer.hollowoutput.DrmHeader;
import com.netflix.vms.transformer.hollowoutput.DrmInfo;
import com.netflix.vms.transformer.hollowoutput.DrmInfoData;
import com.netflix.vms.transformer.hollowoutput.DrmKey;
import com.netflix.vms.transformer.hollowoutput.FrameRate;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.ImageSubtitleIndexByteRange;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.QoEInfo;
import com.netflix.vms.transformer.hollowoutput.StreamAdditionalData;
import com.netflix.vms.transformer.hollowoutput.StreamCropParams;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.StreamDataDescriptor;
import com.netflix.vms.transformer.hollowoutput.StreamDownloadLocationFilename;
import com.netflix.vms.transformer.hollowoutput.StreamDrmData;
import com.netflix.vms.transformer.hollowoutput.StreamMostlyConstantData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TargetDimensions;
import com.netflix.vms.transformer.hollowoutput.TimedTextTypeDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import com.netflix.vms.transformer.hollowoutput.WmDrmKey;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StreamDataModule {
    private static final String DEFAULT_ENCODING_ALGORITHM = "default";

    public static final VideoFormatDebugMap debugVideoFormatMap = new VideoFormatDebugMap();
    public static final DebugLogConfig debugLogConfig = new DebugLogConfig();
    public static final int PLAYREADY_SYSTEM = 1;

    private final StreamDrmData EMPTY_DRM_DATA = new StreamDrmData();
    private final DownloadLocationSet EMPTY_DOWNLOAD_LOCATIONS = new DownloadLocationSet();

    private final Map<String, AssetTypeDescriptor> assetTypeDescriptorMap;
    private final Map<String, TimedTextTypeDescriptor> timedTextTypeDescriptorMap;
    private final Map<String, Integer> deploymentLabelBitsetOffsetMap;
    private final Map<String, List<Strings>> tagsLists;
    private final Map<Integer, Object> drmKeysByGroupId;
    private final Map<Integer, DrmInfo> drmInfoByGroupId;

    private final VideoFormatDescriptorIdentifier videoFormatIdentifier;
    private final HollowPrimaryKeyIndex streamProfileIdx;
    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final CycleConstants cycleConstants;
    private final HollowObjectMapper objectMapper;

    public StreamDataModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, VMSTransformerIndexer indexer, HollowObjectMapper objectMapper, Map<Integer, Object> drmKeysByGroupId, Map<Integer, DrmInfo> drmInfoByGroupId) {
        this.api = api;
        this.ctx = ctx;
        this.cycleConstants = cycleConstants;
        this.assetTypeDescriptorMap = getAssetTypeDescriptorMap();
        this.timedTextTypeDescriptorMap = getTimedTextTypeDescriptorMap();
        this.deploymentLabelBitsetOffsetMap = getDeploymentLabelBitsetOffsetMap();
        this.tagsLists = new HashMap<>();
        this.drmKeysByGroupId = drmKeysByGroupId;
        this.drmInfoByGroupId = drmInfoByGroupId;

        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.videoFormatIdentifier = new VideoFormatDescriptorIdentifier(api, ctx, cycleConstants, indexer);

        /// only necessary for rogue DrmKeys.
        this.objectMapper = objectMapper;

        EMPTY_DOWNLOAD_LOCATIONS.filename = new StreamDownloadLocationFilename("");
        EMPTY_DOWNLOAD_LOCATIONS.locations = Collections.emptyList();
    }

    StreamData convertStreamData(PackageHollow packages, PackageStreamHollow inputStream, DrmInfoData drmInfoData) {
        int encodingProfileId = (int) inputStream._getStreamProfileId();
        int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(Long.valueOf(encodingProfileId));
        StreamProfilesHollow streamProfile = api.getStreamProfilesHollow(streamProfileOrdinal);
        if(streamProfileOrdinal == HollowConstants.ORDINAL_NONE || streamProfile == null)
            return null;

        if (streamProfile._getProfileType()._isValueEqual("MERCHSTILL")) {
            return null;
        }

        ImageStreamInfoHollow inputStreamImageInfo = inputStream._getImageInfo();
        StreamFileIdentificationHollow inputStreamIdentity = inputStream._getFileIdentification();
        StreamDeploymentHollow inputStreamDeployment = inputStream._getDeployment();
        StreamDimensionsHollow inputStreamDimensions = inputStream._getDimensions();
        StreamNonImageInfoHollow inputNonImageInfo = inputStream._getNonImageInfo();
        AudioStreamInfoHollow inputAudioStreamInfo = inputNonImageInfo._getAudioInfo();
        VideoStreamInfoHollow inputVideoStreamInfo = inputNonImageInfo._getVideoInfo();
        TextStreamInfoHollow inputTextStreamInfo = inputNonImageInfo._getTextInfo();

        StreamData outputStream = new StreamData();

        outputStream.downloadableId = new DownloadableId(inputStream._getDownloadableId());
        outputStream.packageId = (int)packages._getPackageId();

        // Get the encoding algorithm
        StringHollow algoHash = inputStream._getEncodingAlgorithmHash();
        if(algoHash != null)
        	outputStream.encodingAlgorithmHash = new Strings(algoHash._getValue());
        else
        	outputStream.encodingAlgorithmHash = new Strings(DEFAULT_ENCODING_ALGORITHM);

        outputStream.fileSizeInBytes = inputStreamIdentity._getFileSizeInBytes();
        outputStream.creationTimeStampInSeconds = inputStreamIdentity._getCreatedTimeSeconds();
        outputStream.cRC32Hash = inputStreamIdentity._getCrc32();
        outputStream.sha1_1 = inputStreamIdentity._getSha1_1();
        outputStream.sha1_2 = inputStreamIdentity._getSha1_2();
        outputStream.sha1_3 = inputStreamIdentity._getSha1_3();

        outputStream.drmData = EMPTY_DRM_DATA;
        outputStream.additionalData = new StreamAdditionalData();
        outputStream.additionalData.mostlyConstantData = new StreamMostlyConstantData();
        outputStream.additionalData.mostlyConstantData.tags = Collections.emptyList();

        if(!Float.isNaN(inputVideoStreamInfo._getFps()))
            outputStream.additionalData.frameRate = new FrameRate(inputVideoStreamInfo._getFps());
        outputStream.additionalData.qoeInfo = new QoEInfo();
        if(inputVideoStreamInfo._getScaledPsnrTimesHundred() != Long.MIN_VALUE)
            outputStream.additionalData.qoeInfo.scaledPsnrScore = (int)inputVideoStreamInfo._getScaledPsnrTimesHundred();
        if(inputVideoStreamInfo._getVmafScore() != Long.MIN_VALUE)
            outputStream.additionalData.qoeInfo.vmafScore = (int)inputVideoStreamInfo._getVmafScore();
        outputStream.additionalData.qoeInfo.vmafAlgoVersionExp = inputVideoStreamInfo._getVmafAlgoVersionExp();
        outputStream.additionalData.qoeInfo.vmafAlgoVersionLts = inputVideoStreamInfo._getVmafAlgoVersionLts();
        outputStream.additionalData.qoeInfo.vmafScoreExp = inputVideoStreamInfo._getVmafScoreExp();
        outputStream.additionalData.qoeInfo.vmafScoreLts = inputVideoStreamInfo._getVmafScoreLts();
        outputStream.additionalData.qoeInfo.vmafplusScoreExp = inputVideoStreamInfo._getVmafplusScoreExp();
        outputStream.additionalData.qoeInfo.vmafplusScoreLts = inputVideoStreamInfo._getVmafplusScoreLts();
        outputStream.additionalData.qoeInfo.vmafplusPhoneScoreExp = inputVideoStreamInfo._getVmafplusPhoneScoreExp();
        outputStream.additionalData.qoeInfo.vmafplusPhoneScoreLts = inputVideoStreamInfo._getVmafplusPhoneScoreLts();

        VideoStreamCropParamsHollow inputCropParams = inputVideoStreamInfo._getCropParams();
        if(inputCropParams != null) {
            outputStream.additionalData.cropParams = new StreamCropParams();
            outputStream.additionalData.cropParams.x = inputCropParams._getX();
            outputStream.additionalData.cropParams.y = inputCropParams._getY();
            outputStream.additionalData.cropParams.width = inputCropParams._getWidth();
            outputStream.additionalData.cropParams.height = inputCropParams._getHeight();
        }

        Set<StreamDeploymentLabelHollow> deploymentLabels = inputStreamDeployment._getDeploymentLabel();
        int deploymentLabelBits = 0;
        if(deploymentLabels != null) {
            for(StreamDeploymentLabelHollow label : deploymentLabels) {
                Integer labelBit = deploymentLabelBitsetOffsetMap.get(label._getValue()._getValue());
                if(labelBit != null)
                    deploymentLabelBits |= (1 << labelBit.intValue());
            }
        }
        outputStream.additionalData.mostlyConstantData.deploymentLabel = deploymentLabelBits;
        outputStream.additionalData.mostlyConstantData.deploymentPriority = inputStreamDeployment._getDeploymentPriority() == Integer.MIN_VALUE ? 300 : inputStreamDeployment._getDeploymentPriority();

        StringHollow tags = inputStream._getTags();
        if(tags != null) {
            outputStream.additionalData.mostlyConstantData.tags = getTagsList(tags._getValue());
        }

        outputStream.additionalData.downloadLocations = EMPTY_DOWNLOAD_LOCATIONS;

        outputStream.streamDataDescriptor = new StreamDataDescriptor();
        outputStream.streamDataDescriptor.cacheDeployedCountries = Collections.emptySet();
        outputStream.downloadDescriptor = new DownloadDescriptor();

        StreamDeploymentInfoHollow deploymentInfo = inputStreamDeployment._getDeploymentInfo();
        if(deploymentInfo != null) {
            Set<ISOCountryHollow> cacheDeployedCountries = deploymentInfo._getCacheDeployedCountries();
            if(cacheDeployedCountries != null) {
                outputStream.streamDataDescriptor.cacheDeployedCountries = new HashSet<ISOCountry>();
                for(ISOCountryHollow country : cacheDeployedCountries) {
                    outputStream.streamDataDescriptor.cacheDeployedCountries.add(cycleConstants.getISOCountry(country._getValue()));
                }
            }

            Set<CdnDeploymentHollow> cdnDeployments = deploymentInfo._getCdnDeployments();
            if(cdnDeployments != null) {
                if(cdnDeployments.size() > 0) {
                    outputStream.additionalData.downloadLocations = new DownloadLocationSet();
                    outputStream.additionalData.downloadLocations.filename = new StreamDownloadLocationFilename(inputStreamIdentity._getFilename());
                    outputStream.additionalData.downloadLocations.locations = new ArrayList<DownloadLocation>();

                    for(CdnDeploymentHollow cdnDeployment : cdnDeployments) {
                        DownloadLocation location = new DownloadLocation();
                        location.directory = new Strings(cdnDeployment._getDirectory()._getValue());
                        location.originServerName = new Strings(cdnDeployment._getOriginServer()._getValue());

                        outputStream.additionalData.downloadLocations.locations.add(location);
                    }
                }
            }
        }

        StringHollow conformingGroupId = inputStreamDeployment._getS3PathComponent();
        if(conformingGroupId != null) {
            outputStream.additionalData.mostlyConstantData.conformingGroupId = Integer.parseInt(conformingGroupId._getValue());
        }

        StringHollow s3FullPath = inputStreamDeployment._getS3FullPath();
        if(s3FullPath != null) {
            outputStream.additionalData.mostlyConstantData.s3FullPath = s3FullPath._getValue().toCharArray();
        }

        int pixelAspectHeight = inputStreamDimensions._getPixelAspectRatioHeight();
        if(pixelAspectHeight != Integer.MIN_VALUE) {
            outputStream.streamDataDescriptor.pixelAspect = new PixelAspect();
            outputStream.streamDataDescriptor.pixelAspect.height = pixelAspectHeight;
            outputStream.streamDataDescriptor.pixelAspect.width = inputStreamDimensions._getPixelAspectRatioWidth();
        }

        int videoResolutionHeight = inputStreamDimensions._getHeightInPixels();
        if(videoResolutionHeight != Integer.MIN_VALUE) {
            outputStream.streamDataDescriptor.videoResolution = new VideoResolution();
            outputStream.streamDataDescriptor.videoResolution.height = videoResolutionHeight;
            outputStream.streamDataDescriptor.videoResolution.width = inputStreamDimensions._getWidthInPixels();
        }
        outputStream.streamDataDescriptor.imageCount = 0;


        StreamAssetTypeHollow inputAssetType = inputStream._getAssetType();
        if(inputAssetType != null && inputAssetType._getAssetType() != null) {
            outputStream.downloadDescriptor.assetTypeDescriptor = assetTypeDescriptorMap.get(inputAssetType._getAssetType()._getValue());
        } else {
            outputStream.downloadDescriptor.assetTypeDescriptor = assetTypeDescriptorMap.get("primary");
        }

        StreamAssetMetadataHollow assetMetadataId = inputStream._getMetadataId();
        if(assetMetadataId != null) {
            String id = assetMetadataId._getId();
            if(id != null) {
                outputStream.downloadDescriptor.assetMetaData = new AssetMetaData(new Strings(id));
            }
        }



        if(inputNonImageInfo != null) {
            outputStream.streamDataDescriptor.runTimeInSeconds = (int) inputNonImageInfo._getRuntimeSeconds();
        }



        if(inputVideoStreamInfo != null) {
            int height = inputStreamDimensions._getHeightInPixels();
            int width = inputStreamDimensions._getWidthInPixels();
            int targetHeight = inputStreamDimensions._getTargetHeightInPixels();
            int targetWidth = inputStreamDimensions._getTargetWidthInPixels();
            int bitrate = inputVideoStreamInfo._getVideoBitrateKBPS();
            int peakBitrate = inputVideoStreamInfo._getVideoPeakBitrateKBPS();

            VideoFormatDescriptor selectVideoFormatDescriptorNew = null;
            VideoFormatDescriptor selectVideoFormatDescriptorOld = null;
            if (ctx.getConfig().useVideoResolutionType()) {
                outputStream.downloadDescriptor.videoFormatDescriptor = videoFormatIdentifier.selectVideoFormatDescriptor(height, width);
                selectVideoFormatDescriptorNew = outputStream.downloadDescriptor.videoFormatDescriptor;
            } else {
                outputStream.downloadDescriptor.videoFormatDescriptor = videoFormatIdentifier.selectVideoFormatDescriptorOld(encodingProfileId, bitrate, height, width, targetHeight, targetWidth);
                selectVideoFormatDescriptorOld = outputStream.downloadDescriptor.videoFormatDescriptor;
            }
            outputStream.streamDataDescriptor.bitrate = bitrate;
            outputStream.streamDataDescriptor.peakBitrate = peakBitrate;

            { // DEBUGGING
                if (selectVideoFormatDescriptorNew == null) selectVideoFormatDescriptorNew = videoFormatIdentifier.selectVideoFormatDescriptor(height, width);
                if (selectVideoFormatDescriptorOld == null) selectVideoFormatDescriptorOld = videoFormatIdentifier.selectVideoFormatDescriptorOld(encodingProfileId, bitrate, height, width, targetHeight, targetWidth);

                int videoId = (int) packages._getMovieId();
                long downloadableId = inputStream._getDownloadableId();
                String diffKey = String.format("encodingProfileId=%s, height=%s, width=%s", encodingProfileId, height, width);
                String newFormat = new String(selectVideoFormatDescriptorNew.name.value);
                String oldFormat = new String(selectVideoFormatDescriptorOld.name.value);
                debugVideoFormatMap.track(diffKey, videoId, inputStream._getDownloadableId(), oldFormat, newFormat);

                if (debugLogConfig.isLogDetails() && selectVideoFormatDescriptorOld.id != selectVideoFormatDescriptorNew.id) {
                    ctx.getLogger().warn(TransformerLogTag.VideoFormatMismatch_downloadableIds, "videoId={}: new={}, old={}, downloadableId={}, {}",
                            videoId, newFormat, oldFormat, downloadableId, diffKey);
                }
            }

            if(targetHeight != Integer.MIN_VALUE && targetWidth != Integer.MIN_VALUE) {
                outputStream.streamDataDescriptor.targetDimensions = new TargetDimensions();
                outputStream.streamDataDescriptor.targetDimensions.heightInPixels = targetHeight;
                outputStream.streamDataDescriptor.targetDimensions.widthInPixels = targetWidth;
            }

            Integer drmKeyGroup = Integer.valueOf((int)streamProfile._getDrmKeyGroup());
            Object drmKey = drmKeysByGroupId.get(drmKeyGroup);
            if(drmKey != null) {
                ////TODO: Probably get rid of this if/else, then don't need special logic to add DrmKeys to the ObjectMapper
                if(inputNonImageInfo != null && inputNonImageInfo._getDrmInfo() != null) {
                    outputStream.drmData = new StreamDrmData();
                    if(drmKeyGroup.intValue() == PackageDataModule.WMDRMKEY_GROUP) {
                        WmDrmKey wmDrmKey = ((WmDrmKey)drmKey).clone();
                        wmDrmKey.downloadableId = outputStream.downloadableId;
                        outputStream.drmData.wmDrmKey = wmDrmKey;
                    } else {
                        outputStream.drmData.drmKey = (DrmKey) drmKey;
                    }
                } else {
                    ///TODO: Why exclude WmDrmKeys?
                    if(drmKeyGroup.intValue() != PackageDataModule.WMDRMKEY_GROUP)
                        objectMapper.add(drmKey);
                }

                DrmInfo drmInfo = drmInfoByGroupId.get(drmKeyGroup);
                if (drmInfo != null) {
                    if (drmInfo.drmHeaders != null) {
                        DrmHeader header = drmInfo.drmHeaders.get(PLAYREADY_SYSTEM);
                        if (header != null) {
                            StringHollow playreadyHeaderVersion = streamProfile._getPlayreadyHeaderVersion();
                            if (playreadyHeaderVersion != null) {
                                if (header.attributes == null || header.attributes.isEmpty()) {
                                    header.attributes = new HashMap<>();
                                }
                                header.attributes.put(DrmHeader.HEADER_VERSION,
                                        DrmHeader.newHeaderVersionAttributeValue(playreadyHeaderVersion._getValue()));
                            }
                        }
                    }
                    drmInfoData.downloadableIdToDrmInfoMap.put(outputStream.downloadableId, drmInfo);
                }
            }
        }

        outputStream.downloadDescriptor.encodingProfileId = (int)inputStream._getStreamProfileId();
        if(inputAudioStreamInfo != null) {
            if(inputAudioStreamInfo._getAudioLanguageCode() != null)
                outputStream.downloadDescriptor.audioLanguageBcp47code = new Strings(inputAudioStreamInfo._getAudioLanguageCode()._getValue());
            if(outputStream.streamDataDescriptor.bitrate == Integer.MIN_VALUE) {
                outputStream.streamDataDescriptor.bitrate = inputAudioStreamInfo._getAudioBitrateKBPS();
                outputStream.streamDataDescriptor.peakBitrate = Integer.MIN_VALUE;
            }
        }

        if(outputStream.streamDataDescriptor.bitrate == Integer.MIN_VALUE)
            outputStream.streamDataDescriptor.bitrate = 0;
        if(outputStream.streamDataDescriptor.peakBitrate == Integer.MIN_VALUE)
            outputStream.streamDataDescriptor.peakBitrate = 0;

        if(inputTextStreamInfo != null) {
            if(inputTextStreamInfo._getTextLanguageCode() != null)
                outputStream.downloadDescriptor.textLanguageBcp47code = new Strings(inputTextStreamInfo._getTextLanguageCode()._getValue());
            if(inputTextStreamInfo._getTimedTextType() != null) {
                outputStream.downloadDescriptor.timedTextTypeDescriptor = timedTextTypeDescriptorMap.get(inputTextStreamInfo._getTimedTextType()._getValue());
            }

            if(inputTextStreamInfo._getImageTimedTextMasterIndexLength() != Long.MIN_VALUE) {
                outputStream.additionalData.mostlyConstantData.imageSubtitleIndexByteRange = new ImageSubtitleIndexByteRange();
                outputStream.additionalData.mostlyConstantData.imageSubtitleIndexByteRange.masterIndexOffset = inputTextStreamInfo._getImageTimedTextMasterIndexOffset();
                outputStream.additionalData.mostlyConstantData.imageSubtitleIndexByteRange.masterIndexSize = (int) inputTextStreamInfo._getImageTimedTextMasterIndexLength();
            }
        }

        if(inputStreamImageInfo != null && inputStreamImageInfo._getImageCount() != Integer.MIN_VALUE) {
            outputStream.streamDataDescriptor.imageCount = inputStreamImageInfo._getImageCount();
        }

        return outputStream;

    }

    private List<Strings> getTagsList(String tags) {
        List<Strings> cachedList = tagsLists.get(tags);
        if(cachedList != null)
            return cachedList;

        cachedList = new ArrayList<Strings>();
        for(String tag : tags.split(",")) {
            cachedList.add(new Strings(tag));
        }
        tagsLists.put(tags, cachedList);
        return cachedList;
    }


    private Map<String, AssetTypeDescriptor> getAssetTypeDescriptorMap() {
        Map<String, AssetTypeDescriptor> map = new HashMap<String, AssetTypeDescriptor>();

        map.put("primary", assetTypeDescriptor(1, "primary", "Primary"));
        map.put("assistive", assetTypeDescriptor(2, "assistive", "Assistive"));
        map.put("commentary", assetTypeDescriptor(3, "commentary", "Commentary"));
        map.put("clip", assetTypeDescriptor(4, "clip", "Clip"));

        return map;
    }

    private AssetTypeDescriptor assetTypeDescriptor(int id, String name, String description) {
        AssetTypeDescriptor descriptor = new AssetTypeDescriptor();
        descriptor.id = id;
        descriptor.name = new Strings(name);
        descriptor.description = new Strings(description);
        return descriptor;
    }

    private Map<String, Integer> getDeploymentLabelBitsetOffsetMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("PartiallyDeployedReplacement", Integer.valueOf(0));
        map.put("FutureLabel", Integer.valueOf(1));
        map.put("DeployASAP", Integer.valueOf(2));
        map.put("DeployASAP-ignoreRules", Integer.valueOf(3));
        map.put("DoNotPlay", Integer.valueOf(4));

        return map;
    }

    private Map<String, TimedTextTypeDescriptor> getTimedTextTypeDescriptorMap() {
        Map<String, TimedTextTypeDescriptor> map = new HashMap<String, TimedTextTypeDescriptor>();

        map.put("CC", new TimedTextTypeDescriptor("ClosedCaptions"));
        map.put("SUBS", new TimedTextTypeDescriptor("Subtitles"));
        map.put("FN", new TimedTextTypeDescriptor("Forced"));
        map.put("UNKNOWN", new TimedTextTypeDescriptor("Unknown"));

        return map;
    }

    @Deprecated // should be removed once new video resolution code is enabled
    private static class DebugLogConfig {
        private Set<Integer> idSet = Collections.emptySet();
        private static final FastProperty.StringProperty EXCLUDE_ENCPROF_IDS_FROM_LOG = new FastProperty.StringProperty("com.netflix.vms.server.exclude.encprof_ids.inlogs", "");
        private static final FastProperty.BooleanProperty LOG_DETAILS_ENABLED = new FastProperty.BooleanProperty("com.netflix.vms.server.log.videoformatdiff_details.enabled", false);

        public DebugLogConfig() {
            EXCLUDE_ENCPROF_IDS_FROM_LOG.addCallback(() -> {
                updateIdSet();
            });
            updateIdSet();
        }

        private void updateIdSet() {
            String value = EXCLUDE_ENCPROF_IDS_FROM_LOG.getValue();
            if (value == null || value.trim().isEmpty()) {
                idSet = Collections.emptySet();
            } else {
                Set<Integer> newSet = new HashSet<>();
                for (String id : value.split(",")) {
                    if (id.trim().isEmpty()) continue;
                    newSet.add(Integer.parseInt(id.trim()));
                }
                idSet = newSet;
            }
        }

        public boolean isLogDetails() {
            return LOG_DETAILS_ENABLED.get();
        }

        public boolean isExcludeEncodingProfileIdInDetailLog(int encodingProfileId) {
            return idSet.contains(encodingProfileId);
        }
    }

    @Deprecated
    public static class VideoFormatDebugMap {
        public static Map<String, Map<String, Set<Integer>>> diffMap = new HashMap<>();
        public static Map<Integer, Set<String>> oldFormatMap = new HashMap<>();
        public static Map<Integer, Set<String>> newFormatMap = new HashMap<>();
        public static Set<Long> downloadableIdDiffsSet = new HashSet<>();

        public void track(String key, Integer video, long downloadableId, String oldFormat, String newFormat) {
            add(video, oldFormat, oldFormatMap);
            add(video, newFormat, newFormatMap);

            if (!oldFormat.equals(newFormat)) {
                downloadableIdDiffsSet.add(downloadableId);
                String change = String.format("new=%s, old=%s", newFormat, oldFormat);
                track(key, change, video);
            }
        }

        private void add(Integer video, String format, Map<Integer, Set<String>> map) {
            Set<String> values = map.get(video);
            if (values == null) {
                values = new HashSet<>();
                map.put(video, values);
            }
            values.add(format);
        }

        public void track(String key, String change, Integer video) {
            Map<String, Set<Integer>> innerMap = diffMap.get(key);
            if (innerMap == null) {
                innerMap = new HashMap<>();
                diffMap.put(key, innerMap);
            }

            Set<Integer> videos = innerMap.get(change);
            if (videos == null) {
                videos = new HashSet<>();
                innerMap.put(change, videos);
            }

            videos.add(video);
        }

        public void reset() {
            diffMap.clear();
            oldFormatMap.clear();
            newFormatMap.clear();
            downloadableIdDiffsSet.clear();
        }
    }

    @Deprecated
    public static void clearVideoFormatDiffs() {
        debugVideoFormatMap.reset();
    }

    @Deprecated
    public static void logVideoFormatDiffs(TransformerContext ctx) {
        ctx.getLogger().warn(TransformerLogTag.VideoFormatMismatch_downloadableIds_total, "total downloadableId diffs={}", debugVideoFormatMap.downloadableIdDiffsSet.size());

        Set<Integer> videoIds = new HashSet<>();
        for (String encProKey : debugVideoFormatMap.diffMap.keySet()) {
            Map<String, Set<Integer>> innerMap = debugVideoFormatMap.diffMap.get(encProKey);

            for (Set<Integer> vSet : innerMap.values()) {
                videoIds.addAll(vSet);
            }

            ctx.getLogger().warn(TransformerLogTag.VideoFormatMismatch_encodingProfileIds, "{} : {}", encProKey, debugLogConfig.isLogDetails() ? innerMap : innerMap.keySet());
        }

        for (Integer vId : videoIds) {
            Set<String> oldSet = debugVideoFormatMap.oldFormatMap.get(vId);
            Set<String> newSet = debugVideoFormatMap.newFormatMap.get(vId);
            if (oldSet == null) oldSet = Collections.emptySet();
            if (newSet == null) newSet = Collections.emptySet();
            if (!Objects.equals(oldSet,  newSet)) {
                ctx.getLogger().warn(TransformerLogTag.VideoFormatMismatch_videoIds, "videoId={} : old={}, new={}", vId, oldSet, newSet);

                if (!newSet.containsAll(oldSet)) {
                    ctx.getLogger().warn(TransformerLogTag.VideoFormatMismatch_videoIds_missingFormat, "videoId={} : old={}, new={}", vId, oldSet, newSet);
                }
            }
        }
    }
}
