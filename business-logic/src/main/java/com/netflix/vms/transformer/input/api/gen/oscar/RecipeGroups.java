package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RecipeGroups extends HollowObject {

    public RecipeGroups(RecipeGroupsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public RecipeGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RecipeGroupsDelegate delegate() {
        return (RecipeGroupsDelegate)delegate;
    }

}