package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowinput.ContractHollow;

public class ConsolidatedContractInfo {
	
	private ContractHollow contract = null;
	private long prePromoDays = Long.MAX_VALUE;
	private boolean dayAfterBroadcast = false;
	private boolean dayOfBroadcast = false;
	
	public ContractHollow getContract() {
		return contract;
	}
	public void setContract(ContractHollow contract) {
		this.contract = contract;
	}
	public long getPrePromoDays() {
		return prePromoDays;
	}
	public void setPrePromoDaysIfLesserThanExisting(long prePromoDays) {
		if(prePromoDays < this.prePromoDays)
			this.prePromoDays = prePromoDays;
	}
	public boolean isDayAfterBroadcast() {
		return dayAfterBroadcast;
	}
	public void mergeDayAfterBroadcast(boolean dayAfterBroadcast) {
		this.dayAfterBroadcast |= dayAfterBroadcast;
	}
	public boolean isDayOfBroadcast() {
		return dayOfBroadcast;
	}
	public void mergeDayOfBroadcast(boolean dayOfBroadcast) {
		this.dayOfBroadcast |= dayOfBroadcast;
	}
	public void setPrePromoDays(long prePromoDays) {
		this.prePromoDays = prePromoDays;
	}
	public void setDayAfterBroadcast(boolean dayAfterBroadcast) {
		this.dayAfterBroadcast = dayAfterBroadcast;
	}
	public void setDayOfBroadcast(boolean dayOfBroadcast) {
		this.dayOfBroadcast = dayOfBroadcast;
	}
	
	
	

}
