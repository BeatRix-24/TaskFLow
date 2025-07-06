# TaskFlow 

A secure, full-featured task management application built with Java 21 and Spring Boot. It supports user registration with email verification, JWT-based authentication, password reset via email OTP, session management, and CRUD operations for tasks.

---

## Features
- User registration with email verification (OTP)
- JWT authentication (access & refresh tokens)
- Password reset via email OTP
- Session management (view, refresh, revoke sessions)
- Task management (create, update, mark done, delete, list)
- PostgreSQL for data storage
- Redis for OTP/session management
- Email notifications via Mailtrap
- OpenAPI/Swagger UI for API documentation

---

## Tech Stack
- Java 21
- Spring Boot 3.5
- PostgreSQL
- Redis
- Mailtrap (SMTP)
- Maven
- Lombok
- Spring Security
- springdoc-openapi (Swagger UI)

---

## Getting Started

### Prerequisites
- Java 21+
- Maven
- PostgreSQL
- Redis

### Configuration

Set these variables:

```
# JWT
JWT_SECRET=your_jwt_secret

# DB
DB_URL=jdbc:postgresql://localhost:5432/todo_db
DB_USERNAME=todo_user
DB_PASSWORD=your_db_password

# Mailtrap (SMTP)
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=your_mailtrap_username
MAIL_PASSWORD=your_mailtrap_password
```

The application reads these from `application.yml` using `${...}` placeholders.

### Database
- Create a PostgreSQL database and user matching your `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
- Redis should be running locally (default settings).

### Running the Application

```
mvn clean install
mvn spring-boot:run
```

The app will start on `http://localhost:8080` by default.

---

## API Endpoints

### Authentication (`/api/auth`)

- **POST `/sign-up`**
  - Register a new user. Request body: `{ firstname, lastname, email, password }`
  - Response: `User registered successfully. Check your email for the verification code`

- **POST `/verify-email`**
  - Verify email with OTP. Request body: `{ email, otpCode }`
  - Response: `{ accessToken, refreshToken }`

- **POST `/login`**
  - Login with email and password. Request body: `{ email, password }`
  - Response: `{ accessToken, refreshToken }`

### Password Reset (`/api/auth`)

- **POST `/forgot-password`**
  - Request password reset OTP. Request body: `{ email }`
  - Response: `Reset email sent successfully`

- **POST `/reset-password`**
  - Reset password with OTP. Request body: `{ email, token, newPassword }`
  - Response: `Password reset successful`

### Session Management (`/api/auth`)

- **GET `/sessions`**
  - List active sessions for the current user. Requires authentication.
  - Response: List of `{ id, deviceInfo, ipAddress, createdAt, expiresAt }`

- **POST `/refresh`**
  - Refresh access and refresh tokens. Request body: `{ refreshToken }`
  - Response: `{ accessToken, refreshToken }`

- **POST `/logout`**
  - Revoke a refresh token (logout from a session). Request body: `{ refreshToken }`
  - Response: `204 No Content`

### Task Management (`/api/tasks`)

- **POST `/create-task`**
  - Create a new task. Request body: `{ task }`
  - Response: `{ id, task, done, createdAt }`

- **GET `/my-tasks`**
  - List all tasks for the current user. Response: List of `{ id, task, done, createdAt }`

- **POST `/{taskId}/mark-done`**
  - Mark a task as done. Response: Updated task object.

- **PUT `/update-task/{taskId}`**
  - Update a task. Request body: `{ task?, done? }`
  - Response: Updated task object.

- **DELETE `/delete-task/{taskId}`**
  - Delete a task. Response: `Task deleted successfully`

---

## API Documentation

- After running the app, access Swagger UI at: `http://localhost:8080/swagger-ui.html` or `/swagger-ui/index.html`

---

## Notes
- Ensure your SMTP and database credentials are correct.
- OTPs for verification and password reset are valid for 2 minutes.
- JWT access tokens expire in 1 hour; refresh tokens in 7 days.
