package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AuditGroup extends HollowObject {

    public AuditGroup(AuditGroupDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getUser() {
        return delegate().getUser(ordinal);
    }

    public boolean isUserEqual(String testValue) {
        return delegate().isUserEqual(ordinal, testValue);
    }

    public HString getUserHollowReference() {
        int refOrdinal = delegate().getUserOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getApp() {
        return delegate().getApp(ordinal);
    }

    public boolean isAppEqual(String testValue) {
        return delegate().isAppEqual(ordinal, testValue);
    }

    public HString getAppHollowReference() {
        int refOrdinal = delegate().getAppOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public long getTimestamp() {
        return delegate().getTimestamp(ordinal);
    }

    public Long getTimestampBoxed() {
        return delegate().getTimestampBoxed(ordinal);
    }

    public int getRevision() {
        return delegate().getRevision(ordinal);
    }

    public Integer getRevisionBoxed() {
        return delegate().getRevisionBoxed(ordinal);
    }

    public FlexDSAPI api() {
        return typeApi().getAPI();
    }

    public AuditGroupTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AuditGroupDelegate delegate() {
        return (AuditGroupDelegate)delegate;
    }

}