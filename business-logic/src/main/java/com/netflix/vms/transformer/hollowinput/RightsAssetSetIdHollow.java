package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsAssetSetIdHollow extends HollowObject {

    public RightsAssetSetIdHollow(RightsAssetSetIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean _isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsAssetSetIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsAssetSetIdDelegate delegate() {
        return (RightsAssetSetIdDelegate)delegate;
    }

}