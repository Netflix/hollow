package com.netflix.vms.transformer.hollowoutput;

public class SchedulePhaseInfo {
	public long start = 0l;
	public long end = java.lang.Long.MIN_VALUE;
	public boolean isOffset = true;
	public boolean isAutomatedImg = true;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + (isOffset ? 1231 : 1237);
		result = prime * result + (int) (start ^ (start >>> 32));
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
		SchedulePhaseInfo other = (SchedulePhaseInfo) obj;
		if (end != other.end)
			return false;
		if (isOffset != other.isOffset)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
	
}
