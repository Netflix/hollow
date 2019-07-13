package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DealCountryGroupDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DealCountryGroupDelegate {

    private final Long dealId;
    private final int dealIdOrdinal;
    private final int countryWindowOrdinal;
    private DealCountryGroupTypeAPI typeAPI;

    public DealCountryGroupDelegateCachedImpl(DealCountryGroupTypeAPI typeAPI, int ordinal) {
        this.dealIdOrdinal = typeAPI.getDealIdOrdinal(ordinal);
        int dealIdTempOrdinal = dealIdOrdinal;
        this.dealId = dealIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(dealIdTempOrdinal);
        this.countryWindowOrdinal = typeAPI.getCountryWindowOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getDealId(int ordinal) {
        if(dealId == null)
            return Long.MIN_VALUE;
        return dealId.longValue();
    }

    public Long getDealIdBoxed(int ordinal) {
        return dealId;
    }

    public int getDealIdOrdinal(int ordinal) {
        return dealIdOrdinal;
    }

    public int getCountryWindowOrdinal(int ordinal) {
        return countryWindowOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DealCountryGroupTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DealCountryGroupTypeAPI) typeAPI;
    }

}