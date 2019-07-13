package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

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
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class Gk2StatusAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final DateTypeAPI dateTypeAPI;
    private final MapKeyTypeAPI mapKeyTypeAPI;
    private final MapOfFlagsFirstDisplayDatesTypeAPI mapOfFlagsFirstDisplayDatesTypeAPI;
    private final ParentNodeIdTypeAPI parentNodeIdTypeAPI;
    private final RightsContractPackageTypeAPI rightsContractPackageTypeAPI;
    private final ListOfRightsContractPackageTypeAPI listOfRightsContractPackageTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final ListOfStringTypeAPI listOfStringTypeAPI;
    private final RightsContractAssetTypeAPI rightsContractAssetTypeAPI;
    private final ListOfRightsContractAssetTypeAPI listOfRightsContractAssetTypeAPI;
    private final RightsWindowContractTypeAPI rightsWindowContractTypeAPI;
    private final ListOfRightsWindowContractTypeAPI listOfRightsWindowContractTypeAPI;
    private final RightsWindowTypeAPI rightsWindowTypeAPI;
    private final ListOfRightsWindowTypeAPI listOfRightsWindowTypeAPI;
    private final RightsTypeAPI rightsTypeAPI;
    private final SetOfStringTypeAPI setOfStringTypeAPI;
    private final AvailableAssetsTypeAPI availableAssetsTypeAPI;
    private final FlagsTypeAPI flagsTypeAPI;
    private final VideoNodeTypeTypeAPI videoNodeTypeTypeAPI;
    private final VideoHierarchyInfoTypeAPI videoHierarchyInfoTypeAPI;
    private final StatusTypeAPI statusTypeAPI;

    private final HollowObjectProvider dateProvider;
    private final HollowObjectProvider mapKeyProvider;
    private final HollowObjectProvider mapOfFlagsFirstDisplayDatesProvider;
    private final HollowObjectProvider parentNodeIdProvider;
    private final HollowObjectProvider rightsContractPackageProvider;
    private final HollowObjectProvider listOfRightsContractPackageProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider listOfStringProvider;
    private final HollowObjectProvider rightsContractAssetProvider;
    private final HollowObjectProvider listOfRightsContractAssetProvider;
    private final HollowObjectProvider rightsWindowContractProvider;
    private final HollowObjectProvider listOfRightsWindowContractProvider;
    private final HollowObjectProvider rightsWindowProvider;
    private final HollowObjectProvider listOfRightsWindowProvider;
    private final HollowObjectProvider rightsProvider;
    private final HollowObjectProvider setOfStringProvider;
    private final HollowObjectProvider availableAssetsProvider;
    private final HollowObjectProvider flagsProvider;
    private final HollowObjectProvider videoNodeTypeProvider;
    private final HollowObjectProvider videoHierarchyInfoProvider;
    private final HollowObjectProvider statusProvider;

    public Gk2StatusAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public Gk2StatusAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public Gk2StatusAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public Gk2StatusAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, com.netflix.vms.transformer.input.api.gen.gatekeeper2.Gk2StatusAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Date","MapKey","MapOfFlagsFirstDisplayDates","ParentNodeId","RightsContractPackage","ListOfRightsContractPackage","String","ListOfString","RightsContractAsset","ListOfRightsContractAsset","RightsWindowContract","ListOfRightsWindowContract","RightsWindow","ListOfRightsWindow","Rights","SetOfString","AvailableAssets","Flags","VideoNodeType","VideoHierarchyInfo","Status");

        typeDataAccess = dataAccess.getTypeDataAccess("Date");
        if(typeDataAccess != null) {
            dateTypeAPI = new DateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            dateTypeAPI = new DateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Date"));
        }
        addTypeAPI(dateTypeAPI);
        factory = factoryOverrides.get("Date");
        if(factory == null)
            factory = new DateHollowFactory();
        if(cachedTypes.contains("Date")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.dateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.dateProvider;
            dateProvider = new HollowObjectCacheProvider(typeDataAccess, dateTypeAPI, factory, previousCacheProvider);
        } else {
            dateProvider = new HollowObjectFactoryProvider(typeDataAccess, dateTypeAPI, factory);
        }

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

        typeDataAccess = dataAccess.getTypeDataAccess("MapOfFlagsFirstDisplayDates");
        if(typeDataAccess != null) {
            mapOfFlagsFirstDisplayDatesTypeAPI = new MapOfFlagsFirstDisplayDatesTypeAPI(this, (HollowMapTypeDataAccess)typeDataAccess);
        } else {
            mapOfFlagsFirstDisplayDatesTypeAPI = new MapOfFlagsFirstDisplayDatesTypeAPI(this, new HollowMapMissingDataAccess(dataAccess, "MapOfFlagsFirstDisplayDates"));
        }
        addTypeAPI(mapOfFlagsFirstDisplayDatesTypeAPI);
        factory = factoryOverrides.get("MapOfFlagsFirstDisplayDates");
        if(factory == null)
            factory = new MapOfFlagsFirstDisplayDatesHollowFactory();
        if(cachedTypes.contains("MapOfFlagsFirstDisplayDates")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.mapOfFlagsFirstDisplayDatesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.mapOfFlagsFirstDisplayDatesProvider;
            mapOfFlagsFirstDisplayDatesProvider = new HollowObjectCacheProvider(typeDataAccess, mapOfFlagsFirstDisplayDatesTypeAPI, factory, previousCacheProvider);
        } else {
            mapOfFlagsFirstDisplayDatesProvider = new HollowObjectFactoryProvider(typeDataAccess, mapOfFlagsFirstDisplayDatesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ParentNodeId");
        if(typeDataAccess != null) {
            parentNodeIdTypeAPI = new ParentNodeIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            parentNodeIdTypeAPI = new ParentNodeIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ParentNodeId"));
        }
        addTypeAPI(parentNodeIdTypeAPI);
        factory = factoryOverrides.get("ParentNodeId");
        if(factory == null)
            factory = new ParentNodeIdHollowFactory();
        if(cachedTypes.contains("ParentNodeId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.parentNodeIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.parentNodeIdProvider;
            parentNodeIdProvider = new HollowObjectCacheProvider(typeDataAccess, parentNodeIdTypeAPI, factory, previousCacheProvider);
        } else {
            parentNodeIdProvider = new HollowObjectFactoryProvider(typeDataAccess, parentNodeIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsContractPackage");
        if(typeDataAccess != null) {
            rightsContractPackageTypeAPI = new RightsContractPackageTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsContractPackageTypeAPI = new RightsContractPackageTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsContractPackage"));
        }
        addTypeAPI(rightsContractPackageTypeAPI);
        factory = factoryOverrides.get("RightsContractPackage");
        if(factory == null)
            factory = new RightsContractPackageHollowFactory();
        if(cachedTypes.contains("RightsContractPackage")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsContractPackageProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsContractPackageProvider;
            rightsContractPackageProvider = new HollowObjectCacheProvider(typeDataAccess, rightsContractPackageTypeAPI, factory, previousCacheProvider);
        } else {
            rightsContractPackageProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsContractPackageTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsContractPackage");
        if(typeDataAccess != null) {
            listOfRightsContractPackageTypeAPI = new ListOfRightsContractPackageTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsContractPackageTypeAPI = new ListOfRightsContractPackageTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsContractPackage"));
        }
        addTypeAPI(listOfRightsContractPackageTypeAPI);
        factory = factoryOverrides.get("ListOfRightsContractPackage");
        if(factory == null)
            factory = new ListOfRightsContractPackageHollowFactory();
        if(cachedTypes.contains("ListOfRightsContractPackage")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsContractPackageProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsContractPackageProvider;
            listOfRightsContractPackageProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsContractPackageTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsContractPackageProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsContractPackageTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfString");
        if(typeDataAccess != null) {
            listOfStringTypeAPI = new ListOfStringTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfStringTypeAPI = new ListOfStringTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfString"));
        }
        addTypeAPI(listOfStringTypeAPI);
        factory = factoryOverrides.get("ListOfString");
        if(factory == null)
            factory = new ListOfStringHollowFactory();
        if(cachedTypes.contains("ListOfString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfStringProvider;
            listOfStringProvider = new HollowObjectCacheProvider(typeDataAccess, listOfStringTypeAPI, factory, previousCacheProvider);
        } else {
            listOfStringProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsContractAsset");
        if(typeDataAccess != null) {
            rightsContractAssetTypeAPI = new RightsContractAssetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsContractAssetTypeAPI = new RightsContractAssetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsContractAsset"));
        }
        addTypeAPI(rightsContractAssetTypeAPI);
        factory = factoryOverrides.get("RightsContractAsset");
        if(factory == null)
            factory = new RightsContractAssetHollowFactory();
        if(cachedTypes.contains("RightsContractAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsContractAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsContractAssetProvider;
            rightsContractAssetProvider = new HollowObjectCacheProvider(typeDataAccess, rightsContractAssetTypeAPI, factory, previousCacheProvider);
        } else {
            rightsContractAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsContractAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsContractAsset");
        if(typeDataAccess != null) {
            listOfRightsContractAssetTypeAPI = new ListOfRightsContractAssetTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsContractAssetTypeAPI = new ListOfRightsContractAssetTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsContractAsset"));
        }
        addTypeAPI(listOfRightsContractAssetTypeAPI);
        factory = factoryOverrides.get("ListOfRightsContractAsset");
        if(factory == null)
            factory = new ListOfRightsContractAssetHollowFactory();
        if(cachedTypes.contains("ListOfRightsContractAsset")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsContractAssetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsContractAssetProvider;
            listOfRightsContractAssetProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsContractAssetTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsContractAssetProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsContractAssetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsWindowContract");
        if(typeDataAccess != null) {
            rightsWindowContractTypeAPI = new RightsWindowContractTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsWindowContractTypeAPI = new RightsWindowContractTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsWindowContract"));
        }
        addTypeAPI(rightsWindowContractTypeAPI);
        factory = factoryOverrides.get("RightsWindowContract");
        if(factory == null)
            factory = new RightsWindowContractHollowFactory();
        if(cachedTypes.contains("RightsWindowContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsWindowContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsWindowContractProvider;
            rightsWindowContractProvider = new HollowObjectCacheProvider(typeDataAccess, rightsWindowContractTypeAPI, factory, previousCacheProvider);
        } else {
            rightsWindowContractProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsWindowContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsWindowContract");
        if(typeDataAccess != null) {
            listOfRightsWindowContractTypeAPI = new ListOfRightsWindowContractTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsWindowContractTypeAPI = new ListOfRightsWindowContractTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsWindowContract"));
        }
        addTypeAPI(listOfRightsWindowContractTypeAPI);
        factory = factoryOverrides.get("ListOfRightsWindowContract");
        if(factory == null)
            factory = new ListOfRightsWindowContractHollowFactory();
        if(cachedTypes.contains("ListOfRightsWindowContract")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsWindowContractProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsWindowContractProvider;
            listOfRightsWindowContractProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsWindowContractTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsWindowContractProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsWindowContractTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RightsWindow");
        if(typeDataAccess != null) {
            rightsWindowTypeAPI = new RightsWindowTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsWindowTypeAPI = new RightsWindowTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RightsWindow"));
        }
        addTypeAPI(rightsWindowTypeAPI);
        factory = factoryOverrides.get("RightsWindow");
        if(factory == null)
            factory = new RightsWindowHollowFactory();
        if(cachedTypes.contains("RightsWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsWindowProvider;
            rightsWindowProvider = new HollowObjectCacheProvider(typeDataAccess, rightsWindowTypeAPI, factory, previousCacheProvider);
        } else {
            rightsWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfRightsWindow");
        if(typeDataAccess != null) {
            listOfRightsWindowTypeAPI = new ListOfRightsWindowTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfRightsWindowTypeAPI = new ListOfRightsWindowTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfRightsWindow"));
        }
        addTypeAPI(listOfRightsWindowTypeAPI);
        factory = factoryOverrides.get("ListOfRightsWindow");
        if(factory == null)
            factory = new ListOfRightsWindowHollowFactory();
        if(cachedTypes.contains("ListOfRightsWindow")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfRightsWindowProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfRightsWindowProvider;
            listOfRightsWindowProvider = new HollowObjectCacheProvider(typeDataAccess, listOfRightsWindowTypeAPI, factory, previousCacheProvider);
        } else {
            listOfRightsWindowProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfRightsWindowTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Rights");
        if(typeDataAccess != null) {
            rightsTypeAPI = new RightsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rightsTypeAPI = new RightsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Rights"));
        }
        addTypeAPI(rightsTypeAPI);
        factory = factoryOverrides.get("Rights");
        if(factory == null)
            factory = new RightsHollowFactory();
        if(cachedTypes.contains("Rights")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rightsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rightsProvider;
            rightsProvider = new HollowObjectCacheProvider(typeDataAccess, rightsTypeAPI, factory, previousCacheProvider);
        } else {
            rightsProvider = new HollowObjectFactoryProvider(typeDataAccess, rightsTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("AvailableAssets");
        if(typeDataAccess != null) {
            availableAssetsTypeAPI = new AvailableAssetsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            availableAssetsTypeAPI = new AvailableAssetsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AvailableAssets"));
        }
        addTypeAPI(availableAssetsTypeAPI);
        factory = factoryOverrides.get("AvailableAssets");
        if(factory == null)
            factory = new AvailableAssetsHollowFactory();
        if(cachedTypes.contains("AvailableAssets")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.availableAssetsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.availableAssetsProvider;
            availableAssetsProvider = new HollowObjectCacheProvider(typeDataAccess, availableAssetsTypeAPI, factory, previousCacheProvider);
        } else {
            availableAssetsProvider = new HollowObjectFactoryProvider(typeDataAccess, availableAssetsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Flags");
        if(typeDataAccess != null) {
            flagsTypeAPI = new FlagsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            flagsTypeAPI = new FlagsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Flags"));
        }
        addTypeAPI(flagsTypeAPI);
        factory = factoryOverrides.get("Flags");
        if(factory == null)
            factory = new FlagsHollowFactory();
        if(cachedTypes.contains("Flags")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.flagsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.flagsProvider;
            flagsProvider = new HollowObjectCacheProvider(typeDataAccess, flagsTypeAPI, factory, previousCacheProvider);
        } else {
            flagsProvider = new HollowObjectFactoryProvider(typeDataAccess, flagsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoNodeType");
        if(typeDataAccess != null) {
            videoNodeTypeTypeAPI = new VideoNodeTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoNodeTypeTypeAPI = new VideoNodeTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoNodeType"));
        }
        addTypeAPI(videoNodeTypeTypeAPI);
        factory = factoryOverrides.get("VideoNodeType");
        if(factory == null)
            factory = new VideoNodeTypeHollowFactory();
        if(cachedTypes.contains("VideoNodeType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoNodeTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoNodeTypeProvider;
            videoNodeTypeProvider = new HollowObjectCacheProvider(typeDataAccess, videoNodeTypeTypeAPI, factory, previousCacheProvider);
        } else {
            videoNodeTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, videoNodeTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("VideoHierarchyInfo");
        if(typeDataAccess != null) {
            videoHierarchyInfoTypeAPI = new VideoHierarchyInfoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            videoHierarchyInfoTypeAPI = new VideoHierarchyInfoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "VideoHierarchyInfo"));
        }
        addTypeAPI(videoHierarchyInfoTypeAPI);
        factory = factoryOverrides.get("VideoHierarchyInfo");
        if(factory == null)
            factory = new VideoHierarchyInfoHollowFactory();
        if(cachedTypes.contains("VideoHierarchyInfo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.videoHierarchyInfoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.videoHierarchyInfoProvider;
            videoHierarchyInfoProvider = new HollowObjectCacheProvider(typeDataAccess, videoHierarchyInfoTypeAPI, factory, previousCacheProvider);
        } else {
            videoHierarchyInfoProvider = new HollowObjectFactoryProvider(typeDataAccess, videoHierarchyInfoTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Status");
        if(typeDataAccess != null) {
            statusTypeAPI = new StatusTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            statusTypeAPI = new StatusTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Status"));
        }
        addTypeAPI(statusTypeAPI);
        factory = factoryOverrides.get("Status");
        if(factory == null)
            factory = new StatusHollowFactory();
        if(cachedTypes.contains("Status")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.statusProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.statusProvider;
            statusProvider = new HollowObjectCacheProvider(typeDataAccess, statusTypeAPI, factory, previousCacheProvider);
        } else {
            statusProvider = new HollowObjectFactoryProvider(typeDataAccess, statusTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(dateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)dateProvider).detach();
        if(mapKeyProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapKeyProvider).detach();
        if(mapOfFlagsFirstDisplayDatesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)mapOfFlagsFirstDisplayDatesProvider).detach();
        if(parentNodeIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)parentNodeIdProvider).detach();
        if(rightsContractPackageProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsContractPackageProvider).detach();
        if(listOfRightsContractPackageProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsContractPackageProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(listOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfStringProvider).detach();
        if(rightsContractAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsContractAssetProvider).detach();
        if(listOfRightsContractAssetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsContractAssetProvider).detach();
        if(rightsWindowContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsWindowContractProvider).detach();
        if(listOfRightsWindowContractProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsWindowContractProvider).detach();
        if(rightsWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsWindowProvider).detach();
        if(listOfRightsWindowProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfRightsWindowProvider).detach();
        if(rightsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rightsProvider).detach();
        if(setOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfStringProvider).detach();
        if(availableAssetsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)availableAssetsProvider).detach();
        if(flagsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)flagsProvider).detach();
        if(videoNodeTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoNodeTypeProvider).detach();
        if(videoHierarchyInfoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)videoHierarchyInfoProvider).detach();
        if(statusProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)statusProvider).detach();
    }

    public DateTypeAPI getDateTypeAPI() {
        return dateTypeAPI;
    }
    public MapKeyTypeAPI getMapKeyTypeAPI() {
        return mapKeyTypeAPI;
    }
    public MapOfFlagsFirstDisplayDatesTypeAPI getMapOfFlagsFirstDisplayDatesTypeAPI() {
        return mapOfFlagsFirstDisplayDatesTypeAPI;
    }
    public ParentNodeIdTypeAPI getParentNodeIdTypeAPI() {
        return parentNodeIdTypeAPI;
    }
    public RightsContractPackageTypeAPI getRightsContractPackageTypeAPI() {
        return rightsContractPackageTypeAPI;
    }
    public ListOfRightsContractPackageTypeAPI getListOfRightsContractPackageTypeAPI() {
        return listOfRightsContractPackageTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public ListOfStringTypeAPI getListOfStringTypeAPI() {
        return listOfStringTypeAPI;
    }
    public RightsContractAssetTypeAPI getRightsContractAssetTypeAPI() {
        return rightsContractAssetTypeAPI;
    }
    public ListOfRightsContractAssetTypeAPI getListOfRightsContractAssetTypeAPI() {
        return listOfRightsContractAssetTypeAPI;
    }
    public RightsWindowContractTypeAPI getRightsWindowContractTypeAPI() {
        return rightsWindowContractTypeAPI;
    }
    public ListOfRightsWindowContractTypeAPI getListOfRightsWindowContractTypeAPI() {
        return listOfRightsWindowContractTypeAPI;
    }
    public RightsWindowTypeAPI getRightsWindowTypeAPI() {
        return rightsWindowTypeAPI;
    }
    public ListOfRightsWindowTypeAPI getListOfRightsWindowTypeAPI() {
        return listOfRightsWindowTypeAPI;
    }
    public RightsTypeAPI getRightsTypeAPI() {
        return rightsTypeAPI;
    }
    public SetOfStringTypeAPI getSetOfStringTypeAPI() {
        return setOfStringTypeAPI;
    }
    public AvailableAssetsTypeAPI getAvailableAssetsTypeAPI() {
        return availableAssetsTypeAPI;
    }
    public FlagsTypeAPI getFlagsTypeAPI() {
        return flagsTypeAPI;
    }
    public VideoNodeTypeTypeAPI getVideoNodeTypeTypeAPI() {
        return videoNodeTypeTypeAPI;
    }
    public VideoHierarchyInfoTypeAPI getVideoHierarchyInfoTypeAPI() {
        return videoHierarchyInfoTypeAPI;
    }
    public StatusTypeAPI getStatusTypeAPI() {
        return statusTypeAPI;
    }
    public Collection<Date> getAllDate() {
        return new AllHollowRecordCollection<Date>(getDataAccess().getTypeDataAccess("Date").getTypeState()) {
            protected Date getForOrdinal(int ordinal) {
                return getDate(ordinal);
            }
        };
    }
    public Date getDate(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (Date)dateProvider.getHollowObject(ordinal);
    }
    public Collection<MapKey> getAllMapKey() {
        return new AllHollowRecordCollection<MapKey>(getDataAccess().getTypeDataAccess("MapKey").getTypeState()) {
            protected MapKey getForOrdinal(int ordinal) {
                return getMapKey(ordinal);
            }
        };
    }
    public MapKey getMapKey(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (MapKey)mapKeyProvider.getHollowObject(ordinal);
    }
    public Collection<MapOfFlagsFirstDisplayDates> getAllMapOfFlagsFirstDisplayDates() {
        return new AllHollowRecordCollection<MapOfFlagsFirstDisplayDates>(getDataAccess().getTypeDataAccess("MapOfFlagsFirstDisplayDates").getTypeState()) {
            protected MapOfFlagsFirstDisplayDates getForOrdinal(int ordinal) {
                return getMapOfFlagsFirstDisplayDates(ordinal);
            }
        };
    }
    public MapOfFlagsFirstDisplayDates getMapOfFlagsFirstDisplayDates(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (MapOfFlagsFirstDisplayDates)mapOfFlagsFirstDisplayDatesProvider.getHollowObject(ordinal);
    }
    public Collection<ParentNodeId> getAllParentNodeId() {
        return new AllHollowRecordCollection<ParentNodeId>(getDataAccess().getTypeDataAccess("ParentNodeId").getTypeState()) {
            protected ParentNodeId getForOrdinal(int ordinal) {
                return getParentNodeId(ordinal);
            }
        };
    }
    public ParentNodeId getParentNodeId(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (ParentNodeId)parentNodeIdProvider.getHollowObject(ordinal);
    }
    public Collection<RightsContractPackage> getAllRightsContractPackage() {
        return new AllHollowRecordCollection<RightsContractPackage>(getDataAccess().getTypeDataAccess("RightsContractPackage").getTypeState()) {
            protected RightsContractPackage getForOrdinal(int ordinal) {
                return getRightsContractPackage(ordinal);
            }
        };
    }
    public RightsContractPackage getRightsContractPackage(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (RightsContractPackage)rightsContractPackageProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsContractPackage> getAllListOfRightsContractPackage() {
        return new AllHollowRecordCollection<ListOfRightsContractPackage>(getDataAccess().getTypeDataAccess("ListOfRightsContractPackage").getTypeState()) {
            protected ListOfRightsContractPackage getForOrdinal(int ordinal) {
                return getListOfRightsContractPackage(ordinal);
            }
        };
    }
    public ListOfRightsContractPackage getListOfRightsContractPackage(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (ListOfRightsContractPackage)listOfRightsContractPackageProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfString> getAllListOfString() {
        return new AllHollowRecordCollection<ListOfString>(getDataAccess().getTypeDataAccess("ListOfString").getTypeState()) {
            protected ListOfString getForOrdinal(int ordinal) {
                return getListOfString(ordinal);
            }
        };
    }
    public ListOfString getListOfString(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (ListOfString)listOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<RightsContractAsset> getAllRightsContractAsset() {
        return new AllHollowRecordCollection<RightsContractAsset>(getDataAccess().getTypeDataAccess("RightsContractAsset").getTypeState()) {
            protected RightsContractAsset getForOrdinal(int ordinal) {
                return getRightsContractAsset(ordinal);
            }
        };
    }
    public RightsContractAsset getRightsContractAsset(int ordinal) {
        objectCreationSampler.recordCreation(8);
        return (RightsContractAsset)rightsContractAssetProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsContractAsset> getAllListOfRightsContractAsset() {
        return new AllHollowRecordCollection<ListOfRightsContractAsset>(getDataAccess().getTypeDataAccess("ListOfRightsContractAsset").getTypeState()) {
            protected ListOfRightsContractAsset getForOrdinal(int ordinal) {
                return getListOfRightsContractAsset(ordinal);
            }
        };
    }
    public ListOfRightsContractAsset getListOfRightsContractAsset(int ordinal) {
        objectCreationSampler.recordCreation(9);
        return (ListOfRightsContractAsset)listOfRightsContractAssetProvider.getHollowObject(ordinal);
    }
    public Collection<RightsWindowContract> getAllRightsWindowContract() {
        return new AllHollowRecordCollection<RightsWindowContract>(getDataAccess().getTypeDataAccess("RightsWindowContract").getTypeState()) {
            protected RightsWindowContract getForOrdinal(int ordinal) {
                return getRightsWindowContract(ordinal);
            }
        };
    }
    public RightsWindowContract getRightsWindowContract(int ordinal) {
        objectCreationSampler.recordCreation(10);
        return (RightsWindowContract)rightsWindowContractProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsWindowContract> getAllListOfRightsWindowContract() {
        return new AllHollowRecordCollection<ListOfRightsWindowContract>(getDataAccess().getTypeDataAccess("ListOfRightsWindowContract").getTypeState()) {
            protected ListOfRightsWindowContract getForOrdinal(int ordinal) {
                return getListOfRightsWindowContract(ordinal);
            }
        };
    }
    public ListOfRightsWindowContract getListOfRightsWindowContract(int ordinal) {
        objectCreationSampler.recordCreation(11);
        return (ListOfRightsWindowContract)listOfRightsWindowContractProvider.getHollowObject(ordinal);
    }
    public Collection<RightsWindow> getAllRightsWindow() {
        return new AllHollowRecordCollection<RightsWindow>(getDataAccess().getTypeDataAccess("RightsWindow").getTypeState()) {
            protected RightsWindow getForOrdinal(int ordinal) {
                return getRightsWindow(ordinal);
            }
        };
    }
    public RightsWindow getRightsWindow(int ordinal) {
        objectCreationSampler.recordCreation(12);
        return (RightsWindow)rightsWindowProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfRightsWindow> getAllListOfRightsWindow() {
        return new AllHollowRecordCollection<ListOfRightsWindow>(getDataAccess().getTypeDataAccess("ListOfRightsWindow").getTypeState()) {
            protected ListOfRightsWindow getForOrdinal(int ordinal) {
                return getListOfRightsWindow(ordinal);
            }
        };
    }
    public ListOfRightsWindow getListOfRightsWindow(int ordinal) {
        objectCreationSampler.recordCreation(13);
        return (ListOfRightsWindow)listOfRightsWindowProvider.getHollowObject(ordinal);
    }
    public Collection<Rights> getAllRights() {
        return new AllHollowRecordCollection<Rights>(getDataAccess().getTypeDataAccess("Rights").getTypeState()) {
            protected Rights getForOrdinal(int ordinal) {
                return getRights(ordinal);
            }
        };
    }
    public Rights getRights(int ordinal) {
        objectCreationSampler.recordCreation(14);
        return (Rights)rightsProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfString> getAllSetOfString() {
        return new AllHollowRecordCollection<SetOfString>(getDataAccess().getTypeDataAccess("SetOfString").getTypeState()) {
            protected SetOfString getForOrdinal(int ordinal) {
                return getSetOfString(ordinal);
            }
        };
    }
    public SetOfString getSetOfString(int ordinal) {
        objectCreationSampler.recordCreation(15);
        return (SetOfString)setOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<AvailableAssets> getAllAvailableAssets() {
        return new AllHollowRecordCollection<AvailableAssets>(getDataAccess().getTypeDataAccess("AvailableAssets").getTypeState()) {
            protected AvailableAssets getForOrdinal(int ordinal) {
                return getAvailableAssets(ordinal);
            }
        };
    }
    public AvailableAssets getAvailableAssets(int ordinal) {
        objectCreationSampler.recordCreation(16);
        return (AvailableAssets)availableAssetsProvider.getHollowObject(ordinal);
    }
    public Collection<Flags> getAllFlags() {
        return new AllHollowRecordCollection<Flags>(getDataAccess().getTypeDataAccess("Flags").getTypeState()) {
            protected Flags getForOrdinal(int ordinal) {
                return getFlags(ordinal);
            }
        };
    }
    public Flags getFlags(int ordinal) {
        objectCreationSampler.recordCreation(17);
        return (Flags)flagsProvider.getHollowObject(ordinal);
    }
    public Collection<VideoNodeType> getAllVideoNodeType() {
        return new AllHollowRecordCollection<VideoNodeType>(getDataAccess().getTypeDataAccess("VideoNodeType").getTypeState()) {
            protected VideoNodeType getForOrdinal(int ordinal) {
                return getVideoNodeType(ordinal);
            }
        };
    }
    public VideoNodeType getVideoNodeType(int ordinal) {
        objectCreationSampler.recordCreation(18);
        return (VideoNodeType)videoNodeTypeProvider.getHollowObject(ordinal);
    }
    public Collection<VideoHierarchyInfo> getAllVideoHierarchyInfo() {
        return new AllHollowRecordCollection<VideoHierarchyInfo>(getDataAccess().getTypeDataAccess("VideoHierarchyInfo").getTypeState()) {
            protected VideoHierarchyInfo getForOrdinal(int ordinal) {
                return getVideoHierarchyInfo(ordinal);
            }
        };
    }
    public VideoHierarchyInfo getVideoHierarchyInfo(int ordinal) {
        objectCreationSampler.recordCreation(19);
        return (VideoHierarchyInfo)videoHierarchyInfoProvider.getHollowObject(ordinal);
    }
    public Collection<Status> getAllStatus() {
        return new AllHollowRecordCollection<Status>(getDataAccess().getTypeDataAccess("Status").getTypeState()) {
            protected Status getForOrdinal(int ordinal) {
                return getStatus(ordinal);
            }
        };
    }
    public Status getStatus(int ordinal) {
        objectCreationSampler.recordCreation(20);
        return (Status)statusProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
