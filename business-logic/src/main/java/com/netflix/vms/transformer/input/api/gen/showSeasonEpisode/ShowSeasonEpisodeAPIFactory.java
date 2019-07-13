package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("all")
public class ShowSeasonEpisodeAPIFactory implements HollowAPIFactory {

    private final Set<String> cachedTypes;

    public ShowSeasonEpisodeAPIFactory() {
        this(Collections.<String>emptySet());
    }

    public ShowSeasonEpisodeAPIFactory(Set<String> cachedTypes) {
        this.cachedTypes = cachedTypes;
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess) {
        return new ShowSeasonEpisodeAPI(dataAccess, cachedTypes);
    }

    @Override
    public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
        if (!(previousCycleAPI instanceof ShowSeasonEpisodeAPI)) {
            throw new ClassCastException(previousCycleAPI.getClass() + " not instance of ShowSeasonEpisodeAPI");        }
        return new ShowSeasonEpisodeAPI(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap(), (ShowSeasonEpisodeAPI) previousCycleAPI);
    }

}