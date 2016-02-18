package com.netflix.vms.transformer.hollowoutput;


public class RolloutCast {

    public VPerson person;
    public int sequenceNumber;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutCast))
            return false;

        RolloutCast o = (RolloutCast) other;
        if(o.person == null) {
            if(person != null) return false;
        } else if(!o.person.equals(person)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}