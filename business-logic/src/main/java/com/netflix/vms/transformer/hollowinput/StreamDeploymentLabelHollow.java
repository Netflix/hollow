package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class StreamDeploymentLabelHollow extends HollowObject {

    public StreamDeploymentLabelHollow(StreamDeploymentLabelDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDeploymentLabelTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamDeploymentLabelDelegate delegate() {
        return (StreamDeploymentLabelDelegate)delegate;
    }

}