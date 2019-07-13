package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PersonVideoRole extends HollowObject {

    public PersonVideoRole(PersonVideoRoleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public int getRoleTypeId() {
        return delegate().getRoleTypeId(ordinal);
    }

    public Integer getRoleTypeIdBoxed() {
        return delegate().getRoleTypeIdBoxed(ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public PersonVideoAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoRoleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonVideoRoleDelegate delegate() {
        return (PersonVideoRoleDelegate)delegate;
    }

}