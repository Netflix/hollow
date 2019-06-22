package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfFlagsFirstDisplayDates extends HollowMap<MapKey, Date> {

    public MapOfFlagsFirstDisplayDates(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public MapKey instantiateKey(int ordinal) {
        return (MapKey) api().getMapKey(ordinal);
    }

    @Override
    public Date instantiateValue(int ordinal) {
        return (Date) api().getDate(ordinal);
    }

    public Date get(String k0) {
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

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public MapOfFlagsFirstDisplayDatesTypeAPI typeApi() {
        return (MapOfFlagsFirstDisplayDatesTypeAPI) delegate.getTypeAPI();
    }

}