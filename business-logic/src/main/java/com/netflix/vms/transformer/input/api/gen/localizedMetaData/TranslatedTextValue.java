package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class TranslatedTextValue extends HollowObject {

    public TranslatedTextValue(TranslatedTextValueDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public HString getValueHollowReference() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public LocalizedMetaDataAPI api() {
        return typeApi().getAPI();
    }

    public TranslatedTextValueTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TranslatedTextValueDelegate delegate() {
        return (TranslatedTextValueDelegate)delegate;
    }

}