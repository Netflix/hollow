package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;
import java.lang.Iterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;


@SuppressWarnings("all")
public class VMSHollowInputAPIHashIndex extends AbstractHollowHashIndex<VMSHollowInputAPI> {

    public VMSHollowInputAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public VMSHollowInputAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<CharacterQuoteHollow> findCharacterQuoteMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharacterQuoteHollow>(matches.iterator()) {
            public CharacterQuoteHollow getData(int ordinal) {
                return api.getCharacterQuoteHollow(ordinal);
            }
        };
    }

    public Iterable<CharacterQuoteListHollow> findCharacterQuoteListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharacterQuoteListHollow>(matches.iterator()) {
            public CharacterQuoteListHollow getData(int ordinal) {
                return api.getCharacterQuoteListHollow(ordinal);
            }
        };
    }

    public Iterable<ChunkDurationsStringHollow> findChunkDurationsStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ChunkDurationsStringHollow>(matches.iterator()) {
            public ChunkDurationsStringHollow getData(int ordinal) {
                return api.getChunkDurationsStringHollow(ordinal);
            }
        };
    }

    public Iterable<CodecPrivateDataStringHollow> findCodecPrivateDataStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CodecPrivateDataStringHollow>(matches.iterator()) {
            public CodecPrivateDataStringHollow getData(int ordinal) {
                return api.getCodecPrivateDataStringHollow(ordinal);
            }
        };
    }

    public Iterable<DateHollow> findDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DateHollow>(matches.iterator()) {
            public DateHollow getData(int ordinal) {
                return api.getDateHollow(ordinal);
            }
        };
    }

    public Iterable<DerivativeTagHollow> findDerivativeTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DerivativeTagHollow>(matches.iterator()) {
            public DerivativeTagHollow getData(int ordinal) {
                return api.getDerivativeTagHollow(ordinal);
            }
        };
    }

    public Iterable<DownloadableIdHollow> findDownloadableIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DownloadableIdHollow>(matches.iterator()) {
            public DownloadableIdHollow getData(int ordinal) {
                return api.getDownloadableIdHollow(ordinal);
            }
        };
    }

    public Iterable<DownloadableIdListHollow> findDownloadableIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DownloadableIdListHollow>(matches.iterator()) {
            public DownloadableIdListHollow getData(int ordinal) {
                return api.getDownloadableIdListHollow(ordinal);
            }
        };
    }

    public Iterable<DrmInfoStringHollow> findDrmInfoStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DrmInfoStringHollow>(matches.iterator()) {
            public DrmInfoStringHollow getData(int ordinal) {
                return api.getDrmInfoStringHollow(ordinal);
            }
        };
    }

    public Iterable<EpisodeHollow> findEpisodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<EpisodeHollow>(matches.iterator()) {
            public EpisodeHollow getData(int ordinal) {
                return api.getEpisodeHollow(ordinal);
            }
        };
    }

    public Iterable<EpisodeListHollow> findEpisodeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<EpisodeListHollow>(matches.iterator()) {
            public EpisodeListHollow getData(int ordinal) {
                return api.getEpisodeListHollow(ordinal);
            }
        };
    }

    public Iterable<ExplicitDateHollow> findExplicitDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ExplicitDateHollow>(matches.iterator()) {
            public ExplicitDateHollow getData(int ordinal) {
                return api.getExplicitDateHollow(ordinal);
            }
        };
    }

    public Iterable<ISOCountryHollow> findISOCountryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountryHollow>(matches.iterator()) {
            public ISOCountryHollow getData(int ordinal) {
                return api.getISOCountryHollow(ordinal);
            }
        };
    }

    public Iterable<ISOCountryListHollow> findISOCountryListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountryListHollow>(matches.iterator()) {
            public ISOCountryListHollow getData(int ordinal) {
                return api.getISOCountryListHollow(ordinal);
            }
        };
    }

    public Iterable<ISOCountrySetHollow> findISOCountrySetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ISOCountrySetHollow>(matches.iterator()) {
            public ISOCountrySetHollow getData(int ordinal) {
                return api.getISOCountrySetHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfDerivativeTagHollow> findListOfDerivativeTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfDerivativeTagHollow>(matches.iterator()) {
            public ListOfDerivativeTagHollow getData(int ordinal) {
                return api.getListOfDerivativeTagHollow(ordinal);
            }
        };
    }

    public Iterable<LongHollow> findLongMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LongHollow>(matches.iterator()) {
            public LongHollow getData(int ordinal) {
                return api.getLongHollow(ordinal);
            }
        };
    }

    public Iterable<MapKeyHollow> findMapKeyMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapKeyHollow>(matches.iterator()) {
            public MapKeyHollow getData(int ordinal) {
                return api.getMapKeyHollow(ordinal);
            }
        };
    }

    public Iterable<MapOfFlagsFirstDisplayDatesHollow> findMapOfFlagsFirstDisplayDatesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfFlagsFirstDisplayDatesHollow>(matches.iterator()) {
            public MapOfFlagsFirstDisplayDatesHollow getData(int ordinal) {
                return api.getMapOfFlagsFirstDisplayDatesHollow(ordinal);
            }
        };
    }

    public Iterable<PersonCharacterHollow> findPersonCharacterMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonCharacterHollow>(matches.iterator()) {
            public PersonCharacterHollow getData(int ordinal) {
                return api.getPersonCharacterHollow(ordinal);
            }
        };
    }

    public Iterable<CharacterListHollow> findCharacterListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharacterListHollow>(matches.iterator()) {
            public CharacterListHollow getData(int ordinal) {
                return api.getCharacterListHollow(ordinal);
            }
        };
    }

    public Iterable<MovieCharacterPersonHollow> findMovieCharacterPersonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieCharacterPersonHollow>(matches.iterator()) {
            public MovieCharacterPersonHollow getData(int ordinal) {
                return api.getMovieCharacterPersonHollow(ordinal);
            }
        };
    }

    public Iterable<PersonVideoAliasIdHollow> findPersonVideoAliasIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoAliasIdHollow>(matches.iterator()) {
            public PersonVideoAliasIdHollow getData(int ordinal) {
                return api.getPersonVideoAliasIdHollow(ordinal);
            }
        };
    }

    public Iterable<PersonVideoAliasIdsListHollow> findPersonVideoAliasIdsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoAliasIdsListHollow>(matches.iterator()) {
            public PersonVideoAliasIdsListHollow getData(int ordinal) {
                return api.getPersonVideoAliasIdsListHollow(ordinal);
            }
        };
    }

    public Iterable<PersonVideoRoleHollow> findPersonVideoRoleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoRoleHollow>(matches.iterator()) {
            public PersonVideoRoleHollow getData(int ordinal) {
                return api.getPersonVideoRoleHollow(ordinal);
            }
        };
    }

    public Iterable<PersonVideoRolesListHollow> findPersonVideoRolesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoRolesListHollow>(matches.iterator()) {
            public PersonVideoRolesListHollow getData(int ordinal) {
                return api.getPersonVideoRolesListHollow(ordinal);
            }
        };
    }

    public Iterable<PersonVideoHollow> findPersonVideoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoHollow>(matches.iterator()) {
            public PersonVideoHollow getData(int ordinal) {
                return api.getPersonVideoHollow(ordinal);
            }
        };
    }

    public Iterable<RightsContractPackageHollow> findRightsContractPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsContractPackageHollow>(matches.iterator()) {
            public RightsContractPackageHollow getData(int ordinal) {
                return api.getRightsContractPackageHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsContractPackageHollow> findListOfRightsContractPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsContractPackageHollow>(matches.iterator()) {
            public ListOfRightsContractPackageHollow getData(int ordinal) {
                return api.getListOfRightsContractPackageHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseWindowHollow> findRolloutPhaseWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseWindowHollow>(matches.iterator()) {
            public RolloutPhaseWindowHollow getData(int ordinal) {
                return api.getRolloutPhaseWindowHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseWindowMapHollow> findRolloutPhaseWindowMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseWindowMapHollow>(matches.iterator()) {
            public RolloutPhaseWindowMapHollow getData(int ordinal) {
                return api.getRolloutPhaseWindowMapHollow(ordinal);
            }
        };
    }

    public Iterable<ShowMemberTypeHollow> findShowMemberTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberTypeHollow>(matches.iterator()) {
            public ShowMemberTypeHollow getData(int ordinal) {
                return api.getShowMemberTypeHollow(ordinal);
            }
        };
    }

    public Iterable<ShowMemberTypeListHollow> findShowMemberTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberTypeListHollow>(matches.iterator()) {
            public ShowMemberTypeListHollow getData(int ordinal) {
                return api.getShowMemberTypeListHollow(ordinal);
            }
        };
    }

    public Iterable<ShowCountryLabelHollow> findShowCountryLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowCountryLabelHollow>(matches.iterator()) {
            public ShowCountryLabelHollow getData(int ordinal) {
                return api.getShowCountryLabelHollow(ordinal);
            }
        };
    }

    public Iterable<StreamAssetMetadataHollow> findStreamAssetMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamAssetMetadataHollow>(matches.iterator()) {
            public StreamAssetMetadataHollow getData(int ordinal) {
                return api.getStreamAssetMetadataHollow(ordinal);
            }
        };
    }

    public Iterable<StreamBoxInfoKeyHollow> findStreamBoxInfoKeyMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamBoxInfoKeyHollow>(matches.iterator()) {
            public StreamBoxInfoKeyHollow getData(int ordinal) {
                return api.getStreamBoxInfoKeyHollow(ordinal);
            }
        };
    }

    public Iterable<StreamBoxInfoHollow> findStreamBoxInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamBoxInfoHollow>(matches.iterator()) {
            public StreamBoxInfoHollow getData(int ordinal) {
                return api.getStreamBoxInfoHollow(ordinal);
            }
        };
    }

    public Iterable<SetOfStreamBoxInfoHollow> findSetOfStreamBoxInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfStreamBoxInfoHollow>(matches.iterator()) {
            public SetOfStreamBoxInfoHollow getData(int ordinal) {
                return api.getSetOfStreamBoxInfoHollow(ordinal);
            }
        };
    }

    public Iterable<DashStreamHeaderDataHollow> findDashStreamHeaderDataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DashStreamHeaderDataHollow>(matches.iterator()) {
            public DashStreamHeaderDataHollow getData(int ordinal) {
                return api.getDashStreamHeaderDataHollow(ordinal);
            }
        };
    }

    public Iterable<StreamDimensionsHollow> findStreamDimensionsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamDimensionsHollow>(matches.iterator()) {
            public StreamDimensionsHollow getData(int ordinal) {
                return api.getStreamDimensionsHollow(ordinal);
            }
        };
    }

    public Iterable<StreamFileIdentificationHollow> findStreamFileIdentificationMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamFileIdentificationHollow>(matches.iterator()) {
            public StreamFileIdentificationHollow getData(int ordinal) {
                return api.getStreamFileIdentificationHollow(ordinal);
            }
        };
    }

    public Iterable<StreamProfileIdHollow> findStreamProfileIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamProfileIdHollow>(matches.iterator()) {
            public StreamProfileIdHollow getData(int ordinal) {
                return api.getStreamProfileIdHollow(ordinal);
            }
        };
    }

    public Iterable<StreamProfileIdListHollow> findStreamProfileIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamProfileIdListHollow>(matches.iterator()) {
            public StreamProfileIdListHollow getData(int ordinal) {
                return api.getStreamProfileIdListHollow(ordinal);
            }
        };
    }

    public Iterable<StringHollow> findStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StringHollow>(matches.iterator()) {
            public StringHollow getData(int ordinal) {
                return api.getStringHollow(ordinal);
            }
        };
    }

    public Iterable<AbsoluteScheduleHollow> findAbsoluteScheduleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AbsoluteScheduleHollow>(matches.iterator()) {
            public AbsoluteScheduleHollow getData(int ordinal) {
                return api.getAbsoluteScheduleHollow(ordinal);
            }
        };
    }

    public Iterable<ArtWorkImageTypeHollow> findArtWorkImageTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ArtWorkImageTypeHollow>(matches.iterator()) {
            public ArtWorkImageTypeHollow getData(int ordinal) {
                return api.getArtWorkImageTypeHollow(ordinal);
            }
        };
    }

    public Iterable<ArtworkRecipeHollow> findArtworkRecipeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ArtworkRecipeHollow>(matches.iterator()) {
            public ArtworkRecipeHollow getData(int ordinal) {
                return api.getArtworkRecipeHollow(ordinal);
            }
        };
    }

    public Iterable<AudioStreamInfoHollow> findAudioStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AudioStreamInfoHollow>(matches.iterator()) {
            public AudioStreamInfoHollow getData(int ordinal) {
                return api.getAudioStreamInfoHollow(ordinal);
            }
        };
    }

    public Iterable<CSMReviewHollow> findCSMReviewMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CSMReviewHollow>(matches.iterator()) {
            public CSMReviewHollow getData(int ordinal) {
                return api.getCSMReviewHollow(ordinal);
            }
        };
    }

    public Iterable<CacheDeploymentIntentHollow> findCacheDeploymentIntentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CacheDeploymentIntentHollow>(matches.iterator()) {
            public CacheDeploymentIntentHollow getData(int ordinal) {
                return api.getCacheDeploymentIntentHollow(ordinal);
            }
        };
    }

    public Iterable<CdnHollow> findCdnMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CdnHollow>(matches.iterator()) {
            public CdnHollow getData(int ordinal) {
                return api.getCdnHollow(ordinal);
            }
        };
    }

    public Iterable<CdnDeploymentHollow> findCdnDeploymentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CdnDeploymentHollow>(matches.iterator()) {
            public CdnDeploymentHollow getData(int ordinal) {
                return api.getCdnDeploymentHollow(ordinal);
            }
        };
    }

    public Iterable<CdnDeploymentSetHollow> findCdnDeploymentSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CdnDeploymentSetHollow>(matches.iterator()) {
            public CdnDeploymentSetHollow getData(int ordinal) {
                return api.getCdnDeploymentSetHollow(ordinal);
            }
        };
    }

    public Iterable<CertificationSystemRatingHollow> findCertificationSystemRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CertificationSystemRatingHollow>(matches.iterator()) {
            public CertificationSystemRatingHollow getData(int ordinal) {
                return api.getCertificationSystemRatingHollow(ordinal);
            }
        };
    }

    public Iterable<CertificationSystemRatingListHollow> findCertificationSystemRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CertificationSystemRatingListHollow>(matches.iterator()) {
            public CertificationSystemRatingListHollow getData(int ordinal) {
                return api.getCertificationSystemRatingListHollow(ordinal);
            }
        };
    }

    public Iterable<CertificationSystemHollow> findCertificationSystemMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CertificationSystemHollow>(matches.iterator()) {
            public CertificationSystemHollow getData(int ordinal) {
                return api.getCertificationSystemHollow(ordinal);
            }
        };
    }

    public Iterable<CharacterElementsHollow> findCharacterElementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharacterElementsHollow>(matches.iterator()) {
            public CharacterElementsHollow getData(int ordinal) {
                return api.getCharacterElementsHollow(ordinal);
            }
        };
    }

    public Iterable<CharacterHollow> findCharacterMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharacterHollow>(matches.iterator()) {
            public CharacterHollow getData(int ordinal) {
                return api.getCharacterHollow(ordinal);
            }
        };
    }

    public Iterable<CinderCupTokenRecordHollow> findCinderCupTokenRecordMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CinderCupTokenRecordHollow>(matches.iterator()) {
            public CinderCupTokenRecordHollow getData(int ordinal) {
                return api.getCinderCupTokenRecordHollow(ordinal);
            }
        };
    }

    public Iterable<DamMerchStillsMomentHollow> findDamMerchStillsMomentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DamMerchStillsMomentHollow>(matches.iterator()) {
            public DamMerchStillsMomentHollow getData(int ordinal) {
                return api.getDamMerchStillsMomentHollow(ordinal);
            }
        };
    }

    public Iterable<DamMerchStillsHollow> findDamMerchStillsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DamMerchStillsHollow>(matches.iterator()) {
            public DamMerchStillsHollow getData(int ordinal) {
                return api.getDamMerchStillsHollow(ordinal);
            }
        };
    }

    public Iterable<DisallowedSubtitleLangCodeHollow> findDisallowedSubtitleLangCodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DisallowedSubtitleLangCodeHollow>(matches.iterator()) {
            public DisallowedSubtitleLangCodeHollow getData(int ordinal) {
                return api.getDisallowedSubtitleLangCodeHollow(ordinal);
            }
        };
    }

    public Iterable<DisallowedSubtitleLangCodesListHollow> findDisallowedSubtitleLangCodesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DisallowedSubtitleLangCodesListHollow>(matches.iterator()) {
            public DisallowedSubtitleLangCodesListHollow getData(int ordinal) {
                return api.getDisallowedSubtitleLangCodesListHollow(ordinal);
            }
        };
    }

    public Iterable<DisallowedAssetBundleHollow> findDisallowedAssetBundleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DisallowedAssetBundleHollow>(matches.iterator()) {
            public DisallowedAssetBundleHollow getData(int ordinal) {
                return api.getDisallowedAssetBundleHollow(ordinal);
            }
        };
    }

    public Iterable<DisallowedAssetBundlesListHollow> findDisallowedAssetBundlesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DisallowedAssetBundlesListHollow>(matches.iterator()) {
            public DisallowedAssetBundlesListHollow getData(int ordinal) {
                return api.getDisallowedAssetBundlesListHollow(ordinal);
            }
        };
    }

    public Iterable<ContractHollow> findContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ContractHollow>(matches.iterator()) {
            public ContractHollow getData(int ordinal) {
                return api.getContractHollow(ordinal);
            }
        };
    }

    public Iterable<DrmHeaderInfoHollow> findDrmHeaderInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DrmHeaderInfoHollow>(matches.iterator()) {
            public DrmHeaderInfoHollow getData(int ordinal) {
                return api.getDrmHeaderInfoHollow(ordinal);
            }
        };
    }

    public Iterable<DrmHeaderInfoListHollow> findDrmHeaderInfoListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DrmHeaderInfoListHollow>(matches.iterator()) {
            public DrmHeaderInfoListHollow getData(int ordinal) {
                return api.getDrmHeaderInfoListHollow(ordinal);
            }
        };
    }

    public Iterable<DrmSystemIdentifiersHollow> findDrmSystemIdentifiersMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DrmSystemIdentifiersHollow>(matches.iterator()) {
            public DrmSystemIdentifiersHollow getData(int ordinal) {
                return api.getDrmSystemIdentifiersHollow(ordinal);
            }
        };
    }

    public Iterable<IPLArtworkDerivativeHollow> findIPLArtworkDerivativeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLArtworkDerivativeHollow>(matches.iterator()) {
            public IPLArtworkDerivativeHollow getData(int ordinal) {
                return api.getIPLArtworkDerivativeHollow(ordinal);
            }
        };
    }

    public Iterable<IPLDerivativeSetHollow> findIPLDerivativeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLDerivativeSetHollow>(matches.iterator()) {
            public IPLDerivativeSetHollow getData(int ordinal) {
                return api.getIPLDerivativeSetHollow(ordinal);
            }
        };
    }

    public Iterable<IPLDerivativeGroupHollow> findIPLDerivativeGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLDerivativeGroupHollow>(matches.iterator()) {
            public IPLDerivativeGroupHollow getData(int ordinal) {
                return api.getIPLDerivativeGroupHollow(ordinal);
            }
        };
    }

    public Iterable<IPLDerivativeGroupSetHollow> findIPLDerivativeGroupSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLDerivativeGroupSetHollow>(matches.iterator()) {
            public IPLDerivativeGroupSetHollow getData(int ordinal) {
                return api.getIPLDerivativeGroupSetHollow(ordinal);
            }
        };
    }

    public Iterable<IPLArtworkDerivativeSetHollow> findIPLArtworkDerivativeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IPLArtworkDerivativeSetHollow>(matches.iterator()) {
            public IPLArtworkDerivativeSetHollow getData(int ordinal) {
                return api.getIPLArtworkDerivativeSetHollow(ordinal);
            }
        };
    }

    public Iterable<ImageStreamInfoHollow> findImageStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ImageStreamInfoHollow>(matches.iterator()) {
            public ImageStreamInfoHollow getData(int ordinal) {
                return api.getImageStreamInfoHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfContractHollow> findListOfContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfContractHollow>(matches.iterator()) {
            public ListOfContractHollow getData(int ordinal) {
                return api.getListOfContractHollow(ordinal);
            }
        };
    }

    public Iterable<ContractsHollow> findContractsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ContractsHollow>(matches.iterator()) {
            public ContractsHollow getData(int ordinal) {
                return api.getContractsHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfPackageTagsHollow> findListOfPackageTagsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfPackageTagsHollow>(matches.iterator()) {
            public ListOfPackageTagsHollow getData(int ordinal) {
                return api.getListOfPackageTagsHollow(ordinal);
            }
        };
    }

    public Iterable<DeployablePackagesHollow> findDeployablePackagesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<DeployablePackagesHollow>(matches.iterator()) {
            public DeployablePackagesHollow getData(int ordinal) {
                return api.getDeployablePackagesHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfStringHollow> findListOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfStringHollow>(matches.iterator()) {
            public ListOfStringHollow getData(int ordinal) {
                return api.getListOfStringHollow(ordinal);
            }
        };
    }

    public Iterable<LocaleTerritoryCodeHollow> findLocaleTerritoryCodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LocaleTerritoryCodeHollow>(matches.iterator()) {
            public LocaleTerritoryCodeHollow getData(int ordinal) {
                return api.getLocaleTerritoryCodeHollow(ordinal);
            }
        };
    }

    public Iterable<LocaleTerritoryCodeListHollow> findLocaleTerritoryCodeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LocaleTerritoryCodeListHollow>(matches.iterator()) {
            public LocaleTerritoryCodeListHollow getData(int ordinal) {
                return api.getLocaleTerritoryCodeListHollow(ordinal);
            }
        };
    }

    public Iterable<MapOfStringToLongHollow> findMapOfStringToLongMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfStringToLongHollow>(matches.iterator()) {
            public MapOfStringToLongHollow getData(int ordinal) {
                return api.getMapOfStringToLongHollow(ordinal);
            }
        };
    }

    public Iterable<FeedMovieCountryLanguagesHollow> findFeedMovieCountryLanguagesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<FeedMovieCountryLanguagesHollow>(matches.iterator()) {
            public FeedMovieCountryLanguagesHollow getData(int ordinal) {
                return api.getFeedMovieCountryLanguagesHollow(ordinal);
            }
        };
    }

    public Iterable<MasterScheduleHollow> findMasterScheduleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MasterScheduleHollow>(matches.iterator()) {
            public MasterScheduleHollow getData(int ordinal) {
                return api.getMasterScheduleHollow(ordinal);
            }
        };
    }

    public Iterable<MultiValuePassthroughMapHollow> findMultiValuePassthroughMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MultiValuePassthroughMapHollow>(matches.iterator()) {
            public MultiValuePassthroughMapHollow getData(int ordinal) {
                return api.getMultiValuePassthroughMapHollow(ordinal);
            }
        };
    }

    public Iterable<OriginServerHollow> findOriginServerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<OriginServerHollow>(matches.iterator()) {
            public OriginServerHollow getData(int ordinal) {
                return api.getOriginServerHollow(ordinal);
            }
        };
    }

    public Iterable<OverrideScheduleHollow> findOverrideScheduleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<OverrideScheduleHollow>(matches.iterator()) {
            public OverrideScheduleHollow getData(int ordinal) {
                return api.getOverrideScheduleHollow(ordinal);
            }
        };
    }

    public Iterable<PackageDrmInfoHollow> findPackageDrmInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageDrmInfoHollow>(matches.iterator()) {
            public PackageDrmInfoHollow getData(int ordinal) {
                return api.getPackageDrmInfoHollow(ordinal);
            }
        };
    }

    public Iterable<PackageDrmInfoListHollow> findPackageDrmInfoListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageDrmInfoListHollow>(matches.iterator()) {
            public PackageDrmInfoListHollow getData(int ordinal) {
                return api.getPackageDrmInfoListHollow(ordinal);
            }
        };
    }

    public Iterable<PackageMomentHollow> findPackageMomentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageMomentHollow>(matches.iterator()) {
            public PackageMomentHollow getData(int ordinal) {
                return api.getPackageMomentHollow(ordinal);
            }
        };
    }

    public Iterable<PackageMomentListHollow> findPackageMomentListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageMomentListHollow>(matches.iterator()) {
            public PackageMomentListHollow getData(int ordinal) {
                return api.getPackageMomentListHollow(ordinal);
            }
        };
    }

    public Iterable<PhaseTagHollow> findPhaseTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseTagHollow>(matches.iterator()) {
            public PhaseTagHollow getData(int ordinal) {
                return api.getPhaseTagHollow(ordinal);
            }
        };
    }

    public Iterable<PhaseTagListHollow> findPhaseTagListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PhaseTagListHollow>(matches.iterator()) {
            public PhaseTagListHollow getData(int ordinal) {
                return api.getPhaseTagListHollow(ordinal);
            }
        };
    }

    public Iterable<ProtectionTypesHollow> findProtectionTypesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ProtectionTypesHollow>(matches.iterator()) {
            public ProtectionTypesHollow getData(int ordinal) {
                return api.getProtectionTypesHollow(ordinal);
            }
        };
    }

    public Iterable<ReleaseDateHollow> findReleaseDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ReleaseDateHollow>(matches.iterator()) {
            public ReleaseDateHollow getData(int ordinal) {
                return api.getReleaseDateHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfReleaseDatesHollow> findListOfReleaseDatesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfReleaseDatesHollow>(matches.iterator()) {
            public ListOfReleaseDatesHollow getData(int ordinal) {
                return api.getListOfReleaseDatesHollow(ordinal);
            }
        };
    }

    public Iterable<RightsContractAssetHollow> findRightsContractAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsContractAssetHollow>(matches.iterator()) {
            public RightsContractAssetHollow getData(int ordinal) {
                return api.getRightsContractAssetHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsContractAssetHollow> findListOfRightsContractAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsContractAssetHollow>(matches.iterator()) {
            public ListOfRightsContractAssetHollow getData(int ordinal) {
                return api.getListOfRightsContractAssetHollow(ordinal);
            }
        };
    }

    public Iterable<RightsWindowContractHollow> findRightsWindowContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsWindowContractHollow>(matches.iterator()) {
            public RightsWindowContractHollow getData(int ordinal) {
                return api.getRightsWindowContractHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsWindowContractHollow> findListOfRightsWindowContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsWindowContractHollow>(matches.iterator()) {
            public ListOfRightsWindowContractHollow getData(int ordinal) {
                return api.getListOfRightsWindowContractHollow(ordinal);
            }
        };
    }

    public Iterable<RightsWindowHollow> findRightsWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsWindowHollow>(matches.iterator()) {
            public RightsWindowHollow getData(int ordinal) {
                return api.getRightsWindowHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsWindowHollow> findListOfRightsWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsWindowHollow>(matches.iterator()) {
            public ListOfRightsWindowHollow getData(int ordinal) {
                return api.getListOfRightsWindowHollow(ordinal);
            }
        };
    }

    public Iterable<RightsHollow> findRightsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsHollow>(matches.iterator()) {
            public RightsHollow getData(int ordinal) {
                return api.getRightsHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseArtworkSourceFileIdHollow> findRolloutPhaseArtworkSourceFileIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseArtworkSourceFileIdHollow>(matches.iterator()) {
            public RolloutPhaseArtworkSourceFileIdHollow getData(int ordinal) {
                return api.getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseArtworkSourceFileIdListHollow> findRolloutPhaseArtworkSourceFileIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseArtworkSourceFileIdListHollow>(matches.iterator()) {
            public RolloutPhaseArtworkSourceFileIdListHollow getData(int ordinal) {
                return api.getRolloutPhaseArtworkSourceFileIdListHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseArtworkHollow> findRolloutPhaseArtworkMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseArtworkHollow>(matches.iterator()) {
            public RolloutPhaseArtworkHollow getData(int ordinal) {
                return api.getRolloutPhaseArtworkHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseLocalizedMetadataHollow> findRolloutPhaseLocalizedMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseLocalizedMetadataHollow>(matches.iterator()) {
            public RolloutPhaseLocalizedMetadataHollow getData(int ordinal) {
                return api.getRolloutPhaseLocalizedMetadataHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseElementsHollow> findRolloutPhaseElementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseElementsHollow>(matches.iterator()) {
            public RolloutPhaseElementsHollow getData(int ordinal) {
                return api.getRolloutPhaseElementsHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseHollow> findRolloutPhaseMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseHollow>(matches.iterator()) {
            public RolloutPhaseHollow getData(int ordinal) {
                return api.getRolloutPhaseHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutPhaseListHollow> findRolloutPhaseListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutPhaseListHollow>(matches.iterator()) {
            public RolloutPhaseListHollow getData(int ordinal) {
                return api.getRolloutPhaseListHollow(ordinal);
            }
        };
    }

    public Iterable<RolloutHollow> findRolloutMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RolloutHollow>(matches.iterator()) {
            public RolloutHollow getData(int ordinal) {
                return api.getRolloutHollow(ordinal);
            }
        };
    }

    public Iterable<SeasonHollow> findSeasonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SeasonHollow>(matches.iterator()) {
            public SeasonHollow getData(int ordinal) {
                return api.getSeasonHollow(ordinal);
            }
        };
    }

    public Iterable<SeasonListHollow> findSeasonListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SeasonListHollow>(matches.iterator()) {
            public SeasonListHollow getData(int ordinal) {
                return api.getSeasonListHollow(ordinal);
            }
        };
    }

    public Iterable<SetOfStringHollow> findSetOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SetOfStringHollow>(matches.iterator()) {
            public SetOfStringHollow getData(int ordinal) {
                return api.getSetOfStringHollow(ordinal);
            }
        };
    }

    public Iterable<FlagsHollow> findFlagsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<FlagsHollow>(matches.iterator()) {
            public FlagsHollow getData(int ordinal) {
                return api.getFlagsHollow(ordinal);
            }
        };
    }

    public Iterable<ShowSeasonEpisodeHollow> findShowSeasonEpisodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowSeasonEpisodeHollow>(matches.iterator()) {
            public ShowSeasonEpisodeHollow getData(int ordinal) {
                return api.getShowSeasonEpisodeHollow(ordinal);
            }
        };
    }

    public Iterable<SingleValuePassthroughMapHollow> findSingleValuePassthroughMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SingleValuePassthroughMapHollow>(matches.iterator()) {
            public SingleValuePassthroughMapHollow getData(int ordinal) {
                return api.getSingleValuePassthroughMapHollow(ordinal);
            }
        };
    }

    public Iterable<PassthroughDataHollow> findPassthroughDataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PassthroughDataHollow>(matches.iterator()) {
            public PassthroughDataHollow getData(int ordinal) {
                return api.getPassthroughDataHollow(ordinal);
            }
        };
    }

    public Iterable<ArtworkAttributesHollow> findArtworkAttributesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ArtworkAttributesHollow>(matches.iterator()) {
            public ArtworkAttributesHollow getData(int ordinal) {
                return api.getArtworkAttributesHollow(ordinal);
            }
        };
    }

    public Iterable<ArtworkLocaleHollow> findArtworkLocaleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ArtworkLocaleHollow>(matches.iterator()) {
            public ArtworkLocaleHollow getData(int ordinal) {
                return api.getArtworkLocaleHollow(ordinal);
            }
        };
    }

    public Iterable<ArtworkLocaleListHollow> findArtworkLocaleListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ArtworkLocaleListHollow>(matches.iterator()) {
            public ArtworkLocaleListHollow getData(int ordinal) {
                return api.getArtworkLocaleListHollow(ordinal);
            }
        };
    }

    public Iterable<CharacterArtworkSourceHollow> findCharacterArtworkSourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharacterArtworkSourceHollow>(matches.iterator()) {
            public CharacterArtworkSourceHollow getData(int ordinal) {
                return api.getCharacterArtworkSourceHollow(ordinal);
            }
        };
    }

    public Iterable<IndividualSupplementalHollow> findIndividualSupplementalMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<IndividualSupplementalHollow>(matches.iterator()) {
            public IndividualSupplementalHollow getData(int ordinal) {
                return api.getIndividualSupplementalHollow(ordinal);
            }
        };
    }

    public Iterable<PersonArtworkSourceHollow> findPersonArtworkSourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonArtworkSourceHollow>(matches.iterator()) {
            public PersonArtworkSourceHollow getData(int ordinal) {
                return api.getPersonArtworkSourceHollow(ordinal);
            }
        };
    }

    public Iterable<StatusHollow> findStatusMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StatusHollow>(matches.iterator()) {
            public StatusHollow getData(int ordinal) {
                return api.getStatusHollow(ordinal);
            }
        };
    }

    public Iterable<StorageGroupsHollow> findStorageGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StorageGroupsHollow>(matches.iterator()) {
            public StorageGroupsHollow getData(int ordinal) {
                return api.getStorageGroupsHollow(ordinal);
            }
        };
    }

    public Iterable<StreamAssetTypeHollow> findStreamAssetTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamAssetTypeHollow>(matches.iterator()) {
            public StreamAssetTypeHollow getData(int ordinal) {
                return api.getStreamAssetTypeHollow(ordinal);
            }
        };
    }

    public Iterable<StreamDeploymentInfoHollow> findStreamDeploymentInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamDeploymentInfoHollow>(matches.iterator()) {
            public StreamDeploymentInfoHollow getData(int ordinal) {
                return api.getStreamDeploymentInfoHollow(ordinal);
            }
        };
    }

    public Iterable<StreamDeploymentLabelHollow> findStreamDeploymentLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamDeploymentLabelHollow>(matches.iterator()) {
            public StreamDeploymentLabelHollow getData(int ordinal) {
                return api.getStreamDeploymentLabelHollow(ordinal);
            }
        };
    }

    public Iterable<StreamDeploymentLabelSetHollow> findStreamDeploymentLabelSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamDeploymentLabelSetHollow>(matches.iterator()) {
            public StreamDeploymentLabelSetHollow getData(int ordinal) {
                return api.getStreamDeploymentLabelSetHollow(ordinal);
            }
        };
    }

    public Iterable<StreamDeploymentHollow> findStreamDeploymentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamDeploymentHollow>(matches.iterator()) {
            public StreamDeploymentHollow getData(int ordinal) {
                return api.getStreamDeploymentHollow(ordinal);
            }
        };
    }

    public Iterable<StreamDrmInfoHollow> findStreamDrmInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamDrmInfoHollow>(matches.iterator()) {
            public StreamDrmInfoHollow getData(int ordinal) {
                return api.getStreamDrmInfoHollow(ordinal);
            }
        };
    }

    public Iterable<StreamProfileGroupsHollow> findStreamProfileGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamProfileGroupsHollow>(matches.iterator()) {
            public StreamProfileGroupsHollow getData(int ordinal) {
                return api.getStreamProfileGroupsHollow(ordinal);
            }
        };
    }

    public Iterable<StreamProfilesHollow> findStreamProfilesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamProfilesHollow>(matches.iterator()) {
            public StreamProfilesHollow getData(int ordinal) {
                return api.getStreamProfilesHollow(ordinal);
            }
        };
    }

    public Iterable<SupplementalsListHollow> findSupplementalsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SupplementalsListHollow>(matches.iterator()) {
            public SupplementalsListHollow getData(int ordinal) {
                return api.getSupplementalsListHollow(ordinal);
            }
        };
    }

    public Iterable<SupplementalsHollow> findSupplementalsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SupplementalsHollow>(matches.iterator()) {
            public SupplementalsHollow getData(int ordinal) {
                return api.getSupplementalsHollow(ordinal);
            }
        };
    }

    public Iterable<TerritoryCountriesHollow> findTerritoryCountriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TerritoryCountriesHollow>(matches.iterator()) {
            public TerritoryCountriesHollow getData(int ordinal) {
                return api.getTerritoryCountriesHollow(ordinal);
            }
        };
    }

    public Iterable<TextStreamInfoHollow> findTextStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TextStreamInfoHollow>(matches.iterator()) {
            public TextStreamInfoHollow getData(int ordinal) {
                return api.getTextStreamInfoHollow(ordinal);
            }
        };
    }

    public Iterable<TimecodedMomentAnnotationHollow> findTimecodedMomentAnnotationMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TimecodedMomentAnnotationHollow>(matches.iterator()) {
            public TimecodedMomentAnnotationHollow getData(int ordinal) {
                return api.getTimecodedMomentAnnotationHollow(ordinal);
            }
        };
    }

    public Iterable<TimecodeAnnotationsListHollow> findTimecodeAnnotationsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TimecodeAnnotationsListHollow>(matches.iterator()) {
            public TimecodeAnnotationsListHollow getData(int ordinal) {
                return api.getTimecodeAnnotationsListHollow(ordinal);
            }
        };
    }

    public Iterable<TimecodeAnnotationHollow> findTimecodeAnnotationMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TimecodeAnnotationHollow>(matches.iterator()) {
            public TimecodeAnnotationHollow getData(int ordinal) {
                return api.getTimecodeAnnotationHollow(ordinal);
            }
        };
    }

    public Iterable<TopNAttributeHollow> findTopNAttributeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TopNAttributeHollow>(matches.iterator()) {
            public TopNAttributeHollow getData(int ordinal) {
                return api.getTopNAttributeHollow(ordinal);
            }
        };
    }

    public Iterable<TopNAttributesSetHollow> findTopNAttributesSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TopNAttributesSetHollow>(matches.iterator()) {
            public TopNAttributesSetHollow getData(int ordinal) {
                return api.getTopNAttributesSetHollow(ordinal);
            }
        };
    }

    public Iterable<TopNHollow> findTopNMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TopNHollow>(matches.iterator()) {
            public TopNHollow getData(int ordinal) {
                return api.getTopNHollow(ordinal);
            }
        };
    }

    public Iterable<TranslatedTextValueHollow> findTranslatedTextValueMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TranslatedTextValueHollow>(matches.iterator()) {
            public TranslatedTextValueHollow getData(int ordinal) {
                return api.getTranslatedTextValueHollow(ordinal);
            }
        };
    }

    public Iterable<MapOfTranslatedTextHollow> findMapOfTranslatedTextMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfTranslatedTextHollow>(matches.iterator()) {
            public MapOfTranslatedTextHollow getData(int ordinal) {
                return api.getMapOfTranslatedTextHollow(ordinal);
            }
        };
    }

    public Iterable<AltGenresAlternateNamesHollow> findAltGenresAlternateNamesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AltGenresAlternateNamesHollow>(matches.iterator()) {
            public AltGenresAlternateNamesHollow getData(int ordinal) {
                return api.getAltGenresAlternateNamesHollow(ordinal);
            }
        };
    }

    public Iterable<AltGenresAlternateNamesListHollow> findAltGenresAlternateNamesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AltGenresAlternateNamesListHollow>(matches.iterator()) {
            public AltGenresAlternateNamesListHollow getData(int ordinal) {
                return api.getAltGenresAlternateNamesListHollow(ordinal);
            }
        };
    }

    public Iterable<LocalizedCharacterHollow> findLocalizedCharacterMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LocalizedCharacterHollow>(matches.iterator()) {
            public LocalizedCharacterHollow getData(int ordinal) {
                return api.getLocalizedCharacterHollow(ordinal);
            }
        };
    }

    public Iterable<LocalizedMetadataHollow> findLocalizedMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LocalizedMetadataHollow>(matches.iterator()) {
            public LocalizedMetadataHollow getData(int ordinal) {
                return api.getLocalizedMetadataHollow(ordinal);
            }
        };
    }

    public Iterable<StoriesSynopsesHookHollow> findStoriesSynopsesHookMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StoriesSynopsesHookHollow>(matches.iterator()) {
            public StoriesSynopsesHookHollow getData(int ordinal) {
                return api.getStoriesSynopsesHookHollow(ordinal);
            }
        };
    }

    public Iterable<StoriesSynopsesHookListHollow> findStoriesSynopsesHookListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StoriesSynopsesHookListHollow>(matches.iterator()) {
            public StoriesSynopsesHookListHollow getData(int ordinal) {
                return api.getStoriesSynopsesHookListHollow(ordinal);
            }
        };
    }

    public Iterable<TranslatedTextHollow> findTranslatedTextMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TranslatedTextHollow>(matches.iterator()) {
            public TranslatedTextHollow getData(int ordinal) {
                return api.getTranslatedTextHollow(ordinal);
            }
        };
    }

    public Iterable<AltGenresHollow> findAltGenresMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AltGenresHollow>(matches.iterator()) {
            public AltGenresHollow getData(int ordinal) {
                return api.getAltGenresHollow(ordinal);
            }
        };
    }

    public Iterable<AssetMetaDatasHollow> findAssetMetaDatasMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AssetMetaDatasHollow>(matches.iterator()) {
            public AssetMetaDatasHollow getData(int ordinal) {
                return api.getAssetMetaDatasHollow(ordinal);
            }
        };
    }

    public Iterable<AwardsHollow> findAwardsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AwardsHollow>(matches.iterator()) {
            public AwardsHollow getData(int ordinal) {
                return api.getAwardsHollow(ordinal);
            }
        };
    }

    public Iterable<CategoriesHollow> findCategoriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CategoriesHollow>(matches.iterator()) {
            public CategoriesHollow getData(int ordinal) {
                return api.getCategoriesHollow(ordinal);
            }
        };
    }

    public Iterable<CategoryGroupsHollow> findCategoryGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CategoryGroupsHollow>(matches.iterator()) {
            public CategoryGroupsHollow getData(int ordinal) {
                return api.getCategoryGroupsHollow(ordinal);
            }
        };
    }

    public Iterable<CertificationsHollow> findCertificationsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CertificationsHollow>(matches.iterator()) {
            public CertificationsHollow getData(int ordinal) {
                return api.getCertificationsHollow(ordinal);
            }
        };
    }

    public Iterable<CharactersHollow> findCharactersMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<CharactersHollow>(matches.iterator()) {
            public CharactersHollow getData(int ordinal) {
                return api.getCharactersHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedCertSystemRatingHollow> findConsolidatedCertSystemRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedCertSystemRatingHollow>(matches.iterator()) {
            public ConsolidatedCertSystemRatingHollow getData(int ordinal) {
                return api.getConsolidatedCertSystemRatingHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedCertSystemRatingListHollow> findConsolidatedCertSystemRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedCertSystemRatingListHollow>(matches.iterator()) {
            public ConsolidatedCertSystemRatingListHollow getData(int ordinal) {
                return api.getConsolidatedCertSystemRatingListHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedCertificationSystemsHollow> findConsolidatedCertificationSystemsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedCertificationSystemsHollow>(matches.iterator()) {
            public ConsolidatedCertificationSystemsHollow getData(int ordinal) {
                return api.getConsolidatedCertificationSystemsHollow(ordinal);
            }
        };
    }

    public Iterable<EpisodesHollow> findEpisodesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<EpisodesHollow>(matches.iterator()) {
            public EpisodesHollow getData(int ordinal) {
                return api.getEpisodesHollow(ordinal);
            }
        };
    }

    public Iterable<FestivalsHollow> findFestivalsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<FestivalsHollow>(matches.iterator()) {
            public FestivalsHollow getData(int ordinal) {
                return api.getFestivalsHollow(ordinal);
            }
        };
    }

    public Iterable<LanguagesHollow> findLanguagesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LanguagesHollow>(matches.iterator()) {
            public LanguagesHollow getData(int ordinal) {
                return api.getLanguagesHollow(ordinal);
            }
        };
    }

    public Iterable<MovieRatingsHollow> findMovieRatingsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MovieRatingsHollow>(matches.iterator()) {
            public MovieRatingsHollow getData(int ordinal) {
                return api.getMovieRatingsHollow(ordinal);
            }
        };
    }

    public Iterable<MoviesHollow> findMoviesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MoviesHollow>(matches.iterator()) {
            public MoviesHollow getData(int ordinal) {
                return api.getMoviesHollow(ordinal);
            }
        };
    }

    public Iterable<PersonAliasesHollow> findPersonAliasesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonAliasesHollow>(matches.iterator()) {
            public PersonAliasesHollow getData(int ordinal) {
                return api.getPersonAliasesHollow(ordinal);
            }
        };
    }

    public Iterable<PersonCharacterResourceHollow> findPersonCharacterResourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonCharacterResourceHollow>(matches.iterator()) {
            public PersonCharacterResourceHollow getData(int ordinal) {
                return api.getPersonCharacterResourceHollow(ordinal);
            }
        };
    }

    public Iterable<PersonsHollow> findPersonsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonsHollow>(matches.iterator()) {
            public PersonsHollow getData(int ordinal) {
                return api.getPersonsHollow(ordinal);
            }
        };
    }

    public Iterable<RatingsHollow> findRatingsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RatingsHollow>(matches.iterator()) {
            public RatingsHollow getData(int ordinal) {
                return api.getRatingsHollow(ordinal);
            }
        };
    }

    public Iterable<ShowMemberTypesHollow> findShowMemberTypesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowMemberTypesHollow>(matches.iterator()) {
            public ShowMemberTypesHollow getData(int ordinal) {
                return api.getShowMemberTypesHollow(ordinal);
            }
        };
    }

    public Iterable<StoriesSynopsesHollow> findStoriesSynopsesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StoriesSynopsesHollow>(matches.iterator()) {
            public StoriesSynopsesHollow getData(int ordinal) {
                return api.getStoriesSynopsesHollow(ordinal);
            }
        };
    }

    public Iterable<TurboCollectionsHollow> findTurboCollectionsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TurboCollectionsHollow>(matches.iterator()) {
            public TurboCollectionsHollow getData(int ordinal) {
                return api.getTurboCollectionsHollow(ordinal);
            }
        };
    }

    public Iterable<VMSAwardHollow> findVMSAwardMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VMSAwardHollow>(matches.iterator()) {
            public VMSAwardHollow getData(int ordinal) {
                return api.getVMSAwardHollow(ordinal);
            }
        };
    }

    public Iterable<VideoArtworkSourceHollow> findVideoArtworkSourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoArtworkSourceHollow>(matches.iterator()) {
            public VideoArtworkSourceHollow getData(int ordinal) {
                return api.getVideoArtworkSourceHollow(ordinal);
            }
        };
    }

    public Iterable<VideoAwardMappingHollow> findVideoAwardMappingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoAwardMappingHollow>(matches.iterator()) {
            public VideoAwardMappingHollow getData(int ordinal) {
                return api.getVideoAwardMappingHollow(ordinal);
            }
        };
    }

    public Iterable<VideoAwardListHollow> findVideoAwardListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoAwardListHollow>(matches.iterator()) {
            public VideoAwardListHollow getData(int ordinal) {
                return api.getVideoAwardListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoAwardHollow> findVideoAwardMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoAwardHollow>(matches.iterator()) {
            public VideoAwardHollow getData(int ordinal) {
                return api.getVideoAwardHollow(ordinal);
            }
        };
    }

    public Iterable<VideoDateWindowHollow> findVideoDateWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoDateWindowHollow>(matches.iterator()) {
            public VideoDateWindowHollow getData(int ordinal) {
                return api.getVideoDateWindowHollow(ordinal);
            }
        };
    }

    public Iterable<VideoDateWindowListHollow> findVideoDateWindowListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoDateWindowListHollow>(matches.iterator()) {
            public VideoDateWindowListHollow getData(int ordinal) {
                return api.getVideoDateWindowListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoDateHollow> findVideoDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoDateHollow>(matches.iterator()) {
            public VideoDateHollow getData(int ordinal) {
                return api.getVideoDateHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralAliasHollow> findVideoGeneralAliasMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralAliasHollow>(matches.iterator()) {
            public VideoGeneralAliasHollow getData(int ordinal) {
                return api.getVideoGeneralAliasHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralAliasListHollow> findVideoGeneralAliasListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralAliasListHollow>(matches.iterator()) {
            public VideoGeneralAliasListHollow getData(int ordinal) {
                return api.getVideoGeneralAliasListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralEpisodeTypeHollow> findVideoGeneralEpisodeTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralEpisodeTypeHollow>(matches.iterator()) {
            public VideoGeneralEpisodeTypeHollow getData(int ordinal) {
                return api.getVideoGeneralEpisodeTypeHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralEpisodeTypeListHollow> findVideoGeneralEpisodeTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralEpisodeTypeListHollow>(matches.iterator()) {
            public VideoGeneralEpisodeTypeListHollow getData(int ordinal) {
                return api.getVideoGeneralEpisodeTypeListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralTitleTypeHollow> findVideoGeneralTitleTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralTitleTypeHollow>(matches.iterator()) {
            public VideoGeneralTitleTypeHollow getData(int ordinal) {
                return api.getVideoGeneralTitleTypeHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralTitleTypeListHollow> findVideoGeneralTitleTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralTitleTypeListHollow>(matches.iterator()) {
            public VideoGeneralTitleTypeListHollow getData(int ordinal) {
                return api.getVideoGeneralTitleTypeListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoGeneralHollow> findVideoGeneralMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoGeneralHollow>(matches.iterator()) {
            public VideoGeneralHollow getData(int ordinal) {
                return api.getVideoGeneralHollow(ordinal);
            }
        };
    }

    public Iterable<VideoIdHollow> findVideoIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoIdHollow>(matches.iterator()) {
            public VideoIdHollow getData(int ordinal) {
                return api.getVideoIdHollow(ordinal);
            }
        };
    }

    public Iterable<ListOfVideoIdsHollow> findListOfVideoIdsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfVideoIdsHollow>(matches.iterator()) {
            public ListOfVideoIdsHollow getData(int ordinal) {
                return api.getListOfVideoIdsHollow(ordinal);
            }
        };
    }

    public Iterable<PersonBioHollow> findPersonBioMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonBioHollow>(matches.iterator()) {
            public PersonBioHollow getData(int ordinal) {
                return api.getPersonBioHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingAdvisoryIdHollow> findVideoRatingAdvisoryIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingAdvisoryIdHollow>(matches.iterator()) {
            public VideoRatingAdvisoryIdHollow getData(int ordinal) {
                return api.getVideoRatingAdvisoryIdHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingAdvisoryIdListHollow> findVideoRatingAdvisoryIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingAdvisoryIdListHollow>(matches.iterator()) {
            public VideoRatingAdvisoryIdListHollow getData(int ordinal) {
                return api.getVideoRatingAdvisoryIdListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingAdvisoriesHollow> findVideoRatingAdvisoriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingAdvisoriesHollow>(matches.iterator()) {
            public VideoRatingAdvisoriesHollow getData(int ordinal) {
                return api.getVideoRatingAdvisoriesHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedVideoCountryRatingHollow> findConsolidatedVideoCountryRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedVideoCountryRatingHollow>(matches.iterator()) {
            public ConsolidatedVideoCountryRatingHollow getData(int ordinal) {
                return api.getConsolidatedVideoCountryRatingHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedVideoCountryRatingListHollow> findConsolidatedVideoCountryRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedVideoCountryRatingListHollow>(matches.iterator()) {
            public ConsolidatedVideoCountryRatingListHollow getData(int ordinal) {
                return api.getConsolidatedVideoCountryRatingListHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedVideoRatingHollow> findConsolidatedVideoRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedVideoRatingHollow>(matches.iterator()) {
            public ConsolidatedVideoRatingHollow getData(int ordinal) {
                return api.getConsolidatedVideoRatingHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedVideoRatingListHollow> findConsolidatedVideoRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedVideoRatingListHollow>(matches.iterator()) {
            public ConsolidatedVideoRatingListHollow getData(int ordinal) {
                return api.getConsolidatedVideoRatingListHollow(ordinal);
            }
        };
    }

    public Iterable<ConsolidatedVideoRatingsHollow> findConsolidatedVideoRatingsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ConsolidatedVideoRatingsHollow>(matches.iterator()) {
            public ConsolidatedVideoRatingsHollow getData(int ordinal) {
                return api.getConsolidatedVideoRatingsHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingRatingReasonIdsHollow> findVideoRatingRatingReasonIdsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingRatingReasonIdsHollow>(matches.iterator()) {
            public VideoRatingRatingReasonIdsHollow getData(int ordinal) {
                return api.getVideoRatingRatingReasonIdsHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingRatingReasonArrayOfIdsHollow> findVideoRatingRatingReasonArrayOfIdsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingRatingReasonArrayOfIdsHollow>(matches.iterator()) {
            public VideoRatingRatingReasonArrayOfIdsHollow getData(int ordinal) {
                return api.getVideoRatingRatingReasonArrayOfIdsHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingRatingReasonHollow> findVideoRatingRatingReasonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingRatingReasonHollow>(matches.iterator()) {
            public VideoRatingRatingReasonHollow getData(int ordinal) {
                return api.getVideoRatingRatingReasonHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingRatingHollow> findVideoRatingRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingRatingHollow>(matches.iterator()) {
            public VideoRatingRatingHollow getData(int ordinal) {
                return api.getVideoRatingRatingHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingArrayOfRatingHollow> findVideoRatingArrayOfRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingArrayOfRatingHollow>(matches.iterator()) {
            public VideoRatingArrayOfRatingHollow getData(int ordinal) {
                return api.getVideoRatingArrayOfRatingHollow(ordinal);
            }
        };
    }

    public Iterable<VideoRatingHollow> findVideoRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoRatingHollow>(matches.iterator()) {
            public VideoRatingHollow getData(int ordinal) {
                return api.getVideoRatingHollow(ordinal);
            }
        };
    }

    public Iterable<VideoStreamCropParamsHollow> findVideoStreamCropParamsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoStreamCropParamsHollow>(matches.iterator()) {
            public VideoStreamCropParamsHollow getData(int ordinal) {
                return api.getVideoStreamCropParamsHollow(ordinal);
            }
        };
    }

    public Iterable<VideoStreamInfoHollow> findVideoStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoStreamInfoHollow>(matches.iterator()) {
            public VideoStreamInfoHollow getData(int ordinal) {
                return api.getVideoStreamInfoHollow(ordinal);
            }
        };
    }

    public Iterable<StreamNonImageInfoHollow> findStreamNonImageInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<StreamNonImageInfoHollow>(matches.iterator()) {
            public StreamNonImageInfoHollow getData(int ordinal) {
                return api.getStreamNonImageInfoHollow(ordinal);
            }
        };
    }

    public Iterable<PackageStreamHollow> findPackageStreamMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageStreamHollow>(matches.iterator()) {
            public PackageStreamHollow getData(int ordinal) {
                return api.getPackageStreamHollow(ordinal);
            }
        };
    }

    public Iterable<PackageStreamSetHollow> findPackageStreamSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageStreamSetHollow>(matches.iterator()) {
            public PackageStreamSetHollow getData(int ordinal) {
                return api.getPackageStreamSetHollow(ordinal);
            }
        };
    }

    public Iterable<PackageHollow> findPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PackageHollow>(matches.iterator()) {
            public PackageHollow getData(int ordinal) {
                return api.getPackageHollow(ordinal);
            }
        };
    }

    public Iterable<VideoTypeMediaHollow> findVideoTypeMediaMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeMediaHollow>(matches.iterator()) {
            public VideoTypeMediaHollow getData(int ordinal) {
                return api.getVideoTypeMediaHollow(ordinal);
            }
        };
    }

    public Iterable<VideoTypeMediaListHollow> findVideoTypeMediaListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeMediaListHollow>(matches.iterator()) {
            public VideoTypeMediaListHollow getData(int ordinal) {
                return api.getVideoTypeMediaListHollow(ordinal);
            }
        };
    }

    public Iterable<VideoTypeDescriptorHollow> findVideoTypeDescriptorMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeDescriptorHollow>(matches.iterator()) {
            public VideoTypeDescriptorHollow getData(int ordinal) {
                return api.getVideoTypeDescriptorHollow(ordinal);
            }
        };
    }

    public Iterable<VideoTypeDescriptorSetHollow> findVideoTypeDescriptorSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeDescriptorSetHollow>(matches.iterator()) {
            public VideoTypeDescriptorSetHollow getData(int ordinal) {
                return api.getVideoTypeDescriptorSetHollow(ordinal);
            }
        };
    }

    public Iterable<VideoTypeHollow> findVideoTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoTypeHollow>(matches.iterator()) {
            public VideoTypeHollow getData(int ordinal) {
                return api.getVideoTypeHollow(ordinal);
            }
        };
    }

}