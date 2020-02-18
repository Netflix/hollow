package com.netflix.sunjeetsonboardingroot;

import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Set;

@HollowPrimaryKey(fields={"onboardingSequence"})
public class OnboardingItem {

    long onboardingSequence;

    @HollowInline
    String onboardingItemName;

    boolean doneStatus;

    public OnboardingItem(long onboardingSequence, String onboardingItemName, boolean doneStatus) {
        this.onboardingSequence = onboardingSequence;

        this.onboardingItemName = onboardingItemName;

        this.doneStatus = doneStatus;
    }
}
