package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoDisplaySetDelegate extends HollowObjectDelegate {

    public long getTopNodeId(int ordinal);

    public Long getTopNodeIdBoxed(int ordinal);

    public int getSetsOrdinal(int ordinal);

    public VideoDisplaySetTypeAPI getTypeAPI();

}