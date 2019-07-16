package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class AuditGroupDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AuditGroupDelegate {

    private final String user;
    private final int userOrdinal;
    private final String app;
    private final int appOrdinal;
    private final Long timestamp;
    private final Integer revision;
    private AuditGroupTypeAPI typeAPI;

    public AuditGroupDelegateCachedImpl(AuditGroupTypeAPI typeAPI, int ordinal) {
        this.userOrdinal = typeAPI.getUserOrdinal(ordinal);
        int userTempOrdinal = userOrdinal;
        this.user = userTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(userTempOrdinal);
        this.appOrdinal = typeAPI.getAppOrdinal(ordinal);
        int appTempOrdinal = appOrdinal;
        this.app = appTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(appTempOrdinal);
        this.timestamp = typeAPI.getTimestampBoxed(ordinal);
        this.revision = typeAPI.getRevisionBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getUser(int ordinal) {
        return user;
    }

    public boolean isUserEqual(int ordinal, String testValue) {
        if(testValue == null)
            return user == null;
        return testValue.equals(user);
    }

    public int getUserOrdinal(int ordinal) {
        return userOrdinal;
    }

    public String getApp(int ordinal) {
        return app;
    }

    public boolean isAppEqual(int ordinal, String testValue) {
        if(testValue == null)
            return app == null;
        return testValue.equals(app);
    }

    public int getAppOrdinal(int ordinal) {
        return appOrdinal;
    }

    public long getTimestamp(int ordinal) {
        if(timestamp == null)
            return Long.MIN_VALUE;
        return timestamp.longValue();
    }

    public Long getTimestampBoxed(int ordinal) {
        return timestamp;
    }

    public int getRevision(int ordinal) {
        if(revision == null)
            return Integer.MIN_VALUE;
        return revision.intValue();
    }

    public Integer getRevisionBoxed(int ordinal) {
        return revision;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AuditGroupTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AuditGroupTypeAPI) typeAPI;
    }

}