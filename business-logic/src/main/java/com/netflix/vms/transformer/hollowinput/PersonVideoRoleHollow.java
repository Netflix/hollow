package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonVideoRoleHollow extends HollowObject {

    public PersonVideoRoleHollow(PersonVideoRoleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public int _getRoleTypeId() {
        return delegate().getRoleTypeId(ordinal);
    }

    public Integer _getRoleTypeIdBoxed() {
        return delegate().getRoleTypeIdBoxed(ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoRoleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonVideoRoleDelegate delegate() {
        return (PersonVideoRoleDelegate)delegate;
    }

}