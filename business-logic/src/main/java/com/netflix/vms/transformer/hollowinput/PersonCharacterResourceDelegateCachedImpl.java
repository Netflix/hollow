package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PersonCharacterResourceDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonCharacterResourceDelegate {

    private final Long id;
    private final int prefixOrdinal;
    private final int cnOrdinal;
   private PersonCharacterResourceTypeAPI typeAPI;

    public PersonCharacterResourceDelegateCachedImpl(PersonCharacterResourceTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.prefixOrdinal = typeAPI.getPrefixOrdinal(ordinal);
        this.cnOrdinal = typeAPI.getCnOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getPrefixOrdinal(int ordinal) {
        return prefixOrdinal;
    }

    public int getCnOrdinal(int ordinal) {
        return cnOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonCharacterResourceTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonCharacterResourceTypeAPI) typeAPI;
    }

}