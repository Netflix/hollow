package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonBioDelegate extends HollowObjectDelegate {

    public int getSpousesOrdinal(int ordinal);

    public int getPartnersOrdinal(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public int getBirthDateOrdinal(int ordinal);

    public int getMovieIdsOrdinal(int ordinal);

    public PersonBioTypeAPI getTypeAPI();

}