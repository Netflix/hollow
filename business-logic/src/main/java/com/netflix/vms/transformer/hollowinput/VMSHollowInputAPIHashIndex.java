package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Collections;
import java.lang.Iterable;
import java.util.Iterator;

public class VMSHollowInputAPIHashIndex implements HollowConsumer.RefreshListener {

    private HollowHashIndex idx;
    private VMSHollowInputAPI api;
    private final String queryType;    private final String selectFieldPath;
    private final String matchFieldPaths[];

    public VMSHollowInputAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        this.queryType = queryType;        this.selectFieldPath = selectFieldPath;
        this.matchFieldPaths = matchFieldPaths;
        consumer.getRefreshLock().lock();
        try {
            this.api = (VMSHollowInputAPI)consumer.getAPI();
            this.idx = new HollowHashIndex(consumer.getStateEngine(), queryType, selectFieldPath, matchFieldPaths);
            consumer.addRefreshListener(this);
        } catch(ClassCastException cce) {
            throw new ClassCastException("The HollowConsumer provided was not created with the VMSHollowInputAPI generated API class.");
        } finally {
            consumer.getRefreshLock().unlock();
        }
    }

    public Iterable<CharacterQuoteHollow> findCharacterQuoteMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharacterQuoteHollow>() {
            public Iterator<CharacterQuoteHollow> iterator() {
                return new Iterator<CharacterQuoteHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharacterQuoteHollow next() {
                        CharacterQuoteHollow obj = api.getCharacterQuoteHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CharacterQuoteListHollow> findCharacterQuoteListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharacterQuoteListHollow>() {
            public Iterator<CharacterQuoteListHollow> iterator() {
                return new Iterator<CharacterQuoteListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharacterQuoteListHollow next() {
                        CharacterQuoteListHollow obj = api.getCharacterQuoteListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ChunkDurationsStringHollow> findChunkDurationsStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ChunkDurationsStringHollow>() {
            public Iterator<ChunkDurationsStringHollow> iterator() {
                return new Iterator<ChunkDurationsStringHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ChunkDurationsStringHollow next() {
                        ChunkDurationsStringHollow obj = api.getChunkDurationsStringHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CodecPrivateDataStringHollow> findCodecPrivateDataStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CodecPrivateDataStringHollow>() {
            public Iterator<CodecPrivateDataStringHollow> iterator() {
                return new Iterator<CodecPrivateDataStringHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CodecPrivateDataStringHollow next() {
                        CodecPrivateDataStringHollow obj = api.getCodecPrivateDataStringHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DateHollow> findDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DateHollow>() {
            public Iterator<DateHollow> iterator() {
                return new Iterator<DateHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DateHollow next() {
                        DateHollow obj = api.getDateHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DerivativeTagHollow> findDerivativeTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DerivativeTagHollow>() {
            public Iterator<DerivativeTagHollow> iterator() {
                return new Iterator<DerivativeTagHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DerivativeTagHollow next() {
                        DerivativeTagHollow obj = api.getDerivativeTagHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DownloadableIdHollow> findDownloadableIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DownloadableIdHollow>() {
            public Iterator<DownloadableIdHollow> iterator() {
                return new Iterator<DownloadableIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DownloadableIdHollow next() {
                        DownloadableIdHollow obj = api.getDownloadableIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DownloadableIdListHollow> findDownloadableIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DownloadableIdListHollow>() {
            public Iterator<DownloadableIdListHollow> iterator() {
                return new Iterator<DownloadableIdListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DownloadableIdListHollow next() {
                        DownloadableIdListHollow obj = api.getDownloadableIdListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DrmInfoStringHollow> findDrmInfoStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DrmInfoStringHollow>() {
            public Iterator<DrmInfoStringHollow> iterator() {
                return new Iterator<DrmInfoStringHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DrmInfoStringHollow next() {
                        DrmInfoStringHollow obj = api.getDrmInfoStringHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<EpisodeHollow> findEpisodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<EpisodeHollow>() {
            public Iterator<EpisodeHollow> iterator() {
                return new Iterator<EpisodeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public EpisodeHollow next() {
                        EpisodeHollow obj = api.getEpisodeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<EpisodeListHollow> findEpisodeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<EpisodeListHollow>() {
            public Iterator<EpisodeListHollow> iterator() {
                return new Iterator<EpisodeListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public EpisodeListHollow next() {
                        EpisodeListHollow obj = api.getEpisodeListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ExplicitDateHollow> findExplicitDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ExplicitDateHollow>() {
            public Iterator<ExplicitDateHollow> iterator() {
                return new Iterator<ExplicitDateHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ExplicitDateHollow next() {
                        ExplicitDateHollow obj = api.getExplicitDateHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ISOCountryHollow> findISOCountryMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ISOCountryHollow>() {
            public Iterator<ISOCountryHollow> iterator() {
                return new Iterator<ISOCountryHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ISOCountryHollow next() {
                        ISOCountryHollow obj = api.getISOCountryHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ISOCountryListHollow> findISOCountryListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ISOCountryListHollow>() {
            public Iterator<ISOCountryListHollow> iterator() {
                return new Iterator<ISOCountryListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ISOCountryListHollow next() {
                        ISOCountryListHollow obj = api.getISOCountryListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ISOCountrySetHollow> findISOCountrySetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ISOCountrySetHollow>() {
            public Iterator<ISOCountrySetHollow> iterator() {
                return new Iterator<ISOCountrySetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ISOCountrySetHollow next() {
                        ISOCountrySetHollow obj = api.getISOCountrySetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfDerivativeTagHollow> findListOfDerivativeTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfDerivativeTagHollow>() {
            public Iterator<ListOfDerivativeTagHollow> iterator() {
                return new Iterator<ListOfDerivativeTagHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfDerivativeTagHollow next() {
                        ListOfDerivativeTagHollow obj = api.getListOfDerivativeTagHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MapKeyHollow> findMapKeyMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MapKeyHollow>() {
            public Iterator<MapKeyHollow> iterator() {
                return new Iterator<MapKeyHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MapKeyHollow next() {
                        MapKeyHollow obj = api.getMapKeyHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MapOfFlagsFirstDisplayDatesHollow> findMapOfFlagsFirstDisplayDatesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MapOfFlagsFirstDisplayDatesHollow>() {
            public Iterator<MapOfFlagsFirstDisplayDatesHollow> iterator() {
                return new Iterator<MapOfFlagsFirstDisplayDatesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MapOfFlagsFirstDisplayDatesHollow next() {
                        MapOfFlagsFirstDisplayDatesHollow obj = api.getMapOfFlagsFirstDisplayDatesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<FlagsHollow> findFlagsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<FlagsHollow>() {
            public Iterator<FlagsHollow> iterator() {
                return new Iterator<FlagsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public FlagsHollow next() {
                        FlagsHollow obj = api.getFlagsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonCharacterHollow> findPersonCharacterMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonCharacterHollow>() {
            public Iterator<PersonCharacterHollow> iterator() {
                return new Iterator<PersonCharacterHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonCharacterHollow next() {
                        PersonCharacterHollow obj = api.getPersonCharacterHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CharacterListHollow> findCharacterListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharacterListHollow>() {
            public Iterator<CharacterListHollow> iterator() {
                return new Iterator<CharacterListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharacterListHollow next() {
                        CharacterListHollow obj = api.getCharacterListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MovieCharacterPersonHollow> findMovieCharacterPersonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MovieCharacterPersonHollow>() {
            public Iterator<MovieCharacterPersonHollow> iterator() {
                return new Iterator<MovieCharacterPersonHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MovieCharacterPersonHollow next() {
                        MovieCharacterPersonHollow obj = api.getMovieCharacterPersonHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonVideoAliasIdHollow> findPersonVideoAliasIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonVideoAliasIdHollow>() {
            public Iterator<PersonVideoAliasIdHollow> iterator() {
                return new Iterator<PersonVideoAliasIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonVideoAliasIdHollow next() {
                        PersonVideoAliasIdHollow obj = api.getPersonVideoAliasIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonVideoAliasIdsListHollow> findPersonVideoAliasIdsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonVideoAliasIdsListHollow>() {
            public Iterator<PersonVideoAliasIdsListHollow> iterator() {
                return new Iterator<PersonVideoAliasIdsListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonVideoAliasIdsListHollow next() {
                        PersonVideoAliasIdsListHollow obj = api.getPersonVideoAliasIdsListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonVideoRoleHollow> findPersonVideoRoleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonVideoRoleHollow>() {
            public Iterator<PersonVideoRoleHollow> iterator() {
                return new Iterator<PersonVideoRoleHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonVideoRoleHollow next() {
                        PersonVideoRoleHollow obj = api.getPersonVideoRoleHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonVideoRolesListHollow> findPersonVideoRolesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonVideoRolesListHollow>() {
            public Iterator<PersonVideoRolesListHollow> iterator() {
                return new Iterator<PersonVideoRolesListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonVideoRolesListHollow next() {
                        PersonVideoRolesListHollow obj = api.getPersonVideoRolesListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonVideoHollow> findPersonVideoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonVideoHollow>() {
            public Iterator<PersonVideoHollow> iterator() {
                return new Iterator<PersonVideoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonVideoHollow next() {
                        PersonVideoHollow obj = api.getPersonVideoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsAssetSetIdHollow> findRightsAssetSetIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsAssetSetIdHollow>() {
            public Iterator<RightsAssetSetIdHollow> iterator() {
                return new Iterator<RightsAssetSetIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsAssetSetIdHollow next() {
                        RightsAssetSetIdHollow obj = api.getRightsAssetSetIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsContractPackageHollow> findRightsContractPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsContractPackageHollow>() {
            public Iterator<RightsContractPackageHollow> iterator() {
                return new Iterator<RightsContractPackageHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsContractPackageHollow next() {
                        RightsContractPackageHollow obj = api.getRightsContractPackageHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfRightsContractPackageHollow> findListOfRightsContractPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfRightsContractPackageHollow>() {
            public Iterator<ListOfRightsContractPackageHollow> iterator() {
                return new Iterator<ListOfRightsContractPackageHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfRightsContractPackageHollow next() {
                        ListOfRightsContractPackageHollow obj = api.getListOfRightsContractPackageHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsWindowContractHollow> findRightsWindowContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsWindowContractHollow>() {
            public Iterator<RightsWindowContractHollow> iterator() {
                return new Iterator<RightsWindowContractHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsWindowContractHollow next() {
                        RightsWindowContractHollow obj = api.getRightsWindowContractHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfRightsWindowContractHollow> findListOfRightsWindowContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfRightsWindowContractHollow>() {
            public Iterator<ListOfRightsWindowContractHollow> iterator() {
                return new Iterator<ListOfRightsWindowContractHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfRightsWindowContractHollow next() {
                        ListOfRightsWindowContractHollow obj = api.getListOfRightsWindowContractHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsWindowHollow> findRightsWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsWindowHollow>() {
            public Iterator<RightsWindowHollow> iterator() {
                return new Iterator<RightsWindowHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsWindowHollow next() {
                        RightsWindowHollow obj = api.getRightsWindowHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfRightsWindowHollow> findListOfRightsWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfRightsWindowHollow>() {
            public Iterator<ListOfRightsWindowHollow> iterator() {
                return new Iterator<ListOfRightsWindowHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfRightsWindowHollow next() {
                        ListOfRightsWindowHollow obj = api.getListOfRightsWindowHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseWindowHollow> findRolloutPhaseWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseWindowHollow>() {
            public Iterator<RolloutPhaseWindowHollow> iterator() {
                return new Iterator<RolloutPhaseWindowHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseWindowHollow next() {
                        RolloutPhaseWindowHollow obj = api.getRolloutPhaseWindowHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseWindowMapHollow> findRolloutPhaseWindowMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseWindowMapHollow>() {
            public Iterator<RolloutPhaseWindowMapHollow> iterator() {
                return new Iterator<RolloutPhaseWindowMapHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseWindowMapHollow next() {
                        RolloutPhaseWindowMapHollow obj = api.getRolloutPhaseWindowMapHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SeasonHollow> findSeasonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SeasonHollow>() {
            public Iterator<SeasonHollow> iterator() {
                return new Iterator<SeasonHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SeasonHollow next() {
                        SeasonHollow obj = api.getSeasonHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SeasonListHollow> findSeasonListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SeasonListHollow>() {
            public Iterator<SeasonListHollow> iterator() {
                return new Iterator<SeasonListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SeasonListHollow next() {
                        SeasonListHollow obj = api.getSeasonListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ShowMemberTypeHollow> findShowMemberTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ShowMemberTypeHollow>() {
            public Iterator<ShowMemberTypeHollow> iterator() {
                return new Iterator<ShowMemberTypeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ShowMemberTypeHollow next() {
                        ShowMemberTypeHollow obj = api.getShowMemberTypeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ShowMemberTypeListHollow> findShowMemberTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ShowMemberTypeListHollow>() {
            public Iterator<ShowMemberTypeListHollow> iterator() {
                return new Iterator<ShowMemberTypeListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ShowMemberTypeListHollow next() {
                        ShowMemberTypeListHollow obj = api.getShowMemberTypeListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ShowCountryLabelHollow> findShowCountryLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ShowCountryLabelHollow>() {
            public Iterator<ShowCountryLabelHollow> iterator() {
                return new Iterator<ShowCountryLabelHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ShowCountryLabelHollow next() {
                        ShowCountryLabelHollow obj = api.getShowCountryLabelHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ShowSeasonEpisodeHollow> findShowSeasonEpisodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ShowSeasonEpisodeHollow>() {
            public Iterator<ShowSeasonEpisodeHollow> iterator() {
                return new Iterator<ShowSeasonEpisodeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ShowSeasonEpisodeHollow next() {
                        ShowSeasonEpisodeHollow obj = api.getShowSeasonEpisodeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamAssetMetadataHollow> findStreamAssetMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamAssetMetadataHollow>() {
            public Iterator<StreamAssetMetadataHollow> iterator() {
                return new Iterator<StreamAssetMetadataHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamAssetMetadataHollow next() {
                        StreamAssetMetadataHollow obj = api.getStreamAssetMetadataHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamBoxInfoKeyHollow> findStreamBoxInfoKeyMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamBoxInfoKeyHollow>() {
            public Iterator<StreamBoxInfoKeyHollow> iterator() {
                return new Iterator<StreamBoxInfoKeyHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamBoxInfoKeyHollow next() {
                        StreamBoxInfoKeyHollow obj = api.getStreamBoxInfoKeyHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamBoxInfoHollow> findStreamBoxInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamBoxInfoHollow>() {
            public Iterator<StreamBoxInfoHollow> iterator() {
                return new Iterator<StreamBoxInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamBoxInfoHollow next() {
                        StreamBoxInfoHollow obj = api.getStreamBoxInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SetOfStreamBoxInfoHollow> findSetOfStreamBoxInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SetOfStreamBoxInfoHollow>() {
            public Iterator<SetOfStreamBoxInfoHollow> iterator() {
                return new Iterator<SetOfStreamBoxInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SetOfStreamBoxInfoHollow next() {
                        SetOfStreamBoxInfoHollow obj = api.getSetOfStreamBoxInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DashStreamHeaderDataHollow> findDashStreamHeaderDataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DashStreamHeaderDataHollow>() {
            public Iterator<DashStreamHeaderDataHollow> iterator() {
                return new Iterator<DashStreamHeaderDataHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DashStreamHeaderDataHollow next() {
                        DashStreamHeaderDataHollow obj = api.getDashStreamHeaderDataHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamDimensionsHollow> findStreamDimensionsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamDimensionsHollow>() {
            public Iterator<StreamDimensionsHollow> iterator() {
                return new Iterator<StreamDimensionsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamDimensionsHollow next() {
                        StreamDimensionsHollow obj = api.getStreamDimensionsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamFileIdentificationHollow> findStreamFileIdentificationMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamFileIdentificationHollow>() {
            public Iterator<StreamFileIdentificationHollow> iterator() {
                return new Iterator<StreamFileIdentificationHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamFileIdentificationHollow next() {
                        StreamFileIdentificationHollow obj = api.getStreamFileIdentificationHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamProfileIdHollow> findStreamProfileIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamProfileIdHollow>() {
            public Iterator<StreamProfileIdHollow> iterator() {
                return new Iterator<StreamProfileIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamProfileIdHollow next() {
                        StreamProfileIdHollow obj = api.getStreamProfileIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamProfileIdListHollow> findStreamProfileIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamProfileIdListHollow>() {
            public Iterator<StreamProfileIdListHollow> iterator() {
                return new Iterator<StreamProfileIdListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamProfileIdListHollow next() {
                        StreamProfileIdListHollow obj = api.getStreamProfileIdListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StringHollow> findStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StringHollow>() {
            public Iterator<StringHollow> iterator() {
                return new Iterator<StringHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StringHollow next() {
                        StringHollow obj = api.getStringHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AbsoluteScheduleHollow> findAbsoluteScheduleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AbsoluteScheduleHollow>() {
            public Iterator<AbsoluteScheduleHollow> iterator() {
                return new Iterator<AbsoluteScheduleHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AbsoluteScheduleHollow next() {
                        AbsoluteScheduleHollow obj = api.getAbsoluteScheduleHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ArtWorkImageTypeHollow> findArtWorkImageTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ArtWorkImageTypeHollow>() {
            public Iterator<ArtWorkImageTypeHollow> iterator() {
                return new Iterator<ArtWorkImageTypeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ArtWorkImageTypeHollow next() {
                        ArtWorkImageTypeHollow obj = api.getArtWorkImageTypeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ArtworkRecipeHollow> findArtworkRecipeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ArtworkRecipeHollow>() {
            public Iterator<ArtworkRecipeHollow> iterator() {
                return new Iterator<ArtworkRecipeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ArtworkRecipeHollow next() {
                        ArtworkRecipeHollow obj = api.getArtworkRecipeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AudioStreamInfoHollow> findAudioStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AudioStreamInfoHollow>() {
            public Iterator<AudioStreamInfoHollow> iterator() {
                return new Iterator<AudioStreamInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AudioStreamInfoHollow next() {
                        AudioStreamInfoHollow obj = api.getAudioStreamInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CSMReviewHollow> findCSMReviewMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CSMReviewHollow>() {
            public Iterator<CSMReviewHollow> iterator() {
                return new Iterator<CSMReviewHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CSMReviewHollow next() {
                        CSMReviewHollow obj = api.getCSMReviewHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CacheDeploymentIntentHollow> findCacheDeploymentIntentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CacheDeploymentIntentHollow>() {
            public Iterator<CacheDeploymentIntentHollow> iterator() {
                return new Iterator<CacheDeploymentIntentHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CacheDeploymentIntentHollow next() {
                        CacheDeploymentIntentHollow obj = api.getCacheDeploymentIntentHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CdnHollow> findCdnMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CdnHollow>() {
            public Iterator<CdnHollow> iterator() {
                return new Iterator<CdnHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CdnHollow next() {
                        CdnHollow obj = api.getCdnHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CdnDeploymentHollow> findCdnDeploymentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CdnDeploymentHollow>() {
            public Iterator<CdnDeploymentHollow> iterator() {
                return new Iterator<CdnDeploymentHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CdnDeploymentHollow next() {
                        CdnDeploymentHollow obj = api.getCdnDeploymentHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CdnDeploymentSetHollow> findCdnDeploymentSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CdnDeploymentSetHollow>() {
            public Iterator<CdnDeploymentSetHollow> iterator() {
                return new Iterator<CdnDeploymentSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CdnDeploymentSetHollow next() {
                        CdnDeploymentSetHollow obj = api.getCdnDeploymentSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CertificationSystemRatingHollow> findCertificationSystemRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CertificationSystemRatingHollow>() {
            public Iterator<CertificationSystemRatingHollow> iterator() {
                return new Iterator<CertificationSystemRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CertificationSystemRatingHollow next() {
                        CertificationSystemRatingHollow obj = api.getCertificationSystemRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CertificationSystemRatingListHollow> findCertificationSystemRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CertificationSystemRatingListHollow>() {
            public Iterator<CertificationSystemRatingListHollow> iterator() {
                return new Iterator<CertificationSystemRatingListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CertificationSystemRatingListHollow next() {
                        CertificationSystemRatingListHollow obj = api.getCertificationSystemRatingListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CertificationSystemHollow> findCertificationSystemMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CertificationSystemHollow>() {
            public Iterator<CertificationSystemHollow> iterator() {
                return new Iterator<CertificationSystemHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CertificationSystemHollow next() {
                        CertificationSystemHollow obj = api.getCertificationSystemHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CharacterElementsHollow> findCharacterElementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharacterElementsHollow>() {
            public Iterator<CharacterElementsHollow> iterator() {
                return new Iterator<CharacterElementsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharacterElementsHollow next() {
                        CharacterElementsHollow obj = api.getCharacterElementsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CharacterHollow> findCharacterMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharacterHollow>() {
            public Iterator<CharacterHollow> iterator() {
                return new Iterator<CharacterHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharacterHollow next() {
                        CharacterHollow obj = api.getCharacterHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DamMerchStillsMomentHollow> findDamMerchStillsMomentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DamMerchStillsMomentHollow>() {
            public Iterator<DamMerchStillsMomentHollow> iterator() {
                return new Iterator<DamMerchStillsMomentHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DamMerchStillsMomentHollow next() {
                        DamMerchStillsMomentHollow obj = api.getDamMerchStillsMomentHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DamMerchStillsHollow> findDamMerchStillsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DamMerchStillsHollow>() {
            public Iterator<DamMerchStillsHollow> iterator() {
                return new Iterator<DamMerchStillsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DamMerchStillsHollow next() {
                        DamMerchStillsHollow obj = api.getDamMerchStillsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DisallowedSubtitleLangCodeHollow> findDisallowedSubtitleLangCodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DisallowedSubtitleLangCodeHollow>() {
            public Iterator<DisallowedSubtitleLangCodeHollow> iterator() {
                return new Iterator<DisallowedSubtitleLangCodeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DisallowedSubtitleLangCodeHollow next() {
                        DisallowedSubtitleLangCodeHollow obj = api.getDisallowedSubtitleLangCodeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DisallowedSubtitleLangCodesListHollow> findDisallowedSubtitleLangCodesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DisallowedSubtitleLangCodesListHollow>() {
            public Iterator<DisallowedSubtitleLangCodesListHollow> iterator() {
                return new Iterator<DisallowedSubtitleLangCodesListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DisallowedSubtitleLangCodesListHollow next() {
                        DisallowedSubtitleLangCodesListHollow obj = api.getDisallowedSubtitleLangCodesListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DisallowedAssetBundleHollow> findDisallowedAssetBundleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DisallowedAssetBundleHollow>() {
            public Iterator<DisallowedAssetBundleHollow> iterator() {
                return new Iterator<DisallowedAssetBundleHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DisallowedAssetBundleHollow next() {
                        DisallowedAssetBundleHollow obj = api.getDisallowedAssetBundleHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DisallowedAssetBundlesListHollow> findDisallowedAssetBundlesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DisallowedAssetBundlesListHollow>() {
            public Iterator<DisallowedAssetBundlesListHollow> iterator() {
                return new Iterator<DisallowedAssetBundlesListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DisallowedAssetBundlesListHollow next() {
                        DisallowedAssetBundlesListHollow obj = api.getDisallowedAssetBundlesListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ContractHollow> findContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ContractHollow>() {
            public Iterator<ContractHollow> iterator() {
                return new Iterator<ContractHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ContractHollow next() {
                        ContractHollow obj = api.getContractHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DrmHeaderInfoHollow> findDrmHeaderInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DrmHeaderInfoHollow>() {
            public Iterator<DrmHeaderInfoHollow> iterator() {
                return new Iterator<DrmHeaderInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DrmHeaderInfoHollow next() {
                        DrmHeaderInfoHollow obj = api.getDrmHeaderInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DrmHeaderInfoListHollow> findDrmHeaderInfoListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DrmHeaderInfoListHollow>() {
            public Iterator<DrmHeaderInfoListHollow> iterator() {
                return new Iterator<DrmHeaderInfoListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DrmHeaderInfoListHollow next() {
                        DrmHeaderInfoListHollow obj = api.getDrmHeaderInfoListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DrmSystemIdentifiersHollow> findDrmSystemIdentifiersMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DrmSystemIdentifiersHollow>() {
            public Iterator<DrmSystemIdentifiersHollow> iterator() {
                return new Iterator<DrmSystemIdentifiersHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DrmSystemIdentifiersHollow next() {
                        DrmSystemIdentifiersHollow obj = api.getDrmSystemIdentifiersHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<IPLArtworkDerivativeHollow> findIPLArtworkDerivativeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<IPLArtworkDerivativeHollow>() {
            public Iterator<IPLArtworkDerivativeHollow> iterator() {
                return new Iterator<IPLArtworkDerivativeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public IPLArtworkDerivativeHollow next() {
                        IPLArtworkDerivativeHollow obj = api.getIPLArtworkDerivativeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<IPLDerivativeSetHollow> findIPLDerivativeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<IPLDerivativeSetHollow>() {
            public Iterator<IPLDerivativeSetHollow> iterator() {
                return new Iterator<IPLDerivativeSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public IPLDerivativeSetHollow next() {
                        IPLDerivativeSetHollow obj = api.getIPLDerivativeSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<IPLDerivativeGroupHollow> findIPLDerivativeGroupMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<IPLDerivativeGroupHollow>() {
            public Iterator<IPLDerivativeGroupHollow> iterator() {
                return new Iterator<IPLDerivativeGroupHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public IPLDerivativeGroupHollow next() {
                        IPLDerivativeGroupHollow obj = api.getIPLDerivativeGroupHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<IPLDerivativeGroupSetHollow> findIPLDerivativeGroupSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<IPLDerivativeGroupSetHollow>() {
            public Iterator<IPLDerivativeGroupSetHollow> iterator() {
                return new Iterator<IPLDerivativeGroupSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public IPLDerivativeGroupSetHollow next() {
                        IPLDerivativeGroupSetHollow obj = api.getIPLDerivativeGroupSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<IPLArtworkDerivativeSetHollow> findIPLArtworkDerivativeSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<IPLArtworkDerivativeSetHollow>() {
            public Iterator<IPLArtworkDerivativeSetHollow> iterator() {
                return new Iterator<IPLArtworkDerivativeSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public IPLArtworkDerivativeSetHollow next() {
                        IPLArtworkDerivativeSetHollow obj = api.getIPLArtworkDerivativeSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ImageStreamInfoHollow> findImageStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ImageStreamInfoHollow>() {
            public Iterator<ImageStreamInfoHollow> iterator() {
                return new Iterator<ImageStreamInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ImageStreamInfoHollow next() {
                        ImageStreamInfoHollow obj = api.getImageStreamInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfContractHollow> findListOfContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfContractHollow>() {
            public Iterator<ListOfContractHollow> iterator() {
                return new Iterator<ListOfContractHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfContractHollow next() {
                        ListOfContractHollow obj = api.getListOfContractHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ContractsHollow> findContractsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ContractsHollow>() {
            public Iterator<ContractsHollow> iterator() {
                return new Iterator<ContractsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ContractsHollow next() {
                        ContractsHollow obj = api.getContractsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfPackageTagsHollow> findListOfPackageTagsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfPackageTagsHollow>() {
            public Iterator<ListOfPackageTagsHollow> iterator() {
                return new Iterator<ListOfPackageTagsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfPackageTagsHollow next() {
                        ListOfPackageTagsHollow obj = api.getListOfPackageTagsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<DeployablePackagesHollow> findDeployablePackagesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<DeployablePackagesHollow>() {
            public Iterator<DeployablePackagesHollow> iterator() {
                return new Iterator<DeployablePackagesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public DeployablePackagesHollow next() {
                        DeployablePackagesHollow obj = api.getDeployablePackagesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfStringHollow> findListOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfStringHollow>() {
            public Iterator<ListOfStringHollow> iterator() {
                return new Iterator<ListOfStringHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfStringHollow next() {
                        ListOfStringHollow obj = api.getListOfStringHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<LocaleTerritoryCodeHollow> findLocaleTerritoryCodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<LocaleTerritoryCodeHollow>() {
            public Iterator<LocaleTerritoryCodeHollow> iterator() {
                return new Iterator<LocaleTerritoryCodeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public LocaleTerritoryCodeHollow next() {
                        LocaleTerritoryCodeHollow obj = api.getLocaleTerritoryCodeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<LocaleTerritoryCodeListHollow> findLocaleTerritoryCodeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<LocaleTerritoryCodeListHollow>() {
            public Iterator<LocaleTerritoryCodeListHollow> iterator() {
                return new Iterator<LocaleTerritoryCodeListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public LocaleTerritoryCodeListHollow next() {
                        LocaleTerritoryCodeListHollow obj = api.getLocaleTerritoryCodeListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MasterScheduleHollow> findMasterScheduleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MasterScheduleHollow>() {
            public Iterator<MasterScheduleHollow> iterator() {
                return new Iterator<MasterScheduleHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MasterScheduleHollow next() {
                        MasterScheduleHollow obj = api.getMasterScheduleHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MultiValuePassthroughMapHollow> findMultiValuePassthroughMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MultiValuePassthroughMapHollow>() {
            public Iterator<MultiValuePassthroughMapHollow> iterator() {
                return new Iterator<MultiValuePassthroughMapHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MultiValuePassthroughMapHollow next() {
                        MultiValuePassthroughMapHollow obj = api.getMultiValuePassthroughMapHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<OriginServerHollow> findOriginServerMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<OriginServerHollow>() {
            public Iterator<OriginServerHollow> iterator() {
                return new Iterator<OriginServerHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public OriginServerHollow next() {
                        OriginServerHollow obj = api.getOriginServerHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<OverrideScheduleHollow> findOverrideScheduleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<OverrideScheduleHollow>() {
            public Iterator<OverrideScheduleHollow> iterator() {
                return new Iterator<OverrideScheduleHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public OverrideScheduleHollow next() {
                        OverrideScheduleHollow obj = api.getOverrideScheduleHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageDrmInfoHollow> findPackageDrmInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageDrmInfoHollow>() {
            public Iterator<PackageDrmInfoHollow> iterator() {
                return new Iterator<PackageDrmInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageDrmInfoHollow next() {
                        PackageDrmInfoHollow obj = api.getPackageDrmInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageDrmInfoListHollow> findPackageDrmInfoListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageDrmInfoListHollow>() {
            public Iterator<PackageDrmInfoListHollow> iterator() {
                return new Iterator<PackageDrmInfoListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageDrmInfoListHollow next() {
                        PackageDrmInfoListHollow obj = api.getPackageDrmInfoListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageMomentHollow> findPackageMomentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageMomentHollow>() {
            public Iterator<PackageMomentHollow> iterator() {
                return new Iterator<PackageMomentHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageMomentHollow next() {
                        PackageMomentHollow obj = api.getPackageMomentHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageMomentListHollow> findPackageMomentListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageMomentListHollow>() {
            public Iterator<PackageMomentListHollow> iterator() {
                return new Iterator<PackageMomentListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageMomentListHollow next() {
                        PackageMomentListHollow obj = api.getPackageMomentListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PhaseTagHollow> findPhaseTagMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PhaseTagHollow>() {
            public Iterator<PhaseTagHollow> iterator() {
                return new Iterator<PhaseTagHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PhaseTagHollow next() {
                        PhaseTagHollow obj = api.getPhaseTagHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PhaseTagListHollow> findPhaseTagListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PhaseTagListHollow>() {
            public Iterator<PhaseTagListHollow> iterator() {
                return new Iterator<PhaseTagListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PhaseTagListHollow next() {
                        PhaseTagListHollow obj = api.getPhaseTagListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ProtectionTypesHollow> findProtectionTypesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ProtectionTypesHollow>() {
            public Iterator<ProtectionTypesHollow> iterator() {
                return new Iterator<ProtectionTypesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ProtectionTypesHollow next() {
                        ProtectionTypesHollow obj = api.getProtectionTypesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ReleaseDateHollow> findReleaseDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ReleaseDateHollow>() {
            public Iterator<ReleaseDateHollow> iterator() {
                return new Iterator<ReleaseDateHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ReleaseDateHollow next() {
                        ReleaseDateHollow obj = api.getReleaseDateHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfReleaseDatesHollow> findListOfReleaseDatesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfReleaseDatesHollow>() {
            public Iterator<ListOfReleaseDatesHollow> iterator() {
                return new Iterator<ListOfReleaseDatesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfReleaseDatesHollow next() {
                        ListOfReleaseDatesHollow obj = api.getListOfReleaseDatesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsAssetHollow> findRightsAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsAssetHollow>() {
            public Iterator<RightsAssetHollow> iterator() {
                return new Iterator<RightsAssetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsAssetHollow next() {
                        RightsAssetHollow obj = api.getRightsAssetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsContractAssetHollow> findRightsContractAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsContractAssetHollow>() {
            public Iterator<RightsContractAssetHollow> iterator() {
                return new Iterator<RightsContractAssetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsContractAssetHollow next() {
                        RightsContractAssetHollow obj = api.getRightsContractAssetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfRightsContractAssetHollow> findListOfRightsContractAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfRightsContractAssetHollow>() {
            public Iterator<ListOfRightsContractAssetHollow> iterator() {
                return new Iterator<ListOfRightsContractAssetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfRightsContractAssetHollow next() {
                        ListOfRightsContractAssetHollow obj = api.getListOfRightsContractAssetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsContractHollow> findRightsContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsContractHollow>() {
            public Iterator<RightsContractHollow> iterator() {
                return new Iterator<RightsContractHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsContractHollow next() {
                        RightsContractHollow obj = api.getRightsContractHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfRightsContractHollow> findListOfRightsContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfRightsContractHollow>() {
            public Iterator<ListOfRightsContractHollow> iterator() {
                return new Iterator<ListOfRightsContractHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfRightsContractHollow next() {
                        ListOfRightsContractHollow obj = api.getListOfRightsContractHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsHollow> findRightsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsHollow>() {
            public Iterator<RightsHollow> iterator() {
                return new Iterator<RightsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsHollow next() {
                        RightsHollow obj = api.getRightsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseArtworkSourceFileIdHollow> findRolloutPhaseArtworkSourceFileIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseArtworkSourceFileIdHollow>() {
            public Iterator<RolloutPhaseArtworkSourceFileIdHollow> iterator() {
                return new Iterator<RolloutPhaseArtworkSourceFileIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseArtworkSourceFileIdHollow next() {
                        RolloutPhaseArtworkSourceFileIdHollow obj = api.getRolloutPhaseArtworkSourceFileIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseArtworkSourceFileIdListHollow> findRolloutPhaseArtworkSourceFileIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseArtworkSourceFileIdListHollow>() {
            public Iterator<RolloutPhaseArtworkSourceFileIdListHollow> iterator() {
                return new Iterator<RolloutPhaseArtworkSourceFileIdListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseArtworkSourceFileIdListHollow next() {
                        RolloutPhaseArtworkSourceFileIdListHollow obj = api.getRolloutPhaseArtworkSourceFileIdListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseArtworkHollow> findRolloutPhaseArtworkMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseArtworkHollow>() {
            public Iterator<RolloutPhaseArtworkHollow> iterator() {
                return new Iterator<RolloutPhaseArtworkHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseArtworkHollow next() {
                        RolloutPhaseArtworkHollow obj = api.getRolloutPhaseArtworkHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseLocalizedMetadataHollow> findRolloutPhaseLocalizedMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseLocalizedMetadataHollow>() {
            public Iterator<RolloutPhaseLocalizedMetadataHollow> iterator() {
                return new Iterator<RolloutPhaseLocalizedMetadataHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseLocalizedMetadataHollow next() {
                        RolloutPhaseLocalizedMetadataHollow obj = api.getRolloutPhaseLocalizedMetadataHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseElementsHollow> findRolloutPhaseElementsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseElementsHollow>() {
            public Iterator<RolloutPhaseElementsHollow> iterator() {
                return new Iterator<RolloutPhaseElementsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseElementsHollow next() {
                        RolloutPhaseElementsHollow obj = api.getRolloutPhaseElementsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseHollow> findRolloutPhaseMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseHollow>() {
            public Iterator<RolloutPhaseHollow> iterator() {
                return new Iterator<RolloutPhaseHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseHollow next() {
                        RolloutPhaseHollow obj = api.getRolloutPhaseHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutPhaseListHollow> findRolloutPhaseListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutPhaseListHollow>() {
            public Iterator<RolloutPhaseListHollow> iterator() {
                return new Iterator<RolloutPhaseListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutPhaseListHollow next() {
                        RolloutPhaseListHollow obj = api.getRolloutPhaseListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RolloutHollow> findRolloutMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RolloutHollow>() {
            public Iterator<RolloutHollow> iterator() {
                return new Iterator<RolloutHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RolloutHollow next() {
                        RolloutHollow obj = api.getRolloutHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SetOfRightsAssetHollow> findSetOfRightsAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SetOfRightsAssetHollow>() {
            public Iterator<SetOfRightsAssetHollow> iterator() {
                return new Iterator<SetOfRightsAssetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SetOfRightsAssetHollow next() {
                        SetOfRightsAssetHollow obj = api.getSetOfRightsAssetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RightsAssetsHollow> findRightsAssetsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RightsAssetsHollow>() {
            public Iterator<RightsAssetsHollow> iterator() {
                return new Iterator<RightsAssetsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RightsAssetsHollow next() {
                        RightsAssetsHollow obj = api.getRightsAssetsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SetOfStringHollow> findSetOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SetOfStringHollow>() {
            public Iterator<SetOfStringHollow> iterator() {
                return new Iterator<SetOfStringHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SetOfStringHollow next() {
                        SetOfStringHollow obj = api.getSetOfStringHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SingleValuePassthroughMapHollow> findSingleValuePassthroughMapMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SingleValuePassthroughMapHollow>() {
            public Iterator<SingleValuePassthroughMapHollow> iterator() {
                return new Iterator<SingleValuePassthroughMapHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SingleValuePassthroughMapHollow next() {
                        SingleValuePassthroughMapHollow obj = api.getSingleValuePassthroughMapHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PassthroughDataHollow> findPassthroughDataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PassthroughDataHollow>() {
            public Iterator<PassthroughDataHollow> iterator() {
                return new Iterator<PassthroughDataHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PassthroughDataHollow next() {
                        PassthroughDataHollow obj = api.getPassthroughDataHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ArtworkAttributesHollow> findArtworkAttributesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ArtworkAttributesHollow>() {
            public Iterator<ArtworkAttributesHollow> iterator() {
                return new Iterator<ArtworkAttributesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ArtworkAttributesHollow next() {
                        ArtworkAttributesHollow obj = api.getArtworkAttributesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ArtworkLocaleHollow> findArtworkLocaleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ArtworkLocaleHollow>() {
            public Iterator<ArtworkLocaleHollow> iterator() {
                return new Iterator<ArtworkLocaleHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ArtworkLocaleHollow next() {
                        ArtworkLocaleHollow obj = api.getArtworkLocaleHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ArtworkLocaleListHollow> findArtworkLocaleListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ArtworkLocaleListHollow>() {
            public Iterator<ArtworkLocaleListHollow> iterator() {
                return new Iterator<ArtworkLocaleListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ArtworkLocaleListHollow next() {
                        ArtworkLocaleListHollow obj = api.getArtworkLocaleListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CharacterArtworkSourceHollow> findCharacterArtworkSourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharacterArtworkSourceHollow>() {
            public Iterator<CharacterArtworkSourceHollow> iterator() {
                return new Iterator<CharacterArtworkSourceHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharacterArtworkSourceHollow next() {
                        CharacterArtworkSourceHollow obj = api.getCharacterArtworkSourceHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<IndividualSupplementalHollow> findIndividualSupplementalMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<IndividualSupplementalHollow>() {
            public Iterator<IndividualSupplementalHollow> iterator() {
                return new Iterator<IndividualSupplementalHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public IndividualSupplementalHollow next() {
                        IndividualSupplementalHollow obj = api.getIndividualSupplementalHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonArtworkSourceHollow> findPersonArtworkSourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonArtworkSourceHollow>() {
            public Iterator<PersonArtworkSourceHollow> iterator() {
                return new Iterator<PersonArtworkSourceHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonArtworkSourceHollow next() {
                        PersonArtworkSourceHollow obj = api.getPersonArtworkSourceHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StatusHollow> findStatusMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StatusHollow>() {
            public Iterator<StatusHollow> iterator() {
                return new Iterator<StatusHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StatusHollow next() {
                        StatusHollow obj = api.getStatusHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StorageGroupsHollow> findStorageGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StorageGroupsHollow>() {
            public Iterator<StorageGroupsHollow> iterator() {
                return new Iterator<StorageGroupsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StorageGroupsHollow next() {
                        StorageGroupsHollow obj = api.getStorageGroupsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamAssetTypeHollow> findStreamAssetTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamAssetTypeHollow>() {
            public Iterator<StreamAssetTypeHollow> iterator() {
                return new Iterator<StreamAssetTypeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamAssetTypeHollow next() {
                        StreamAssetTypeHollow obj = api.getStreamAssetTypeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamDeploymentInfoHollow> findStreamDeploymentInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamDeploymentInfoHollow>() {
            public Iterator<StreamDeploymentInfoHollow> iterator() {
                return new Iterator<StreamDeploymentInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamDeploymentInfoHollow next() {
                        StreamDeploymentInfoHollow obj = api.getStreamDeploymentInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamDeploymentLabelHollow> findStreamDeploymentLabelMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamDeploymentLabelHollow>() {
            public Iterator<StreamDeploymentLabelHollow> iterator() {
                return new Iterator<StreamDeploymentLabelHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamDeploymentLabelHollow next() {
                        StreamDeploymentLabelHollow obj = api.getStreamDeploymentLabelHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamDeploymentLabelSetHollow> findStreamDeploymentLabelSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamDeploymentLabelSetHollow>() {
            public Iterator<StreamDeploymentLabelSetHollow> iterator() {
                return new Iterator<StreamDeploymentLabelSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamDeploymentLabelSetHollow next() {
                        StreamDeploymentLabelSetHollow obj = api.getStreamDeploymentLabelSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamDeploymentHollow> findStreamDeploymentMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamDeploymentHollow>() {
            public Iterator<StreamDeploymentHollow> iterator() {
                return new Iterator<StreamDeploymentHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamDeploymentHollow next() {
                        StreamDeploymentHollow obj = api.getStreamDeploymentHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamDrmInfoHollow> findStreamDrmInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamDrmInfoHollow>() {
            public Iterator<StreamDrmInfoHollow> iterator() {
                return new Iterator<StreamDrmInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamDrmInfoHollow next() {
                        StreamDrmInfoHollow obj = api.getStreamDrmInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamProfileGroupsHollow> findStreamProfileGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamProfileGroupsHollow>() {
            public Iterator<StreamProfileGroupsHollow> iterator() {
                return new Iterator<StreamProfileGroupsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamProfileGroupsHollow next() {
                        StreamProfileGroupsHollow obj = api.getStreamProfileGroupsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamProfilesHollow> findStreamProfilesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamProfilesHollow>() {
            public Iterator<StreamProfilesHollow> iterator() {
                return new Iterator<StreamProfilesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamProfilesHollow next() {
                        StreamProfilesHollow obj = api.getStreamProfilesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SupplementalsListHollow> findSupplementalsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SupplementalsListHollow>() {
            public Iterator<SupplementalsListHollow> iterator() {
                return new Iterator<SupplementalsListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SupplementalsListHollow next() {
                        SupplementalsListHollow obj = api.getSupplementalsListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<SupplementalsHollow> findSupplementalsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<SupplementalsHollow>() {
            public Iterator<SupplementalsHollow> iterator() {
                return new Iterator<SupplementalsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public SupplementalsHollow next() {
                        SupplementalsHollow obj = api.getSupplementalsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TerritoryCountriesHollow> findTerritoryCountriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TerritoryCountriesHollow>() {
            public Iterator<TerritoryCountriesHollow> iterator() {
                return new Iterator<TerritoryCountriesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TerritoryCountriesHollow next() {
                        TerritoryCountriesHollow obj = api.getTerritoryCountriesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TextStreamInfoHollow> findTextStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TextStreamInfoHollow>() {
            public Iterator<TextStreamInfoHollow> iterator() {
                return new Iterator<TextStreamInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TextStreamInfoHollow next() {
                        TextStreamInfoHollow obj = api.getTextStreamInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TimecodedMomentAnnotationHollow> findTimecodedMomentAnnotationMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TimecodedMomentAnnotationHollow>() {
            public Iterator<TimecodedMomentAnnotationHollow> iterator() {
                return new Iterator<TimecodedMomentAnnotationHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TimecodedMomentAnnotationHollow next() {
                        TimecodedMomentAnnotationHollow obj = api.getTimecodedMomentAnnotationHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TimecodeAnnotationsListHollow> findTimecodeAnnotationsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TimecodeAnnotationsListHollow>() {
            public Iterator<TimecodeAnnotationsListHollow> iterator() {
                return new Iterator<TimecodeAnnotationsListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TimecodeAnnotationsListHollow next() {
                        TimecodeAnnotationsListHollow obj = api.getTimecodeAnnotationsListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TimecodeAnnotationHollow> findTimecodeAnnotationMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TimecodeAnnotationHollow>() {
            public Iterator<TimecodeAnnotationHollow> iterator() {
                return new Iterator<TimecodeAnnotationHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TimecodeAnnotationHollow next() {
                        TimecodeAnnotationHollow obj = api.getTimecodeAnnotationHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TopNAttributeHollow> findTopNAttributeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TopNAttributeHollow>() {
            public Iterator<TopNAttributeHollow> iterator() {
                return new Iterator<TopNAttributeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TopNAttributeHollow next() {
                        TopNAttributeHollow obj = api.getTopNAttributeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TopNAttributesSetHollow> findTopNAttributesSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TopNAttributesSetHollow>() {
            public Iterator<TopNAttributesSetHollow> iterator() {
                return new Iterator<TopNAttributesSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TopNAttributesSetHollow next() {
                        TopNAttributesSetHollow obj = api.getTopNAttributesSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TopNHollow> findTopNMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TopNHollow>() {
            public Iterator<TopNHollow> iterator() {
                return new Iterator<TopNHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TopNHollow next() {
                        TopNHollow obj = api.getTopNHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TranslatedTextValueHollow> findTranslatedTextValueMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TranslatedTextValueHollow>() {
            public Iterator<TranslatedTextValueHollow> iterator() {
                return new Iterator<TranslatedTextValueHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TranslatedTextValueHollow next() {
                        TranslatedTextValueHollow obj = api.getTranslatedTextValueHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MapOfTranslatedTextHollow> findMapOfTranslatedTextMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MapOfTranslatedTextHollow>() {
            public Iterator<MapOfTranslatedTextHollow> iterator() {
                return new Iterator<MapOfTranslatedTextHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MapOfTranslatedTextHollow next() {
                        MapOfTranslatedTextHollow obj = api.getMapOfTranslatedTextHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AltGenresAlternateNamesHollow> findAltGenresAlternateNamesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AltGenresAlternateNamesHollow>() {
            public Iterator<AltGenresAlternateNamesHollow> iterator() {
                return new Iterator<AltGenresAlternateNamesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AltGenresAlternateNamesHollow next() {
                        AltGenresAlternateNamesHollow obj = api.getAltGenresAlternateNamesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AltGenresAlternateNamesListHollow> findAltGenresAlternateNamesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AltGenresAlternateNamesListHollow>() {
            public Iterator<AltGenresAlternateNamesListHollow> iterator() {
                return new Iterator<AltGenresAlternateNamesListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AltGenresAlternateNamesListHollow next() {
                        AltGenresAlternateNamesListHollow obj = api.getAltGenresAlternateNamesListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<LocalizedCharacterHollow> findLocalizedCharacterMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<LocalizedCharacterHollow>() {
            public Iterator<LocalizedCharacterHollow> iterator() {
                return new Iterator<LocalizedCharacterHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public LocalizedCharacterHollow next() {
                        LocalizedCharacterHollow obj = api.getLocalizedCharacterHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<LocalizedMetadataHollow> findLocalizedMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<LocalizedMetadataHollow>() {
            public Iterator<LocalizedMetadataHollow> iterator() {
                return new Iterator<LocalizedMetadataHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public LocalizedMetadataHollow next() {
                        LocalizedMetadataHollow obj = api.getLocalizedMetadataHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StoriesSynopsesHookHollow> findStoriesSynopsesHookMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StoriesSynopsesHookHollow>() {
            public Iterator<StoriesSynopsesHookHollow> iterator() {
                return new Iterator<StoriesSynopsesHookHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StoriesSynopsesHookHollow next() {
                        StoriesSynopsesHookHollow obj = api.getStoriesSynopsesHookHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StoriesSynopsesHookListHollow> findStoriesSynopsesHookListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StoriesSynopsesHookListHollow>() {
            public Iterator<StoriesSynopsesHookListHollow> iterator() {
                return new Iterator<StoriesSynopsesHookListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StoriesSynopsesHookListHollow next() {
                        StoriesSynopsesHookListHollow obj = api.getStoriesSynopsesHookListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TranslatedTextHollow> findTranslatedTextMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TranslatedTextHollow>() {
            public Iterator<TranslatedTextHollow> iterator() {
                return new Iterator<TranslatedTextHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TranslatedTextHollow next() {
                        TranslatedTextHollow obj = api.getTranslatedTextHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AltGenresHollow> findAltGenresMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AltGenresHollow>() {
            public Iterator<AltGenresHollow> iterator() {
                return new Iterator<AltGenresHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AltGenresHollow next() {
                        AltGenresHollow obj = api.getAltGenresHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AssetMetaDatasHollow> findAssetMetaDatasMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AssetMetaDatasHollow>() {
            public Iterator<AssetMetaDatasHollow> iterator() {
                return new Iterator<AssetMetaDatasHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AssetMetaDatasHollow next() {
                        AssetMetaDatasHollow obj = api.getAssetMetaDatasHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<AwardsHollow> findAwardsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<AwardsHollow>() {
            public Iterator<AwardsHollow> iterator() {
                return new Iterator<AwardsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public AwardsHollow next() {
                        AwardsHollow obj = api.getAwardsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CategoriesHollow> findCategoriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CategoriesHollow>() {
            public Iterator<CategoriesHollow> iterator() {
                return new Iterator<CategoriesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CategoriesHollow next() {
                        CategoriesHollow obj = api.getCategoriesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CategoryGroupsHollow> findCategoryGroupsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CategoryGroupsHollow>() {
            public Iterator<CategoryGroupsHollow> iterator() {
                return new Iterator<CategoryGroupsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CategoryGroupsHollow next() {
                        CategoryGroupsHollow obj = api.getCategoryGroupsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CertificationsHollow> findCertificationsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CertificationsHollow>() {
            public Iterator<CertificationsHollow> iterator() {
                return new Iterator<CertificationsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CertificationsHollow next() {
                        CertificationsHollow obj = api.getCertificationsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<CharactersHollow> findCharactersMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<CharactersHollow>() {
            public Iterator<CharactersHollow> iterator() {
                return new Iterator<CharactersHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public CharactersHollow next() {
                        CharactersHollow obj = api.getCharactersHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedCertSystemRatingHollow> findConsolidatedCertSystemRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedCertSystemRatingHollow>() {
            public Iterator<ConsolidatedCertSystemRatingHollow> iterator() {
                return new Iterator<ConsolidatedCertSystemRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedCertSystemRatingHollow next() {
                        ConsolidatedCertSystemRatingHollow obj = api.getConsolidatedCertSystemRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedCertSystemRatingListHollow> findConsolidatedCertSystemRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedCertSystemRatingListHollow>() {
            public Iterator<ConsolidatedCertSystemRatingListHollow> iterator() {
                return new Iterator<ConsolidatedCertSystemRatingListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedCertSystemRatingListHollow next() {
                        ConsolidatedCertSystemRatingListHollow obj = api.getConsolidatedCertSystemRatingListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedCertificationSystemsHollow> findConsolidatedCertificationSystemsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedCertificationSystemsHollow>() {
            public Iterator<ConsolidatedCertificationSystemsHollow> iterator() {
                return new Iterator<ConsolidatedCertificationSystemsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedCertificationSystemsHollow next() {
                        ConsolidatedCertificationSystemsHollow obj = api.getConsolidatedCertificationSystemsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<EpisodesHollow> findEpisodesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<EpisodesHollow>() {
            public Iterator<EpisodesHollow> iterator() {
                return new Iterator<EpisodesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public EpisodesHollow next() {
                        EpisodesHollow obj = api.getEpisodesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<FestivalsHollow> findFestivalsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<FestivalsHollow>() {
            public Iterator<FestivalsHollow> iterator() {
                return new Iterator<FestivalsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public FestivalsHollow next() {
                        FestivalsHollow obj = api.getFestivalsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<LanguagesHollow> findLanguagesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<LanguagesHollow>() {
            public Iterator<LanguagesHollow> iterator() {
                return new Iterator<LanguagesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public LanguagesHollow next() {
                        LanguagesHollow obj = api.getLanguagesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MovieRatingsHollow> findMovieRatingsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MovieRatingsHollow>() {
            public Iterator<MovieRatingsHollow> iterator() {
                return new Iterator<MovieRatingsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MovieRatingsHollow next() {
                        MovieRatingsHollow obj = api.getMovieRatingsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<MoviesHollow> findMoviesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<MoviesHollow>() {
            public Iterator<MoviesHollow> iterator() {
                return new Iterator<MoviesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public MoviesHollow next() {
                        MoviesHollow obj = api.getMoviesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonAliasesHollow> findPersonAliasesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonAliasesHollow>() {
            public Iterator<PersonAliasesHollow> iterator() {
                return new Iterator<PersonAliasesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonAliasesHollow next() {
                        PersonAliasesHollow obj = api.getPersonAliasesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonCharacterResourceHollow> findPersonCharacterResourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonCharacterResourceHollow>() {
            public Iterator<PersonCharacterResourceHollow> iterator() {
                return new Iterator<PersonCharacterResourceHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonCharacterResourceHollow next() {
                        PersonCharacterResourceHollow obj = api.getPersonCharacterResourceHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonsHollow> findPersonsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonsHollow>() {
            public Iterator<PersonsHollow> iterator() {
                return new Iterator<PersonsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonsHollow next() {
                        PersonsHollow obj = api.getPersonsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<RatingsHollow> findRatingsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<RatingsHollow>() {
            public Iterator<RatingsHollow> iterator() {
                return new Iterator<RatingsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public RatingsHollow next() {
                        RatingsHollow obj = api.getRatingsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ShowMemberTypesHollow> findShowMemberTypesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ShowMemberTypesHollow>() {
            public Iterator<ShowMemberTypesHollow> iterator() {
                return new Iterator<ShowMemberTypesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ShowMemberTypesHollow next() {
                        ShowMemberTypesHollow obj = api.getShowMemberTypesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StoriesSynopsesHollow> findStoriesSynopsesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StoriesSynopsesHollow>() {
            public Iterator<StoriesSynopsesHollow> iterator() {
                return new Iterator<StoriesSynopsesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StoriesSynopsesHollow next() {
                        StoriesSynopsesHollow obj = api.getStoriesSynopsesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<TurboCollectionsHollow> findTurboCollectionsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<TurboCollectionsHollow>() {
            public Iterator<TurboCollectionsHollow> iterator() {
                return new Iterator<TurboCollectionsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public TurboCollectionsHollow next() {
                        TurboCollectionsHollow obj = api.getTurboCollectionsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VMSAwardHollow> findVMSAwardMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VMSAwardHollow>() {
            public Iterator<VMSAwardHollow> iterator() {
                return new Iterator<VMSAwardHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VMSAwardHollow next() {
                        VMSAwardHollow obj = api.getVMSAwardHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoArtworkSourceHollow> findVideoArtworkSourceMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoArtworkSourceHollow>() {
            public Iterator<VideoArtworkSourceHollow> iterator() {
                return new Iterator<VideoArtworkSourceHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoArtworkSourceHollow next() {
                        VideoArtworkSourceHollow obj = api.getVideoArtworkSourceHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoAwardMappingHollow> findVideoAwardMappingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoAwardMappingHollow>() {
            public Iterator<VideoAwardMappingHollow> iterator() {
                return new Iterator<VideoAwardMappingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoAwardMappingHollow next() {
                        VideoAwardMappingHollow obj = api.getVideoAwardMappingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoAwardListHollow> findVideoAwardListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoAwardListHollow>() {
            public Iterator<VideoAwardListHollow> iterator() {
                return new Iterator<VideoAwardListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoAwardListHollow next() {
                        VideoAwardListHollow obj = api.getVideoAwardListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoAwardHollow> findVideoAwardMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoAwardHollow>() {
            public Iterator<VideoAwardHollow> iterator() {
                return new Iterator<VideoAwardHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoAwardHollow next() {
                        VideoAwardHollow obj = api.getVideoAwardHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoDateWindowHollow> findVideoDateWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoDateWindowHollow>() {
            public Iterator<VideoDateWindowHollow> iterator() {
                return new Iterator<VideoDateWindowHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoDateWindowHollow next() {
                        VideoDateWindowHollow obj = api.getVideoDateWindowHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoDateWindowListHollow> findVideoDateWindowListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoDateWindowListHollow>() {
            public Iterator<VideoDateWindowListHollow> iterator() {
                return new Iterator<VideoDateWindowListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoDateWindowListHollow next() {
                        VideoDateWindowListHollow obj = api.getVideoDateWindowListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoDateHollow> findVideoDateMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoDateHollow>() {
            public Iterator<VideoDateHollow> iterator() {
                return new Iterator<VideoDateHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoDateHollow next() {
                        VideoDateHollow obj = api.getVideoDateHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralAliasHollow> findVideoGeneralAliasMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralAliasHollow>() {
            public Iterator<VideoGeneralAliasHollow> iterator() {
                return new Iterator<VideoGeneralAliasHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralAliasHollow next() {
                        VideoGeneralAliasHollow obj = api.getVideoGeneralAliasHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralAliasListHollow> findVideoGeneralAliasListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralAliasListHollow>() {
            public Iterator<VideoGeneralAliasListHollow> iterator() {
                return new Iterator<VideoGeneralAliasListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralAliasListHollow next() {
                        VideoGeneralAliasListHollow obj = api.getVideoGeneralAliasListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralEpisodeTypeHollow> findVideoGeneralEpisodeTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralEpisodeTypeHollow>() {
            public Iterator<VideoGeneralEpisodeTypeHollow> iterator() {
                return new Iterator<VideoGeneralEpisodeTypeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralEpisodeTypeHollow next() {
                        VideoGeneralEpisodeTypeHollow obj = api.getVideoGeneralEpisodeTypeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralEpisodeTypeListHollow> findVideoGeneralEpisodeTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralEpisodeTypeListHollow>() {
            public Iterator<VideoGeneralEpisodeTypeListHollow> iterator() {
                return new Iterator<VideoGeneralEpisodeTypeListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralEpisodeTypeListHollow next() {
                        VideoGeneralEpisodeTypeListHollow obj = api.getVideoGeneralEpisodeTypeListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralTitleTypeHollow> findVideoGeneralTitleTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralTitleTypeHollow>() {
            public Iterator<VideoGeneralTitleTypeHollow> iterator() {
                return new Iterator<VideoGeneralTitleTypeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralTitleTypeHollow next() {
                        VideoGeneralTitleTypeHollow obj = api.getVideoGeneralTitleTypeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralTitleTypeListHollow> findVideoGeneralTitleTypeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralTitleTypeListHollow>() {
            public Iterator<VideoGeneralTitleTypeListHollow> iterator() {
                return new Iterator<VideoGeneralTitleTypeListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralTitleTypeListHollow next() {
                        VideoGeneralTitleTypeListHollow obj = api.getVideoGeneralTitleTypeListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoGeneralHollow> findVideoGeneralMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoGeneralHollow>() {
            public Iterator<VideoGeneralHollow> iterator() {
                return new Iterator<VideoGeneralHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoGeneralHollow next() {
                        VideoGeneralHollow obj = api.getVideoGeneralHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoIdHollow> findVideoIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoIdHollow>() {
            public Iterator<VideoIdHollow> iterator() {
                return new Iterator<VideoIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoIdHollow next() {
                        VideoIdHollow obj = api.getVideoIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ListOfVideoIdsHollow> findListOfVideoIdsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ListOfVideoIdsHollow>() {
            public Iterator<ListOfVideoIdsHollow> iterator() {
                return new Iterator<ListOfVideoIdsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ListOfVideoIdsHollow next() {
                        ListOfVideoIdsHollow obj = api.getListOfVideoIdsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PersonBioHollow> findPersonBioMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PersonBioHollow>() {
            public Iterator<PersonBioHollow> iterator() {
                return new Iterator<PersonBioHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PersonBioHollow next() {
                        PersonBioHollow obj = api.getPersonBioHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingAdvisoryIdHollow> findVideoRatingAdvisoryIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingAdvisoryIdHollow>() {
            public Iterator<VideoRatingAdvisoryIdHollow> iterator() {
                return new Iterator<VideoRatingAdvisoryIdHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingAdvisoryIdHollow next() {
                        VideoRatingAdvisoryIdHollow obj = api.getVideoRatingAdvisoryIdHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingAdvisoryIdListHollow> findVideoRatingAdvisoryIdListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingAdvisoryIdListHollow>() {
            public Iterator<VideoRatingAdvisoryIdListHollow> iterator() {
                return new Iterator<VideoRatingAdvisoryIdListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingAdvisoryIdListHollow next() {
                        VideoRatingAdvisoryIdListHollow obj = api.getVideoRatingAdvisoryIdListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingAdvisoriesHollow> findVideoRatingAdvisoriesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingAdvisoriesHollow>() {
            public Iterator<VideoRatingAdvisoriesHollow> iterator() {
                return new Iterator<VideoRatingAdvisoriesHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingAdvisoriesHollow next() {
                        VideoRatingAdvisoriesHollow obj = api.getVideoRatingAdvisoriesHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedVideoCountryRatingHollow> findConsolidatedVideoCountryRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedVideoCountryRatingHollow>() {
            public Iterator<ConsolidatedVideoCountryRatingHollow> iterator() {
                return new Iterator<ConsolidatedVideoCountryRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedVideoCountryRatingHollow next() {
                        ConsolidatedVideoCountryRatingHollow obj = api.getConsolidatedVideoCountryRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedVideoCountryRatingListHollow> findConsolidatedVideoCountryRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedVideoCountryRatingListHollow>() {
            public Iterator<ConsolidatedVideoCountryRatingListHollow> iterator() {
                return new Iterator<ConsolidatedVideoCountryRatingListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedVideoCountryRatingListHollow next() {
                        ConsolidatedVideoCountryRatingListHollow obj = api.getConsolidatedVideoCountryRatingListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedVideoRatingHollow> findConsolidatedVideoRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedVideoRatingHollow>() {
            public Iterator<ConsolidatedVideoRatingHollow> iterator() {
                return new Iterator<ConsolidatedVideoRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedVideoRatingHollow next() {
                        ConsolidatedVideoRatingHollow obj = api.getConsolidatedVideoRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedVideoRatingListHollow> findConsolidatedVideoRatingListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedVideoRatingListHollow>() {
            public Iterator<ConsolidatedVideoRatingListHollow> iterator() {
                return new Iterator<ConsolidatedVideoRatingListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedVideoRatingListHollow next() {
                        ConsolidatedVideoRatingListHollow obj = api.getConsolidatedVideoRatingListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<ConsolidatedVideoRatingsHollow> findConsolidatedVideoRatingsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<ConsolidatedVideoRatingsHollow>() {
            public Iterator<ConsolidatedVideoRatingsHollow> iterator() {
                return new Iterator<ConsolidatedVideoRatingsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public ConsolidatedVideoRatingsHollow next() {
                        ConsolidatedVideoRatingsHollow obj = api.getConsolidatedVideoRatingsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingRatingReasonIdsHollow> findVideoRatingRatingReasonIdsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingRatingReasonIdsHollow>() {
            public Iterator<VideoRatingRatingReasonIdsHollow> iterator() {
                return new Iterator<VideoRatingRatingReasonIdsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingRatingReasonIdsHollow next() {
                        VideoRatingRatingReasonIdsHollow obj = api.getVideoRatingRatingReasonIdsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingRatingReasonArrayOfIdsHollow> findVideoRatingRatingReasonArrayOfIdsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingRatingReasonArrayOfIdsHollow>() {
            public Iterator<VideoRatingRatingReasonArrayOfIdsHollow> iterator() {
                return new Iterator<VideoRatingRatingReasonArrayOfIdsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingRatingReasonArrayOfIdsHollow next() {
                        VideoRatingRatingReasonArrayOfIdsHollow obj = api.getVideoRatingRatingReasonArrayOfIdsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingRatingReasonHollow> findVideoRatingRatingReasonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingRatingReasonHollow>() {
            public Iterator<VideoRatingRatingReasonHollow> iterator() {
                return new Iterator<VideoRatingRatingReasonHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingRatingReasonHollow next() {
                        VideoRatingRatingReasonHollow obj = api.getVideoRatingRatingReasonHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingRatingHollow> findVideoRatingRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingRatingHollow>() {
            public Iterator<VideoRatingRatingHollow> iterator() {
                return new Iterator<VideoRatingRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingRatingHollow next() {
                        VideoRatingRatingHollow obj = api.getVideoRatingRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingArrayOfRatingHollow> findVideoRatingArrayOfRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingArrayOfRatingHollow>() {
            public Iterator<VideoRatingArrayOfRatingHollow> iterator() {
                return new Iterator<VideoRatingArrayOfRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingArrayOfRatingHollow next() {
                        VideoRatingArrayOfRatingHollow obj = api.getVideoRatingArrayOfRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoRatingHollow> findVideoRatingMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoRatingHollow>() {
            public Iterator<VideoRatingHollow> iterator() {
                return new Iterator<VideoRatingHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoRatingHollow next() {
                        VideoRatingHollow obj = api.getVideoRatingHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoStreamCropParamsHollow> findVideoStreamCropParamsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoStreamCropParamsHollow>() {
            public Iterator<VideoStreamCropParamsHollow> iterator() {
                return new Iterator<VideoStreamCropParamsHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoStreamCropParamsHollow next() {
                        VideoStreamCropParamsHollow obj = api.getVideoStreamCropParamsHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoStreamInfoHollow> findVideoStreamInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoStreamInfoHollow>() {
            public Iterator<VideoStreamInfoHollow> iterator() {
                return new Iterator<VideoStreamInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoStreamInfoHollow next() {
                        VideoStreamInfoHollow obj = api.getVideoStreamInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<StreamNonImageInfoHollow> findStreamNonImageInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<StreamNonImageInfoHollow>() {
            public Iterator<StreamNonImageInfoHollow> iterator() {
                return new Iterator<StreamNonImageInfoHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public StreamNonImageInfoHollow next() {
                        StreamNonImageInfoHollow obj = api.getStreamNonImageInfoHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageStreamHollow> findPackageStreamMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageStreamHollow>() {
            public Iterator<PackageStreamHollow> iterator() {
                return new Iterator<PackageStreamHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageStreamHollow next() {
                        PackageStreamHollow obj = api.getPackageStreamHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageStreamSetHollow> findPackageStreamSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageStreamSetHollow>() {
            public Iterator<PackageStreamSetHollow> iterator() {
                return new Iterator<PackageStreamSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageStreamSetHollow next() {
                        PackageStreamSetHollow obj = api.getPackageStreamSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<PackageHollow> findPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<PackageHollow>() {
            public Iterator<PackageHollow> iterator() {
                return new Iterator<PackageHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public PackageHollow next() {
                        PackageHollow obj = api.getPackageHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoTypeMediaHollow> findVideoTypeMediaMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoTypeMediaHollow>() {
            public Iterator<VideoTypeMediaHollow> iterator() {
                return new Iterator<VideoTypeMediaHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoTypeMediaHollow next() {
                        VideoTypeMediaHollow obj = api.getVideoTypeMediaHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoTypeMediaListHollow> findVideoTypeMediaListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoTypeMediaListHollow>() {
            public Iterator<VideoTypeMediaListHollow> iterator() {
                return new Iterator<VideoTypeMediaListHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoTypeMediaListHollow next() {
                        VideoTypeMediaListHollow obj = api.getVideoTypeMediaListHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoTypeDescriptorHollow> findVideoTypeDescriptorMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoTypeDescriptorHollow>() {
            public Iterator<VideoTypeDescriptorHollow> iterator() {
                return new Iterator<VideoTypeDescriptorHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoTypeDescriptorHollow next() {
                        VideoTypeDescriptorHollow obj = api.getVideoTypeDescriptorHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoTypeDescriptorSetHollow> findVideoTypeDescriptorSetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoTypeDescriptorSetHollow>() {
            public Iterator<VideoTypeDescriptorSetHollow> iterator() {
                return new Iterator<VideoTypeDescriptorSetHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoTypeDescriptorSetHollow next() {
                        VideoTypeDescriptorSetHollow obj = api.getVideoTypeDescriptorSetHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Iterable<VideoTypeHollow> findVideoTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null)
            return Collections.emptySet();

        final HollowOrdinalIterator iter = matches.iterator();

        return new Iterable<VideoTypeHollow>() {
            public Iterator<VideoTypeHollow> iterator() {
                return new Iterator<VideoTypeHollow>() {

                    private int next = iter.next();

                    public boolean hasNext() {
                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
                    }

                    public VideoTypeHollow next() {
                        VideoTypeHollow obj = api.getVideoTypeHollow(next);
                        next = iter.next();
                        return obj;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        reindex(stateEngine, api);
    }

    @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        reindex(stateEngine, api);
    }

    private void reindex(HollowReadStateEngine stateEngine, HollowAPI api) {
        this.idx = new HollowHashIndex(stateEngine, queryType, selectFieldPath, matchFieldPaths);
        this.api = (VMSHollowInputAPI) api;
    }

    @Override public void refreshStarted(long currentVersion, long requestedVersion) { }
    @Override public void blobLoaded(HollowConsumer.Blob transition) { }
    @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) { }
    @Override public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) { }

}