#Webscolaire Registry

## Run test
./mvnw clean test

## Build application
./mvnw clean install

## Run application
###- In development environment
java -jar target/webscolaire-registry-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
 
###- In production environment
java -jar target/webscolaire-registry-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
 
## Docker
