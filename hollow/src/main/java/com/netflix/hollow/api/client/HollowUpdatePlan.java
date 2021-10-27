/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.client;

import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.HollowConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A plan, containing one or more {@link HollowConsumer.Blob}s, which will be used to update the current data state to a desired data state.
 */
public class HollowUpdatePlan implements Iterable<HollowConsumer.Blob> {

    public static HollowUpdatePlan DO_NOTHING = new HollowUpdatePlan(Collections.<HollowConsumer.Blob>emptyList());

    private final List<HollowConsumer.Blob> transitions;

    private boolean containsSchemaChange = false;

    private HollowUpdatePlan(List<HollowConsumer.Blob> transitions) {
        this.transitions = transitions;
    }

    public HollowUpdatePlan() {
        this.transitions = new ArrayList<HollowConsumer.Blob>();
    }

    public boolean isSnapshotPlan() {
        return !transitions.isEmpty() && transitions.get(0).isSnapshot();
    }

    public boolean isContainsSchemaChange() {
        return containsSchemaChange;
    }

    public void containsSchemaChange() {
        this.containsSchemaChange = true;
    }

    public HollowConsumer.Blob getSnapshotTransition() {
        if(!isSnapshotPlan())
            return null;
        return transitions.get(0);
    }

    public List<HollowConsumer.Blob> getDeltaTransitions() {
        if(!isSnapshotPlan())
            return transitions;
        return transitions.subList(1, transitions.size());
    }

    public HollowConsumer.Blob getTransition(int index) {
        return transitions.get(index);
    }

    public List<HollowConsumer.Blob> getTransitions() {
        return transitions;
    }

    public List<HollowConsumer.Blob.BlobType> getTransitionSequence() {
        return transitions.stream()
                .map(t -> t.getBlobType())
                .collect(toList());
    }

    public long destinationVersion(long currentVersion) {
        long dest = destinationVersion();
        return dest == HollowConstants.VERSION_NONE ? currentVersion : dest;
    }

    public long destinationVersion() {
        return transitions.isEmpty() ? HollowConstants.VERSION_NONE
            : transitions.get(transitions.size() - 1).getToVersion();
    }

    public int numTransitions() {
        return transitions.size();
    }

    public void add(HollowConsumer.Blob transition) {
        transitions.add(transition);
    }

    public void appendPlan(HollowUpdatePlan plan) {
        transitions.addAll(plan.transitions);
    }

    @Override
    public Iterator<HollowConsumer.Blob> iterator() {
        return transitions.iterator();
    }
}
