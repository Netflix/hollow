package com.netflix.vms.transformer.input;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.hollow.api.client.HollowClientMemoryConfig;
import com.netflix.hollow.api.client.HollowUpdateListener;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import java.util.Collections;

@Deprecated
public class VMSInputDataClient extends HollowClient {

    public static final String TEST_PROXY_URL = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-test";
    public static final String PROD_PROXY_URL = "http://discovery.cloud.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-prod";

    public VMSInputDataClient(String baseProxyURL, String localDataDir, String converterVip) {
        super(new VMSDataProxyTransitionCreator(baseProxyURL, localDataDir, converterVip),
              new VMSInputDataProxyUpdateDirector(baseProxyURL, converterVip),
              HollowUpdateListener.DEFAULT_LISTENER,
              new VMSInputDataAPIFactory(),
              new DefaultHashCodeFinder(),
              HollowClientMemoryConfig.DEFAULT_CONFIG);
        setMaxDeltas(40);
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) super.getAPI();
    }

    private static class VMSInputDataAPIFactory implements HollowAPIFactory {

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess) {
            VMSHollowInputAPI api = new VMSHollowInputAPI(dataAccess);
            api.setSamplingDirector(DisabledSamplingDirector.INSTANCE);
            return api;
        }

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
            VMSHollowInputAPI api = new VMSHollowInputAPI(dataAccess, Collections.emptySet(), Collections.emptyMap(), (VMSHollowInputAPI)previousCycleAPI);
            api.setSamplingDirector(DisabledSamplingDirector.INSTANCE);
            return api;
        }
    }
}
