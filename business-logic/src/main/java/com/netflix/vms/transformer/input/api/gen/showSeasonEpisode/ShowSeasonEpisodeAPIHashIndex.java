package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<Episode, K> uki = HashIndex.from(consumer, Episode.class)
 *         .usingBean(k);
 *     Stream<Episode> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code Episode} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class ShowSeasonEpisodeAPIHashIndex extends AbstractHollowHashIndex<ShowSeasonEpisodeAPI> {

    public ShowSeasonEpisodeAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public ShowSeasonEpisodeAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<Episode> findEpisodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Episode>(matches.iterator()) {
            public Episode getData(int ordinal) {
                return api.getEpisode(ordinal);
            }
        };
    }

    public Iterable<EpisodeList> findEpisodeListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<EpisodeList>(matches.iterator()) {
            public EpisodeList getData(int ordinal) {
                return api.getEpisodeList(ordinal);
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

    public Iterable<HString> findStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<HString>(matches.iterator()) {
            public HString getData(int ordinal) {
                return api.getHString(ordinal);
            }
        };
    }

    public Iterable<Season> findSeasonMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Season>(matches.iterator()) {
            public Season getData(int ordinal) {
                return api.getSeason(ordinal);
            }
        };
    }

    public Iterable<SeasonList> findSeasonListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<SeasonList>(matches.iterator()) {
            public SeasonList getData(int ordinal) {
                return api.getSeasonList(ordinal);
            }
        };
    }

    public Iterable<ShowSeasonEpisode> findShowSeasonEpisodeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ShowSeasonEpisode>(matches.iterator()) {
            public ShowSeasonEpisode getData(int ordinal) {
                return api.getShowSeasonEpisode(ordinal);
            }
        };
    }

}