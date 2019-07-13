package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PersonVideoRoleDelegate extends HollowObjectDelegate {

    public int getSequenceNumber(int ordinal);

    public Integer getSequenceNumberBoxed(int ordinal);

    public int getRoleTypeId(int ordinal);

    public Integer getRoleTypeIdBoxed(int ordinal);

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public PersonVideoRoleTypeAPI getTypeAPI();

}