package com.netflix.hollow.api.client;

public interface IHollowUpdatePlanner {
    public HollowUpdatePlan planUpdate(long currentVersion, long desiredVersion, boolean allowSnapshot) throws Exception;

}
