# 🗓️ Calendar Integration Test Guide

## 📋 Prerequisites

### 1. Setup Google Calendar API
1. **Tạo Google Cloud Project**: https://console.cloud.google.com/
2. **Enable Google Calendar API**
3. **Tạo OAuth2 Credentials**:
   - Type: Web application
   - Authorized redirect URIs: `http://localhost:8080/api/v1/calendar/oauth/callback`
4. **Cấu hình OAuth Consent Screen**:
   - Publishing status: "Testing"
   - Add test users: Thêm email của các doctor cần test
   - Scopes: `https://www.googleapis.com/auth/calendar`, `https://www.googleapis.com/auth/calendar.events`
5. **Cập nhật application.yml**:
   ```yaml
   google:
     calendar:
       client-id: YOUR_GOOGLE_CLIENT_ID
       client-secret: YOUR_GOOGLE_CLIENT_SECRET
   ```
### 2. Start Application
```bash
mvn spring-boot:run
```

## 🔧 Detailed OAuth2 Setup

### Step 1: Google Cloud Console Setup
1. **Tạo project mới** hoặc chọn project có sẵn
2. **Enable APIs**: Google Calendar API
3. **OAuth consent screen**:
   - User Type: External
   - App name: "Care Sync System Calendar"
   - User support email: Your email
   - Developer contact information: Your email
   - Scopes: Add `https://www.googleapis.com/auth/calendar` và `https://www.googleapis.com/auth/calendar.events`
   - Test users: Add emails của các doctor cần test

### Step 2: OAuth2 Credentials
1. **Create Credentials** > **OAuth 2.0 Client IDs**
2. **Application type**: Web application
3. **Authorized redirect URIs**: `http://localhost:8080/api/v1/calendar/oauth/callback`
4. **Copy Client ID và Client Secret**

### Step 3: Add Test Users
1. **OAuth consent screen** > **Test users**
2. **Add Users**: Thêm email của các doctor
3. **Wait 5-10 phút** để Google sync
4. **Test với từng email** đã thêm

## 🧪 Test Flow

### Step 1: Doctor Login
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@example.com",
    "password": "password"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "..."
  }
}
```

### Step 2: Get Google OAuth2 Authorization URL
```bash
curl "http://localhost:8080/api/v1/calendar/oauth/google/authorize" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "success": true,
  "data": "https://accounts.google.com/o/oauth2/auth?...",
  "message": "Authorization URL generated"
}
```

### Step 3: Authorize Google Calendar
1. **Copy URL** từ response và paste vào browser
2. **Đăng nhập Google** và authorize ứng dụng
3. **Google sẽ redirect** về callback URL với code
4. **Hệ thống tự động lưu** access token

### Step 4: Check Calendar Integration Status
```bash
curl "http://localhost:8080/api/v1/calendar/integrations/me" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "calendarType": "GOOGLE",
    "isActive": true,
    "accessToken": "...",
    "refreshToken": "..."
  }
}
```

### Step 5: Patient Login
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@example.com",
    "password": "password"
  }'
```

### Step 6: Get Available Appointment Slots
```bash
curl "http://localhost:8080/api/v1/appointments/slots?doctorId=1&date=2025-01-15" \
  -H "Authorization: Bearer PATIENT_JWT_TOKEN"
```

### Step 7: Create Appointment
```bash
curl -X POST "http://localhost:8080/api/v1/patient/appointments" \
  -H "Authorization: Bearer PATIENT_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "patientId": 1,
    "serviceIds": [1],
    "notes": "Test appointment for calendar integration"
  }'
```

### Step 8: Doctor Confirm Appointment
```bash
curl -X PUT "http://localhost:8080/api/v1/doctor/appointments/1/confirm" \
  -H "Authorization: Bearer DOCTOR_JWT_TOKEN"
```

## ✅ Expected Results

### 1. Database Check
```sql
-- Check calendar integration
SELECT * FROM doctor_calendars WHERE doctor_id = 1;

-- Check appointment status
SELECT a.id, a.status, s.start_time, s.end_time 
FROM appointments a 
JOIN appointment_slots s ON a.id = s.appointment_id 
WHERE a.id = 1;
```

### 2. Google Calendar Check
1. **Mở Google Calendar**: https://calendar.google.com/
2. **Tìm event mới** với title: "✅ Lịch hẹn với [Patient Name]"
3. **Kiểm tra details**:
   - Thời gian đúng với appointment slot
   - Description có thông tin bệnh nhân
   - Status: CONFIRMED

### 3. Logs Check
```bash
# Check application logs
tail -f logs/application.log | grep -i calendar
```

**Expected Logs:**
```
INFO  - Starting Google OAuth2 flow for doctor: 1
INFO  - Successfully obtained Google OAuth2 tokens for doctor: 1
INFO  - Successfully synced appointment 1 (status: CONFIRMED) to calendar for doctor 1
INFO  - Created Google Calendar event: abc123xyz
```

## 🐛 Troubleshooting

### Issue 1: 401 Unauthorized
- **Cause**: Invalid or missing JWT token
- **Solution**: Login again to get fresh token

### Issue 2: 403 Forbidden
- **Cause**: User is not a doctor
- **Solution**: Use doctor account

### Issue 3: OAuth2 Callback Error
- **Cause**: Invalid redirect URI or client credentials
- **Solution**: Check Google Cloud Console settings

### Issue 4: 403 Access Denied (OAuth2)
- **Cause**: Email not added to test users
- **Solution**: Add email to OAuth consent screen test users

### Issue 5: Calendar Sync Failed
- **Cause**: Invalid access token or expired
- **Solution**: Re-authorize Google Calendar

## 🎯 Success Criteria

✅ Doctor can login and get OAuth2 authorization URL  
✅ Google Calendar integration is established  
✅ Patient can create appointment  
✅ Doctor can confirm appointment  
✅ Appointment appears in Google Calendar with correct details  
✅ Event title shows appointment status (✅, ❌, 🚫)  
✅ Event description contains patient information  

## 📝 Notes

- **Token Refresh**: System automatically refreshes expired tokens
- **Error Handling**: Calendar sync failures don't break appointment flow
- **Event Format**: Events include emojis to indicate status
- **Timezone**: Events use system default timezone
- **Test Users**: Only emails added to OAuth consent screen can authorize
- **Development**: Use test users for development, publish app for production 