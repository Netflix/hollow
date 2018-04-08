package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="RightsWindow")
public class RightsWindow implements Cloneable {

    public long startDate = java.lang.Long.MIN_VALUE;
    public long endDate = java.lang.Long.MIN_VALUE;
    public boolean onHold = false;
    public List<RightsWindowContract> contractIdsExt = null;

    public RightsWindow setStartDate(long startDate) {
        this.startDate = startDate;
        return this;
    }
    public RightsWindow setEndDate(long endDate) {
        this.endDate = endDate;
        return this;
    }
    public RightsWindow setOnHold(boolean onHold) {
        this.onHold = onHold;
        return this;
    }
    public RightsWindow setContractIdsExt(List<RightsWindowContract> contractIdsExt) {
        this.contractIdsExt = contractIdsExt;
        return this;
    }
    public RightsWindow addToContractIdsExt(RightsWindowContract rightsWindowContract) {
        if (this.contractIdsExt == null) {
            this.contractIdsExt = new ArrayList<RightsWindowContract>();
        }
        this.contractIdsExt.add(rightsWindowContract);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RightsWindow))
            return false;

        RightsWindow o = (RightsWindow) other;
        if(o.startDate != startDate) return false;
        if(o.endDate != endDate) return false;
        if(o.onHold != onHold) return false;
        if(o.contractIdsExt == null) {
            if(contractIdsExt != null) return false;
        } else if(!o.contractIdsExt.equals(contractIdsExt)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (startDate ^ (startDate >>> 32));
        hashCode = hashCode * 31 + (int) (endDate ^ (endDate >>> 32));
        hashCode = hashCode * 31 + (onHold? 1231 : 1237);
        hashCode = hashCode * 31 + (contractIdsExt == null ? 1237 : contractIdsExt.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RightsWindow{");
        builder.append("startDate=").append(startDate);
        builder.append(",endDate=").append(endDate);
        builder.append(",onHold=").append(onHold);
        builder.append(",contractIdsExt=").append(contractIdsExt);
        builder.append("}");
        return builder.toString();
    }

    public RightsWindow clone() {
        try {
            RightsWindow clone = (RightsWindow)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}