package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PackageDrmInfoHollow extends HollowObject {

    public PackageDrmInfoHollow(PackageDrmInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getContentPackagerPublicKey() {
        int refOrdinal = delegate().getContentPackagerPublicKeyOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getKeySeed() {
        int refOrdinal = delegate().getKeySeedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getKeyId() {
        return delegate().getKeyId(ordinal);
    }

    public Long _getKeyIdBoxed() {
        return delegate().getKeyIdBoxed(ordinal);
    }

    public long _getDrmKeyGroup() {
        return delegate().getDrmKeyGroup(ordinal);
    }

    public Long _getDrmKeyGroupBoxed() {
        return delegate().getDrmKeyGroupBoxed(ordinal);
    }

    public StringHollow _getKey() {
        int refOrdinal = delegate().getKeyOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public DrmHeaderInfoListHollow _getDrmHeaderInfo() {
        int refOrdinal = delegate().getDrmHeaderInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDrmHeaderInfoListHollow(refOrdinal);
    }

    public boolean _getKeyDecrypted() {
        return delegate().getKeyDecrypted(ordinal);
    }

    public Boolean _getKeyDecryptedBoxed() {
        return delegate().getKeyDecryptedBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageDrmInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PackageDrmInfoDelegate delegate() {
        return (PackageDrmInfoDelegate)delegate;
    }

}