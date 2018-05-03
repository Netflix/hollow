package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class CharacterQuoteHollow extends HollowObject {

    public CharacterQuoteHollow(CharacterQuoteDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterQuoteTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterQuoteDelegate delegate() {
        return (CharacterQuoteDelegate)delegate;
    }

}