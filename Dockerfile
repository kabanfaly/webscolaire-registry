FROM adoptopenjdk/openjdk11:jdk-11.0.3_7
LABEL maintainer="nfalykaba@gmail.com"
RUN mkdir -p /opt/app
VOLUME /opt/app
EXPOSE 8181
ENV JAVA_OPTS=''
ARG JAR_FILE=target/webscolaire-registry-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} wsRgistry.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "$JAVA_OPTS", "-jar", "/wsRgistry.jar"]

