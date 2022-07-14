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
package com.netflix.hollow.tools.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A breadcrumbs-like unique identifier for a field's location within a type hierarchy, used in the {@link HollowDiff}. 
 * 
 * Calling toString() returns a human-readable representation of the field within the type hierarchy. 
 */
public class HollowDiffNodeIdentifier {

    private final List<HollowDiffNodeIdentifier> parents;

    private final String viaFieldName;
    private final String nodeName;

    public HollowDiffNodeIdentifier(String typeName) {
        this(null, null, typeName);
    }

    public HollowDiffNodeIdentifier(HollowDiffNodeIdentifier parent, String viaFieldName, String typeName) {
        this.parents = parent == null ?
                Collections.<HollowDiffNodeIdentifier>emptyList()
                : buildParentsList(parent);
        this.viaFieldName = viaFieldName;
        this.nodeName = typeName;
    }

    public List<HollowDiffNodeIdentifier> getParents() {
        return parents;
    }

    public String getViaFieldName() {
        return viaFieldName;
    }

    public String getNodeName() {
        return nodeName;
    }

    private List<HollowDiffNodeIdentifier> buildParentsList(HollowDiffNodeIdentifier immediateParent) {
        List<HollowDiffNodeIdentifier> parents = new ArrayList<HollowDiffNodeIdentifier>(immediateParent.getParents().size() + 1);
        parents.addAll(immediateParent.getParents());
        parents.add(immediateParent);
        return parents;
    }

    public int hashCode() {
        int hashCode = 0;

        for(int i = 0; i < parents.size(); i++) {
            String parentViaFieldName = parents.get(i).getViaFieldName();
            if(parentViaFieldName != null)
                hashCode = 31 * hashCode + parentViaFieldName.hashCode();
            hashCode = 31 * hashCode + parents.get(i).getNodeName().hashCode();
        }

        if(viaFieldName != null)
            hashCode = 31 * hashCode + viaFieldName.hashCode();
        hashCode = 31 * hashCode + nodeName.hashCode();

        return hashCode;
    }

    public boolean equals(Object other) {
        if(this == other)
            return true;
        if(other instanceof HollowDiffNodeIdentifier) {
            HollowDiffNodeIdentifier otherId = (HollowDiffNodeIdentifier) other;
            if(otherId.getParents().size() == parents.size()) {
                for(int i = parents.size() - 1; i >= 0; i--) {
                    HollowDiffNodeIdentifier myParent = parents.get(i);
                    HollowDiffNodeIdentifier otherParent = otherId.getParents().get(i);

                    if(!myParent.shallowEquals(otherParent))
                        return false;
                }

                return shallowEquals(otherId);
            }
        }
        return false;
    }

    /**
     * @return a human-readable representation of this field location
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if(parents.size() > 0) {
            builder.append(parents.get(0).getNodeName());
        }

        for(int i = 1; i < parents.size(); i++) {
            builder.append('.').append(parents.get(i).getViaFieldName());
        }

        builder.append('.').append(viaFieldName);

        builder.append(" (").append(nodeName).append(")");

        return builder.toString();
    }

    private boolean shallowEquals(HollowDiffNodeIdentifier other) {
        if(viaFieldName == null ?
                other.getViaFieldName() == null
                : viaFieldName.equals(other.getViaFieldName()))
            return nodeName.equals(other.getNodeName());
        return false;
    }
}
