package com.netflix.hollow.diff.ui.temp;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

@SuppressWarnings("all")
@HollowTypeName(name="profileId")
public class ProfileId extends HollowObject {

    public ProfileId(ProfileIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int getValue() {
        return delegate().getValue(ordinal);
    }

    public Integer getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public MyNamespaceAPI api() {
        return typeApi().getAPI();
    }

    public ProfileIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ProfileIdDelegate delegate() {
        return (ProfileIdDelegate)delegate;
    }

}