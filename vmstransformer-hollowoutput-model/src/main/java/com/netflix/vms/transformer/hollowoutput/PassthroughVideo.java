package com.netflix.vms.transformer.hollowoutput;


public class PassthroughVideo implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;

    public PassthroughVideo() { }

    public PassthroughVideo(int value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PassthroughVideo))
            return false;

        PassthroughVideo o = (PassthroughVideo) other;
        if(o.id != id) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PassthroughVideo{");
        builder.append("id=").append(id);
        builder.append("}");
        return builder.toString();
    }

    public PassthroughVideo clone() {
        try {
            PassthroughVideo clone = (PassthroughVideo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
