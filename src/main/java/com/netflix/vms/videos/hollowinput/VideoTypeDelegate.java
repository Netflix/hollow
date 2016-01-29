package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoTypeDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getTypeOrdinal(int ordinal);

    public boolean getIsTV(int ordinal);

    public Boolean getIsTVBoxed(int ordinal);

    public VideoTypeTypeAPI getTypeAPI();

}