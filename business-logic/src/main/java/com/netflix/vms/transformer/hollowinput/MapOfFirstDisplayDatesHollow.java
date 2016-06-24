package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowMap;
import com.netflix.hollow.HollowMapSchema;
import com.netflix.hollow.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfFirstDisplayDatesHollow extends HollowMap<MapKeyHollow, DateHollow> {

    public MapOfFirstDisplayDatesHollow(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public MapKeyHollow instantiateKey(int ordinal) {
        return (MapKeyHollow) api().getMapKeyHollow(ordinal);
    }

    @Override
    public DateHollow instantiateValue(int ordinal) {
        return (DateHollow) api().getDateHollow(ordinal);
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

    public MapOfFirstDisplayDatesTypeAPI typeApi() {
        return (MapOfFirstDisplayDatesTypeAPI) delegate.getTypeAPI();
    }

}