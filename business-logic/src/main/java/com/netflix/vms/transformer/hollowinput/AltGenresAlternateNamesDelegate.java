package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface AltGenresAlternateNamesDelegate extends HollowObjectDelegate {

    public long getTypeId(int ordinal);

    public Long getTypeIdBoxed(int ordinal);

    public int getTypeOrdinal(int ordinal);

    public int getTranslatedTextsOrdinal(int ordinal);

    public AltGenresAlternateNamesTypeAPI getTypeAPI();

}