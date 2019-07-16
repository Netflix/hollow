package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsWindow extends HollowObject {

    public RightsWindow(RightsWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getStartDate() {
        return delegate().getStartDate(ordinal);
    }

    public Long getStartDateBoxed() {
        return delegate().getStartDateBoxed(ordinal);
    }

    public long getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Long getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
    }

    public boolean getOnHold() {
        return delegate().getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed() {
        return delegate().getOnHoldBoxed(ordinal);
    }

    public ListOfRightsWindowContract getContractIdsExt() {
        int refOrdinal = delegate().getContractIdsExtOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsWindowContract(refOrdinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public RightsWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsWindowDelegate delegate() {
        return (RightsWindowDelegate)delegate;
    }

}