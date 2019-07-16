package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieSetContentLabelDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieSetContentLabelDelegate {

    private final String description;
    private final int descriptionOrdinal;
    private final Integer id;
    private final String _name;
    private MovieSetContentLabelTypeAPI typeAPI;

    public MovieSetContentLabelDelegateCachedImpl(MovieSetContentLabelTypeAPI typeAPI, int ordinal) {
        this.descriptionOrdinal = typeAPI.getDescriptionOrdinal(ordinal);
        int descriptionTempOrdinal = descriptionOrdinal;
        this.description = descriptionTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(descriptionTempOrdinal);
        this.id = typeAPI.getIdBoxed(ordinal);
        this._name = typeAPI.get_name(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getDescription(int ordinal) {
        return description;
    }

    public boolean isDescriptionEqual(int ordinal, String testValue) {
        if(testValue == null)
            return description == null;
        return testValue.equals(description);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return descriptionOrdinal;
    }

    public int getId(int ordinal) {
        if(id == null)
            return Integer.MIN_VALUE;
        return id.intValue();
    }

    public Integer getIdBoxed(int ordinal) {
        return id;
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

    public MovieSetContentLabelTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieSetContentLabelTypeAPI) typeAPI;
    }

}