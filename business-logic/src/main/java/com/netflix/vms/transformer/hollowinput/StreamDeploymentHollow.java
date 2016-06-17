package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class StreamDeploymentHollow extends HollowObject {

    public StreamDeploymentHollow(StreamDeploymentDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StreamDeploymentInfoHollow _getDeploymentInfo() {
        int refOrdinal = delegate().getDeploymentInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamDeploymentInfoHollow(refOrdinal);
    }

    public StreamDeploymentLabelSetHollow _getDeploymentLabel() {
        int refOrdinal = delegate().getDeploymentLabelOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamDeploymentLabelSetHollow(refOrdinal);
    }

    public int _getDeploymentPriority() {
        return delegate().getDeploymentPriority(ordinal);
    }

    public Integer _getDeploymentPriorityBoxed() {
        return delegate().getDeploymentPriorityBoxed(ordinal);
    }

    public StringHollow _getS3PathComponent() {
        int refOrdinal = delegate().getS3PathComponentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDeploymentTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamDeploymentDelegate delegate() {
        return (StreamDeploymentDelegate)delegate;
    }

}