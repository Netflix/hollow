package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfContainer extends HollowSet<Container> {

    public SetOfContainer(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public Container instantiateElement(int ordinal) {
        return (Container) api().getContainer(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public FlexDSAPI api() {
        return typeApi().getAPI();
    }

    public SetOfContainerTypeAPI typeApi() {
        return (SetOfContainerTypeAPI) delegate.getTypeAPI();
    }

}