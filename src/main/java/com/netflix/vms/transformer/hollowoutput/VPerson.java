package com.netflix.vms.transformer.hollowoutput;


public class VPerson implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;

    public VPerson() { }

    public VPerson(int value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VPerson))
            return false;

        VPerson o = (VPerson) other;
        if(o.id != id) return false;
        return true;
    }

    public VPerson clone() {
        try {
            return (VPerson)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}