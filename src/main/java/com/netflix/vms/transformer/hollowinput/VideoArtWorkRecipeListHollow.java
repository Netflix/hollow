package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkRecipeListHollow extends HollowList<VideoArtWorkRecipesHollow> {

    public VideoArtWorkRecipeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkRecipesHollow instantiateElement(int ordinal) {
        return (VideoArtWorkRecipesHollow) api().getVideoArtWorkRecipesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkRecipeListTypeAPI typeApi() {
        return (VideoArtWorkRecipeListTypeAPI) delegate.getTypeAPI();
    }

}