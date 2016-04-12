package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowMap;
import com.netflix.hollow.HollowMapSchema;
import com.netflix.hollow.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class RolloutPhasesElementsTrailerSupplementalInfoMapHollow extends HollowMap<MapKeyHollow, RolloutPhaseTrailerSupplementalInfoHollow> {

    public RolloutPhasesElementsTrailerSupplementalInfoMapHollow(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapKeyHollow instantiateKey(int ordinal) {
        return (MapKeyHollow) api().getMapKeyHollow(ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RolloutPhaseTrailerSupplementalInfoHollow instantiateValue(int ordinal) {
        return (RolloutPhaseTrailerSupplementalInfoHollow) api().getRolloutPhaseTrailerSupplementalInfoHollow(ordinal);
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

    public RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI typeApi() {
        return (RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI) delegate.getTypeAPI();
    }

}