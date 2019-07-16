package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfPhaseCastMember extends HollowSet<PhaseCastMember> {

    public SetOfPhaseCastMember(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PhaseCastMember instantiateElement(int ordinal) {
        return (PhaseCastMember) api().getPhaseCastMember(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public SetOfPhaseCastMemberTypeAPI typeApi() {
        return (SetOfPhaseCastMemberTypeAPI) delegate.getTypeAPI();
    }

}