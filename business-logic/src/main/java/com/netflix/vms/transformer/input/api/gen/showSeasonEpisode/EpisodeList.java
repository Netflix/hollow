package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class EpisodeList extends HollowList<Episode> {

    public EpisodeList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public Episode instantiateElement(int ordinal) {
        return (Episode) api().getEpisode(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public ShowSeasonEpisodeAPI api() {
        return typeApi().getAPI();
    }

    public EpisodeListTypeAPI typeApi() {
        return (EpisodeListTypeAPI) delegate.getTypeAPI();
    }

}