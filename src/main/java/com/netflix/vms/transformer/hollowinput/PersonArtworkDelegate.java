package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PersonArtworkDelegate extends HollowObjectDelegate {

    public int getDerivativesOrdinal(int ordinal);

    public int getLocalesOrdinal(int ordinal);

    public long getSeqNum(int ordinal);

    public Long getSeqNumBoxed(int ordinal);

    public long getOrdinalPriority(int ordinal);

    public Long getOrdinalPriorityBoxed(int ordinal);

    public int getSourceFileIdOrdinal(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public PersonArtworkTypeAPI getTypeAPI();

}