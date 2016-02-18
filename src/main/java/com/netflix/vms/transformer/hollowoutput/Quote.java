package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class Quote {

    public int characterId;
    public char[] rawL10nLabel;
    public int sequenceNumber;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Quote))
            return false;

        Quote o = (Quote) other;
        if(o.characterId != characterId) return false;
        if(!Arrays.equals(o.rawL10nLabel, rawL10nLabel)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}