package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileStore;

import com.netflix.hollow.client.HollowClientMemoryConfig;
import com.netflix.hollow.util.DefaultHashCodeFinder;
import java.util.Collections;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.hollow.read.customapi.HollowAPI;
import com.netflix.hollow.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.client.HollowAPIFactory;
import com.netflix.hollow.client.HollowUpdateListener;
import com.netflix.hollow.client.HollowClientUpdateDirector;
import com.netflix.hollow.client.HollowClient;

public class VMSInputDataClient extends HollowClient {

    public VMSInputDataClient(FileStore fileStore) {
        super(new VMSInputDataTransitionCreator(fileStore),
              new HollowClientUpdateDirector.DefaultDirector(),
              HollowUpdateListener.DEFAULT_LISTENER,
              new VMSInputDataAPIFactory(),
              new DefaultHashCodeFinder(),
              HollowClientMemoryConfig.DEFAULT_CONFIG);
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) super.getAPI();
    }

    private static class VMSInputDataAPIFactory implements HollowAPIFactory {

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess) {
            return new VMSHollowVideoInputAPI(dataAccess);
        }

        @Override
        public HollowAPI createAPI(HollowDataAccess dataAccess, HollowAPI previousCycleAPI) {
            return new VMSHollowVideoInputAPI(dataAccess, Collections.emptySet(), Collections.emptyMap(), (VMSHollowVideoInputAPI)previousCycleAPI);
        }

    }

}
