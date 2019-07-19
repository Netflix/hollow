package com.netflix.vms.transformer.input.api.gen.oscar;

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
public class OscarAPI extends HollowAPI  {

    private final HollowObjectCreationSampler objectCreationSampler;

    private final AttributeNameTypeAPI attributeNameTypeAPI;
    private final AttributeValueTypeAPI attributeValueTypeAPI;
    private final BcpCodeTypeAPI bcpCodeTypeAPI;
    private final CountryStringTypeAPI countryStringTypeAPI;
    private final DateTypeAPI dateTypeAPI;
    private final DistributorNameTypeAPI distributorNameTypeAPI;
    private final ForceReasonTypeAPI forceReasonTypeAPI;
    private final ISOCountryTypeAPI iSOCountryTypeAPI;
    private final ISOCountryListTypeAPI iSOCountryListTypeAPI;
    private final ImageTypeTypeAPI imageTypeTypeAPI;
    private final InteractiveTypeTypeAPI interactiveTypeTypeAPI;
    private final LongTypeAPI longTypeAPI;
    private final MovieIdTypeAPI movieIdTypeAPI;
    private final MovieReleaseTypeTypeAPI movieReleaseTypeTypeAPI;
    private final MovieTitleStringTypeAPI movieTitleStringTypeAPI;
    private final MovieTitleTypeTypeAPI movieTitleTypeTypeAPI;
    private final MovieTypeTypeAPI movieTypeTypeAPI;
    private final OverrideEntityTypeTypeAPI overrideEntityTypeTypeAPI;
    private final OverrideEntityValueTypeAPI overrideEntityValueTypeAPI;
    private final PersonIdTypeAPI personIdTypeAPI;
    private final PersonNameTypeAPI personNameTypeAPI;
    private final PhaseNameTypeAPI phaseNameTypeAPI;
    private final PhaseTypeTypeAPI phaseTypeTypeAPI;
    private final RatingsRequirementsTypeAPI ratingsRequirementsTypeAPI;
    private final RecipeGroupsTypeAPI recipeGroupsTypeAPI;
    private final RolloutNameTypeAPI rolloutNameTypeAPI;
    private final RolloutStatusTypeAPI rolloutStatusTypeAPI;
    private final RolloutTypeTypeAPI rolloutTypeTypeAPI;
    private final ShowMemberTypeTypeAPI showMemberTypeTypeAPI;
    private final ShowMemberTypeListTypeAPI showMemberTypeListTypeAPI;
    private final ShowCountryLabelTypeAPI showCountryLabelTypeAPI;
    private final SourceRequestDefaultFulfillmentTypeAPI sourceRequestDefaultFulfillmentTypeAPI;
    private final StringTypeAPI stringTypeAPI;
    private final MovieCountriesNotOriginalTypeAPI movieCountriesNotOriginalTypeAPI;
    private final MovieExtensionOverrideTypeAPI movieExtensionOverrideTypeAPI;
    private final MovieReleaseHistoryTypeAPI movieReleaseHistoryTypeAPI;
    private final MovieSetContentLabelTypeAPI movieSetContentLabelTypeAPI;
    private final PhaseArtworkTypeAPI phaseArtworkTypeAPI;
    private final PhaseCastMemberTypeAPI phaseCastMemberTypeAPI;
    private final PhaseMetadataElementTypeAPI phaseMetadataElementTypeAPI;
    private final PhaseRequiredImageTypeTypeAPI phaseRequiredImageTypeTypeAPI;
    private final PhaseTrailerTypeAPI phaseTrailerTypeAPI;
    private final RolloutCountryTypeAPI rolloutCountryTypeAPI;
    private final SetOfMovieExtensionOverrideTypeAPI setOfMovieExtensionOverrideTypeAPI;
    private final MovieExtensionTypeAPI movieExtensionTypeAPI;
    private final SetOfPhaseArtworkTypeAPI setOfPhaseArtworkTypeAPI;
    private final SetOfPhaseCastMemberTypeAPI setOfPhaseCastMemberTypeAPI;
    private final SetOfPhaseMetadataElementTypeAPI setOfPhaseMetadataElementTypeAPI;
    private final SetOfPhaseRequiredImageTypeTypeAPI setOfPhaseRequiredImageTypeTypeAPI;
    private final SetOfPhaseTrailerTypeAPI setOfPhaseTrailerTypeAPI;
    private final SetOfRolloutCountryTypeAPI setOfRolloutCountryTypeAPI;
    private final SetOfStringTypeAPI setOfStringTypeAPI;
    private final MovieCountriesTypeAPI movieCountriesTypeAPI;
    private final ShowCountryLabelOverrideTypeAPI showCountryLabelOverrideTypeAPI;
    private final SubsDubsTypeAPI subsDubsTypeAPI;
    private final SubtypeStringTypeAPI subtypeStringTypeAPI;
    private final SubtypeTypeAPI subtypeTypeAPI;
    private final SupplementalSubtypeTypeAPI supplementalSubtypeTypeAPI;
    private final MovieTypeAPI movieTypeAPI;
    private final TitleSetupRequirementsTemplateTypeAPI titleSetupRequirementsTemplateTypeAPI;
    private final TitleSetupRequirementsTypeAPI titleSetupRequirementsTypeAPI;
    private final TitleSourceTypeTypeAPI titleSourceTypeTypeAPI;
    private final MovieTitleAkaTypeAPI movieTitleAkaTypeAPI;
    private final WindowTypeTypeAPI windowTypeTypeAPI;
    private final RolloutPhaseTypeAPI rolloutPhaseTypeAPI;
    private final SetOfRolloutPhaseTypeAPI setOfRolloutPhaseTypeAPI;
    private final RolloutTypeAPI rolloutTypeAPI;
    private final IsOriginalTitleTypeAPI isOriginalTitleTypeAPI;
    private final MovieTitleNLSTypeAPI movieTitleNLSTypeAPI;

    private final HollowObjectProvider attributeNameProvider;
    private final HollowObjectProvider attributeValueProvider;
    private final HollowObjectProvider bcpCodeProvider;
    private final HollowObjectProvider countryStringProvider;
    private final HollowObjectProvider dateProvider;
    private final HollowObjectProvider distributorNameProvider;
    private final HollowObjectProvider forceReasonProvider;
    private final HollowObjectProvider iSOCountryProvider;
    private final HollowObjectProvider iSOCountryListProvider;
    private final HollowObjectProvider imageTypeProvider;
    private final HollowObjectProvider interactiveTypeProvider;
    private final HollowObjectProvider longProvider;
    private final HollowObjectProvider movieIdProvider;
    private final HollowObjectProvider movieReleaseTypeProvider;
    private final HollowObjectProvider movieTitleStringProvider;
    private final HollowObjectProvider movieTitleTypeProvider;
    private final HollowObjectProvider movieTypeProvider;
    private final HollowObjectProvider overrideEntityTypeProvider;
    private final HollowObjectProvider overrideEntityValueProvider;
    private final HollowObjectProvider personIdProvider;
    private final HollowObjectProvider personNameProvider;
    private final HollowObjectProvider phaseNameProvider;
    private final HollowObjectProvider phaseTypeProvider;
    private final HollowObjectProvider ratingsRequirementsProvider;
    private final HollowObjectProvider recipeGroupsProvider;
    private final HollowObjectProvider rolloutNameProvider;
    private final HollowObjectProvider rolloutStatusProvider;
    private final HollowObjectProvider rolloutTypeProvider;
    private final HollowObjectProvider showMemberTypeProvider;
    private final HollowObjectProvider showMemberTypeListProvider;
    private final HollowObjectProvider showCountryLabelProvider;
    private final HollowObjectProvider sourceRequestDefaultFulfillmentProvider;
    private final HollowObjectProvider stringProvider;
    private final HollowObjectProvider movieCountriesNotOriginalProvider;
    private final HollowObjectProvider movieExtensionOverrideProvider;
    private final HollowObjectProvider movieReleaseHistoryProvider;
    private final HollowObjectProvider movieSetContentLabelProvider;
    private final HollowObjectProvider phaseArtworkProvider;
    private final HollowObjectProvider phaseCastMemberProvider;
    private final HollowObjectProvider phaseMetadataElementProvider;
    private final HollowObjectProvider phaseRequiredImageTypeProvider;
    private final HollowObjectProvider phaseTrailerProvider;
    private final HollowObjectProvider rolloutCountryProvider;
    private final HollowObjectProvider setOfMovieExtensionOverrideProvider;
    private final HollowObjectProvider movieExtensionProvider;
    private final HollowObjectProvider setOfPhaseArtworkProvider;
    private final HollowObjectProvider setOfPhaseCastMemberProvider;
    private final HollowObjectProvider setOfPhaseMetadataElementProvider;
    private final HollowObjectProvider setOfPhaseRequiredImageTypeProvider;
    private final HollowObjectProvider setOfPhaseTrailerProvider;
    private final HollowObjectProvider setOfRolloutCountryProvider;
    private final HollowObjectProvider setOfStringProvider;
    private final HollowObjectProvider movieCountriesProvider;
    private final HollowObjectProvider showCountryLabelOverrideProvider;
    private final HollowObjectProvider subsDubsProvider;
    private final HollowObjectProvider subtypeStringProvider;
    private final HollowObjectProvider subtypeProvider;
    private final HollowObjectProvider supplementalSubtypeProvider;
    private final HollowObjectProvider movieProvider;
    private final HollowObjectProvider titleSetupRequirementsTemplateProvider;
    private final HollowObjectProvider titleSetupRequirementsProvider;
    private final HollowObjectProvider titleSourceTypeProvider;
    private final HollowObjectProvider movieTitleAkaProvider;
    private final HollowObjectProvider windowTypeProvider;
    private final HollowObjectProvider rolloutPhaseProvider;
    private final HollowObjectProvider setOfRolloutPhaseProvider;
    private final HollowObjectProvider rolloutProvider;
    private final HollowObjectProvider isOriginalTitleProvider;
    private final HollowObjectProvider movieTitleNLSProvider;

    public OscarAPI(HollowDataAccess dataAccess) {
        this(dataAccess, Collections.<String>emptySet());
    }

