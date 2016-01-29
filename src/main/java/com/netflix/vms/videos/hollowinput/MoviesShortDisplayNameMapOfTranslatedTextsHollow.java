package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowMap;
import com.netflix.hollow.HollowMapSchema;
import com.netflix.hollow.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class MoviesShortDisplayNameMapOfTranslatedTextsHollow extends HollowMap<MapKeyHollow, MoviesShortDisplayNameTranslatedTextsHollow> {

    public MoviesShortDisplayNameMapOfTranslatedTextsHollow(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapKeyHollow instantiateKey(int ordinal) {
        return (MapKeyHollow) api().getMapKeyHollow(ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MoviesShortDisplayNameTranslatedTextsHollow instantiateValue(int ordinal) {
        return (MoviesShortDisplayNameTranslatedTextsHollow) api().getMoviesShortDisplayNameTranslatedTextsHollow(ordinal);
    }

    @Override
    public boolean equalsKey(int keyOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getKeyType(), keyOrdinal, testObject);
    }

    @Override
    public boolean equalsValue(int valueOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getValueType(), valueOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesShortDisplayNameMapOfTranslatedTextsTypeAPI typeApi() {
        return (MoviesShortDisplayNameMapOfTranslatedTextsTypeAPI) delegate.getTypeAPI();
    }

}