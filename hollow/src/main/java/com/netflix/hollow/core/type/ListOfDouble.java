package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.DoubleRetriever;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class ListOfDouble extends HollowList<HDouble> {

    public ListOfDouble(HollowListDelegate<HDouble> delegate, int ordinal) {
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

    public ListOfDoubleTypeAPI typeApi() {
        return (ListOfDoubleTypeAPI) delegate.getTypeAPI();
    }
}
