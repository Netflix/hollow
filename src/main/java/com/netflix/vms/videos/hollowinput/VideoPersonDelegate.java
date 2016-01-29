package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoPersonDelegate extends HollowObjectDelegate {

    public int getCastOrdinal(int ordinal);

    public int getAliasOrdinal(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public VideoPersonTypeAPI getTypeAPI();

}