package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ContractsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ContractsDelegate {

    private final Long movieId;
    private final int countryCodeOrdinal;
    private final int contractsOrdinal;
   private ContractsTypeAPI typeAPI;

    public ContractsDelegateCachedImpl(ContractsTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.contractsOrdinal = typeAPI.getContractsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getContractsOrdinal(int ordinal) {
        return contractsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ContractsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ContractsTypeAPI) typeAPI;
    }

}