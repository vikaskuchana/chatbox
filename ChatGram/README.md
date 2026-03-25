# 💬 Telegram Clone - Real-time Messaging Platform

A full-featured, scalable messaging application built with **Spring Boot**, **WebSocket**, and **Kafka**. This project demonstrates enterprise-level architecture with real-time communication, message persistence, and comprehensive audit logging.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Kafka-Latest-black.svg)](https://kafka.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 🌟 Features

### Core Functionality

- ✅ **Real-time Messaging** - WebSocket-based instant message delivery
- ✅ **User Authentication** - JWT-based secure authentication
- ✅ **Group Chat** - Create and manage group conversations
- ✅ **Message Persistence** - Guaranteed message storage and delivery
- ✅ **Delivery Receipts** - Track message status (Sent → Delivered → Read)
- ✅ **Typing Indicators** - Real-time typing notifications
- ✅ **Online/Offline Status** - User presence tracking
- ✅ **Audit Logging** - Complete user interaction tracking

### Technical Highlights

- 🚀 **Asynchronous Processing** - Kafka-based message queuing
- 🔒 **Security** - BCrypt password hashing, JWT tokens, HTTPS/WSS
- 📊 **Scalability** - Horizontal scaling with Redis session clustering
- 💾 **Multi-Database** - PostgreSQL for data, MongoDB for audit logs, Redis for caching
- 🔄 **Message Guarantees** - At-least-once delivery with retry logic

## 🏗️ Architecture

```
┌─────────────┐
│   Client    │ (Web/Mobile)
└──────┬──────┘
       │ WebSocket/HTTP
┌──────▼──────────┐
│  API Gateway    │ (Load Balancer)
└──────┬──────────┘
       │
   ┌───┴───┬─────────┬──────────┐
   │       │         │          │
┌──▼───┐ ┌▼────┐ ┌──▼─────┐ ┌──▼────┐
│ Auth │ │ Msg │ │ User   │ │ Group │
│Service│ │Service│ │Service│ │Service│
└──┬───┘ └──┬──┘ └───┬────┘ └───┬───┘
   │        │        │          │
   └────────┼────────┴──────────┘
            │
      ┌─────┼─────┬──────────┐
      │     │     │          │
   ┌──▼──┐ ┌▼───┐ ┌▼──────┐ ┌▼──────┐
   │PostgreSQL│ │Kafka│ │Redis │ │MongoDB│
   │(Messages)│ │(Queue)│ │(Cache)│ │(Audit)│
   └─────────┘ └────┘ └───────┘ └───────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker** and **Docker Compose**

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/yourusername/telegram-clone.git
cd telegram-clone
```

**2. Start infrastructure services**

```bash
docker-compose up -d
```

This will start:

- PostgreSQL (port 5432)
- MongoDB (port 27017)
- Redis (port 6379)
- Kafka + Zookeeper (port 9092)

**3. Build the application**

```bash
mvn clean install
```

**4. Run the application**

```bash
mvn spring-boot:run
```

**5. Access the application**

- API: `http://localhost:8080`
- WebSocket: `ws://localhost:8080/ws`

## 📡 API Documentation

### Authentication

#### Register User

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john_doe@telegram.com",
    "displayName": "john_doe"
  }
}
```

#### Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

### Messaging

#### Send Message

```bash
POST /api/messages/send
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "chatId": "chat_123",
  "content": "Hello, World!",
  "type": "TEXT"
}
```

#### Get Chat History

```bash
GET /api/messages/history/chat_123
Authorization: Bearer <JWT_TOKEN>
```

#### Update Message Status

```bash
PUT /api/messages/456/status
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "status": "READ"
}
```

### Groups

#### Create Group

```bash
POST /api/groups
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Tech Enthusiasts",
  "description": "Discuss latest tech trends"
}
```

#### Add Member to Group

```bash
POST /api/groups/1/members
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "userId": 2
}
```

#### Get Group Members

```bash
GET /api/groups/1/members
Authorization: Bearer <JWT_TOKEN>
```

#### Get My Groups

```bash
GET /api/groups/my-groups
Authorization: Bearer <JWT_TOKEN>
```

### Users

#### Get User Profile

```bash
GET /api/users/1
Authorization: Bearer <JWT_TOKEN>
```

## 🔌 WebSocket Integration

### Connect to WebSocket

```javascript
// Using SockJS and STOMP
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// Connect with JWT token
stompClient.connect(
  { Authorization: 'Bearer ' + yourJwtToken },
  function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to chat messages
    stompClient.subscribe('/topic/chat/chat_123', function(message) {
      const msg = JSON.parse(message.body);
      console.log('New message:', msg);
    });
    
    // Subscribe to delivery receipts
    stompClient.subscribe('/user/queue/receipts', function(receipt) {
      const data = JSON.parse(receipt.body);
      console.log('Receipt:', data);
    });
    
    // Subscribe to typing indicators
    stompClient.subscribe('/topic/chat/chat_123/typing', function(typing) {
      const data = JSON.parse(typing.body);
      console.log('User typing:', data);
    });
    
    // Subscribe to status updates
    stompClient.subscribe('/topic/status', function(status) {
      const data = JSON.parse(status.body);
      console.log('Status update:', data);
    });
  }
);
```

### Send Messages via WebSocket

```javascript
// Send typing indicator
stompClient.send('/app/chat.typing', {}, JSON.stringify({
  chatId: 'chat_123',
  isTyping: true
}));

