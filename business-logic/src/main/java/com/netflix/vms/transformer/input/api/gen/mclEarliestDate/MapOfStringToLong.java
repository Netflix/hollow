package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfStringToLong extends HollowMap<HString, HLong> {

    public MapOfStringToLong(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HString instantiateKey(int ordinal) {
        return (HString) api().getHString(ordinal);
    }

    @Override
    public HLong instantiateValue(int ordinal) {
        return (HLong) api().getHLong(ordinal);
    }

    public HLong get(String k0) {
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

    public MclEarliestDateAPI api() {
        return typeApi().getAPI();
    }

    public MapOfStringToLongTypeAPI typeApi() {
        return (MapOfStringToLongTypeAPI) delegate.getTypeAPI();
    }

}