package com.netflix.hollow.test.consumer;

import com.netflix.hollow.api.consumer.HollowConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestBlobRetrieverWithNearestSnapshotMatch extends TestBlobRetriever {

    @Override
    public HollowConsumer.Blob retrieveSnapshotBlob(long desiredVersion) {
        long version = findNearestSnapshotVersion(desiredVersion);
        HollowConsumer.Blob b = snapshots.get(version);
        resetStream(b);
        return b;
    }


    private long findNearestSnapshotVersion(long desiredVersion) {
        List<Long> snapshotVersions = new ArrayList<>();
        snapshotVersions.addAll(snapshots.keySet());
        Collections.sort(snapshotVersions);
        int start = 0;
        int end = snapshotVersions.size() - 1;
        int mid = 0;
        while (start + 1< end) {
            mid = (start + end) / 2;
            if (mid < desiredVersion) {
                start = mid;
            } else if (mid > desiredVersion){
                end = mid;
            } else {
                return snapshotVersions.get(mid);
            }
        }
        if (end <= desiredVersion) {
            return snapshotVersions.get(end);
        }
        return snapshotVersions.get(start);
    }
}
