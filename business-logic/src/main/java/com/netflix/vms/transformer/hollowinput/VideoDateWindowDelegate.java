package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoDateWindowDelegate extends HollowObjectDelegate {

    public int getCountryCodeOrdinal(int ordinal);

    public int getReleaseDatesOrdinal(int ordinal);

    public VideoDateWindowTypeAPI getTypeAPI();

}