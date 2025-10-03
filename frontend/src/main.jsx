import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';

function init() {
  const rootElement = document.getElementById('app');
  
  if (!rootElement) {
    console.error('❌ ERROR: Cannot find element with id "app"');
    return;
  }
  
  console.log('✅ Found app element, rendering React app...');
  
  const root = ReactDOM.createRoot(rootElement);
  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init);
} else {
  init();
}