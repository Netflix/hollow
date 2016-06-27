package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamProfilesHollow extends HollowObject {

    public StreamProfilesHollow(StreamProfilesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getId() {
        return delegate().getId(ordinal);
    }

    public Long _getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public long _getDrmType() {
        return delegate().getDrmType(ordinal);
    }

    public Long _getDrmTypeBoxed() {
        return delegate().getDrmTypeBoxed(ordinal);
    }

    public StringHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getIs3D() {
        return delegate().getIs3D(ordinal);
    }

    public Boolean _getIs3DBoxed() {
        return delegate().getIs3DBoxed(ordinal);
    }

    public StringHollow _getName27AndAbove() {
        int refOrdinal = delegate().getName27AndAboveOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getMimeType() {
        int refOrdinal = delegate().getMimeTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getDrmKeyGroup() {
        return delegate().getDrmKeyGroup(ordinal);
    }

    public Long _getDrmKeyGroupBoxed() {
        return delegate().getDrmKeyGroupBoxed(ordinal);
    }

    public StringHollow _getName26AndBelow() {
        int refOrdinal = delegate().getName26AndBelowOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getAudioChannelCount() {
        return delegate().getAudioChannelCount(ordinal);
    }

    public Long _getAudioChannelCountBoxed() {
        return delegate().getAudioChannelCountBoxed(ordinal);
    }

    public StringHollow _getProfileType() {
        int refOrdinal = delegate().getProfileTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getFileExtension() {
        int refOrdinal = delegate().getFileExtensionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public boolean _getIsAdaptiveSwitching() {
        return delegate().getIsAdaptiveSwitching(ordinal);
    }

    public Boolean _getIsAdaptiveSwitchingBoxed() {
        return delegate().getIsAdaptiveSwitchingBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamProfilesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamProfilesDelegate delegate() {
        return (StreamProfilesDelegate)delegate;
    }

}