package com.netflix.vms.transformer.input.api.gen.mceImage;

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
public class MceImageV3API extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final DerivativeTagTypeAPI derivativeTagTypeAPI;
    private final ListOfDerivativeTagTypeAPI listOfDerivativeTagTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final IPLArtworkDerivativeTypeAPI iPLArtworkDerivativeTypeAPI;
    private final IPLDerivativeSetTypeAPI iPLDerivativeSetTypeAPI;
    private final IPLDerivativeGroupTypeAPI iPLDerivativeGroupTypeAPI;
    private final IPLDerivativeGroupSetTypeAPI iPLDerivativeGroupSetTypeAPI;
    private final IPLArtworkDerivativeSetTypeAPI iPLArtworkDerivativeSetTypeAPI;

    private final HollowObjectProvider derivativeTagProvider;
    private final HollowObjectProvider listOfDerivativeTagProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider iPLArtworkDerivativeProvider;
    private final HollowObjectProvider iPLDerivativeSetProvider;
    private final HollowObjectProvider iPLDerivativeGroupProvider;
    private final HollowObjectProvider iPLDerivativeGroupSetProvider;
    private final HollowObjectProvider iPLArtworkDerivativeSetProvider;

    public MceImageV3API(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public MceImageV3API(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public MceImageV3API(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public MceImageV3API(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, MceImageV3API previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("DerivativeTag","ListOfDerivativeTag","String","IPLArtworkDerivative","IPLDerivativeSet","IPLDerivativeGroup","IPLDerivativeGroupSet","IPLArtworkDerivativeSet");

        typeDataAccess = dataAccess.getTypeDataAccess("DerivativeTag");
        if(typeDataAccess != null) {
            derivativeTagTypeAPI = new DerivativeTagTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            derivativeTagTypeAPI = new DerivativeTagTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DerivativeTag"));
        }
        addTypeAPI(derivativeTagTypeAPI);
        factory = factoryOverrides.get("DerivativeTag");
        if(factory == null)
            factory = new DerivativeTagHollowFactory();
        if(cachedTypes.contains("DerivativeTag")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.derivativeTagProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.derivativeTagProvider;
            derivativeTagProvider = new HollowObjectCacheProvider(typeDataAccess, derivativeTagTypeAPI, factory, previousCacheProvider);
        } else {
            derivativeTagProvider = new HollowObjectFactoryProvider(typeDataAccess, derivativeTagTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ListOfDerivativeTag");
        if(typeDataAccess != null) {
            listOfDerivativeTagTypeAPI = new ListOfDerivativeTagTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            listOfDerivativeTagTypeAPI = new ListOfDerivativeTagTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ListOfDerivativeTag"));
        }
        addTypeAPI(listOfDerivativeTagTypeAPI);
        factory = factoryOverrides.get("ListOfDerivativeTag");
        if(factory == null)
            factory = new ListOfDerivativeTagHollowFactory();
        if(cachedTypes.contains("ListOfDerivativeTag")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.listOfDerivativeTagProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.listOfDerivativeTagProvider;
            listOfDerivativeTagProvider = new HollowObjectCacheProvider(typeDataAccess, listOfDerivativeTagTypeAPI, factory, previousCacheProvider);
        } else {
            listOfDerivativeTagProvider = new HollowObjectFactoryProvider(typeDataAccess, listOfDerivativeTagTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("IPLArtworkDerivative");
        if(typeDataAccess != null) {
            iPLArtworkDerivativeTypeAPI = new IPLArtworkDerivativeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iPLArtworkDerivativeTypeAPI = new IPLArtworkDerivativeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IPLArtworkDerivative"));
        }
        addTypeAPI(iPLArtworkDerivativeTypeAPI);
        factory = factoryOverrides.get("IPLArtworkDerivative");
        if(factory == null)
            factory = new IPLArtworkDerivativeHollowFactory();
        if(cachedTypes.contains("IPLArtworkDerivative")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLArtworkDerivativeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLArtworkDerivativeProvider;
            iPLArtworkDerivativeProvider = new HollowObjectCacheProvider(typeDataAccess, iPLArtworkDerivativeTypeAPI, factory, previousCacheProvider);
        } else {
            iPLArtworkDerivativeProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLArtworkDerivativeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLDerivativeSet");
        if(typeDataAccess != null) {
            iPLDerivativeSetTypeAPI = new IPLDerivativeSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            iPLDerivativeSetTypeAPI = new IPLDerivativeSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IPLDerivativeSet"));
        }
        addTypeAPI(iPLDerivativeSetTypeAPI);
        factory = factoryOverrides.get("IPLDerivativeSet");
        if(factory == null)
            factory = new IPLDerivativeSetHollowFactory();
        if(cachedTypes.contains("IPLDerivativeSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLDerivativeSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLDerivativeSetProvider;
            iPLDerivativeSetProvider = new HollowObjectCacheProvider(typeDataAccess, iPLDerivativeSetTypeAPI, factory, previousCacheProvider);
        } else {
            iPLDerivativeSetProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLDerivativeSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLDerivativeGroup");
        if(typeDataAccess != null) {
            iPLDerivativeGroupTypeAPI = new IPLDerivativeGroupTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iPLDerivativeGroupTypeAPI = new IPLDerivativeGroupTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IPLDerivativeGroup"));
        }
        addTypeAPI(iPLDerivativeGroupTypeAPI);
        factory = factoryOverrides.get("IPLDerivativeGroup");
        if(factory == null)
            factory = new IPLDerivativeGroupHollowFactory();
        if(cachedTypes.contains("IPLDerivativeGroup")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLDerivativeGroupProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLDerivativeGroupProvider;
            iPLDerivativeGroupProvider = new HollowObjectCacheProvider(typeDataAccess, iPLDerivativeGroupTypeAPI, factory, previousCacheProvider);
        } else {
            iPLDerivativeGroupProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLDerivativeGroupTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLDerivativeGroupSet");
        if(typeDataAccess != null) {
            iPLDerivativeGroupSetTypeAPI = new IPLDerivativeGroupSetTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            iPLDerivativeGroupSetTypeAPI = new IPLDerivativeGroupSetTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "IPLDerivativeGroupSet"));
        }
        addTypeAPI(iPLDerivativeGroupSetTypeAPI);
        factory = factoryOverrides.get("IPLDerivativeGroupSet");
        if(factory == null)
            factory = new IPLDerivativeGroupSetHollowFactory();
        if(cachedTypes.contains("IPLDerivativeGroupSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLDerivativeGroupSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLDerivativeGroupSetProvider;
            iPLDerivativeGroupSetProvider = new HollowObjectCacheProvider(typeDataAccess, iPLDerivativeGroupSetTypeAPI, factory, previousCacheProvider);
        } else {
            iPLDerivativeGroupSetProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLDerivativeGroupSetTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("IPLArtworkDerivativeSet");
        if(typeDataAccess != null) {
            iPLArtworkDerivativeSetTypeAPI = new IPLArtworkDerivativeSetTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            iPLArtworkDerivativeSetTypeAPI = new IPLArtworkDerivativeSetTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "IPLArtworkDerivativeSet"));
        }
        addTypeAPI(iPLArtworkDerivativeSetTypeAPI);
        factory = factoryOverrides.get("IPLArtworkDerivativeSet");
        if(factory == null)
            factory = new IPLArtworkDerivativeSetHollowFactory();
        if(cachedTypes.contains("IPLArtworkDerivativeSet")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iPLArtworkDerivativeSetProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iPLArtworkDerivativeSetProvider;
            iPLArtworkDerivativeSetProvider = new HollowObjectCacheProvider(typeDataAccess, iPLArtworkDerivativeSetTypeAPI, factory, previousCacheProvider);
        } else {
            iPLArtworkDerivativeSetProvider = new HollowObjectFactoryProvider(typeDataAccess, iPLArtworkDerivativeSetTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(derivativeTagProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)derivativeTagProvider).detach();
        if(listOfDerivativeTagProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)listOfDerivativeTagProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(iPLArtworkDerivativeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLArtworkDerivativeProvider).detach();
        if(iPLDerivativeSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLDerivativeSetProvider).detach();
        if(iPLDerivativeGroupProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLDerivativeGroupProvider).detach();
        if(iPLDerivativeGroupSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLDerivativeGroupSetProvider).detach();
        if(iPLArtworkDerivativeSetProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iPLArtworkDerivativeSetProvider).detach();
    }

    public DerivativeTagTypeAPI getDerivativeTagTypeAPI() {
        return derivativeTagTypeAPI;
    }
    public ListOfDerivativeTagTypeAPI getListOfDerivativeTagTypeAPI() {
        return listOfDerivativeTagTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public IPLArtworkDerivativeTypeAPI getIPLArtworkDerivativeTypeAPI() {
        return iPLArtworkDerivativeTypeAPI;
    }
    public IPLDerivativeSetTypeAPI getIPLDerivativeSetTypeAPI() {
        return iPLDerivativeSetTypeAPI;
    }
    public IPLDerivativeGroupTypeAPI getIPLDerivativeGroupTypeAPI() {
        return iPLDerivativeGroupTypeAPI;
    }
    public IPLDerivativeGroupSetTypeAPI getIPLDerivativeGroupSetTypeAPI() {
        return iPLDerivativeGroupSetTypeAPI;
    }
    public IPLArtworkDerivativeSetTypeAPI getIPLArtworkDerivativeSetTypeAPI() {
        return iPLArtworkDerivativeSetTypeAPI;
    }
    public Collection<DerivativeTag> getAllDerivativeTag() {
        return new AllHollowRecordCollection<DerivativeTag>(getDataAccess().getTypeDataAccess("DerivativeTag").getTypeState()) {
            protected DerivativeTag getForOrdinal(int ordinal) {
                return getDerivativeTag(ordinal);
            }
        };
    }
    public DerivativeTag getDerivativeTag(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (DerivativeTag)derivativeTagProvider.getHollowObject(ordinal);
    }
    public Collection<ListOfDerivativeTag> getAllListOfDerivativeTag() {
        return new AllHollowRecordCollection<ListOfDerivativeTag>(getDataAccess().getTypeDataAccess("ListOfDerivativeTag").getTypeState()) {
            protected ListOfDerivativeTag getForOrdinal(int ordinal) {
                return getListOfDerivativeTag(ordinal);
            }
        };
    }
    public ListOfDerivativeTag getListOfDerivativeTag(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (ListOfDerivativeTag)listOfDerivativeTagProvider.getHollowObject(ordinal);
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
    public Collection<IPLArtworkDerivative> getAllIPLArtworkDerivative() {
        return new AllHollowRecordCollection<IPLArtworkDerivative>(getDataAccess().getTypeDataAccess("IPLArtworkDerivative").getTypeState()) {
            protected IPLArtworkDerivative getForOrdinal(int ordinal) {
                return getIPLArtworkDerivative(ordinal);
            }
        };
    }
    public IPLArtworkDerivative getIPLArtworkDerivative(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (IPLArtworkDerivative)iPLArtworkDerivativeProvider.getHollowObject(ordinal);
    }
    public Collection<IPLDerivativeSet> getAllIPLDerivativeSet() {
        return new AllHollowRecordCollection<IPLDerivativeSet>(getDataAccess().getTypeDataAccess("IPLDerivativeSet").getTypeState()) {
            protected IPLDerivativeSet getForOrdinal(int ordinal) {
                return getIPLDerivativeSet(ordinal);
            }
        };
    }
    public IPLDerivativeSet getIPLDerivativeSet(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (IPLDerivativeSet)iPLDerivativeSetProvider.getHollowObject(ordinal);
    }
    public Collection<IPLDerivativeGroup> getAllIPLDerivativeGroup() {
        return new AllHollowRecordCollection<IPLDerivativeGroup>(getDataAccess().getTypeDataAccess("IPLDerivativeGroup").getTypeState()) {
            protected IPLDerivativeGroup getForOrdinal(int ordinal) {
                return getIPLDerivativeGroup(ordinal);
            }
        };
    }
    public IPLDerivativeGroup getIPLDerivativeGroup(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (IPLDerivativeGroup)iPLDerivativeGroupProvider.getHollowObject(ordinal);
    }
    public Collection<IPLDerivativeGroupSet> getAllIPLDerivativeGroupSet() {
        return new AllHollowRecordCollection<IPLDerivativeGroupSet>(getDataAccess().getTypeDataAccess("IPLDerivativeGroupSet").getTypeState()) {
            protected IPLDerivativeGroupSet getForOrdinal(int ordinal) {
                return getIPLDerivativeGroupSet(ordinal);
            }
        };
    }
    public IPLDerivativeGroupSet getIPLDerivativeGroupSet(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (IPLDerivativeGroupSet)iPLDerivativeGroupSetProvider.getHollowObject(ordinal);
    }
    public Collection<IPLArtworkDerivativeSet> getAllIPLArtworkDerivativeSet() {
        return new AllHollowRecordCollection<IPLArtworkDerivativeSet>(getDataAccess().getTypeDataAccess("IPLArtworkDerivativeSet").getTypeState()) {
            protected IPLArtworkDerivativeSet getForOrdinal(int ordinal) {
                return getIPLArtworkDerivativeSet(ordinal);
            }
        };
    }
    public IPLArtworkDerivativeSet getIPLArtworkDerivativeSet(int ordinal) {
        objectCreationSampler.recordCreation(7);
        return (IPLArtworkDerivativeSet)iPLArtworkDerivativeSetProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
