package com.netflix.vms.transformer.input.datasets;

import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.input.api.gen.oscar.Movie;
import com.netflix.vms.transformer.input.api.gen.oscar.MovieExtension;
import com.netflix.vms.transformer.input.api.gen.oscar.MovieTitleAka;
import com.netflix.vms.transformer.input.api.gen.oscar.OscarAPI;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OscarDataset extends UpstreamDataset {
    //TODO: enable me once we can turn on the new data set including follow vip functionality
    //private final OscarDataset oscarDataset;
    public static final String EPISODE_TYPE = "EPISODE_TYPE";
    public static final String COUNTRY = "COUNTRY";
    public static final String TEST_TITLE_TYPE = "TEST_TITLE_TYPE";

    public enum MovieExtensionAttributeName {
        EPISODE_TYPE,
        TEST_TITLE_TYPE,
        REGULATORY_ADVISORY
    }

    public enum MovieExtensionOverrideEntityType {
        COUNTRY
    }

    private final OscarAPI api;
    private final HollowPrimaryKeyIndex movieIdx;
    private final HollowHashIndex movieExtensionIdx;
    private final HollowHashIndex movieTitleAkaIdx;

    private static final HollowOrdinalIterator EMPTY_ORDINAL_ITERATOR = () -> HollowOrdinalIterator.NO_MORE_ORDINALS;

    public OscarDataset(InputState input) {
        super(input);
        HollowReadStateEngine readStateEngine = input.getStateEngine();
        this.api = new OscarAPI(readStateEngine);
        this.movieIdx = new HollowPrimaryKeyIndex(readStateEngine, new PrimaryKey("Movie", "movieId"));
        this.movieExtensionIdx = new HollowHashIndex(readStateEngine, "MovieExtension", "", "movieId", "attributeName.value");
        this.movieTitleAkaIdx = new HollowHashIndex(readStateEngine, "MovieTitleAka", "", "movieId");
    }

    @Override
    public OscarAPI getAPI() {
        return api;
    }

    public HollowPrimaryKeyIndex getMoviePrimaryKeyIdx() {
        return movieIdx;
    }

    public Movie getMovie(long movieId) {
        int ordinal = movieIdx.getMatchingOrdinal(movieId);
        Movie movie = ordinal == -1 ? null : api.getMovie(ordinal);

        // filter by active and visible
        if (movie==null || !movie.getActive() || !movie.getVisible()) {
            return null;
        }
        return movie;
    }

    public int maxMovieOrdinal() {
        return movieIdx.getTypeState().maxOrdinal();
    }

    public boolean movieExists(long videoId) {
        int ordinal = getMoviePrimaryKeyIdx().getMatchingOrdinal(videoId);
        return (ordinal != HollowConstants.ORDINAL_NONE);
    }

    public <R> Optional<R> mapWithMovieIfExists(long videoId, Function<Movie,R> f) {
        if (!movieExists(videoId))
            return Optional.empty();
        return Optional.ofNullable(f.apply(getMovie(videoId)));
    }

    public void execWithMovieIfExists(long videoId, Consumer<Movie> f) {
        if (!movieExists(videoId))
            return;
        f.accept(getMovie(videoId));
    }

    public Stream<MovieExtension> getMovieExtensions(long movieId, MovieExtensionAttributeName attributeName) {
        HollowHashIndexResult matches = this.movieExtensionIdx.findMatches(movieId,attributeName.name());
        return streamMatches(matches,(ordinal)->api.getMovieExtension(ordinal));
    }

    public Stream<MovieTitleAka> getMovieTitleAkas(long movieId) {
        HollowHashIndexResult matches = this.movieTitleAkaIdx.findMatches(movieId);
        return streamMatches(matches,(ordinal)->api.getMovieTitleAka(ordinal));
    }

    private <T> Stream<T> streamMatches(HollowHashIndexResult matches, Function<Integer,T> getObjF) {
        Iterator<T> iterator = new Iterator<T>() {
            private final HollowOrdinalIterator iter = matches == null ? EMPTY_ORDINAL_ITERATOR : matches.iterator();
            private int nextOrdinal = iter.next();
            private T nextObject = findNextObject();

            public boolean hasNext() {
                return nextObject != null;
            }

            @Override
            public T next() {
                if(nextObject == null)
                    throw new NoSuchElementException();
                T obj = nextObject;
                nextObject = findNextObject();
                return obj;
            }

            private T findNextObject() {
                while(nextOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    T obj = getObjF.apply(nextOrdinal);
                    nextOrdinal = iter.next();
                    return obj;
                }
                return null;
            }
        };
        return StreamSupport.stream(Spliterators.spliterator(iterator,matches.numResults(),0),false);
    }

    public Set<Strings> getSetStringsFromMovieExtensions(long videoId, MovieExtensionAttributeName movieExtensionName) {
        return getMovieExtensions(videoId, movieExtensionName)
                .filter(ttt->ttt.getAttributeValue()!=null)
                .map(ttt->new Strings(ttt.getAttributeValue()))
                .collect(Collectors.toSet());
    }

}
