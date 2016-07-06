package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StreamDeploymentLabelDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public StreamDeploymentLabelTypeAPI getTypeAPI();

}