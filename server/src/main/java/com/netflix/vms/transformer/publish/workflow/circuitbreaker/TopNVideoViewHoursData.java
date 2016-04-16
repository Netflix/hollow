package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import java.util.Map;


public interface TopNVideoViewHoursData {

    public String getCountryId();
    public float getCountryViewHrs1Day();
    public Map<Integer, Float> getVideoViewHrs1Day();

}