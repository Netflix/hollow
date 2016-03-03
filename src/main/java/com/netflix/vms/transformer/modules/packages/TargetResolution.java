package com.netflix.vms.transformer.modules.packages;

public class TargetResolution {

    private final int height;
    private final int width;
    public TargetResolution(int height, int width) {
        this.height = height;
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + width;
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
        TargetResolution other = (TargetResolution) obj;
        if (height != other.height)
            return false;
        if (width != other.width)
            return false;
        return true;
    }

}
