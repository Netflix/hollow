package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfReleaseDates extends HollowList<ReleaseDate> {

    public ListOfReleaseDates(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ReleaseDate instantiateElement(int ordinal) {
        return (ReleaseDate) api().getReleaseDate(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoDateAPI api() {
        return typeApi().getAPI();
    }

    public ListOfReleaseDatesTypeAPI typeApi() {
        return (ListOfReleaseDatesTypeAPI) delegate.getTypeAPI();
    }

}