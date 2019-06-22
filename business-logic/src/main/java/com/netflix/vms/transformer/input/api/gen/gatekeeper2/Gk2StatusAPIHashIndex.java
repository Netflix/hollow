package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import java.util.Collections;


/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.HashIndex} which can be built as follows:
 * <pre>{@code
 *     HashIndex<Date, K> uki = HashIndex.from(consumer, Date.class)
 *         .usingBean(k);
 *     Stream<Date> results = uki.findMatches(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the query to find the matching {@code Date} objects.
 */
@Deprecated
@SuppressWarnings("all")
public class Gk2StatusAPIHashIndex extends AbstractHollowHashIndex<Gk2StatusAPI> {

    public Gk2StatusAPIHashIndex(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, true, queryType, selectFieldPath, matchFieldPaths);
    }

    public Gk2StatusAPIHashIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String queryType, String selectFieldPath, String... matchFieldPaths) {
        super(consumer, isListenToDataRefresh, queryType, selectFieldPath, matchFieldPaths);
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

    public Iterable<MapKey> findMapKeyMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapKey>(matches.iterator()) {
            public MapKey getData(int ordinal) {
                return api.getMapKey(ordinal);
            }
        };
    }

    public Iterable<MapOfFlagsFirstDisplayDates> findMapOfFlagsFirstDisplayDatesMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<MapOfFlagsFirstDisplayDates>(matches.iterator()) {
            public MapOfFlagsFirstDisplayDates getData(int ordinal) {
                return api.getMapOfFlagsFirstDisplayDates(ordinal);
            }
        };
    }

    public Iterable<ParentNodeId> findParentNodeIdMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ParentNodeId>(matches.iterator()) {
            public ParentNodeId getData(int ordinal) {
                return api.getParentNodeId(ordinal);
            }
        };
    }

    public Iterable<RightsContractPackage> findRightsContractPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsContractPackage>(matches.iterator()) {
            public RightsContractPackage getData(int ordinal) {
                return api.getRightsContractPackage(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsContractPackage> findListOfRightsContractPackageMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsContractPackage>(matches.iterator()) {
            public ListOfRightsContractPackage getData(int ordinal) {
                return api.getListOfRightsContractPackage(ordinal);
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

    public Iterable<ListOfString> findListOfStringMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfString>(matches.iterator()) {
            public ListOfString getData(int ordinal) {
                return api.getListOfString(ordinal);
            }
        };
    }

    public Iterable<RightsContractAsset> findRightsContractAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsContractAsset>(matches.iterator()) {
            public RightsContractAsset getData(int ordinal) {
                return api.getRightsContractAsset(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsContractAsset> findListOfRightsContractAssetMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsContractAsset>(matches.iterator()) {
            public ListOfRightsContractAsset getData(int ordinal) {
                return api.getListOfRightsContractAsset(ordinal);
            }
        };
    }

    public Iterable<RightsWindowContract> findRightsWindowContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsWindowContract>(matches.iterator()) {
            public RightsWindowContract getData(int ordinal) {
                return api.getRightsWindowContract(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsWindowContract> findListOfRightsWindowContractMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsWindowContract>(matches.iterator()) {
            public ListOfRightsWindowContract getData(int ordinal) {
                return api.getListOfRightsWindowContract(ordinal);
            }
        };
    }

    public Iterable<RightsWindow> findRightsWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<RightsWindow>(matches.iterator()) {
            public RightsWindow getData(int ordinal) {
                return api.getRightsWindow(ordinal);
            }
        };
    }

    public Iterable<ListOfRightsWindow> findListOfRightsWindowMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<ListOfRightsWindow>(matches.iterator()) {
            public ListOfRightsWindow getData(int ordinal) {
                return api.getListOfRightsWindow(ordinal);
            }
        };
    }

    public Iterable<Rights> findRightsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Rights>(matches.iterator()) {
            public Rights getData(int ordinal) {
                return api.getRights(ordinal);
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

    public Iterable<AvailableAssets> findAvailableAssetsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<AvailableAssets>(matches.iterator()) {
            public AvailableAssets getData(int ordinal) {
                return api.getAvailableAssets(ordinal);
            }
        };
    }

    public Iterable<Flags> findFlagsMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Flags>(matches.iterator()) {
            public Flags getData(int ordinal) {
                return api.getFlags(ordinal);
            }
        };
    }

    public Iterable<VideoNodeType> findVideoNodeTypeMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoNodeType>(matches.iterator()) {
            public VideoNodeType getData(int ordinal) {
                return api.getVideoNodeType(ordinal);
            }
        };
    }

    public Iterable<VideoHierarchyInfo> findVideoHierarchyInfoMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<VideoHierarchyInfo>(matches.iterator()) {
            public VideoHierarchyInfo getData(int ordinal) {
                return api.getVideoHierarchyInfo(ordinal);
            }
        };
    }

    public Iterable<Status> findStatusMatches(Object... keys) {
        HollowHashIndexResult matches = idx.findMatches(keys);
        if(matches == null) return Collections.emptySet();

        return new AbstractHollowOrdinalIterable<Status>(matches.iterator()) {
            public Status getData(int ordinal) {
                return api.getStatus(ordinal);
            }
        };
    }

}