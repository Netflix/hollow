package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowMap;
import com.netflix.hollow.HollowMapSchema;
import com.netflix.hollow.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class RolloutPhaseWindowMapHollow extends HollowMap<ISOCountryHollow, RolloutPhaseWindowHollow> {

    public RolloutPhaseWindowMapHollow(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ISOCountryHollow instantiateKey(int ordinal) {
        return (ISOCountryHollow) api().getISOCountryHollow(ordinal);
    }

    @Override
    public RolloutPhaseWindowHollow instantiateValue(int ordinal) {
        return (RolloutPhaseWindowHollow) api().getRolloutPhaseWindowHollow(ordinal);
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

    public RolloutPhaseWindowMapTypeAPI typeApi() {
        return (RolloutPhaseWindowMapTypeAPI) delegate.getTypeAPI();
    }

}