import React, { useState, useEffect, useRef } from 'react';
import { io } from 'socket.io-client';
import Message from '../Message/Message';
import SecurityWarning from '../Security/SecurityWarning';
import './ChatRoom.css';

const ChatRoom = () => {
  const [socket, setSocket] = useState(null);
  const [messages, setMessages] = useState([]);
  const [messageInput, setMessageInput] = useState('');
  const [username, setUsername] = useState('');
  const [room, setRoom] = useState('general');
  const [joined, setJoined] = useState(false);
  const [securityWarning, setSecurityWarning] = useState(null);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    if (joined) {
      const newSocket = io('http://localhost:5001');
      setSocket(newSocket);

      newSocket.emit('join_room', { username, room });

      newSocket.on('room_history', (history) => {
        setMessages(history);
      });

      newSocket.on('new_message', (message) => {
        setMessages(prev => [...prev, message]);
      });

      newSocket.on('message_blocked', (data) => {
        setSecurityWarning({
          type: 'blocked',
          message: data.message,
          reason: data.reason
        });
      });

      newSocket.on('user_joined', (data) => {
        setMessages(prev => [...prev, { ...data, type: 'system' }]);
      });

      newSocket.on('user_left', (data) => {
        setMessages(prev => [...prev, { ...data, type: 'system' }]);
      });

      return () => newSocket.close();
    }
  }, [joined, username, room]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleJoin = (e) => {
    e.preventDefault();
    if (username.trim()) {
      setJoined(true);
    }
  };

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (messageInput.trim() && socket) {
      socket.emit('send_message', {
        content: messageInput.trim(),
        type: 'text'
      });
      setMessageInput('');
    }
  };

  const handleDismissWarning = () => {
    setSecurityWarning(null);
  };

  if (!joined) {
    return (
      <div className="login-container">
        <div className="login-form">
          <h1>🔒 CryptChat</h1>
          <p>Secure AI-Powered Chat Platform</p>
          <form onSubmit={handleJoin}>
            <input
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <select value={room} onChange={(e) => setRoom(e.target.value)}>
              <option value="general">General</option>
              <option value="security">Security Team</option>
              <option value="random">Random</option>
            </select>
            <button type="submit">Join Chat</button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="chat-container">
      {securityWarning && (
        <SecurityWarning 
          warning={securityWarning}
          onDismiss={handleDismissWarning}
        />
      )}
      
      <div className="chat-header">
        <h2>🔒 CryptChat - {room}</h2>
        <div className="user-info">Welcome, {username}!</div>
      </div>

      <div className="messages-container">
        {messages.map((message) => (
          <Message key={message.id} message={message} />
        ))}
        <div ref={messagesEndRef} />
      </div>

      <form className="message-form" onSubmit={handleSendMessage}>
        <input
          type="text"
          placeholder="Type your message..."
          value={messageInput}
          onChange={(e) => setMessageInput(e.target.value)}
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
};

export default ChatRoom;