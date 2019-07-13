package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class MapOfTranslatedText extends HollowMap<MapKey, TranslatedTextValue> {

    public MapOfTranslatedText(HollowMapDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public MapKey instantiateKey(int ordinal) {
        return (MapKey) api().getMapKey(ordinal);
    }

    @Override
    public TranslatedTextValue instantiateValue(int ordinal) {
        return (TranslatedTextValue) api().getTranslatedTextValue(ordinal);
    }

    public TranslatedTextValue get(String k0) {
        return findValue(k0);
    }

    @Override
    public boolean equalsKey(int keyOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getKeyType(), keyOrdinal, testObject);
    }

    @Override
    public boolean equalsValue(int valueOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getValueType(), valueOrdinal, testObject);
    }

    public LocalizedMetaDataAPI api() {
        return typeApi().getAPI();
    }

    public MapOfTranslatedTextTypeAPI typeApi() {
        return (MapOfTranslatedTextTypeAPI) delegate.getTypeAPI();
    }

}