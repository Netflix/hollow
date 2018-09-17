package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TimecodeAnnotation implements Cloneable {

	public char[] type;
	public long startMillis;
	public long endMillis;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endMillis ^ (endMillis >>> 32));
		result = prime * result + (int) (startMillis ^ (startMillis >>> 32));
		result = prime * result + ((type == null) ? 0 : Arrays.hashCode(type));
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
		TimecodeAnnotation other = (TimecodeAnnotation) obj;
		if (endMillis != other.endMillis)
			return false;
		if (startMillis != other.startMillis)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!Arrays.equals(other.type, type))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TimecodeAnnotation [type=" + type.toString() + ", startMillis=" + startMillis + ", endMillis=" + endMillis + "]";
	}
	
	public TimecodeAnnotation clone() {
		try {
			TimecodeAnnotation clone = (TimecodeAnnotation)super.clone();
			clone.__assigned_ordinal = -1;
			return clone;
		} catch(CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;

}
