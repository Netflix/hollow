package com.netflix.vms.transformer.hollowoutput;


public class VRole implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;

    public VRole() { }

    public VRole(int value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VRole))
            return false;

        VRole o = (VRole) other;
        if(o.id != id) return false;
        return true;
    }

    public VRole clone() {
        try {
            return (VRole)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}