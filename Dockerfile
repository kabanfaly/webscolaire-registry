FROM adoptopenjdk/openjdk11:jdk-11.0.7_10-alpine-slim
LABEL maintainer="nfalykaba@gmail.com"
RUN mkdir -p /opt/app
VOLUME /opt/app
ENV JAVA_OPTS=''
EXPOSE 8181
ARG JAR_FILE=target/webscolaire-registry-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} wsRegistry.jar
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /wsRegistry.jar"]
