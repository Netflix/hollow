package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoDisplaySetSetsDelegate extends HollowObjectDelegate {

    public int getChildrenOrdinal(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public int getSetTypeOrdinal(int ordinal);

    public VideoDisplaySetSetsTypeAPI getTypeAPI();

}