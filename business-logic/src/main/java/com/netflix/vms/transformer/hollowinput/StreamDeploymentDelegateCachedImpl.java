package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamDeploymentDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamDeploymentDelegate {

    private final int deploymentInfoOrdinal;
    private final int deploymentLabelOrdinal;
    private final Integer deploymentPriority;
    private final int s3PathComponentOrdinal;
    private final int s3FullPathOrdinal;
   private StreamDeploymentTypeAPI typeAPI;

    public StreamDeploymentDelegateCachedImpl(StreamDeploymentTypeAPI typeAPI, int ordinal) {
        this.deploymentInfoOrdinal = typeAPI.getDeploymentInfoOrdinal(ordinal);
        this.deploymentLabelOrdinal = typeAPI.getDeploymentLabelOrdinal(ordinal);
        this.deploymentPriority = typeAPI.getDeploymentPriorityBoxed(ordinal);
        this.s3PathComponentOrdinal = typeAPI.getS3PathComponentOrdinal(ordinal);
        this.s3FullPathOrdinal = typeAPI.getS3FullPathOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getDeploymentInfoOrdinal(int ordinal) {
        return deploymentInfoOrdinal;
    }

    public int getDeploymentLabelOrdinal(int ordinal) {
        return deploymentLabelOrdinal;
    }

    public int getDeploymentPriority(int ordinal) {
        return deploymentPriority.intValue();
    }

    public Integer getDeploymentPriorityBoxed(int ordinal) {
        return deploymentPriority;
    }

    public int getS3PathComponentOrdinal(int ordinal) {
        return s3PathComponentOrdinal;
    }

    public int getS3FullPathOrdinal(int ordinal) {
        return s3FullPathOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamDeploymentTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamDeploymentTypeAPI) typeAPI;
    }

}