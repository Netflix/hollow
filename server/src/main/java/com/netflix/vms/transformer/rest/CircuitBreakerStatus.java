package com.netflix.vms.transformer.rest;

import java.util.List;
import java.util.Map;

public class CircuitBreakerStatus {
	private String vipName;
	private String circuitBreakerName;
	private String cycleId;
	private String cycleSuccess;
	private boolean cbSuccess;
	private Map<String, List<Integer>> failedVideos;
	private List<String> errors;
	private List<String> warnings;
	
	public String getVipName() {
		return vipName;
	}
	public void setVipName(String vipName) {
		this.vipName = vipName;
	}
	public String getCircuitBreakerName() {
		return circuitBreakerName;
	}
	public void setCircuitBreakerName(String circuitBreakerName) {
		this.circuitBreakerName = circuitBreakerName;
	}
	public String getCycleId() {
		return cycleId;
	}
	public void setCycleId(String cycleId) {
		this.cycleId = cycleId;
	}
	public String getCycleSuccess() {
		return cycleSuccess;
	}
	public void setCycleSuccess(String cycleSuccess) {
		this.cycleSuccess = cycleSuccess;
	}
	public boolean isCbSuccess() {
		return cbSuccess;
	}
	public void setCbSuccess(boolean cbSuccess) {
		this.cbSuccess = cbSuccess;
	}
	public Map<String, List<Integer>> getFailedVideos() {
		return failedVideos;
	}
	public void setFailedVideos(Map<String, List<Integer>> failedVideos) {
		this.failedVideos = failedVideos;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public List<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	
	
}
