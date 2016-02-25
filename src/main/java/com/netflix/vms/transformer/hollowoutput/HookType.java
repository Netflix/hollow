package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class HookType implements Cloneable {

    public char[] value = null;

    public HookType() { }

    public HookType(char[] value) {
        this.value = value;
    }

    public HookType(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof HookType))
            return false;

        HookType o = (HookType) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public HookType clone() {
        try {
            HookType clone = (HookType)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}