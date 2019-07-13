package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ShowMemberTypeDelegate extends HollowObjectDelegate {

    public int getCountryCodesOrdinal(int ordinal);

    public long getSequenceLabelId(int ordinal);

    public Long getSequenceLabelIdBoxed(int ordinal);

    public ShowMemberTypeTypeAPI getTypeAPI();

}