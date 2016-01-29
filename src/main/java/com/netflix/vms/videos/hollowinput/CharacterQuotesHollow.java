package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterQuotesHollow extends HollowObject {

    public CharacterQuotesHollow(CharacterQuotesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterQuotesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterQuotesDelegate delegate() {
        return (CharacterQuotesDelegate)delegate;
    }

}