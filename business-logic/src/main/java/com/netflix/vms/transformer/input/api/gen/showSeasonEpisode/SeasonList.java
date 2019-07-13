package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SeasonList extends HollowList<Season> {

    public SeasonList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public Season instantiateElement(int ordinal) {
        return (Season) api().getSeason(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public ShowSeasonEpisodeAPI api() {
        return typeApi().getAPI();
    }

    public SeasonListTypeAPI typeApi() {
        return (SeasonListTypeAPI) delegate.getTypeAPI();
    }

}