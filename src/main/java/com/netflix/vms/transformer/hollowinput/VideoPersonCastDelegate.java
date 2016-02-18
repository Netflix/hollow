package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoPersonCastDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public long getRoleTypeId(int ordinal);

    public Long getRoleTypeIdBoxed(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public int getRoleNameOrdinal(int ordinal);

    public VideoPersonCastTypeAPI getTypeAPI();

}