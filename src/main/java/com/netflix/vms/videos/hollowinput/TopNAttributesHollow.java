package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class TopNAttributesHollow extends HollowObject {

    public TopNAttributesHollow(TopNAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCountry() {
        int refOrdinal = delegate().getCountryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getViewShare() {
        int refOrdinal = delegate().getViewShareOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCountryViewHrs() {
        int refOrdinal = delegate().getCountryViewHrsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TopNAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TopNAttributesDelegate delegate() {
        return (TopNAttributesDelegate)delegate;
    }

}