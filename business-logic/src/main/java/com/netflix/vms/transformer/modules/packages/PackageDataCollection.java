package com.netflix.vms.transformer.modules.packages;

import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowoutput.BaseDownloadable;
import com.netflix.vms.transformer.hollowoutput.DownloadableId;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDescriptor;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadableFilename;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to encapsulate data collection when iterating through streams data.
 */
public class PackageDataCollection {

    private static final String VIDEO = "VIDEO";
    private static final String AUDIO = "AUDIO";
    private static final String MUXED = "MUXED";
    private static final String TRICKPLAY = "TRICKPLAY";
    private static final Map<Integer, TrickPlayType> trickPlayTypeMap = new HashMap() {{
        put(241, new TrickPlayType("BIF_W240"));
        put(321, new TrickPlayType("BIF_W320"));
        put(641, new TrickPlayType("BIF_W640"));
        put(242, new TrickPlayType("ZIP_W240"));
        put(322, new TrickPlayType("ZIP_W320"));
        put(642, new TrickPlayType("ZIP_W640"));
    }};

    private PackageData packageData;
    private Set<VideoFormatDescriptor> videoFormatDescriptors;
    private long longestRuntimeInSeconds;
    private Set<Strings> screenFormats;
    public Map<TrickPlayType, TrickPlayItem> trickPlayItemMap;
    private Map<ISOCountry, Set<Strings>> soundTypesByCountry;

    private Map<Float, Strings> screenFormatCache;
    private Set<Integer> fourKProfileIds;
    private Set<Integer> hdrProfileIds;
    private Set<Integer> atmosStreamProfileIds;
    private Map<Integer, Strings> soundTypesMap;
    private CycleConstants cycleConstants;

    public PackageDataCollection(Set<Integer> fourKProfileIds, Set<Integer> hdrProfileIds, Set<Integer> atmosStreamProfileIds, Map<Integer, Strings> soundTypesMap, CycleConstants cycleConstants) {
        this.packageData = new PackageData();
        this.videoFormatDescriptors = new HashSet<>();
        this.longestRuntimeInSeconds = 0;
        this.soundTypesByCountry = new HashMap<>();
        this.screenFormats = new TreeSet<>();
        this.trickPlayItemMap = new HashMap<>();


        this.screenFormatCache = new HashMap<>();
        this.fourKProfileIds = fourKProfileIds;
        this.hdrProfileIds = hdrProfileIds;
        this.atmosStreamProfileIds = atmosStreamProfileIds;
        this.soundTypesMap = soundTypesMap;
        this.cycleConstants = cycleConstants;
    }

    public PackageData getPackageData() {
        return this.packageData;
    }

    public long getLongestRuntimeInSeconds() {
        return this.longestRuntimeInSeconds;
    }

    public List<Strings> getSoundTypes(String country) {
        if (soundTypesByCountry.containsKey(cycleConstants.getISOCountry(country)))
            return new ArrayList<>(soundTypesByCountry.get(cycleConstants.getISOCountry(country)));
        return new ArrayList<>();
    }

    public Set<VideoFormatDescriptor> getVideoDescriptorFormats() {
        return videoFormatDescriptors;
    }

    public List<Strings> getScreenFormats() {
        return new ArrayList<>(screenFormats);
    }

    public Map<TrickPlayType, TrickPlayItem> getTrickPlayItemMap() {
        return this.trickPlayItemMap;
    }

    public void processStreamData(StreamData streamData, StreamProfilesHollow profilesHollow, Map<ISOCountry, Set<DownloadableId>> excludedDownloadables, int videoId, PackageStreamHollow inputStream) {
        int encodingProfileId = streamData.downloadDescriptor.encodingProfileId;
        String profileType = profilesHollow._getProfileType()._getValue();

        // collect video format descriptors
        collectVideoDescriptorFormats(streamData, profileType, encodingProfileId);
        checkLongestRuntime(streamData, profileType);
        collectScreenFormats(streamData, profileType);
        collectSoundTypes(streamData, profileType, profilesHollow, excludedDownloadables);

        // build trick play item map
        collectTrickPlayType(inputStream, profileType, videoId);
    }

