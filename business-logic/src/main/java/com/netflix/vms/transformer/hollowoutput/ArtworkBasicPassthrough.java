package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.NullablePrimitiveBoolean;

import java.util.List;

public class ArtworkBasicPassthrough implements Cloneable {

    public List<__passthrough_string> themes = null;
    public List<__passthrough_string> awardCampaigns = null;
    public PassthroughString tone = null;
    public PassthroughString group_id = null;
    public BasicPassthroughLists lists = null;
    public List<__passthrough_string> identifiers = null;
    public PassthroughString design_attribute = null;
    public PassthroughString focal_point = null;
    public PassthroughString approval_source = null;
    public List<__passthrough_string> personIdStrs = null;
    public NullablePrimitiveBoolean approval_state = null;
    public ArtworkReExploreLongTimestamp reExploreLongTimestamp = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkBasicPassthrough))
            return false;

        ArtworkBasicPassthrough o = (ArtworkBasicPassthrough) other;
        if(o.themes == null) {
            if(themes != null) return false;
        } else if(!o.themes.equals(themes)) return false;
        if(o.awardCampaigns == null) {
            if(awardCampaigns != null) return false;
        } else if(!o.awardCampaigns.equals(awardCampaigns)) return false;
        if(o.tone == null) {
            if(tone != null) return false;
        } else if(!o.tone.equals(tone)) return false;
        if(o.group_id == null) {
            if(group_id != null) return false;
        } else if(!o.group_id.equals(group_id)) return false;
        if(o.lists == null) {
            if(lists != null) return false;
        } else if(!o.lists.equals(lists)) return false;
        if(o.identifiers == null) {
            if(identifiers != null) return false;
        } else if(!o.identifiers.equals(identifiers)) return false;
        if(o.design_attribute == null) {
            if(design_attribute != null) return false;
        } else if(!o.design_attribute.equals(design_attribute)) return false;
        if(o.focal_point == null) {
            if(focal_point != null) return false;
        } else if(!o.focal_point.equals(focal_point)) return false;
        if(o.approval_source == null) {
            if(approval_source != null) return false;
        } else if(!o.approval_source.equals(approval_source)) return false;
        if(o.personIdStrs == null) {
            if(personIdStrs != null) return false;
        } else if(!o.personIdStrs.equals(personIdStrs)) return false;
        if(o.approval_state != approval_state) return false;
        if (o.reExploreLongTimestamp == null) {
            if (reExploreLongTimestamp != null) return false;
        } else if (!o.reExploreLongTimestamp.equals(reExploreLongTimestamp)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (themes == null ? 1237 : themes.hashCode());
        hashCode = hashCode * 31 + (awardCampaigns == null ? 1237 : awardCampaigns.hashCode());
        hashCode = hashCode * 31 + (tone == null ? 1237 : tone.hashCode());
        hashCode = hashCode * 31 + (group_id == null ? 1237 : group_id.hashCode());
        hashCode = hashCode * 31 + (lists == null ? 1237 : lists.hashCode());
        hashCode = hashCode * 31 + (identifiers == null ? 1237 : identifiers.hashCode());
        hashCode = hashCode * 31 + (design_attribute == null ? 1237 : design_attribute.hashCode());
        hashCode = hashCode * 31 + (focal_point == null ? 1237 : focal_point.hashCode());
        hashCode = hashCode * 31 + (approval_source == null ? 1237 : approval_source.hashCode());
        hashCode = hashCode * 31 + (personIdStrs == null ? 1237 : personIdStrs.hashCode());
        hashCode = hashCode * 31 + (approval_state == null ? 1231 : approval_state.hashCode());
        hashCode = hashCode * 31 + (reExploreLongTimestamp == null ? 1231 : reExploreLongTimestamp.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkBasicPassthrough{");
        builder.append("themes=").append(themes);
        builder.append(",awardCampaigns=").append(awardCampaigns);
        builder.append(",tone=").append(tone);
        builder.append(",group_id=").append(group_id);
        builder.append(",lists=").append(lists);
        builder.append(",identifiers=").append(identifiers);
        builder.append(",design_attribute=").append(design_attribute);
        builder.append(",focal_point=").append(focal_point);
        builder.append(",approval_source=").append(approval_source);
        builder.append(",personIdStrs=").append(personIdStrs);
        builder.append(",approval_state=").append(approval_state);
        builder.append(",re_explore_timestamp=").append(reExploreLongTimestamp.toString());
        builder.append("}");
        return builder.toString();
    }

    public ArtworkBasicPassthrough clone() {
        try {
            ArtworkBasicPassthrough clone = (ArtworkBasicPassthrough)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}