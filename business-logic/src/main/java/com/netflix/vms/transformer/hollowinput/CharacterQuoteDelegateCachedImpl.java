package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CharacterQuoteDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharacterQuoteDelegate {

    private final Long sequenceNumber;
   private CharacterQuoteTypeAPI typeAPI;

    public CharacterQuoteDelegateCachedImpl(CharacterQuoteTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CharacterQuoteTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CharacterQuoteTypeAPI) typeAPI;
    }

}