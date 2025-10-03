package com.cryptchat.model;

import java.util.List;

public class SecurityThreat {
	public SecurityThreat() {
		super();
	}
    public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRisk() {
		return risk;
	}
	public void setRisk(String risk) {
		this.risk = risk;
	}
	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	public List<String> getReasons() {
		return reasons;
	}
	public void setReasons(List<String> reasons) {
		this.reasons = reasons;
	}
	public Integer getRiskScore() {
		return riskScore;
	}
	public void setRiskScore(Integer riskScore) {
		this.riskScore = riskScore;
	}
	private String type;
    private String content;
    private String risk;
    
	private Double confidence;
    private List<String> reasons;
    private Integer riskScore;
}