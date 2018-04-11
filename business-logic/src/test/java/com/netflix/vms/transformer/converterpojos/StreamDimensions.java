package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamDimensions")
public class StreamDimensions implements Cloneable {

    public int widthInPixels = java.lang.Integer.MIN_VALUE;
    public int heightInPixels = java.lang.Integer.MIN_VALUE;
    public int pixelAspectRatioWidth = java.lang.Integer.MIN_VALUE;
    public int pixelAspectRatioHeight = java.lang.Integer.MIN_VALUE;
    public int targetWidthInPixels = java.lang.Integer.MIN_VALUE;
    public int targetHeightInPixels = java.lang.Integer.MIN_VALUE;

    public StreamDimensions setWidthInPixels(int widthInPixels) {
        this.widthInPixels = widthInPixels;
        return this;
    }
    public StreamDimensions setHeightInPixels(int heightInPixels) {
        this.heightInPixels = heightInPixels;
        return this;
    }
    public StreamDimensions setPixelAspectRatioWidth(int pixelAspectRatioWidth) {
        this.pixelAspectRatioWidth = pixelAspectRatioWidth;
        return this;
    }
    public StreamDimensions setPixelAspectRatioHeight(int pixelAspectRatioHeight) {
        this.pixelAspectRatioHeight = pixelAspectRatioHeight;
        return this;
    }
    public StreamDimensions setTargetWidthInPixels(int targetWidthInPixels) {
        this.targetWidthInPixels = targetWidthInPixels;
        return this;
    }
    public StreamDimensions setTargetHeightInPixels(int targetHeightInPixels) {
        this.targetHeightInPixels = targetHeightInPixels;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDimensions))
            return false;

        StreamDimensions o = (StreamDimensions) other;
        if(o.widthInPixels != widthInPixels) return false;
        if(o.heightInPixels != heightInPixels) return false;
        if(o.pixelAspectRatioWidth != pixelAspectRatioWidth) return false;
        if(o.pixelAspectRatioHeight != pixelAspectRatioHeight) return false;
        if(o.targetWidthInPixels != targetWidthInPixels) return false;
        if(o.targetHeightInPixels != targetHeightInPixels) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + widthInPixels;
        hashCode = hashCode * 31 + heightInPixels;
        hashCode = hashCode * 31 + pixelAspectRatioWidth;
        hashCode = hashCode * 31 + pixelAspectRatioHeight;
        hashCode = hashCode * 31 + targetWidthInPixels;
        hashCode = hashCode * 31 + targetHeightInPixels;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamDimensions{");
        builder.append("widthInPixels=").append(widthInPixels);
        builder.append(",heightInPixels=").append(heightInPixels);
        builder.append(",pixelAspectRatioWidth=").append(pixelAspectRatioWidth);
        builder.append(",pixelAspectRatioHeight=").append(pixelAspectRatioHeight);
        builder.append(",targetWidthInPixels=").append(targetWidthInPixels);
        builder.append(",targetHeightInPixels=").append(targetHeightInPixels);
        builder.append("}");
        return builder.toString();
    }

    public StreamDimensions clone() {
        try {
            StreamDimensions clone = (StreamDimensions)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}