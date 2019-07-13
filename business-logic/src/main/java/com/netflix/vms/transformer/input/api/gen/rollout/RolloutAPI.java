package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class RolloutAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final ISOCountryTypeAPI iSOCountryTypeAPI;
    private final RolloutPhaseWindowTypeAPI rolloutPhaseWindowTypeAPI;
    private final RolloutPhaseWindowMapTypeAPI rolloutPhaseWindowMapTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final RolloutPhaseArtworkSourceFileIdTypeAPI rolloutPhaseArtworkSourceFileIdTypeAPI;
    private final RolloutPhaseArtworkSourceFileIdListTypeAPI rolloutPhaseArtworkSourceFileIdListTypeAPI;
    private final RolloutPhaseArtworkTypeAPI rolloutPhaseArtworkTypeAPI;
    private final RolloutPhaseLocalizedMetadataTypeAPI rolloutPhaseLocalizedMetadataTypeAPI;
    private final RolloutPhaseElementsTypeAPI rolloutPhaseElementsTypeAPI;
    private final RolloutPhaseTypeAPI rolloutPhaseTypeAPI;
    private final RolloutPhaseListTypeAPI rolloutPhaseListTypeAPI;
    private final RolloutTypeAPI rolloutTypeAPI;

    private final HollowObjectProvider iSOCountryProvider;
    private final HollowObjectProvider rolloutPhaseWindowProvider;
    private final HollowObjectProvider rolloutPhaseWindowMapProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider rolloutPhaseArtworkSourceFileIdProvider;
    private final HollowObjectProvider rolloutPhaseArtworkSourceFileIdListProvider;
    private final HollowObjectProvider rolloutPhaseArtworkProvider;
    private final HollowObjectProvider rolloutPhaseLocalizedMetadataProvider;
    private final HollowObjectProvider rolloutPhaseElementsProvider;
    private final HollowObjectProvider rolloutPhaseProvider;
    private final HollowObjectProvider rolloutPhaseListProvider;
    private final HollowObjectProvider rolloutProvider;

    public RolloutAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public RolloutAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public RolloutAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public RolloutAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, RolloutAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("ISOCountry","RolloutPhaseWindow","RolloutPhaseWindowMap","String","RolloutPhaseArtworkSourceFileId","RolloutPhaseArtworkSourceFileIdList","RolloutPhaseArtwork","RolloutPhaseLocalizedMetadata","RolloutPhaseElements","RolloutPhase","RolloutPhaseList","Rollout");

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountry");
        if(typeDataAccess != null) {
            iSOCountryTypeAPI = new ISOCountryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iSOCountryTypeAPI = new ISOCountryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ISOCountry"));
        }
        addTypeAPI(iSOCountryTypeAPI);
        factory = factoryOverrides.get("ISOCountry");
        if(factory == null)
            factory = new ISOCountryHollowFactory();
        if(cachedTypes.contains("ISOCountry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountryProvider;
            iSOCountryProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountryTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountryProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseWindow");
        if(typeDataAccess != null) {
            rolloutPhaseWindowTypeAPI = new RolloutPhaseWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseWindowTypeAPI = new RolloutPhaseWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseWindow"));
        }
        addTypeAPI(rolloutPhaseWindowTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseWindow");
        if(factory == null)
            factory = new RolloutPhaseWindowHollowFactory();
        if(cachedTypes.contains("RolloutPhaseWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseWindowProvider;
            rolloutPhaseWindowProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseWindowTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseWindowMap");
        if(typeDataAccess != null) {
            rolloutPhaseWindowMapTypeAPI = new RolloutPhaseWindowMapTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseWindowMapTypeAPI = new RolloutPhaseWindowMapTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "RolloutPhaseWindowMap"));
        }
        addTypeAPI(rolloutPhaseWindowMapTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseWindowMap");
        if(factory == null)
            factory = new RolloutPhaseWindowMapHollowFactory();
        if(cachedTypes.contains("RolloutPhaseWindowMap")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseWindowMapProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseWindowMapProvider;
            rolloutPhaseWindowMapProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseWindowMapTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseWindowMapProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseWindowMapTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseArtworkSourceFileId");
        if(typeDataAccess != null) {
            rolloutPhaseArtworkSourceFileIdTypeAPI = new RolloutPhaseArtworkSourceFileIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseArtworkSourceFileIdTypeAPI = new RolloutPhaseArtworkSourceFileIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseArtworkSourceFileId"));
        }
        addTypeAPI(rolloutPhaseArtworkSourceFileIdTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseArtworkSourceFileId");
        if(factory == null)
            factory = new RolloutPhaseArtworkSourceFileIdHollowFactory();
        if(cachedTypes.contains("RolloutPhaseArtworkSourceFileId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseArtworkSourceFileIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseArtworkSourceFileIdProvider;
            rolloutPhaseArtworkSourceFileIdProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseArtworkSourceFileIdProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseArtworkSourceFileIdList");
        if(typeDataAccess != null) {
            rolloutPhaseArtworkSourceFileIdListTypeAPI = new RolloutPhaseArtworkSourceFileIdListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseArtworkSourceFileIdListTypeAPI = new RolloutPhaseArtworkSourceFileIdListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseArtworkSourceFileIdList"));
        }
        addTypeAPI(rolloutPhaseArtworkSourceFileIdListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseArtworkSourceFileIdList");
        if(factory == null)
            factory = new RolloutPhaseArtworkSourceFileIdListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseArtworkSourceFileIdList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseArtworkSourceFileIdListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseArtworkSourceFileIdListProvider;
            rolloutPhaseArtworkSourceFileIdListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseArtworkSourceFileIdListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseArtworkSourceFileIdListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseArtwork");
        if(typeDataAccess != null) {
            rolloutPhaseArtworkTypeAPI = new RolloutPhaseArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseArtworkTypeAPI = new RolloutPhaseArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseArtwork"));
        }
        addTypeAPI(rolloutPhaseArtworkTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseArtwork");
        if(factory == null)
            factory = new RolloutPhaseArtworkHollowFactory();
        if(cachedTypes.contains("RolloutPhaseArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseArtworkProvider;
            rolloutPhaseArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseLocalizedMetadata");
        if(typeDataAccess != null) {
            rolloutPhaseLocalizedMetadataTypeAPI = new RolloutPhaseLocalizedMetadataTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseLocalizedMetadataTypeAPI = new RolloutPhaseLocalizedMetadataTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseLocalizedMetadata"));
        }
        addTypeAPI(rolloutPhaseLocalizedMetadataTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseLocalizedMetadata");
        if(factory == null)
            factory = new RolloutPhaseLocalizedMetadataHollowFactory();
        if(cachedTypes.contains("RolloutPhaseLocalizedMetadata")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseLocalizedMetadataProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseLocalizedMetadataProvider;
            rolloutPhaseLocalizedMetadataProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseLocalizedMetadataTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseLocalizedMetadataProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseLocalizedMetadataTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseElements");
        if(typeDataAccess != null) {
            rolloutPhaseElementsTypeAPI = new RolloutPhaseElementsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseElementsTypeAPI = new RolloutPhaseElementsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhaseElements"));
        }
        addTypeAPI(rolloutPhaseElementsTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseElements");
        if(factory == null)
            factory = new RolloutPhaseElementsHollowFactory();
        if(cachedTypes.contains("RolloutPhaseElements")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseElementsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseElementsProvider;
            rolloutPhaseElementsProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseElementsTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseElementsProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseElementsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhase");
        if(typeDataAccess != null) {
            rolloutPhaseTypeAPI = new RolloutPhaseTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseTypeAPI = new RolloutPhaseTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutPhase"));
        }
        addTypeAPI(rolloutPhaseTypeAPI);
        factory = factoryOverrides.get("RolloutPhase");
        if(factory == null)
            factory = new RolloutPhaseHollowFactory();
        if(cachedTypes.contains("RolloutPhase")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseProvider;
            rolloutPhaseProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutPhaseList");
        if(typeDataAccess != null) {
            rolloutPhaseListTypeAPI = new RolloutPhaseListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            rolloutPhaseListTypeAPI = new RolloutPhaseListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "RolloutPhaseList"));
        }
        addTypeAPI(rolloutPhaseListTypeAPI);
        factory = factoryOverrides.get("RolloutPhaseList");
        if(factory == null)
            factory = new RolloutPhaseListHollowFactory();
        if(cachedTypes.contains("RolloutPhaseList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutPhaseListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutPhaseListProvider;
            rolloutPhaseListProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutPhaseListTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutPhaseListProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutPhaseListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Rollout");
        if(typeDataAccess != null) {
            rolloutTypeAPI = new RolloutTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutTypeAPI = new RolloutTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Rollout"));
        }
        addTypeAPI(rolloutTypeAPI);
        factory = factoryOverrides.get("Rollout");
        if(factory == null)
            factory = new RolloutHollowFactory();
        if(cachedTypes.contains("Rollout")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutProvider;
            rolloutProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(iSOCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryProvider).detach();
        if(rolloutPhaseWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseWindowProvider).detach();
        if(rolloutPhaseWindowMapProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseWindowMapProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(rolloutPhaseArtworkSourceFileIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkSourceFileIdProvider).detach();
        if(rolloutPhaseArtworkSourceFileIdListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkSourceFileIdListProvider).detach();
        if(rolloutPhaseArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseArtworkProvider).detach();
        if(rolloutPhaseLocalizedMetadataProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseLocalizedMetadataProvider).detach();
        if(rolloutPhaseElementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseElementsProvider).detach();
        if(rolloutPhaseProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseProvider).detach();
        if(rolloutPhaseListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseListProvider).detach();
        if(rolloutProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutProvider).detach();
    }

    public ISOCountryTypeAPI getISOCountryTypeAPI() {
        return iSOCountryTypeAPI;
    }
    public RolloutPhaseWindowTypeAPI getRolloutPhaseWindowTypeAPI() {
        return rolloutPhaseWindowTypeAPI;
    }
    public RolloutPhaseWindowMapTypeAPI getRolloutPhaseWindowMapTypeAPI() {
        return rolloutPhaseWindowMapTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public RolloutPhaseArtworkSourceFileIdTypeAPI getRolloutPhaseArtworkSourceFileIdTypeAPI() {
        return rolloutPhaseArtworkSourceFileIdTypeAPI;
    }
    public RolloutPhaseArtworkSourceFileIdListTypeAPI getRolloutPhaseArtworkSourceFileIdListTypeAPI() {
        return rolloutPhaseArtworkSourceFileIdListTypeAPI;
    }
    public RolloutPhaseArtworkTypeAPI getRolloutPhaseArtworkTypeAPI() {
        return rolloutPhaseArtworkTypeAPI;
    }
    public RolloutPhaseLocalizedMetadataTypeAPI getRolloutPhaseLocalizedMetadataTypeAPI() {
        return rolloutPhaseLocalizedMetadataTypeAPI;
    }
    public RolloutPhaseElementsTypeAPI getRolloutPhaseElementsTypeAPI() {
        return rolloutPhaseElementsTypeAPI;
    }
    public RolloutPhaseTypeAPI getRolloutPhaseTypeAPI() {
        return rolloutPhaseTypeAPI;
    }
    public RolloutPhaseListTypeAPI getRolloutPhaseListTypeAPI() {
        return rolloutPhaseListTypeAPI;
    }
    public RolloutTypeAPI getRolloutTypeAPI() {
        return rolloutTypeAPI;
    }
    public Collection<ISOCountry> getAllISOCountry() {
        return new AllHollowRecordCollection<ISOCountry>(getDataAccess().getTypeDataAccess("ISOCountry").getTypeState()) {
            protected ISOCountry getForOrdinal(int ordinal) {
                return getISOCountry(ordinal);
            }
        };
    }
    public ISOCountry getISOCountry(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (ISOCountry)iSOCountryProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseWindow> getAllRolloutPhaseWindow() {
        return new AllHollowRecordCollection<RolloutPhaseWindow>(getDataAccess().getTypeDataAccess("RolloutPhaseWindow").getTypeState()) {
            protected RolloutPhaseWindow getForOrdinal(int ordinal) {
                return getRolloutPhaseWindow(ordinal);
            }
        };
    }
    public RolloutPhaseWindow getRolloutPhaseWindow(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (RolloutPhaseWindow)rolloutPhaseWindowProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseWindowMap> getAllRolloutPhaseWindowMap() {
        return new AllHollowRecordCollection<RolloutPhaseWindowMap>(getDataAccess().getTypeDataAccess("RolloutPhaseWindowMap").getTypeState()) {
            protected RolloutPhaseWindowMap getForOrdinal(int ordinal) {
                return getRolloutPhaseWindowMap(ordinal);
            }
        };
    }
    public RolloutPhaseWindowMap getRolloutPhaseWindowMap(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (RolloutPhaseWindowMap)rolloutPhaseWindowMapProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtworkSourceFileId> getAllRolloutPhaseArtworkSourceFileId() {
        return new AllHollowRecordCollection<RolloutPhaseArtworkSourceFileId>(getDataAccess().getTypeDataAccess("RolloutPhaseArtworkSourceFileId").getTypeState()) {
            protected RolloutPhaseArtworkSourceFileId getForOrdinal(int ordinal) {
                return getRolloutPhaseArtworkSourceFileId(ordinal);
            }
        };
    }
    public RolloutPhaseArtworkSourceFileId getRolloutPhaseArtworkSourceFileId(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (RolloutPhaseArtworkSourceFileId)rolloutPhaseArtworkSourceFileIdProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtworkSourceFileIdList> getAllRolloutPhaseArtworkSourceFileIdList() {
        return new AllHollowRecordCollection<RolloutPhaseArtworkSourceFileIdList>(getDataAccess().getTypeDataAccess("RolloutPhaseArtworkSourceFileIdList").getTypeState()) {
            protected RolloutPhaseArtworkSourceFileIdList getForOrdinal(int ordinal) {
                return getRolloutPhaseArtworkSourceFileIdList(ordinal);
            }
        };
    }
    public RolloutPhaseArtworkSourceFileIdList getRolloutPhaseArtworkSourceFileIdList(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (RolloutPhaseArtworkSourceFileIdList)rolloutPhaseArtworkSourceFileIdListProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseArtwork> getAllRolloutPhaseArtwork() {
        return new AllHollowRecordCollection<RolloutPhaseArtwork>(getDataAccess().getTypeDataAccess("RolloutPhaseArtwork").getTypeState()) {
            protected RolloutPhaseArtwork getForOrdinal(int ordinal) {
                return getRolloutPhaseArtwork(ordinal);
            }
        };
    }
    public RolloutPhaseArtwork getRolloutPhaseArtwork(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (RolloutPhaseArtwork)rolloutPhaseArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseLocalizedMetadata> getAllRolloutPhaseLocalizedMetadata() {
        return new AllHollowRecordCollection<RolloutPhaseLocalizedMetadata>(getDataAccess().getTypeDataAccess("RolloutPhaseLocalizedMetadata").getTypeState()) {
            protected RolloutPhaseLocalizedMetadata getForOrdinal(int ordinal) {
                return getRolloutPhaseLocalizedMetadata(ordinal);
            }
        };
    }
    public RolloutPhaseLocalizedMetadata getRolloutPhaseLocalizedMetadata(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (RolloutPhaseLocalizedMetadata)rolloutPhaseLocalizedMetadataProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseElements> getAllRolloutPhaseElements() {
        return new AllHollowRecordCollection<RolloutPhaseElements>(getDataAccess().getTypeDataAccess("RolloutPhaseElements").getTypeState()) {
            protected RolloutPhaseElements getForOrdinal(int ordinal) {
                return getRolloutPhaseElements(ordinal);
            }
        };
    }
    public RolloutPhaseElements getRolloutPhaseElements(int ordinal) {
        objectCreationSampler.recordCreation(8);
        return (RolloutPhaseElements)rolloutPhaseElementsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhase> getAllRolloutPhase() {
        return new AllHollowRecordCollection<RolloutPhase>(getDataAccess().getTypeDataAccess("RolloutPhase").getTypeState()) {
            protected RolloutPhase getForOrdinal(int ordinal) {
                return getRolloutPhase(ordinal);
            }
        };
    }
    public RolloutPhase getRolloutPhase(int ordinal) {
        objectCreationSampler.recordCreation(9);
        return (RolloutPhase)rolloutPhaseProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhaseList> getAllRolloutPhaseList() {
        return new AllHollowRecordCollection<RolloutPhaseList>(getDataAccess().getTypeDataAccess("RolloutPhaseList").getTypeState()) {
            protected RolloutPhaseList getForOrdinal(int ordinal) {
                return getRolloutPhaseList(ordinal);
            }
        };
    }
    public RolloutPhaseList getRolloutPhaseList(int ordinal) {
        objectCreationSampler.recordCreation(10);
        return (RolloutPhaseList)rolloutPhaseListProvider.getHollowObject(ordinal);
    }
    public Collection<Rollout> getAllRollout() {
        return new AllHollowRecordCollection<Rollout>(getDataAccess().getTypeDataAccess("Rollout").getTypeState()) {
            protected Rollout getForOrdinal(int ordinal) {
                return getRollout(ordinal);
            }
        };
    }
    public Rollout getRollout(int ordinal) {
        objectCreationSampler.recordCreation(11);
        return (Rollout)rolloutProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
