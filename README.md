# mortgage-application

# Before running the app
Before running the app basic database configuration needs to be set up. The application properties file (application.properties) is located in the resources folder and requires the user to setup the following (existing values are only examples):

```
spring.datasource.url = jdbc:mysql://localhost:3306/mortgage_app?useSSL=false
spring.datasource.username = mortgage-user
spring.datasource.password = mortgage
```

Integration tests are using in memory H2 database for simplicity and require no additional configuration.

# Run the app
To run the application change your current directory to the root directory of the project and run `mvn spring-boot:run`.
