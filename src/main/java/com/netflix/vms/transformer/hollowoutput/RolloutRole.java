package com.netflix.vms.transformer.hollowoutput;


public class RolloutRole implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public int characterId = java.lang.Integer.MIN_VALUE;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public VPerson person = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + characterId;
        hashCode = hashCode * 31 + sequenceNumber;
        hashCode = hashCode * 31 + (person == null ? 1237 : person.hashCode());
        return hashCode;
    }

    public RolloutRole clone() {
        try {
            RolloutRole clone = (RolloutRole)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}