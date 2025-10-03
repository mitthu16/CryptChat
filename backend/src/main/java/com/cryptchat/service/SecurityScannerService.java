package com.cryptchat.service;

import com.cryptchat.model.SecurityScanResult;
import com.cryptchat.model.SecurityThreat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SecurityScannerService {

    @Value("${ai.scanner.url}")
    private String aiScannerUrl;

    private final WebClient webClient;
    private final Pattern urlPattern = Pattern.compile("https?://[^\\s]+");

    // Known malicious domains
    private final List<String> maliciousDomains = List.of(
        "fake-login.com", "phishing-bank.com", "secure-verify.net",
        "account-update.com", "password-reset-now.com"
    );

    public SecurityScannerService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(aiScannerUrl).build();
    }

    public SecurityScanResult scanMessage(String content) {
        List<SecurityThreat> threats = new ArrayList<>();
        
        try {
            // Scan for URLs
            var urlThreats = scanUrls(content);
            threats.addAll(urlThreats);
            
            // Scan for patterns
            var patternThreats = scanPatterns(content);
            threats.addAll(patternThreats);
            
            SecurityScanResult result = new SecurityScanResult();
            result.setScanTime(LocalDateTime.now());
            
            if (!threats.isEmpty()) {
                boolean hasMalicious = threats.stream()
                    .anyMatch(threat -> "malicious".equals(threat.getRisk()));
                
                result.setStatus(hasMalicious ? 
                    SecurityScanResult.ScanStatus.BLOCKED : 
                    SecurityScanResult.ScanStatus.THREAT_DETECTED);
                result.setThreats(threats);
                result.setBlocked(hasMalicious);
            } else {
                result.setStatus(SecurityScanResult.ScanStatus.SAFE);
                result.setBlocked(false);
            }
            
            return result;
            
        } catch (Exception e) {
            SecurityScanResult errorResult = new SecurityScanResult();
            errorResult.setStatus(SecurityScanResult.ScanStatus.ERROR);
            errorResult.setScanTime(LocalDateTime.now());
            errorResult.setBlocked(false);
            return errorResult;
        }
    }

    private List<SecurityThreat> scanUrls(String content) {
        List<SecurityThreat> threats = new ArrayList<>();
        var matcher = urlPattern.matcher(content);
        
        while (matcher.find()) {
            String url = matcher.group();
            SecurityThreat threat = analyzeUrl(url);
            if (threat != null) {
                threats.add(threat);
            }
        }
        
        return threats;
    }

    private SecurityThreat analyzeUrl(String url) {
        // First check against known malicious domains
        for (String domain : maliciousDomains) {
            if (url.contains(domain)) {
                SecurityThreat threat = new SecurityThreat();
                threat.setType("malicious_domain");
                threat.setContent(url);
                threat.setRisk("malicious");
                threat.setConfidence(0.95);
                threat.setReasons(List.of("Known phishing domain"));
                threat.setRiskScore(95);
                return threat;
            }
        }
        
        // Use AI scanner for advanced analysis
        try {
            return webClient.post()
                .uri("")
                .bodyValue("{\"content\": \"" + url + "\"}")
                .retrieve()
                .bodyToMono(SecurityThreat.class)
                .block();
        } catch (Exception e) {
            // Fallback to basic analysis if AI service is unavailable
            return performBasicUrlAnalysis(url);
        }
    }

    private SecurityThreat performBasicUrlAnalysis(String url) {
        List<String> reasons = new ArrayList<>();
        
        // Check for IP address
        if (url.matches("https?://\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            reasons.add("Uses IP address instead of domain");
        }
        
        // Check for excessive subdomains
        String domain = extractDomain(url);
        if (domain.chars().filter(ch -> ch == '.').count() > 2) {
            reasons.add("Excessive subdomains");
        }
        
        // Check for suspicious keywords
        List<String> suspiciousKeywords = List.of("login", "verify", "password", "account", "bank");
        for (String keyword : suspiciousKeywords) {
            if (domain.contains(keyword)) {
                reasons.add("Suspicious keyword in domain: " + keyword);
                break;
            }
        }
        
        if (!reasons.isEmpty()) {
            int riskScore = Math.min(80, reasons.size() * 20);
            SecurityThreat threat = new SecurityThreat();
            threat.setType("suspicious_url");
            threat.setContent(url);
            threat.setRisk(riskScore >= 60 ? "malicious" : "suspicious");
            threat.setConfidence(riskScore / 100.0);
            threat.setReasons(reasons);
            threat.setRiskScore(riskScore);
            return threat;
        }
        
        return null;
    }

    private List<SecurityThreat> scanPatterns(String content) {
        List<SecurityThreat> threats = new ArrayList<>();
        String lowerContent = content.toLowerCase();
        
        // Check for credential harvesting patterns
        if (lowerContent.matches(".*(password|login|credit card|social security).*")) {
            SecurityThreat threat = new SecurityThreat();
            threat.setType("credential_harvesting");
            threat.setContent(content);
            threat.setRisk("suspicious");
            threat.setConfidence(0.7);
            threat.setReasons(List.of("Possible credential harvesting attempt"));
            threat.setRiskScore(70);
            threats.add(threat);
        }
        
        return threats;
    }

    private String extractDomain(String url) {
        return url.replaceAll("https?://", "")
                 .replaceAll("/.*", "");
    }
}