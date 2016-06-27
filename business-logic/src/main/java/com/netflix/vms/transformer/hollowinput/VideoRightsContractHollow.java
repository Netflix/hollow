package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRightsContractHollow extends HollowObject {

    public VideoRightsContractHollow(VideoRightsContractDelegate delegate, int ordinal) {
        super(delegate, ordinal);
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

    public StringHollow _getCupToken() {
        int refOrdinal = delegate().getCupTokenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
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

    public DisallowedAssetBundlesListHollow _getDisallowedAssetBundles() {
        int refOrdinal = delegate().getDisallowedAssetBundlesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDisallowedAssetBundlesListHollow(refOrdinal);
    }

    public VideoRightsContractAssetsSetHollow _getAssets() {
        int refOrdinal = delegate().getAssetsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsContractAssetsSetHollow(refOrdinal);
    }

    public VideoRightsContractPackagesListHollow _getPackages() {
        int refOrdinal = delegate().getPackagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRightsContractPackagesListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRightsContractDelegate delegate() {
        return (VideoRightsContractDelegate)delegate;
    }

}