import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields={"onboardingSequence"})
public class OnboardingItem {

    long onboardingSequence;
    String onboardingItemName;
    boolean doneStatus;

    public OnboardingItem(long onboardingSequence, String onboardingItemName, boolean doneStatus) {
        this.onboardingSequence = onboardingSequence;
        this.onboardingItemName = onboardingItemName;
        this.doneStatus = doneStatus;
    }
}
