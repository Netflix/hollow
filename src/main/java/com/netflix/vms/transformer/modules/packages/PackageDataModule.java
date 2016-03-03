package com.netflix.vms.transformer.modules.packages;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.DeployablePackagesHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.PackageDrmInfoHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.DrmKey;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.WmDrmKey;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PackageDataModule {

    static final int WMDRMKEY_GROUP = 1;

    private final VMSHollowVideoInputAPI api;
    private final HollowObjectMapper mapper;

    private final HollowHashIndex packagesByVideoIdx;
    private final HollowPrimaryKeyIndex deployablePackagesIdx;

    private final Map<Integer, Object> drmKeysByGroupId;

    private final StreamDataModule streamDataModule;
    private final ContractRestrictionModule contractRestrictionModule;
    private final EncodeSummaryDescriptorModule encodeSummaryModule;

    public PackageDataModule(VMSHollowVideoInputAPI api, HollowObjectMapper objectMapper, VMSTransformerIndexer indexer) {
        this.api = api;
        this.mapper = objectMapper;
        this.packagesByVideoIdx = indexer.getHashIndex(IndexSpec.PACKAGES_BY_VIDEO);
        this.deployablePackagesIdx = indexer.getPrimaryKeyIndex(IndexSpec.DEPLOYABLE_PACKAGES);

        this.drmKeysByGroupId = new HashMap<Integer, Object>();

        this.streamDataModule = new StreamDataModule(api, indexer, objectMapper, drmKeysByGroupId);
        this.contractRestrictionModule = new ContractRestrictionModule(api, indexer);
        this.encodeSummaryModule = new EncodeSummaryDescriptorModule(api, indexer);
    }

    public void transform(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Set<Integer> videoIds = gatherVideoIds(showHierarchiesByCountry);

        for(Integer videoId : videoIds) {
            HollowHashIndexResult packagesForVideo = packagesByVideoIdx.findMatches((long)videoId);

            if(packagesForVideo != null) {
                HollowOrdinalIterator iter = packagesForVideo.iterator();

                int packageOrdinal = iter.next();
                while(packageOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    drmKeysByGroupId.clear();

                    PackagesHollow packages = api.getPackagesHollow(packageOrdinal);
                    populateDrmKeysByGroupId(packages, videoId);
                    convertPackage(packages);
                    packageOrdinal = iter.next();
                }

            }
        }
    }

    private void populateDrmKeysByGroupId(PackagesHollow packageInput, Integer videoId) {
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
                drmKeysByGroupId.put(Integer.valueOf(drmKeyGroup), drmKey);
            }

        }
    }

    private void convertPackage(PackagesHollow packages) {
        PackageData pkg = new PackageData();

        pkg.id = (int)packages._getPackageId();
        pkg.video = new Video((int)packages._getMovieId());
        pkg.isPrimaryPackage = true;

        /////////// CONTRACT RESTRICTIONS /////////////////

        pkg.contractRestrictions = contractRestrictionModule.getContractRestrictions(packages);

        /////////// STREAMS ///////////

        pkg.streams = new HashSet<StreamData>();

        for(PackageStreamHollow inputStream : packages._getDownloadables()) {
            StreamData outputStream = streamDataModule.convertStreamData(packages, inputStream);

            if(outputStream != null)
                pkg.streams.add(outputStream);
        }

        //////////// DEPLOYABLE PACKAGES //////////////

        int deployablePackagesOrdinal = deployablePackagesIdx.getMatchingOrdinal(packages._getPackageId());
        if(deployablePackagesOrdinal != -1) {
            pkg.allDeployableCountries = new HashSet<ISOCountry>();
            DeployablePackagesHollow deployablePackages = api.getDeployablePackagesHollow(deployablePackagesOrdinal);
            for(ISOCountryHollow isoCountry : deployablePackages._getCountryCodes()) {
                pkg.allDeployableCountries.add(new ISOCountry(isoCountry._getValue()));
            }
        }

        //////////// ENCODE SUMMARY DESCRIPTORS /////////////////

        encodeSummaryModule.summarize(pkg);

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


}
