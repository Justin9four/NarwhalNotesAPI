# Dockerfile for Project Fawkes API
# Author: Justin Chandler

FROM openjdk:8
RUN adduser --system --group api
USER api:api
ARG VERSION=1.0.4
ARG JAR_FILE=target/api-$VERSION.jar
COPY ${JAR_FILE} projectFawkesAPI.jar
LABEL version=$VERSION \
      author="Justin Chandler"
ENTRYPOINT ["java", "-jar", "/projectFawkesAPI.jar"]