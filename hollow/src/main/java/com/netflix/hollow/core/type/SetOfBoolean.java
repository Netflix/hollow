package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.BooleanRetriever;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class SetOfBoolean extends HollowSet<HBoolean> {

    public SetOfBoolean(HollowSetDelegate<HBoolean> delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HBoolean instantiateElement(int ordinal) {
        return retriever().getHBoolean(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    private BooleanRetriever retriever() {
        return (BooleanRetriever) typeApi().getAPI();
    }

    public SetOfBooleanTypeAPI typeApi() {
        return (SetOfBooleanTypeAPI) delegate.getTypeAPI();
    }
}
