package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TimedTextTypeDescriptor implements Cloneable {

    public char[] nameStr = null;

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

    public TimedTextTypeDescriptor clone() {
        try {
            TimedTextTypeDescriptor clone = (TimedTextTypeDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}