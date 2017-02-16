package com.netflix.vms.transformer.hollowoutput;

public class StreamCropParams implements Cloneable {
    public int x;
    public int y;
    public int height;
    public int width;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + width;
        result = prime * result + x;
        result = prime * result + y;
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
        StreamCropParams other = (StreamCropParams) obj;
        if (height != other.height)
            return false;
        if (width != other.width)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "StreamCropParams [x=" + x + ", y=" + y + ", height=" + height
                + ", width=" + width + "]";
    }
    public StreamCropParams clone() {
        try {
            StreamCropParams clone = (StreamCropParams)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
    
}
