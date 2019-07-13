package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoDateWindowDelegate extends HollowObjectDelegate {

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public int getReleaseDatesOrdinal(int ordinal);

    public VideoDateWindowTypeAPI getTypeAPI();

}