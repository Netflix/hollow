package com.netflix.vms.transformer.hollowoutput;


public class RolloutCast implements Cloneable {

    public VPerson person = null;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (person == null ? 1237 : person.hashCode());
        hashCode = hashCode * 31 + sequenceNumber;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutCast{");
        builder.append("person=").append(person);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append("}");
        return builder.toString();
    }

    public RolloutCast clone() {
        try {
            RolloutCast clone = (RolloutCast)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
