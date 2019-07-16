package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DisplaySetDelegate extends HollowObjectDelegate {

    public long getSetId(int ordinal);

    public Long getSetIdBoxed(int ordinal);

    public int getCountryCodesOrdinal(int ordinal);

    public boolean getIsDefault(int ordinal);

    public Boolean getIsDefaultBoxed(int ordinal);

    public int getDisplaySetTypesOrdinal(int ordinal);

    public int getContainersOrdinal(int ordinal);

    public int getCreatedOrdinal(int ordinal);

    public int getUpdatedOrdinal(int ordinal);

    public DisplaySetTypeAPI getTypeAPI();

}