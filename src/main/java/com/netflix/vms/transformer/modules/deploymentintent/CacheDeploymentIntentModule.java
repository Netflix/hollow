package com.netflix.vms.transformer.modules.deploymentintent;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.CacheDeploymentIntentHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.DeploymentIntent;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import java.util.Collection;

public class CacheDeploymentIntentModule {
    private final VMSHollowVideoInputAPI api;
    private final HollowObjectMapper mapper;

    public CacheDeploymentIntentModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    public void transform() {
        Collection<CacheDeploymentIntentHollow> inputs = api.getAllCacheDeploymentIntentHollow();
        for (CacheDeploymentIntentHollow input : inputs) {
            DeploymentIntent di = new DeploymentIntent();
            di.profileId = (int) input._getStreamProfileId();
            di.bitrate = (int) input._getBitrateKBPS();
            di.country = new ISOCountry(input._getIsoCountryCode()._getValue());
            mapper.addObject(di);
        }
    }
}
