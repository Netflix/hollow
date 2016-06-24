package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamDeploymentDelegate extends HollowObjectDelegate {

    public int getDeploymentInfoOrdinal(int ordinal);

    public int getDeploymentLabelOrdinal(int ordinal);

    public int getDeploymentPriority(int ordinal);

    public Integer getDeploymentPriorityBoxed(int ordinal);

    public int getS3PathComponentOrdinal(int ordinal);

    public int getS3FullPathOrdinal(int ordinal);

    public StreamDeploymentTypeAPI getTypeAPI();

}