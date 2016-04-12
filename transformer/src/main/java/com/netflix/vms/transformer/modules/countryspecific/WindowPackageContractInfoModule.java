package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractHollow;
import com.netflix.vms.transformer.hollowoutput.ContractRestriction;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class WindowPackageContractInfoModule {

	private final VMSHollowVideoInputAPI api;
    private final TransformerContext ctx;
    private final HollowPrimaryKeyIndex packageIdx;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final PackageMomentDataModule packageMomentDataModule;

    private final Map<Integer, Strings> soundTypesMap;
    private final VideoPackageInfo FILTERED_VIDEO_PACKAGE_INFO;

    public WindowPackageContractInfoModule(VMSHollowVideoInputAPI api, TransformerContext ctx, VMSTransformerIndexer indexer) {
    	this.api = api;
    	this.ctx = ctx;

    	this.packageMomentDataModule = new PackageMomentDataModule(api, indexer);

    	this.packageIdx = indexer.getPrimaryKeyIndex(IndexSpec.PACKAGES);
    	this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.soundTypesMap = getSoundTypesMap();

        FILTERED_VIDEO_PACKAGE_INFO = getFilteredVideoPackageInfo();
    }

    public WindowPackageContractInfo buildWindowPackageContractInfo(PackageData packageData, VideoRightsContractHollow contract, String country) {
        PackagesHollow inputPackage = api.getPackagesHollow(packageIdx.getMatchingOrdinal((long)packageData.id));

        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = new VideoContractInfo();
        info.videoContractInfo.contractId = (int) contract._getContractId();
        info.videoContractInfo.primaryPackageId = (int) contract._getPackageId();
        if(contract._getPrePromotionDays() != Long.MIN_VALUE)
            info.videoContractInfo.prePromotionDays = (int) contract._getPrePromotionDays();
        info.videoContractInfo.isDayAfterBroadcast = contract._getDayAfterBroadcast();
        info.videoContractInfo.hasRollingEpisodes = contract._getDayAfterBroadcast();
        info.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(Collections.singletonList(new Strings(contract._getCupToken()._getValue())));
        info.videoContractInfo.assetBcp47Codes = new HashSet<Strings>();

        for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
            info.videoContractInfo.assetBcp47Codes.add(new Strings(asset._getBcp47Code()._getValue()));
        }

        info.videoPackageInfo = new VideoPackageInfo();
        info.videoPackageInfo.packageId = packageData.id;
        info.videoPackageInfo.formats = new HashSet<VideoFormatDescriptor>();
        info.videoPackageInfo.soundTypes = new ArrayList<Strings>();

        Set<com.netflix.vms.transformer.hollowoutput.Long> excludedDownloadables = findRelevantExcludedDownloadables(packageData, country);

        Set<Integer> soundTypesAudioChannels = new TreeSet<Integer>();
        Set<String> screenFormats = new TreeSet<String>();

        long longestRuntimeInSeconds = 0;

        for(StreamData streamData : packageData.streams) {
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long) streamData.downloadDescriptor.encodingProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();


            if("VIDEO".equals(streamProfileType) || "MUXED".equals(streamProfileType)) {
                /// add the videoFormatDescriptor
                VideoFormatDescriptor descriptor = streamData.downloadDescriptor.videoFormatDescriptor;
                if(descriptor.id == 1 || descriptor.id == 3 || descriptor.id == 4)  // Only interested in HD or better
                    info.videoPackageInfo.formats.add(descriptor);

                if(streamData.streamDataDescriptor.runTimeInSeconds > longestRuntimeInSeconds && "VIDEO".equals(streamProfileType))
                    longestRuntimeInSeconds = streamData.streamDataDescriptor.runTimeInSeconds;

                PixelAspect pixelAspect = streamData.streamDataDescriptor.pixelAspect;
                VideoResolution videoResolution = streamData.streamDataDescriptor.videoResolution;

                if(pixelAspect != null && videoResolution != null && videoResolution.height != 0 && videoResolution.width != 0) {
                    int parHeight = Math.max(pixelAspect.height, 1);
                    int parWidth = Math.max(pixelAspect.width, 1);

                    float screenFormat = ((float) (videoResolution.width * parWidth)) / (videoResolution.height * parHeight);
                    screenFormats.add(String.format("%.2f:1", screenFormat));
                }

            } else if("AUDIO".equals(streamProfileType)) {
                if(excludedDownloadables != null && !excludedDownloadables.contains(new com.netflix.vms.transformer.hollowoutput.Long(streamData.downloadableId)))
                    soundTypesAudioChannels.add(Integer.valueOf((int)profile._getAudioChannelCount()));
            }
        }


        PackageMomentData packageMomentData = packageMomentDataModule.getWindowPackageMomentData(packageData, inputPackage);

        info.videoPackageInfo.stillImagesMap = packageMomentData.stillImagesMap;
        info.videoPackageInfo.phoneSnacks = packageMomentData.phoneSnackMoments;
        info.videoPackageInfo.trickPlayMap = packageMomentData.trickPlayItemMap;

        info.videoPackageInfo.screenFormats = new ArrayList<Strings>(screenFormats.size());
        for(String screenFormat : screenFormats) {
            info.videoPackageInfo.screenFormats.add(new Strings(screenFormat));
        }

        info.videoPackageInfo.soundTypes = new ArrayList<Strings>(soundTypesAudioChannels.size());
        for(Integer soundType : soundTypesAudioChannels) {
            Strings soundTypeStr= soundTypesMap.get(soundType);
            if(soundTypeStr != null)
                info.videoPackageInfo.soundTypes.add(soundTypeStr);
        }

        info.videoPackageInfo.runtimeInSeconds = (int) longestRuntimeInSeconds;

        return info;
    }

    public WindowPackageContractInfo buildWindowPackageContractInfoWithoutPackage(VideoRightsContractHollow contract, String country) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = new VideoContractInfo();
        info.videoContractInfo.contractId = (int) contract._getContractId();
        info.videoContractInfo.primaryPackageId = 0;
        if(contract._getPrePromotionDays() != Long.MIN_VALUE)
            info.videoContractInfo.prePromotionDays = (int) contract._getPrePromotionDays();
        info.videoContractInfo.isDayAfterBroadcast = contract._getDayAfterBroadcast();
        info.videoContractInfo.hasRollingEpisodes = contract._getDayAfterBroadcast();
        info.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(Collections.singletonList(new Strings(contract._getCupToken()._getValue())));
        info.videoContractInfo.assetBcp47Codes = new HashSet<Strings>();

        for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
            info.videoContractInfo.assetBcp47Codes.add(new Strings(asset._getBcp47Code()._getValue()));
        }

        info.videoPackageInfo = FILTERED_VIDEO_PACKAGE_INFO;

        return info;
    }

    public WindowPackageContractInfo buildFilteredWindowPackageContractInfo(int contractId) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = getFilteredVideoContractInfo(contractId);
        info.videoPackageInfo = FILTERED_VIDEO_PACKAGE_INFO;
        return info;
    }


    private VideoContractInfo getFilteredVideoContractInfo(int contractId) {
        VideoContractInfo info = new VideoContractInfo();
        info.contractId = contractId;
        info.primaryPackageId = 0;
        info.cupTokens = new LinkedHashSetOfStrings();
        info.cupTokens.ordinals = Collections.emptyList();
        info.assetBcp47Codes = Collections.emptySet();
        return info;
    }


    private Set<com.netflix.vms.transformer.hollowoutput.Long> findRelevantExcludedDownloadables(PackageData packageData, String country) {
        Set<ContractRestriction> countryContractRestrictions = packageData.contractRestrictions.get(new ISOCountry(country));

        if(countryContractRestrictions == null)
            return null;

        long now = ctx.getNowMillis();

        Set<com.netflix.vms.transformer.hollowoutput.Long> nextExcludedDownloadables = Collections.emptySet();
        long nextStartDate = Long.MAX_VALUE;

        for(ContractRestriction restriction : countryContractRestrictions) {
            if(now > restriction.availabilityWindow.startDate.val && now < restriction.availabilityWindow.endDate.val) {
                return restriction.excludedDownloadables;
            } else if(now < restriction.availabilityWindow.startDate.val) {
                if(nextStartDate > restriction.availabilityWindow.startDate.val) {
                    nextStartDate = restriction.availabilityWindow.startDate.val;
                    nextExcludedDownloadables = restriction.excludedDownloadables;
                }
            }
        }

        return nextExcludedDownloadables;
    }

    private VideoPackageInfo getFilteredVideoPackageInfo() {
        VideoPackageInfo info = new VideoPackageInfo();
        info.packageId = 0;
        info.runtimeInSeconds = 0;
        info.soundTypes = Collections.emptyList();
        info.screenFormats = Collections.emptyList();
        info.phoneSnacks = Collections.emptyList();
        info.stillImagesMap = Collections.emptyMap();
        info.videoClipMap = Collections.emptyMap();
        info.trickPlayMap = Collections.emptyMap();
        info.formats = Collections.emptySet();
        return info;
    }

    private Map<Integer, Strings> getSoundTypesMap() {
        Map<Integer, Strings> map = new HashMap<Integer, Strings>();

        map.put(1, new Strings("1.0"));
        map.put(2, new Strings("2.0"));
        map.put(6, new Strings("5.1"));
        map.put(8, new Strings("8.1"));

        return map;
    }

    public void reset() {
        this.packageMomentDataModule.reset();
    }

}
