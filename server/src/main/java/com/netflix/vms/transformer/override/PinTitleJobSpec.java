package com.netflix.vms.transformer.override;

import java.util.Arrays;

public class PinTitleJobSpec implements Comparable<PinTitleJobSpec> {
    final long version;
    final int[] topNodes;
    final boolean isInputBased;

    public PinTitleJobSpec(boolean isInputBased, long version, int... topNodes) {
        this.version = version;
        this.isInputBased = isInputBased;
        this.topNodes = topNodes;
    }


    @Override
    public int compareTo(PinTitleJobSpec o) {
        int result = Long.compare(this.version, o.version);
        if (result != 0) return result;

        return Boolean.compare(this.isInputBased, o.isInputBased);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PinTitleJobSpec other = (PinTitleJobSpec) obj;
        if (isInputBased != other.isInputBased) return false;
        if (!Arrays.equals(topNodes, other.topNodes)) return false;
        if (version != other.version) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isInputBased ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(topNodes);
        result = prime * result + (int) (version ^ (version >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return isInputBased ? "i" : "o" + ":" + version + ":" + PinTitleHelper.toString(topNodes);
    }
}