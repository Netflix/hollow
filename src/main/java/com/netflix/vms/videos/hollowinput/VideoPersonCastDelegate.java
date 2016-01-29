package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoPersonCastDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getRoleTypeId(int ordinal);

    public Long getRoleTypeIdBoxed(int ordinal);

    public int getRoleNameOrdinal(int ordinal);

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public VideoPersonCastTypeAPI getTypeAPI();

}