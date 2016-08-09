package com.netflix.vms.transformer.input;

import com.netflix.hollow.sampling.DisabledSamplingDirector;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowAPIFactory;
import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.client.HollowClientMemoryConfig;
import com.netflix.hollow.client.HollowUpdateListener;
import com.netflix.hollow.read.customapi.HollowAPI;
import com.netflix.hollow.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.util.DefaultHashCodeFinder;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import java.util.Collections;

public class VMSInputDataClient extends HollowClient {
    
    public static final String TEST_PROXY_URL = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-test";
    public static final String PROD_PROXY_URL = "http://discovery.cloud.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-prod";

    public VMSInputDataClient(FileStore fileStore, String converterVip) {
        super(new VMSDataTransitionCreator(fileStore, converterVip),
              new VMSInputDataUpdateDirector(fileStore, converterVip),
              HollowUpdateListener.DEFAULT_LISTENER,
              new VMSInputDataAPIFactory(),
              new DefaultHashCodeFinder(),
              HollowClientMemoryConfig.DEFAULT_CONFIG);
        setMaxDeltas(40);
    }

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