    private void collectTrickPlayType(PackageStreamHollow inputStream, String profileType, int videoId) {
        if (profileType.equals(TRICKPLAY)) {
            TrickPlayItem trickplay = new TrickPlayItem();
            trickplay.imageCount = inputStream._getImageInfo()._getImageCount();
            trickplay.videoId = new Video(videoId);
            trickplay.trickPlayDownloadable = new TrickPlayDownloadable();
            trickplay.trickPlayDownloadable.fileName = new TrickPlayDownloadableFilename(inputStream._getFileIdentification()._getFilename());
            trickplay.trickPlayDownloadable.descriptor = new TrickPlayDescriptor();
            trickplay.trickPlayDownloadable.descriptor.height = inputStream._getDimensions()._getHeightInPixels();
            trickplay.trickPlayDownloadable.descriptor.width = inputStream._getDimensions()._getWidthInPixels();
            trickplay.trickPlayDownloadable.baseDownloadable = new BaseDownloadable();
            trickplay.trickPlayDownloadable.baseDownloadable.downloadableId = inputStream._getDownloadableId();
            trickplay.trickPlayDownloadable.baseDownloadable.streamProfileId = (int) inputStream._getStreamProfileId();
            trickplay.trickPlayDownloadable.baseDownloadable.originServerNames = new ArrayList<>();

            convertCdnDeploymentsAndAddToList(inputStream, trickplay.trickPlayDownloadable.baseDownloadable.originServerNames);

            trickPlayItemMap.put(trickPlayTypeMap.get((int) inputStream._getStreamProfileId()), trickplay);
        }
    }

    private void convertCdnDeploymentsAndAddToList(PackageStreamHollow stream, List<Strings> originServerNames) {
        Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
        for (CdnDeploymentHollow deployment : cdnDeployments) {
            originServerNames.add(new Strings(deployment._getOriginServer()._getValue()));
        }
    }

    private void collectVideoDescriptorFormats(StreamData streamData, String profileType, int encodingProfileId) {
        // @TODO: Why don't MUXED streams contribute to the package info's videoFormatDescriptors?
        if ("VIDEO".equals(profileType)) {
            VideoFormatDescriptor descriptor = streamData.downloadDescriptor.videoFormatDescriptor;
            // Only interested in HD or better
            if (descriptor.id == 1 || descriptor.id == 3 || descriptor.id == 4) {
                videoFormatDescriptors.add(descriptor);
            }
        }
        if (hdrProfileIds.contains(encodingProfileId))
            videoFormatDescriptors.add(cycleConstants.HDR);
        // @TODO: should FOUR_K be computed from video resolution?  - Perhaps VideoFormat should just exclude video resolutions instead
        if (fourKProfileIds.contains(encodingProfileId))
            videoFormatDescriptors.add(cycleConstants.FOUR_K);
        if (atmosStreamProfileIds.contains(encodingProfileId))
            videoFormatDescriptors.add(cycleConstants.ATMOS);
    }

    private void checkLongestRuntime(StreamData streamData, String profileType) {
        if (profileType.equals(VIDEO) && streamData.streamDataDescriptor.runTimeInSeconds > longestRuntimeInSeconds)
            longestRuntimeInSeconds = streamData.streamDataDescriptor.runTimeInSeconds;
    }

    private void collectScreenFormats(StreamData streamData, String profileType) {
        if (profileType.equals(VIDEO) || profileType.equals(MUXED)) {
            PixelAspect pixelAspect = streamData.streamDataDescriptor.pixelAspect;
            VideoResolution videoResolution = streamData.streamDataDescriptor.videoResolution;

            if (pixelAspect != null && videoResolution != null && videoResolution.height != 0 && videoResolution.width != 0) {
                int parHeight = Math.max(pixelAspect.height, 1);
                int parWidth = Math.max(pixelAspect.width, 1);

                float screenFormat = ((float) (videoResolution.width * parWidth)) / (videoResolution.height * parHeight);
                screenFormats.add(getScreenFormat(screenFormat));
            }
        }
    }

    private Strings getScreenFormat(Float screenFormat) {
        Strings formatStr = screenFormatCache.get(screenFormat);
        if (formatStr == null) {
            formatStr = new Strings(String.format("%.2f:1", screenFormat).toCharArray());
            screenFormatCache.put(screenFormat, formatStr);
        }
        return formatStr;
    }

    private void collectSoundTypes(StreamData streamData, String profileType, StreamProfilesHollow profilesHollow, Map<ISOCountry, Set<DownloadableId>> excludedDownloadables) {
        if (profileType.equals(AUDIO)) {
            // get sound type using audio channel
            int audioChannel = (int) profilesHollow._getAudioChannelCount();
            if (soundTypesMap.containsKey(audioChannel)) {
                Set<ISOCountry> countries = excludedDownloadables.keySet();
                for (ISOCountry country : countries) {
                    Set<DownloadableId> excluded = excludedDownloadables.get(country);
                    if (excluded != null && !excluded.contains(streamData.downloadableId)) {
                        soundTypesByCountry.putIfAbsent(country, new TreeSet<>());
                        soundTypesByCountry.get(country).add(soundTypesMap.get(audioChannel));
                    }
                }
            }
        }
    }
}
