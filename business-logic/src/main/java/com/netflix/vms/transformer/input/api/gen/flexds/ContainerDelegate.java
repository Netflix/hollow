package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ContainerDelegate extends HollowObjectDelegate {

    public int getSequenceNumber(int ordinal);

    public Integer getSequenceNumberBoxed(int ordinal);

    public long getParentId(int ordinal);

    public Long getParentIdBoxed(int ordinal);

    public long getDataId(int ordinal);

    public Long getDataIdBoxed(int ordinal);

    public ContainerTypeAPI getTypeAPI();

}