package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="IPLArtworkDerivative")
public class IPLArtworkDerivative implements Cloneable {

    public String recipeName = null;
    public int widthInPixels = java.lang.Integer.MIN_VALUE;
    public int heightInPixels = java.lang.Integer.MIN_VALUE;
    public int targetWidthInPixels = java.lang.Integer.MIN_VALUE;
    public int targetHeightInPixels = java.lang.Integer.MIN_VALUE;
    public String recipeDescriptor = null;
    public String cdnId = null;
    public String languageCode = null;
    public List<DerivativeTag> modifications = null;
    public List<DerivativeTag> overlayTypes = null;

    public IPLArtworkDerivative setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        return this;
    }
    public IPLArtworkDerivative setWidthInPixels(int widthInPixels) {
        this.widthInPixels = widthInPixels;
        return this;
    }
    public IPLArtworkDerivative setHeightInPixels(int heightInPixels) {
        this.heightInPixels = heightInPixels;
        return this;
    }
    public IPLArtworkDerivative setTargetWidthInPixels(int targetWidthInPixels) {
        this.targetWidthInPixels = targetWidthInPixels;
        return this;
    }
    public IPLArtworkDerivative setTargetHeightInPixels(int targetHeightInPixels) {
        this.targetHeightInPixels = targetHeightInPixels;
        return this;
    }
    public IPLArtworkDerivative setRecipeDescriptor(String recipeDescriptor) {
        this.recipeDescriptor = recipeDescriptor;
        return this;
    }
    public IPLArtworkDerivative setCdnId(String cdnId) {
        this.cdnId = cdnId;
        return this;
    }
    public IPLArtworkDerivative setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }
    public IPLArtworkDerivative setModifications(List<DerivativeTag> modifications) {
        this.modifications = modifications;
        return this;
    }
    public IPLArtworkDerivative setOverlayTypes(List<DerivativeTag> overlayTypes) {
        this.overlayTypes = overlayTypes;
        return this;
    }
    public IPLArtworkDerivative addToModifications(DerivativeTag derivativeTag) {
        if (this.modifications == null) {
            this.modifications = new ArrayList<DerivativeTag>();
        }
        this.modifications.add(derivativeTag);
        return this;
    }
    public IPLArtworkDerivative addToOverlayTypes(DerivativeTag derivativeTag) {
        if (this.overlayTypes == null) {
            this.overlayTypes = new ArrayList<DerivativeTag>();
        }
        this.overlayTypes.add(derivativeTag);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof IPLArtworkDerivative))
            return false;

        IPLArtworkDerivative o = (IPLArtworkDerivative) other;
        if(o.recipeName == null) {
            if(recipeName != null) return false;
        } else if(!o.recipeName.equals(recipeName)) return false;
        if(o.widthInPixels != widthInPixels) return false;
        if(o.heightInPixels != heightInPixels) return false;
        if(o.targetWidthInPixels != targetWidthInPixels) return false;
        if(o.targetHeightInPixels != targetHeightInPixels) return false;
        if(o.recipeDescriptor == null) {
            if(recipeDescriptor != null) return false;
        } else if(!o.recipeDescriptor.equals(recipeDescriptor)) return false;
        if(o.cdnId == null) {
            if(cdnId != null) return false;
        } else if(!o.cdnId.equals(cdnId)) return false;
        if(o.languageCode == null) {
            if(languageCode != null) return false;
        } else if(!o.languageCode.equals(languageCode)) return false;
        if(o.modifications == null) {
            if(modifications != null) return false;
        } else if(!o.modifications.equals(modifications)) return false;
        if(o.overlayTypes == null) {
            if(overlayTypes != null) return false;
        } else if(!o.overlayTypes.equals(overlayTypes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (recipeName == null ? 1237 : recipeName.hashCode());
        hashCode = hashCode * 31 + widthInPixels;
        hashCode = hashCode * 31 + heightInPixels;
        hashCode = hashCode * 31 + targetWidthInPixels;
        hashCode = hashCode * 31 + targetHeightInPixels;
        hashCode = hashCode * 31 + (recipeDescriptor == null ? 1237 : recipeDescriptor.hashCode());
        hashCode = hashCode * 31 + (cdnId == null ? 1237 : cdnId.hashCode());
        hashCode = hashCode * 31 + (languageCode == null ? 1237 : languageCode.hashCode());
        hashCode = hashCode * 31 + (modifications == null ? 1237 : modifications.hashCode());
        hashCode = hashCode * 31 + (overlayTypes == null ? 1237 : overlayTypes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("IPLArtworkDerivative{");
        builder.append("recipeName=").append(recipeName);
        builder.append(",widthInPixels=").append(widthInPixels);
        builder.append(",heightInPixels=").append(heightInPixels);
        builder.append(",targetWidthInPixels=").append(targetWidthInPixels);
        builder.append(",targetHeightInPixels=").append(targetHeightInPixels);
        builder.append(",recipeDescriptor=").append(recipeDescriptor);
        builder.append(",cdnId=").append(cdnId);
        builder.append(",languageCode=").append(languageCode);
        builder.append(",modifications=").append(modifications);
        builder.append(",overlayTypes=").append(overlayTypes);
        builder.append("}");
        return builder.toString();
    }

    public IPLArtworkDerivative clone() {
        try {
            IPLArtworkDerivative clone = (IPLArtworkDerivative)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}