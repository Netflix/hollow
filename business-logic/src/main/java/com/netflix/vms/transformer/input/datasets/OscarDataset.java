package com.netflix.vms.transformer.input.datasets;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.input.api.gen.oscar.Movie;
import com.netflix.vms.transformer.input.api.gen.oscar.MovieExtension;
import com.netflix.vms.transformer.input.api.gen.oscar.OscarAPI;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OscarDataset extends UpstreamDataset {
    private final OscarAPI api;
    private final HollowPrimaryKeyIndex movieIdx;
    private final HollowHashIndex movieExtensionIdx;

    private static final HollowOrdinalIterator EMPTY_ORDINAL_ITERATOR = () -> HollowOrdinalIterator.NO_MORE_ORDINALS;

    public OscarDataset(InputState input) {
        super(input);
        HollowReadStateEngine readStateEngine = input.getStateEngine();
        this.api = new OscarAPI(readStateEngine);
        this.movieIdx = new HollowPrimaryKeyIndex(readStateEngine, new PrimaryKey("Movie", "movieId"));
        this.movieExtensionIdx = new HollowHashIndex(readStateEngine, "MovieExtension", "", "movieId", "attributeName.value");
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


    public Stream<MovieExtension> getMovieExtensions(long movieId, String attributeName) {
        HollowHashIndexResult matches = this.movieExtensionIdx.findMatches(movieId,attributeName);

        Iterator<MovieExtension> iterator = new Iterator<MovieExtension>() {
            private final HollowOrdinalIterator iter = matches == null ? EMPTY_ORDINAL_ITERATOR : matches.iterator();
            private int nextOrdinal = iter.next();
            private MovieExtension nextMovieExtension = findNextMovieExtension();

            public boolean hasNext() {
                return nextMovieExtension != null;
            }

            @Override
            public MovieExtension next() {
                if(nextMovieExtension == null)
                    throw new NoSuchElementException();
                MovieExtension s = nextMovieExtension;
                nextMovieExtension = findNextMovieExtension();
                return s;
            }

            private MovieExtension findNextMovieExtension() {
                while(nextOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    MovieExtension extension = api.getMovieExtension(nextOrdinal);
                    nextOrdinal = iter.next();
                    return extension;
                }
                return null;
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,0),false);

    }
}
