package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class StreamDeploymentTypeAPI extends HollowObjectTypeAPI {

    private final StreamDeploymentDelegateLookupImpl delegateLookupImpl;

    StreamDeploymentTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "deploymentInfo",
            "deploymentLabel",
            "deploymentPriority"
        });
        this.delegateLookupImpl = new StreamDeploymentDelegateLookupImpl(this);
    }

    public int getDeploymentInfoOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDeployment", ordinal, "deploymentInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StreamDeploymentInfoTypeAPI getDeploymentInfoTypeAPI() {
        return getAPI().getStreamDeploymentInfoTypeAPI();
    }

    public int getDeploymentLabelOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDeployment", ordinal, "deploymentLabel");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StreamDeploymentLabelSetTypeAPI getDeploymentLabelTypeAPI() {
        return getAPI().getStreamDeploymentLabelSetTypeAPI();
    }

    public int getDeploymentPriority(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("StreamDeployment", ordinal, "deploymentPriority");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getDeploymentPriorityBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("StreamDeployment", ordinal, "deploymentPriority");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public StreamDeploymentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}