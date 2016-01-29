package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoPersonAliasDelegate extends HollowObjectDelegate {

    public long getAliasId(int ordinal);

    public Long getAliasIdBoxed(int ordinal);

    public VideoPersonAliasTypeAPI getTypeAPI();

}