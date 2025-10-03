import React from 'react';

function App() {
  return (
    <div style={{
      padding: '50px',
      background: 'linear-gradient(135deg, #667eea, #764ba2)',
      color: 'white',
      height: '100vh',
      textAlign: 'center',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      fontSize: '24px'
    }}>
      <h1>🔒 CryptChat</h1>
      <p>Secure AI-Powered Chat Platform</p>
      <div style={{
        background: 'white',
        color: 'black',
        padding: '30px',
        borderRadius: '15px',
        marginTop: '30px',
        boxShadow: '0 10px 30px rgba(0,0,0,0.3)'
      }}>
        <p style={{ fontSize: '18px', marginBottom: '20px' }}>
          ✅ React is working correctly!
        </p>
        <button 
          onClick={() => alert('CryptChat is ready!')}
          style={{
            padding: '12px 24px',
            fontSize: '16px',
            background: '#3498db',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer'
          }}
        >
          Test Button
        </button>
      </div>
    </div>
  );
}

export default App;