package com.netflix.vms.transformer.rest.blobinfo;

import com.netflix.aws.db.Item;
import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.rest.blobinfo.BlobImageEntry.AttributeKeys;
import com.netflix.vms.transformer.rest.blobinfo.BlobImageEntry.BlobType;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Loader to fetch blob info (snapshot, delta and reserve delta)
 *
 * @author dsu
 */
@SuppressWarnings("deprecation")
public class BlobImageEntryLoader {
    private final FileStore fileStore;
    private final HollowBlobKeybaseBuilder keybaseBuilder;
    private SortedMap<String, BlobImageEntry> entryMap = new TreeMap<String, BlobImageEntry>();

    public BlobImageEntryLoader(String vip, FileStore fileStore) {
        this.keybaseBuilder = new HollowBlobKeybaseBuilder(vip);
        this.fileStore = fileStore;
    }

    private static void put(BlobType type, final Item item, final Map<String, BlobImageEntry> entryMap) {
        final String id = item.getIdentifier();
        final ItemAttribute dataVersionItem = item.getAttribute(AttributeKeys.dataVersion.name());

        final String version = dataVersionItem != null ? dataVersionItem.getValue() : id;
        BlobImageEntry entry = entryMap.get(version);
        if (entry == null) {
            entry = new BlobImageEntry(version);
            entryMap.put(version, entry);
        }

        entry.put(type, item);
    }

    /**
     * Fetch and return Map of Version to Image Entry with version sorted descending order (latest first)
     */
    public SortedMap<String, BlobImageEntry> fetchEntryMap() throws Exception {
        return fetchEntryMap(NetflixConfiguration.getRegionEnum());
    }

    /**
     * Fetch and return Map of Version to Image Entry with version sorted descending order (latest first)
     */
    public SortedMap<String, BlobImageEntry> fetchEntryMap(RegionEnum region) throws Exception {
        final SortedMap<String, BlobImageEntry> tmpEntryMap = new TreeMap<String, BlobImageEntry>(Collections.reverseOrder());

        for (final BlobType type : BlobType.values()) {
            String keybase = null;
            switch(type) {
            case SNAPSHOT:
                keybase = keybaseBuilder.getSnapshotKeybase();
                break;
            case DELTA:
                keybase = keybaseBuilder.getDeltaKeybase();
                break;
            case REVERSEDELTA:
                keybase = keybaseBuilder.getReverseDeltaKeybase();
                break;
            }
            
            for (final Item item : fileStore.getAllVersionItems(keybase, region)) {
                put(type, item, tmpEntryMap);
            }
        }

        entryMap = tmpEntryMap;
        return getLastFetchedEntryMap();
    }

    /**
     * Return last fetched Map of Version to Image Entry with version sorted descending order (latest first)
     */
    public SortedMap<String, BlobImageEntry> getLastFetchedEntryMap() {
        return entryMap;
    }
}
