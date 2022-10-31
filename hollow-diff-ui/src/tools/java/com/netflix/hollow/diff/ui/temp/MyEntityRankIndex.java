package com.netflix.hollow.diff.ui.temp;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MyEntityRankIndex extends HollowObject {

    public MyEntityRankIndex(MyEntityRankIndexDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MapOfMyEntityToInteger getIndex() {
        int refOrdinal = delegate().getIndexOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfMyEntityToInteger(refOrdinal);
    }

    public MyNamespaceAPI api() {
        return typeApi().getAPI();
    }

    public MyEntityRankIndexTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MyEntityRankIndexDelegate delegate() {
        return (MyEntityRankIndexDelegate)delegate;
    }

}