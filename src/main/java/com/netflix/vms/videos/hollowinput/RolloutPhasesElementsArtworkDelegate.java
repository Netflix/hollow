package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhasesElementsArtworkDelegate extends HollowObjectDelegate {

    public long getImageId(int ordinal);

    public Long getImageIdBoxed(int ordinal);

    public RolloutPhasesElementsArtworkTypeAPI getTypeAPI();

}