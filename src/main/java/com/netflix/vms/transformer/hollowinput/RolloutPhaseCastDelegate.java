package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseCastDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public RolloutPhaseCastTypeAPI getTypeAPI();

}