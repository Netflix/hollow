package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class ShowSeasonEpisodeHollowFactory<T extends ShowSeasonEpisode> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ShowSeasonEpisode(((ShowSeasonEpisodeTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ShowSeasonEpisode(new ShowSeasonEpisodeDelegateCachedImpl((ShowSeasonEpisodeTypeAPI)typeAPI, ordinal), ordinal);
    }

}