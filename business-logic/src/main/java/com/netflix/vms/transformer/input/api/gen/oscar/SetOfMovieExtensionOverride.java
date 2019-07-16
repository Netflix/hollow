package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfMovieExtensionOverride extends HollowSet<MovieExtensionOverride> {

    public SetOfMovieExtensionOverride(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public MovieExtensionOverride instantiateElement(int ordinal) {
        return (MovieExtensionOverride) api().getMovieExtensionOverride(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public SetOfMovieExtensionOverrideTypeAPI typeApi() {
        return (SetOfMovieExtensionOverrideTypeAPI) delegate.getTypeAPI();
    }

}