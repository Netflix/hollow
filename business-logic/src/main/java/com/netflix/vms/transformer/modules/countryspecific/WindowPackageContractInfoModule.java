package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.DeployablePackagesHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractHollow;
import com.netflix.vms.transformer.hollowinput.TimecodeAnnotationHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.PackageDataCollection;

import java.util.Collections;
import java.util.stream.Collectors;

public class WindowPackageContractInfoModule {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex packageIdx;
    private final HollowPrimaryKeyIndex deployablePackageIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowPrimaryKeyIndex timecodeAnnotationIdx;

    private final PackageMomentDataModule packageMomentDataModule;
    private final VideoPackageInfo FILTERED_VIDEO_PACKAGE_INFO;

    public WindowPackageContractInfoModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.packageMomentDataModule = new PackageMomentDataModule();
        this.packageIdx = indexer.getPrimaryKeyIndex(IndexSpec.PACKAGES);
        this.deployablePackageIdx = indexer.getPrimaryKeyIndex(IndexSpec.DEPLOYABLE_PACKAGES);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.timecodeAnnotationIdx = indexer.getPrimaryKeyIndex(IndexSpec.TIMECODE_ANNOTATIONS);
        FILTERED_VIDEO_PACKAGE_INFO = newEmptyVideoPackageInfo();
    }

    public WindowPackageContractInfo buildWindowPackageContractInfo(PackageData packageData, RightsContractHollow rightsContract, ContractHollow contract, String country, boolean isAvailableForDownload, PackageDataCollection packageDataCollection) {
        PackageHollow inputPackage = api.getPackageHollow(packageIdx.getMatchingOrdinal((long) packageData.id));
        
        int ordinal = timecodeAnnotationIdx.getMatchingOrdinal((long)packageData.id);
        TimecodeAnnotationHollow inputTimecodeAnnotation = null;
        if(ordinal != -1)
          inputTimecodeAnnotation = api.getTimecodeAnnotationHollow(timecodeAnnotationIdx.getMatchingOrdinal((long)packageData.id));
        
        // create contract info
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = new VideoContractInfo();
        info.videoContractInfo.contractId = (int) rightsContract._getContractId();
        info.videoContractInfo.isAvailableForDownload = isAvailableForDownload;
        info.videoContractInfo.primaryPackageId = (int) rightsContract._getPackageId();
        assignContracInfo(info, contract);
        info.videoContractInfo.assetBcp47Codes = rightsContract._getAssets().stream().map(a -> new Strings(a._getBcp47Code()._getValue().toCharArray())).collect(Collectors.toSet());

        // create package info
        info.videoPackageInfo = newEmptyVideoPackageInfo();
        info.videoPackageInfo.packageId = packageData.id;
        int deployablePackageOrdinal = deployablePackageIdx.getMatchingOrdinal((long) packageData.id);
        DeployablePackagesHollow deployablePackage = deployablePackageOrdinal == -1 ? null : api.getDeployablePackagesHollow(deployablePackageOrdinal);
        if (deployablePackage != null) info.videoPackageInfo.isDefaultPackage = deployablePackage._getDefaultPackage();

        // package moment data
        PackageMomentData packageMomentData = packageMomentDataModule.getWindowPackageMomentData(packageData, inputPackage, inputTimecodeAnnotation);
        info.videoPackageInfo.startMomentOffsetInMillis = packageMomentData.startMomentOffsetInMillis;
        info.videoPackageInfo.endMomentOffsetInMillis = packageMomentData.endMomentOffsetInMillis;

        info.videoPackageInfo.trickPlayMap = packageDataCollection.getTrickPlayItemMap();
        info.videoPackageInfo.formats = packageDataCollection.getVideoDescriptorFormats();
        info.videoPackageInfo.screenFormats = packageDataCollection.getScreenFormats();
        info.videoPackageInfo.soundTypes = packageDataCollection.getSoundTypes(country);
        info.videoPackageInfo.runtimeInSeconds = (int) packageDataCollection.getLongestRuntimeInSeconds();

        return info;
    }


    public WindowPackageContractInfo buildWindowPackageContractInfoWithoutPackage(int packageId, RightsContractHollow rightsContract, ContractHollow contract, int videoId) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = new VideoContractInfo();
        info.videoContractInfo.contractId = (int) rightsContract._getContractId();
        info.videoContractInfo.primaryPackageId = packageId;
        assignContracInfo(info, contract);
        info.videoContractInfo.assetBcp47Codes = rightsContract._getAssets().stream().map(a -> new Strings(a._getBcp47Code()._getValue().toCharArray())).collect(Collectors.toSet());
        info.videoPackageInfo = getFilteredVideoPackageInfo(videoId, packageId);
        return info;
    }

    private void assignContracInfo(WindowPackageContractInfo info, ContractHollow contract) {
        if (contract != null) {
            if (contract._getPrePromotionDays() != Long.MIN_VALUE)
                info.videoContractInfo.prePromotionDays = (int) contract._getPrePromotionDays();
            info.videoContractInfo.isDayAfterBroadcast = contract._getDayAfterBroadcast();
            info.videoContractInfo.hasRollingEpisodes = contract._getDayAfterBroadcast();
            info.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(Collections.singletonList(new Strings(contract._getCupToken()._getValue())));
        } else {
            info.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(Collections.emptyList());
        }
    }

    public WindowPackageContractInfo buildFilteredWindowPackageContractInfo(int contractId, int videoId) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = getFilteredVideoContractInfo(contractId);
        info.videoPackageInfo = getFilteredVideoPackageInfo(videoId);
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

    private int getApproximateRuntimeInSecods(long videoId) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal(videoId);
        VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
        if (general != null)
            return (int) general._getRuntime();
        return 0;
    }

    private VideoPackageInfo getFilteredVideoPackageInfo(long videoId) {
        int approxRuntimeInSecs = getApproximateRuntimeInSecods(videoId);
        if (approxRuntimeInSecs == 0) return FILTERED_VIDEO_PACKAGE_INFO;

        VideoPackageInfo result = newEmptyVideoPackageInfo();
        result.runtimeInSeconds = approxRuntimeInSecs;
        return result;
    }

    private VideoPackageInfo getFilteredVideoPackageInfo(long videoId, int packageId) {
        VideoPackageInfo result = newEmptyVideoPackageInfo();
        result.packageId = packageId;
        result.runtimeInSeconds = getApproximateRuntimeInSecods(videoId);
        return result;
    }

    static VideoPackageInfo newEmptyVideoPackageInfo() {
        VideoPackageInfo info = new VideoPackageInfo();
        info.packageId = 0;
        info.runtimeInSeconds = 0;
        info.soundTypes = Collections.emptyList();
        info.screenFormats = Collections.emptyList();
        info.trickPlayMap = Collections.emptyMap();
        info.formats = Collections.emptySet();
        return info;

    }

    public void reset() {
        this.packageMomentDataModule.reset();
    }

}