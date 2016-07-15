package com.netflix.vms.transformer.override;

public class TitleOverrideJobSpec implements Comparable<TitleOverrideJobSpec> {
    final long version;
    final int topNode;
    final boolean isInputBased;

    public TitleOverrideJobSpec(long version, int topNode, boolean isInputBased) {
        this.version = version;
        this.topNode = topNode;
        this.isInputBased = isInputBased;
    }

    @Override
    public int compareTo(TitleOverrideJobSpec o) {
        int result = Long.compare(this.version, o.version);
        if (result != 0) return result;

        result = Integer.compare(this.topNode, o.topNode);
        if (result != 0) return result;

        return Boolean.compare(this.isInputBased, o.isInputBased);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TitleOverrideJobSpec other = (TitleOverrideJobSpec) obj;
        if (isInputBased != other.isInputBased) return false;
        if (topNode != other.topNode) return false;
        if (version != other.version) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isInputBased ? 1231 : 1237);
        result = prime * result + topNode;
        result = prime * result + (int) (version ^ (version >>> 32));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TitleOverrideJobSpec [version=");
        builder.append(version);
        builder.append(", topNode=");
        builder.append(topNode);
        builder.append(", isInputBased=");
        builder.append(isInputBased);
        builder.append("]");
        return builder.toString();
    }
}
