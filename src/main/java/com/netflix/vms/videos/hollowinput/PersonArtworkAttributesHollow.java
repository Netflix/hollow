package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkAttributesHollow extends HollowObject {

    public PersonArtworkAttributesHollow(PersonArtworkAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getFile_seq() {
        int refOrdinal = delegate().getFile_seqOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonArtworkAttributesDelegate delegate() {
        return (PersonArtworkAttributesDelegate)delegate;
    }

}