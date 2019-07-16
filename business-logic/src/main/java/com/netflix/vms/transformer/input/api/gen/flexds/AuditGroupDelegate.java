package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface AuditGroupDelegate extends HollowObjectDelegate {

    public String getUser(int ordinal);

    public boolean isUserEqual(int ordinal, String testValue);

    public int getUserOrdinal(int ordinal);

    public String getApp(int ordinal);

    public boolean isAppEqual(int ordinal, String testValue);

    public int getAppOrdinal(int ordinal);

    public long getTimestamp(int ordinal);

    public Long getTimestampBoxed(int ordinal);

    public int getRevision(int ordinal);

    public Integer getRevisionBoxed(int ordinal);

    public AuditGroupTypeAPI getTypeAPI();

}