package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonsDelegate extends HollowObjectDelegate {

    public int getNameOrdinal(int ordinal);

    public int getBioOrdinal(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public PersonsTypeAPI getTypeAPI();

}