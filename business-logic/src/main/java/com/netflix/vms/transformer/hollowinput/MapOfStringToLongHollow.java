package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfStringToLongHollow extends HollowMap<StringHollow, LongHollow> {

    public MapOfStringToLongHollow(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public StringHollow instantiateKey(int ordinal) {
        return (StringHollow) api().getStringHollow(ordinal);
    }

    @Override
    public LongHollow instantiateValue(int ordinal) {
        return (LongHollow) api().getLongHollow(ordinal);
    }

    public LongHollow get(String k0) {
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public MapOfStringToLongTypeAPI typeApi() {
        return (MapOfStringToLongTypeAPI) delegate.getTypeAPI();
    }

}