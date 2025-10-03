import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class SocketService {
  constructor() {
    this.client = null;
    this.isConnected = false;
    this.subscriptions = new Map();
  }

  connect(onConnect, onError) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        console.log('STOMP:', str);
      }
    });

    this.client.onConnect = (frame) => {
      console.log('Connected to Spring Boot WebSocket:', frame);
      this.isConnected = true;
      if (onConnect) onConnect(frame);
    };

    this.client.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
      if (onError) onError(frame);
    };

    this.client.onWebSocketClose = (event) => {
      console.log('WebSocket closed:', event);
      this.isConnected = false;
    };

    this.client.activate();
  }

  joinRoom(room, username, onMessage) {
    if (!this.client || !this.isConnected) {
      console.error('Not connected to WebSocket');
      return;
    }

    // Send join request
    this.client.publish({
      destination: `/app/chat.join`,
      body: JSON.stringify({ username, room })
    });

    // Subscribe to room messages
    const subscription = this.client.subscribe(`/topic/room.${room}`, (message) => {
      const parsedMessage = JSON.parse(message.body);
      console.log('Received message:', parsedMessage);
      if (onMessage) onMessage(parsedMessage);
    });

    this.subscriptions.set(room, subscription);
    console.log(`Joined room: ${room} as ${username}`);
  }

  sendMessage(room, content) {
    if (!this.client || !this.isConnected) {
      console.error('Not connected to WebSocket');
      return false;
    }

    try {
      this.client.publish({
        destination: `/app/chat.send`,
        body: JSON.stringify({ 
          content, 
          type: 'TEXT',
          room 
        })
      });
      console.log('Message sent:', content);
      return true;
    } catch (error) {
      console.error('Error sending message:', error);
      return false;
    }
  }

  leaveRoom(room) {
    const subscription = this.subscriptions.get(room);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(room);
    }
    
    if (this.client && this.isConnected) {
      this.client.publish({
        destination: `/app/chat.leave`
      });
    }
  }

  disconnect() {
    if (this.client) {
      this.subscriptions.forEach((subscription, room) => {
        subscription.unsubscribe();
      });
      this.subscriptions.clear();
      this.client.deactivate();
      this.isConnected = false;
      console.log('Disconnected from WebSocket');
    }
  }

  getConnectionStatus() {
    return this.isConnected;
  }
}

export default new SocketService();