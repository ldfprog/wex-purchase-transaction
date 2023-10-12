# wex-purchase-transaction
Application for storage and retrieval of transactions with currency conversion.

# Technologies
Language: Java  
Frameworks: Spring Boot, Spring Security and Spring Data JPA  
Additional libraries: Stringdoc and Jackson  
Database: H2  
Build and deploy: Apache Maven  
Container: Docker  

# Build and deploy

A simple **mvn clean install** builds and runs all tests.  
A **jar** package is generated in the folder **/target**.  
To exceute the service run the command **java -jar /target/wex-purchase-transaction-1.0-SNAPSHOT.jar**.  
A Dockerfile is provided for easy image packaging with accompanying basic compose definition.  

# Testing the deployed service

A Swagger-ui page is provided with the service for easy testing.  
Page: {host:port}/wex/swagger-ui/index.html#/  
Api docs JSON: {host:port}/wex/v3/api-docs

# Description and uses for service operations

## POST {host:port}/wex/transaction

Endpoint for submissions of purchase transactions.  
It is implemented as an upsert, inserting new database records with unique description or updating existing record if the same description already exists, making the operation idempotent. It returns the id for the inserted or updated record.

## GET {host:port}/wex/transaction

Endpoint for retrieval of recorded transactions.  
If the transactions is found, the operation integrates with the treasury API to provide the original amount and the converted one based on exchange rate returned by the external API.
