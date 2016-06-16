package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowAPIFactory;
import com.netflix.hollow.client.HollowClient;
import com.netflix.hollow.client.HollowClientMemoryConfig;
import com.netflix.hollow.client.HollowUpdateListener;
import com.netflix.hollow.read.customapi.HollowAPI;
import com.netflix.hollow.read.dataaccess.HollowDataAccess;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;
import java.util.Collections;

public class VMSInputDataClient extends HollowClient {

    public VMSInputDataClient(FileStore fileStore, String converterVip) {
        super(new VMSInputDataTransitionCreator(fileStore, converterVip),
              new VMSInputDataUpdateDirector(fileStore, converterVip),
              HollowUpdateListener.DEFAULT_LISTENER,
              new VMSInputDataAPIFactory(),
              new VMSTransformerHashCodeFinder(),
              HollowClientMemoryConfig.DEFAULT_CONFIG);
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) super.getAPI();
    }

    private static class VMSInputDataAPIFactory implements HollowAPIFactory {

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess) {
            return new VMSHollowInputAPI(dataAccess);
        }

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
            return new VMSHollowInputAPI(dataAccess, Collections.emptySet(), Collections.emptyMap(), (VMSHollowInputAPI)previousCycleAPI);
        }

    }
}
