package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ShowMemberTypeDelegate extends HollowObjectDelegate {

    public int getCountryCodesOrdinal(int ordinal);

    public long getSequenceLabelId(int ordinal);

    public Long getSequenceLabelIdBoxed(int ordinal);

    public ShowMemberTypeTypeAPI getTypeAPI();

}