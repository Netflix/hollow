package com.netflix.vms.transformer.modules.packages;

import java.util.List;

import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.ImageStreamInfoHollow;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.StreamDataDescriptor;
import java.util.Arrays;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import java.util.HashMap;
import com.netflix.vms.transformer.hollowinput.StreamAssetTypeHollow;
import com.netflix.vms.transformer.hollowoutput.ImageSubtitleIndexByteRange;
import com.netflix.vms.transformer.hollowoutput.TimedTextTypeDescriptor;
import com.netflix.vms.transformer.hollowoutput.AssetTypeDescriptor;
import com.netflix.vms.transformer.hollowoutput.AssetMetaData;
import com.netflix.vms.transformer.hollowoutput.DownloadDescriptor;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentInfoHollow;
import java.util.ArrayList;
import com.netflix.vms.transformer.hollowoutput.DownloadLocation;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentSetHollow;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.DownloadLocationSet;
import java.util.Collections;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentLabelHollow;
import com.netflix.vms.transformer.hollowoutput.StreamMostlyConstantData;
import com.netflix.vms.transformer.hollowoutput.QoEInfo;
import com.netflix.vms.transformer.hollowoutput.FrameRate;
import com.netflix.vms.transformer.hollowinput.StreamDrmInfoHollow;
import com.netflix.vms.transformer.hollowinput.TextStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.VideoStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.AudioStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamDeploymentHollow;
import com.netflix.vms.transformer.hollowoutput.StreamAdditionalData;
import com.netflix.vms.transformer.hollowoutput.StreamHashData;
import com.netflix.vms.transformer.hollowinput.StreamFileIdentificationHollow;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PackageDataModule {

    private final Map<String, AssetTypeDescriptor> assetTypeDescriptorMap;
    private final Map<String, VideoFormatDescriptor> videoFormatDescriptorMap;
    private final Set<SuperHDIdentifier> validSuperHDIdentifiers;
    private final Map<String, List<Strings>> tagsLists;

    private final VMSHollowVideoInputAPI api;
    private final HollowObjectMapper mapper;

    private final HollowHashIndex packagesByVideoIdx;

    public PackageDataModule(VMSHollowVideoInputAPI api, HollowObjectMapper objectMapper, VMSTransformerIndexer indexer) {
        this.api = api;
        this.mapper = objectMapper;
        this.packagesByVideoIdx = indexer.getHashIndex(IndexSpec.PACKAGES_BY_VIDEO);

        this.assetTypeDescriptorMap = getAssetTypeDescriptorMap();
        this.videoFormatDescriptorMap = getVideoFormatDescriptorMap();
        this.validSuperHDIdentifiers = getValidSuperHDIdentifiers();
        this.tagsLists = new HashMap<String, List<Strings>>();
    }

    public void transform(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Set<Integer> videoIds = gatherVideoIds(showHierarchiesByCountry);

        for(Integer videoId : videoIds) {
            HollowHashIndexResult packagesForVideo = packagesByVideoIdx.findMatches((long)videoId);

            if(packagesForVideo != null) {
                HollowOrdinalIterator iter = packagesForVideo.iterator();

                int packageOrdinal = iter.next();
                while(packageOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    PackagesHollow packages = api.getPackagesHollow(packageOrdinal);
                    convertPackage(packages);
                    packageOrdinal = iter.next();
                }

            }
        }
    }

    private void convertPackage(PackagesHollow packages) {
        PackageData pkg = new PackageData();

        pkg.id = (int)packages._getPackageId();
        pkg.video = new Video((int)packages._getMovieId());

        pkg.streams = new HashSet<StreamData>();

        for(PackageStreamHollow inputStream : packages._getDownloadables()) {
            ImageStreamInfoHollow inputStreamImageInfo = inputStream._getImageInfo();
            if(inputStreamImageInfo != null && inputStreamImageInfo._getOffsetMillis() != Long.MIN_VALUE)
                continue;

            StreamFileIdentificationHollow inputStreamIdentity = inputStream._getFileIdentification();
            StreamDeploymentHollow inputStreamDeployment = inputStream._getDeployment();
            StreamDimensionsHollow inputStreamDimensions = inputStream._getDimensions();
            StreamNonImageInfoHollow inputNonImageInfo = inputStream._getNonImageInfo();
            AudioStreamInfoHollow inputAudioStreamInfo = inputNonImageInfo._getAudioInfo();
            VideoStreamInfoHollow inputVideoStreamInfo = inputNonImageInfo._getVideoInfo();
            TextStreamInfoHollow inputTextStreamInfo = inputNonImageInfo._getTextInfo();
            StreamDrmInfoHollow inputDrmInfo = inputNonImageInfo._getDrmInfo();

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

            outputStream.additionalData = new StreamAdditionalData();
            outputStream.additionalData.mostlyConstantData = new StreamMostlyConstantData();

            outputStream.additionalData.frameRate = new FrameRate(inputVideoStreamInfo._getFps());
            outputStream.additionalData.qoeInfo = new QoEInfo();
            if(inputVideoStreamInfo._getScaledPsnrTimesHundred() != 0)
                outputStream.additionalData.qoeInfo.scaledPsnrScore = (int)inputVideoStreamInfo._getScaledPsnrTimesHundred();
            if(inputVideoStreamInfo._getVmafScore() != 0)
                outputStream.additionalData.qoeInfo.vmafScore = (int)inputVideoStreamInfo._getVmafScore();
            outputStream.additionalData.mostlyConstantData.deploymentLabel = 0;
            outputStream.additionalData.mostlyConstantData.deploymentPriority = inputStreamDeployment._getDeploymentPriority() == Integer.MIN_VALUE ? 300 : inputStreamDeployment._getDeploymentPriority();

            StringHollow tags = inputStream._getTags();


            outputStream.additionalData.mostlyConstantData.tags = Collections.emptyList();

            outputStream.additionalData.downloadLocations = new DownloadLocationSet();
            outputStream.additionalData.downloadLocations.filename = new Strings(inputStreamIdentity._getFilename());
            outputStream.additionalData.downloadLocations.locations = new ArrayList<DownloadLocation>();

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
                    for(CdnDeploymentHollow cdnDeployment : cdnDeployments) {
                        DownloadLocation location = new DownloadLocation();
                        location.directory = new Strings(cdnDeployment._getDirectory()._getValue());
                        location.originServerName = new Strings(cdnDeployment._getOriginServer()._getValue());

                        outputStream.additionalData.downloadLocations.locations.add(location);
                    }
                }
            }


            outputStream.streamDataDescriptor.pixelAspect = new PixelAspect();
            outputStream.streamDataDescriptor.pixelAspect.height = inputStreamDimensions._getPixelAspectRatioHeight();
            outputStream.streamDataDescriptor.pixelAspect.width = inputStreamDimensions._getPixelAspectRatioWidth();
            outputStream.streamDataDescriptor.videoResolution = new VideoResolution();
            outputStream.streamDataDescriptor.videoResolution.height = inputStreamDimensions._getHeightInPixels();
            outputStream.streamDataDescriptor.videoResolution.width = inputStreamDimensions._getWidthInPixels();


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
                int encodingProfileId = (int) inputStream._getStreamProfileId();
                int bitrate = inputVideoStreamInfo._getVideoBitrateKBPS();

                outputStream.downloadDescriptor.videoFormatDescriptor = selectVideoFormatDescriptor(height, targetHeight, targetWidth, new SuperHDIdentifier(encodingProfileId, bitrate));
                outputStream.streamDataDescriptor.bitrate = bitrate;
            }

            outputStream.downloadDescriptor.encodingProfileId = (int)inputStream._getStreamProfileId();
            if(inputAudioStreamInfo != null) {
                if(inputAudioStreamInfo._getAudioLanguageCode() != null)
                    outputStream.downloadDescriptor.audioLanguageBcp47code = new Strings(inputAudioStreamInfo._getAudioLanguageCode()._getValue());
                outputStream.streamDataDescriptor.bitrate = inputAudioStreamInfo._getAudioBitrateKBPS();
            }

            if(inputTextStreamInfo != null) {
                if(inputTextStreamInfo._getTextLanguageCode() != null)
                    outputStream.downloadDescriptor.textLanguageBcp47code = new Strings(inputTextStreamInfo._getTextLanguageCode()._getValue());
                if(inputTextStreamInfo._getTimedTextType() != null) {
                    outputStream.downloadDescriptor.timedTextTypeDescriptor = new TimedTextTypeDescriptor();
                    outputStream.downloadDescriptor.timedTextTypeDescriptor.nameStr = inputTextStreamInfo._getTimedTextType()._getValue().toCharArray();
                }

                if(inputTextStreamInfo._getImageTimedTextMasterIndexLength() != Long.MIN_VALUE) {
                    outputStream.additionalData.mostlyConstantData.imageSubtitleIndexByteRange = new ImageSubtitleIndexByteRange();
                    outputStream.additionalData.mostlyConstantData.imageSubtitleIndexByteRange.masterIndexOffset = inputTextStreamInfo._getImageTimedTextMasterIndexOffset();
                    outputStream.additionalData.mostlyConstantData.imageSubtitleIndexByteRange.masterIndexSize = (int) inputTextStreamInfo._getImageTimedTextMasterIndexLength();
                }
            }




            pkg.streams.add(outputStream);
        }

        mapper.addObject(pkg);
    }

    private Set<Integer> gatherVideoIds(Map<String, ShowHierarchy> showHierarchyByCountry) {
        Set<Integer> videoIds = new HashSet<Integer>();
        for(Map.Entry<String, ShowHierarchy> entry : showHierarchyByCountry.entrySet()) {
            ShowHierarchy showHierarchy = entry.getValue();

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

        return videoIds;
    }

    private List<Strings> getTagsList(String tags) {
        List<Strings> cachedList = tagsLists.get(tags);
        if(cachedList != null)
            return cachedList;

        return null;
    }

    private VideoFormatDescriptor selectVideoFormatDescriptor(int height, int targetHeight, int targetWidth, SuperHDIdentifier superHDIdentifier) {
        if(targetHeight != Integer.MIN_VALUE) {

        }

        if(height == Integer.MIN_VALUE)
            return videoFormatDescriptorMap.get("unknown");

        if(height < 719)
            return videoFormatDescriptorMap.get("SD");

        if(validSuperHDIdentifiers.contains(superHDIdentifier))
            return videoFormatDescriptorMap.get("Super_HD");

        return videoFormatDescriptorMap.get("HD");
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
