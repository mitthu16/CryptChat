import React from 'react';
import SecurityBadge from '../Security/SecurityBadge';
import './Message.css';

const Message = ({ message }) => {
  const isSystem = message.type === 'system';
  const isBlocked = message.security?.blocked;

  if (isSystem) {
    return (
      <div className="message system-message">
        <span className="system-text">{message.message}</span>
      </div>
    );
  }

  return (
    <div className={`message ${isBlocked ? 'blocked' : ''}`}>
      <div className="message-header">
        <span className="username">{message.username}</span>
        <span className="timestamp">
          {new Date(message.timestamp).toLocaleTimeString()}
        </span>
        <SecurityBadge security={message.security} />
      </div>
      
      <div className="message-content">
        {formatMessageContent(message.content, message.security)}
      </div>

      {message.security?.threats?.map((threat, index) => (
        <div key={index} className="security-warning">
          <strong>⚠️ Security Alert:</strong> {threat.reasons.join(', ')}
        </div>
      ))}
    </div>
  );
};

const formatMessageContent = (content, security) => {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  
  return content.split(urlRegex).map((part, index) => {
    if (part.match(urlRegex)) {
      const isMalicious = security?.threats?.some(t => t.content === part && t.risk === 'malicious');
      const isSuspicious = security?.threats?.some(t => t.content === part && t.risk === 'suspicious');
      
      let className = 'url-link';
      if (isMalicious) className += ' malicious-url';
      else if (isSuspicious) className += ' suspicious-url';
      
      return (
        <a 
          key={index}
          href={isMalicious ? '#' : part}
          className={className}
          onClick={isMalicious ? (e) => e.preventDefault() : null}
          target="_blank"
          rel="noopener noreferrer"
        >
          {part}
        </a>
      );
    }
    return part;
  });
};

export default Message;