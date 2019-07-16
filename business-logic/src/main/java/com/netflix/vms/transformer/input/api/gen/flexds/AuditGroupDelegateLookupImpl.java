package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AuditGroupDelegateLookupImpl extends HollowObjectAbstractDelegate implements AuditGroupDelegate {

    private final AuditGroupTypeAPI typeAPI;

    public AuditGroupDelegateLookupImpl(AuditGroupTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getUser(int ordinal) {
        ordinal = typeAPI.getUserOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isUserEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getUserOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getUserOrdinal(int ordinal) {
        return typeAPI.getUserOrdinal(ordinal);
    }

    public String getApp(int ordinal) {
        ordinal = typeAPI.getAppOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isAppEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getAppOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getAppOrdinal(int ordinal) {
        return typeAPI.getAppOrdinal(ordinal);
    }

    public long getTimestamp(int ordinal) {
        return typeAPI.getTimestamp(ordinal);
    }

    public Long getTimestampBoxed(int ordinal) {
        return typeAPI.getTimestampBoxed(ordinal);
    }

    public int getRevision(int ordinal) {
        return typeAPI.getRevision(ordinal);
    }

    public Integer getRevisionBoxed(int ordinal) {
        return typeAPI.getRevisionBoxed(ordinal);
    }

    public AuditGroupTypeAPI getTypeAPI() {
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