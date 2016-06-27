package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DrmHeaderInfoDelegate extends HollowObjectDelegate {

    public int getKeyIdOrdinal(int ordinal);

    public long getDrmSystemId(int ordinal);

    public Long getDrmSystemIdBoxed(int ordinal);

    public int getChecksumOrdinal(int ordinal);

    public DrmHeaderInfoTypeAPI getTypeAPI();

}