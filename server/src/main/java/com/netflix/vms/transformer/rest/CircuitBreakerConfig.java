package com.netflix.vms.transformer.rest;

import java.util.List;
import java.util.Map;

public class CircuitBreakerConfig {
	private String vip;
	private boolean enabled;
	private List<String> allowedOverrides;
	private List<String> enabledOverrides;
	private List<String> disabledOverrides;
	private Map<String, List<String>> exclusions;
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public List<String> getAllowedOverrides() {
		return allowedOverrides;
	}
	public void setAllowedOverrides(List<String> allowedOverrides) {
		this.allowedOverrides = allowedOverrides;
	}
	public List<String> getEnabledOverrides() {
		return enabledOverrides;
	}
	public void setEnabledOverrides(List<String> enabledOverrides) {
		this.enabledOverrides = enabledOverrides;
	}
	public List<String> getDisabledOverrides() {
		return disabledOverrides;
	}
	public void setDisabledOverrides(List<String> disabledOverrides) {
		this.disabledOverrides = disabledOverrides;
	}
	public Map<String, List<String>> getExclusions() {
		return exclusions;
	}
	public void setExclusions(Map<String, List<String>> exclusions) {
		this.exclusions = exclusions;
	}
	
	
}
