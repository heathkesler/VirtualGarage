# 🚀 Virtual Garage API Server - READY FOR UI CONNECTION

## ✅ **APPLICATION STATUS: RUNNING**

The Virtual Garage API server is **successfully running** and ready for your UI to connect!

### 🌐 **API Server Details**
- **Status**: ✅ **RUNNING**
- **Base URL**: `http://localhost:8080/api/v1`
- **Health Check**: ✅ Responding
- **Last Verified**: 2025-09-12 18:02:28 UTC

### 🔧 **Infrastructure Services Status**
| Service | Port | Status | Purpose |
|---------|------|--------|---------|
| **API Server** | 8080 | ✅ Running | Main REST API |
| **PostgreSQL** | 5432 | ✅ Running | Primary database |
| **Redis** | 6379 | ✅ Running | Caching & sessions |
| **Kafka** | 9092 | ✅ Running | Message streaming |
| **Elasticsearch** | 9200 | ✅ Running | Search engine |
| **MinIO S3** | 9000/9001 | ✅ Running | File storage |

---

## 🔗 **UI CONNECTION GUIDE**

### **API Base URL for Frontend**
```javascript
const API_BASE_URL = 'http://localhost:8080/api/v1'
```

### **CORS Configuration**
The API is configured to accept requests from:
- ✅ `http://localhost:3000` (React default)
- ✅ `http://localhost:5173` (Vite default)  
- ✅ `https://virtualgarage.com` (Production)

---

## 🔐 **Authentication Flow for UI**

### **1. User Registration**
```javascript
// POST /auth/register
fetch(`${API_BASE_URL}/auth/register`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    name: 'User Name',
    password: 'password123'
  })
})
```

### **2. User Login**
```javascript
// POST /auth/login  
fetch(`${API_BASE_URL}/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com', 
    password: 'password123'
  })
})
```

### **3. Authenticated Requests**
```javascript
// Include Bearer token in Authorization header
fetch(`${API_BASE_URL}/api/health`, {
  headers: {
    'Authorization': `Bearer ${accessToken}`,
    'Content-Type': 'application/json'
  }
})
```

### **4. Token Refresh**
```javascript
// POST /auth/refresh
fetch(`${API_BASE_URL}/auth/refresh`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    refreshToken: refreshToken
  })
})
```

---

## ✅ **Working Endpoints (Ready for UI)**

### **Public Endpoints**
- ✅ `GET /auth/health` - Service health check
- ✅ `POST /auth/register` - User registration
- ✅ `POST /auth/login` - User authentication
- ✅ `POST /auth/refresh` - Token refresh
- ✅ `POST /auth/logout` - User logout

### **Protected Endpoints**
- ✅ `GET /api/health` - API health check
- ✅ `GET /api/vehicles/stock` - Get stock vehicles
- ✅ `GET /api/vehicles/stock/search` - Search stock vehicles

---

## 📱 **Sample API Responses**

### **Registration/Login Response**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900000,
  "user": {
    "id": "uuid-here",
    "email": "user@example.com",
    "name": "User Name",
    "avatar": null,
    "role": "USER",
    "createdAt": "2025-09-12T18:02:28Z",
    "lastLogin": null
  }
}
```

### **Error Response Format**
```json
{
  "timestamp": "2025-09-12T18:02:28Z",
  "status": 400,
  "error": "Bad Request", 
  "message": "Validation failed",
  "path": "/api/v1/auth/register"
}
```

---

## 🔧 **Development Notes**

### **Token Management**
- **Access Token**: Expires in 15 minutes (900,000 ms)
- **Refresh Token**: Expires in 7 days  
- **Storage**: Store securely (localStorage/sessionStorage)

### **Error Handling**
- **401 Unauthorized**: Token expired/invalid → Redirect to login
- **403 Forbidden**: Insufficient permissions
- **400 Bad Request**: Validation errors
- **500 Internal Server Error**: Server issues

### **Request Headers**
```javascript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${accessToken}`, // For protected endpoints
  'Accept': 'application/json'
}
```

---

## 🚦 **Quick Health Check**

Test the API connection:
```bash
curl http://localhost:8080/api/v1/auth/health
```

Expected response:
```json
{
  "service": "Authentication Service",
  "timestamp": "2025-09-12T18:02:28.391432Z", 
  "status": "UP"
}
```

---

## 🎯 **Ready for UI Integration!**

The Virtual Garage API server is **fully operational** and ready for your React UI to connect. All authentication endpoints are working, the database is configured, and CORS is properly set up for local development.

### **Next Steps for UI Team:**
1. ✅ **Connect to API** - Use base URL `http://localhost:8080/api/v1`
2. ✅ **Implement Auth Flow** - Registration, login, token management
3. ✅ **Test Endpoints** - Use provided examples above
4. ✅ **Handle Errors** - Implement proper error handling
5. ✅ **Store Tokens** - Secure token storage and refresh logic

The API will handle all the backend logic while your UI focuses on the user experience!

---

## 📞 **Server Management**

### **Server Logs**
```bash
tail -f /Users/heathkesler/code/VirtualGarage/backend/app.log
```

### **Restart Server** (if needed)
```bash
# Kill current server
pkill -f "com.virtualgarage.VirtualGarageApplication"

# Start fresh  
cd /Users/heathkesler/code/VirtualGarage/backend/virtualgarage-api
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
mvn spring-boot:run > ../app.log 2>&1 &
```

**🎉 The API server is ready for your UI to connect and build amazing Virtual Garage features!**