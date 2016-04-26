package com.netflix.vms.transformer.modules.deploymentintent;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CacheDeploymentIntentHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.DeploymentIntent;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.Collection;

public class CacheDeploymentIntentModule extends AbstractTransformModule {

    public CacheDeploymentIntentModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
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
