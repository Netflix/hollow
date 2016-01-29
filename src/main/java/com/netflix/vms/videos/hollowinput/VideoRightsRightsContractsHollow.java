package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsRightsContractsHollow extends HollowObject {

    public VideoRightsRightsContractsHollow(VideoRightsRightsContractsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow _getDisallowedAssetBundles() {
        int refOrdinal = delegate().getDisallowedAssetBundlesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow(refOrdinal);
    }

    public VideoRightsRightsContractsArrayOfAssetsHollow _getAssets() {
        int refOrdinal = delegate().getAssetsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsContractsArrayOfAssetsHollow(refOrdinal);
    }

    public StringHollow _getCupToken() {
        int refOrdinal = delegate().getCupTokenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getContractId() {
        return delegate().getContractId(ordinal);
    }

    public Long _getContractIdBoxed() {
        return delegate().getContractIdBoxed(ordinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public long _getPrePromotionDays() {
        return delegate().getPrePromotionDays(ordinal);
    }

    public Long _getPrePromotionDaysBoxed() {
        return delegate().getPrePromotionDaysBoxed(ordinal);
    }

    public boolean _getDayAfterBroadcast() {
        return delegate().getDayAfterBroadcast(ordinal);
    }

    public Boolean _getDayAfterBroadcastBoxed() {
        return delegate().getDayAfterBroadcastBoxed(ordinal);
    }

    public VideoRightsRightsContractsArrayOfPackagesHollow _getPackages() {
        int refOrdinal = delegate().getPackagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsRightsContractsArrayOfPackagesHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsRightsContractsDelegate delegate() {
        return (VideoRightsRightsContractsDelegate)delegate;
    }

}