package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieTypeDelegate {

    private final MovieTypeTypeAPI typeAPI;

    public MovieTypeDelegateLookupImpl(MovieTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public boolean getStreamingType(int ordinal) {
        return typeAPI.getStreamingType(ordinal);
    }

    public Boolean getStreamingTypeBoxed(int ordinal) {
        return typeAPI.getStreamingTypeBoxed(ordinal);
    }

    public boolean getViewable(int ordinal) {
        return typeAPI.getViewable(ordinal);
    }

    public Boolean getViewableBoxed(int ordinal) {
        return typeAPI.getViewableBoxed(ordinal);
    }

    public boolean getMerchable(int ordinal) {
        return typeAPI.getMerchable(ordinal);
    }

    public Boolean getMerchableBoxed(int ordinal) {
        return typeAPI.getMerchableBoxed(ordinal);
    }

    public String get_name(int ordinal) {
        return typeAPI.get_name(ordinal);
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        return typeAPI.is_nameEqual(ordinal, testValue);
    }

    public MovieTypeTypeAPI getTypeAPI() {
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