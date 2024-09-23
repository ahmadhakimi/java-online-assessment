# java-online-assessment-etiqa
Java Technical Assessment utilizing in Spring Boot Application for managing products and customers.


## Requirement
- Java 21
- MySQL 8.0
- Spring Boot 3.3.4
- Maven 4-0-0
- IDE Intellij Idea
- Docker version 4.*

## Setup Instructions

### Clone the Repository
```bash
  git clone https://github.com/yourusername/bookstore.git
  cd bookstore
```
### Configure Database
  ``` bash
# mysql database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/<databaseName>
spring.datasource.username=username
spring.datasource.password=password
spring.datasource-driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```
### Build Application 
# Maven
``` bash
./mvnw clean install
```


#### 4. **API Endpoints**
```markdown
## API Endpoints

### Customer Endpoints

- **Create Customer**
  - **URL**: `/api/customers`
  - **Method**: `POST`
  - **Request Body**:
    ```json
    {
      "firstName": "John",
      "lastName": "Doe",
      "emailOffice": "john.doe@example.com",
      "emailPersonal": "johndoe@gmail.com",
      "familyMembers": ["Jane Doe", "Joe Doe"],
      "phoneNo":"014456789"
    }
    ```

- **Get All Customers**
  - **URL**: `/api/customer`
  - **Method**: `GET`
  - **Response**: List of customers

- **Get Customer by ID**
  - **URL**: `/api/customer/{id}`
  - **Method**: `GET`
  - **Response**: Customer details

- **Update Customer**
  - **URL**: `/api/customer/{id}`
  - **Method**: `PUT`
  - **Request Body**: Same as Create Customer

- **Delete Customer**
  - **URL**: `/api/customer/{id}`
  - **Method**: `DELETE`

### Product Endpoints

- **Create Product**
  - **URL**: `/api/products`
  - **Method**: `POST`
  - **Request Body**:
    ```json
    {
      "bookTitle": "Effective Java",
      "bookPrice": 29.99,
      "bookQuantity": 100
    }
    ```

- **Get All Products**
  - **URL**: `/api/products`
  - **Method**: `GET`
  - **Response**: List of products

- **Get Product by ID**
  - **URL**: `/api/products/{id}`
  - **Method**: `GET`
  - **Response**: Product details

- **Update Product**
  - **URL**: `/api/products/{id}`
  - **Method**: `PUT`
  - **Request Body**: Same as Create Product

- **Delete Product**
  - **URL**: `/api/products/{id}`
  - **Method**: `DELETE`

```
## Testing

To run the unit tests:
```bash
# For Maven
./mvnw test

# For Gradle
./gradlew test

```

#### 6. **Logging**
```markdown
## Logging

This application uses Logback for logging. Logs will be available in the console and can be configured in the `src/main/resources/logback-spring.xml` file.
```

