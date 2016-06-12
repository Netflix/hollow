package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CharacterQuoteDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public CharacterQuoteTypeAPI getTypeAPI();

}