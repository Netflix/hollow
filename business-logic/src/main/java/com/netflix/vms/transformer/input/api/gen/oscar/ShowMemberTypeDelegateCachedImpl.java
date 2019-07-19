package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ShowMemberTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ShowMemberTypeDelegate {

    private final int countryCodesOrdinal;
    private final Long sequenceLabelId;
    private ShowMemberTypeTypeAPI typeAPI;

    public ShowMemberTypeDelegateCachedImpl(ShowMemberTypeTypeAPI typeAPI, int ordinal) {
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.sequenceLabelId = typeAPI.getSequenceLabelIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    public long getSequenceLabelId(int ordinal) {
        if(sequenceLabelId == null)
            return Long.MIN_VALUE;
        return sequenceLabelId.longValue();
    }

    public Long getSequenceLabelIdBoxed(int ordinal) {
        return sequenceLabelId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ShowMemberTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ShowMemberTypeTypeAPI) typeAPI;
    }

}