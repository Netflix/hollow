package com.netflix.hollow.core.type;

import com.netflix.hollow.api.consumer.HollowConsumerAPI.StringRetriever;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

public class SetOfString extends HollowSet<HString> {

    public SetOfString(HollowSetDelegate<HString> delegate, int ordinal) {
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

    public SetOfStringTypeAPI typeApi() {
        return (SetOfStringTypeAPI) delegate.getTypeAPI();
    }
}
