package com.netflix.vms.transformer.modules.packages;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.AudioStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.ImageStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.StreamAssetTypeHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamFileIdentificationHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TextStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoStreamInfoHollow;
import com.netflix.vms.transformer.hollowoutput.AssetTypeDescriptor;
import com.netflix.vms.transformer.hollowoutput.DownloadDescriptor;
import com.netflix.vms.transformer.hollowoutput.DownloadLocation;
import com.netflix.vms.transformer.hollowoutput.DownloadLocationSet;
import com.netflix.vms.transformer.hollowoutput.DrmKey;
import com.netflix.vms.transformer.hollowoutput.FrameRate;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.ImageSubtitleIndexByteRange;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.QoEInfo;
import com.netflix.vms.transformer.hollowoutput.StreamAdditionalData;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.StreamDataDescriptor;
import com.netflix.vms.transformer.hollowoutput.StreamDrmData;
import com.netflix.vms.transformer.hollowoutput.StreamHashData;
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
import java.util.Set;

public class StreamDataModule {

    private final StreamDrmData EMPTY_DRM_DATA = new StreamDrmData();
    private final DownloadLocationSet EMPTY_DOWNLOAD_LOCATIONS = new DownloadLocationSet();

    private final Map<String, AssetTypeDescriptor> assetTypeDescriptorMap;
    private final Map<String, VideoFormatDescriptor> videoFormatDescriptorMap;
    private final Map<String, TimedTextTypeDescriptor> timedTextTypeDescriptorMap;
    private final Set<SuperHDIdentifier> validSuperHDIdentifiers;
    private final Map<String, List<Strings>> tagsLists;
    private final Map<Integer, Object> drmKeysByGroupId;

    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final VMSHollowVideoInputAPI api;

