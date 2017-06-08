package com.netflix.vms.transformer.hollowoutput;

public class ArtworkScreensaverPassthrough implements Cloneable {

    public int startX = java.lang.Integer.MIN_VALUE;
    public int endX = java.lang.Integer.MIN_VALUE;
    public int offsetY = java.lang.Integer.MIN_VALUE;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + endX;
        result = prime * result + offsetY;
        result = prime * result + startX;
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
        ArtworkScreensaverPassthrough other = (ArtworkScreensaverPassthrough) obj;
        if (endX != other.endX)
            return false;
        if (offsetY != other.offsetY)
            return false;
        if (startX != other.startX)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkBasicPassthrough{");
        builder.append("startX=").append(startX);
        builder.append(",endX=").append(endX);
        builder.append(",offsetY=").append(offsetY);
        builder.append("}");
        
        return builder.toString();
    }

    public ArtworkScreensaverPassthrough clone() {
        try {
            ArtworkScreensaverPassthrough clone = (ArtworkScreensaverPassthrough)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;

}
