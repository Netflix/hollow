package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class ProtectionTypesHollow extends HollowObject {

    public ProtectionTypesHollow(ProtectionTypesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getId() {
        return delegate().getId(ordinal);
    }

    public Long _getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ProtectionTypesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ProtectionTypesDelegate delegate() {
        return (ProtectionTypesDelegate)delegate;
    }

}