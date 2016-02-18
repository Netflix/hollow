package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TimedTextTypeDescriptor {

    public char[] nameStr;

    public TimedTextTypeDescriptor() { }

    public TimedTextTypeDescriptor(char[] value) {
        this.nameStr = value;
    }

    public TimedTextTypeDescriptor(String value) {
        this.nameStr = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TimedTextTypeDescriptor))
            return false;

        TimedTextTypeDescriptor o = (TimedTextTypeDescriptor) other;
        if(!Arrays.equals(o.nameStr, nameStr)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}