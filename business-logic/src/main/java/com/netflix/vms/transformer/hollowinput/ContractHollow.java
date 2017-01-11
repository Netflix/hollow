package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ContractHollow extends HollowObject {

    public ContractHollow(ContractDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getContractId() {
        return delegate().getContractId(ordinal);
    }

    public Long _getContractIdBoxed() {
        return delegate().getContractIdBoxed(ordinal);
    }

    public boolean _getOriginal() {
        return delegate().getOriginal(ordinal);
    }

    public Boolean _getOriginalBoxed() {
        return delegate().getOriginalBoxed(ordinal);
    }

    public StringHollow _getCupToken() {
        int refOrdinal = delegate().getCupTokenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getDayOfBroadcast() {
        return delegate().getDayOfBroadcast(ordinal);
    }

    public Boolean _getDayOfBroadcastBoxed() {
        return delegate().getDayOfBroadcastBoxed(ordinal);
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ContractTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ContractDelegate delegate() {
        return (ContractDelegate)delegate;
    }

}