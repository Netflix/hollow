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

    public static HollowUpdatePlan DO_NOTHING = new HollowUpdatePlan(Collections.emptyList());

    private final List<HollowConsumer.Blob> transitions;

    private HollowUpdatePlan(List<HollowConsumer.Blob> transitions) {
        this.transitions = transitions;
    }

    public HollowUpdatePlan() {
        this.transitions = new ArrayList();
    }

    public boolean isSnapshotPlan() {
        return !transitions.isEmpty() && transitions.get(0).isSnapshot();
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

    /**
     * Creates a new plan with an additional transition appended.
     *
     * @param transition the transition blob to append
     * @return a new HollowUpdatePlan with the additional transition
     */
    public HollowUpdatePlan withAdditionalTransition(HollowConsumer.Blob transition) {
        List<HollowConsumer.Blob> newTransitions = new ArrayList<>(this.transitions);
        newTransitions.add(transition);
        return new HollowUpdatePlan(newTransitions);
    }

    /**
     * Checks if this plan contains a repair transition.
     *
     * @return true if a REPAIR transition exists in this plan
     */
    public boolean hasRepairTransition() {
        return transitions.stream()
            .anyMatch(blob -> blob.getBlobType() == HollowConsumer.Blob.BlobType.REPAIR);
    }

    /**
     * Gets the first repair transition in this plan, if any.
     *
     * @return the repair transition blob, or null if none exists
     */
    public HollowConsumer.Blob getRepairTransition() {
        return transitions.stream()
            .filter(blob -> blob.getBlobType() == HollowConsumer.Blob.BlobType.REPAIR)
            .findFirst()
            .orElse(null);
    }

    @Override
    public Iterator<HollowConsumer.Blob> iterator() {
        return transitions.iterator();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (transitions!= null) {
            for (int i=0; i<transitions.size(); i++) {
                HollowConsumer.Blob blob = transitions.get(i);
                result.append(blob.getBlobType()).append(" to ").append(blob.getToVersion());
                if (i < transitions.size()-1) {
                    result.append(", ");
                }
            }
        }
        return result.toString();
    }
}
