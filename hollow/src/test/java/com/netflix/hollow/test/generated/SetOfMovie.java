package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfMovie extends HollowSet<Movie> {

    public SetOfMovie(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public Movie instantiateElement(int ordinal) {
        return (Movie) api().getMovie(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public AwardsAPI api() {
        return typeApi().getAPI();
    }

    public SetOfMovieTypeAPI typeApi() {
        return (SetOfMovieTypeAPI) delegate.getTypeAPI();
    }

}