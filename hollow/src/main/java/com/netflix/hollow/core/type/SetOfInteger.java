package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.IntegerRetriever;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class SetOfInteger extends HollowSet<HInteger> {

    public SetOfInteger(HollowSetDelegate<HInteger> delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HInteger instantiateElement(int ordinal) {
        return retriever().getHInteger(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    private IntegerRetriever retriever() {
        return (IntegerRetriever) typeApi().getAPI();
    }

    public SetOfIntegerTypeAPI typeApi() {
        return (SetOfIntegerTypeAPI) delegate.getTypeAPI();
    }
}
