package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseImageIdDelegate extends HollowObjectDelegate {

    public long getImageId(int ordinal);

    public Long getImageIdBoxed(int ordinal);

    public RolloutPhaseImageIdTypeAPI getTypeAPI();

}