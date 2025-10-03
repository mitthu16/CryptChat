from flask import Flask, request, jsonify
from flask_cors import CORS
import re
import requests
from datetime import datetime

app = Flask(__name__)
CORS(app)

class AdvancedAIScanner:
    def __init__(self):
        self.phishing_domains = self.load_phishing_domains()
        self.suspicious_keywords = [
            'login', 'verify', 'password', 'account', 'security',
            'banking', 'paypal', 'urgent', 'immediately', 'confirm'
        ]
    
    def load_phishing_domains(self):
        # Load from file or database
        return [
            'fake-login.com', 'phishing-bank.com', 'secure-verify.net',
            'account-update.com', 'password-reset-now.com'
        ]
    
    def scan_message(self, content):
        """Scan chat message for multiple threat types"""
        threats = []
        
        # URL scanning
        url_threats = self.scan_urls(content)
        threats.extend(url_threats)
        
        # Pattern scanning (basic heuristics)
        pattern_threats = self.scan_patterns(content)
        threats.extend(pattern_threats)
        
        return threats
    
    def scan_urls(self, content):
        threats = []
        urls = re.findall(r'https?://[^\s]+', content)
        
        for url in urls:
            threat = self.analyze_url(url)
            if threat:
                threats.append(threat)
        
        return threats
    
    def analyze_url(self, url):
        # Domain analysis
        domain = self.extract_domain(url)
        
        if domain in self.phishing_domains:
            return {
                'type': 'malicious_domain',
                'content': url,
                'risk': 'malicious',
                'confidence': 0.95,
                'reasons': ['Known phishing domain'],
                'risk_score': 95
            }
        
        # URL structure analysis
        suspicious_factors = []
        
        # Check for IP address instead of domain
        if re.match(r'https?://\d+\.\d+\.\d+\.\d+', url):
            suspicious_factors.append('Uses IP address instead of domain')
        
        # Check for excessive subdomains
        if domain.count('.') > 2:
            suspicious_factors.append('Excessive subdomains')
        
        # Check for suspicious keywords in domain
        for keyword in self.suspicious_keywords:
            if keyword in domain.lower():
                suspicious_factors.append(f'Suspicious keyword in domain: {keyword}')
        
        # Check for homograph attacks (basic)
        if self.check_homograph(domain):
            suspicious_factors.append('Possible homograph attack detected')
        
        risk_score = min(90, len(suspicious_factors) * 25)
        
        if risk_score >= 75:
            return {
                'type': 'suspicious_url',
                'content': url,
                'risk': 'malicious',
                'confidence': risk_score / 100,
                'reasons': suspicious_factors,
                'risk_score': risk_score
            }
        elif risk_score >= 40:
            return {
                'type': 'suspicious_url',
                'content': url,
                'risk': 'suspicious',
                'confidence': risk_score / 100,
                'reasons': suspicious_factors,
                'risk_score': risk_score
            }
        
        return None
    
    def scan_patterns(self, content):
        threats = []
        
        # Check for credential requests
        credential_patterns = [
            r'password\s*[\:\=]',
            r'login\s*details',
            r'credit\s*card',
            r'social\s*security'
        ]
        
        for pattern in credential_patterns:
            if re.search(pattern, content, re.IGNORECASE):
                threats.append({
                    'type': 'credential_harvesting',
                    'content': content,
                    'risk': 'suspicious',
                    'confidence': 0.7,
                    'reasons': ['Possible credential harvesting attempt'],
                    'risk_score': 70
                })
                break
        
        return threats
    
    def extract_domain(self, url):
        """Extract domain from URL"""
        domain = re.sub(r'https?://', '', url)
        domain = re.sub(r'/.*$', '', domain)
        return domain
    
    def check_homograph(self, domain):
        """Basic homograph attack detection"""
        suspicious_chars = ['1', '0', 'l', 'I']  # Can be expanded
        for char in suspicious_chars:
            if char in domain:
                return True
        return False

scanner = AdvancedAIScanner()

@app.route('/api/analyze', methods=['POST'])
def analyze_message():
    data = request.json
    content = data.get('content', '')
    
    if not content:
        return jsonify({'error': 'Content is required'}), 400
    
    try:
        threats = scanner.scan_message(content)
        return jsonify({
            'threats': threats,
            'scan_time': datetime.utcnow().isoformat(),
            'content_preview': content[:100] + '...' if len(content) > 100 else content
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'healthy', 
        'service': 'CryptChat AI Scanner',
        'version': '1.0'
    })

if __name__ == '__main__':
    app.run(debug=True, port=5000, host='0.0.0.0')