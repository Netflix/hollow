package com.netflix.vms.transformer.hollowoutput;


public class AudioChannelsDescriptor {

    public int numberOfChannels;
    public Strings name;
    public Strings description;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}