# Backend

Hệ thống backend cho ứng dụng đặt lịch khám bệnh, được xây dựng bằng Java Spring Boot.

## Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database ORM
- **MySQL 8.0+** - Production database
- **JWT** - Token-based authentication
- **MapStruct** - DTO mapping
- **Lombok** - Code generation
- **Swagger/OpenAPI** - API documentation

## Cấu trúc dự án

```
src/main/java/com/backend/
├── config/          # Configuration classes
├── controller/      # REST Controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA Entities
├── exception/      # Custom exceptions
├── mapper/         # MapStruct mappers
├── repository/     # JPA Repositories
├── service/        # Business logic
├── security/       # Security configuration
└── util/           # Utility classes
```

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (cho production)

## API Documentation

Sau khi chạy ứng dụng, truy cập:
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API Docs: http://localhost:8080/api/api-docs

## Database Schema

### Các entity chính:
- **User** - Thông tin người dùng
- **Role** - Vai trò người dùng
- **Doctor** - Thông tin bác sĩ
- **Specialty** - Chuyên khoa
- **Appointment** - Lịch hẹn
- **Payment** - Thanh toán
- **Notification** - Thông báo
- **MedicalRecord** - Hồ sơ y tế
- **Feedback** - Đánh giá

## Authentication & Authorization

Hệ thống sử dụng JWT token cho authentication:

### Đăng ký
```bash
POST /api/auth/register
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+84901234567"
}
```

### Đăng nhập
```bash
POST /api/auth/login
{
  "username": "john_doe",
  "password": "password123"
}
```

### Sử dụng token
```bash
Authorization: Bearer <jwt_token>
```

## Roles và Permissions

- **PATIENT** - Bệnh nhân: Đặt lịch, xem lịch sử, đánh giá
- **DOCTOR** - Bác sĩ: Quản lý lịch làm việc, xác nhận lịch hẹn
- **ADMIN** - Quản trị viên: Quản lý toàn bộ hệ thống
- **RECEPTIONIST** - Nhân viên tiếp nhận: Hỗ trợ bệnh nhân

## Configuration

### Application Properties
Cấu hình chính trong `application.yml`:
- Database connection
- JWT settings
- Mail settings

### Environment Variables
```bash
JWT_SECRET=your-secret-key
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## Testing

```bash
# Chạy unit tests
mvn test

# Chạy integration tests
mvn verify
```

## Monitoring

- Health check: http://localhost:8080/api/actuator/health
- Metrics: http://localhost:8080/api/actuator/metrics
- Info: http://localhost:8080/api/actuator/info

