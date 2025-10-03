import React from 'react';

const SecurityBadge = ({ security }) => {
  if (!security) return null;

  const getBadgeInfo = (status) => {
    switch (status) {
      case 'safe':
        return { icon: '✅', text: 'Safe', color: 'green' };
      case 'scanning':
        return { icon: '🔍', text: 'Scanning...', color: 'blue' };
      case 'threat_detected':
        return { icon: '⚠️', text: 'Threat Detected', color: 'orange' };
      case 'blocked':
        return { icon: '🚫', text: 'Blocked', color: 'red' };
      default:
        return { icon: '❓', text: 'Unknown', color: 'gray' };
    }
  };

  const badge = getBadgeInfo(security.status);

  return (
    <span className={`security-badge ${badge.color}`}>
      {badge.icon} {badge.text}
    </span>
  );
};

export default SecurityBadge;