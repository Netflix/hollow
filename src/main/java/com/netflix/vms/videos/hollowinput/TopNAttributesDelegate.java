package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TopNAttributesDelegate extends HollowObjectDelegate {

    public int getCountryOrdinal(int ordinal);

    public int getViewShareOrdinal(int ordinal);

    public int getCountryViewHrsOrdinal(int ordinal);

    public TopNAttributesTypeAPI getTypeAPI();

}