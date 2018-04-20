package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class StreamDrmInfoHollow extends HollowObject {

    public StreamDrmInfoHollow(StreamDrmInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public DrmInfoStringHollow _getKeyId() {
        int refOrdinal = delegate().getKeyIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDrmInfoStringHollow(refOrdinal);
    }

    public DrmInfoStringHollow _getKey() {
        int refOrdinal = delegate().getKeyOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDrmInfoStringHollow(refOrdinal);
    }

    public DrmInfoStringHollow _getContentPackagerPublicKey() {
        int refOrdinal = delegate().getContentPackagerPublicKeyOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDrmInfoStringHollow(refOrdinal);
    }

    public DrmInfoStringHollow _getKeySeed() {
        int refOrdinal = delegate().getKeySeedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDrmInfoStringHollow(refOrdinal);
    }

    public StringHollow _getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDrmInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamDrmInfoDelegate delegate() {
        return (StreamDrmInfoDelegate)delegate;
    }

}