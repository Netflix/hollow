package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoGeneralEpisodeTypeDelegate extends HollowObjectDelegate {

    public String getValue(int ordinal);

    public boolean isValueEqual(int ordinal, String testValue);

    public int getValueOrdinal(int ordinal);

    public String getCountry(int ordinal);

    public boolean isCountryEqual(int ordinal, String testValue);

    public int getCountryOrdinal(int ordinal);

    public VideoGeneralEpisodeTypeTypeAPI getTypeAPI();

}