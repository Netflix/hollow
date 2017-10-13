package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.FloatRetriever;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class SetOfFloat extends HollowSet<HFloat> {

    public SetOfFloat(HollowSetDelegate<HFloat> delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HFloat instantiateElement(int ordinal) {
        return retriever().getHFloat(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    private FloatRetriever retriever() {
        return (FloatRetriever) typeApi().getAPI();
    }

    public SetOfFloatTypeAPI typeApi() {
        return (SetOfFloatTypeAPI) delegate.getTypeAPI();
    }
}
