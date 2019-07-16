package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class AuditGroupTypeAPI extends HollowObjectTypeAPI {

    private final AuditGroupDelegateLookupImpl delegateLookupImpl;

    public AuditGroupTypeAPI(FlexDSAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "user",
            "app",
            "timestamp",
            "revision"
        });
        this.delegateLookupImpl = new AuditGroupDelegateLookupImpl(this);
    }

    public int getUserOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AuditGroup", ordinal, "user");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getUserTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAppOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AuditGroup", ordinal, "app");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getAppTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getTimestamp(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("AuditGroup", ordinal, "timestamp");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getTimestampBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("AuditGroup", ordinal, "timestamp");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getRevision(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("AuditGroup", ordinal, "revision");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getRevisionBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("AuditGroup", ordinal, "revision");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public AuditGroupDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public FlexDSAPI getAPI() {
        return (FlexDSAPI) api;
    }

}