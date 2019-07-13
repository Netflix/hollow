package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<MapKey, K> uki = HashIndex.from(consumer, MapKey.class)
 *         .usingBean(k);
 *     Stream<MapKey> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code MapKey} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class LocalizedMetaDataAPIHashIndex extends AbstractHollowHashIndex<LocalizedMetaDataAPI> {

    public LocalizedMetaDataAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public LocalizedMetaDataAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
    }

    public Iterable<MapKey> findMapKeyMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapKey>(matches.iterator()) {
            public MapKey getData(int ordinal) {
                return api.getMapKey(ordinal);
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

    public Iterable<TranslatedTextValue> findTranslatedTextValueMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<TranslatedTextValue>(matches.iterator()) {
            public TranslatedTextValue getData(int ordinal) {
                return api.getTranslatedTextValue(ordinal);
            }
        };
    }

    public Iterable<MapOfTranslatedText> findMapOfTranslatedTextMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfTranslatedText>(matches.iterator()) {
            public MapOfTranslatedText getData(int ordinal) {
                return api.getMapOfTranslatedText(ordinal);
            }
        };
    }

    public Iterable<LocalizedMetadata> findLocalizedMetadataMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<LocalizedMetadata>(matches.iterator()) {
            public LocalizedMetadata getData(int ordinal) {
                return api.getLocalizedMetadata(ordinal);
            }
        };
    }

}