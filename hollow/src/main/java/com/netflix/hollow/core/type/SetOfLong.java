package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.LongRetriever;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class SetOfLong extends HollowSet<HLong> {

    public SetOfLong(HollowSetDelegate<HLong> delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HLong instantiateElement(int ordinal) {
        return retriever().getHLong(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    private LongRetriever retriever() {
        return (LongRetriever) typeApi().getAPI();
    }

    public SetOfLongTypeAPI typeApi() {
        return (SetOfLongTypeAPI) delegate.getTypeAPI();
    }
}
