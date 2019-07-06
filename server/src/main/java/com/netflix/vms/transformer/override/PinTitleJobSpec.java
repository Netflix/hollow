package com.netflix.vms.transformer.override;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PinTitleJobSpec implements Comparable<PinTitleJobSpec> {
    final String id;
    final long version;
    final int[] topNodes;

    public PinTitleJobSpec(long version, int... topNodes) {
        this.version = version;
        this.topNodes = topNodes;
        this.id = createId(version);
    }

    public PinTitleJobSpec merge(PinTitleJobSpec other) {
        if (!this.getID().equals(other.getID())) {
            throw new IllegalArgumentException("Tried to merge to jobSpecs with different Ids: " + this.getID() + " vs " + other.getID());
        }

        // Dedupe
        Set<Integer> topNodeSet = new HashSet<>();
        for(int id : this.topNodes) {
            topNodeSet.add(id);
        }
        for(int id : other.topNodes) {
            topNodeSet.add(id);
        }
        int i = 0;
        int[] newTopNodes = new int[topNodeSet.size()];
        for (Integer id : topNodeSet) {
            newTopNodes[i++] = id;
        }

        PinTitleJobSpec spec = new PinTitleJobSpec(this.version, newTopNodes);
        return spec;
    }

    private static String createId(long version) {
        return "o" + ":" + version; // o represents output-based pinning
    }

    public String getID() {
        return id;
    }


    @Override
    public int compareTo(PinTitleJobSpec o) {
        int result = Long.compare(this.version, o.version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PinTitleJobSpec other = (PinTitleJobSpec) obj;
        if (!Arrays.equals(topNodes, other.topNodes)) return false;
        if (version != other.version) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(topNodes);
        result = prime * result + (int) (version ^ (version >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return getID() + ":" + PinTitleHelper.toString(topNodes);
    }
}