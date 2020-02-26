package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfTopNAttribute extends HollowSet<TopNAttribute> {

    public SetOfTopNAttribute(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public TopNAttribute instantiateElement(int ordinal) {
        return (TopNAttribute) api().getTopNAttribute(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public TopNAPI api() {
        return typeApi().getAPI();
    }

    public SetOfTopNAttributeTypeAPI typeApi() {
        return (SetOfTopNAttributeTypeAPI) delegate.getTypeAPI();
    }

}