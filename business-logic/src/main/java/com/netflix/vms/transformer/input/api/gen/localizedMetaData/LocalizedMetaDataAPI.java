package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class LocalizedMetaDataAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final MapKeyTypeAPI mapKeyTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final TranslatedTextValueTypeAPI translatedTextValueTypeAPI;
    private final MapOfTranslatedTextTypeAPI mapOfTranslatedTextTypeAPI;
    private final LocalizedMetadataTypeAPI localizedMetadataTypeAPI;

    private final HollowObjectProvider mapKeyProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider translatedTextValueProvider;
    private final HollowObjectProvider mapOfTranslatedTextProvider;
    private final HollowObjectProvider localizedMetadataProvider;

    public LocalizedMetaDataAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public LocalizedMetaDataAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public LocalizedMetaDataAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public LocalizedMetaDataAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, LocalizedMetaDataAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("MapKey","String","TranslatedTextValue","MapOfTranslatedText","LocalizedMetadata");

        typeDataAccess = dataAccess.getTypeDataAccess("MapKey");
        if(typeDataAccess != null) {
            mapKeyTypeAPI = new MapKeyTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            mapKeyTypeAPI = new MapKeyTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MapKey"));
        }
        addTypeAPI(mapKeyTypeAPI);
        factory = factoryOverrides.get("MapKey");
        if(factory == null)
            factory = new MapKeyHollowFactory();
        if(cachedTypes.contains("MapKey")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapKeyProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapKeyProvider;
            mapKeyProvider = new HollowObjectCacheProvider(typeDataAccess, mapKeyTypeAPI, factory, previousCacheProvider);
        } else {
            mapKeyProvider = new HollowObjectFactoryProvider(typeDataAccess, mapKeyTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("String");
        if(typeDataAccess != null) {
            stringTypeAPI = new StringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            stringTypeAPI = new StringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "String"));
        }
        addTypeAPI(stringTypeAPI);
        factory = factoryOverrides.get("String");
        if(factory == null)
            factory = new StringHollowFactory();
        if(cachedTypes.contains("String")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.stringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.stringProvider;
            stringProvider = new HollowObjectCacheProvider(typeDataAccess, stringTypeAPI, factory, previousCacheProvider);
        } else {
            stringProvider = new HollowObjectFactoryProvider(typeDataAccess, stringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TranslatedTextValue");
        if(typeDataAccess != null) {
            translatedTextValueTypeAPI = new TranslatedTextValueTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            translatedTextValueTypeAPI = new TranslatedTextValueTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TranslatedTextValue"));
        }
        addTypeAPI(translatedTextValueTypeAPI);
        factory = factoryOverrides.get("TranslatedTextValue");
        if(factory == null)
            factory = new TranslatedTextValueHollowFactory();
        if(cachedTypes.contains("TranslatedTextValue")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.translatedTextValueProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.translatedTextValueProvider;
            translatedTextValueProvider = new HollowObjectCacheProvider(typeDataAccess, translatedTextValueTypeAPI, factory, previousCacheProvider);
        } else {
            translatedTextValueProvider = new HollowObjectFactoryProvider(typeDataAccess, translatedTextValueTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfTranslatedText");
        if(typeDataAccess != null) {
            mapOfTranslatedTextTypeAPI = new MapOfTranslatedTextTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfTranslatedTextTypeAPI = new MapOfTranslatedTextTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfTranslatedText"));
        }
        addTypeAPI(mapOfTranslatedTextTypeAPI);
        factory = factoryOverrides.get("MapOfTranslatedText");
        if(factory == null)
            factory = new MapOfTranslatedTextHollowFactory();
        if(cachedTypes.contains("MapOfTranslatedText")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfTranslatedTextProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfTranslatedTextProvider;
            mapOfTranslatedTextProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfTranslatedTextTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfTranslatedTextProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfTranslatedTextTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("LocalizedMetadata");
        if(typeDataAccess != null) {
            localizedMetadataTypeAPI = new LocalizedMetadataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            localizedMetadataTypeAPI = new LocalizedMetadataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "LocalizedMetadata"));
        }
        addTypeAPI(localizedMetadataTypeAPI);
        factory = factoryOverrides.get("LocalizedMetadata");
        if(factory == null)
            factory = new LocalizedMetadataHollowFactory();
        if(cachedTypes.contains("LocalizedMetadata")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.localizedMetadataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.localizedMetadataProvider;
            localizedMetadataProvider = new HollowObjectCacheProvider(typeDataAccess, localizedMetadataTypeAPI, factory, previousCacheProvider);
        } else {
            localizedMetadataProvider = new HollowObjectFactoryProvider(typeDataAccess, localizedMetadataTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(mapKeyProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapKeyProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(translatedTextValueProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)translatedTextValueProvider).detach();
        if(mapOfTranslatedTextProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfTranslatedTextProvider).detach();
        if(localizedMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)localizedMetadataProvider).detach();
    }

    public MapKeyTypeAPI getMapKeyTypeAPI() {
        return mapKeyTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public TranslatedTextValueTypeAPI getTranslatedTextValueTypeAPI() {
        return translatedTextValueTypeAPI;
    }
    public MapOfTranslatedTextTypeAPI getMapOfTranslatedTextTypeAPI() {
        return mapOfTranslatedTextTypeAPI;
    }
    public LocalizedMetadataTypeAPI getLocalizedMetadataTypeAPI() {
        return localizedMetadataTypeAPI;
    }
    public Collection<MapKey> getAllMapKey() {
        return new AllHollowRecordCollection<MapKey>(getDataAccess().getTypeDataAccess("MapKey").getTypeState()) {
            protected MapKey getForOrdinal(int ordinal) {
                return getMapKey(ordinal);
            }
        };
    }
    public MapKey getMapKey(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (MapKey)mapKeyProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<TranslatedTextValue> getAllTranslatedTextValue() {
        return new AllHollowRecordCollection<TranslatedTextValue>(getDataAccess().getTypeDataAccess("TranslatedTextValue").getTypeState()) {
            protected TranslatedTextValue getForOrdinal(int ordinal) {
                return getTranslatedTextValue(ordinal);
            }
        };
    }
    public TranslatedTextValue getTranslatedTextValue(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (TranslatedTextValue)translatedTextValueProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfTranslatedText> getAllMapOfTranslatedText() {
        return new AllHollowRecordCollection<MapOfTranslatedText>(getDataAccess().getTypeDataAccess("MapOfTranslatedText").getTypeState()) {
            protected MapOfTranslatedText getForOrdinal(int ordinal) {
                return getMapOfTranslatedText(ordinal);
            }
        };
    }
    public MapOfTranslatedText getMapOfTranslatedText(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (MapOfTranslatedText)mapOfTranslatedTextProvider.getHollowObject(ordinal);
    }
    public Collection<LocalizedMetadata> getAllLocalizedMetadata() {
        return new AllHollowRecordCollection<LocalizedMetadata>(getDataAccess().getTypeDataAccess("LocalizedMetadata").getTypeState()) {
            protected LocalizedMetadata getForOrdinal(int ordinal) {
                return getLocalizedMetadata(ordinal);
            }
        };
    }
    public LocalizedMetadata getLocalizedMetadata(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (LocalizedMetadata)localizedMetadataProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
