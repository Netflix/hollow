package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoPersonDelegate extends HollowObjectDelegate {

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public int getCastOrdinal(int ordinal);

    public int getAliasOrdinal(int ordinal);

    public VideoPersonTypeAPI getTypeAPI();

}