package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamProfilesDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public long getDrmType(int ordinal);

    public Long getDrmTypeBoxed(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public boolean getIs3D(int ordinal);

    public Boolean getIs3DBoxed(int ordinal);

    public int getName27AndAboveOrdinal(int ordinal);

    public int getMimeTypeOrdinal(int ordinal);

    public long getDrmKeyGroup(int ordinal);

    public Long getDrmKeyGroupBoxed(int ordinal);

    public int getName26AndBelowOrdinal(int ordinal);

    public long getAudioChannelCount(int ordinal);

    public Long getAudioChannelCountBoxed(int ordinal);

    public int getProfileTypeOrdinal(int ordinal);

    public int getFileExtensionOrdinal(int ordinal);

    public boolean getIsAdaptiveSwitching(int ordinal);

    public Boolean getIsAdaptiveSwitchingBoxed(int ordinal);

    public StreamProfilesTypeAPI getTypeAPI();

}