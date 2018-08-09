package com.netflix.vms.transformer.data;

import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.hollowinput.DealCountryGroupHollow;
import com.netflix.vms.transformer.hollowinput.DeployablePackagesHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.ListOfPackageTagsHollow;
import com.netflix.vms.transformer.hollowinput.PackageMovieDealCountryGroupHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A helper class to encapsulate deployable packages info from the HollowInputAPI. Once we finish
 * the migration from the DeployablePackages json feed to the PackageMovieDealCountryGroup Cinder
 * feed, this class can be removed and its functionality inlined.
 */
public class DeployablePackagesFetcher {
    private final HollowPrimaryKeyIndex deployablePackageIdx;
    private final HollowPrimaryKeyIndex packageMovieDealCountryGroupIndex;
    private final TransformerConfig config;
    private final VMSHollowInputAPI api;

    public DeployablePackagesFetcher(TransformerConfig config, VMSTransformerIndexer indexer,
            VMSHollowInputAPI api) {
        this.config = config;
        this.api = api;
        this.packageMovieDealCountryGroupIndex =
            indexer.getPrimaryKeyIndex(IndexSpec.PACKAGE_MOVIE_DEAL_COUNTRY_GROUP);
        this.deployablePackageIdx = indexer.getPrimaryKeyIndex(IndexSpec.DEPLOYABLE_PACKAGES);
    }

    public boolean isPackageExists(long packageId, int videoId) {
        if (config.isReadDeployablePackagesFromCinderFeed()) {
            return getFromCinder(packageId, videoId) != null;
        } else {
            return getFromJson(packageId) != null;
        }
    }

    public ListOfPackageTagsHollow getTags(long packageId, int videoId) {
        return config.isReadDeployablePackagesFromCinderFeed()
            ? getFromCinder(packageId,videoId)._getTags() : getFromJson(packageId)._getTags();
    }

    public boolean isDefaultPackage(long packageId, int videoId) {
        if (config.isReadDeployablePackagesFromCinderFeed()) {
            PackageMovieDealCountryGroupHollow g = getFromCinder(packageId, videoId);
            return g == null || g._getDefaultPackage();
        } else {
            DeployablePackagesHollow p = getFromJson(packageId);
            return p == null || p._getDefaultPackage();
        }
    }

    public Set<String> getCountryCodes(long packageId, int videoId) {
        if (config.isReadDeployablePackagesFromCinderFeed()) {
            Set<String> countries = new HashSet<>();
            PackageMovieDealCountryGroupHollow p = getFromCinder(packageId, videoId);
            if (p._getDealCountryGroups() == null) {
                return countries;
            }
            for (DealCountryGroupHollow deal : p._getDealCountryGroups()) {
                if (deal._getCountryWindow() != null) {
                    deal._getCountryWindow().forEach((country, deployable) -> {
                        if (deployable._getValue()) {
                            countries.add(country._getValue());
                        }
                    });
                }
            }
            return countries;
        } else {
            return getFromJson(packageId)._getCountryCodes().stream()
                    .map(ISOCountryHollow::_getValue)
                    .collect(Collectors.toSet());
        }
    }

    public Set<Integer> getAllMovieIds() {
        Set<Integer> allIds = new HashSet<>();
        for (DeployablePackagesHollow dp : api.getAllDeployablePackagesHollow()) {
            allIds.add((int) dp._getMovieId());
        }
        return allIds;
    }

    private DeployablePackagesHollow getFromJson(long packageId) {
        int ordinal = deployablePackageIdx.getMatchingOrdinal(packageId);
        return ordinal == HollowConstants.ORDINAL_NONE ? null
            : api.getDeployablePackagesHollow(ordinal);
    }

    private PackageMovieDealCountryGroupHollow getFromCinder(long packageId, long movieId) {
        int ordinal = packageMovieDealCountryGroupIndex.getMatchingOrdinal(movieId, packageId);
        return ordinal == HollowConstants.ORDINAL_NONE ? null
                : api.getPackageMovieDealCountryGroupHollow(ordinal);
    }
}
