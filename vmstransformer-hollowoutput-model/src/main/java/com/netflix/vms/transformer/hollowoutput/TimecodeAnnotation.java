package com.netflix.vms.transformer.hollowoutput;

import java.util.Objects;

public class TimecodeAnnotation implements Cloneable {

	public char[] type;
	public long startMillis;
	public long endMillis;
	@Override
	public int hashCode() {
		return Objects.hash(type, startMillis, endMillis);
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
		return Objects.equals(type, other.type) && Objects.equals(startMillis, other.startMillis)
				&& Objects.equals(endMillis, other.endMillis);
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
