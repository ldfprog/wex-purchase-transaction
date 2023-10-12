FROM openjdk:17-jdk-alpine
MAINTAINER lucasdf.org
COPY target/wex-purchase-transaction-1.0-SNAPSHOT.jar wex-purchase-transaction-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/wex-purchase-transaction-1.0-SNAPSHOT.jar"]