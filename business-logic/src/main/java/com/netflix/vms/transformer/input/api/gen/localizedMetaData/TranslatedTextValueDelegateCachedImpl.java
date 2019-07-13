package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TranslatedTextValueDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TranslatedTextValueDelegate {

    private final String value;
    private final int valueOrdinal;
    private TranslatedTextValueTypeAPI typeAPI;

    public TranslatedTextValueDelegateCachedImpl(TranslatedTextValueTypeAPI typeAPI, int ordinal) {
        this.valueOrdinal = typeAPI.getValueOrdinal(ordinal);
        int valueTempOrdinal = valueOrdinal;
        this.value = valueTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(valueTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return value;
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        if(testValue == null)
            return value == null;
        return testValue.equals(value);
    }

    public int getValueOrdinal(int ordinal) {
        return valueOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TranslatedTextValueTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TranslatedTextValueTypeAPI) typeAPI;
    }

}