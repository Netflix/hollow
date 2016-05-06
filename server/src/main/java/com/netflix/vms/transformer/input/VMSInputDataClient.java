package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileStore;

import com.netflix.hollow.client.HollowClientMemoryConfig;
import com.netflix.hollow.util.DefaultHashCodeFinder;
import java.util.Collections;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.hollow.read.customapi.HollowAPI;
import com.netflix.hollow.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.client.HollowAPIFactory;
import com.netflix.hollow.client.HollowUpdateListener;
import com.netflix.hollow.client.HollowClientUpdateDirector;
import com.netflix.hollow.client.HollowClient;

public class VMSInputDataClient extends HollowClient {

    public VMSInputDataClient(FileStore fileStore, String converterVip) {
        super(new VMSInputDataTransitionCreator(fileStore, converterVip),
              new HollowClientUpdateDirector.DefaultDirector(),
              HollowUpdateListener.DEFAULT_LISTENER,
              new VMSInputDataAPIFactory(),
              new DefaultHashCodeFinder(),
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
