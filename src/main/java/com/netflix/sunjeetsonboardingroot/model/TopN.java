package com.netflix.sunjeetsonboardingroot.model;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Set;

@HollowPrimaryKey(fields={"videoId"})
public class TopN {

    long videoId;

    Set<TopNAttribute> attributes;

    public TopN(long videoId, Set<TopNAttribute> attributes) {
        this.videoId = videoId;
        this.attributes = attributes;
    }
}