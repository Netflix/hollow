package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class RightsHollow extends HollowObject {

    public RightsHollow(RightsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ListOfRightsWindowHollow _getWindows() {
        int refOrdinal = delegate().getWindowsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsWindowHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsDelegate delegate() {
        return (RightsDelegate)delegate;
    }

}