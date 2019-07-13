package com.netflix.vms.transformer.input.api.gen.personVideo;

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
public class PersonVideoAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final PersonVideoAliasIdTypeAPI personVideoAliasIdTypeAPI;
    private final PersonVideoAliasIdsListTypeAPI personVideoAliasIdsListTypeAPI;
    private final PersonVideoRoleTypeAPI personVideoRoleTypeAPI;
    private final PersonVideoRolesListTypeAPI personVideoRolesListTypeAPI;
    private final PersonVideoTypeAPI personVideoTypeAPI;

    private final HollowObjectProvider personVideoAliasIdProvider;
    private final HollowObjectProvider personVideoAliasIdsListProvider;
    private final HollowObjectProvider personVideoRoleProvider;
    private final HollowObjectProvider personVideoRolesListProvider;
    private final HollowObjectProvider personVideoProvider;

    public PersonVideoAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public PersonVideoAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public PersonVideoAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public PersonVideoAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, PersonVideoAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("PersonVideoAliasId","PersonVideoAliasIdsList","PersonVideoRole","PersonVideoRolesList","PersonVideo");

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoAliasId");
        if(typeDataAccess != null) {
            personVideoAliasIdTypeAPI = new PersonVideoAliasIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personVideoAliasIdTypeAPI = new PersonVideoAliasIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonVideoAliasId"));
        }
        addTypeAPI(personVideoAliasIdTypeAPI);
        factory = factoryOverrides.get("PersonVideoAliasId");
        if(factory == null)
            factory = new PersonVideoAliasIdHollowFactory();
        if(cachedTypes.contains("PersonVideoAliasId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoAliasIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoAliasIdProvider;
            personVideoAliasIdProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoAliasIdTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoAliasIdProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoAliasIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoAliasIdsList");
        if(typeDataAccess != null) {
            personVideoAliasIdsListTypeAPI = new PersonVideoAliasIdsListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personVideoAliasIdsListTypeAPI = new PersonVideoAliasIdsListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonVideoAliasIdsList"));
        }
        addTypeAPI(personVideoAliasIdsListTypeAPI);
        factory = factoryOverrides.get("PersonVideoAliasIdsList");
        if(factory == null)
            factory = new PersonVideoAliasIdsListHollowFactory();
        if(cachedTypes.contains("PersonVideoAliasIdsList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoAliasIdsListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoAliasIdsListProvider;
            personVideoAliasIdsListProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoAliasIdsListTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoAliasIdsListProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoAliasIdsListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoRole");
        if(typeDataAccess != null) {
            personVideoRoleTypeAPI = new PersonVideoRoleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personVideoRoleTypeAPI = new PersonVideoRoleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonVideoRole"));
        }
        addTypeAPI(personVideoRoleTypeAPI);
        factory = factoryOverrides.get("PersonVideoRole");
        if(factory == null)
            factory = new PersonVideoRoleHollowFactory();
        if(cachedTypes.contains("PersonVideoRole")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoRoleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoRoleProvider;
            personVideoRoleProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoRoleTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoRoleProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoRoleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideoRolesList");
        if(typeDataAccess != null) {
            personVideoRolesListTypeAPI = new PersonVideoRolesListTypeAPI(this, (HollowListTypeDataAccess)typeDataAccess);
        } else {
            personVideoRolesListTypeAPI = new PersonVideoRolesListTypeAPI(this, new HollowListMissingDataAccess(dataAccess, "PersonVideoRolesList"));
        }
        addTypeAPI(personVideoRolesListTypeAPI);
        factory = factoryOverrides.get("PersonVideoRolesList");
        if(factory == null)
            factory = new PersonVideoRolesListHollowFactory();
        if(cachedTypes.contains("PersonVideoRolesList")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoRolesListProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoRolesListProvider;
            personVideoRolesListProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoRolesListTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoRolesListProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoRolesListTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonVideo");
        if(typeDataAccess != null) {
            personVideoTypeAPI = new PersonVideoTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personVideoTypeAPI = new PersonVideoTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonVideo"));
        }
        addTypeAPI(personVideoTypeAPI);
        factory = factoryOverrides.get("PersonVideo");
        if(factory == null)
            factory = new PersonVideoHollowFactory();
        if(cachedTypes.contains("PersonVideo")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personVideoProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personVideoProvider;
            personVideoProvider = new HollowObjectCacheProvider(typeDataAccess, personVideoTypeAPI, factory, previousCacheProvider);
        } else {
            personVideoProvider = new HollowObjectFactoryProvider(typeDataAccess, personVideoTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(personVideoAliasIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoAliasIdProvider).detach();
        if(personVideoAliasIdsListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoAliasIdsListProvider).detach();
        if(personVideoRoleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoRoleProvider).detach();
        if(personVideoRolesListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoRolesListProvider).detach();
        if(personVideoProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personVideoProvider).detach();
    }

    public PersonVideoAliasIdTypeAPI getPersonVideoAliasIdTypeAPI() {
        return personVideoAliasIdTypeAPI;
    }
    public PersonVideoAliasIdsListTypeAPI getPersonVideoAliasIdsListTypeAPI() {
        return personVideoAliasIdsListTypeAPI;
    }
    public PersonVideoRoleTypeAPI getPersonVideoRoleTypeAPI() {
        return personVideoRoleTypeAPI;
    }
    public PersonVideoRolesListTypeAPI getPersonVideoRolesListTypeAPI() {
        return personVideoRolesListTypeAPI;
    }
    public PersonVideoTypeAPI getPersonVideoTypeAPI() {
        return personVideoTypeAPI;
    }
    public Collection<PersonVideoAliasId> getAllPersonVideoAliasId() {
        return new AllHollowRecordCollection<PersonVideoAliasId>(getDataAccess().getTypeDataAccess("PersonVideoAliasId").getTypeState()) {
            protected PersonVideoAliasId getForOrdinal(int ordinal) {
                return getPersonVideoAliasId(ordinal);
            }
        };
    }
    public PersonVideoAliasId getPersonVideoAliasId(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (PersonVideoAliasId)personVideoAliasIdProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoAliasIdsList> getAllPersonVideoAliasIdsList() {
        return new AllHollowRecordCollection<PersonVideoAliasIdsList>(getDataAccess().getTypeDataAccess("PersonVideoAliasIdsList").getTypeState()) {
            protected PersonVideoAliasIdsList getForOrdinal(int ordinal) {
                return getPersonVideoAliasIdsList(ordinal);
            }
        };
    }
    public PersonVideoAliasIdsList getPersonVideoAliasIdsList(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (PersonVideoAliasIdsList)personVideoAliasIdsListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoRole> getAllPersonVideoRole() {
        return new AllHollowRecordCollection<PersonVideoRole>(getDataAccess().getTypeDataAccess("PersonVideoRole").getTypeState()) {
            protected PersonVideoRole getForOrdinal(int ordinal) {
                return getPersonVideoRole(ordinal);
            }
        };
    }
    public PersonVideoRole getPersonVideoRole(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (PersonVideoRole)personVideoRoleProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideoRolesList> getAllPersonVideoRolesList() {
        return new AllHollowRecordCollection<PersonVideoRolesList>(getDataAccess().getTypeDataAccess("PersonVideoRolesList").getTypeState()) {
            protected PersonVideoRolesList getForOrdinal(int ordinal) {
                return getPersonVideoRolesList(ordinal);
            }
        };
    }
    public PersonVideoRolesList getPersonVideoRolesList(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (PersonVideoRolesList)personVideoRolesListProvider.getHollowObject(ordinal);
    }
    public Collection<PersonVideo> getAllPersonVideo() {
        return new AllHollowRecordCollection<PersonVideo>(getDataAccess().getTypeDataAccess("PersonVideo").getTypeState()) {
            protected PersonVideo getForOrdinal(int ordinal) {
                return getPersonVideo(ordinal);
            }
        };
    }
    public PersonVideo getPersonVideo(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (PersonVideo)personVideoProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
