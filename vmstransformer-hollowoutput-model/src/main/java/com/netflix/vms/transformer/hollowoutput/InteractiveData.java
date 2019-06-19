package com.netflix.vms.transformer.hollowoutput;

import java.util.Objects;

/**
 * This class contains metadata about interactive videos.
 */
public class InteractiveData {

    // The type of interactive video, sourced from Oscar, is enumified in videometadata
    public String interactiveType = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteractiveData that = (InteractiveData) o;
        return Objects.equals(interactiveType, that.interactiveType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interactiveType);
    }

    @Override
    public String toString() {
        return "InteractiveData{" +
                "interactiveType='" + interactiveType + '\'' +
                '}';
    }
}