    public OscarAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
    }

    public OscarAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
        this(dataAccess, cachedTypes, factoryOverrides, null);
    }

    public OscarAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, OscarAPI previousCycleAPI) {
        super(dataAccess);
        HollowTypeDataAccess typeDataAccess;
        HollowFactory factory;

        objectCreationSampler = new HollowObjectCreationSampler("AttributeName","AttributeValue","BcpCode","CountryString","Date","DistributorName","ForceReason","ISOCountry","ISOCountryList","ImageType","InteractiveType","Long","MovieId","MovieReleaseType","MovieTitleString","MovieTitleType","MovieType","OverrideEntityType","OverrideEntityValue","PersonId","PersonName","PhaseName","PhaseType","RatingsRequirements","RecipeGroups","RolloutName","RolloutStatus","RolloutType","ShowMemberType","ShowMemberTypeList","ShowCountryLabel","SourceRequestDefaultFulfillment","String","MovieCountriesNotOriginal","MovieExtensionOverride","MovieReleaseHistory","MovieSetContentLabel","PhaseArtwork","PhaseCastMember","PhaseMetadataElement","PhaseRequiredImageType","PhaseTrailer","RolloutCountry","SetOfMovieExtensionOverride","MovieExtension","SetOfPhaseArtwork","SetOfPhaseCastMember","SetOfPhaseMetadataElement","SetOfPhaseRequiredImageType","SetOfPhaseTrailer","SetOfRolloutCountry","SetOfString","MovieCountries","ShowCountryLabelOverride","SubsDubs","SubtypeString","Subtype","SupplementalSubtype","Movie","TitleSetupRequirementsTemplate","TitleSetupRequirements","TitleSourceType","MovieTitleAka","WindowType","RolloutPhase","SetOfRolloutPhase","Rollout","isOriginalTitle","MovieTitleNLS");

        typeDataAccess = dataAccess.getTypeDataAccess("AttributeName");
        if(typeDataAccess != null) {
            attributeNameTypeAPI = new AttributeNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            attributeNameTypeAPI = new AttributeNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AttributeName"));
        }
        addTypeAPI(attributeNameTypeAPI);
        factory = factoryOverrides.get("AttributeName");
        if(factory == null)
            factory = new AttributeNameHollowFactory();
        if(cachedTypes.contains("AttributeName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.attributeNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.attributeNameProvider;
            attributeNameProvider = new HollowObjectCacheProvider(typeDataAccess, attributeNameTypeAPI, factory, previousCacheProvider);
        } else {
            attributeNameProvider = new HollowObjectFactoryProvider(typeDataAccess, attributeNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("AttributeValue");
        if(typeDataAccess != null) {
            attributeValueTypeAPI = new AttributeValueTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            attributeValueTypeAPI = new AttributeValueTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "AttributeValue"));
        }
        addTypeAPI(attributeValueTypeAPI);
        factory = factoryOverrides.get("AttributeValue");
        if(factory == null)
            factory = new AttributeValueHollowFactory();
        if(cachedTypes.contains("AttributeValue")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.attributeValueProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.attributeValueProvider;
            attributeValueProvider = new HollowObjectCacheProvider(typeDataAccess, attributeValueTypeAPI, factory, previousCacheProvider);
        } else {
            attributeValueProvider = new HollowObjectFactoryProvider(typeDataAccess, attributeValueTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("BcpCode");
        if(typeDataAccess != null) {
            bcpCodeTypeAPI = new BcpCodeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            bcpCodeTypeAPI = new BcpCodeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "BcpCode"));
        }
        addTypeAPI(bcpCodeTypeAPI);
        factory = factoryOverrides.get("BcpCode");
        if(factory == null)
            factory = new BcpCodeHollowFactory();
        if(cachedTypes.contains("BcpCode")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.bcpCodeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.bcpCodeProvider;
            bcpCodeProvider = new HollowObjectCacheProvider(typeDataAccess, bcpCodeTypeAPI, factory, previousCacheProvider);
        } else {
            bcpCodeProvider = new HollowObjectFactoryProvider(typeDataAccess, bcpCodeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("CountryString");
        if(typeDataAccess != null) {
            countryStringTypeAPI = new CountryStringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            countryStringTypeAPI = new CountryStringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "CountryString"));
        }
        addTypeAPI(countryStringTypeAPI);
        factory = factoryOverrides.get("CountryString");
        if(factory == null)
            factory = new CountryStringHollowFactory();
        if(cachedTypes.contains("CountryString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.countryStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.countryStringProvider;
            countryStringProvider = new HollowObjectCacheProvider(typeDataAccess, countryStringTypeAPI, factory, previousCacheProvider);
        } else {
            countryStringProvider = new HollowObjectFactoryProvider(typeDataAccess, countryStringTypeAPI, factory);
        }

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

        typeDataAccess = dataAccess.getTypeDataAccess("DistributorName");
        if(typeDataAccess != null) {
            distributorNameTypeAPI = new DistributorNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            distributorNameTypeAPI = new DistributorNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "DistributorName"));
        }
        addTypeAPI(distributorNameTypeAPI);
        factory = factoryOverrides.get("DistributorName");
        if(factory == null)
            factory = new DistributorNameHollowFactory();
        if(cachedTypes.contains("DistributorName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.distributorNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.distributorNameProvider;
            distributorNameProvider = new HollowObjectCacheProvider(typeDataAccess, distributorNameTypeAPI, factory, previousCacheProvider);
        } else {
            distributorNameProvider = new HollowObjectFactoryProvider(typeDataAccess, distributorNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ForceReason");
        if(typeDataAccess != null) {
            forceReasonTypeAPI = new ForceReasonTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            forceReasonTypeAPI = new ForceReasonTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ForceReason"));
        }
        addTypeAPI(forceReasonTypeAPI);
        factory = factoryOverrides.get("ForceReason");
        if(factory == null)
            factory = new ForceReasonHollowFactory();
        if(cachedTypes.contains("ForceReason")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.forceReasonProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.forceReasonProvider;
            forceReasonProvider = new HollowObjectCacheProvider(typeDataAccess, forceReasonTypeAPI, factory, previousCacheProvider);
        } else {
            forceReasonProvider = new HollowObjectFactoryProvider(typeDataAccess, forceReasonTypeAPI, factory);
        }

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

        typeDataAccess = dataAccess.getTypeDataAccess("ImageType");
        if(typeDataAccess != null) {
            imageTypeTypeAPI = new ImageTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            imageTypeTypeAPI = new ImageTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ImageType"));
        }
        addTypeAPI(imageTypeTypeAPI);
        factory = factoryOverrides.get("ImageType");
        if(factory == null)
            factory = new ImageTypeHollowFactory();
        if(cachedTypes.contains("ImageType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.imageTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.imageTypeProvider;
            imageTypeProvider = new HollowObjectCacheProvider(typeDataAccess, imageTypeTypeAPI, factory, previousCacheProvider);
        } else {
            imageTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, imageTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("InteractiveType");
        if(typeDataAccess != null) {
            interactiveTypeTypeAPI = new InteractiveTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            interactiveTypeTypeAPI = new InteractiveTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "InteractiveType"));
        }
        addTypeAPI(interactiveTypeTypeAPI);
        factory = factoryOverrides.get("InteractiveType");
        if(factory == null)
            factory = new InteractiveTypeHollowFactory();
        if(cachedTypes.contains("InteractiveType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.interactiveTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.interactiveTypeProvider;
            interactiveTypeProvider = new HollowObjectCacheProvider(typeDataAccess, interactiveTypeTypeAPI, factory, previousCacheProvider);
        } else {
            interactiveTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, interactiveTypeTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("MovieId");
        if(typeDataAccess != null) {
            movieIdTypeAPI = new MovieIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieIdTypeAPI = new MovieIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieId"));
        }
        addTypeAPI(movieIdTypeAPI);
        factory = factoryOverrides.get("MovieId");
        if(factory == null)
            factory = new MovieIdHollowFactory();
        if(cachedTypes.contains("MovieId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieIdProvider;
            movieIdProvider = new HollowObjectCacheProvider(typeDataAccess, movieIdTypeAPI, factory, previousCacheProvider);
        } else {
            movieIdProvider = new HollowObjectFactoryProvider(typeDataAccess, movieIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieReleaseType");
        if(typeDataAccess != null) {
            movieReleaseTypeTypeAPI = new MovieReleaseTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieReleaseTypeTypeAPI = new MovieReleaseTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieReleaseType"));
        }
        addTypeAPI(movieReleaseTypeTypeAPI);
        factory = factoryOverrides.get("MovieReleaseType");
        if(factory == null)
            factory = new MovieReleaseTypeHollowFactory();
        if(cachedTypes.contains("MovieReleaseType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieReleaseTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieReleaseTypeProvider;
            movieReleaseTypeProvider = new HollowObjectCacheProvider(typeDataAccess, movieReleaseTypeTypeAPI, factory, previousCacheProvider);
        } else {
            movieReleaseTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, movieReleaseTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieTitleString");
        if(typeDataAccess != null) {
            movieTitleStringTypeAPI = new MovieTitleStringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTitleStringTypeAPI = new MovieTitleStringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieTitleString"));
        }
        addTypeAPI(movieTitleStringTypeAPI);
        factory = factoryOverrides.get("MovieTitleString");
        if(factory == null)
            factory = new MovieTitleStringHollowFactory();
        if(cachedTypes.contains("MovieTitleString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieTitleStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieTitleStringProvider;
            movieTitleStringProvider = new HollowObjectCacheProvider(typeDataAccess, movieTitleStringTypeAPI, factory, previousCacheProvider);
        } else {
            movieTitleStringProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTitleStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieTitleType");
        if(typeDataAccess != null) {
            movieTitleTypeTypeAPI = new MovieTitleTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTitleTypeTypeAPI = new MovieTitleTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieTitleType"));
        }
        addTypeAPI(movieTitleTypeTypeAPI);
        factory = factoryOverrides.get("MovieTitleType");
        if(factory == null)
            factory = new MovieTitleTypeHollowFactory();
        if(cachedTypes.contains("MovieTitleType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieTitleTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieTitleTypeProvider;
            movieTitleTypeProvider = new HollowObjectCacheProvider(typeDataAccess, movieTitleTypeTypeAPI, factory, previousCacheProvider);
        } else {
            movieTitleTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTitleTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieType");
        if(typeDataAccess != null) {
            movieTypeTypeAPI = new MovieTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTypeTypeAPI = new MovieTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieType"));
        }
        addTypeAPI(movieTypeTypeAPI);
        factory = factoryOverrides.get("MovieType");
        if(factory == null)
            factory = new MovieTypeHollowFactory();
        if(cachedTypes.contains("MovieType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieTypeProvider;
            movieTypeProvider = new HollowObjectCacheProvider(typeDataAccess, movieTypeTypeAPI, factory, previousCacheProvider);
        } else {
            movieTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("OverrideEntityType");
        if(typeDataAccess != null) {
            overrideEntityTypeTypeAPI = new OverrideEntityTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            overrideEntityTypeTypeAPI = new OverrideEntityTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "OverrideEntityType"));
        }
        addTypeAPI(overrideEntityTypeTypeAPI);
        factory = factoryOverrides.get("OverrideEntityType");
        if(factory == null)
            factory = new OverrideEntityTypeHollowFactory();
        if(cachedTypes.contains("OverrideEntityType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.overrideEntityTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.overrideEntityTypeProvider;
            overrideEntityTypeProvider = new HollowObjectCacheProvider(typeDataAccess, overrideEntityTypeTypeAPI, factory, previousCacheProvider);
        } else {
            overrideEntityTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, overrideEntityTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("OverrideEntityValue");
        if(typeDataAccess != null) {
            overrideEntityValueTypeAPI = new OverrideEntityValueTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            overrideEntityValueTypeAPI = new OverrideEntityValueTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "OverrideEntityValue"));
        }
        addTypeAPI(overrideEntityValueTypeAPI);
        factory = factoryOverrides.get("OverrideEntityValue");
        if(factory == null)
            factory = new OverrideEntityValueHollowFactory();
        if(cachedTypes.contains("OverrideEntityValue")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.overrideEntityValueProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.overrideEntityValueProvider;
            overrideEntityValueProvider = new HollowObjectCacheProvider(typeDataAccess, overrideEntityValueTypeAPI, factory, previousCacheProvider);
        } else {
            overrideEntityValueProvider = new HollowObjectFactoryProvider(typeDataAccess, overrideEntityValueTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonId");
        if(typeDataAccess != null) {
            personIdTypeAPI = new PersonIdTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personIdTypeAPI = new PersonIdTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonId"));
        }
        addTypeAPI(personIdTypeAPI);
        factory = factoryOverrides.get("PersonId");
        if(factory == null)
            factory = new PersonIdHollowFactory();
        if(cachedTypes.contains("PersonId")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personIdProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personIdProvider;
            personIdProvider = new HollowObjectCacheProvider(typeDataAccess, personIdTypeAPI, factory, previousCacheProvider);
        } else {
            personIdProvider = new HollowObjectFactoryProvider(typeDataAccess, personIdTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PersonName");
        if(typeDataAccess != null) {
            personNameTypeAPI = new PersonNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            personNameTypeAPI = new PersonNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PersonName"));
        }
        addTypeAPI(personNameTypeAPI);
        factory = factoryOverrides.get("PersonName");
        if(factory == null)
            factory = new PersonNameHollowFactory();
        if(cachedTypes.contains("PersonName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.personNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.personNameProvider;
            personNameProvider = new HollowObjectCacheProvider(typeDataAccess, personNameTypeAPI, factory, previousCacheProvider);
        } else {
            personNameProvider = new HollowObjectFactoryProvider(typeDataAccess, personNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseName");
        if(typeDataAccess != null) {
            phaseNameTypeAPI = new PhaseNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseNameTypeAPI = new PhaseNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseName"));
        }
        addTypeAPI(phaseNameTypeAPI);
        factory = factoryOverrides.get("PhaseName");
        if(factory == null)
            factory = new PhaseNameHollowFactory();
        if(cachedTypes.contains("PhaseName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseNameProvider;
            phaseNameProvider = new HollowObjectCacheProvider(typeDataAccess, phaseNameTypeAPI, factory, previousCacheProvider);
        } else {
            phaseNameProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseType");
        if(typeDataAccess != null) {
            phaseTypeTypeAPI = new PhaseTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseTypeTypeAPI = new PhaseTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseType"));
        }
        addTypeAPI(phaseTypeTypeAPI);
        factory = factoryOverrides.get("PhaseType");
        if(factory == null)
            factory = new PhaseTypeHollowFactory();
        if(cachedTypes.contains("PhaseType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseTypeProvider;
            phaseTypeProvider = new HollowObjectCacheProvider(typeDataAccess, phaseTypeTypeAPI, factory, previousCacheProvider);
        } else {
            phaseTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RatingsRequirements");
        if(typeDataAccess != null) {
            ratingsRequirementsTypeAPI = new RatingsRequirementsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            ratingsRequirementsTypeAPI = new RatingsRequirementsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RatingsRequirements"));
        }
        addTypeAPI(ratingsRequirementsTypeAPI);
        factory = factoryOverrides.get("RatingsRequirements");
        if(factory == null)
            factory = new RatingsRequirementsHollowFactory();
        if(cachedTypes.contains("RatingsRequirements")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.ratingsRequirementsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.ratingsRequirementsProvider;
            ratingsRequirementsProvider = new HollowObjectCacheProvider(typeDataAccess, ratingsRequirementsTypeAPI, factory, previousCacheProvider);
        } else {
            ratingsRequirementsProvider = new HollowObjectFactoryProvider(typeDataAccess, ratingsRequirementsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RecipeGroups");
        if(typeDataAccess != null) {
            recipeGroupsTypeAPI = new RecipeGroupsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            recipeGroupsTypeAPI = new RecipeGroupsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RecipeGroups"));
        }
        addTypeAPI(recipeGroupsTypeAPI);
        factory = factoryOverrides.get("RecipeGroups");
        if(factory == null)
            factory = new RecipeGroupsHollowFactory();
        if(cachedTypes.contains("RecipeGroups")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.recipeGroupsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.recipeGroupsProvider;
            recipeGroupsProvider = new HollowObjectCacheProvider(typeDataAccess, recipeGroupsTypeAPI, factory, previousCacheProvider);
        } else {
            recipeGroupsProvider = new HollowObjectFactoryProvider(typeDataAccess, recipeGroupsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutName");
        if(typeDataAccess != null) {
            rolloutNameTypeAPI = new RolloutNameTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutNameTypeAPI = new RolloutNameTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutName"));
        }
        addTypeAPI(rolloutNameTypeAPI);
        factory = factoryOverrides.get("RolloutName");
        if(factory == null)
            factory = new RolloutNameHollowFactory();
        if(cachedTypes.contains("RolloutName")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutNameProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutNameProvider;
            rolloutNameProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutNameTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutNameProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutNameTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutStatus");
        if(typeDataAccess != null) {
            rolloutStatusTypeAPI = new RolloutStatusTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutStatusTypeAPI = new RolloutStatusTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutStatus"));
        }
        addTypeAPI(rolloutStatusTypeAPI);
        factory = factoryOverrides.get("RolloutStatus");
        if(factory == null)
            factory = new RolloutStatusHollowFactory();
        if(cachedTypes.contains("RolloutStatus")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutStatusProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutStatusProvider;
            rolloutStatusProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutStatusTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutStatusProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutStatusTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutType");
        if(typeDataAccess != null) {
            rolloutTypeTypeAPI = new RolloutTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutTypeTypeAPI = new RolloutTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutType"));
        }
        addTypeAPI(rolloutTypeTypeAPI);
        factory = factoryOverrides.get("RolloutType");
        if(factory == null)
            factory = new RolloutTypeHollowFactory();
        if(cachedTypes.contains("RolloutType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutTypeProvider;
            rolloutTypeProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutTypeTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutTypeTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("SourceRequestDefaultFulfillment");
        if(typeDataAccess != null) {
            sourceRequestDefaultFulfillmentTypeAPI = new SourceRequestDefaultFulfillmentTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            sourceRequestDefaultFulfillmentTypeAPI = new SourceRequestDefaultFulfillmentTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "SourceRequestDefaultFulfillment"));
        }
        addTypeAPI(sourceRequestDefaultFulfillmentTypeAPI);
        factory = factoryOverrides.get("SourceRequestDefaultFulfillment");
        if(factory == null)
            factory = new SourceRequestDefaultFulfillmentHollowFactory();
        if(cachedTypes.contains("SourceRequestDefaultFulfillment")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.sourceRequestDefaultFulfillmentProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.sourceRequestDefaultFulfillmentProvider;
            sourceRequestDefaultFulfillmentProvider = new HollowObjectCacheProvider(typeDataAccess, sourceRequestDefaultFulfillmentTypeAPI, factory, previousCacheProvider);
        } else {
            sourceRequestDefaultFulfillmentProvider = new HollowObjectFactoryProvider(typeDataAccess, sourceRequestDefaultFulfillmentTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("MovieCountriesNotOriginal");
        if(typeDataAccess != null) {
            movieCountriesNotOriginalTypeAPI = new MovieCountriesNotOriginalTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieCountriesNotOriginalTypeAPI = new MovieCountriesNotOriginalTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieCountriesNotOriginal"));
        }
        addTypeAPI(movieCountriesNotOriginalTypeAPI);
        factory = factoryOverrides.get("MovieCountriesNotOriginal");
        if(factory == null)
            factory = new MovieCountriesNotOriginalHollowFactory();
        if(cachedTypes.contains("MovieCountriesNotOriginal")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieCountriesNotOriginalProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieCountriesNotOriginalProvider;
            movieCountriesNotOriginalProvider = new HollowObjectCacheProvider(typeDataAccess, movieCountriesNotOriginalTypeAPI, factory, previousCacheProvider);
        } else {
            movieCountriesNotOriginalProvider = new HollowObjectFactoryProvider(typeDataAccess, movieCountriesNotOriginalTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieExtensionOverride");
        if(typeDataAccess != null) {
            movieExtensionOverrideTypeAPI = new MovieExtensionOverrideTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieExtensionOverrideTypeAPI = new MovieExtensionOverrideTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieExtensionOverride"));
        }
        addTypeAPI(movieExtensionOverrideTypeAPI);
        factory = factoryOverrides.get("MovieExtensionOverride");
        if(factory == null)
            factory = new MovieExtensionOverrideHollowFactory();
        if(cachedTypes.contains("MovieExtensionOverride")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieExtensionOverrideProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieExtensionOverrideProvider;
            movieExtensionOverrideProvider = new HollowObjectCacheProvider(typeDataAccess, movieExtensionOverrideTypeAPI, factory, previousCacheProvider);
        } else {
            movieExtensionOverrideProvider = new HollowObjectFactoryProvider(typeDataAccess, movieExtensionOverrideTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieReleaseHistory");
        if(typeDataAccess != null) {
            movieReleaseHistoryTypeAPI = new MovieReleaseHistoryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieReleaseHistoryTypeAPI = new MovieReleaseHistoryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieReleaseHistory"));
        }
        addTypeAPI(movieReleaseHistoryTypeAPI);
        factory = factoryOverrides.get("MovieReleaseHistory");
        if(factory == null)
            factory = new MovieReleaseHistoryHollowFactory();
        if(cachedTypes.contains("MovieReleaseHistory")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieReleaseHistoryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieReleaseHistoryProvider;
            movieReleaseHistoryProvider = new HollowObjectCacheProvider(typeDataAccess, movieReleaseHistoryTypeAPI, factory, previousCacheProvider);
        } else {
            movieReleaseHistoryProvider = new HollowObjectFactoryProvider(typeDataAccess, movieReleaseHistoryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieSetContentLabel");
        if(typeDataAccess != null) {
            movieSetContentLabelTypeAPI = new MovieSetContentLabelTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieSetContentLabelTypeAPI = new MovieSetContentLabelTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieSetContentLabel"));
        }
        addTypeAPI(movieSetContentLabelTypeAPI);
        factory = factoryOverrides.get("MovieSetContentLabel");
        if(factory == null)
            factory = new MovieSetContentLabelHollowFactory();
        if(cachedTypes.contains("MovieSetContentLabel")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieSetContentLabelProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieSetContentLabelProvider;
            movieSetContentLabelProvider = new HollowObjectCacheProvider(typeDataAccess, movieSetContentLabelTypeAPI, factory, previousCacheProvider);
        } else {
            movieSetContentLabelProvider = new HollowObjectFactoryProvider(typeDataAccess, movieSetContentLabelTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseArtwork");
        if(typeDataAccess != null) {
            phaseArtworkTypeAPI = new PhaseArtworkTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseArtworkTypeAPI = new PhaseArtworkTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseArtwork"));
        }
        addTypeAPI(phaseArtworkTypeAPI);
        factory = factoryOverrides.get("PhaseArtwork");
        if(factory == null)
            factory = new PhaseArtworkHollowFactory();
        if(cachedTypes.contains("PhaseArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseArtworkProvider;
            phaseArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, phaseArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            phaseArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseCastMember");
        if(typeDataAccess != null) {
            phaseCastMemberTypeAPI = new PhaseCastMemberTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseCastMemberTypeAPI = new PhaseCastMemberTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseCastMember"));
        }
        addTypeAPI(phaseCastMemberTypeAPI);
        factory = factoryOverrides.get("PhaseCastMember");
        if(factory == null)
            factory = new PhaseCastMemberHollowFactory();
        if(cachedTypes.contains("PhaseCastMember")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseCastMemberProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseCastMemberProvider;
            phaseCastMemberProvider = new HollowObjectCacheProvider(typeDataAccess, phaseCastMemberTypeAPI, factory, previousCacheProvider);
        } else {
            phaseCastMemberProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseCastMemberTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseMetadataElement");
        if(typeDataAccess != null) {
            phaseMetadataElementTypeAPI = new PhaseMetadataElementTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseMetadataElementTypeAPI = new PhaseMetadataElementTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseMetadataElement"));
        }
        addTypeAPI(phaseMetadataElementTypeAPI);
        factory = factoryOverrides.get("PhaseMetadataElement");
        if(factory == null)
            factory = new PhaseMetadataElementHollowFactory();
        if(cachedTypes.contains("PhaseMetadataElement")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseMetadataElementProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseMetadataElementProvider;
            phaseMetadataElementProvider = new HollowObjectCacheProvider(typeDataAccess, phaseMetadataElementTypeAPI, factory, previousCacheProvider);
        } else {
            phaseMetadataElementProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseMetadataElementTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseRequiredImageType");
        if(typeDataAccess != null) {
            phaseRequiredImageTypeTypeAPI = new PhaseRequiredImageTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseRequiredImageTypeTypeAPI = new PhaseRequiredImageTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseRequiredImageType"));
        }
        addTypeAPI(phaseRequiredImageTypeTypeAPI);
        factory = factoryOverrides.get("PhaseRequiredImageType");
        if(factory == null)
            factory = new PhaseRequiredImageTypeHollowFactory();
        if(cachedTypes.contains("PhaseRequiredImageType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseRequiredImageTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseRequiredImageTypeProvider;
            phaseRequiredImageTypeProvider = new HollowObjectCacheProvider(typeDataAccess, phaseRequiredImageTypeTypeAPI, factory, previousCacheProvider);
        } else {
            phaseRequiredImageTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseRequiredImageTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("PhaseTrailer");
        if(typeDataAccess != null) {
            phaseTrailerTypeAPI = new PhaseTrailerTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            phaseTrailerTypeAPI = new PhaseTrailerTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "PhaseTrailer"));
        }
        addTypeAPI(phaseTrailerTypeAPI);
        factory = factoryOverrides.get("PhaseTrailer");
        if(factory == null)
            factory = new PhaseTrailerHollowFactory();
        if(cachedTypes.contains("PhaseTrailer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.phaseTrailerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.phaseTrailerProvider;
            phaseTrailerProvider = new HollowObjectCacheProvider(typeDataAccess, phaseTrailerTypeAPI, factory, previousCacheProvider);
        } else {
            phaseTrailerProvider = new HollowObjectFactoryProvider(typeDataAccess, phaseTrailerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("RolloutCountry");
        if(typeDataAccess != null) {
            rolloutCountryTypeAPI = new RolloutCountryTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            rolloutCountryTypeAPI = new RolloutCountryTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "RolloutCountry"));
        }
        addTypeAPI(rolloutCountryTypeAPI);
        factory = factoryOverrides.get("RolloutCountry");
        if(factory == null)
            factory = new RolloutCountryHollowFactory();
        if(cachedTypes.contains("RolloutCountry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.rolloutCountryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.rolloutCountryProvider;
            rolloutCountryProvider = new HollowObjectCacheProvider(typeDataAccess, rolloutCountryTypeAPI, factory, previousCacheProvider);
        } else {
            rolloutCountryProvider = new HollowObjectFactoryProvider(typeDataAccess, rolloutCountryTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfMovieExtensionOverride");
        if(typeDataAccess != null) {
            setOfMovieExtensionOverrideTypeAPI = new SetOfMovieExtensionOverrideTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfMovieExtensionOverrideTypeAPI = new SetOfMovieExtensionOverrideTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfMovieExtensionOverride"));
        }
        addTypeAPI(setOfMovieExtensionOverrideTypeAPI);
        factory = factoryOverrides.get("SetOfMovieExtensionOverride");
        if(factory == null)
            factory = new SetOfMovieExtensionOverrideHollowFactory();
        if(cachedTypes.contains("SetOfMovieExtensionOverride")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfMovieExtensionOverrideProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfMovieExtensionOverrideProvider;
            setOfMovieExtensionOverrideProvider = new HollowObjectCacheProvider(typeDataAccess, setOfMovieExtensionOverrideTypeAPI, factory, previousCacheProvider);
        } else {
            setOfMovieExtensionOverrideProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfMovieExtensionOverrideTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieExtension");
        if(typeDataAccess != null) {
            movieExtensionTypeAPI = new MovieExtensionTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieExtensionTypeAPI = new MovieExtensionTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieExtension"));
        }
        addTypeAPI(movieExtensionTypeAPI);
        factory = factoryOverrides.get("MovieExtension");
        if(factory == null)
            factory = new MovieExtensionHollowFactory();
        if(cachedTypes.contains("MovieExtension")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieExtensionProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieExtensionProvider;
            movieExtensionProvider = new HollowObjectCacheProvider(typeDataAccess, movieExtensionTypeAPI, factory, previousCacheProvider);
        } else {
            movieExtensionProvider = new HollowObjectFactoryProvider(typeDataAccess, movieExtensionTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfPhaseArtwork");
        if(typeDataAccess != null) {
            setOfPhaseArtworkTypeAPI = new SetOfPhaseArtworkTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfPhaseArtworkTypeAPI = new SetOfPhaseArtworkTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfPhaseArtwork"));
        }
        addTypeAPI(setOfPhaseArtworkTypeAPI);
        factory = factoryOverrides.get("SetOfPhaseArtwork");
        if(factory == null)
            factory = new SetOfPhaseArtworkHollowFactory();
        if(cachedTypes.contains("SetOfPhaseArtwork")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfPhaseArtworkProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfPhaseArtworkProvider;
            setOfPhaseArtworkProvider = new HollowObjectCacheProvider(typeDataAccess, setOfPhaseArtworkTypeAPI, factory, previousCacheProvider);
        } else {
            setOfPhaseArtworkProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfPhaseArtworkTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfPhaseCastMember");
        if(typeDataAccess != null) {
            setOfPhaseCastMemberTypeAPI = new SetOfPhaseCastMemberTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfPhaseCastMemberTypeAPI = new SetOfPhaseCastMemberTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfPhaseCastMember"));
        }
        addTypeAPI(setOfPhaseCastMemberTypeAPI);
        factory = factoryOverrides.get("SetOfPhaseCastMember");
        if(factory == null)
            factory = new SetOfPhaseCastMemberHollowFactory();
        if(cachedTypes.contains("SetOfPhaseCastMember")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfPhaseCastMemberProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfPhaseCastMemberProvider;
            setOfPhaseCastMemberProvider = new HollowObjectCacheProvider(typeDataAccess, setOfPhaseCastMemberTypeAPI, factory, previousCacheProvider);
        } else {
            setOfPhaseCastMemberProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfPhaseCastMemberTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfPhaseMetadataElement");
        if(typeDataAccess != null) {
            setOfPhaseMetadataElementTypeAPI = new SetOfPhaseMetadataElementTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfPhaseMetadataElementTypeAPI = new SetOfPhaseMetadataElementTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfPhaseMetadataElement"));
        }
        addTypeAPI(setOfPhaseMetadataElementTypeAPI);
        factory = factoryOverrides.get("SetOfPhaseMetadataElement");
        if(factory == null)
            factory = new SetOfPhaseMetadataElementHollowFactory();
        if(cachedTypes.contains("SetOfPhaseMetadataElement")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfPhaseMetadataElementProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfPhaseMetadataElementProvider;
            setOfPhaseMetadataElementProvider = new HollowObjectCacheProvider(typeDataAccess, setOfPhaseMetadataElementTypeAPI, factory, previousCacheProvider);
        } else {
            setOfPhaseMetadataElementProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfPhaseMetadataElementTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfPhaseRequiredImageType");
        if(typeDataAccess != null) {
            setOfPhaseRequiredImageTypeTypeAPI = new SetOfPhaseRequiredImageTypeTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfPhaseRequiredImageTypeTypeAPI = new SetOfPhaseRequiredImageTypeTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfPhaseRequiredImageType"));
        }
        addTypeAPI(setOfPhaseRequiredImageTypeTypeAPI);
        factory = factoryOverrides.get("SetOfPhaseRequiredImageType");
        if(factory == null)
            factory = new SetOfPhaseRequiredImageTypeHollowFactory();
        if(cachedTypes.contains("SetOfPhaseRequiredImageType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfPhaseRequiredImageTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfPhaseRequiredImageTypeProvider;
            setOfPhaseRequiredImageTypeProvider = new HollowObjectCacheProvider(typeDataAccess, setOfPhaseRequiredImageTypeTypeAPI, factory, previousCacheProvider);
        } else {
            setOfPhaseRequiredImageTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfPhaseRequiredImageTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfPhaseTrailer");
        if(typeDataAccess != null) {
            setOfPhaseTrailerTypeAPI = new SetOfPhaseTrailerTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfPhaseTrailerTypeAPI = new SetOfPhaseTrailerTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfPhaseTrailer"));
        }
        addTypeAPI(setOfPhaseTrailerTypeAPI);
        factory = factoryOverrides.get("SetOfPhaseTrailer");
        if(factory == null)
            factory = new SetOfPhaseTrailerHollowFactory();
        if(cachedTypes.contains("SetOfPhaseTrailer")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfPhaseTrailerProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfPhaseTrailerProvider;
            setOfPhaseTrailerProvider = new HollowObjectCacheProvider(typeDataAccess, setOfPhaseTrailerTypeAPI, factory, previousCacheProvider);
        } else {
            setOfPhaseTrailerProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfPhaseTrailerTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfRolloutCountry");
        if(typeDataAccess != null) {
            setOfRolloutCountryTypeAPI = new SetOfRolloutCountryTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfRolloutCountryTypeAPI = new SetOfRolloutCountryTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfRolloutCountry"));
        }
        addTypeAPI(setOfRolloutCountryTypeAPI);
        factory = factoryOverrides.get("SetOfRolloutCountry");
        if(factory == null)
            factory = new SetOfRolloutCountryHollowFactory();
        if(cachedTypes.contains("SetOfRolloutCountry")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfRolloutCountryProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfRolloutCountryProvider;
            setOfRolloutCountryProvider = new HollowObjectCacheProvider(typeDataAccess, setOfRolloutCountryTypeAPI, factory, previousCacheProvider);
        } else {
            setOfRolloutCountryProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfRolloutCountryTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("MovieCountries");
        if(typeDataAccess != null) {
            movieCountriesTypeAPI = new MovieCountriesTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieCountriesTypeAPI = new MovieCountriesTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieCountries"));
        }
        addTypeAPI(movieCountriesTypeAPI);
        factory = factoryOverrides.get("MovieCountries");
        if(factory == null)
            factory = new MovieCountriesHollowFactory();
        if(cachedTypes.contains("MovieCountries")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieCountriesProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieCountriesProvider;
            movieCountriesProvider = new HollowObjectCacheProvider(typeDataAccess, movieCountriesTypeAPI, factory, previousCacheProvider);
        } else {
            movieCountriesProvider = new HollowObjectFactoryProvider(typeDataAccess, movieCountriesTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("ShowCountryLabelOverride");
        if(typeDataAccess != null) {
            showCountryLabelOverrideTypeAPI = new ShowCountryLabelOverrideTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            showCountryLabelOverrideTypeAPI = new ShowCountryLabelOverrideTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "ShowCountryLabelOverride"));
        }
        addTypeAPI(showCountryLabelOverrideTypeAPI);
        factory = factoryOverrides.get("ShowCountryLabelOverride");
        if(factory == null)
            factory = new ShowCountryLabelOverrideHollowFactory();
        if(cachedTypes.contains("ShowCountryLabelOverride")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.showCountryLabelOverrideProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.showCountryLabelOverrideProvider;
            showCountryLabelOverrideProvider = new HollowObjectCacheProvider(typeDataAccess, showCountryLabelOverrideTypeAPI, factory, previousCacheProvider);
        } else {
            showCountryLabelOverrideProvider = new HollowObjectFactoryProvider(typeDataAccess, showCountryLabelOverrideTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SubsDubs");
        if(typeDataAccess != null) {
            subsDubsTypeAPI = new SubsDubsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            subsDubsTypeAPI = new SubsDubsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "SubsDubs"));
        }
        addTypeAPI(subsDubsTypeAPI);
        factory = factoryOverrides.get("SubsDubs");
        if(factory == null)
            factory = new SubsDubsHollowFactory();
        if(cachedTypes.contains("SubsDubs")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.subsDubsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.subsDubsProvider;
            subsDubsProvider = new HollowObjectCacheProvider(typeDataAccess, subsDubsTypeAPI, factory, previousCacheProvider);
        } else {
            subsDubsProvider = new HollowObjectFactoryProvider(typeDataAccess, subsDubsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SubtypeString");
        if(typeDataAccess != null) {
            subtypeStringTypeAPI = new SubtypeStringTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            subtypeStringTypeAPI = new SubtypeStringTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "SubtypeString"));
        }
        addTypeAPI(subtypeStringTypeAPI);
        factory = factoryOverrides.get("SubtypeString");
        if(factory == null)
            factory = new SubtypeStringHollowFactory();
        if(cachedTypes.contains("SubtypeString")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.subtypeStringProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.subtypeStringProvider;
            subtypeStringProvider = new HollowObjectCacheProvider(typeDataAccess, subtypeStringTypeAPI, factory, previousCacheProvider);
        } else {
            subtypeStringProvider = new HollowObjectFactoryProvider(typeDataAccess, subtypeStringTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Subtype");
        if(typeDataAccess != null) {
            subtypeTypeAPI = new SubtypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            subtypeTypeAPI = new SubtypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Subtype"));
        }
        addTypeAPI(subtypeTypeAPI);
        factory = factoryOverrides.get("Subtype");
        if(factory == null)
            factory = new SubtypeHollowFactory();
        if(cachedTypes.contains("Subtype")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.subtypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.subtypeProvider;
            subtypeProvider = new HollowObjectCacheProvider(typeDataAccess, subtypeTypeAPI, factory, previousCacheProvider);
        } else {
            subtypeProvider = new HollowObjectFactoryProvider(typeDataAccess, subtypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("SupplementalSubtype");
        if(typeDataAccess != null) {
            supplementalSubtypeTypeAPI = new SupplementalSubtypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            supplementalSubtypeTypeAPI = new SupplementalSubtypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "SupplementalSubtype"));
        }
        addTypeAPI(supplementalSubtypeTypeAPI);
        factory = factoryOverrides.get("SupplementalSubtype");
        if(factory == null)
            factory = new SupplementalSubtypeHollowFactory();
        if(cachedTypes.contains("SupplementalSubtype")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.supplementalSubtypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.supplementalSubtypeProvider;
            supplementalSubtypeProvider = new HollowObjectCacheProvider(typeDataAccess, supplementalSubtypeTypeAPI, factory, previousCacheProvider);
        } else {
            supplementalSubtypeProvider = new HollowObjectFactoryProvider(typeDataAccess, supplementalSubtypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("Movie");
        if(typeDataAccess != null) {
            movieTypeAPI = new MovieTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTypeAPI = new MovieTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Movie"));
        }
        addTypeAPI(movieTypeAPI);
        factory = factoryOverrides.get("Movie");
        if(factory == null)
            factory = new MovieHollowFactory();
        if(cachedTypes.contains("Movie")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieProvider;
            movieProvider = new HollowObjectCacheProvider(typeDataAccess, movieTypeAPI, factory, previousCacheProvider);
        } else {
            movieProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TitleSetupRequirementsTemplate");
        if(typeDataAccess != null) {
            titleSetupRequirementsTemplateTypeAPI = new TitleSetupRequirementsTemplateTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            titleSetupRequirementsTemplateTypeAPI = new TitleSetupRequirementsTemplateTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TitleSetupRequirementsTemplate"));
        }
        addTypeAPI(titleSetupRequirementsTemplateTypeAPI);
        factory = factoryOverrides.get("TitleSetupRequirementsTemplate");
        if(factory == null)
            factory = new TitleSetupRequirementsTemplateHollowFactory();
        if(cachedTypes.contains("TitleSetupRequirementsTemplate")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.titleSetupRequirementsTemplateProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.titleSetupRequirementsTemplateProvider;
            titleSetupRequirementsTemplateProvider = new HollowObjectCacheProvider(typeDataAccess, titleSetupRequirementsTemplateTypeAPI, factory, previousCacheProvider);
        } else {
            titleSetupRequirementsTemplateProvider = new HollowObjectFactoryProvider(typeDataAccess, titleSetupRequirementsTemplateTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TitleSetupRequirements");
        if(typeDataAccess != null) {
            titleSetupRequirementsTypeAPI = new TitleSetupRequirementsTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            titleSetupRequirementsTypeAPI = new TitleSetupRequirementsTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TitleSetupRequirements"));
        }
        addTypeAPI(titleSetupRequirementsTypeAPI);
        factory = factoryOverrides.get("TitleSetupRequirements");
        if(factory == null)
            factory = new TitleSetupRequirementsHollowFactory();
        if(cachedTypes.contains("TitleSetupRequirements")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.titleSetupRequirementsProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.titleSetupRequirementsProvider;
            titleSetupRequirementsProvider = new HollowObjectCacheProvider(typeDataAccess, titleSetupRequirementsTypeAPI, factory, previousCacheProvider);
        } else {
            titleSetupRequirementsProvider = new HollowObjectFactoryProvider(typeDataAccess, titleSetupRequirementsTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("TitleSourceType");
        if(typeDataAccess != null) {
            titleSourceTypeTypeAPI = new TitleSourceTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            titleSourceTypeTypeAPI = new TitleSourceTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "TitleSourceType"));
        }
        addTypeAPI(titleSourceTypeTypeAPI);
        factory = factoryOverrides.get("TitleSourceType");
        if(factory == null)
            factory = new TitleSourceTypeHollowFactory();
        if(cachedTypes.contains("TitleSourceType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.titleSourceTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.titleSourceTypeProvider;
            titleSourceTypeProvider = new HollowObjectCacheProvider(typeDataAccess, titleSourceTypeTypeAPI, factory, previousCacheProvider);
        } else {
            titleSourceTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, titleSourceTypeTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieTitleAka");
        if(typeDataAccess != null) {
            movieTitleAkaTypeAPI = new MovieTitleAkaTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTitleAkaTypeAPI = new MovieTitleAkaTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieTitleAka"));
        }
        addTypeAPI(movieTitleAkaTypeAPI);
        factory = factoryOverrides.get("MovieTitleAka");
        if(factory == null)
            factory = new MovieTitleAkaHollowFactory();
        if(cachedTypes.contains("MovieTitleAka")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieTitleAkaProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieTitleAkaProvider;
            movieTitleAkaProvider = new HollowObjectCacheProvider(typeDataAccess, movieTitleAkaTypeAPI, factory, previousCacheProvider);
        } else {
            movieTitleAkaProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTitleAkaTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("WindowType");
        if(typeDataAccess != null) {
            windowTypeTypeAPI = new WindowTypeTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            windowTypeTypeAPI = new WindowTypeTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "WindowType"));
        }
        addTypeAPI(windowTypeTypeAPI);
        factory = factoryOverrides.get("WindowType");
        if(factory == null)
            factory = new WindowTypeHollowFactory();
        if(cachedTypes.contains("WindowType")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.windowTypeProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.windowTypeProvider;
            windowTypeProvider = new HollowObjectCacheProvider(typeDataAccess, windowTypeTypeAPI, factory, previousCacheProvider);
        } else {
            windowTypeProvider = new HollowObjectFactoryProvider(typeDataAccess, windowTypeTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("SetOfRolloutPhase");
        if(typeDataAccess != null) {
            setOfRolloutPhaseTypeAPI = new SetOfRolloutPhaseTypeAPI(this, (HollowSetTypeDataAccess)typeDataAccess);
        } else {
            setOfRolloutPhaseTypeAPI = new SetOfRolloutPhaseTypeAPI(this, new HollowSetMissingDataAccess(dataAccess, "SetOfRolloutPhase"));
        }
        addTypeAPI(setOfRolloutPhaseTypeAPI);
        factory = factoryOverrides.get("SetOfRolloutPhase");
        if(factory == null)
            factory = new SetOfRolloutPhaseHollowFactory();
        if(cachedTypes.contains("SetOfRolloutPhase")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.setOfRolloutPhaseProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.setOfRolloutPhaseProvider;
            setOfRolloutPhaseProvider = new HollowObjectCacheProvider(typeDataAccess, setOfRolloutPhaseTypeAPI, factory, previousCacheProvider);
        } else {
            setOfRolloutPhaseProvider = new HollowObjectFactoryProvider(typeDataAccess, setOfRolloutPhaseTypeAPI, factory);
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

        typeDataAccess = dataAccess.getTypeDataAccess("isOriginalTitle");
        if(typeDataAccess != null) {
            isOriginalTitleTypeAPI = new IsOriginalTitleTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            isOriginalTitleTypeAPI = new IsOriginalTitleTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "isOriginalTitle"));
        }
        addTypeAPI(isOriginalTitleTypeAPI);
        factory = factoryOverrides.get("isOriginalTitle");
        if(factory == null)
            factory = new IsOriginalTitleHollowFactory();
        if(cachedTypes.contains("isOriginalTitle")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.isOriginalTitleProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.isOriginalTitleProvider;
            isOriginalTitleProvider = new HollowObjectCacheProvider(typeDataAccess, isOriginalTitleTypeAPI, factory, previousCacheProvider);
        } else {
            isOriginalTitleProvider = new HollowObjectFactoryProvider(typeDataAccess, isOriginalTitleTypeAPI, factory);
        }

        typeDataAccess = dataAccess.getTypeDataAccess("MovieTitleNLS");
        if(typeDataAccess != null) {
            movieTitleNLSTypeAPI = new MovieTitleNLSTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
        } else {
            movieTitleNLSTypeAPI = new MovieTitleNLSTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "MovieTitleNLS"));
        }
        addTypeAPI(movieTitleNLSTypeAPI);
        factory = factoryOverrides.get("MovieTitleNLS");
        if(factory == null)
            factory = new MovieTitleNLSHollowFactory();
        if(cachedTypes.contains("MovieTitleNLS")) {
            HollowObjectCacheProvider previousCacheProvider = null;
            if(previousCycleAPI != null && (previousCycleAPI.movieTitleNLSProvider instanceof HollowObjectCacheProvider))
                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.movieTitleNLSProvider;
            movieTitleNLSProvider = new HollowObjectCacheProvider(typeDataAccess, movieTitleNLSTypeAPI, factory, previousCacheProvider);
        } else {
            movieTitleNLSProvider = new HollowObjectFactoryProvider(typeDataAccess, movieTitleNLSTypeAPI, factory);
        }

    }

    public void detachCaches() {
        if(attributeNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)attributeNameProvider).detach();
        if(attributeValueProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)attributeValueProvider).detach();
        if(bcpCodeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)bcpCodeProvider).detach();
        if(countryStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)countryStringProvider).detach();
        if(dateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)dateProvider).detach();
        if(distributorNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)distributorNameProvider).detach();
        if(forceReasonProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)forceReasonProvider).detach();
        if(iSOCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryProvider).detach();
        if(iSOCountryListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)iSOCountryListProvider).detach();
        if(imageTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)imageTypeProvider).detach();
        if(interactiveTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)interactiveTypeProvider).detach();
        if(longProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)longProvider).detach();
        if(movieIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieIdProvider).detach();
        if(movieReleaseTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieReleaseTypeProvider).detach();
        if(movieTitleStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieTitleStringProvider).detach();
        if(movieTitleTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieTitleTypeProvider).detach();
        if(movieTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieTypeProvider).detach();
        if(overrideEntityTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)overrideEntityTypeProvider).detach();
        if(overrideEntityValueProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)overrideEntityValueProvider).detach();
        if(personIdProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personIdProvider).detach();
        if(personNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)personNameProvider).detach();
        if(phaseNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseNameProvider).detach();
        if(phaseTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseTypeProvider).detach();
        if(ratingsRequirementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)ratingsRequirementsProvider).detach();
        if(recipeGroupsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)recipeGroupsProvider).detach();
        if(rolloutNameProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutNameProvider).detach();
        if(rolloutStatusProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutStatusProvider).detach();
        if(rolloutTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutTypeProvider).detach();
        if(showMemberTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypeProvider).detach();
        if(showMemberTypeListProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showMemberTypeListProvider).detach();
        if(showCountryLabelProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showCountryLabelProvider).detach();
        if(sourceRequestDefaultFulfillmentProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)sourceRequestDefaultFulfillmentProvider).detach();
        if(stringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)stringProvider).detach();
        if(movieCountriesNotOriginalProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieCountriesNotOriginalProvider).detach();
        if(movieExtensionOverrideProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieExtensionOverrideProvider).detach();
        if(movieReleaseHistoryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieReleaseHistoryProvider).detach();
        if(movieSetContentLabelProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieSetContentLabelProvider).detach();
        if(phaseArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseArtworkProvider).detach();
        if(phaseCastMemberProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseCastMemberProvider).detach();
        if(phaseMetadataElementProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseMetadataElementProvider).detach();
        if(phaseRequiredImageTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseRequiredImageTypeProvider).detach();
        if(phaseTrailerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)phaseTrailerProvider).detach();
        if(rolloutCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutCountryProvider).detach();
        if(setOfMovieExtensionOverrideProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfMovieExtensionOverrideProvider).detach();
        if(movieExtensionProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieExtensionProvider).detach();
        if(setOfPhaseArtworkProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfPhaseArtworkProvider).detach();
        if(setOfPhaseCastMemberProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfPhaseCastMemberProvider).detach();
        if(setOfPhaseMetadataElementProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfPhaseMetadataElementProvider).detach();
        if(setOfPhaseRequiredImageTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfPhaseRequiredImageTypeProvider).detach();
        if(setOfPhaseTrailerProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfPhaseTrailerProvider).detach();
        if(setOfRolloutCountryProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfRolloutCountryProvider).detach();
        if(setOfStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfStringProvider).detach();
        if(movieCountriesProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieCountriesProvider).detach();
        if(showCountryLabelOverrideProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)showCountryLabelOverrideProvider).detach();
        if(subsDubsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)subsDubsProvider).detach();
        if(subtypeStringProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)subtypeStringProvider).detach();
        if(subtypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)subtypeProvider).detach();
        if(supplementalSubtypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)supplementalSubtypeProvider).detach();
        if(movieProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieProvider).detach();
        if(titleSetupRequirementsTemplateProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)titleSetupRequirementsTemplateProvider).detach();
        if(titleSetupRequirementsProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)titleSetupRequirementsProvider).detach();
        if(titleSourceTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)titleSourceTypeProvider).detach();
        if(movieTitleAkaProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieTitleAkaProvider).detach();
        if(windowTypeProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)windowTypeProvider).detach();
        if(rolloutPhaseProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutPhaseProvider).detach();
        if(setOfRolloutPhaseProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)setOfRolloutPhaseProvider).detach();
        if(rolloutProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)rolloutProvider).detach();
        if(isOriginalTitleProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)isOriginalTitleProvider).detach();
        if(movieTitleNLSProvider instanceof HollowObjectCacheProvider)
            ((HollowObjectCacheProvider)movieTitleNLSProvider).detach();
    }

    public AttributeNameTypeAPI getAttributeNameTypeAPI() {
        return attributeNameTypeAPI;
    }
    public AttributeValueTypeAPI getAttributeValueTypeAPI() {
        return attributeValueTypeAPI;
    }
    public BcpCodeTypeAPI getBcpCodeTypeAPI() {
        return bcpCodeTypeAPI;
    }
    public CountryStringTypeAPI getCountryStringTypeAPI() {
        return countryStringTypeAPI;
    }
    public DateTypeAPI getDateTypeAPI() {
        return dateTypeAPI;
    }
    public DistributorNameTypeAPI getDistributorNameTypeAPI() {
        return distributorNameTypeAPI;
    }
    public ForceReasonTypeAPI getForceReasonTypeAPI() {
        return forceReasonTypeAPI;
    }
    public ISOCountryTypeAPI getISOCountryTypeAPI() {
        return iSOCountryTypeAPI;
    }
    public ISOCountryListTypeAPI getISOCountryListTypeAPI() {
        return iSOCountryListTypeAPI;
    }
    public ImageTypeTypeAPI getImageTypeTypeAPI() {
        return imageTypeTypeAPI;
    }
    public InteractiveTypeTypeAPI getInteractiveTypeTypeAPI() {
        return interactiveTypeTypeAPI;
    }
    public LongTypeAPI getLongTypeAPI() {
        return longTypeAPI;
    }
    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return movieIdTypeAPI;
    }
    public MovieReleaseTypeTypeAPI getMovieReleaseTypeTypeAPI() {
        return movieReleaseTypeTypeAPI;
    }
    public MovieTitleStringTypeAPI getMovieTitleStringTypeAPI() {
        return movieTitleStringTypeAPI;
    }
    public MovieTitleTypeTypeAPI getMovieTitleTypeTypeAPI() {
        return movieTitleTypeTypeAPI;
    }
    public MovieTypeTypeAPI getMovieTypeTypeAPI() {
        return movieTypeTypeAPI;
    }
    public OverrideEntityTypeTypeAPI getOverrideEntityTypeTypeAPI() {
        return overrideEntityTypeTypeAPI;
    }
    public OverrideEntityValueTypeAPI getOverrideEntityValueTypeAPI() {
        return overrideEntityValueTypeAPI;
    }
    public PersonIdTypeAPI getPersonIdTypeAPI() {
        return personIdTypeAPI;
    }
    public PersonNameTypeAPI getPersonNameTypeAPI() {
        return personNameTypeAPI;
    }
    public PhaseNameTypeAPI getPhaseNameTypeAPI() {
        return phaseNameTypeAPI;
    }
    public PhaseTypeTypeAPI getPhaseTypeTypeAPI() {
        return phaseTypeTypeAPI;
    }
    public RatingsRequirementsTypeAPI getRatingsRequirementsTypeAPI() {
        return ratingsRequirementsTypeAPI;
    }
    public RecipeGroupsTypeAPI getRecipeGroupsTypeAPI() {
        return recipeGroupsTypeAPI;
    }
    public RolloutNameTypeAPI getRolloutNameTypeAPI() {
        return rolloutNameTypeAPI;
    }
    public RolloutStatusTypeAPI getRolloutStatusTypeAPI() {
        return rolloutStatusTypeAPI;
    }
    public RolloutTypeTypeAPI getRolloutTypeTypeAPI() {
        return rolloutTypeTypeAPI;
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
    public SourceRequestDefaultFulfillmentTypeAPI getSourceRequestDefaultFulfillmentTypeAPI() {
        return sourceRequestDefaultFulfillmentTypeAPI;
    }
    public StringTypeAPI getStringTypeAPI() {
        return stringTypeAPI;
    }
    public MovieCountriesNotOriginalTypeAPI getMovieCountriesNotOriginalTypeAPI() {
        return movieCountriesNotOriginalTypeAPI;
    }
    public MovieExtensionOverrideTypeAPI getMovieExtensionOverrideTypeAPI() {
        return movieExtensionOverrideTypeAPI;
    }
    public MovieReleaseHistoryTypeAPI getMovieReleaseHistoryTypeAPI() {
        return movieReleaseHistoryTypeAPI;
    }
    public MovieSetContentLabelTypeAPI getMovieSetContentLabelTypeAPI() {
        return movieSetContentLabelTypeAPI;
    }
    public PhaseArtworkTypeAPI getPhaseArtworkTypeAPI() {
        return phaseArtworkTypeAPI;
    }
    public PhaseCastMemberTypeAPI getPhaseCastMemberTypeAPI() {
        return phaseCastMemberTypeAPI;
    }
    public PhaseMetadataElementTypeAPI getPhaseMetadataElementTypeAPI() {
        return phaseMetadataElementTypeAPI;
    }
    public PhaseRequiredImageTypeTypeAPI getPhaseRequiredImageTypeTypeAPI() {
        return phaseRequiredImageTypeTypeAPI;
    }
    public PhaseTrailerTypeAPI getPhaseTrailerTypeAPI() {
        return phaseTrailerTypeAPI;
    }
    public RolloutCountryTypeAPI getRolloutCountryTypeAPI() {
        return rolloutCountryTypeAPI;
    }
    public SetOfMovieExtensionOverrideTypeAPI getSetOfMovieExtensionOverrideTypeAPI() {
        return setOfMovieExtensionOverrideTypeAPI;
    }
    public MovieExtensionTypeAPI getMovieExtensionTypeAPI() {
        return movieExtensionTypeAPI;
    }
    public SetOfPhaseArtworkTypeAPI getSetOfPhaseArtworkTypeAPI() {
        return setOfPhaseArtworkTypeAPI;
    }
    public SetOfPhaseCastMemberTypeAPI getSetOfPhaseCastMemberTypeAPI() {
        return setOfPhaseCastMemberTypeAPI;
    }
    public SetOfPhaseMetadataElementTypeAPI getSetOfPhaseMetadataElementTypeAPI() {
        return setOfPhaseMetadataElementTypeAPI;
    }
    public SetOfPhaseRequiredImageTypeTypeAPI getSetOfPhaseRequiredImageTypeTypeAPI() {
        return setOfPhaseRequiredImageTypeTypeAPI;
    }
    public SetOfPhaseTrailerTypeAPI getSetOfPhaseTrailerTypeAPI() {
        return setOfPhaseTrailerTypeAPI;
    }
    public SetOfRolloutCountryTypeAPI getSetOfRolloutCountryTypeAPI() {
        return setOfRolloutCountryTypeAPI;
    }
    public SetOfStringTypeAPI getSetOfStringTypeAPI() {
        return setOfStringTypeAPI;
    }
    public MovieCountriesTypeAPI getMovieCountriesTypeAPI() {
        return movieCountriesTypeAPI;
    }
    public ShowCountryLabelOverrideTypeAPI getShowCountryLabelOverrideTypeAPI() {
        return showCountryLabelOverrideTypeAPI;
    }
    public SubsDubsTypeAPI getSubsDubsTypeAPI() {
        return subsDubsTypeAPI;
    }
    public SubtypeStringTypeAPI getSubtypeStringTypeAPI() {
        return subtypeStringTypeAPI;
    }
    public SubtypeTypeAPI getSubtypeTypeAPI() {
        return subtypeTypeAPI;
    }
    public SupplementalSubtypeTypeAPI getSupplementalSubtypeTypeAPI() {
        return supplementalSubtypeTypeAPI;
    }
    public MovieTypeAPI getMovieTypeAPI() {
        return movieTypeAPI;
    }
    public TitleSetupRequirementsTemplateTypeAPI getTitleSetupRequirementsTemplateTypeAPI() {
        return titleSetupRequirementsTemplateTypeAPI;
    }
    public TitleSetupRequirementsTypeAPI getTitleSetupRequirementsTypeAPI() {
        return titleSetupRequirementsTypeAPI;
    }
    public TitleSourceTypeTypeAPI getTitleSourceTypeTypeAPI() {
        return titleSourceTypeTypeAPI;
    }
    public MovieTitleAkaTypeAPI getMovieTitleAkaTypeAPI() {
        return movieTitleAkaTypeAPI;
    }
    public WindowTypeTypeAPI getWindowTypeTypeAPI() {
        return windowTypeTypeAPI;
    }
    public RolloutPhaseTypeAPI getRolloutPhaseTypeAPI() {
        return rolloutPhaseTypeAPI;
    }
    public SetOfRolloutPhaseTypeAPI getSetOfRolloutPhaseTypeAPI() {
        return setOfRolloutPhaseTypeAPI;
    }
    public RolloutTypeAPI getRolloutTypeAPI() {
        return rolloutTypeAPI;
    }
    public IsOriginalTitleTypeAPI getIsOriginalTitleTypeAPI() {
        return isOriginalTitleTypeAPI;
    }
    public MovieTitleNLSTypeAPI getMovieTitleNLSTypeAPI() {
        return movieTitleNLSTypeAPI;
    }
    public Collection<AttributeName> getAllAttributeName() {
        return new AllHollowRecordCollection<AttributeName>(getDataAccess().getTypeDataAccess("AttributeName").getTypeState()) {
            protected AttributeName getForOrdinal(int ordinal) {
                return getAttributeName(ordinal);
            }
        };
    }
    public AttributeName getAttributeName(int ordinal) {
        objectCreationSampler.recordCreation(0);
        return (AttributeName)attributeNameProvider.getHollowObject(ordinal);
    }
    public Collection<AttributeValue> getAllAttributeValue() {
        return new AllHollowRecordCollection<AttributeValue>(getDataAccess().getTypeDataAccess("AttributeValue").getTypeState()) {
            protected AttributeValue getForOrdinal(int ordinal) {
                return getAttributeValue(ordinal);
            }
        };
    }
    public AttributeValue getAttributeValue(int ordinal) {
        objectCreationSampler.recordCreation(1);
        return (AttributeValue)attributeValueProvider.getHollowObject(ordinal);
    }
    public Collection<BcpCode> getAllBcpCode() {
        return new AllHollowRecordCollection<BcpCode>(getDataAccess().getTypeDataAccess("BcpCode").getTypeState()) {
            protected BcpCode getForOrdinal(int ordinal) {
                return getBcpCode(ordinal);
            }
        };
    }
    public BcpCode getBcpCode(int ordinal) {
        objectCreationSampler.recordCreation(2);
        return (BcpCode)bcpCodeProvider.getHollowObject(ordinal);
    }
    public Collection<CountryString> getAllCountryString() {
        return new AllHollowRecordCollection<CountryString>(getDataAccess().getTypeDataAccess("CountryString").getTypeState()) {
            protected CountryString getForOrdinal(int ordinal) {
                return getCountryString(ordinal);
            }
        };
    }
    public CountryString getCountryString(int ordinal) {
        objectCreationSampler.recordCreation(3);
        return (CountryString)countryStringProvider.getHollowObject(ordinal);
    }
    public Collection<Date> getAllDate() {
        return new AllHollowRecordCollection<Date>(getDataAccess().getTypeDataAccess("Date").getTypeState()) {
            protected Date getForOrdinal(int ordinal) {
                return getDate(ordinal);
            }
        };
    }
    public Date getDate(int ordinal) {
        objectCreationSampler.recordCreation(4);
        return (Date)dateProvider.getHollowObject(ordinal);
    }
    public Collection<DistributorName> getAllDistributorName() {
        return new AllHollowRecordCollection<DistributorName>(getDataAccess().getTypeDataAccess("DistributorName").getTypeState()) {
            protected DistributorName getForOrdinal(int ordinal) {
                return getDistributorName(ordinal);
            }
        };
    }
    public DistributorName getDistributorName(int ordinal) {
        objectCreationSampler.recordCreation(5);
        return (DistributorName)distributorNameProvider.getHollowObject(ordinal);
    }
    public Collection<ForceReason> getAllForceReason() {
        return new AllHollowRecordCollection<ForceReason>(getDataAccess().getTypeDataAccess("ForceReason").getTypeState()) {
            protected ForceReason getForOrdinal(int ordinal) {
                return getForceReason(ordinal);
            }
        };
    }
    public ForceReason getForceReason(int ordinal) {
        objectCreationSampler.recordCreation(6);
        return (ForceReason)forceReasonProvider.getHollowObject(ordinal);
    }
    public Collection<ISOCountry> getAllISOCountry() {
        return new AllHollowRecordCollection<ISOCountry>(getDataAccess().getTypeDataAccess("ISOCountry").getTypeState()) {
            protected ISOCountry getForOrdinal(int ordinal) {
                return getISOCountry(ordinal);
            }
        };
    }
    public ISOCountry getISOCountry(int ordinal) {
        objectCreationSampler.recordCreation(7);
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
        objectCreationSampler.recordCreation(8);
        return (ISOCountryList)iSOCountryListProvider.getHollowObject(ordinal);
    }
    public Collection<ImageType> getAllImageType() {
        return new AllHollowRecordCollection<ImageType>(getDataAccess().getTypeDataAccess("ImageType").getTypeState()) {
            protected ImageType getForOrdinal(int ordinal) {
                return getImageType(ordinal);
            }
        };
    }
    public ImageType getImageType(int ordinal) {
        objectCreationSampler.recordCreation(9);
        return (ImageType)imageTypeProvider.getHollowObject(ordinal);
    }
    public Collection<InteractiveType> getAllInteractiveType() {
        return new AllHollowRecordCollection<InteractiveType>(getDataAccess().getTypeDataAccess("InteractiveType").getTypeState()) {
            protected InteractiveType getForOrdinal(int ordinal) {
                return getInteractiveType(ordinal);
            }
        };
    }
    public InteractiveType getInteractiveType(int ordinal) {
        objectCreationSampler.recordCreation(10);
        return (InteractiveType)interactiveTypeProvider.getHollowObject(ordinal);
    }
    public Collection<HLong> getAllHLong() {
        return new AllHollowRecordCollection<HLong>(getDataAccess().getTypeDataAccess("Long").getTypeState()) {
            protected HLong getForOrdinal(int ordinal) {
                return getHLong(ordinal);
            }
        };
    }
    public HLong getHLong(int ordinal) {
        objectCreationSampler.recordCreation(11);
        return (HLong)longProvider.getHollowObject(ordinal);
    }
    public Collection<MovieId> getAllMovieId() {
        return new AllHollowRecordCollection<MovieId>(getDataAccess().getTypeDataAccess("MovieId").getTypeState()) {
            protected MovieId getForOrdinal(int ordinal) {
                return getMovieId(ordinal);
            }
        };
    }
    public MovieId getMovieId(int ordinal) {
        objectCreationSampler.recordCreation(12);
        return (MovieId)movieIdProvider.getHollowObject(ordinal);
    }
    public Collection<MovieReleaseType> getAllMovieReleaseType() {
        return new AllHollowRecordCollection<MovieReleaseType>(getDataAccess().getTypeDataAccess("MovieReleaseType").getTypeState()) {
            protected MovieReleaseType getForOrdinal(int ordinal) {
                return getMovieReleaseType(ordinal);
            }
        };
    }
    public MovieReleaseType getMovieReleaseType(int ordinal) {
        objectCreationSampler.recordCreation(13);
        return (MovieReleaseType)movieReleaseTypeProvider.getHollowObject(ordinal);
    }
    public Collection<MovieTitleString> getAllMovieTitleString() {
        return new AllHollowRecordCollection<MovieTitleString>(getDataAccess().getTypeDataAccess("MovieTitleString").getTypeState()) {
            protected MovieTitleString getForOrdinal(int ordinal) {
                return getMovieTitleString(ordinal);
            }
        };
    }
    public MovieTitleString getMovieTitleString(int ordinal) {
        objectCreationSampler.recordCreation(14);
        return (MovieTitleString)movieTitleStringProvider.getHollowObject(ordinal);
    }
    public Collection<MovieTitleType> getAllMovieTitleType() {
        return new AllHollowRecordCollection<MovieTitleType>(getDataAccess().getTypeDataAccess("MovieTitleType").getTypeState()) {
            protected MovieTitleType getForOrdinal(int ordinal) {
                return getMovieTitleType(ordinal);
            }
        };
    }
    public MovieTitleType getMovieTitleType(int ordinal) {
        objectCreationSampler.recordCreation(15);
        return (MovieTitleType)movieTitleTypeProvider.getHollowObject(ordinal);
    }
    public Collection<MovieType> getAllMovieType() {
        return new AllHollowRecordCollection<MovieType>(getDataAccess().getTypeDataAccess("MovieType").getTypeState()) {
            protected MovieType getForOrdinal(int ordinal) {
                return getMovieType(ordinal);
            }
        };
    }
    public MovieType getMovieType(int ordinal) {
        objectCreationSampler.recordCreation(16);
        return (MovieType)movieTypeProvider.getHollowObject(ordinal);
    }
    public Collection<OverrideEntityType> getAllOverrideEntityType() {
        return new AllHollowRecordCollection<OverrideEntityType>(getDataAccess().getTypeDataAccess("OverrideEntityType").getTypeState()) {
            protected OverrideEntityType getForOrdinal(int ordinal) {
                return getOverrideEntityType(ordinal);
            }
        };
    }
    public OverrideEntityType getOverrideEntityType(int ordinal) {
        objectCreationSampler.recordCreation(17);
        return (OverrideEntityType)overrideEntityTypeProvider.getHollowObject(ordinal);
    }
    public Collection<OverrideEntityValue> getAllOverrideEntityValue() {
        return new AllHollowRecordCollection<OverrideEntityValue>(getDataAccess().getTypeDataAccess("OverrideEntityValue").getTypeState()) {
            protected OverrideEntityValue getForOrdinal(int ordinal) {
                return getOverrideEntityValue(ordinal);
            }
        };
    }
    public OverrideEntityValue getOverrideEntityValue(int ordinal) {
        objectCreationSampler.recordCreation(18);
        return (OverrideEntityValue)overrideEntityValueProvider.getHollowObject(ordinal);
    }
    public Collection<PersonId> getAllPersonId() {
        return new AllHollowRecordCollection<PersonId>(getDataAccess().getTypeDataAccess("PersonId").getTypeState()) {
            protected PersonId getForOrdinal(int ordinal) {
                return getPersonId(ordinal);
            }
        };
    }
    public PersonId getPersonId(int ordinal) {
        objectCreationSampler.recordCreation(19);
        return (PersonId)personIdProvider.getHollowObject(ordinal);
    }
    public Collection<PersonName> getAllPersonName() {
        return new AllHollowRecordCollection<PersonName>(getDataAccess().getTypeDataAccess("PersonName").getTypeState()) {
            protected PersonName getForOrdinal(int ordinal) {
                return getPersonName(ordinal);
            }
        };
    }
    public PersonName getPersonName(int ordinal) {
        objectCreationSampler.recordCreation(20);
        return (PersonName)personNameProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseName> getAllPhaseName() {
        return new AllHollowRecordCollection<PhaseName>(getDataAccess().getTypeDataAccess("PhaseName").getTypeState()) {
            protected PhaseName getForOrdinal(int ordinal) {
                return getPhaseName(ordinal);
            }
        };
    }
    public PhaseName getPhaseName(int ordinal) {
        objectCreationSampler.recordCreation(21);
        return (PhaseName)phaseNameProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseType> getAllPhaseType() {
        return new AllHollowRecordCollection<PhaseType>(getDataAccess().getTypeDataAccess("PhaseType").getTypeState()) {
            protected PhaseType getForOrdinal(int ordinal) {
                return getPhaseType(ordinal);
            }
        };
    }
    public PhaseType getPhaseType(int ordinal) {
        objectCreationSampler.recordCreation(22);
        return (PhaseType)phaseTypeProvider.getHollowObject(ordinal);
    }
    public Collection<RatingsRequirements> getAllRatingsRequirements() {
        return new AllHollowRecordCollection<RatingsRequirements>(getDataAccess().getTypeDataAccess("RatingsRequirements").getTypeState()) {
            protected RatingsRequirements getForOrdinal(int ordinal) {
                return getRatingsRequirements(ordinal);
            }
        };
    }
    public RatingsRequirements getRatingsRequirements(int ordinal) {
        objectCreationSampler.recordCreation(23);
        return (RatingsRequirements)ratingsRequirementsProvider.getHollowObject(ordinal);
    }
    public Collection<RecipeGroups> getAllRecipeGroups() {
        return new AllHollowRecordCollection<RecipeGroups>(getDataAccess().getTypeDataAccess("RecipeGroups").getTypeState()) {
            protected RecipeGroups getForOrdinal(int ordinal) {
                return getRecipeGroups(ordinal);
            }
        };
    }
    public RecipeGroups getRecipeGroups(int ordinal) {
        objectCreationSampler.recordCreation(24);
        return (RecipeGroups)recipeGroupsProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutName> getAllRolloutName() {
        return new AllHollowRecordCollection<RolloutName>(getDataAccess().getTypeDataAccess("RolloutName").getTypeState()) {
            protected RolloutName getForOrdinal(int ordinal) {
                return getRolloutName(ordinal);
            }
        };
    }
    public RolloutName getRolloutName(int ordinal) {
        objectCreationSampler.recordCreation(25);
        return (RolloutName)rolloutNameProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutStatus> getAllRolloutStatus() {
        return new AllHollowRecordCollection<RolloutStatus>(getDataAccess().getTypeDataAccess("RolloutStatus").getTypeState()) {
            protected RolloutStatus getForOrdinal(int ordinal) {
                return getRolloutStatus(ordinal);
            }
        };
    }
    public RolloutStatus getRolloutStatus(int ordinal) {
        objectCreationSampler.recordCreation(26);
        return (RolloutStatus)rolloutStatusProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutType> getAllRolloutType() {
        return new AllHollowRecordCollection<RolloutType>(getDataAccess().getTypeDataAccess("RolloutType").getTypeState()) {
            protected RolloutType getForOrdinal(int ordinal) {
                return getRolloutType(ordinal);
            }
        };
    }
    public RolloutType getRolloutType(int ordinal) {
        objectCreationSampler.recordCreation(27);
        return (RolloutType)rolloutTypeProvider.getHollowObject(ordinal);
    }
    public Collection<ShowMemberType> getAllShowMemberType() {
        return new AllHollowRecordCollection<ShowMemberType>(getDataAccess().getTypeDataAccess("ShowMemberType").getTypeState()) {
            protected ShowMemberType getForOrdinal(int ordinal) {
                return getShowMemberType(ordinal);
            }
        };
    }
    public ShowMemberType getShowMemberType(int ordinal) {
        objectCreationSampler.recordCreation(28);
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
        objectCreationSampler.recordCreation(29);
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
        objectCreationSampler.recordCreation(30);
        return (ShowCountryLabel)showCountryLabelProvider.getHollowObject(ordinal);
    }
    public Collection<SourceRequestDefaultFulfillment> getAllSourceRequestDefaultFulfillment() {
        return new AllHollowRecordCollection<SourceRequestDefaultFulfillment>(getDataAccess().getTypeDataAccess("SourceRequestDefaultFulfillment").getTypeState()) {
            protected SourceRequestDefaultFulfillment getForOrdinal(int ordinal) {
                return getSourceRequestDefaultFulfillment(ordinal);
            }
        };
    }
    public SourceRequestDefaultFulfillment getSourceRequestDefaultFulfillment(int ordinal) {
        objectCreationSampler.recordCreation(31);
        return (SourceRequestDefaultFulfillment)sourceRequestDefaultFulfillmentProvider.getHollowObject(ordinal);
    }
    public Collection<HString> getAllHString() {
        return new AllHollowRecordCollection<HString>(getDataAccess().getTypeDataAccess("String").getTypeState()) {
            protected HString getForOrdinal(int ordinal) {
                return getHString(ordinal);
            }
        };
    }
    public HString getHString(int ordinal) {
        objectCreationSampler.recordCreation(32);
        return (HString)stringProvider.getHollowObject(ordinal);
    }
    public Collection<MovieCountriesNotOriginal> getAllMovieCountriesNotOriginal() {
        return new AllHollowRecordCollection<MovieCountriesNotOriginal>(getDataAccess().getTypeDataAccess("MovieCountriesNotOriginal").getTypeState()) {
            protected MovieCountriesNotOriginal getForOrdinal(int ordinal) {
                return getMovieCountriesNotOriginal(ordinal);
            }
        };
    }
    public MovieCountriesNotOriginal getMovieCountriesNotOriginal(int ordinal) {
        objectCreationSampler.recordCreation(33);
        return (MovieCountriesNotOriginal)movieCountriesNotOriginalProvider.getHollowObject(ordinal);
    }
    public Collection<MovieExtensionOverride> getAllMovieExtensionOverride() {
        return new AllHollowRecordCollection<MovieExtensionOverride>(getDataAccess().getTypeDataAccess("MovieExtensionOverride").getTypeState()) {
            protected MovieExtensionOverride getForOrdinal(int ordinal) {
                return getMovieExtensionOverride(ordinal);
            }
        };
    }
    public MovieExtensionOverride getMovieExtensionOverride(int ordinal) {
        objectCreationSampler.recordCreation(34);
        return (MovieExtensionOverride)movieExtensionOverrideProvider.getHollowObject(ordinal);
    }
    public Collection<MovieReleaseHistory> getAllMovieReleaseHistory() {
        return new AllHollowRecordCollection<MovieReleaseHistory>(getDataAccess().getTypeDataAccess("MovieReleaseHistory").getTypeState()) {
            protected MovieReleaseHistory getForOrdinal(int ordinal) {
                return getMovieReleaseHistory(ordinal);
            }
        };
    }
    public MovieReleaseHistory getMovieReleaseHistory(int ordinal) {
        objectCreationSampler.recordCreation(35);
        return (MovieReleaseHistory)movieReleaseHistoryProvider.getHollowObject(ordinal);
    }
    public Collection<MovieSetContentLabel> getAllMovieSetContentLabel() {
        return new AllHollowRecordCollection<MovieSetContentLabel>(getDataAccess().getTypeDataAccess("MovieSetContentLabel").getTypeState()) {
            protected MovieSetContentLabel getForOrdinal(int ordinal) {
                return getMovieSetContentLabel(ordinal);
            }
        };
    }
    public MovieSetContentLabel getMovieSetContentLabel(int ordinal) {
        objectCreationSampler.recordCreation(36);
        return (MovieSetContentLabel)movieSetContentLabelProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseArtwork> getAllPhaseArtwork() {
        return new AllHollowRecordCollection<PhaseArtwork>(getDataAccess().getTypeDataAccess("PhaseArtwork").getTypeState()) {
            protected PhaseArtwork getForOrdinal(int ordinal) {
                return getPhaseArtwork(ordinal);
            }
        };
    }
    public PhaseArtwork getPhaseArtwork(int ordinal) {
        objectCreationSampler.recordCreation(37);
        return (PhaseArtwork)phaseArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseCastMember> getAllPhaseCastMember() {
        return new AllHollowRecordCollection<PhaseCastMember>(getDataAccess().getTypeDataAccess("PhaseCastMember").getTypeState()) {
            protected PhaseCastMember getForOrdinal(int ordinal) {
                return getPhaseCastMember(ordinal);
            }
        };
    }
    public PhaseCastMember getPhaseCastMember(int ordinal) {
        objectCreationSampler.recordCreation(38);
        return (PhaseCastMember)phaseCastMemberProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseMetadataElement> getAllPhaseMetadataElement() {
        return new AllHollowRecordCollection<PhaseMetadataElement>(getDataAccess().getTypeDataAccess("PhaseMetadataElement").getTypeState()) {
            protected PhaseMetadataElement getForOrdinal(int ordinal) {
                return getPhaseMetadataElement(ordinal);
            }
        };
    }
    public PhaseMetadataElement getPhaseMetadataElement(int ordinal) {
        objectCreationSampler.recordCreation(39);
        return (PhaseMetadataElement)phaseMetadataElementProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseRequiredImageType> getAllPhaseRequiredImageType() {
        return new AllHollowRecordCollection<PhaseRequiredImageType>(getDataAccess().getTypeDataAccess("PhaseRequiredImageType").getTypeState()) {
            protected PhaseRequiredImageType getForOrdinal(int ordinal) {
                return getPhaseRequiredImageType(ordinal);
            }
        };
    }
    public PhaseRequiredImageType getPhaseRequiredImageType(int ordinal) {
        objectCreationSampler.recordCreation(40);
        return (PhaseRequiredImageType)phaseRequiredImageTypeProvider.getHollowObject(ordinal);
    }
    public Collection<PhaseTrailer> getAllPhaseTrailer() {
        return new AllHollowRecordCollection<PhaseTrailer>(getDataAccess().getTypeDataAccess("PhaseTrailer").getTypeState()) {
            protected PhaseTrailer getForOrdinal(int ordinal) {
                return getPhaseTrailer(ordinal);
            }
        };
    }
    public PhaseTrailer getPhaseTrailer(int ordinal) {
        objectCreationSampler.recordCreation(41);
        return (PhaseTrailer)phaseTrailerProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutCountry> getAllRolloutCountry() {
        return new AllHollowRecordCollection<RolloutCountry>(getDataAccess().getTypeDataAccess("RolloutCountry").getTypeState()) {
            protected RolloutCountry getForOrdinal(int ordinal) {
                return getRolloutCountry(ordinal);
            }
        };
    }
    public RolloutCountry getRolloutCountry(int ordinal) {
        objectCreationSampler.recordCreation(42);
        return (RolloutCountry)rolloutCountryProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfMovieExtensionOverride> getAllSetOfMovieExtensionOverride() {
        return new AllHollowRecordCollection<SetOfMovieExtensionOverride>(getDataAccess().getTypeDataAccess("SetOfMovieExtensionOverride").getTypeState()) {
            protected SetOfMovieExtensionOverride getForOrdinal(int ordinal) {
                return getSetOfMovieExtensionOverride(ordinal);
            }
        };
    }
    public SetOfMovieExtensionOverride getSetOfMovieExtensionOverride(int ordinal) {
        objectCreationSampler.recordCreation(43);
        return (SetOfMovieExtensionOverride)setOfMovieExtensionOverrideProvider.getHollowObject(ordinal);
    }
    public Collection<MovieExtension> getAllMovieExtension() {
        return new AllHollowRecordCollection<MovieExtension>(getDataAccess().getTypeDataAccess("MovieExtension").getTypeState()) {
            protected MovieExtension getForOrdinal(int ordinal) {
                return getMovieExtension(ordinal);
            }
        };
    }
    public MovieExtension getMovieExtension(int ordinal) {
        objectCreationSampler.recordCreation(44);
        return (MovieExtension)movieExtensionProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfPhaseArtwork> getAllSetOfPhaseArtwork() {
        return new AllHollowRecordCollection<SetOfPhaseArtwork>(getDataAccess().getTypeDataAccess("SetOfPhaseArtwork").getTypeState()) {
            protected SetOfPhaseArtwork getForOrdinal(int ordinal) {
                return getSetOfPhaseArtwork(ordinal);
            }
        };
    }
    public SetOfPhaseArtwork getSetOfPhaseArtwork(int ordinal) {
        objectCreationSampler.recordCreation(45);
        return (SetOfPhaseArtwork)setOfPhaseArtworkProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfPhaseCastMember> getAllSetOfPhaseCastMember() {
        return new AllHollowRecordCollection<SetOfPhaseCastMember>(getDataAccess().getTypeDataAccess("SetOfPhaseCastMember").getTypeState()) {
            protected SetOfPhaseCastMember getForOrdinal(int ordinal) {
                return getSetOfPhaseCastMember(ordinal);
            }
        };
    }
    public SetOfPhaseCastMember getSetOfPhaseCastMember(int ordinal) {
        objectCreationSampler.recordCreation(46);
        return (SetOfPhaseCastMember)setOfPhaseCastMemberProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfPhaseMetadataElement> getAllSetOfPhaseMetadataElement() {
        return new AllHollowRecordCollection<SetOfPhaseMetadataElement>(getDataAccess().getTypeDataAccess("SetOfPhaseMetadataElement").getTypeState()) {
            protected SetOfPhaseMetadataElement getForOrdinal(int ordinal) {
                return getSetOfPhaseMetadataElement(ordinal);
            }
        };
    }
    public SetOfPhaseMetadataElement getSetOfPhaseMetadataElement(int ordinal) {
        objectCreationSampler.recordCreation(47);
        return (SetOfPhaseMetadataElement)setOfPhaseMetadataElementProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfPhaseRequiredImageType> getAllSetOfPhaseRequiredImageType() {
        return new AllHollowRecordCollection<SetOfPhaseRequiredImageType>(getDataAccess().getTypeDataAccess("SetOfPhaseRequiredImageType").getTypeState()) {
            protected SetOfPhaseRequiredImageType getForOrdinal(int ordinal) {
                return getSetOfPhaseRequiredImageType(ordinal);
            }
        };
    }
    public SetOfPhaseRequiredImageType getSetOfPhaseRequiredImageType(int ordinal) {
        objectCreationSampler.recordCreation(48);
        return (SetOfPhaseRequiredImageType)setOfPhaseRequiredImageTypeProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfPhaseTrailer> getAllSetOfPhaseTrailer() {
        return new AllHollowRecordCollection<SetOfPhaseTrailer>(getDataAccess().getTypeDataAccess("SetOfPhaseTrailer").getTypeState()) {
            protected SetOfPhaseTrailer getForOrdinal(int ordinal) {
                return getSetOfPhaseTrailer(ordinal);
            }
        };
    }
    public SetOfPhaseTrailer getSetOfPhaseTrailer(int ordinal) {
        objectCreationSampler.recordCreation(49);
        return (SetOfPhaseTrailer)setOfPhaseTrailerProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfRolloutCountry> getAllSetOfRolloutCountry() {
        return new AllHollowRecordCollection<SetOfRolloutCountry>(getDataAccess().getTypeDataAccess("SetOfRolloutCountry").getTypeState()) {
            protected SetOfRolloutCountry getForOrdinal(int ordinal) {
                return getSetOfRolloutCountry(ordinal);
            }
        };
    }
    public SetOfRolloutCountry getSetOfRolloutCountry(int ordinal) {
        objectCreationSampler.recordCreation(50);
        return (SetOfRolloutCountry)setOfRolloutCountryProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfString> getAllSetOfString() {
        return new AllHollowRecordCollection<SetOfString>(getDataAccess().getTypeDataAccess("SetOfString").getTypeState()) {
            protected SetOfString getForOrdinal(int ordinal) {
                return getSetOfString(ordinal);
            }
        };
    }
    public SetOfString getSetOfString(int ordinal) {
        objectCreationSampler.recordCreation(51);
        return (SetOfString)setOfStringProvider.getHollowObject(ordinal);
    }
    public Collection<MovieCountries> getAllMovieCountries() {
        return new AllHollowRecordCollection<MovieCountries>(getDataAccess().getTypeDataAccess("MovieCountries").getTypeState()) {
            protected MovieCountries getForOrdinal(int ordinal) {
                return getMovieCountries(ordinal);
            }
        };
    }
    public MovieCountries getMovieCountries(int ordinal) {
        objectCreationSampler.recordCreation(52);
        return (MovieCountries)movieCountriesProvider.getHollowObject(ordinal);
    }
    public Collection<ShowCountryLabelOverride> getAllShowCountryLabelOverride() {
        return new AllHollowRecordCollection<ShowCountryLabelOverride>(getDataAccess().getTypeDataAccess("ShowCountryLabelOverride").getTypeState()) {
            protected ShowCountryLabelOverride getForOrdinal(int ordinal) {
                return getShowCountryLabelOverride(ordinal);
            }
        };
    }
    public ShowCountryLabelOverride getShowCountryLabelOverride(int ordinal) {
        objectCreationSampler.recordCreation(53);
        return (ShowCountryLabelOverride)showCountryLabelOverrideProvider.getHollowObject(ordinal);
    }
    public Collection<SubsDubs> getAllSubsDubs() {
        return new AllHollowRecordCollection<SubsDubs>(getDataAccess().getTypeDataAccess("SubsDubs").getTypeState()) {
            protected SubsDubs getForOrdinal(int ordinal) {
                return getSubsDubs(ordinal);
            }
        };
    }
    public SubsDubs getSubsDubs(int ordinal) {
        objectCreationSampler.recordCreation(54);
        return (SubsDubs)subsDubsProvider.getHollowObject(ordinal);
    }
    public Collection<SubtypeString> getAllSubtypeString() {
        return new AllHollowRecordCollection<SubtypeString>(getDataAccess().getTypeDataAccess("SubtypeString").getTypeState()) {
            protected SubtypeString getForOrdinal(int ordinal) {
                return getSubtypeString(ordinal);
            }
        };
    }
    public SubtypeString getSubtypeString(int ordinal) {
        objectCreationSampler.recordCreation(55);
        return (SubtypeString)subtypeStringProvider.getHollowObject(ordinal);
    }
    public Collection<Subtype> getAllSubtype() {
        return new AllHollowRecordCollection<Subtype>(getDataAccess().getTypeDataAccess("Subtype").getTypeState()) {
            protected Subtype getForOrdinal(int ordinal) {
                return getSubtype(ordinal);
            }
        };
    }
    public Subtype getSubtype(int ordinal) {
        objectCreationSampler.recordCreation(56);
        return (Subtype)subtypeProvider.getHollowObject(ordinal);
    }
    public Collection<SupplementalSubtype> getAllSupplementalSubtype() {
        return new AllHollowRecordCollection<SupplementalSubtype>(getDataAccess().getTypeDataAccess("SupplementalSubtype").getTypeState()) {
            protected SupplementalSubtype getForOrdinal(int ordinal) {
                return getSupplementalSubtype(ordinal);
            }
        };
    }
    public SupplementalSubtype getSupplementalSubtype(int ordinal) {
        objectCreationSampler.recordCreation(57);
        return (SupplementalSubtype)supplementalSubtypeProvider.getHollowObject(ordinal);
    }
    public Collection<Movie> getAllMovie() {
        return new AllHollowRecordCollection<Movie>(getDataAccess().getTypeDataAccess("Movie").getTypeState()) {
            protected Movie getForOrdinal(int ordinal) {
                return getMovie(ordinal);
            }
        };
    }
    public Movie getMovie(int ordinal) {
        objectCreationSampler.recordCreation(58);
        return (Movie)movieProvider.getHollowObject(ordinal);
    }
    public Collection<TitleSetupRequirementsTemplate> getAllTitleSetupRequirementsTemplate() {
        return new AllHollowRecordCollection<TitleSetupRequirementsTemplate>(getDataAccess().getTypeDataAccess("TitleSetupRequirementsTemplate").getTypeState()) {
            protected TitleSetupRequirementsTemplate getForOrdinal(int ordinal) {
                return getTitleSetupRequirementsTemplate(ordinal);
            }
        };
    }
    public TitleSetupRequirementsTemplate getTitleSetupRequirementsTemplate(int ordinal) {
        objectCreationSampler.recordCreation(59);
        return (TitleSetupRequirementsTemplate)titleSetupRequirementsTemplateProvider.getHollowObject(ordinal);
    }
    public Collection<TitleSetupRequirements> getAllTitleSetupRequirements() {
        return new AllHollowRecordCollection<TitleSetupRequirements>(getDataAccess().getTypeDataAccess("TitleSetupRequirements").getTypeState()) {
            protected TitleSetupRequirements getForOrdinal(int ordinal) {
                return getTitleSetupRequirements(ordinal);
            }
        };
    }
    public TitleSetupRequirements getTitleSetupRequirements(int ordinal) {
        objectCreationSampler.recordCreation(60);
        return (TitleSetupRequirements)titleSetupRequirementsProvider.getHollowObject(ordinal);
    }
    public Collection<TitleSourceType> getAllTitleSourceType() {
        return new AllHollowRecordCollection<TitleSourceType>(getDataAccess().getTypeDataAccess("TitleSourceType").getTypeState()) {
            protected TitleSourceType getForOrdinal(int ordinal) {
                return getTitleSourceType(ordinal);
            }
        };
    }
    public TitleSourceType getTitleSourceType(int ordinal) {
        objectCreationSampler.recordCreation(61);
        return (TitleSourceType)titleSourceTypeProvider.getHollowObject(ordinal);
    }
    public Collection<MovieTitleAka> getAllMovieTitleAka() {
        return new AllHollowRecordCollection<MovieTitleAka>(getDataAccess().getTypeDataAccess("MovieTitleAka").getTypeState()) {
            protected MovieTitleAka getForOrdinal(int ordinal) {
                return getMovieTitleAka(ordinal);
            }
        };
    }
    public MovieTitleAka getMovieTitleAka(int ordinal) {
        objectCreationSampler.recordCreation(62);
        return (MovieTitleAka)movieTitleAkaProvider.getHollowObject(ordinal);
    }
    public Collection<WindowType> getAllWindowType() {
        return new AllHollowRecordCollection<WindowType>(getDataAccess().getTypeDataAccess("WindowType").getTypeState()) {
            protected WindowType getForOrdinal(int ordinal) {
                return getWindowType(ordinal);
            }
        };
    }
    public WindowType getWindowType(int ordinal) {
        objectCreationSampler.recordCreation(63);
        return (WindowType)windowTypeProvider.getHollowObject(ordinal);
    }
    public Collection<RolloutPhase> getAllRolloutPhase() {
        return new AllHollowRecordCollection<RolloutPhase>(getDataAccess().getTypeDataAccess("RolloutPhase").getTypeState()) {
            protected RolloutPhase getForOrdinal(int ordinal) {
                return getRolloutPhase(ordinal);
            }
        };
    }
    public RolloutPhase getRolloutPhase(int ordinal) {
        objectCreationSampler.recordCreation(64);
        return (RolloutPhase)rolloutPhaseProvider.getHollowObject(ordinal);
    }
    public Collection<SetOfRolloutPhase> getAllSetOfRolloutPhase() {
        return new AllHollowRecordCollection<SetOfRolloutPhase>(getDataAccess().getTypeDataAccess("SetOfRolloutPhase").getTypeState()) {
            protected SetOfRolloutPhase getForOrdinal(int ordinal) {
                return getSetOfRolloutPhase(ordinal);
            }
        };
    }
    public SetOfRolloutPhase getSetOfRolloutPhase(int ordinal) {
        objectCreationSampler.recordCreation(65);
        return (SetOfRolloutPhase)setOfRolloutPhaseProvider.getHollowObject(ordinal);
    }
    public Collection<Rollout> getAllRollout() {
        return new AllHollowRecordCollection<Rollout>(getDataAccess().getTypeDataAccess("Rollout").getTypeState()) {
            protected Rollout getForOrdinal(int ordinal) {
                return getRollout(ordinal);
            }
        };
    }
    public Rollout getRollout(int ordinal) {
        objectCreationSampler.recordCreation(66);
        return (Rollout)rolloutProvider.getHollowObject(ordinal);
    }
    public Collection<IsOriginalTitle> getAllIsOriginalTitle() {
        return new AllHollowRecordCollection<IsOriginalTitle>(getDataAccess().getTypeDataAccess("isOriginalTitle").getTypeState()) {
            protected IsOriginalTitle getForOrdinal(int ordinal) {
                return getIsOriginalTitle(ordinal);
            }
        };
    }
    public IsOriginalTitle getIsOriginalTitle(int ordinal) {
        objectCreationSampler.recordCreation(67);
        return (IsOriginalTitle)isOriginalTitleProvider.getHollowObject(ordinal);
    }
    public Collection<MovieTitleNLS> getAllMovieTitleNLS() {
        return new AllHollowRecordCollection<MovieTitleNLS>(getDataAccess().getTypeDataAccess("MovieTitleNLS").getTypeState()) {
            protected MovieTitleNLS getForOrdinal(int ordinal) {
                return getMovieTitleNLS(ordinal);
            }
        };
    }
    public MovieTitleNLS getMovieTitleNLS(int ordinal) {
        objectCreationSampler.recordCreation(68);
        return (MovieTitleNLS)movieTitleNLSProvider.getHollowObject(ordinal);
    }
    public void setSamplingDirector(HollowSamplingDirector director) {
        super.setSamplingDirector(director);
        objectCreationSampler.setSamplingDirector(director);
    }

    public Collection<SampleResult> getObjectCreationSamplingResults() {
        return objectCreationSampler.getSampleResults();
    }

}