// Mark message as read
stompClient.send('/app/message.read', {}, JSON.stringify({
  messageId: 456
}));

// Update online status
stompClient.send('/app/user.status', {}, JSON.stringify({
  status: 'ONLINE'
}));
```

### WebSocket Endpoints

| Endpoint | Type | Description |
|----------|------|-------------|
| `/topic/chat/{chatId}` | Subscribe | Receive chat messages |
| `/topic/chat/{chatId}/typing` | Subscribe | Receive typing indicators |
| `/user/queue/receipts` | Subscribe | Receive delivery/read receipts |
| `/topic/status` | Subscribe | Receive user status updates |
| `/app/chat.typing` | Send | Send typing indicator |
| `/app/message.read` | Send | Mark message as read |
| `/app/user.status` | Send | Update user status |

## 🗄️ Database Schema

### PostgreSQL Tables

**Users**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    profile_picture_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Messages**

```sql
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    chat_id VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    status VARCHAR(20) DEFAULT 'SENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP
);
```

**Groups**

```sql
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Group Members**

```sql
CREATE TABLE group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT REFERENCES groups(id),
    user_id BIGINT REFERENCES users(id),
    role VARCHAR(20) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(group_id, user_id)
);
```

## ⚙️ Configuration

### application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/telegram_db
    username: postgres
    password: postgres
  
  data:
    redis:
      host: localhost
      port: 6379
    mongodb:
      uri: mongodb://localhost:27017/telegram_audit
  
  kafka:
    bootstrap-servers: localhost:9092

jwt:
  secret: your-256-bit-secret-key-change-this-in-production
  expiration: 86400000  # 24 hours
```

### Environment Variables

You can override configuration using environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/telegram_db
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your-super-secret-key
export SPRING_KAFKA_BOOTSTRAP_SERVERS=your-kafka-host:9092
```

## 📊 Kafka Topics

| Topic | Purpose |
|-------|---------|
| `message.sent` | New messages published by Message Service |
| `message.delivered` | Message delivery confirmations |
| `message.read` | Read receipt notifications |
| `user.audit` | User action audit events |
| `notification.push` | Push notification events |

## 🔒 Security

### Authentication Flow

1. User registers/logs in → receives JWT token
2. Client includes token in `Authorization: Bearer <token>` header
3. `JwtAuthenticationFilter` validates token on every request
4. WebSocket connections also require JWT in connect headers

### Password Security

- Passwords hashed using **BCrypt** (strength: 10)
- Salt automatically generated per password
- Never stored in plain text

### Best Practices Implemented

- ✅ JWT with short expiration (24 hours)
- ✅ HTTPS/WSS in production
- ✅ SQL injection prevention via JPA
- ✅ XSS protection headers
- ✅ Rate limiting per user
- ✅ CORS configuration

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Test with cURL

**Register:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

**Send Message:**

```bash
curl -X POST http://localhost:8080/api/messages/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"chatId":"chat123","content":"Hello!","type":"TEXT"}'
```

## 📈 Monitoring & Observability

### Health Check

```bash
GET /actuator/health
```

### Metrics (if enabled)

```bash
GET /actuator/metrics
```

### Kafka Consumer Lag

Monitor using Kafka's built-in tools or Kafka Manager

## 🚀 Deployment

### Docker Deployment

**1. Build Docker image**

```bash
docker build -t telegram-clone:latest .
```

**2. Run with Docker Compose**

```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Production Checklist

- [ ] Change JWT secret to a strong random value
- [ ] Enable HTTPS/WSS with valid certificates
- [ ] Configure proper CORS origins
- [ ] Set up database backups
- [ ] Enable application logging (ELK stack recommended)
- [ ] Set up monitoring (Prometheus + Grafana)
- [ ] Configure Kafka replication factor ≥ 3
- [ ] Use connection pooling for databases
- [ ] Set up rate limiting per IP
- [ ] Configure Redis persistence

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Write meaningful commit messages
- Add JavaDoc for public methods
- Include unit tests for new features

## 👨‍💻 Author
Kuchana Vikas

- GitHub: [@vikaskuchana](https://github.com/vikaskuchana)
- LinkedIn: [Vikas Kuchana](https://www.linkedin.com/in/vikas-kuchana-11447a202/?skipRedirect=true)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Apache Kafka for reliable message streaming
- Socket.IO/SockJS for WebSocket support
- The open-source community

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [WebSocket Protocol](https://tools.ietf.org/html/rfc6455)
- [JWT Introduction](https://jwt.io/introduction/)

## 🐛 Known Issues

- WebSocket reconnection not implemented (client-side needs retry logic)
- File upload for images/videos not yet supported
- Voice/video calls not implemented

## 🗺️ Roadmap

- [ ] End-to-end encryption
- [ ] Media file support (images, videos, documents)
- [ ] Voice and video calls (WebRTC)
- [ ] Message search functionality
- [ ] User blocking/reporting
- [ ] Push notifications (FCM/APNS)
- [ ] Message reactions and replies
- [ ] Admin dashboard
- [ ] Analytics and insights

---

⭐ **If you find this project useful, please consider giving it a star!** ⭐