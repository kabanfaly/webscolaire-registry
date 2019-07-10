FROM adoptopenjdk/openjdk11:jdk-11.0.3_7
LABEL maintainer="nfalykaba@gmail.com"
RUN mkdir -p /opt/app
VOLUME /opt/app
EXPOSE 8181
ARG JAR_FILE=target/webscolaire-registry-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} wsRgistry.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.data.mongodb.uri=mongodb://ws-registry-mongo:27017", "-jar", "/wsRgistry.jar"]

