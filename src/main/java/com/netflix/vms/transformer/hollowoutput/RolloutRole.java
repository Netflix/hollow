package com.netflix.vms.transformer.hollowoutput;


public class RolloutRole {

    public int id;
    public int characterId;
    public int sequenceNumber;
    public VPerson person;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutRole))
            return false;

        RolloutRole o = (RolloutRole) other;
        if(o.id != id) return false;
        if(o.characterId != characterId) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.person == null) {
            if(person != null) return false;
        } else if(!o.person.equals(person)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}