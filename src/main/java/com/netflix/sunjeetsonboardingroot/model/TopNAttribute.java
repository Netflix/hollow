package com.netflix.sunjeetsonboardingroot.model;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields={"country"})
public class TopNAttribute {
    String country;
    long countryViewHoursDaily;
    long videoViewHoursDaily;

    public TopNAttribute(String country, long countryViewHoursDaily, long videoViewHoursDaily) {
        this.country = country;
        this.countryViewHoursDaily = countryViewHoursDaily;
        this.videoViewHoursDaily = videoViewHoursDaily;
    }
}