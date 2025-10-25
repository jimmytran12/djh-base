# Base Project

A Spring Boot-based application template with JWT authentication, security, and common utilities for rapid development of enterprise applications.

## 🚀 Features

- **Spring Boot 3.0.7** with Java 17
- **Spring Security** with JWT authentication
- **JPA** and **Hibernate** for database operations
- **MapStruct** for object mapping
- **Lombok** for reduced boilerplate code
- **Thymeleaf** template engine
- **MySQL** database support
- **Email** functionality with Spring Mail
- **JWT** (JSON Web Token) for stateless authentication
- **Excel** file handling with Apache POI
- **Gson** for JSON processing

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6.3 or later
- MySQL 8.0 or later
- Your favorite IDE (IntelliJ IDEA, Eclipse, etc.)

## 🛠️ Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd base
   ```

2. Configure your database settings in `application.properties`

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

   Or run the main class `BaseApplication` from your IDE.

## 🏗️ Project Structure

```
src/main/java/com/djh/base/
├── BaseApplication.java     # Main application class
├── common/                     # Common utilities and base classes
├── entity/                     # JPA entities
├── mapper/                     # MapStruct mappers
├── repository/                 # JPA repositories
└── security/                   # Security configuration and components
```

## 🔒 Security

The application uses JWT (JSON Web Tokens) for stateless authentication. Key security components include:

- `TokenAuthenticationFilter` - Validates JWT tokens
- `RestAuthenticationEntryPoint` - Handles authentication exceptions
- Custom user details service and authentication providers

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📫 Contact

Project Link: [https://github.com/thinh1407/base](https://github.com/thinh1407/base)