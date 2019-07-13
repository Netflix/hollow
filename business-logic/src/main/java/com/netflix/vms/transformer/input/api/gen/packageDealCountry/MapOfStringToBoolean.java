package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfStringToBoolean extends HollowMap<HString, HBoolean> {

    public MapOfStringToBoolean(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HString instantiateKey(int ordinal) {
        return (HString) api().getHString(ordinal);
    }

    @Override
    public HBoolean instantiateValue(int ordinal) {
        return (HBoolean) api().getHBoolean(ordinal);
    }

    public HBoolean get(String k0) {
        return findValue(k0);
    }

    @Override
    public boolean equalsKey(int keyOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getKeyType(), keyOrdinal, testObject);
    }

    @Override
    public boolean equalsValue(int valueOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getValueType(), valueOrdinal, testObject);
    }

    public PackageDealCountryAPI api() {
        return typeApi().getAPI();
    }

    public MapOfStringToBooleanTypeAPI typeApi() {
        return (MapOfStringToBooleanTypeAPI) delegate.getTypeAPI();
    }

}