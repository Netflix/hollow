package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class TextStreamInfoHollow extends HollowObject {

    public TextStreamInfoHollow(TextStreamInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getTextLanguageCode() {
        int refOrdinal = delegate().getTextLanguageCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getTimedTextType() {
        int refOrdinal = delegate().getTimedTextTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getImageTimedTextMasterIndexOffset() {
        return delegate().getImageTimedTextMasterIndexOffset(ordinal);
    }

    public Long _getImageTimedTextMasterIndexOffsetBoxed() {
        return delegate().getImageTimedTextMasterIndexOffsetBoxed(ordinal);
    }

    public long _getImageTimedTextMasterIndexLength() {
        return delegate().getImageTimedTextMasterIndexLength(ordinal);
    }

    public Long _getImageTimedTextMasterIndexLengthBoxed() {
        return delegate().getImageTimedTextMasterIndexLengthBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TextStreamInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TextStreamInfoDelegate delegate() {
        return (TextStreamInfoDelegate)delegate;
    }

}