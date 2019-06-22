package com.netflix.vms.transformer.input.datasets;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Gk2StatusAPI;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Gatekeeper2Dataset extends UpstreamDataset {

    private final Gk2StatusAPI api;
    private final HollowPrimaryKeyIndex statusIdx;
    private final HollowHashIndex allStatusIdx;

    private static final HollowOrdinalIterator EMPTY_ORDINAL_ITERATOR = () -> HollowOrdinalIterator.NO_MORE_ORDINALS;

    public Gatekeeper2Dataset(InputState input) {
        super(input);
        HollowReadStateEngine readStateEngine = input.getStateEngine();
        this.api = new Gk2StatusAPI(readStateEngine);
        this.statusIdx = new HollowPrimaryKeyIndex(readStateEngine, new PrimaryKey("Status", "movieId", "countryCode"));
        this.allStatusIdx = new HollowHashIndex(readStateEngine, "Status", "", "movieId");
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return api;
    }

    public Status getStatus(Long videoId, String country) {
        int ordinal = statusIdx.getMatchingOrdinal(videoId, country);
        return ordinal == -1 ? null : api.getStatus(ordinal);
    }

    public Iterable<Status> getAllStatus(Long videoId) {
        HollowHashIndexResult matches = this.allStatusIdx.findMatches(videoId);

        return () -> new Iterator<Status>() {
            private final HollowOrdinalIterator iter = matches == null ? EMPTY_ORDINAL_ITERATOR : matches.iterator();
            private int nextOrdinal = iter.next();
            private Status nextStatus = findNextStatus();

            public boolean hasNext() {
                return nextStatus != null;
            }

            @Override
            public Status next() {
                if(nextStatus == null)
                    throw new NoSuchElementException();
                Status s = nextStatus;
                nextStatus = findNextStatus();
                return s;
            }

            private Status findNextStatus() {
                while(nextOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    Status status = api.getStatus(nextOrdinal);
                    nextOrdinal = iter.next();
                    return status;
                }
                return null;
            }
        };
    }
}
