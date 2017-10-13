package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.LongRetriever;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class ListOfLong extends HollowList<HLong> {

    public ListOfLong(HollowListDelegate<HLong> delegate, int ordinal) {
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

    public ListOfLongTypeAPI typeApi() {
        return (ListOfLongTypeAPI) delegate.getTypeAPI();
    }
}
