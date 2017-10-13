package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.DoubleRetriever;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class SetOfDouble extends HollowSet<HDouble> {

    public SetOfDouble(HollowSetDelegate<HDouble> delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HDouble instantiateElement(int ordinal) {
        return retriever().getHDouble(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    private DoubleRetriever retriever() {
        return (DoubleRetriever) typeApi().getAPI();
    }

    public SetOfDoubleTypeAPI typeApi() {
        return (SetOfDoubleTypeAPI) delegate.getTypeAPI();
    }
}
