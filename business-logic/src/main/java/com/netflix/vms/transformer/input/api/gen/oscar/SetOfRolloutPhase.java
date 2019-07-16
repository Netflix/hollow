package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfRolloutPhase extends HollowSet<RolloutPhase> {

    public SetOfRolloutPhase(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RolloutPhase instantiateElement(int ordinal) {
        return (RolloutPhase) api().getRolloutPhase(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public SetOfRolloutPhaseTypeAPI typeApi() {
        return (SetOfRolloutPhaseTypeAPI) delegate.getTypeAPI();
    }

}