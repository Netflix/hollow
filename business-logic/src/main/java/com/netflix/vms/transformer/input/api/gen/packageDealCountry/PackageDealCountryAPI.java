package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

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
public class PackageDealCountryAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final BooleanTypeAPI booleanTypeAPI;
    private final LongTypeAPI longTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final ListOfPackageTagsTypeAPI listOfPackageTagsTypeAPI;
    private final MapOfStringToBooleanTypeAPI mapOfStringToBooleanTypeAPI;
    private final DealCountryGroupTypeAPI dealCountryGroupTypeAPI;
    private final ListOfDealCountryGroupTypeAPI listOfDealCountryGroupTypeAPI;
    private final PackageMovieDealCountryGroupTypeAPI packageMovieDealCountryGroupTypeAPI;

    private final HollowObjectProvider booleanProvider;
    private final HollowObjectProvider longProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider listOfPackageTagsProvider;
    private final HollowObjectProvider mapOfStringToBooleanProvider;
    private final HollowObjectProvider dealCountryGroupProvider;
    private final HollowObjectProvider listOfDealCountryGroupProvider;
    private final HollowObjectProvider packageMovieDealCountryGroupProvider;

    public PackageDealCountryAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public PackageDealCountryAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public PackageDealCountryAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public PackageDealCountryAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, PackageDealCountryAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Boolean","Long","String","ListOfPackageTags","MapOfStringToBoolean","DealCountryGroup","ListOfDealCountryGroup","PackageMovieDealCountryGroup");

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

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfPackageTags");
        if(typeDataAccess != null) {
            listOfPackageTagsTypeAPI = new ListOfPackageTagsTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfPackageTagsTypeAPI = new ListOfPackageTagsTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfPackageTags"));
        }
        addTypeAPI(listOfPackageTagsTypeAPI);
        factory = factoryOverrides.get("ListOfPackageTags");
        if(factory == null)
            factory = new ListOfPackageTagsHollowFactory();
        if(cachedTypes.contains("ListOfPackageTags")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfPackageTagsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfPackageTagsProvider;
            listOfPackageTagsProvider = new HollowObjectCacheProvider(typeDataAccess, listOfPackageTagsTypeAPI, factory, previousCacheProvider);
        } else {
            listOfPackageTagsProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfPackageTagsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfStringToBoolean");
        if(typeDataAccess != null) {
            mapOfStringToBooleanTypeAPI = new MapOfStringToBooleanTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfStringToBooleanTypeAPI = new MapOfStringToBooleanTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfStringToBoolean"));
        }
        addTypeAPI(mapOfStringToBooleanTypeAPI);
        factory = factoryOverrides.get("MapOfStringToBoolean");
        if(factory == null)
            factory = new MapOfStringToBooleanHollowFactory();
        if(cachedTypes.contains("MapOfStringToBoolean")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfStringToBooleanProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfStringToBooleanProvider;
            mapOfStringToBooleanProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfStringToBooleanTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfStringToBooleanProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfStringToBooleanTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("DealCountryGroup");
        if(typeDataAccess != null) {
            dealCountryGroupTypeAPI = new DealCountryGroupTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            dealCountryGroupTypeAPI = new DealCountryGroupTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DealCountryGroup"));
        }
        addTypeAPI(dealCountryGroupTypeAPI);
        factory = factoryOverrides.get("DealCountryGroup");
        if(factory == null)
            factory = new DealCountryGroupHollowFactory();
        if(cachedTypes.contains("DealCountryGroup")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.dealCountryGroupProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.dealCountryGroupProvider;
            dealCountryGroupProvider = new HollowObjectCacheProvider(typeDataAccess, dealCountryGroupTypeAPI, factory, previousCacheProvider);
        } else {
            dealCountryGroupProvider = new HollowObjectFactoryProvider(typeDataAccess, dealCountryGroupTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfDealCountryGroup");
        if(typeDataAccess != null) {
            listOfDealCountryGroupTypeAPI = new ListOfDealCountryGroupTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfDealCountryGroupTypeAPI = new ListOfDealCountryGroupTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfDealCountryGroup"));
        }
        addTypeAPI(listOfDealCountryGroupTypeAPI);
        factory = factoryOverrides.get("ListOfDealCountryGroup");
        if(factory == null)
            factory = new ListOfDealCountryGroupHollowFactory();
        if(cachedTypes.contains("ListOfDealCountryGroup")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfDealCountryGroupProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfDealCountryGroupProvider;
            listOfDealCountryGroupProvider = new HollowObjectCacheProvider(typeDataAccess, listOfDealCountryGroupTypeAPI, factory, previousCacheProvider);
        } else {
            listOfDealCountryGroupProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfDealCountryGroupTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PackageMovieDealCountryGroup");
        if(typeDataAccess != null) {
            packageMovieDealCountryGroupTypeAPI = new PackageMovieDealCountryGroupTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            packageMovieDealCountryGroupTypeAPI = new PackageMovieDealCountryGroupTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PackageMovieDealCountryGroup"));
        }
        addTypeAPI(packageMovieDealCountryGroupTypeAPI);
        factory = factoryOverrides.get("PackageMovieDealCountryGroup");
        if(factory == null)
            factory = new PackageMovieDealCountryGroupHollowFactory();
        if(cachedTypes.contains("PackageMovieDealCountryGroup")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.packageMovieDealCountryGroupProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.packageMovieDealCountryGroupProvider;
            packageMovieDealCountryGroupProvider = new HollowObjectCacheProvider(typeDataAccess, packageMovieDealCountryGroupTypeAPI, factory, previousCacheProvider);
        } else {
            packageMovieDealCountryGroupProvider = new HollowObjectFactoryProvider(typeDataAccess, packageMovieDealCountryGroupTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(booleanProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)booleanProvider).detach();
        if(longProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)longProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(listOfPackageTagsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfPackageTagsProvider).detach();
        if(mapOfStringToBooleanProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfStringToBooleanProvider).detach();
        if(dealCountryGroupProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)dealCountryGroupProvider).detach();
        if(listOfDealCountryGroupProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfDealCountryGroupProvider).detach();
        if(packageMovieDealCountryGroupProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)packageMovieDealCountryGroupProvider).detach();
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
    public ListOfPackageTagsTypeAPI getListOfPackageTagsTypeAPI() {
        return listOfPackageTagsTypeAPI;
    }
    public MapOfStringToBooleanTypeAPI getMapOfStringToBooleanTypeAPI() {
        return mapOfStringToBooleanTypeAPI;
    }
    public DealCountryGroupTypeAPI getDealCountryGroupTypeAPI() {
        return dealCountryGroupTypeAPI;
    }
    public ListOfDealCountryGroupTypeAPI getListOfDealCountryGroupTypeAPI() {
        return listOfDealCountryGroupTypeAPI;
    }
    public PackageMovieDealCountryGroupTypeAPI getPackageMovieDealCountryGroupTypeAPI() {
        return packageMovieDealCountryGroupTypeAPI;
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
    public Collection<ListOfPackageTags> getAllListOfPackageTags() {
        return new AllHollowRecordCollection<ListOfPackageTags>(getDataAccess().getTypeDataAccess("ListOfPackageTags").getTypeState()) {
            protected ListOfPackageTags getForOrdinal(int ordinal) {
                return getListOfPackageTags(ordinal);
            }
        };
    }
    public ListOfPackageTags getListOfPackageTags(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (ListOfPackageTags)listOfPackageTagsProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfStringToBoolean> getAllMapOfStringToBoolean() {
        return new AllHollowRecordCollection<MapOfStringToBoolean>(getDataAccess().getTypeDataAccess("MapOfStringToBoolean").getTypeState()) {
            protected MapOfStringToBoolean getForOrdinal(int ordinal) {
                return getMapOfStringToBoolean(ordinal);
            }
        };
    }
    public MapOfStringToBoolean getMapOfStringToBoolean(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (MapOfStringToBoolean)mapOfStringToBooleanProvider.getHollowObject(ordinal);
    }
    public Collection<DealCountryGroup> getAllDealCountryGroup() {
        return new AllHollowRecordCollection<DealCountryGroup>(getDataAccess().getTypeDataAccess("DealCountryGroup").getTypeState()) {
            protected DealCountryGroup getForOrdinal(int ordinal) {
                return getDealCountryGroup(ordinal);
            }
        };
    }
    public DealCountryGroup getDealCountryGroup(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (DealCountryGroup)dealCountryGroupProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfDealCountryGroup> getAllListOfDealCountryGroup() {
        return new AllHollowRecordCollection<ListOfDealCountryGroup>(getDataAccess().getTypeDataAccess("ListOfDealCountryGroup").getTypeState()) {
            protected ListOfDealCountryGroup getForOrdinal(int ordinal) {
                return getListOfDealCountryGroup(ordinal);
            }
        };
    }
    public ListOfDealCountryGroup getListOfDealCountryGroup(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (ListOfDealCountryGroup)listOfDealCountryGroupProvider.getHollowObject(ordinal);
    }
    public Collection<PackageMovieDealCountryGroup> getAllPackageMovieDealCountryGroup() {
        return new AllHollowRecordCollection<PackageMovieDealCountryGroup>(getDataAccess().getTypeDataAccess("PackageMovieDealCountryGroup").getTypeState()) {
            protected PackageMovieDealCountryGroup getForOrdinal(int ordinal) {
                return getPackageMovieDealCountryGroup(ordinal);
            }
        };
    }
    public PackageMovieDealCountryGroup getPackageMovieDealCountryGroup(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (PackageMovieDealCountryGroup)packageMovieDealCountryGroupProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
