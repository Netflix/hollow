package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.StringRetriever;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class ListOfString extends HollowList<HString> {

    public ListOfString(HollowListDelegate<HString> delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HString instantiateElement(int ordinal) {
        return retriever().getHString(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    private StringRetriever retriever() {
        return (StringRetriever) typeApi().getAPI();
    }

    public ListOfStringTypeAPI typeApi() {
        return (ListOfStringTypeAPI) delegate.getTypeAPI();
    }
}
