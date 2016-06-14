package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsContractIdDelegate extends HollowObjectDelegate {

    public long getValue(int ordinal);

    public Long getValueBoxed(int ordinal);

    public VideoRightsContractIdTypeAPI getTypeAPI();

}