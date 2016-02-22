package com.netflix.vms.transformer.hollowoutput;


public class AudioChannelsDescriptor implements Cloneable {

    public int numberOfChannels = java.lang.Integer.MIN_VALUE;
    public Strings name = null;
    public Strings description = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AudioChannelsDescriptor))
            return false;

        AudioChannelsDescriptor o = (AudioChannelsDescriptor) other;
        if(o.numberOfChannels != numberOfChannels) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    public AudioChannelsDescriptor clone() {
        try {
            return (AudioChannelsDescriptor)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}