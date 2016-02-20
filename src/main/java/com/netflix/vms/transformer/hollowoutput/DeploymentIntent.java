package com.netflix.vms.transformer.hollowoutput;


public class DeploymentIntent {

    public int profileId = java.lang.Integer.MIN_VALUE;
    public int bitrate = java.lang.Integer.MIN_VALUE;
    public ISOCountry country = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DeploymentIntent))
            return false;

        DeploymentIntent o = (DeploymentIntent) other;
        if(o.profileId != profileId) return false;
        if(o.bitrate != bitrate) return false;
        if(o.country == null) {
            if(country != null) return false;
        } else if(!o.country.equals(country)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}