package com.netflix.vms.transformer.input.api.gen.supplemental;

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
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class SupplementalAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final StringTypeAPI stringTypeAPI;
    private final IndividualSupplementalIdentifierSetTypeAPI individualSupplementalIdentifierSetTypeAPI;
    private final IndividualSupplementalThemeSetTypeAPI individualSupplementalThemeSetTypeAPI;
    private final IndividualSupplementalUsageSetTypeAPI individualSupplementalUsageSetTypeAPI;
    private final IndividualSupplementalTypeAPI individualSupplementalTypeAPI;
    private final SupplementalsListTypeAPI supplementalsListTypeAPI;
    private final SupplementalsTypeAPI supplementalsTypeAPI;

    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider individualSupplementalIdentifierSetProvider;
    private final HollowObjectProvider individualSupplementalThemeSetProvider;
    private final HollowObjectProvider individualSupplementalUsageSetProvider;
    private final HollowObjectProvider individualSupplementalProvider;
    private final HollowObjectProvider supplementalsListProvider;
    private final HollowObjectProvider supplementalsProvider;

    public SupplementalAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public SupplementalAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public SupplementalAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public SupplementalAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, SupplementalAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("String","IndividualSupplementalIdentifierSet","IndividualSupplementalThemeSet","IndividualSupplementalUsageSet","IndividualSupplemental","SupplementalsList","Supplementals");

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

        typeDataAccess = dataAccess.getTypeDataAccess("IndividualSupplementalIdentifierSet");
        if(typeDataAccess != null) {
            individualSupplementalIdentifierSetTypeAPI = new IndividualSupplementalIdentifierSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            individualSupplementalIdentifierSetTypeAPI = new IndividualSupplementalIdentifierSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IndividualSupplementalIdentifierSet"));
        }
        addTypeAPI(individualSupplementalIdentifierSetTypeAPI);
        factory = factoryOverrides.get("IndividualSupplementalIdentifierSet");
        if(factory == null)
            factory = new IndividualSupplementalIdentifierSetHollowFactory();
        if(cachedTypes.contains("IndividualSupplementalIdentifierSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.individualSupplementalIdentifierSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.individualSupplementalIdentifierSetProvider;
            individualSupplementalIdentifierSetProvider = new HollowObjectCacheProvider(typeDataAccess, individualSupplementalIdentifierSetTypeAPI, factory, previousCacheProvider);
        } else {
            individualSupplementalIdentifierSetProvider = new HollowObjectFactoryProvider(typeDataAccess, individualSupplementalIdentifierSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IndividualSupplementalThemeSet");
        if(typeDataAccess != null) {
            individualSupplementalThemeSetTypeAPI = new IndividualSupplementalThemeSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            individualSupplementalThemeSetTypeAPI = new IndividualSupplementalThemeSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IndividualSupplementalThemeSet"));
        }
        addTypeAPI(individualSupplementalThemeSetTypeAPI);
        factory = factoryOverrides.get("IndividualSupplementalThemeSet");
        if(factory == null)
            factory = new IndividualSupplementalThemeSetHollowFactory();
        if(cachedTypes.contains("IndividualSupplementalThemeSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.individualSupplementalThemeSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.individualSupplementalThemeSetProvider;
            individualSupplementalThemeSetProvider = new HollowObjectCacheProvider(typeDataAccess, individualSupplementalThemeSetTypeAPI, factory, previousCacheProvider);
        } else {
            individualSupplementalThemeSetProvider = new HollowObjectFactoryProvider(typeDataAccess, individualSupplementalThemeSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IndividualSupplementalUsageSet");
        if(typeDataAccess != null) {
            individualSupplementalUsageSetTypeAPI = new IndividualSupplementalUsageSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            individualSupplementalUsageSetTypeAPI = new IndividualSupplementalUsageSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IndividualSupplementalUsageSet"));
        }
        addTypeAPI(individualSupplementalUsageSetTypeAPI);
        factory = factoryOverrides.get("IndividualSupplementalUsageSet");
        if(factory == null)
            factory = new IndividualSupplementalUsageSetHollowFactory();
        if(cachedTypes.contains("IndividualSupplementalUsageSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.individualSupplementalUsageSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.individualSupplementalUsageSetProvider;
            individualSupplementalUsageSetProvider = new HollowObjectCacheProvider(typeDataAccess, individualSupplementalUsageSetTypeAPI, factory, previousCacheProvider);
        } else {
            individualSupplementalUsageSetProvider = new HollowObjectFactoryProvider(typeDataAccess, individualSupplementalUsageSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IndividualSupplemental");
        if(typeDataAccess != null) {
            individualSupplementalTypeAPI = new IndividualSupplementalTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            individualSupplementalTypeAPI = new IndividualSupplementalTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IndividualSupplemental"));
        }
        addTypeAPI(individualSupplementalTypeAPI);
        factory = factoryOverrides.get("IndividualSupplemental");
        if(factory == null)
            factory = new IndividualSupplementalHollowFactory();
        if(cachedTypes.contains("IndividualSupplemental")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.individualSupplementalProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.individualSupplementalProvider;
            individualSupplementalProvider = new HollowObjectCacheProvider(typeDataAccess, individualSupplementalTypeAPI, factory, previousCacheProvider);
        } else {
            individualSupplementalProvider = new HollowObjectFactoryProvider(typeDataAccess, individualSupplementalTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SupplementalsList");
        if(typeDataAccess != null) {
            supplementalsListTypeAPI = new SupplementalsListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            supplementalsListTypeAPI = new SupplementalsListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "SupplementalsList"));
        }
        addTypeAPI(supplementalsListTypeAPI);
        factory = factoryOverrides.get("SupplementalsList");
        if(factory == null)
            factory = new SupplementalsListHollowFactory();
        if(cachedTypes.contains("SupplementalsList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.supplementalsListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.supplementalsListProvider;
            supplementalsListProvider = new HollowObjectCacheProvider(typeDataAccess, supplementalsListTypeAPI, factory, previousCacheProvider);
        } else {
            supplementalsListProvider = new HollowObjectFactoryProvider(typeDataAccess, supplementalsListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Supplementals");
        if(typeDataAccess != null) {
            supplementalsTypeAPI = new SupplementalsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            supplementalsTypeAPI = new SupplementalsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Supplementals"));
        }
        addTypeAPI(supplementalsTypeAPI);
        factory = factoryOverrides.get("Supplementals");
        if(factory == null)
            factory = new SupplementalsHollowFactory();
        if(cachedTypes.contains("Supplementals")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.supplementalsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.supplementalsProvider;
            supplementalsProvider = new HollowObjectCacheProvider(typeDataAccess, supplementalsTypeAPI, factory, previousCacheProvider);
        } else {
            supplementalsProvider = new HollowObjectFactoryProvider(typeDataAccess, supplementalsTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(individualSupplementalIdentifierSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)individualSupplementalIdentifierSetProvider).detach();
        if(individualSupplementalThemeSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)individualSupplementalThemeSetProvider).detach();
        if(individualSupplementalUsageSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)individualSupplementalUsageSetProvider).detach();
        if(individualSupplementalProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)individualSupplementalProvider).detach();
        if(supplementalsListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)supplementalsListProvider).detach();
        if(supplementalsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)supplementalsProvider).detach();
    }

    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public IndividualSupplementalIdentifierSetTypeAPI getIndividualSupplementalIdentifierSetTypeAPI() {
        return individualSupplementalIdentifierSetTypeAPI;
    }
    public IndividualSupplementalThemeSetTypeAPI getIndividualSupplementalThemeSetTypeAPI() {
        return individualSupplementalThemeSetTypeAPI;
    }
    public IndividualSupplementalUsageSetTypeAPI getIndividualSupplementalUsageSetTypeAPI() {
        return individualSupplementalUsageSetTypeAPI;
    }
    public IndividualSupplementalTypeAPI getIndividualSupplementalTypeAPI() {
        return individualSupplementalTypeAPI;
    }
    public SupplementalsListTypeAPI getSupplementalsListTypeAPI() {
        return supplementalsListTypeAPI;
    }
    public SupplementalsTypeAPI getSupplementalsTypeAPI() {
        return supplementalsTypeAPI;
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<IndividualSupplementalIdentifierSet> getAllIndividualSupplementalIdentifierSet() {
        return new AllHollowRecordCollection<IndividualSupplementalIdentifierSet>(getDataAccess().getTypeDataAccess("IndividualSupplementalIdentifierSet").getTypeState()) {
            protected IndividualSupplementalIdentifierSet getForOrdinal(int ordinal) {
                return getIndividualSupplementalIdentifierSet(ordinal);
            }
        };
    }
    public IndividualSupplementalIdentifierSet getIndividualSupplementalIdentifierSet(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (IndividualSupplementalIdentifierSet)individualSupplementalIdentifierSetProvider.getHollowObject(ordinal);
    }
    public Collection<IndividualSupplementalThemeSet> getAllIndividualSupplementalThemeSet() {
        return new AllHollowRecordCollection<IndividualSupplementalThemeSet>(getDataAccess().getTypeDataAccess("IndividualSupplementalThemeSet").getTypeState()) {
            protected IndividualSupplementalThemeSet getForOrdinal(int ordinal) {
                return getIndividualSupplementalThemeSet(ordinal);
            }
        };
    }
    public IndividualSupplementalThemeSet getIndividualSupplementalThemeSet(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (IndividualSupplementalThemeSet)individualSupplementalThemeSetProvider.getHollowObject(ordinal);
    }
    public Collection<IndividualSupplementalUsageSet> getAllIndividualSupplementalUsageSet() {
        return new AllHollowRecordCollection<IndividualSupplementalUsageSet>(getDataAccess().getTypeDataAccess("IndividualSupplementalUsageSet").getTypeState()) {
            protected IndividualSupplementalUsageSet getForOrdinal(int ordinal) {
                return getIndividualSupplementalUsageSet(ordinal);
            }
        };
    }
    public IndividualSupplementalUsageSet getIndividualSupplementalUsageSet(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (IndividualSupplementalUsageSet)individualSupplementalUsageSetProvider.getHollowObject(ordinal);
    }
    public Collection<IndividualSupplemental> getAllIndividualSupplemental() {
        return new AllHollowRecordCollection<IndividualSupplemental>(getDataAccess().getTypeDataAccess("IndividualSupplemental").getTypeState()) {
            protected IndividualSupplemental getForOrdinal(int ordinal) {
                return getIndividualSupplemental(ordinal);
            }
        };
    }
    public IndividualSupplemental getIndividualSupplemental(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (IndividualSupplemental)individualSupplementalProvider.getHollowObject(ordinal);
    }
    public Collection<SupplementalsList> getAllSupplementalsList() {
        return new AllHollowRecordCollection<SupplementalsList>(getDataAccess().getTypeDataAccess("SupplementalsList").getTypeState()) {
            protected SupplementalsList getForOrdinal(int ordinal) {
                return getSupplementalsList(ordinal);
            }
        };
    }
    public SupplementalsList getSupplementalsList(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (SupplementalsList)supplementalsListProvider.getHollowObject(ordinal);
    }
    public Collection<Supplementals> getAllSupplementals() {
        return new AllHollowRecordCollection<Supplementals>(getDataAccess().getTypeDataAccess("Supplementals").getTypeState()) {
            protected Supplementals getForOrdinal(int ordinal) {
                return getSupplementals(ordinal);
            }
        };
    }
    public Supplementals getSupplementals(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (Supplementals)supplementalsProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
