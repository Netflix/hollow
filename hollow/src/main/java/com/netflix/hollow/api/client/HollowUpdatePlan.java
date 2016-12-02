/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A plan, containing one or more {@link HollowBlob}s, which will be used to update the current data state to a desired data state.
 * 
 * @author dkoszewnik
 *
 */
public class HollowUpdatePlan implements Iterable<HollowBlob> {

    public static HollowUpdatePlan DO_NOTHING = new HollowUpdatePlan(Collections.<HollowBlob>emptyList());

    private final List<HollowBlob> transitions;

    private HollowUpdatePlan(List<HollowBlob> transitions) {
        this.transitions = transitions;
    }

    public HollowUpdatePlan() {
        this.transitions = new ArrayList<HollowBlob>();
    }

    public boolean isSnapshotPlan() {
        return !transitions.isEmpty() && transitions.get(0).isSnapshot();
    }

    public HollowBlob getSnapshotTransition() {
        if(!isSnapshotPlan())
            return null;
        return transitions.get(0);
    }

    public List<HollowBlob> getDeltaTransitions() {
        if(!isSnapshotPlan())
            return transitions;
        return transitions.subList(1, transitions.size());
    }

    public HollowBlob getTransition(int index) {
        return transitions.get(index);
    }

    public long destinationVersion(long currentVersion) {
        long dest = destinationVersion();
        if(dest == Long.MIN_VALUE)
            return currentVersion;
        return dest;
    }

    public long destinationVersion() {
        if(transitions.isEmpty())
            return Long.MIN_VALUE;

        HollowBlob lastTransition = transitions.get(transitions.size() - 1);
        return lastTransition.getToVersion();
    }

    public int numTransitions() {
        return transitions.size();
    }

    public void add(HollowBlob transition) {
        transitions.add(transition);
    }

    public void appendPlan(HollowUpdatePlan plan) {
        transitions.addAll(plan.transitions);
    }

    @Override
    public Iterator<HollowBlob> iterator() {
        return transitions.iterator();
    }
}
