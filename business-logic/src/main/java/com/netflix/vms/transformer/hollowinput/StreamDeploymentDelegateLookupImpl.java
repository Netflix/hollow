package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamDeploymentDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamDeploymentDelegate {

    private final StreamDeploymentTypeAPI typeAPI;

    public StreamDeploymentDelegateLookupImpl(StreamDeploymentTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getDeploymentInfoOrdinal(int ordinal) {
        return typeAPI.getDeploymentInfoOrdinal(ordinal);
    }

    public int getDeploymentLabelOrdinal(int ordinal) {
        return typeAPI.getDeploymentLabelOrdinal(ordinal);
    }

    public int getDeploymentPriority(int ordinal) {
        return typeAPI.getDeploymentPriority(ordinal);
    }

    public Integer getDeploymentPriorityBoxed(int ordinal) {
        return typeAPI.getDeploymentPriorityBoxed(ordinal);
    }

    public int getS3PathComponentOrdinal(int ordinal) {
        return typeAPI.getS3PathComponentOrdinal(ordinal);
    }

    public int getS3FullPathOrdinal(int ordinal) {
        return typeAPI.getS3FullPathOrdinal(ordinal);
    }

    public StreamDeploymentTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}