    public StreamDataModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer, Map<Integer, Object> drmKeysByGroupId) {
        this.assetTypeDescriptorMap = getAssetTypeDescriptorMap();
        this.videoFormatDescriptorMap = getVideoFormatDescriptorMap();
        this.validSuperHDIdentifiers = getValidSuperHDIdentifiers();
        this.timedTextTypeDescriptorMap = getTimedTextTypeDescriptorMap();
        this.tagsLists = new HashMap<String, List<Strings>>();
        this.api = api;
        this.drmKeysByGroupId = drmKeysByGroupId;

        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        EMPTY_DOWNLOAD_LOCATIONS.filename = new Strings("");
        EMPTY_DOWNLOAD_LOCATIONS.locations = Collections.emptyList();
    }

    StreamData convertStreamData(PackagesHollow packages, PackageStreamHollow inputStream) {
        int encodingProfileId = (int) inputStream._getStreamProfileId();
        int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(Long.valueOf(encodingProfileId));
        StreamProfilesHollow streamProfile = api.getStreamProfilesHollow(streamProfileOrdinal);
        if(streamProfile == null)
            return null;

        if(streamProfile._getProfileType()._isValueEqual("MERCHSTILL"))
            return null;

        ImageStreamInfoHollow inputStreamImageInfo = inputStream._getImageInfo();
        StreamFileIdentificationHollow inputStreamIdentity = inputStream._getFileIdentification();
        StreamDeploymentHollow inputStreamDeployment = inputStream._getDeployment();
        StreamDimensionsHollow inputStreamDimensions = inputStream._getDimensions();
        StreamNonImageInfoHollow inputNonImageInfo = inputStream._getNonImageInfo();
        AudioStreamInfoHollow inputAudioStreamInfo = inputNonImageInfo._getAudioInfo();
        VideoStreamInfoHollow inputVideoStreamInfo = inputNonImageInfo._getVideoInfo();
        TextStreamInfoHollow inputTextStreamInfo = inputNonImageInfo._getTextInfo();

        StreamData outputStream = new StreamData();

        outputStream.downloadableId = inputStream._getDownloadableId();
        outputStream.packageId = (int)packages._getPackageId();

        outputStream.fileSizeInBytes = inputStreamIdentity._getFileSizeInBytes();
        outputStream.creationTimeStampInSeconds = inputStreamIdentity._getCreatedTimeSeconds();
        outputStream.hashData = new StreamHashData();
        outputStream.hashData.cRC32Hash = inputStreamIdentity._getCrc32();
        outputStream.hashData.sha1_1 = inputStreamIdentity._getSha1_1();
        outputStream.hashData.sha1_2 = inputStreamIdentity._getSha1_2();
        outputStream.hashData.sha1_3 = inputStreamIdentity._getSha1_3();

        outputStream.drmData = EMPTY_DRM_DATA;
        outputStream.additionalData = new StreamAdditionalData();
        outputStream.additionalData.mostlyConstantData = new StreamMostlyConstantData();

        if(!Float.isNaN(inputVideoStreamInfo._getFps()))
            outputStream.additionalData.frameRate = new FrameRate(inputVideoStreamInfo._getFps());
        outputStream.additionalData.qoeInfo = new QoEInfo();
        if(inputVideoStreamInfo._getScaledPsnrTimesHundred() != Long.MIN_VALUE)
            outputStream.additionalData.qoeInfo.scaledPsnrScore = (int)inputVideoStreamInfo._getScaledPsnrTimesHundred();
        if(inputVideoStreamInfo._getVmafScore() != Long.MIN_VALUE)
            outputStream.additionalData.qoeInfo.vmafScore = (int)inputVideoStreamInfo._getVmafScore();
        outputStream.additionalData.mostlyConstantData.deploymentLabel = 0;
        outputStream.additionalData.mostlyConstantData.deploymentPriority = inputStreamDeployment._getDeploymentPriority() == Integer.MIN_VALUE ? 300 : inputStreamDeployment._getDeploymentPriority();

        StringHollow tags = inputStream._getTags();
        if(tags != null) {
            outputStream.additionalData.mostlyConstantData.tags = getTagsList(tags._getValue());
        }

        outputStream.additionalData.downloadLocations = EMPTY_DOWNLOAD_LOCATIONS;

        outputStream.streamDataDescriptor = new StreamDataDescriptor();
        outputStream.downloadDescriptor = new DownloadDescriptor();

        StreamDeploymentInfoHollow deploymentInfo = inputStreamDeployment._getDeploymentInfo();
        if(deploymentInfo != null) {
            Set<ISOCountryHollow> cacheDeployedCountries = deploymentInfo._getCacheDeployedCountries();
            if(cacheDeployedCountries != null) {
                outputStream.streamDataDescriptor.cacheDeployedCountries = new HashSet<ISOCountry>();
                for(ISOCountryHollow country : cacheDeployedCountries) {
                    outputStream.streamDataDescriptor.cacheDeployedCountries.add(new ISOCountry(country._getValue()));
                }
            }

            Set<CdnDeploymentHollow> cdnDeployments = deploymentInfo._getCdnDeployments();
            if(cdnDeployments != null) {
                if(cdnDeployments.size() > 0) {
                    outputStream.additionalData.downloadLocations = new DownloadLocationSet();
                    outputStream.additionalData.downloadLocations.filename = new Strings(inputStreamIdentity._getFilename());
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


        if(inputNonImageInfo != null) {
            outputStream.streamDataDescriptor.runTimeInSeconds = (int) inputNonImageInfo._getRuntimeSeconds();
        }



        if(inputVideoStreamInfo != null) {
            int height = inputStreamDimensions._getHeightInPixels();
            int targetHeight = inputStreamDimensions._getTargetHeightInPixels();
            int targetWidth = inputStreamDimensions._getTargetWidthInPixels();
            int bitrate = inputVideoStreamInfo._getVideoBitrateKBPS();

            outputStream.downloadDescriptor.videoFormatDescriptor = selectVideoFormatDescriptor(height, targetHeight, targetWidth, new SuperHDIdentifier(encodingProfileId, bitrate));
            outputStream.streamDataDescriptor.bitrate = bitrate;

            if(targetHeight != Integer.MIN_VALUE && targetWidth != Integer.MIN_VALUE) {
                outputStream.streamDataDescriptor.targetDimensions = new TargetDimensions();
                outputStream.streamDataDescriptor.targetDimensions.heightInPixels = targetHeight;
                outputStream.streamDataDescriptor.targetDimensions.widthInPixels = targetWidth;
            }

            Integer drmKeyGroup = Integer.valueOf((int)streamProfile._getDrmKeyGroup());
            Object drmKey = drmKeysByGroupId.get(drmKeyGroup);
            if(drmKey != null) {
                outputStream.drmData = new StreamDrmData();
                if(drmKeyGroup.intValue() == PackageDataModule.WMDRMKEY_GROUP) {
                    WmDrmKey wmDrmKey = ((WmDrmKey)drmKey).clone();
                    wmDrmKey.downloadableId = outputStream.downloadableId;
                    outputStream.drmData.wmDrmKey = wmDrmKey;
                } else {
                    outputStream.drmData.drmKey = (DrmKey) drmKey;
                }
            }

        }

        outputStream.downloadDescriptor.encodingProfileId = (int)inputStream._getStreamProfileId();
        if(inputAudioStreamInfo != null) {
            if(inputAudioStreamInfo._getAudioLanguageCode() != null)
                outputStream.downloadDescriptor.audioLanguageBcp47code = new Strings(inputAudioStreamInfo._getAudioLanguageCode()._getValue());
            if(outputStream.streamDataDescriptor.bitrate == Integer.MIN_VALUE)
                outputStream.streamDataDescriptor.bitrate = inputAudioStreamInfo._getAudioBitrateKBPS();
        }

        if(outputStream.streamDataDescriptor.bitrate == Integer.MIN_VALUE)
            outputStream.streamDataDescriptor.bitrate = 0;

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

    private VideoFormatDescriptor selectVideoFormatDescriptor(int height, int targetHeight, int targetWidth, SuperHDIdentifier superHDIdentifier) {
        if(height == Integer.MIN_VALUE)
            return videoFormatDescriptorMap.get("unknown");

        if(height < 719)
            return videoFormatDescriptorMap.get("SD");

        if(validSuperHDIdentifiers.contains(superHDIdentifier))
            return videoFormatDescriptorMap.get("Super_HD");

        return videoFormatDescriptorMap.get("HD");
    }

    private Map<String, TimedTextTypeDescriptor> getTimedTextTypeDescriptorMap() {
        Map<String, TimedTextTypeDescriptor> map = new HashMap<String, TimedTextTypeDescriptor>();

        map.put("CC", new TimedTextTypeDescriptor("ClosedCaptions"));
        map.put("SUBS", new TimedTextTypeDescriptor("Subtitles"));
        map.put("FN", new TimedTextTypeDescriptor("Forced"));
        map.put("UNKNOWN", new TimedTextTypeDescriptor("Unknown"));

        return map;
    }

    private Map<String, VideoFormatDescriptor> getVideoFormatDescriptorMap() {
        Map<String, VideoFormatDescriptor> map = new HashMap<String, VideoFormatDescriptor>();

        map.put("unknown", videoFormatDescriptor(-1, "unknown", "unknown"));
        map.put("SD", videoFormatDescriptor(2, "SD", "Standard Definition"));
        map.put("HD", videoFormatDescriptor(1, "HD", "HiDefinition"));
        map.put("Super_HD", videoFormatDescriptor(3, "Super_HD", "Super HiDefinition"));
        map.put("Ultra_HD", videoFormatDescriptor(4, "Ultra_HD", "Ultra HiDefinition"));

        return map;
    }

    private VideoFormatDescriptor videoFormatDescriptor(int id, String name, String description) {
        VideoFormatDescriptor descriptor = new VideoFormatDescriptor();
        descriptor.id = id;
        descriptor.name = new Strings(name);
        descriptor.description = new Strings(description);
        return descriptor;
    }

    private Set<SuperHDIdentifier> getValidSuperHDIdentifiers() {
        Set<SuperHDIdentifier> set = new HashSet<SuperHDIdentifier>();

        set.add(new SuperHDIdentifier(214, 4300));
        set.add(new SuperHDIdentifier(214, 4302));
        set.add(new SuperHDIdentifier(67, 4300));
        set.add(new SuperHDIdentifier(214, 5800));
        set.add(new SuperHDIdentifier(214, 5802));
        set.add(new SuperHDIdentifier(67, 5800));

        return set;
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

}
