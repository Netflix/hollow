package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface AwardsDelegate extends HollowObjectDelegate {

    public long getAwardId(int ordinal);

    public Long getAwardIdBoxed(int ordinal);

    public int getAwardNameOrdinal(int ordinal);

    public int getAlternateNameOrdinal(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public AwardsTypeAPI getTypeAPI();

}