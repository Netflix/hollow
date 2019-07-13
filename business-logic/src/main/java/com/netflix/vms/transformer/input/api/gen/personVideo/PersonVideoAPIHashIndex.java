package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<PersonVideoAliasId, K> uki = HashIndex.from(consumer, PersonVideoAliasId.class)
 *         .usingBean(k);
 *     Stream<PersonVideoAliasId> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code PersonVideoAliasId} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class PersonVideoAPIHashIndex extends AbstractHollowHashIndex<PersonVideoAPI> {

    public PersonVideoAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public PersonVideoAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<PersonVideoAliasId> findPersonVideoAliasIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoAliasId>(matches.iterator()) {
            public PersonVideoAliasId getData(int ordinal) {
                return api.getPersonVideoAliasId(ordinal);
            }
        };
    }

    public Iterable<PersonVideoAliasIdsList> findPersonVideoAliasIdsListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoAliasIdsList>(matches.iterator()) {
            public PersonVideoAliasIdsList getData(int ordinal) {
                return api.getPersonVideoAliasIdsList(ordinal);
            }
        };
    }

    public Iterable<PersonVideoRole> findPersonVideoRoleMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoRole>(matches.iterator()) {
            public PersonVideoRole getData(int ordinal) {
                return api.getPersonVideoRole(ordinal);
            }
        };
    }

    public Iterable<PersonVideoRolesList> findPersonVideoRolesListMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideoRolesList>(matches.iterator()) {
            public PersonVideoRolesList getData(int ordinal) {
                return api.getPersonVideoRolesList(ordinal);
            }
        };
    }

    public Iterable<PersonVideo> findPersonVideoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<PersonVideo>(matches.iterator()) {
            public PersonVideo getData(int ordinal) {
                return api.getPersonVideo(ordinal);
            }
        };
    }

}