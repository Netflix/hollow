package com.netflix.vms.transformer.input.api.gen.flexds;

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
public class FlexDSAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final ContainerTypeAPI containerTypeAPI;
    private final SetOfContainerTypeAPI setOfContainerTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final AuditGroupTypeAPI auditGroupTypeAPI;
    private final ListOfStringTypeAPI listOfStringTypeAPI;
    private final SetOfStringTypeAPI setOfStringTypeAPI;
    private final DisplaySetTypeAPI displaySetTypeAPI;

    private final HollowObjectProvider containerProvider;
    private final HollowObjectProvider setOfContainerProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider auditGroupProvider;
    private final HollowObjectProvider listOfStringProvider;
    private final HollowObjectProvider setOfStringProvider;
    private final HollowObjectProvider displaySetProvider;

    public FlexDSAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public FlexDSAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public FlexDSAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public FlexDSAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, FlexDSAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("Container","SetOfContainer","String","AuditGroup","ListOfString","SetOfString","DisplaySet");

        typeDataAccess = dataAccess.getTypeDataAccess("Container");
        if(typeDataAccess != null) {
            containerTypeAPI = new ContainerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            containerTypeAPI = new ContainerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Container"));
        }
        addTypeAPI(containerTypeAPI);
        factory = factoryOverrides.get("Container");
        if(factory == null)
            factory = new ContainerHollowFactory();
        if(cachedTypes.contains("Container")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.containerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.containerProvider;
            containerProvider = new HollowObjectCacheProvider(typeDataAccess, containerTypeAPI, factory, previousCacheProvider);
        } else {
            containerProvider = new HollowObjectFactoryProvider(typeDataAccess, containerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfContainer");
        if(typeDataAccess != null) {
            setOfContainerTypeAPI = new SetOfContainerTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfContainerTypeAPI = new SetOfContainerTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfContainer"));
        }
        addTypeAPI(setOfContainerTypeAPI);
        factory = factoryOverrides.get("SetOfContainer");
        if(factory == null)
            factory = new SetOfContainerHollowFactory();
        if(cachedTypes.contains("SetOfContainer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfContainerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfContainerProvider;
            setOfContainerProvider = new HollowObjectCacheProvider(typeDataAccess, setOfContainerTypeAPI, factory, previousCacheProvider);
        } else {
            setOfContainerProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfContainerTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("AuditGroup");
        if(typeDataAccess != null) {
            auditGroupTypeAPI = new AuditGroupTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            auditGroupTypeAPI = new AuditGroupTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AuditGroup"));
        }
        addTypeAPI(auditGroupTypeAPI);
        factory = factoryOverrides.get("AuditGroup");
        if(factory == null)
            factory = new AuditGroupHollowFactory();
        if(cachedTypes.contains("AuditGroup")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.auditGroupProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.auditGroupProvider;
            auditGroupProvider = new HollowObjectCacheProvider(typeDataAccess, auditGroupTypeAPI, factory, previousCacheProvider);
        } else {
            auditGroupProvider = new HollowObjectFactoryProvider(typeDataAccess, auditGroupTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("DisplaySet");
        if(typeDataAccess != null) {
            displaySetTypeAPI = new DisplaySetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            displaySetTypeAPI = new DisplaySetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DisplaySet"));
        }
        addTypeAPI(displaySetTypeAPI);
        factory = factoryOverrides.get("DisplaySet");
        if(factory == null)
            factory = new DisplaySetHollowFactory();
        if(cachedTypes.contains("DisplaySet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.displaySetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.displaySetProvider;
            displaySetProvider = new HollowObjectCacheProvider(typeDataAccess, displaySetTypeAPI, factory, previousCacheProvider);
        } else {
            displaySetProvider = new HollowObjectFactoryProvider(typeDataAccess, displaySetTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(containerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)containerProvider).detach();
        if(setOfContainerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfContainerProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(auditGroupProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)auditGroupProvider).detach();
        if(listOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfStringProvider).detach();
        if(setOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfStringProvider).detach();
        if(displaySetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)displaySetProvider).detach();
    }

    public ContainerTypeAPI getContainerTypeAPI() {
        return containerTypeAPI;
    }
    public SetOfContainerTypeAPI getSetOfContainerTypeAPI() {
        return setOfContainerTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public AuditGroupTypeAPI getAuditGroupTypeAPI() {
        return auditGroupTypeAPI;
    }
    public ListOfStringTypeAPI getListOfStringTypeAPI() {
        return listOfStringTypeAPI;
    }
    public SetOfStringTypeAPI getSetOfStringTypeAPI() {
        return setOfStringTypeAPI;
    }
    public DisplaySetTypeAPI getDisplaySetTypeAPI() {
        return displaySetTypeAPI;
    }
    public Collection<Container> getAllContainer() {
        return new AllHollowRecordCollection<Container>(getDataAccess().getTypeDataAccess("Container").getTypeState()) {
            protected Container getForOrdinal(int ordinal) {
                return getContainer(ordinal);
            }
        };
    }
    public Container getContainer(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (Container)containerProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfContainer> getAllSetOfContainer() {
        return new AllHollowRecordCollection<SetOfContainer>(getDataAccess().getTypeDataAccess("SetOfContainer").getTypeState()) {
            protected SetOfContainer getForOrdinal(int ordinal) {
                return getSetOfContainer(ordinal);
            }
        };
    }
    public SetOfContainer getSetOfContainer(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (SetOfContainer)setOfContainerProvider.getHollowObject(ordinal);
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
    public Collection<AuditGroup> getAllAuditGroup() {
        return new AllHollowRecordCollection<AuditGroup>(getDataAccess().getTypeDataAccess("AuditGroup").getTypeState()) {
            protected AuditGroup getForOrdinal(int ordinal) {
                return getAuditGroup(ordinal);
            }
        };
    }
    public AuditGroup getAuditGroup(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (AuditGroup)auditGroupProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfString> getAllListOfString() {
        return new AllHollowRecordCollection<ListOfString>(getDataAccess().getTypeDataAccess("ListOfString").getTypeState()) {
            protected ListOfString getForOrdinal(int ordinal) {
                return getListOfString(ordinal);
            }
        };
    }
    public ListOfString getListOfString(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (ListOfString)listOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfString> getAllSetOfString() {
        return new AllHollowRecordCollection<SetOfString>(getDataAccess().getTypeDataAccess("SetOfString").getTypeState()) {
            protected SetOfString getForOrdinal(int ordinal) {
                return getSetOfString(ordinal);
            }
        };
    }
    public SetOfString getSetOfString(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (SetOfString)setOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<DisplaySet> getAllDisplaySet() {
        return new AllHollowRecordCollection<DisplaySet>(getDataAccess().getTypeDataAccess("DisplaySet").getTypeState()) {
            protected DisplaySet getForOrdinal(int ordinal) {
                return getDisplaySet(ordinal);
            }
        };
    }
    public DisplaySet getDisplaySet(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (DisplaySet)displaySetProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
