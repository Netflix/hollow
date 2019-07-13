package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class RolloutPhaseWindowMap extends HollowMap<ISOCountry, RolloutPhaseWindow> {

    public RolloutPhaseWindowMap(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ISOCountry instantiateKey(int ordinal) {
        return (ISOCountry) api().getISOCountry(ordinal);
    }

    @Override
    public RolloutPhaseWindow instantiateValue(int ordinal) {
        return (RolloutPhaseWindow) api().getRolloutPhaseWindow(ordinal);
    }

    public RolloutPhaseWindow get(String k0) {
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

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseWindowMapTypeAPI typeApi() {
        return (RolloutPhaseWindowMapTypeAPI) delegate.getTypeAPI();
    }

}