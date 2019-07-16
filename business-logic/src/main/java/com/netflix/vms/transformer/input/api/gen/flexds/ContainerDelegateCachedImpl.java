package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ContainerDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ContainerDelegate {

    private final Integer sequenceNumber;
    private final Long parentId;
    private final Long dataId;
    private ContainerTypeAPI typeAPI;

    public ContainerDelegateCachedImpl(ContainerTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.parentId = typeAPI.getParentIdBoxed(ordinal);
        this.dataId = typeAPI.getDataIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSequenceNumber(int ordinal) {
        if(sequenceNumber == null)
            return Integer.MIN_VALUE;
        return sequenceNumber.intValue();
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getParentId(int ordinal) {
        if(parentId == null)
            return Long.MIN_VALUE;
        return parentId.longValue();
    }

    public Long getParentIdBoxed(int ordinal) {
        return parentId;
    }

    public long getDataId(int ordinal) {
        if(dataId == null)
            return Long.MIN_VALUE;
        return dataId.longValue();
    }

    public Long getDataIdBoxed(int ordinal) {
        return dataId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ContainerTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ContainerTypeAPI) typeAPI;
    }

}