# API Automation with REST Assured & TestNG

A modular, data-driven API Test Automation framework built with **Java**, **REST Assured**, **TestNG**, and **Maven** to test RESTful APIs.

---

## 📌 Features

- **Modular Architecture**: Clean separation of Endpoints, Spec Builders, POJOs, Utilities, and Tests.
- **Dynamic Test Data Generation**: Leverages [JavaFaker](https://github.com/DiUS/java-faker) for realistic, randomized user data.
- **JSON Schema Validation**: Validates API response structures and contracts using REST Assured's JSON Schema Validator.
- **Request & Response Specification Builders**: Reusable configurations for base URIs, common headers, authentication tokens, and expected status codes.
- **Data Persistence**: Automatically stores session/auth tokens into properties files (`config.properties`) for downstream dependent tests.
- **Configurable Environments**: Supports overriding configurations via environment variables (`BASE_URL`, `BASE_URI`, `AUTH_TOKEN`, `API_TOKEN`, etc.).

---

## 🎯 Target API

This framework automates endpoints from the **Contact List App**:
- **Application URL**: [Thinking Tester Contact List App](https://thinking-tester-contact-list.herokuapp.com)
- **API Documentation**: [Postman Documenter](https://documenter.getpostman.com/view/4012288/TzK2bEa8#intro)

### Automated Endpoints
| Feature | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Add User** | `POST` | `/users` | Registers a new random user and verifies creation response schema |
| **User Login** | `POST` | `/users/login` | Authenticates user credentials and captures auth token |
| **Add Contact** | `POST` | `/contacts` | Creates a new contact for the authenticated user |
| **Delete Contact** | `DELETE` | `/contacts/{id}` | Deletes a contact record by ID |

---

## 📁 Project Structure

```text
API-RestAssured-TestNG-Automation
├── pom.xml                               # Maven project dependencies & build configuration
├── testng_suite.xml                      # TestNG test suite execution runner
└── src
    └── test
        ├── java
        │   └── ApiTest
        │       ├── POJO                  # Request/Response POJO classes (e.g., userLogin)
        │       ├── SpecBuilders          # Reusable Request & Response Specifications
        │       ├── Test                  # TestNG test classes (UserTest, ContactTest)
        │       ├── Utils                 # Data factories, environment helpers, properties managers
        │       └── endpoints             # API route constants and base URLs
        └── resources
            ├── config.properties         # Runtime configurations and stored tokens
            └── schemas                   # JSON schema definitions for response contract validation
```

---

## 🛠️ Prerequisites

- **Java JDK**: 22 or higher (or compatible JDK 17+)
- **Apache Maven**: 3.8+
- **Git**: Installed and configured

---

## 📦 Key Dependencies

| Dependency | Purpose |
| :--- | :--- |
| **[REST Assured](https://rest-assured.io/)** | Fluent API testing and HTTP request handling |
| **[TestNG](https://testng.org/)** | Test runner, assertions, test dependencies, and lifecycle annotations |
| **[json-schema-validator](https://github.com/rest-assured/rest-assured)** | JSON Schema validation |
| **[JavaFaker](https://github.com/DiUS/java-faker)** | Realistic mock and random test data generation |
| **[Jackson Databind](https://github.com/FasterXML/jackson-databind)** | Object mapping and JSON serialization/deserialization |
| **[ExtentReports](https://extentreports.com/)** | Rich HTML reporting capabilities |
| **[Apache POI](https://poi.apache.org/)** | Excel data reading/writing utilities |

---

## 🚀 How to Run the Tests

### 1. Clone the Repository
```bash
git clone https://github.com/ShawonBarua/API-RestAssured-TestNG-Automation.git
cd API-RestAssured-TestNG-Automation
```

### 2. Run with Maven (CLI)

- **Execute the entire TestNG suite:**
  ```bash
  mvn clean test
  ```

- **Run specific test classes:**
  ```bash
  mvn test -Dtest=UserTest
  mvn test -Dtest=ContactTest
  ```

- **Run using custom Base URL / Environment Variables:**
  ```bash
  # PowerShell
  $env:BASE_URL="https://thinking-tester-contact-list.herokuapp.com"; mvn test

  # Bash / Linux
  BASE_URL="https://thinking-tester-contact-list.herokuapp.com" mvn test
  ```

### 3. Run via IDE
- Open the project in **IntelliJ IDEA** / **Eclipse** / **VS Code**.
- Right-click `testng_suite.xml` and select **Run 'testng_suite.xml'**.

---

## 📊 Reports

After test execution, reports and logs are generated at:
- **Surefire Reports**: `target/surefire-reports/index.html`
- **TestNG Output**: `target/surefire-reports/emailable-report.html`

---

## 📚 References & Resources

- [REST Assured Official Documentation & Usage Guide](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [TestNG Documentation](https://testng.org/doc/)
- [Postman API Reference](https://documenter.getpostman.com/view/4012288/TzK2bEa8#intro)
