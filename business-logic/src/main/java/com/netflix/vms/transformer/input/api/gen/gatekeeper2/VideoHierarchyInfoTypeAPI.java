package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoHierarchyInfoTypeAPI extends HollowObjectTypeAPI {

    private final VideoHierarchyInfoDelegateLookupImpl delegateLookupImpl;

    public VideoHierarchyInfoTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "topNodeId",
            "parentId",
            "nodeType"
        });
        this.delegateLookupImpl = new VideoHierarchyInfoDelegateLookupImpl(this);
    }

    public int getTopNodeIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoHierarchyInfo", ordinal, "topNodeId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ParentNodeIdTypeAPI getTopNodeIdTypeAPI() {
        return getAPI().getParentNodeIdTypeAPI();
    }

    public int getParentIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoHierarchyInfo", ordinal, "parentId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ParentNodeIdTypeAPI getParentIdTypeAPI() {
        return getAPI().getParentNodeIdTypeAPI();
    }

    public int getNodeTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoHierarchyInfo", ordinal, "nodeType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoNodeTypeTypeAPI getNodeTypeTypeAPI() {
        return getAPI().getVideoNodeTypeTypeAPI();
    }

    public VideoHierarchyInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}