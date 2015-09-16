Spring MVC Rest
=====

The purpose of this example is to show how to develop a RESTFul API with Spring MVC.

The project uses the data model from Spring Data JPA example and it's deployed on Heroku, check it out! :)

[Spring MVC Rest Example on Heroku](http://goo.gl/CTn8lG)

The example covers how to do unit test the RESTful Controllers using Mockmvc and integration tests using the RestTemplate. The example also shows how to deal with custom exceptions using the @ControllerAdvice and return a different http status codes with a message on it for each different exception.

The API documentation is created by Springfox-Swagger2.

<h2>Technology Stack</h2>
Most relevant technologies:

- Maven 3
- Java 8
- Spring 4
- Spring Boot
- Json
- Springfox-Swagger2
- Heroku
- Postgres

<h2>Running the Tests</h2>
Run the unit tests with the following command:

    mvn clean test

Run the integration tests with the following command:

    mvn clean verify

<h2>How to build</h2>
Please run on the parent project:

    mvn clean package

<h2>Code coverage</h2>
- Controller Layer Unit Testing 100%
- Controller Layer Integration Testing 100%