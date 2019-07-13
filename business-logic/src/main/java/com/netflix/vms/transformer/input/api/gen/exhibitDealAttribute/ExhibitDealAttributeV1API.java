package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class ExhibitDealAttributeV1API extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final BooleanTypeAPI booleanTypeAPI;
    private final LongTypeAPI longTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final SetOfStringTypeAPI setOfStringTypeAPI;
    private final DisallowedAssetBundleEntryTypeAPI disallowedAssetBundleEntryTypeAPI;
    private final SetOfDisallowedAssetBundleEntryTypeAPI setOfDisallowedAssetBundleEntryTypeAPI;
    private final VmsAttributeFeedEntryTypeAPI vmsAttributeFeedEntryTypeAPI;

    private final HollowObjectProvider booleanProvider;
    private final HollowObjectProvider longProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider setOfStringProvider;
    private final HollowObjectProvider disallowedAssetBundleEntryProvider;
    private final HollowObjectProvider setOfDisallowedAssetBundleEntryProvider;
    private final HollowObjectProvider vmsAttributeFeedEntryProvider;

    public ExhibitDealAttributeV1API(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public ExhibitDealAttributeV1API(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public ExhibitDealAttributeV1API(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public ExhibitDealAttributeV1API(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, ExhibitDealAttributeV1API previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Boolean","Long","String","SetOfString","DisallowedAssetBundleEntry","SetOfDisallowedAssetBundleEntry","VmsAttributeFeedEntry");

        typeDataAccess = dataAccess.getTypeDataAccess("Boolean");
        if(typeDataAccess != null) {
            booleanTypeAPI = new BooleanTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            booleanTypeAPI = new BooleanTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Boolean"));
        }
        addTypeAPI(booleanTypeAPI);
        factory = factoryOverrides.get("Boolean");
        if(factory == null)
            factory = new BooleanHollowFactory();
        if(cachedTypes.contains("Boolean")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.booleanProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.booleanProvider;
            booleanProvider = new HollowObjectCacheProvider(typeDataAccess, booleanTypeAPI, factory, previousCacheProvider);
        } else {
            booleanProvider = new HollowObjectFactoryProvider(typeDataAccess, booleanTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Long");
        if(typeDataAccess != null) {
            longTypeAPI = new LongTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            longTypeAPI = new LongTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Long"));
        }
        addTypeAPI(longTypeAPI);
        factory = factoryOverrides.get("Long");
        if(factory == null)
            factory = new LongHollowFactory();
        if(cachedTypes.contains("Long")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.longProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.longProvider;
            longProvider = new HollowObjectCacheProvider(typeDataAccess, longTypeAPI, factory, previousCacheProvider);
        } else {
            longProvider = new HollowObjectFactoryProvider(typeDataAccess, longTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfString");
        if(typeDataAccess != null) {
            setOfStringTypeAPI = new SetOfStringTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfStringTypeAPI = new SetOfStringTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfString"));
        }
        addTypeAPI(setOfStringTypeAPI);
        factory = factoryOverrides.get("SetOfString");
        if(factory == null)
            factory = new SetOfStringHollowFactory();
        if(cachedTypes.contains("SetOfString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfStringProvider;
            setOfStringProvider = new HollowObjectCacheProvider(typeDataAccess, setOfStringTypeAPI, factory, previousCacheProvider);
        } else {
            setOfStringProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DisallowedAssetBundleEntry");
        if(typeDataAccess != null) {
            disallowedAssetBundleEntryTypeAPI = new DisallowedAssetBundleEntryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            disallowedAssetBundleEntryTypeAPI = new DisallowedAssetBundleEntryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DisallowedAssetBundleEntry"));
        }
        addTypeAPI(disallowedAssetBundleEntryTypeAPI);
        factory = factoryOverrides.get("DisallowedAssetBundleEntry");
        if(factory == null)
            factory = new DisallowedAssetBundleEntryHollowFactory();
        if(cachedTypes.contains("DisallowedAssetBundleEntry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.disallowedAssetBundleEntryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.disallowedAssetBundleEntryProvider;
            disallowedAssetBundleEntryProvider = new HollowObjectCacheProvider(typeDataAccess, disallowedAssetBundleEntryTypeAPI, factory, previousCacheProvider);
        } else {
            disallowedAssetBundleEntryProvider = new HollowObjectFactoryProvider(typeDataAccess, disallowedAssetBundleEntryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfDisallowedAssetBundleEntry");
        if(typeDataAccess != null) {
            setOfDisallowedAssetBundleEntryTypeAPI = new SetOfDisallowedAssetBundleEntryTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfDisallowedAssetBundleEntryTypeAPI = new SetOfDisallowedAssetBundleEntryTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfDisallowedAssetBundleEntry"));
        }
        addTypeAPI(setOfDisallowedAssetBundleEntryTypeAPI);
        factory = factoryOverrides.get("SetOfDisallowedAssetBundleEntry");
        if(factory == null)
            factory = new SetOfDisallowedAssetBundleEntryHollowFactory();
        if(cachedTypes.contains("SetOfDisallowedAssetBundleEntry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfDisallowedAssetBundleEntryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfDisallowedAssetBundleEntryProvider;
            setOfDisallowedAssetBundleEntryProvider = new HollowObjectCacheProvider(typeDataAccess, setOfDisallowedAssetBundleEntryTypeAPI, factory, previousCacheProvider);
        } else {
            setOfDisallowedAssetBundleEntryProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfDisallowedAssetBundleEntryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VmsAttributeFeedEntry");
        if(typeDataAccess != null) {
            vmsAttributeFeedEntryTypeAPI = new VmsAttributeFeedEntryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            vmsAttributeFeedEntryTypeAPI = new VmsAttributeFeedEntryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VmsAttributeFeedEntry"));
        }
        addTypeAPI(vmsAttributeFeedEntryTypeAPI);
        factory = factoryOverrides.get("VmsAttributeFeedEntry");
        if(factory == null)
            factory = new VmsAttributeFeedEntryHollowFactory();
        if(cachedTypes.contains("VmsAttributeFeedEntry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.vmsAttributeFeedEntryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.vmsAttributeFeedEntryProvider;
            vmsAttributeFeedEntryProvider = new HollowObjectCacheProvider(typeDataAccess, vmsAttributeFeedEntryTypeAPI, factory, previousCacheProvider);
        } else {
            vmsAttributeFeedEntryProvider = new HollowObjectFactoryProvider(typeDataAccess, vmsAttributeFeedEntryTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(booleanProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)booleanProvider).detach();
        if(longProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)longProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(setOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfStringProvider).detach();
        if(disallowedAssetBundleEntryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)disallowedAssetBundleEntryProvider).detach();
        if(setOfDisallowedAssetBundleEntryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfDisallowedAssetBundleEntryProvider).detach();
        if(vmsAttributeFeedEntryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)vmsAttributeFeedEntryProvider).detach();
    }

    public BooleanTypeAPI getBooleanTypeAPI() {
        return booleanTypeAPI;
    }
    public LongTypeAPI getLongTypeAPI() {
        return longTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public SetOfStringTypeAPI getSetOfStringTypeAPI() {
        return setOfStringTypeAPI;
    }
    public DisallowedAssetBundleEntryTypeAPI getDisallowedAssetBundleEntryTypeAPI() {
        return disallowedAssetBundleEntryTypeAPI;
    }
    public SetOfDisallowedAssetBundleEntryTypeAPI getSetOfDisallowedAssetBundleEntryTypeAPI() {
        return setOfDisallowedAssetBundleEntryTypeAPI;
    }
    public VmsAttributeFeedEntryTypeAPI getVmsAttributeFeedEntryTypeAPI() {
        return vmsAttributeFeedEntryTypeAPI;
    }
    public Collection<HBoolean> getAllHBoolean() {
        return new AllHollowRecordCollection<HBoolean>(getDataAccess().getTypeDataAccess("Boolean").getTypeState()) {
            protected HBoolean getForOrdinal(int ordinal) {
                return getHBoolean(ordinal);
            }
        };
    }
    public HBoolean getHBoolean(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (HBoolean)booleanProvider.getHollowObject(ordinal);
    }
    public Collection<HLong> getAllHLong() {
        return new AllHollowRecordCollection<HLong>(getDataAccess().getTypeDataAccess("Long").getTypeState()) {
            protected HLong getForOrdinal(int ordinal) {
                return getHLong(ordinal);
            }
        };
    }
    public HLong getHLong(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (HLong)longProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfString> getAllSetOfString() {
        return new AllHollowRecordCollection<SetOfString>(getDataAccess().getTypeDataAccess("SetOfString").getTypeState()) {
            protected SetOfString getForOrdinal(int ordinal) {
                return getSetOfString(ordinal);
            }
        };
    }
    public SetOfString getSetOfString(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (SetOfString)setOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<DisallowedAssetBundleEntry> getAllDisallowedAssetBundleEntry() {
        return new AllHollowRecordCollection<DisallowedAssetBundleEntry>(getDataAccess().getTypeDataAccess("DisallowedAssetBundleEntry").getTypeState()) {
            protected DisallowedAssetBundleEntry getForOrdinal(int ordinal) {
                return getDisallowedAssetBundleEntry(ordinal);
            }
        };
    }
    public DisallowedAssetBundleEntry getDisallowedAssetBundleEntry(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (DisallowedAssetBundleEntry)disallowedAssetBundleEntryProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfDisallowedAssetBundleEntry> getAllSetOfDisallowedAssetBundleEntry() {
        return new AllHollowRecordCollection<SetOfDisallowedAssetBundleEntry>(getDataAccess().getTypeDataAccess("SetOfDisallowedAssetBundleEntry").getTypeState()) {
            protected SetOfDisallowedAssetBundleEntry getForOrdinal(int ordinal) {
                return getSetOfDisallowedAssetBundleEntry(ordinal);
            }
        };
    }
    public SetOfDisallowedAssetBundleEntry getSetOfDisallowedAssetBundleEntry(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (SetOfDisallowedAssetBundleEntry)setOfDisallowedAssetBundleEntryProvider.getHollowObject(ordinal);
    }
    public Collection<VmsAttributeFeedEntry> getAllVmsAttributeFeedEntry() {
        return new AllHollowRecordCollection<VmsAttributeFeedEntry>(getDataAccess().getTypeDataAccess("VmsAttributeFeedEntry").getTypeState()) {
            protected VmsAttributeFeedEntry getForOrdinal(int ordinal) {
                return getVmsAttributeFeedEntry(ordinal);
            }
        };
    }
    public VmsAttributeFeedEntry getVmsAttributeFeedEntry(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (VmsAttributeFeedEntry)vmsAttributeFeedEntryProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
