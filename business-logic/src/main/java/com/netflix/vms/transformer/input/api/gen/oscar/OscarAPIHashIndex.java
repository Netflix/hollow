package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;
import java.lang.Iterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<AttributeName, K> uki = HashIndex.from(consumer, AttributeName.class)
 *         .usingBean(k);
 *     Stream<AttributeName> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code AttributeName} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class OscarAPIHashIndex extends AbstractHollowHashIndex<OscarAPI> {

    public OscarAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public OscarAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<AttributeName> findAttributeNameMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AttributeName>(matches.iterator()) {
            public AttributeName getData(int ordinal) {
                return api.getAttributeName(ordinal);
            }
        };
    }

    public Iterable<AttributeValue> findAttributeValueMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AttributeValue>(matches.iterator()) {
            public AttributeValue getData(int ordinal) {
                return api.getAttributeValue(ordinal);
            }
        };
    }

    public Iterable<BcpCode> findBcpCodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<BcpCode>(matches.iterator()) {
            public BcpCode getData(int ordinal) {
                return api.getBcpCode(ordinal);
            }
        };
    }

    public Iterable<CountryString> findCountryStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CountryString>(matches.iterator()) {
            public CountryString getData(int ordinal) {
                return api.getCountryString(ordinal);
            }
        };
    }

    public Iterable<Date> findDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Date>(matches.iterator()) {
            public Date getData(int ordinal) {
                return api.getDate(ordinal);
            }
        };
    }

    public Iterable<DistributorName> findDistributorNameMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DistributorName>(matches.iterator()) {
            public DistributorName getData(int ordinal) {
                return api.getDistributorName(ordinal);
            }
        };
    }

    public Iterable<ForceReason> findForceReasonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ForceReason>(matches.iterator()) {
            public ForceReason getData(int ordinal) {
                return api.getForceReason(ordinal);
            }
        };
    }

    public Iterable<ISOCountry> findISOCountryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountry>(matches.iterator()) {
            public ISOCountry getData(int ordinal) {
                return api.getISOCountry(ordinal);
            }
        };
    }

    public Iterable<ISOCountryList> findISOCountryListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountryList>(matches.iterator()) {
            public ISOCountryList getData(int ordinal) {
                return api.getISOCountryList(ordinal);
            }
        };
    }

    public Iterable<ImageType> findImageTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ImageType>(matches.iterator()) {
            public ImageType getData(int ordinal) {
                return api.getImageType(ordinal);
            }
        };
    }

    public Iterable<InteractiveType> findInteractiveTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<InteractiveType>(matches.iterator()) {
            public InteractiveType getData(int ordinal) {
                return api.getInteractiveType(ordinal);
            }
        };
    }

    public Iterable<HLong> findLongMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HLong>(matches.iterator()) {
            public HLong getData(int ordinal) {
                return api.getHLong(ordinal);
            }
        };
    }

    public Iterable<MovieId> findMovieIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieId>(matches.iterator()) {
            public MovieId getData(int ordinal) {
                return api.getMovieId(ordinal);
            }
        };
    }

    public Iterable<MovieReleaseType> findMovieReleaseTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieReleaseType>(matches.iterator()) {
            public MovieReleaseType getData(int ordinal) {
                return api.getMovieReleaseType(ordinal);
            }
        };
    }

    public Iterable<MovieTitleString> findMovieTitleStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieTitleString>(matches.iterator()) {
            public MovieTitleString getData(int ordinal) {
                return api.getMovieTitleString(ordinal);
            }
        };
    }

    public Iterable<MovieTitleType> findMovieTitleTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieTitleType>(matches.iterator()) {
            public MovieTitleType getData(int ordinal) {
                return api.getMovieTitleType(ordinal);
            }
        };
    }

    public Iterable<MovieType> findMovieTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieType>(matches.iterator()) {
            public MovieType getData(int ordinal) {
                return api.getMovieType(ordinal);
            }
        };
    }

    public Iterable<OverrideEntityType> findOverrideEntityTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<OverrideEntityType>(matches.iterator()) {
            public OverrideEntityType getData(int ordinal) {
                return api.getOverrideEntityType(ordinal);
            }
        };
    }

    public Iterable<OverrideEntityValue> findOverrideEntityValueMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<OverrideEntityValue>(matches.iterator()) {
            public OverrideEntityValue getData(int ordinal) {
                return api.getOverrideEntityValue(ordinal);
            }
        };
    }

    public Iterable<PersonId> findPersonIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonId>(matches.iterator()) {
            public PersonId getData(int ordinal) {
                return api.getPersonId(ordinal);
            }
        };
    }

    public Iterable<PersonName> findPersonNameMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonName>(matches.iterator()) {
            public PersonName getData(int ordinal) {
                return api.getPersonName(ordinal);
            }
        };
    }

    public Iterable<PhaseName> findPhaseNameMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseName>(matches.iterator()) {
            public PhaseName getData(int ordinal) {
                return api.getPhaseName(ordinal);
            }
        };
    }

    public Iterable<PhaseType> findPhaseTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseType>(matches.iterator()) {
            public PhaseType getData(int ordinal) {
                return api.getPhaseType(ordinal);
            }
        };
    }

    public Iterable<RatingsRequirements> findRatingsRequirementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RatingsRequirements>(matches.iterator()) {
            public RatingsRequirements getData(int ordinal) {
                return api.getRatingsRequirements(ordinal);
            }
        };
    }

    public Iterable<RecipeGroups> findRecipeGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RecipeGroups>(matches.iterator()) {
            public RecipeGroups getData(int ordinal) {
                return api.getRecipeGroups(ordinal);
            }
        };
    }

    public Iterable<RolloutName> findRolloutNameMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutName>(matches.iterator()) {
            public RolloutName getData(int ordinal) {
                return api.getRolloutName(ordinal);
            }
        };
    }

    public Iterable<RolloutStatus> findRolloutStatusMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutStatus>(matches.iterator()) {
            public RolloutStatus getData(int ordinal) {
                return api.getRolloutStatus(ordinal);
            }
        };
    }

    public Iterable<RolloutType> findRolloutTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutType>(matches.iterator()) {
            public RolloutType getData(int ordinal) {
                return api.getRolloutType(ordinal);
            }
        };
    }

    public Iterable<ShowMemberType> findShowMemberTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberType>(matches.iterator()) {
            public ShowMemberType getData(int ordinal) {
                return api.getShowMemberType(ordinal);
            }
        };
    }

    public Iterable<ShowMemberTypeList> findShowMemberTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberTypeList>(matches.iterator()) {
            public ShowMemberTypeList getData(int ordinal) {
                return api.getShowMemberTypeList(ordinal);
            }
        };
    }

    public Iterable<ShowCountryLabel> findShowCountryLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowCountryLabel>(matches.iterator()) {
            public ShowCountryLabel getData(int ordinal) {
                return api.getShowCountryLabel(ordinal);
            }
        };
    }

    public Iterable<SourceRequestDefaultFulfillment> findSourceRequestDefaultFulfillmentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SourceRequestDefaultFulfillment>(matches.iterator()) {
            public SourceRequestDefaultFulfillment getData(int ordinal) {
                return api.getSourceRequestDefaultFulfillment(ordinal);
            }
        };
    }

    public Iterable<HString> findStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HString>(matches.iterator()) {
            public HString getData(int ordinal) {
                return api.getHString(ordinal);
            }
        };
    }

    public Iterable<MovieCountriesNotOriginal> findMovieCountriesNotOriginalMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieCountriesNotOriginal>(matches.iterator()) {
            public MovieCountriesNotOriginal getData(int ordinal) {
                return api.getMovieCountriesNotOriginal(ordinal);
            }
        };
    }

    public Iterable<MovieExtensionOverride> findMovieExtensionOverrideMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieExtensionOverride>(matches.iterator()) {
            public MovieExtensionOverride getData(int ordinal) {
                return api.getMovieExtensionOverride(ordinal);
            }
        };
    }

    public Iterable<MovieReleaseHistory> findMovieReleaseHistoryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieReleaseHistory>(matches.iterator()) {
            public MovieReleaseHistory getData(int ordinal) {
                return api.getMovieReleaseHistory(ordinal);
            }
        };
    }

    public Iterable<MovieSetContentLabel> findMovieSetContentLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieSetContentLabel>(matches.iterator()) {
            public MovieSetContentLabel getData(int ordinal) {
                return api.getMovieSetContentLabel(ordinal);
            }
        };
    }

    public Iterable<PhaseArtwork> findPhaseArtworkMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseArtwork>(matches.iterator()) {
            public PhaseArtwork getData(int ordinal) {
                return api.getPhaseArtwork(ordinal);
            }
        };
    }

    public Iterable<PhaseCastMember> findPhaseCastMemberMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseCastMember>(matches.iterator()) {
            public PhaseCastMember getData(int ordinal) {
                return api.getPhaseCastMember(ordinal);
            }
        };
    }

    public Iterable<PhaseMetadataElement> findPhaseMetadataElementMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseMetadataElement>(matches.iterator()) {
            public PhaseMetadataElement getData(int ordinal) {
                return api.getPhaseMetadataElement(ordinal);
            }
        };
    }

    public Iterable<PhaseRequiredImageType> findPhaseRequiredImageTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseRequiredImageType>(matches.iterator()) {
            public PhaseRequiredImageType getData(int ordinal) {
                return api.getPhaseRequiredImageType(ordinal);
            }
        };
    }

    public Iterable<PhaseTrailer> findPhaseTrailerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseTrailer>(matches.iterator()) {
            public PhaseTrailer getData(int ordinal) {
                return api.getPhaseTrailer(ordinal);
            }
        };
    }

    public Iterable<RolloutCountry> findRolloutCountryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutCountry>(matches.iterator()) {
            public RolloutCountry getData(int ordinal) {
                return api.getRolloutCountry(ordinal);
            }
        };
    }

    public Iterable<SetOfMovieExtensionOverride> findSetOfMovieExtensionOverrideMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfMovieExtensionOverride>(matches.iterator()) {
            public SetOfMovieExtensionOverride getData(int ordinal) {
                return api.getSetOfMovieExtensionOverride(ordinal);
            }
        };
    }

    public Iterable<MovieExtension> findMovieExtensionMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieExtension>(matches.iterator()) {
            public MovieExtension getData(int ordinal) {
                return api.getMovieExtension(ordinal);
            }
        };
    }

    public Iterable<SetOfPhaseArtwork> findSetOfPhaseArtworkMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfPhaseArtwork>(matches.iterator()) {
            public SetOfPhaseArtwork getData(int ordinal) {
                return api.getSetOfPhaseArtwork(ordinal);
            }
        };
    }

    public Iterable<SetOfPhaseCastMember> findSetOfPhaseCastMemberMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfPhaseCastMember>(matches.iterator()) {
            public SetOfPhaseCastMember getData(int ordinal) {
                return api.getSetOfPhaseCastMember(ordinal);
            }
        };
    }

    public Iterable<SetOfPhaseMetadataElement> findSetOfPhaseMetadataElementMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfPhaseMetadataElement>(matches.iterator()) {
            public SetOfPhaseMetadataElement getData(int ordinal) {
                return api.getSetOfPhaseMetadataElement(ordinal);
            }
        };
    }

    public Iterable<SetOfPhaseRequiredImageType> findSetOfPhaseRequiredImageTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfPhaseRequiredImageType>(matches.iterator()) {
            public SetOfPhaseRequiredImageType getData(int ordinal) {
                return api.getSetOfPhaseRequiredImageType(ordinal);
            }
        };
    }

    public Iterable<SetOfPhaseTrailer> findSetOfPhaseTrailerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfPhaseTrailer>(matches.iterator()) {
            public SetOfPhaseTrailer getData(int ordinal) {
                return api.getSetOfPhaseTrailer(ordinal);
            }
        };
    }

    public Iterable<SetOfRolloutCountry> findSetOfRolloutCountryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfRolloutCountry>(matches.iterator()) {
            public SetOfRolloutCountry getData(int ordinal) {
                return api.getSetOfRolloutCountry(ordinal);
            }
        };
    }

    public Iterable<SetOfString> findSetOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfString>(matches.iterator()) {
            public SetOfString getData(int ordinal) {
                return api.getSetOfString(ordinal);
            }
        };
    }

    public Iterable<MovieCountries> findMovieCountriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieCountries>(matches.iterator()) {
            public MovieCountries getData(int ordinal) {
                return api.getMovieCountries(ordinal);
            }
        };
    }

    public Iterable<ShowCountryLabelOverride> findShowCountryLabelOverrideMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowCountryLabelOverride>(matches.iterator()) {
            public ShowCountryLabelOverride getData(int ordinal) {
                return api.getShowCountryLabelOverride(ordinal);
            }
        };
    }

    public Iterable<SubsDubs> findSubsDubsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SubsDubs>(matches.iterator()) {
            public SubsDubs getData(int ordinal) {
                return api.getSubsDubs(ordinal);
            }
        };
    }

    public Iterable<SubtypeString> findSubtypeStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SubtypeString>(matches.iterator()) {
            public SubtypeString getData(int ordinal) {
                return api.getSubtypeString(ordinal);
            }
        };
    }

    public Iterable<Subtype> findSubtypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Subtype>(matches.iterator()) {
            public Subtype getData(int ordinal) {
                return api.getSubtype(ordinal);
            }
        };
    }

    public Iterable<SupplementalSubtype> findSupplementalSubtypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SupplementalSubtype>(matches.iterator()) {
            public SupplementalSubtype getData(int ordinal) {
                return api.getSupplementalSubtype(ordinal);
            }
        };
    }

    public Iterable<Movie> findMovieMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Movie>(matches.iterator()) {
            public Movie getData(int ordinal) {
                return api.getMovie(ordinal);
            }
        };
    }

    public Iterable<TitleSetupRequirementsTemplate> findTitleSetupRequirementsTemplateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TitleSetupRequirementsTemplate>(matches.iterator()) {
            public TitleSetupRequirementsTemplate getData(int ordinal) {
                return api.getTitleSetupRequirementsTemplate(ordinal);
            }
        };
    }

    public Iterable<TitleSetupRequirements> findTitleSetupRequirementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TitleSetupRequirements>(matches.iterator()) {
            public TitleSetupRequirements getData(int ordinal) {
                return api.getTitleSetupRequirements(ordinal);
            }
        };
    }

    public Iterable<TitleSourceType> findTitleSourceTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TitleSourceType>(matches.iterator()) {
            public TitleSourceType getData(int ordinal) {
                return api.getTitleSourceType(ordinal);
            }
        };
    }

    public Iterable<MovieTitleAka> findMovieTitleAkaMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieTitleAka>(matches.iterator()) {
            public MovieTitleAka getData(int ordinal) {
                return api.getMovieTitleAka(ordinal);
            }
        };
    }

    public Iterable<WindowType> findWindowTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<WindowType>(matches.iterator()) {
            public WindowType getData(int ordinal) {
                return api.getWindowType(ordinal);
            }
        };
    }

    public Iterable<RolloutPhase> findRolloutPhaseMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhase>(matches.iterator()) {
            public RolloutPhase getData(int ordinal) {
                return api.getRolloutPhase(ordinal);
            }
        };
    }

    public Iterable<SetOfRolloutPhase> findSetOfRolloutPhaseMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfRolloutPhase>(matches.iterator()) {
            public SetOfRolloutPhase getData(int ordinal) {
                return api.getSetOfRolloutPhase(ordinal);
            }
        };
    }

    public Iterable<Rollout> findRolloutMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Rollout>(matches.iterator()) {
            public Rollout getData(int ordinal) {
                return api.getRollout(ordinal);
            }
        };
    }

    public Iterable<IsOriginalTitle> findisOriginalTitleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IsOriginalTitle>(matches.iterator()) {
            public IsOriginalTitle getData(int ordinal) {
                return api.getIsOriginalTitle(ordinal);
            }
        };
    }

    public Iterable<MovieTitleNLS> findMovieTitleNLSMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieTitleNLS>(matches.iterator()) {
            public MovieTitleNLS getData(int ordinal) {
                return api.getMovieTitleNLS(ordinal);
            }
        };
    }

}