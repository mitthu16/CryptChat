import React from 'react';

const SecurityWarning = ({ warning, onDismiss }) => {
  if (!warning) return null;

  return (
    <div className="security-warning-overlay">
      <div className="security-warning-modal">
        <div className="warning-header">
          <span className="warning-icon">🚫</span>
          <h3>Security Block</h3>
        </div>
        
        <div className="warning-content">
          <p><strong>Message blocked by CryptChat</strong></p>
          <p>Your message was blocked to protect you and other users.</p>
          
          {warning.reason && (
            <div className="reason">
              <strong>Reason:</strong> {warning.reason}
            </div>
          )}
          
          {warning.message?.security?.threats?.map((threat, index) => (
            <div key={index} className="threat-detail">
              <strong>Threat:</strong> {threat.type}
              <br />
              <strong>Risk Level:</strong> {threat.risk}
              <br />
              <strong>Reasons:</strong> {threat.reasons.join(', ')}
            </div>
          ))}
        </div>
        
        <div className="warning-actions">
          <button onClick={onDismiss} className="dismiss-btn">
            I Understand
          </button>
        </div>
      </div>
    </div>
  );
};

export default SecurityWarning;