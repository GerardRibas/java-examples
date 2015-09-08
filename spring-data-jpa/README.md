Spring Data JPA
=====

The purpose of this example is to show how to use Spring Data JPA with a middle complex Datamodel.

<h2>Datamodel</h2>
I've picked a data model from [databaseanswers](http://www.databaseanswers.org/) (thank you guys!) for a product order invoices datamodel.

![](http://www.databaseanswers.org/data_models/customers_and_orders/images/customers_and_orders_model.gif)

The data model contains several relationships in order to show how to code relationships on the model layer using annotations. I didn't focus if that model is correct or not because that's not the purpose of the example.

<h2>Technology Stack</h2>
Most relevant technologies:

- Maven 3
- Java 8
- JPA 2.1
- Spring Boot
- HikariCP
- Spring Data
- Hibernate
- Liquibase
- Junit
- Spring Test Dbunit
- Dbunit

The example uses the JPA Annotations on the entity classes as well as some custom queries in order to show how it works (check the repository package).

<h2>Running the Tests</h2>
Run the unit tests with the following command:

    mvn clean test

Run the integration tests with the following command:

    mvn clean verify

<h2>How to build</h2>
Please run on the parent project:

    mvn clean package

<h2>Code coverage</h2>
- Service Layer Unit Testing 100%
- Service Layer Integration Testing 100%