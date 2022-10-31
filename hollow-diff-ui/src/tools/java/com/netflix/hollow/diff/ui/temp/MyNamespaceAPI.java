package com.netflix.hollow.diff.ui.temp;

import com.netflix.hollow.Hollow;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowTypeProxyDataAccess;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.tools.history.HollowHistoricalState;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import com.netflix.hollow.tools.history.HollowHistoricalTypeDataAccess;
import java.util.Objects;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.util.AllHollowRecordCollection;

@SuppressWarnings("all")
public class MyNamespaceAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final IntegerTypeAPI integerTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final ProfileIdTypeAPI profileIdTypeAPI;
    private final MyEntityTypeAPI myEntityTypeAPI;
    private final MapOfMyEntityToIntegerTypeAPI mapOfMyEntityToIntegerTypeAPI;
    private final MyEntityRankIndexTypeAPI myEntityRankIndexTypeAPI;

    private final HollowObjectProvider integerProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider profileIdProvider;
    private final HollowObjectProvider myEntityProvider;
    private final HollowObjectProvider mapOfMyEntityToIntegerProvider;
    private final HollowObjectProvider myEntityRankIndexProvider;

    public MyNamespaceAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public MyNamespaceAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public MyNamespaceAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public MyNamespaceAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, MyNamespaceAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Integer","String","profileId","MyEntity","MapOfMyEntityToInteger","MyEntityRankIndex");

        typeDataAccess = dataAccess.getTypeDataAccess("Integer");
        if(typeDataAccess != null) {
            integerTypeAPI = new IntegerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            integerTypeAPI = new IntegerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Integer"));
        }
        addTypeAPI(integerTypeAPI);
        factory = factoryOverrides.get("Integer");
        if(factory == null)
            factory = new IntegerHollowFactory();
        if(cachedTypes.contains("Integer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.integerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.integerProvider;
            integerProvider = new HollowObjectCacheProvider(typeDataAccess, integerTypeAPI, factory, previousCacheProvider);
        } else {
            integerProvider = new HollowObjectFactoryProvider(typeDataAccess, integerTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("profileId");
        if(typeDataAccess != null) {
            profileIdTypeAPI = new ProfileIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            profileIdTypeAPI = new ProfileIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "profileId"));
        }
        addTypeAPI(profileIdTypeAPI);
        factory = factoryOverrides.get("profileId");
        if(factory == null)
            factory = new ProfileIdHollowFactory();
        if(cachedTypes.contains("profileId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.profileIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.profileIdProvider;
            profileIdProvider = new HollowObjectCacheProvider(typeDataAccess, profileIdTypeAPI, factory, previousCacheProvider);
        } else {
            profileIdProvider = new HollowObjectFactoryProvider(typeDataAccess, profileIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MyEntity");
        if(typeDataAccess != null) {
            myEntityTypeAPI = new MyEntityTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            myEntityTypeAPI = new MyEntityTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MyEntity"));
        }
        addTypeAPI(myEntityTypeAPI);
        factory = factoryOverrides.get("MyEntity");
        if(factory == null)
            factory = new MyEntityHollowFactory();
        if(cachedTypes.contains("MyEntity")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.myEntityProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.myEntityProvider;
            myEntityProvider = new HollowObjectCacheProvider(typeDataAccess, myEntityTypeAPI, factory, previousCacheProvider);
        } else {
            myEntityProvider = new HollowObjectFactoryProvider(typeDataAccess, myEntityTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfMyEntityToInteger");
        if(typeDataAccess != null) {
            mapOfMyEntityToIntegerTypeAPI = new MapOfMyEntityToIntegerTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfMyEntityToIntegerTypeAPI = new MapOfMyEntityToIntegerTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfMyEntityToInteger"));
        }
        addTypeAPI(mapOfMyEntityToIntegerTypeAPI);
        factory = factoryOverrides.get("MapOfMyEntityToInteger");
        if(factory == null)
            factory = new MapOfMyEntityToIntegerHollowFactory();
        if(cachedTypes.contains("MapOfMyEntityToInteger")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfMyEntityToIntegerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfMyEntityToIntegerProvider;
            mapOfMyEntityToIntegerProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfMyEntityToIntegerTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfMyEntityToIntegerProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfMyEntityToIntegerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MyEntityRankIndex");
        if(typeDataAccess != null) {
            myEntityRankIndexTypeAPI = new MyEntityRankIndexTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            myEntityRankIndexTypeAPI = new MyEntityRankIndexTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MyEntityRankIndex"));
        }
        addTypeAPI(myEntityRankIndexTypeAPI);
        factory = factoryOverrides.get("MyEntityRankIndex");
        if(factory == null)
            factory = new MyEntityRankIndexHollowFactory();
        if(cachedTypes.contains("MyEntityRankIndex")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.myEntityRankIndexProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.myEntityRankIndexProvider;
            myEntityRankIndexProvider = new HollowObjectCacheProvider(typeDataAccess, myEntityRankIndexTypeAPI, factory, previousCacheProvider);
        } else {
            myEntityRankIndexProvider = new HollowObjectFactoryProvider(typeDataAccess, myEntityRankIndexTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(integerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)integerProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(profileIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)profileIdProvider).detach();
        if(myEntityProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)myEntityProvider).detach();
        if(mapOfMyEntityToIntegerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfMyEntityToIntegerProvider).detach();
        if(myEntityRankIndexProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)myEntityRankIndexProvider).detach();
    }

    public IntegerTypeAPI getIntegerTypeAPI() {
        return integerTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public ProfileIdTypeAPI getProfileIdTypeAPI() {
        return profileIdTypeAPI;
    }
    public MyEntityTypeAPI getMyEntityTypeAPI() {
        return myEntityTypeAPI;
    }
    public MapOfMyEntityToIntegerTypeAPI getMapOfMyEntityToIntegerTypeAPI() {
        return mapOfMyEntityToIntegerTypeAPI;
    }
    public MyEntityRankIndexTypeAPI getMyEntityRankIndexTypeAPI() {
        return myEntityRankIndexTypeAPI;
    }
    public Collection<HInteger> getAllHInteger() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("Integer"), "type not loaded or does not exist in dataset; type=Integer");
        return new AllHollowRecordCollection<HInteger>(tda.getTypeState()) {
            protected HInteger getForOrdinal(int ordinal) {
                return getHInteger(ordinal);
            }
        };
    }
    public HInteger getHInteger(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (HInteger)integerProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("String"), "type not loaded or does not exist in dataset; type=String");
        return new AllHollowRecordCollection<HString>(tda.getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<ProfileId> getAllProfileId() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("profileId"), "type not loaded or does not exist in dataset; type=profileId");
        return new AllHollowRecordCollection<ProfileId>(tda.getTypeState()) {
            protected ProfileId getForOrdinal(int ordinal) {
                return getProfileId(ordinal);
            }
        };
    }
    public ProfileId getProfileId(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (ProfileId)profileIdProvider.getHollowObject(ordinal);
    }
    public Collection<MyEntity> getAllMyEntity() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("MyEntity"), "type not loaded or does not exist in dataset; type=MyEntity");
        if (tda instanceof HollowTypeProxyDataAccess) {
            HollowTypeProxyDataAccess proxyDataAccess = (HollowTypeProxyDataAccess) tda;
            if (proxyDataAccess.getCurrentDataAccess() instanceof HollowHistoricalTypeDataAccess) {  // TODO: needs change in cinder-core, in addition to operating on a copy of populatedOrdinals
                return new AllHollowRecordCollection<MyEntity>(tda.getPopulatedOrdinals()) {    // TODO: needs change in cinder-core
                    protected MyEntity getForOrdinal(int ordinal) {
                        HollowHistoricalStateDataAccess historicalStateDataAccess = (HollowHistoricalStateDataAccess) tda.getDataAccess();
                        HollowHistoricalTypeDataAccess historicalTypeDataAccess = (HollowHistoricalTypeDataAccess) historicalStateDataAccess.getTypeDataAccess("MyEntity", ordinal);
                        return getMyEntity(historicalTypeDataAccess, ordinal);    // TODO: needs change in generated API
                    }
                };
            }
        }
        return new AllHollowRecordCollection<MyEntity>(tda.getTypeState()) {
            protected MyEntity getForOrdinal(int ordinal) {
                return getMyEntity(ordinal);
            }
        };
    }
    public MyEntity getMyEntity(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (MyEntity)myEntityProvider.getHollowObject(ordinal);
    }
    public MyEntity getMyEntity(HollowHistoricalTypeDataAccess historicalTypeDataAccess, int ordinal) {
        objectCreationSampler.recordCreation(3);
        HollowObjectFactoryProvider<MyEntity> historicalTypeProvider = new HollowObjectFactoryProvider<>(historicalTypeDataAccess, myEntityTypeAPI, factory);
        return (MyEntity)historicalTypeProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfMyEntityToInteger> getAllMapOfMyEntityToInteger() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("MapOfMyEntityToInteger"), "type not loaded or does not exist in dataset; type=MapOfMyEntityToInteger");
        return new AllHollowRecordCollection<MapOfMyEntityToInteger>(tda.getTypeState()) {
            protected MapOfMyEntityToInteger getForOrdinal(int ordinal) {
                return getMapOfMyEntityToInteger(ordinal);
            }
        };
    }
    public MapOfMyEntityToInteger getMapOfMyEntityToInteger(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (MapOfMyEntityToInteger)mapOfMyEntityToIntegerProvider.getHollowObject(ordinal);
    }
    public Collection<MyEntityRankIndex> getAllMyEntityRankIndex() {
        HollowTypeDataAccess tda = Objects.requireNonNull(getDataAccess().getTypeDataAccess("MyEntityRankIndex"), "type not loaded or does not exist in dataset; type=MyEntityRankIndex");
        return new AllHollowRecordCollection<MyEntityRankIndex>(tda.getTypeState()) {
            protected MyEntityRankIndex getForOrdinal(int ordinal) {
                return getMyEntityRankIndex(ordinal);
            }
        };
    }
    public MyEntityRankIndex getMyEntityRankIndex(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (MyEntityRankIndex)myEntityRankIndexProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
