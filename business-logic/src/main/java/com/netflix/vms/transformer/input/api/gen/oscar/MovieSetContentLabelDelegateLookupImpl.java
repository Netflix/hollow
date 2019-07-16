package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieSetContentLabelDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieSetContentLabelDelegate {

    private final MovieSetContentLabelTypeAPI typeAPI;

    public MovieSetContentLabelDelegateLookupImpl(MovieSetContentLabelTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getDescription(int ordinal) {
        ordinal = typeAPI.getDescriptionOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isDescriptionEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getDescriptionOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return typeAPI.getDescriptionOrdinal(ordinal);
    }

    public int getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Integer getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public String get_name(int ordinal) {
        return typeAPI.get_name(ordinal);
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        return typeAPI.is_nameEqual(ordinal, testValue);
    }

    public MovieSetContentLabelTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}