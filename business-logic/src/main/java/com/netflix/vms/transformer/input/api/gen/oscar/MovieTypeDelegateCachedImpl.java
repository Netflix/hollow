package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieTypeDelegate {

    private final Boolean streamingType;
    private final Boolean viewable;
    private final Boolean merchable;
    private final String _name;
    private MovieTypeTypeAPI typeAPI;

    public MovieTypeDelegateCachedImpl(MovieTypeTypeAPI typeAPI, int ordinal) {
        this.streamingType = typeAPI.getStreamingTypeBoxed(ordinal);
        this.viewable = typeAPI.getViewableBoxed(ordinal);
        this.merchable = typeAPI.getMerchableBoxed(ordinal);
        this._name = typeAPI.get_name(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getStreamingType(int ordinal) {
        if(streamingType == null)
            return false;
        return streamingType.booleanValue();
    }

    public Boolean getStreamingTypeBoxed(int ordinal) {
        return streamingType;
    }

    public boolean getViewable(int ordinal) {
        if(viewable == null)
            return false;
        return viewable.booleanValue();
    }

    public Boolean getViewableBoxed(int ordinal) {
        return viewable;
    }

    public boolean getMerchable(int ordinal) {
        if(merchable == null)
            return false;
        return merchable.booleanValue();
    }

    public Boolean getMerchableBoxed(int ordinal) {
        return merchable;
    }

    public String get_name(int ordinal) {
        return _name;
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return _name == null;
        return testValue.equals(_name);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MovieTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieTypeTypeAPI) typeAPI;
    }

}