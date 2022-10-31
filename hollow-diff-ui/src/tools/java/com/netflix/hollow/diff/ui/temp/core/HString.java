package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

@SuppressWarnings("all")
@HollowTypeName(name="String")
public class HString extends HollowObject {

    public HString(StringDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public MyNamespaceAPI api() {
        return typeApi().getAPI();
    }

    public StringTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StringDelegate delegate() {
        return (StringDelegate)delegate;
    }

}