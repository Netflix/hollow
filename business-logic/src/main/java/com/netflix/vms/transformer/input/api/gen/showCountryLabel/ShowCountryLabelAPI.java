package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

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
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class ShowCountryLabelAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final ISOCountryTypeAPI iSOCountryTypeAPI;
    private final ISOCountryListTypeAPI iSOCountryListTypeAPI;
    private final ShowMemberTypeTypeAPI showMemberTypeTypeAPI;
    private final ShowMemberTypeListTypeAPI showMemberTypeListTypeAPI;
    private final ShowCountryLabelTypeAPI showCountryLabelTypeAPI;

    private final HollowObjectProvider iSOCountryProvider;
    private final HollowObjectProvider iSOCountryListProvider;
    private final HollowObjectProvider showMemberTypeProvider;
    private final HollowObjectProvider showMemberTypeListProvider;
    private final HollowObjectProvider showCountryLabelProvider;

    public ShowCountryLabelAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public ShowCountryLabelAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public ShowCountryLabelAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public ShowCountryLabelAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, ShowCountryLabelAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("ISOCountry","ISOCountryList","ShowMemberType","ShowMemberTypeList","ShowCountryLabel");

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

        typeDataAccess = dataAccess.getTypeDataAccess("ISOCountryList");
        if(typeDataAccess != null) {
            iSOCountryListTypeAPI = new ISOCountryListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            iSOCountryListTypeAPI = new ISOCountryListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ISOCountryList"));
        }
        addTypeAPI(iSOCountryListTypeAPI);
        factory = factoryOverrides.get("ISOCountryList");
        if(factory == null)
            factory = new ISOCountryListHollowFactory();
        if(cachedTypes.contains("ISOCountryList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.iSOCountryListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.iSOCountryListProvider;
            iSOCountryListProvider = new HollowObjectCacheProvider(typeDataAccess, iSOCountryListTypeAPI, factory, previousCacheProvider);
        } else {
            iSOCountryListProvider = new HollowObjectFactoryProvider(typeDataAccess, iSOCountryListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberType");
        if(typeDataAccess != null) {
            showMemberTypeTypeAPI = new ShowMemberTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypeTypeAPI = new ShowMemberTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowMemberType"));
        }
        addTypeAPI(showMemberTypeTypeAPI);
        factory = factoryOverrides.get("ShowMemberType");
        if(factory == null)
            factory = new ShowMemberTypeHollowFactory();
        if(cachedTypes.contains("ShowMemberType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypeProvider;
            showMemberTypeProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypeTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowMemberTypeList");
        if(typeDataAccess != null) {
            showMemberTypeListTypeAPI = new ShowMemberTypeListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            showMemberTypeListTypeAPI = new ShowMemberTypeListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "ShowMemberTypeList"));
        }
        addTypeAPI(showMemberTypeListTypeAPI);
        factory = factoryOverrides.get("ShowMemberTypeList");
        if(factory == null)
            factory = new ShowMemberTypeListHollowFactory();
        if(cachedTypes.contains("ShowMemberTypeList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showMemberTypeListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showMemberTypeListProvider;
            showMemberTypeListProvider = new HollowObjectCacheProvider(typeDataAccess, showMemberTypeListTypeAPI, factory, previousCacheProvider);
        } else {
            showMemberTypeListProvider = new HollowObjectFactoryProvider(typeDataAccess, showMemberTypeListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowCountryLabel");
        if(typeDataAccess != null) {
            showCountryLabelTypeAPI = new ShowCountryLabelTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showCountryLabelTypeAPI = new ShowCountryLabelTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowCountryLabel"));
        }
        addTypeAPI(showCountryLabelTypeAPI);
        factory = factoryOverrides.get("ShowCountryLabel");
        if(factory == null)
            factory = new ShowCountryLabelHollowFactory();
        if(cachedTypes.contains("ShowCountryLabel")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showCountryLabelProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showCountryLabelProvider;
            showCountryLabelProvider = new HollowObjectCacheProvider(typeDataAccess, showCountryLabelTypeAPI, factory, previousCacheProvider);
        } else {
            showCountryLabelProvider = new HollowObjectFactoryProvider(typeDataAccess, showCountryLabelTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(iSOCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryProvider).detach();
        if(iSOCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryListProvider).detach();
        if(showMemberTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypeProvider).detach();
        if(showMemberTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypeListProvider).detach();
        if(showCountryLabelProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showCountryLabelProvider).detach();
    }

    public ISOCountryTypeAPI getISOCountryTypeAPI() {
        return iSOCountryTypeAPI;
    }
    public ISOCountryListTypeAPI getISOCountryListTypeAPI() {
        return iSOCountryListTypeAPI;
    }
    public ShowMemberTypeTypeAPI getShowMemberTypeTypeAPI() {
        return showMemberTypeTypeAPI;
    }
    public ShowMemberTypeListTypeAPI getShowMemberTypeListTypeAPI() {
        return showMemberTypeListTypeAPI;
    }
    public ShowCountryLabelTypeAPI getShowCountryLabelTypeAPI() {
        return showCountryLabelTypeAPI;
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
    public Collection<ISOCountryList> getAllISOCountryList() {
        return new AllHollowRecordCollection<ISOCountryList>(getDataAccess().getTypeDataAccess("ISOCountryList").getTypeState()) {
            protected ISOCountryList getForOrdinal(int ordinal) {
                return getISOCountryList(ordinal);
            }
        };
    }
    public ISOCountryList getISOCountryList(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (ISOCountryList)iSOCountryListProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberType> getAllShowMemberType() {
        return new AllHollowRecordCollection<ShowMemberType>(getDataAccess().getTypeDataAccess("ShowMemberType").getTypeState()) {
            protected ShowMemberType getForOrdinal(int ordinal) {
                return getShowMemberType(ordinal);
            }
        };
    }
    public ShowMemberType getShowMemberType(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (ShowMemberType)showMemberTypeProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberTypeList> getAllShowMemberTypeList() {
        return new AllHollowRecordCollection<ShowMemberTypeList>(getDataAccess().getTypeDataAccess("ShowMemberTypeList").getTypeState()) {
            protected ShowMemberTypeList getForOrdinal(int ordinal) {
                return getShowMemberTypeList(ordinal);
            }
        };
    }
    public ShowMemberTypeList getShowMemberTypeList(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (ShowMemberTypeList)showMemberTypeListProvider.getHollowObject(ordinal);
    }
    public Collection<ShowCountryLabel> getAllShowCountryLabel() {
        return new AllHollowRecordCollection<ShowCountryLabel>(getDataAccess().getTypeDataAccess("ShowCountryLabel").getTypeState()) {
            protected ShowCountryLabel getForOrdinal(int ordinal) {
                return getShowCountryLabel(ordinal);
            }
        };
    }
    public ShowCountryLabel getShowCountryLabel(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (ShowCountryLabel)showCountryLabelProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
