package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.IntegerRetriever;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class ListOfInteger extends HollowList<HInteger> {

    public ListOfInteger(HollowListDelegate<HInteger> delegate, int ordinal) {
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

    public ListOfIntegerTypeAPI typeApi() {
        return (ListOfIntegerTypeAPI) delegate.getTypeAPI();
    }
}
