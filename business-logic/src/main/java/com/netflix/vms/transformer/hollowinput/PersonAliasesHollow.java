package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class PersonAliasesHollow extends HollowObject {

    public PersonAliasesHollow(PersonAliasesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAliasId() {
        return delegate().getAliasId(ordinal);
    }

    public Long _getAliasIdBoxed() {
        return delegate().getAliasIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonAliasesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonAliasesDelegate delegate() {
        return (PersonAliasesDelegate)delegate;
    }

}