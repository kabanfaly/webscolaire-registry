#Webscolaire Registry

## Run test
`./mvnw clean test`

## Build application
`./mvnw clean install`

## Run application
###- In development environment
`./mvnw -Pdev`
 
###- In production environment
`export MONGO_URI=mongodb://localhost:27017`
`./mvnw -Pprod`
 
## Docker
### To build the image
`./mvnw spring-boot:build-image`

### To start the application
make run

### To stop the application
make stop

### To view the application log
make logs


