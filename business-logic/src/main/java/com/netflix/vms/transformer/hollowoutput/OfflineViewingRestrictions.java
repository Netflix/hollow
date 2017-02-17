package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OfflineViewingRestrictions implements Cloneable {
    public Set<DownloadableId> streamOnlyDownloadables = null;
    public List<CupKey> downloadOnlyCupKeys = null;
    public Map<Strings, LanguageRestrictions> downloadLanguageBcp47RestrictionsMap = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((downloadLanguageBcp47RestrictionsMap == null) ? 0
                        : downloadLanguageBcp47RestrictionsMap.hashCode());
        result = prime
                * result
                + ((downloadOnlyCupKeys == null) ? 0 : downloadOnlyCupKeys
                        .hashCode());
        result = prime
                * result
                + ((streamOnlyDownloadables == null) ? 0
                        : streamOnlyDownloadables.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OfflineViewingRestrictions other = (OfflineViewingRestrictions) obj;
        if (downloadLanguageBcp47RestrictionsMap == null) {
            if (other.downloadLanguageBcp47RestrictionsMap != null)
                return false;
        } else if (!downloadLanguageBcp47RestrictionsMap
                .equals(other.downloadLanguageBcp47RestrictionsMap))
            return false;
        if (downloadOnlyCupKeys == null) {
            if (other.downloadOnlyCupKeys != null)
                return false;
        } else if (!downloadOnlyCupKeys.equals(other.downloadOnlyCupKeys))
            return false;
        if (streamOnlyDownloadables == null) {
            if (other.streamOnlyDownloadables != null)
                return false;
        } else if (!streamOnlyDownloadables
                .equals(other.streamOnlyDownloadables))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "OfflineViewingRestrictions [streamOnlyDownloadables="
                + streamOnlyDownloadables + ", downloadOnlyCupKeys="
                + downloadOnlyCupKeys
                + ", downloadLanguageBcp47RestrictionsMap="
                + downloadLanguageBcp47RestrictionsMap + "]";
    }

    @Override
    public OfflineViewingRestrictions clone() {
        try {
            OfflineViewingRestrictions clone = (OfflineViewingRestrictions)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}
