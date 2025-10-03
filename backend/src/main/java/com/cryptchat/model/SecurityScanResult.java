package com.cryptchat.model;

import java.time.LocalDateTime;
import java.util.List;

public class SecurityScanResult {
    private ScanStatus status;
    private List<SecurityThreat> threats;
    public SecurityScanResult() {
		super();
	}

	public ScanStatus getStatus() {
		return status;
	}

	public void setStatus(ScanStatus status) {
		this.status = status;
	}

	public List<SecurityThreat> getThreats() {
		return threats;
	}

	public void setThreats(List<SecurityThreat> threats) {
		this.threats = threats;
	}

	public Boolean getBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public LocalDateTime getScanTime() {
		return scanTime;
	}

	public void setScanTime(LocalDateTime scanTime) {
		this.scanTime = scanTime;
	}

	private Boolean blocked;
    private LocalDateTime scanTime;

    public enum ScanStatus {
        SCANNING, SAFE, THREAT_DETECTED, BLOCKED, ERROR
    }
}