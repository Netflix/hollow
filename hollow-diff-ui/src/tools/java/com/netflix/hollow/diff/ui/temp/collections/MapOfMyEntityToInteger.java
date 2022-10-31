package com.netflix.hollow.diff.ui.temp.collections;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.MyEntity;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfMyEntityToInteger extends HollowMap<MyEntity, HInteger> {

    public MapOfMyEntityToInteger(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public MyEntity instantiateKey(int ordinal) {
        return (MyEntity) api().getMyEntity(ordinal);
    }

    @Override
    public HInteger instantiateValue(int ordinal) {
        return (HInteger) api().getHInteger(ordinal);
    }

    public HInteger get(Integer k0, String k1, Integer k2) {
        return findValue(k0, k1, k2);
    }

    @Override
    public boolean equalsKey(int keyOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getKeyType(), keyOrdinal, testObject);
    }

    @Override
    public boolean equalsValue(int valueOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getValueType(), valueOrdinal, testObject);
    }

    public MyNamespaceAPI api() {
        return typeApi().getAPI();
    }

    public MapOfMyEntityToIntegerTypeAPI typeApi() {
        return (MapOfMyEntityToIntegerTypeAPI) delegate.getTypeAPI();
    }

}