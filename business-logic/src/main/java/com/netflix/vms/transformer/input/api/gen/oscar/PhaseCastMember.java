package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PhaseCastMember extends HollowObject {

    public PhaseCastMember(PhaseCastMemberDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public long getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public PersonId getPersonIdHollowReference() {
        int refOrdinal = delegate().getPersonIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonId(refOrdinal);
    }

    public int getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public Long getDateCreatedBoxed() {
        return delegate().getDateCreatedBoxed(ordinal);
    }

    public long getDateCreated() {
        return delegate().getDateCreated(ordinal);
    }

    public Date getDateCreatedHollowReference() {
        int refOrdinal = delegate().getDateCreatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public Long getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public long getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Date getLastUpdatedHollowReference() {
        int refOrdinal = delegate().getLastUpdatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public String getCreatedBy() {
        return delegate().getCreatedBy(ordinal);
    }

    public boolean isCreatedByEqual(String testValue) {
        return delegate().isCreatedByEqual(ordinal, testValue);
    }

    public HString getCreatedByHollowReference() {
        int refOrdinal = delegate().getCreatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getUpdatedBy() {
        return delegate().getUpdatedBy(ordinal);
    }

    public boolean isUpdatedByEqual(String testValue) {
        return delegate().isUpdatedByEqual(ordinal, testValue);
    }

    public HString getUpdatedByHollowReference() {
        int refOrdinal = delegate().getUpdatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public PhaseCastMemberTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PhaseCastMemberDelegate delegate() {
        return (PhaseCastMemberDelegate)delegate;
    }

}