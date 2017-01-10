package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamDeploymentInfoTypeAPI extends HollowObjectTypeAPI {

    private final StreamDeploymentInfoDelegateLookupImpl delegateLookupImpl;

    StreamDeploymentInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "cacheDeployedCountries",
            "cdnDeployments"
        });
        this.delegateLookupImpl = new StreamDeploymentInfoDelegateLookupImpl(this);
    }

    public int getCacheDeployedCountriesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDeploymentInfo", ordinal, "cacheDeployedCountries");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ISOCountrySetTypeAPI getCacheDeployedCountriesTypeAPI() {
        return getAPI().getISOCountrySetTypeAPI();
    }

    public int getCdnDeploymentsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamDeploymentInfo", ordinal, "cdnDeployments");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public CdnDeploymentSetTypeAPI getCdnDeploymentsTypeAPI() {
        return getAPI().getCdnDeploymentSetTypeAPI();
    }

    public StreamDeploymentInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}