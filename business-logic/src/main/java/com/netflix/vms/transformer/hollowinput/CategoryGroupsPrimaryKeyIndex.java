package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CategoryGroupsPrimaryKeyIndex implements HollowConsumer.RefreshListener {

    private HollowPrimaryKeyIndex idx;
    private VMSHollowInputAPI api;

    public CategoryGroupsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CategoryGroups")).getPrimaryKey().getFieldPaths());
    }

    public CategoryGroupsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        consumer.getRefreshLock().lock();
        try {
            this.api = (VMSHollowInputAPI)consumer.getAPI();
            this.idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), "CategoryGroups", fieldPaths);
            idx.listenForDeltaUpdates();
            consumer.addRefreshListener(this);
        } catch(ClassCastException cce) {
            throw new ClassCastException("The HollowConsumer provided was not created with the VMSHollowInputAPI generated API class.");
        } finally {
            consumer.getRefreshLock().unlock();
        }
    }

    public CategoryGroupsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCategoryGroupsHollow(ordinal);
    }

    @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
        idx.detachFromDeltaUpdates();
        idx = new HollowPrimaryKeyIndex(stateEngine, idx.getPrimaryKey());
        idx.listenForDeltaUpdates();
        this.api = (VMSHollowInputAPI)api;
    }

    @Override public void refreshStarted(long currentVersion, long requestedVersion) { }
    @Override public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception { }
    @Override public void blobLoaded(HollowConsumer.Blob transition) { }
    @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) { }
    @Override public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) { }
}