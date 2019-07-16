package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class Rights extends HollowObject {

    public Rights(RightsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ListOfRightsWindow getWindows() {
        int refOrdinal = delegate().getWindowsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsWindow(refOrdinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public RightsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsDelegate delegate() {
        return (RightsDelegate)delegate;
    }

}