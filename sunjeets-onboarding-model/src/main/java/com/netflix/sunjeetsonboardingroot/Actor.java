package com.netflix.sunjeetsonboardingroot;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;


@HollowPrimaryKey(fields = "actorId")
public class Actor {
    public int actorId;
    public String actorName;

    public Actor() {
    }

    public Actor(int actorId, String actorName) {
        this.actorId = actorId;
        this.actorName = actorName;
    }

}
