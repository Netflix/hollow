package com.netflix.vms.transformer.hollowoutput;


public class VideoDimensionsDescriptor implements Cloneable {

    public int dimensions = java.lang.Integer.MIN_VALUE;
    public Strings name = null;
    public Strings description = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoDimensionsDescriptor))
            return false;

        VideoDimensionsDescriptor o = (VideoDimensionsDescriptor) other;
        if(o.dimensions != dimensions) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + dimensions;
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoDimensionsDescriptor{");
        builder.append("dimensions=").append(dimensions);
        builder.append(",name=").append(name);
        builder.append(",description=").append(description);
        builder.append("}");
        return builder.toString();
    }

    public VideoDimensionsDescriptor clone() {
        try {
            VideoDimensionsDescriptor clone = (VideoDimensionsDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}