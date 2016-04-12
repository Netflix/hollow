package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowMap;
import com.netflix.hollow.HollowMapSchema;
import com.netflix.hollow.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutMapOfLaunchDatesHollow extends HollowMap<ISOCountryHollow, DateHollow> {

    public RolloutMapOfLaunchDatesHollow(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ISOCountryHollow instantiateKey(int ordinal) {
        return (ISOCountryHollow) api().getISOCountryHollow(ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutMapOfLaunchDatesTypeAPI typeApi() {
        return (RolloutMapOfLaunchDatesTypeAPI) delegate.getTypeAPI();
    }

}