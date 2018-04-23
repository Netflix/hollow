package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="Rights")
public class Rights implements Cloneable {

    public List<RightsWindow> windows = null;

    public Rights() { }

    public Rights(List<RightsWindow> value) {
        this.windows = value;
    }

    public Rights setWindows(List<RightsWindow> windows) {
        this.windows = windows;
        return this;
    }
    public Rights addToWindows(RightsWindow rightsWindow) {
        if (this.windows == null) {
            this.windows = new ArrayList<RightsWindow>();
        }
        this.windows.add(rightsWindow);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Rights))
            return false;

        Rights o = (Rights) other;
        if(o.windows == null) {
            if(windows != null) return false;
        } else if(!o.windows.equals(windows)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (windows == null ? 1237 : windows.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Rights{");
        builder.append("windows=").append(windows);
        builder.append("}");
        return builder.toString();
    }

    public Rights clone() {
        try {
            Rights clone = (Rights)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}